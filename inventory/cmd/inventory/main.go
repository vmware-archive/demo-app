package main

import (
	"encoding/json"
	"log"
	"os"
	"wavefront.com/polyglot/inventory/services/inventory"

	. "wavefront.com/polyglot/inventory/internal"
)

func main() {
	if len(os.Args) < 2 {
		log.Fatal("usage: inventory <config_file>")
	}

	InitGlobalConfig()

	file, err := os.Open(os.Args[1])
	if err != nil {
		log.Fatalf("error reading config: %q", err)
	}
	if derr := json.NewDecoder(file).Decode(&GlobalConfig); derr != nil {
		log.Fatalf("error decoding config: %q", derr)
	}

	closer := NewGlobalTracer(GlobalConfig.Service)
	defer closer.Close()

	server := inventory.NewServer()
	if serr := server.Start(); serr != nil {
		log.Fatalf("error starting inventory service: %q", serr)
	}
}
