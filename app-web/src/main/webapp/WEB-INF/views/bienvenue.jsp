<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Bienvenue"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>${titre}</h1>
<p>Message : ${message}</p>
<p>Statut : ${status}</p>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
