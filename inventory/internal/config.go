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
	SimDelayMS       int     // milliseconds
	SimDelayCpuPct   float64 // cpu pct
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

		Cluster: "us-west",
		Shard:   "primary",

		Source: "",

		SimFailCheckout:  0.04,
		SimFailAvailable: 0.06,
		SimDelayChance:   0.3333,
		SimDelayMS:       1000,
		SimDelayCpuPct:   0.4,
	}
}
