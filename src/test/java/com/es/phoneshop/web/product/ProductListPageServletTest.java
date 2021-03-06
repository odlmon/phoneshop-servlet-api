package com.es.phoneshop.web.product;

import com.es.phoneshop.dao.impl.ArrayListProductDao;
import com.es.phoneshop.exception.NullValuePassedException;
import com.es.phoneshop.exception.OutOfStockException;
import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.enums.SortField;
import com.es.phoneshop.model.enums.SortOrder;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.RecentlyViewedProducts;
import com.es.phoneshop.service.impl.HttpSessionCartService;
import com.es.phoneshop.service.impl.HttpSessionRecentlyViewedProductsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ArrayListProductDao productDao;
    @Mock
    private HttpSessionRecentlyViewedProductsService recentlyViewedProductsService;
    @Mock
    private HttpSessionCartService cartService;
    @InjectMocks
    private ProductListPageServlet servlet = new ProductListPageServlet();

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        Currency usd = Currency.getInstance("USD");
        Product product = new Product(1L,"test-product", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg");
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        when(request.getParameter("query")).thenReturn("Samsung");
        when(request.getParameter("sort")).thenReturn("price");
        when(request.getParameter("order")).thenReturn("asc");
        when(productDao.findProducts("Samsung", SortField.price, SortOrder.asc)).thenReturn(productList);

        RecentlyViewedProducts products = new RecentlyViewedProducts();
        products.getItems().offer(product);
        when(recentlyViewedProductsService.getRecentlyViewedProducts(request)).thenReturn(products);

        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("1");
        when(request.getLocale()).thenReturn(Locale.getDefault());
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);

        verify(productDao).findProducts("Samsung", SortField.price, SortOrder.asc);

        List<Product> productList = productDao.findProducts("Samsung", SortField.price, SortOrder.asc);
        verify(request).setAttribute("products", productList);

        RecentlyViewedProducts products = recentlyViewedProductsService.getRecentlyViewedProducts(request);
        verify(request).setAttribute("recentlyViewed", products);
    }

    @Test
    public void testDoPost() throws ServletException, IOException, NullValuePassedException, OutOfStockException {
        servlet.doPost(request, response);

        verify(request).getParameter(eq("productId"));
        verify(request).getParameter(eq("quantity"));

        Cart cart = cartService.getCart(request);
        verify(cartService).add(cart, 1L, 1);

        verify(response).sendRedirect(anyString());
    }
}