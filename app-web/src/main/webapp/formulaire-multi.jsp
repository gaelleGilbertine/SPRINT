<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Deux personnes</title></head>
<body>
    <h1>Comparer deux personnes</h1>
    <form action="${pageContext.request.contextPath}/api/comparer" method="post">

        <h2>Personne 1</h2>
        <label>Nom : <input type="text" name="p1.nom"></label><br>
        <label>Age : <input type="number" name="p1.age"></label><br>
        <label>Ville : <input type="text" name="p1.adresse.ville"></label><br>

        <h2>Personne 2</h2>
        <label>Nom : <input type="text" name="p2.nom"></label><br>
        <label>Age : <input type="number" name="p2.age"></label><br>
        <label>Ville : <input type="text" name="p2.adresse.ville"></label><br>

        <button type="submit">Envoyer</button>
    </form>
</body>
</html>