package com.es.phoneshop.model;

public interface Identifiable<T extends Number> {
    T getId();
    void setId(T id);
}
