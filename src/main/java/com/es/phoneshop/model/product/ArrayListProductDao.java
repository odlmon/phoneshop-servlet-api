package com.es.phoneshop.model.product;

import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private long maxId;
    private List<Product> products;

    public ArrayListProductDao() {
        this.products = new ArrayList<>();
        saveSampleProducts();
    }

    @Override
    public synchronized Product getProduct(@Nullable Long id) throws ProductNotFoundException, NullValuePassedException {
        if (id == null) {
            throw new NullValuePassedException();
        }

        return products.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(ProductNotFoundException::new);
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
            add(product);
        } else {
            update(product);
        }
    }

    private void add(Product product) {
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

    private void update(Product product) {
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
                .orElseThrow(ProductNotFoundException::new);
        products.remove(deletingProduct);
    }

    private void saveSampleProducts() {
        Currency usd = Currency.getInstance("USD");
        try {
            save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
            save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
            save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
            save(new Product("iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg"));
            save(new Product("iphone6", "Apple iPhone 6", new BigDecimal(1000), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone%206.jpg"));
            save(new Product("htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg"));
            save(new Product("sec901", "Sony Ericsson C901", new BigDecimal(420), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Ericsson%20C901.jpg"));
            save(new Product("xperiaxz", "Sony Xperia XZ", new BigDecimal(120), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Sony/Sony%20Xperia%20XZ.jpg"));
            save(new Product("nokia3310", "Nokia 3310", new BigDecimal(70), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Nokia/Nokia%203310.jpg"));
            save(new Product("palmp", "Palm Pixi", new BigDecimal(170), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Palm/Palm%20Pixi.jpg"));
            save(new Product("simc56", "Siemens C56", new BigDecimal(70), usd, 20, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C56.jpg"));
            save(new Product("simc61", "Siemens C61", new BigDecimal(80), usd, 30, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20C61.jpg"));
            save(new Product("simsxg75", "Siemens SXG75", new BigDecimal(150), usd, 40, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Siemens/Siemens%20SXG75.jpg"));
        } catch (NullValuePassedException e) {
            System.out.println("Null value passed to method");
        }
    }
}
