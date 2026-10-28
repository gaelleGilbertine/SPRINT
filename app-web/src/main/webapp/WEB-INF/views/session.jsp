<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head><meta charset="UTF-8"><title>Test session</title></head>
<body>
  <h1>Test de la session</h1>

  <p><strong>ID de session :</strong> ${sessionId}</p>
  <p><strong>Utilisateur stocké :</strong>
    <p><strong>Utilisateur stocké :</strong> ${empty user ? '(aucun)' : user}</p>
  </p>

  <form method="post" action="${pageContext.request.contextPath}/session/page">
    <input type="text" name="nom" placeholder="Votre nom">
    <button type="submit">Enregistrer dans la session</button>
  </form>

  <p>
    <a href="${pageContext.request.contextPath}/session/page">Recharger (GET)</a> |
    <a href="${pageContext.request.contextPath}/session/logout">Fermer la session</a>
  </p>
</body>
</html>