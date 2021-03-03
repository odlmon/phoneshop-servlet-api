<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ attribute name="title" required="true" %>
<%@ attribute name="quantity" required="false" %>
<%@ attribute name="price" required="true" %>
<%@ attribute name="currency" required="true" %>

<td>${title}</td>
<td></td>
<td class="quantity">
  <fmt:formatNumber value="${quantity}"/>
</td>
<td class="price">
  <fmt:formatNumber value="${price}" type="currency" currencySymbol="${currency}"/>
</td>