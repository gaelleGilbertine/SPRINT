<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Formulaire Artiste"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Formulaire — un objet</h1>

<form id="monForm" class="card form" action="${pageContext.request.contextPath}/artiste/objet" method="post">
    <label>Nom
        <input type="text" name="nom">
    </label>
    <label>Age
        <input type="number" name="age">
    </label>
    <label>Libellé du genre
        <input type="text" name="genre.libelle">
    </label>
   
        <select onchange="document.getElementById('monForm').method = this.value">
            <option value="post">POST</option>
            <option value="get">GET</option>
        </select>
    </label>
    <button type="submit">Envoyer</button>
</form>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
