<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Connexion"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Connexion</h1>

<% if (request.getAttribute("erreur") != null) { %>
<p class="alert alert-error">${erreur}</p>
<% } %>

<form class="card form" action="${pageContext.request.contextPath}/login" method="post">
    <label>Identifiant
        <input type="text" name="login" autocomplete="username" required autofocus>
    </label>
    <label>Mot de passe
        <input type="password" name="motDePasse" autocomplete="current-password" required>
    </label>
    <button type="submit">Se connecter</button>
</form>

<p class="lead">Comptes de démo : <code>admin / admin123</code> (ADMIN), <code>rh / rh123</code> (RH) ou <code>rakoto / rakoto123</code> (EMPLOYE).</p>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>