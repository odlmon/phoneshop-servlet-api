<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<a href="${pageContext.servletContext.contextPath}/cart" style="float: right;">
  Cart: ${cart.totalQuantity} items
</a>