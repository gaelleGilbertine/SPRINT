<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Simple + objet</title></head>
<body>
    <h1>Type simple + objet</h1>
    <form action="${pageContext.request.contextPath}/api/mixte" method="post">

        <h2>Paramètres simples</h2>
        <label>Titre : <input type="text" name="titre"></label><br>
        <label>Quantité : <input type="number" name="quantite"></label><br>

        <h2>Objet Personne (préfixe p.)</h2>
        <label>Nom : <input type="text" name="p.nom"></label><br>
        <label>Age : <input type="number" name="p.age"></label><br>
        <label>Ville : <input type="text" name="p.adresse.ville"></label><br>

        <button type="submit">Envoyer</button>
    </form>
</body>
</html>