package com.es.phoneshop.service;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedProductsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HttpSessionRecentlyViewedProductsServiceTest {
    private HttpSessionRecentlyViewedProductsService recentlyViewedProductsService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private ArrayListProductDao productDao;

    private void resetSingleton() throws NoSuchFieldException, IllegalAccessException {
        Field instance = HttpSessionRecentlyViewedProductsService.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);
    }

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException, NullValuePassedException {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new RecentlyViewedProducts());
        resetSingleton();
        recentlyViewedProductsService = (HttpSessionRecentlyViewedProductsService)
                HttpSessionRecentlyViewedProductsService.getInstance();
        recentlyViewedProductsService.setProductDao(productDao);

        Long id = 1L;
        Currency usd = Currency.getInstance("USD");
        Product product1 = new Product(id, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        Product product2 = new Product(2L, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        Product product3 = new Product(3L, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        Product product4 = new Product(4L, "test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        List<Product> productList = new ArrayList<>();
        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        when(productDao.getItem(id)).thenReturn(product1);
        when(productDao.getItem(2L)).thenReturn(product2);
        when(productDao.getItem(3L)).thenReturn(product3);
        when(productDao.getItem(4L)).thenReturn(product4);
        when(productDao.getItem(null)).thenThrow(NullValuePassedException.class);
    }

    @Test
    public void testGetInstance() {
        RecentlyViewedProductsService recentlyViewedProductsService1 =
                HttpSessionRecentlyViewedProductsService.getInstance();
        RecentlyViewedProductsService recentlyViewedProductsService2 =
                HttpSessionRecentlyViewedProductsService.getInstance();
        assertSame(recentlyViewedProductsService1, recentlyViewedProductsService2);
    }

    @Test
    public void testGetRecentlyViewedProducts() {
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        assertEquals(0, products.getItems().size());
    }

    @Test
    public void testAdd() throws NullValuePassedException {
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        assertEquals(0, products.getItems().size());
        recentlyViewedProductsService.add(products, 1L);
        assertEquals(1, products.getItems().size());
        recentlyViewedProductsService.add(products, 1L);
        assertEquals(1, products.getItems().size());
    }

    @Test(expected = NullValuePassedException.class)
    public void testAddWithNullValueIdPassed() throws NullValuePassedException {
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        recentlyViewedProductsService.add(products, null);
    }

    @Test
    public void testAddWithProductsCountOverflow() throws NullValuePassedException {
        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        recentlyViewedProductsService.add(products, 1L);
        recentlyViewedProductsService.add(products, 2L);
        recentlyViewedProductsService.add(products, 3L);
        assertEquals(3, products.getItems().size());
        recentlyViewedProductsService.add(products, 4L);
        assertEquals(3, products.getItems().size());
    }
}
