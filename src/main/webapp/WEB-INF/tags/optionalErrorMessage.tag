<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="error" required="true" %>

<c:if test="${not empty error}">
  <div class="error">
      ${error}
  </div>
</c:if>