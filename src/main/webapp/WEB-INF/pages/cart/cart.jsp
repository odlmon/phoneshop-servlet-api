<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<jsp:useBean id="errors" class="java.util.HashMap" scope="request"/>
<tags:master pageTitle="Cart">
  <p>Cart: ${cart}</p>
  <tags:status message="${param.message}" hasError="${not empty errors}"/>
  <form method="post" action="${pageContext.servletContext.contextPath}/cart">
    <table>
      <thead>
      <tr>
        <td>Image</td>
        <td>Description</td>
        <td class="quantity">Quantity</td>
        <td class="price">Price</td>
        <td></td>
      </tr>
      </thead>
      <c:forEach var="item" items="${cart.items}" varStatus="status">
        <tr>
          <td>
            <img class="product-tile" src="${item.product.imageUrl}">
          </td>
          <td>
            <a href="${pageContext.servletContext.contextPath}/products/${item.product.id}">
                ${item.product.description}
            </a>
          </td>
          <td class="quantity">
            <fmt:formatNumber value="${item.quantity}" var="quantity"/>
            <c:set var="error" value="${errors[item.product.id]}"/>
            <input name="quantity" class="quantity"
                   value="${not empty error ? paramValues.quantity[status.index] : item.quantity}"/>
            <tags:optionalErrorMessage error="${error}"/>
            <input name="productId" type="hidden" value="${item.product.id}"/>
          </td>
          <td class="price">
            <a href="${pageContext.servletContext.contextPath}/price-history/${item.product.id}">
              <fmt:formatNumber value="${item.product.price}" type="currency"
                                currencySymbol="${item.product.currency.symbol}"/>
            </a>
          </td>
          <td>
            <button form="deleteCartItem"
                    formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem/${item.product.id}">
              Delete
            </button>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${not empty cart.items}">
        <tr>
          <tags:tableInfo title="Total:" quantity="${cart.totalQuantity}" price="${cart.totalCost}"
                        currency="${cart.items.get(0).product.currency.symbol}"/>
          <td></td>
        </tr>
      </c:if>
    </table>
    <p>
      <button>Update</button>
      <button form="checkoutLink">Checkout</button>
    </p>
  </form>
  <form id="checkoutLink" action="${pageContext.servletContext.contextPath}/checkout">
  </form>
  <form id="deleteCartItem" method="post">
  </form>
</tags:master>
