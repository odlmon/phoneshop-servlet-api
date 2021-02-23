<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<jsp:useBean id="recentlyViewed" type="com.es.phoneshop.model.product.RecentlyViewedProducts" scope="request"/>
<tags:master pageTitle="Product List">
  <p>
    Welcome to Expert-Soft training!
  </p>
  <form>
    <input name="query" value="${param.query}">
    <button>Search</button>
  </form>
  <tags:status message="${param.message}" hasError="${not empty error}"/>
  <table>
    <thead>
      <tr>
        <td>Image</td>
        <td>
          Description
          <tags:sortLink sort="description" order="asc"/>
          <tags:sortLink sort="description" order="desc"/>
        </td>
        <td class="quantity">Quantity</td>
        <td class="price">
          Price
          <tags:sortLink sort="price" order="asc"/>
          <tags:sortLink sort="price" order="desc"/>
        </td>
        <td></td>
      </tr>
    </thead>
    <c:forEach var="product" items="${products}">
      <tr>
        <td>
          <img class="product-tile" src="${product.imageUrl}">
        </td>
        <td>
          <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
            ${product.description}
          </a>
        </td>
        <td class="quantity">
          <c:set var="hasError" value="${not empty error and (param.productId eq product.id)}"/>
          <input form="addToCart${product.id}" class="quantity" name="quantity" value="${hasError ? param.quantity : 1}">
          <c:if test="${hasError}">
            <div class="error">
                ${error}
            </div>
          </c:if>
        </td>
        <td class="price">
          <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}">
            <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
          </a>
        </td>
        <td>
          <form id="addToCart${product.id}" method="post" action="${pageContext.servletContext.contextPath}/products">
            <input name="productId" type="hidden" value="${product.id}"/>
            <button>
              Add to cart
            </button>
          </form>
        </td>
      </tr>
    </c:forEach>
  </table>
  <tags:recentlyViewed recentlyViewed="${recentlyViewed}"/>
</tags:master>