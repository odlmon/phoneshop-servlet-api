package com.es.phoneshop.dao;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.ItemNotFoundException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.*;

public class ArrayListProductDaoTest {
    private ArrayListProductDao productDao;
    private Currency usd;
    private Product product;

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = ArrayListProductDao.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        usd = Currency.getInstance("USD");
        product = new Product("test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        resetSingleton();
        productDao = (ArrayListProductDao) ArrayListProductDao.getInstance();
        try {
            productDao.save(new Product("sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg"));
            productDao.save(new Product("sgs2", "Samsung Galaxy S II", new BigDecimal(200), usd, 0, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20II.jpg"));
            productDao.save(new Product("sgs3", "Samsung Galaxy S III", new BigDecimal(300), usd, 5, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S%20III.jpg"));
        } catch (NullValuePassedException e) {
            System.out.println("Null value passed");
        }
    }

    @Test
    public void testGetInstance() {
        ProductDao productDao1 = ArrayListProductDao.getInstance();
        ProductDao productDao2 = ArrayListProductDao.getInstance();
        assertSame(productDao1, productDao2);
    }

    @Test
    public void testGetProducts() {
        try {
            productDao.save(product);
            Product savedProduct = productDao.getItem(product.getId());
            assertEquals(product.getCode(), savedProduct.getCode());
        } catch (NullValuePassedException e) {
            fail("Null value passed");
        } catch (ItemNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testGetProductWithNullValuePassed() {
        Product product = null;
        try {
            product = productDao.getItem(null);
            fail("Unexpected behaviour");
        } catch (NullValuePassedException e) {
            assertNull(product);
        } catch (ItemNotFoundException e) {
            fail("Unexpected behaviour");
        }
    }

    @Test
    public void testGetProductWhichIsMissing() {
        Product product = null;
        try {
            product = productDao.getItem(1000L);
            fail("Unexpected behaviour");
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ItemNotFoundException e) {
            assertNull(product);
        }
    }

    @Test
    public void testFindProductsWithNullParams() {
        assertFalse(productDao.findProducts(null, null, null).isEmpty());
    }

    @Test
    public void testFindProductsWithQuery() {
        List<Product> products = productDao.findProducts("S III", null, null);
        assertEquals(products.get(0).getCode(), "sgs3");
        assertEquals(products.size(), 2);
    }

    @Test
    public void testFindProductsWithSort() {
        List<Product> products = productDao.findProducts(null, SortField.price, SortOrder.asc);
        assertTrue(products.get(0).getPrice().compareTo(products.get(1).getPrice()) <= 0);
    }

    @Test
    public void testFindProductsWithQueryAndSort() {
        List<Product> products = productDao.findProducts("S III", SortField.description, SortOrder.desc);
        assertEquals(products.get(0).getCode(), "sgs3");
    }

    @Test
    public void testSaveNewProduct() {
        try {
            productDao.save(product);
            Product savedProduct = productDao.getItem(product.getId());
            assertEquals(product.getCode(), savedProduct.getCode());
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ItemNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testSaveModifiedProduct() {
        try {
            productDao.save(product);
            Product modifiedProduct = productDao.getItem(product.getId());
            modifiedProduct.setCode("test-product1");
            Long oldId = product.getId();
            productDao.save(modifiedProduct);
            modifiedProduct = productDao.getItem(oldId);
            assertEquals(oldId, modifiedProduct.getId());
            assertNotEquals(null, "test-product", modifiedProduct.getCode());
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ItemNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testSaveProductWithPredefinedId() {
        Long predefinedId = 228L;
        Product product = new Product(predefinedId,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        try {
            productDao.save(product);
            productDao.getItem(predefinedId);
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ItemNotFoundException e) {
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
            int beforeSize = productDao.findProducts(null, null, null).size();
            productDao.delete(product.getId());
            int afterSize = productDao.findProducts(null, null, null).size();
            assertNotEquals(afterSize, beforeSize);
        } catch (NullValuePassedException e) {
            fail("Unexpected behaviour");
        } catch (ItemNotFoundException e) {
            fail("Product not found");
        }
    }

    @Test
    public void testDeleteProductWhichIsMissing() {
        try {
            productDao.delete(1000L);
            fail("Unexpected behaviour");
        } catch (ItemNotFoundException ignored) {

        }
    }

    @Test
    public void testDeleteProductWithNullValuePassed() {
        try {
            productDao.delete(null);
        } catch (ItemNotFoundException e) {
            fail("Unexpected behaviour");
        }
    }
}
