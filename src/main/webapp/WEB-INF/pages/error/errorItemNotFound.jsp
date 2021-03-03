<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ page isErrorPage="true" %>

<tags:master pageTitle="Item not found">
  <c:set var="id" value="${pageContext.exception.id}"/>
  <h1>Item not found ${not empty id ? 'by '.concat(id) : ''}</h1>
</tags:master>