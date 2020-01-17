package internal

import (
	"encoding/json"
	"fmt"
	"io"
	"log"
	"math"
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

		millis := RAND.Intn(GlobalConfig.SimDelayMS)
		chunks := 10
		if chunks*3 < millis {
			chunks = 1
		}

		chunkMillis := millis / chunks
		loadedMillis := float64(chunkMillis) * (RAND.Float64() * GlobalConfig.SimDelayCpuPct)

		for i := 0; i < chunks; i++ {
			endTime := time.Now().Add(time.Duration(loadedMillis) * time.Millisecond)

			// load cpu
			for time.Now().Before(endTime) {
			}

			// sleep for remainder of chunk
			time.Sleep(time.Duration(math.Max(float64(chunkMillis)-loadedMillis, 0)) * time.Millisecond)
		}
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
