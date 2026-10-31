<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="false" %>
<% request.setAttribute("pageTitle", "Accès refusé"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Accès refusé (403)</h1>
<p class="alert alert-error">${message}</p>
<p class="lead">Vous êtes connecté en tant que <strong>${sessionScope['auth.user'].nom}</strong>
    avec le(s) rôle(s) <code>${sessionScope['auth.roles']}</code>.
    Cette page exige l'un des rôles : <code><%= String.join(", ", (String[]) request.getAttribute("rolesRequis")) %></code>.</p>
<p><a href="${pageContext.request.contextPath}/">Retour à l'accueil</a></p>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>