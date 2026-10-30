<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% request.setAttribute("pageTitle", "Accueil"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Démo du mini-framework</h1>
<p class="lead">Choisissez une démonstration :</p>

<section class="card">
    <h2>Sorties du contrôleur</h2>
    <ul class="link-list">
        <li><a href="${pageContext.request.contextPath}/page/hello">Sortie HTML (forward vers une JSP)</a></li>
        <li><a href="${pageContext.request.contextPath}/api/hello">Sortie JSON (@JsonResponse)</a></li>
    </ul>
</section>

<section class="card">
    <h2>Formulaires (data binding)</h2>
    <ul class="link-list">
        <li><a href="${pageContext.request.contextPath}/formulaire.jsp">Paramètres simples (sprint 7)</a></li>
        <li><a href="${pageContext.request.contextPath}/formulaire-objet.jsp">Un objet (sprint 7 bis)</a></li>
        <li><a href="${pageContext.request.contextPath}/formulaire-multi.jsp">Plusieurs objets du même type</a></li>
        <li><a href="${pageContext.request.contextPath}/formulaire-mixte.jsp">Type simple + objet</a></li>
        <li><a href="${pageContext.request.contextPath}/formulaire-Artiste.jsp">Un objet imbriqué (Artiste)</a></li>
        <li><a href="${pageContext.request.contextPath}/formulaire-Dossier.jsp">Pièces jointes MapFile (sprint 8)</a></li>
    </ul>
</section>
<section class="card">
    <h2>Authentification (sprint 10)</h2>
    <ul class="link-list">
        <li><a href="${pageContext.request.contextPath}/login">Se connecter</a></li>
        <li><a href="${pageContext.request.contextPath}/secret/page">Page réservée aux connectés (@Authenticated)</a></li>
    </ul>
</section>
<%@ include file="/WEB-INF/jspf/footer.jspf" %>
