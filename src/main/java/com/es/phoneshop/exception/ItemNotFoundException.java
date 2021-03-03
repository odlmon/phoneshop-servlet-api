package com.es.phoneshop.exception;

public class ItemNotFoundException extends RuntimeException {
    private Long id;

    public ItemNotFoundException() {
    }
    public ItemNotFoundException(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
