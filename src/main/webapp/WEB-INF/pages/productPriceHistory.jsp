<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>
<tags:master pageTitle="Price History">
    <h3>Price history</h3>
    <h4>${product.description}</h4>
    <table>
        <tr>
            <td><b>Start date</b></td>
            <td><b>Price</b></td>
        </tr>
        <c:forEach var="priceDate" items="${product.priceHistory}">
            <tr>
                <td>
                    <fmt:formatDate type="date" value="${priceDate.startDate}"/>
                </td>
                <td>
                    <fmt:formatNumber value="${priceDate.price}" type="currency" currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>
