<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="type" required="true" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>

<tr>
  <td>${label}<span style="color: red">*</span></td>
  <td>
    <c:set var="error" value="${errors[name]}"/>
    <input name="${name}" type="${type}" value="${param[name]}"/>
    <tags:optionalErrorMessage error="${error}"/>
  </td>
</tr>