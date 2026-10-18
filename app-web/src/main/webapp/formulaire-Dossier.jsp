<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Formulaire Dossier"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Formulaire — objet avec MapFile (sprint 8)</h1>

<form class="card form" action="${pageContext.request.contextPath}/dossier/objet" method="post"
      enctype="multipart/form-data">
    <label>Nom
        <input type="text" name="nom">
    </label>
    <label>Prénom
        <input type="text" name="prenom">
    </label>
    <label>Nom du fichier
        <input type="text" name="fichier.nomFichier">
    </label>
    <label>Type
        <input type="text" name="fichier.type">
    </label>
    <label>Recto CIN
        <input type="file" name="fichier.rectoCIN" accept="image/*,.pdf">
    </label>
    <label>Verso CIN
        <input type="file" name="fichier.versoCIN" accept="image/*,.pdf">
    </label>
    <button type="submit">Envoyer</button>
</form>

<p class="lead">
    La réponse JSON indique <code>mapFilePresent</code> (l'objet lié contient
    l'attribut MapFile) et les tailles effectivement chargées en octets.
</p>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>
