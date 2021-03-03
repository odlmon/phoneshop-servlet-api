<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<jsp:useBean id="errors" class="java.util.HashMap" scope="request"/>
<jsp:useBean id="paymentMethods" type="java.util.List" scope="request"/>
<tags:master pageTitle="Checkout">
  <p>Cart: ${order}</p>
  <tags:status message="${param.message}" hasError="${not empty errors}"/>
  <form method="post" action="${pageContext.servletContext.contextPath}/checkout">
    <table>
      <thead>
      <tr>
        <td>Image</td>
        <td>Description</td>
        <td class="quantity">Quantity</td>
        <td class="price">Price</td>
      </tr>
      </thead>
      <c:forEach var="item" items="${order.items}" varStatus="status">
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
            ${item.quantity}
          </td>
          <td class="price">
            <a href="${pageContext.servletContext.contextPath}/price-history/${item.product.id}">
              <fmt:formatNumber value="${item.product.price}" type="currency"
                                currencySymbol="${item.product.currency.symbol}"/>
            </a>
          </td>
        </tr>
      </c:forEach>
      <c:if test="${not empty order.items}">
        <c:set var="currency" value="${order.items.get(0).product.currency.symbol}"/>
        <tr>
          <tags:tableInfo title="Subtotal:" quantity="${order.totalQuantity}" price="${order.subtotal}"
                            currency="${currency}"/>
        </tr>
        <tr>
          <tags:tableInfo title="Delivery cost:" price="${order.deliveryCost}" currency="${currency}"/>
        </tr>
        <tr>
          <tags:tableInfo title="Total:" price="${order.totalCost}" currency="${currency}"/>
        </tr>
      </c:if>
    </table>
    <h2>Your details</h2>
    <table>
      <tags:orderFormRow name="firstName" label="First name" type="text" errors="${errors}"/>
      <tags:orderFormRow name="lastName" label="Last name" type="text" errors="${errors}"/>
      <tags:orderFormRow name="phone" label="Phone" type="text" errors="${errors}"/>
      <tags:orderFormRow name="deliveryDate" label="Delivery date" type="date" errors="${errors}"/>
      <tags:orderFormRow name="deliveryAddress" label="Delivery address" type="text" errors="${errors}"/>
      <tr>
        <td>Payment method<span style="color: red">*</span></td>
        <td>
          <c:set var="error" value="${errors['paymentMethod']}"/>
          <select name="paymentMethod">
            <option></option>
            <c:forEach var="paymentMethod" items="${paymentMethods}">
              <c:set var="selectedMethod" value="${param.paymentMethod}"/>
              <c:set var="selection" value="${selectedMethod eq paymentMethod ? 'selected' : ''}"/>
              <option value="${paymentMethod}" ${selection}>${paymentMethod.label}</option>
            </c:forEach>
          </select>
          <tags:optionalErrorMessage error="${error}"/>
        </td>
      </tr>
    </table>
    <p>
      <button>Place order</button>
    </p>
  </form>
</tags:master>
