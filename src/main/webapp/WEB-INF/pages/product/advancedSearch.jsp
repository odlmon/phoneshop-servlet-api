<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="minPriceError" class="java.lang.String" scope="request"/>
<jsp:useBean id="maxPriceError" class="java.lang.String" scope="request"/>
<jsp:useBean id="products" class="java.util.ArrayList" scope="request"/>
<jsp:useBean id="searchTypes" type="java.util.List" scope="request"/>
<tags:master pageTitle="Advanced Search">
  <h2>Advanced search</h2>
  <form method="post">
    <p>
      Description <input type="text" name="description" value="${param["description"]}">
      <select name="searchType">
        <c:forEach var="searchType" items="${searchTypes}">
          <c:set var="selectedMethod" value="${param.searchType}"/>
          <c:set var="selection" value="${selectedMethod eq searchType ? 'selected' : ''}"/>
          <option value="${searchType}" ${selection}>${searchType.label}</option>
        </c:forEach>
      </select>
    </p>
    <p>
      Min price <input type="text" name="min-price" value="${param["min-price"]}">
      <tags:optionalErrorMessage error="${minPriceError}"/>
    </p>
    <p>
      Max price <input type="text" name="max-price" value="${param["max-price"]}">
      <tags:optionalErrorMessage error="${maxPriceError}"/>
    </p>
    <button>Search</button>
  </form>
  <c:if test="${not empty products}">
    <p>
      <table>
        <thead>
          <tr>
            <td>Image</td>
            <td>Description</td>
            <td class="price">Price</td>
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
            <td class="price">
              <a href="${pageContext.servletContext.contextPath}/price-history/${product.id}">
                <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
              </a>
            </td>
          </tr>
        </c:forEach>
      </table>
    </p>
  </c:if>
</tags:master>