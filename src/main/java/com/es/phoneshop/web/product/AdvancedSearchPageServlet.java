package com.es.phoneshop.web.product;

import com.es.phoneshop.model.enums.SearchType;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.service.AdvancedSearchService;
import com.es.phoneshop.service.impl.DefaultAdvancedSearchService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class AdvancedSearchPageServlet extends HttpServlet {
    private AdvancedSearchService advancedSearchService;

    @Override
    public void init() throws ServletException {
        advancedSearchService = DefaultAdvancedSearchService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("searchTypes", advancedSearchService.getSearchTypes());
        request.getRequestDispatcher("/WEB-INF/pages/product/advancedSearch.jsp").forward(request, response);
    }

    private BigDecimal getPrice(HttpServletRequest request, String priceString, String errorName) {
        try {
            if (!priceString.isEmpty()) {
                return new BigDecimal(priceString);
            }
        } catch (NumberFormatException e) {
            request.setAttribute(errorName, "Not a number");
        }
        return null;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String description = request.getParameter("description");
        String searchTypeString = request.getParameter("searchType");
        String minPriceString = request.getParameter("min-price");
        String maxPriceString = request.getParameter("max-price");
        if (description.isEmpty() && minPriceString.isEmpty() && maxPriceString.isEmpty()) {
            List<Product> productList = advancedSearchService.getAllProducts();
            request.setAttribute("products", productList);
        } else {
            SearchType searchType = SearchType.valueOf(searchTypeString);
            BigDecimal minPrice = getPrice(request, minPriceString, "minPriceError");
            BigDecimal maxPrice = getPrice(request, maxPriceString, "maxPriceError");

            boolean wasMinError = minPrice == null && !minPriceString.isEmpty();
            boolean wasMaxError = maxPrice == null && !maxPriceString.isEmpty();
            if (!wasMinError && !wasMaxError) {
                List<Product> productList = advancedSearchService.searchProducts(description, searchType, minPrice, maxPrice);
                request.setAttribute("products", productList);
            }
        }
        doGet(request, response);
    }
}
