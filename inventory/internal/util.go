package internal

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"math/rand"
	"net/http"
	"time"

	opentracing "github.com/opentracing/opentracing-go"
)

var RAND = rand.New(rand.NewSource(time.Now().UnixNano()))

func NewOrderNum() string {
	b := [16]byte{}
	RAND.Read(b[:])
	return fmt.Sprintf("%x-%x-%x-%x-%x", b[:4], b[4:6], b[6:8], b[8:10], b[10:])
}

func RandSimDelay() {
	if RAND.Float32() < GlobalConfig.SimDelayChance {
		time.Sleep(time.Duration(RAND.Intn(GlobalConfig.SimDelayMS)) * time.Millisecond)
	}
}

func WriteError(w http.ResponseWriter, err string, statusCode int) []byte {
	log.Println(err)
	bytes, _ := json.Marshal(ErrorStatus{Error: err})
	w.WriteHeader(statusCode)
	w.Write(bytes)
	return bytes
}

func GETCall(url string, body io.Reader, spanCtx opentracing.SpanContext) (*http.Response, error) {
	req, err := http.NewRequest("GET", url, body)
	if err != nil {
		return nil, err
	}

	opentracing.GlobalTracer().Inject(spanCtx, opentracing.HTTPHeaders, opentracing.HTTPHeadersCarrier(req.Header))

	return http.DefaultClient.Do(req)
}

func POSTCall(url string, contentType string, body io.Reader, spanCtx opentracing.SpanContext) (*http.Response, error) {
	req, err := http.NewRequest("POST", url, body)
	if err != nil {
		return nil, err
	}
	req.Header.Set("Content-Type", contentType)

	opentracing.GlobalTracer().Inject(spanCtx, opentracing.HTTPHeaders, opentracing.HTTPHeadersCarrier(req.Header))

	return http.DefaultClient.Do(req)
}
