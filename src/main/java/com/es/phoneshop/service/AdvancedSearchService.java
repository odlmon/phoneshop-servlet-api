package com.es.phoneshop.service;

import com.es.phoneshop.model.enums.SearchType;
import com.es.phoneshop.model.product.Product;

import java.math.BigDecimal;
import java.util.List;

public interface AdvancedSearchService {
    List<SearchType> getSearchTypes();
    List<Product> searchProducts(String description, SearchType searchType, BigDecimal minPrice, BigDecimal maxPrice);
    List<Product> getAllProducts();
}
