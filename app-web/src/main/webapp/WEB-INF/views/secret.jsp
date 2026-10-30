<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Espace connecté"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Espace réservé</h1>
<p class="lead">Bonjour ${utilisateur.nom} : cette page n'est visible que par les personnes connectées.</p>

<section class="card">
    <h2>Exécuter la fonction</h2>
    <p>Envoie un nombre à <code>POST /secret/executer</code> (méthode <code>@Authenticated</code>).</p>
    <form id="fonctionForm" class="form">
        <label>Nombre
            <input type="number" id="nombre" name="nombre" value="7" required>
        </label>
        <button type="submit">Exécuter</button>
    </form>
    <pre id="sortie" class="output">(aucun résultat)</pre>
</section>

<script>
document.getElementById('fonctionForm').addEventListener('submit', async function (e) {
    e.preventDefault();
    var sortie = document.getElementById('sortie');
    var corps = new URLSearchParams({ nombre: document.getElementById('nombre').value });
    try {
        var reponse = await fetch('${pageContext.request.contextPath}/secret/executer', {
            method: 'POST',
            body: corps
        });
        var json = await reponse.json();
        sortie.textContent = reponse.status + ' ' + JSON.stringify(json, null, 2);
    } catch (err) {
        sortie.textContent = 'Erreur : ' + err;
    }
});
</script>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>