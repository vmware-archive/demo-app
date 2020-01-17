package internal

type Server interface {
	Start() error
}

type ErrorStatus struct {
	Error string `json:"error"`
}

// Shopping
type Order struct {
	StyleName string
	Quantity  int
}

type OrderStatus struct {
	OrderId string
	Status  string
}

// Styling
type PackedShirts struct {
	Shirts []Shirt
}

type Shirt struct {
	Style ShirtStyle
}

type ShirtStyle struct {
	Name     string
	ImageUrl string
}

// Delivery
type DeliveryStatus struct {
	OrderNum    string
	TrackingNum string
	Status      string
}
