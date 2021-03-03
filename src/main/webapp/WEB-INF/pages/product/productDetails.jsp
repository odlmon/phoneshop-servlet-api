<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<jsp:useBean id="error" class="java.lang.String" scope="request"/>
<jsp:useBean id="recentlyViewed" type="com.es.phoneshop.model.product.RecentlyViewedProducts" scope="request"/>
<tags:master pageTitle="Product Details">
  <p>Cart: ${cart}</p>
  <p>${product.description}</p>
  <tags:status message="${param.message}" hasError="${not empty error}"/>
  <form method="post" action="${pageContext.servletContext.contextPath}/products/${product.id}">
    <table>
      <tr>
        <td>Image</td>
        <td>
          <img src="${product.imageUrl}">
        </td>
      </tr>
      <tr>
        <td>Code</td>
        <td>${product.code}</td>
      </tr>
      <tr>
        <td>Stock</td>
        <td>${product.stock}</td>
      </tr>
      <tr>
        <td>Price</td>
        <td class="price">
          <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
        </td>
      </tr>
      <tr>
        <td>Quantity</td>
        <td>
          <input class="quantity" name="quantity" value="${not empty error ? param.quantity : 1}">
          <tags:optionalErrorMessage error="${error}"/>
        </td>
      </tr>
    </table>
    <p>
      <button>Add to cart</button>
    </p>
    <tags:recentlyViewed recentlyViewed="${recentlyViewed}"/>
  </form>
</tags:master>