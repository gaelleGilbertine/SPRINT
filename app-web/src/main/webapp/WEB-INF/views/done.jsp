<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Résultat"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Données reçues</h1>

<dl class="result">
    <dt>Nom</dt>
    <dd>${nom}</dd>
    <dt>Age</dt>
    <dd>${age}</dd>
    <dt>Prenom</dt>
    <dd>${prenom}</dd>
</dl>

<h2>Adresse (objet imbriqué)</h2>
<dl class="result">
    <dt>Rue</dt>
    <dd>${rue}</dd>
    <dt>Ville</dt>
    <dd>${ville}</dd>
    <dt>Code postal</dt>
    <dd>${codePostal}</dd>
</dl>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
