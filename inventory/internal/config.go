package internal

type InventoryConfig struct {
	Application   string
	Service       string
	InventoryHost string
	WarehouseHost string

	Server string
	Token  string

	ProxyHost              string
	ProxyPort              int
	ProxyDistributionsPort int
	ProxyTracingPort       int

	Cluster string
	Shard   string

	Source string

	// simulation
	SimFailCheckout  float32
	SimFailAvailable float32
	SimDelayChance   float32
	SimDelayMS       int // milliseconds
}

var GlobalConfig InventoryConfig

func InitGlobalConfig() {
	GlobalConfig = InventoryConfig{
		Application:   "",
		Service:       "",
		InventoryHost: "localhost:60001",
		WarehouseHost: "localhost:50060",

		Server: "",
		Token:  "",

		ProxyHost:              "",
		ProxyPort:              2878,
		ProxyDistributionsPort: 40000,
		ProxyTracingPort:       30000,

		Cluster: "kerrupt",
		Shard:   "primary",

		Source: "",

		SimFailCheckout:  0.02,
		SimFailAvailable: 0.03,
		SimDelayChance:   0.3333,
		SimDelayMS:       1000,
	}
}
