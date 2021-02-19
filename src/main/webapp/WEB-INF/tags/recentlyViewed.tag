<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<c:if test="${not empty recentlyViewed.items}">
    <h3>Recently viewed</h3>
    <table>
        <tr>
            <c:forEach items="${recentlyViewed.items}" var="product">
                <td>
                    <p>
                        <img class="product-tile" src="${product.imageUrl}">
                    </p>
                    <p>
                        <a href="${pageContext.servletContext.contextPath}/products/${product.id}">
                                ${product.description}
                        </a></p>
                    <p>
                        <fmt:formatNumber value="${product.price}" type="currency"
                                          currencySymbol="${product.currency.symbol}"/>
                    </p>
                </td>
            </c:forEach>
        </tr>
    </table>
</c:if>