<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Monet</title>
</head>
<body>
<p>Izvod za račun <b>${odabraniRacun}</b> s formatom <b>${odabraniFormat}</b> i kanalom <b>${odabraniKanal}</b> je već prethodno ugovoren.</p>
<form action="UgovaranjeIzvoda.jsp" method="post">
	<input type="submit" value="Natrag na ugovaranje izvoda">
</form>
</body>
</html>