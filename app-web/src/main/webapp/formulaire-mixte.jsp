<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Simple + objet"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Type simple + objet</h1>

<form class="card form" action="${pageContext.request.contextPath}/api/mixte" method="post">
    <h2>Paramètres simples</h2>
    <label>Titre
        <input type="text" name="titre">
    </label>
    <label>Quantité
        <input type="number" name="quantite">
    </label>

    <h2>Objet Personne (préfixe p.)</h2>
    <label>Nom
        <input type="text" name="p.nom">
    </label>
    <label>Age
        <input type="number" name="p.age">
    </label>
    <label>Ville
        <input type="text" name="p.adresse.ville">
    </label>

    <button type="submit">Envoyer</button>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
