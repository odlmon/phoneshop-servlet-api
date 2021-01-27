package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
    }

    @Test
    public void testFindProductsNoResults() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testSaveNewProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        assertTrue(product.getId() > 0);
        try {
            Product result = productDao.getProduct(product.getId());
            assertNotNull(result);
            assertEquals("test-product", result.getCode());
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveModifiedProduct() {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        try {
            Product modifiedProduct = productDao.getProduct(product.getId());
            modifiedProduct.setCode("test-product1");
            Long oldId = product.getId();
            productDao.save(modifiedProduct);
            modifiedProduct = productDao.getProduct(product.getId());
            assertEquals(oldId, modifiedProduct.getId());
            assertNotEquals(null, "test-product", modifiedProduct.getCode());
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSaveProductWithoutGeneratingId() {
        Long predefinedId = 228L;
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(predefinedId,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);

        try {
            productDao.getProduct(predefinedId);
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDeleteProduct() throws ProductNotFoundException {
        Currency usd = Currency.getInstance("USD");
        Product product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        productDao.save(product);
        try {
            Product result = productDao.getProduct(product.getId());
            assertNotNull(result);
            productDao.delete(product.getId());
        } catch (ProductNotFoundException e) {
            e.printStackTrace();
        }
        Product result = productDao.getProduct(product.getId());
        assertNull(result);
    }
}
