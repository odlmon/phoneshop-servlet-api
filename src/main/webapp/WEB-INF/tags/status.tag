<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="message" required="true" %>
<%@ attribute name="hasError" required="true" %>

<c:if test="${not empty message}">
  <div class="success">
      ${message}
  </div>
</c:if>
<c:if test="${hasError}">
  <div class="error">
    There were errors updating cart
  </div>
</c:if>