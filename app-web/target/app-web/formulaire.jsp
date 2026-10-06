<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Formulaire</title>
</head>
<body>
    <h1>Formulaire</h1>
    <form action="${pageContext.request.contextPath}/personne/save" method="post">
        <label>Nom : <input type="text" name="nom"></label><br>
        <label>Age : <input type="number" name="age"></label><br>
        <button type="submit">Envoyer</button>
    </form>
</body>
</html>