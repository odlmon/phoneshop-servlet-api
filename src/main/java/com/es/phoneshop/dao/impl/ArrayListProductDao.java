package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.ProductNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static volatile ProductDao instance;

    private AtomicLong maxId = new AtomicLong(0);
    private List<Product> products = new CopyOnWriteArrayList<>();

    public static ProductDao getInstance() {
        if (instance == null) {
            synchronized (ArrayListProductDao.class) {
                if (instance == null) {
                    instance = new ArrayListProductDao();
                }
            }
        }
        return instance;
    }

    @Override
    public Product getProduct(@Nullable Long id) throws ProductNotFoundException, NullValuePassedException {
        if (id == null) {
            throw new NullValuePassedException();
        }

        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        List<Product> productList = products.stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());

        return processProductList(productList, query, sortField, sortOrder);
    }

    private List<Product> processProductList(List<Product> productList, String query, SortField sortField,
                                             SortOrder sortOrder) {
        boolean isQueried = query != null && !query.isEmpty();
        boolean isSorting = sortField != null && sortOrder != null;
        Map<Product, Double> productRelevance = null;

        if (isQueried) {
            productRelevance = getProductRelevance(productList, query);
            productList = getQueriedProducts(productList, productRelevance);
        }

        if (isSorting) {
            productList = sortByFieldAndOrder(productList, sortField, sortOrder);
        } else if (isQueried && productRelevance != null) {
            productList = sortByProductRelevance(productList, productRelevance);
        }

        return productList;
    }

    private Map<Product, Double> getProductRelevance(List<Product> productStream, String query) {
        String[] queryWords = splitToWords(query);

        return productStream.stream().collect(Collectors.toMap(
                product -> product,
                product -> getQueryRelevance(product.getDescription(), queryWords)
        ));
    }

    private List<Product> sortByFieldAndOrder(List<Product> productList, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> fieldComparator = (sortField == SortField.description)
                ? Comparator.comparing(Product::getDescription)
                : Comparator.comparing(Product::getPrice);
        if (sortOrder == SortOrder.desc) {
            fieldComparator = fieldComparator.reversed();
        }

        return productList.stream()
                .sorted(fieldComparator)
                .collect(Collectors.toList());
    }

    private List<Product> sortByProductRelevance(List<Product> productList, Map<Product, Double> productRelevance) {
        return productList.stream()
                .sorted(Comparator.comparing(productRelevance::get).reversed())
                .collect(Collectors.toList());
    }

    private List<Product> getQueriedProducts(List<Product> productList, Map<Product, Double> productRelevance) {
        return productList.stream()
                .filter(product -> productRelevance.get(product) > 0)
                .collect(Collectors.toList());
    }

    private String[] splitToWords(String query) {
        return query.split("\\s+");
    }

    private Double getQueryRelevance(String productDescription, String[] queryWords) {
        return (double) Arrays.stream(queryWords).filter(productDescription::contains).count() /
                splitToWords(productDescription).length;
    }

    @Override
    public void save(@Nullable Product product) throws NullValuePassedException {
        if (product == null) {
            throw new NullValuePassedException();
        }
        if (product.getId() != null) {
            update(product);
        } else {
            add(product);
        }
    }

    private void update(Product product) {
        Long id = product.getId();
        Optional<Product> optionalProduct = products.stream()
                .filter(p -> id.equals(p.getId()))
                .findAny();
        if (!optionalProduct.isPresent()) {
            products.add(product);
        } else {
            Collections.replaceAll(products, optionalProduct.get(), product);
        }
    }

    private void add(Product product) {
        product.setId(maxId.incrementAndGet());
        products.add(product);
    }

    @Override
    public void delete(@Nullable Long id) throws ProductNotFoundException {
        if (id == null) {
            return;
        }
        Product deletingProduct = products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(() -> new ProductNotFoundException(id));
        products.remove(deletingProduct);
    }
}
