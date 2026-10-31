<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.List,com.example.models.Employe" %>
<% request.setAttribute("pageTitle", "Employés"); %>
<%@ include file="/WEB-INF/jspf/header.jspf" %>

<h1>Liste des employés</h1>
<p class="lead">Réservée aux rôles <code>ADMIN</code> et <code>RH</code>.</p>

<table class="table card">
    <thead>
        <tr><th>#</th><th>Nom</th><th>Poste</th><th>Service</th></tr>
    </thead>
    <tbody>
    <%
        List<Employe> employes = (List<Employe>) request.getAttribute("employes");
        for (Employe e : employes) {
            pageContext.setAttribute("e", e);
    %>
        <tr><td>${e.id}</td><td>${e.nom}</td><td>${e.poste}</td><td>${e.service}</td></tr>
    <% } %>
    </tbody>
</table>

<%@ include file="/WEB-INF/jspf/footer.jspf" %>