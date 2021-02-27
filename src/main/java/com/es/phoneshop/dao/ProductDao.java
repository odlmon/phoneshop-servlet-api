package com.es.phoneshop.dao;

import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.model.product.Product;

import java.util.List;

public interface ProductDao {
    Product getItem(Long id) throws ItemNotFoundException, NullValuePassedException;
    List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder);
    void save(Product product) throws NullValuePassedException;
    void delete(Long id) throws ItemNotFoundException;
}
