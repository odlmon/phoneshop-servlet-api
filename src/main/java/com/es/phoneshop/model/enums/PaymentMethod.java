package com.es.phoneshop.model.enums;

public enum PaymentMethod {
    CACHE("Cache"), CREDIT_CARD("Credit card");

    private final String label;

    PaymentMethod(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
