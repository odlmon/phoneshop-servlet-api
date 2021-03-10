package com.es.phoneshop.service.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.model.enums.SearchType;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.AdvancedSearchService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultAdvancedSearchService implements AdvancedSearchService {
    private ProductDao productDao;

    private static volatile AdvancedSearchService instance;

    private DefaultAdvancedSearchService() {
        productDao = ArrayListProductDao.getInstance();
    }

    public static AdvancedSearchService getInstance() {
        if (instance == null) {
            synchronized (DefaultAdvancedSearchService.class) {
                if (instance == null) {
                    instance = new DefaultAdvancedSearchService();
                }
            }
        }
        return instance;
    }

    @Override
    public List<SearchType> getSearchTypes() {
        return Arrays.asList(SearchType.values());
    }

    @Override
    public List<Product> searchProducts(String description, SearchType searchType, BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> productList = this.getAllProducts();

        if (description != null && !description.equals("")) {
            String[] descriptionWords = splitToWords(description);
            if (searchType == SearchType.ALL_WORDS) {
                productList = findByAllWords(productList, descriptionWords);
            } else {
                productList = findByAnyWord(productList, descriptionWords);
            }
        }

        if (minPrice != null) {
            productList = findByMinPrice(productList, minPrice);
        }

        if (maxPrice != null) {
            productList = findByMaxPrice(productList, maxPrice);
        }

        return productList;
    }

    @Override
    public List<Product> getAllProducts() {
        return productDao.findProducts("", null, null);
    }

    private List<Product> findByMinPrice(List<Product> productList, BigDecimal minPrice) {
        return productList.stream()
                .filter(p -> p.getPrice().compareTo(minPrice) >= 0)
                .collect(Collectors.toList());
    }

    private List<Product> findByMaxPrice(List<Product> productList, BigDecimal maxPrice) {
        return productList.stream()
                .filter(p -> p.getPrice().compareTo(maxPrice) <= 0)
                .collect(Collectors.toList());
    }

    private String[] splitToWords(String query) {
        return query.split("\\s+");
    }

    private boolean containsAllWords(String description, String[] descriptionWords) {
        String[] filtered = Arrays.stream(descriptionWords)
                .filter(description::contains)
                .toArray(String[]::new);
        return filtered.length == descriptionWords.length;
    }

    private boolean containsAnyWord(String description, String[] descriptionWords) {
        String[] filtered = Arrays.stream(descriptionWords)
                .filter(description::contains)
                .toArray(String[]::new);
        return filtered.length > 0;
    }

    private List<Product> findByAllWords(List<Product> productList, String[] descriptionWords) {
        return productList.stream()
                .filter(p -> containsAllWords(p.getDescription(), descriptionWords))
                .collect(Collectors.toList());
    }

    private List<Product> findByAnyWord(List<Product> productList, String[] descriptionWords) {
        return productList.stream()
                .filter(p -> containsAnyWord(p.getDescription(), descriptionWords))
                .collect(Collectors.toList());
    }
}
