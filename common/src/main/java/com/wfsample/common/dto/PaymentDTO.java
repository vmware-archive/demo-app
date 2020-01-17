package com.wfsample.common.dto;

/**
 * DTO for making payments for an order.
 *
 * @author Srujan Narkedamalli (snarkedamall@wavefront.com).
 */
public class PaymentDTO {
    String name;
    String creditCardNum;

    public PaymentDTO() {}

    public PaymentDTO(String name, String creditCardNum) {
        this.name = name;
        this.creditCardNum = creditCardNum;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreditCardNum() {
        return creditCardNum;
    }

    public void setCreditCardNum(String creditCardNum) {
        this.creditCardNum = creditCardNum;
    }
}
