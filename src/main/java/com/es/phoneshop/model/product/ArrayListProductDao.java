package com.es.phoneshop.model.product;

import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private static volatile ProductDao instance;

    private long maxId;
    private List<Product> products;

    private ArrayListProductDao() {
        this.products = new ArrayList<>();
    }

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
    public synchronized Product getProduct(@Nullable Long id) throws ProductNotFoundException, NullValuePassedException {
        if (id == null) {
            throw new NullValuePassedException();
        }

        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    public synchronized List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        List<Product> processingProducts = products.stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());

        if (query != null && !query.isEmpty()) {
            processingProducts = getQueriedProducts(processingProducts, query);
        }

        if (sortField != null && sortOrder != null) {
            processingProducts = getSortedProducts(processingProducts, sortField, sortOrder);
        }

        return processingProducts;
    }

    private List<Product> getSortedProducts(List<Product> products, SortField sortField, SortOrder sortOrder) {
        Comparator<Product> fieldComparator = (sortField == SortField.description)
                ? Comparator.comparing(Product::getDescription)
                : Comparator.comparing(Product::getPrice);
        if (sortOrder == SortOrder.desc) {
            fieldComparator = fieldComparator.reversed();
        }

        return products.stream()
                .sorted(fieldComparator)
                .collect(Collectors.toList());
    }

    private List<Product> getQueriedProducts(List<Product> products, String query) {
        String[] queryWords = splitToWords(query);

        return products.stream()
                .filter(product -> isContainingQueryWords(product.getDescription(), queryWords))
                .sorted(Comparator.comparing(product -> getQueryRelevance(((Product) product).getDescription(), queryWords)).reversed())
                .collect(Collectors.toList());
    }

    private String[] splitToWords(String query) {
        return query.split("\\s+");
    }

    private boolean isContainingQueryWords(String productDescription, String[] queryWords) {
        return Arrays.stream(queryWords).anyMatch(productDescription::contains);
    }

    private Double getQueryRelevance(String productDescription, String[] queryWords) {
        return (double) Arrays.stream(queryWords).filter(productDescription::contains).count() /
                splitToWords(productDescription).length;
    }

    @Override
    public synchronized void save(@Nullable Product product) throws NullValuePassedException {
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
        product.setId(maxId++);
        products.add(product);
    }

    @Override
    public synchronized void delete(@Nullable Long id) throws ProductNotFoundException {
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
