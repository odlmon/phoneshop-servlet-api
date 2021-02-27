package com.es.phoneshop.dao.impl;

import com.es.phoneshop.dao.AbstractArrayListDao;
import com.es.phoneshop.dao.ProductDao;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.model.product.Product;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArrayListProductDao extends AbstractArrayListDao<Product> implements ProductDao {
    private static volatile ProductDao instance;

    private ArrayListProductDao() {
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
    public List<Product> findProducts(String query, SortField sortField, SortOrder sortOrder) {
        List<Product> productList = items.stream()
                .filter(product -> product.getPrice() != null)
                .filter(product -> product.getStock() > 0)
                .collect(Collectors.toList());

        return processProductList(productList, query, sortField, sortOrder);
    }

    @Override
    public void delete(@Nullable Long id) throws ItemNotFoundException {
        if (id == null) {
            return;
        }
        Product deletingProduct = items.stream()
                .filter(product -> id.equals(product.getId()))
                .findAny()
                .orElseThrow(() -> new ItemNotFoundException(id));
        items.remove(deletingProduct);
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
}
