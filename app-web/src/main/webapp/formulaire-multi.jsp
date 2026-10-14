<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Deux personnes"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Comparer deux personnes</h1>

<form class="card form" action="${pageContext.request.contextPath}/api/comparer" method="post">
    <h2>Personne 1</h2>
    <label>Nom
        <input type="text" name="p1.nom">
    </label>
    <label>Age
        <input type="number" name="p1.age">
    </label>
    <label>Ville
        <input type="text" name="p1.adresse.ville">
    </label>

    <h2>Personne 2</h2>
    <label>Nom
        <input type="text" name="p2.nom">
    </label>
    <label>Age
        <input type="number" name="p2.age">
    </label>
    <label>Ville
        <input type="text" name="p2.adresse.ville">
    </label>

    <button type="submit">Envoyer</button>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
