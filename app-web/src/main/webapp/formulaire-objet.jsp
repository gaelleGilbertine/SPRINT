<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Formulaire</title>
</head>
<body>
    <h1>Formulaire</h1>
   <form id="monForm" action="${pageContext.request.contextPath}/personne/objet" method="post">
        <label>Nom : <input type="text" name="nom"></label><br>
        <label>Age : <input type="number" name="age"></label><br>
        <label>Prenom : <input type="text" name="prenom"></label><br>

        <label>Méthode :
        <select onchange="document.getElementById('monForm').method = this.value">
            <option value="post">POST</option>
            <option value="get">GET</option>
        </select>
    </label><br>


        <button type="submit">Envoyer</button>
    </form>
</body>
</html> 