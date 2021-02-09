package com.es.phoneshop.model.product;

import java.util.List;

public interface ProductDao {
    Product getProduct(Long id) throws ProductNotFoundException, NullValuePassedException;
    List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);
    void save(Product product) throws NullValuePassedException;
    void delete(Long id) throws ProductNotFoundException;
}
