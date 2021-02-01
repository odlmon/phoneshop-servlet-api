package com.es.phoneshop.model.product;

import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest
{
    private ArrayListProductDao productDao;
    private Currency usd;
    private Product product;

    @Before
    public void setup() {
        productDao = new ArrayListProductDao();
        usd = Currency.getInstance("USD");
        product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
    }

    @Test
    public void testFindProductsNoResults() {
        assertFalse(productDao.findProducts().isEmpty());
    }

    @Test
    public void testGetProducts() {
        try {
            productDao.save(product);
            Product savedProduct = productDao.getProduct(product.getId());
            assertEquals(product.getCode(), savedProduct.getCode());
        } catch (NullValuePassedException e) {
            fail("Null value passed");
        } catch (ProductNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testGetProductWithNullValuePassed() {
        Product product = null;
        try {
            product = productDao.getProduct(null);
            fail("Unexpected behaviour");
        } catch (NullValuePassedException e) {
            assertNull(product);
        } catch (ProductNotFoundException e) {
            fail("Unexpected behaviour");
        }
    }

    @Test
    public void testGetProductWhichIsMissing() {
        Product product = null;
        try {
            product = productDao.getProduct(1000L);
            fail("Unexpected behaviour");
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ProductNotFoundException e) {
            assertNull(product);
        }
    }

    @Test
    public void testSaveNewProduct() {
        try {
            productDao.save(product);
            Product savedProduct = productDao.getProduct(product.getId());
            assertEquals(product.getCode(), savedProduct.getCode());
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ProductNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testSaveModifiedProduct() {
        try {
            productDao.save(product);
            Product modifiedProduct = productDao.getProduct(product.getId());
            modifiedProduct.setCode("test-product1");
            Long oldId = product.getId();
            productDao.save(modifiedProduct);
            modifiedProduct = productDao.getProduct(oldId);
            assertEquals(oldId, modifiedProduct.getId());
            assertNotEquals(null, "test-product", modifiedProduct.getCode());
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ProductNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testSaveProductWithPredefinedId() {
        Long predefinedId = 228L;
        Product product = new Product(predefinedId,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        try {
            productDao.save(product);
            productDao.getProduct(predefinedId);
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ProductNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testSaveProductWithNullValuePassed() {
        try {
            productDao.save(null);
            fail("Unexpected behaviour");
        } catch (NullValuePassedException ignored) {

        }
    }

    @Test
    public void testDeleteProduct() {
        try {
            productDao.save(product);
            int beforeSize = productDao.findProducts().size();
            productDao.delete(product.getId());
            int afterSize = productDao.findProducts().size();
            assertNotEquals(afterSize, beforeSize);
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ProductNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testDeleteProductWhichIsMissing() {
        try {
            productDao.delete(1000L);
            fail("Unexpected behaviour");
        } catch (ProductNotFoundException ignored) {

        }
    }

    @Test
    public void testDeleteProductWithNullValuePassed() {
        try {
            productDao.delete(null);
        } catch (ProductNotFoundException e) {
            fail("Unexpected behaviour");
        }
    }
}
