package internal

import (
	"io"
	"io/ioutil"
	"log"
	"net/http"

	"github.com/wavefronthq/wavefront-sdk-go/application"
	"github.com/wavefronthq/wavefront-sdk-go/senders"

	"github.com/wavefronthq/wavefront-opentracing-sdk-go/reporter"
	wfTracer "github.com/wavefronthq/wavefront-opentracing-sdk-go/tracer"

	"github.com/opentracing/opentracing-go"
	otrext "github.com/opentracing/opentracing-go/ext"
)

func NewGlobalTracer(serviceName string) io.Closer {

	var sender senders.Sender
	var err error

	if GlobalConfig.Server != "" && GlobalConfig.Token != "" {
		config := &senders.DirectConfiguration{
			Server: GlobalConfig.Server,
			Token:  GlobalConfig.Token,
		}
		sender, err = senders.NewDirectSender(config)
		if err != nil {
			log.Fatalf("error creating wavefront sender: %q", err)
		}
	} else if GlobalConfig.ProxyHost != "" {
		config := &senders.ProxyConfiguration{
			Host:             GlobalConfig.ProxyHost,
			MetricsPort:      GlobalConfig.ProxyPort,
			DistributionPort: GlobalConfig.ProxyDistributionsPort,
			TracingPort:      GlobalConfig.ProxyTracingPort,
		}
		sender, err = senders.NewProxySender(config)
		if err != nil {
			log.Fatalf("error creating wavefront sender: %q", err)
		}
	} else {
		log.Fatalf("Not enough configuration parameter has been specified for sender.")
	}

	appName := GlobalConfig.Application
	if appName == "" {
		appName = "beachshirts"
	}
	appTags := application.New(appName, serviceName)
	appTags.Cluster = GlobalConfig.Cluster
	appTags.Shard = GlobalConfig.Shard

	var spanReporter reporter.WavefrontSpanReporter
	if GlobalConfig.Source != "" {
		spanReporter = reporter.New(sender, appTags, reporter.Source(GlobalConfig.Source))
	} else {
		spanReporter = reporter.New(sender, appTags)
	}

	consoleReporter := reporter.NewConsoleSpanReporter(serviceName)
	compositeReporter := reporter.NewCompositeSpanReporter(spanReporter, consoleReporter)
	wavefrontTracer := wfTracer.New(compositeReporter)
	opentracing.SetGlobalTracer(wavefrontTracer)
	return ioutil.NopCloser(nil)
}

func NewServerSpan(req *http.Request, spanName string) opentracing.Span {
	tracer := opentracing.GlobalTracer()
	parentCtx, err := tracer.Extract(opentracing.HTTPHeaders, opentracing.HTTPHeadersCarrier(req.Header))
	var span opentracing.Span
	if err == nil { // has parent context
		span = tracer.StartSpan(spanName, opentracing.ChildOf(parentCtx))
	} else if err == opentracing.ErrSpanContextNotFound { // no parent
		span = tracer.StartSpan(spanName)
	} else {
		log.Printf("Error in extracting tracer context: %s", err.Error())
	}
	otrext.SpanKindRPCServer.Set(span)
	return span
}
