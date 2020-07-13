<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Monet</title>
</head>
<body>
<p>Klikom na gumb <i>Potvrđujem zahtjev</i> ugovarate izvod o prometu računa sa sljedećim podacima:</p>
<b>Račun:</b> ${odabraniRacun} <br/>
<b>Format izvoda:</b> ${odabraniFormat}<br/>
<b>Kanal izvoda:</b> ${odabraniKanal}<br/>
<b>Cijena jednog izvoda:</b> ${cijena}
<table style="padding:15px">
	<tr>
		<td>
			<form action="ServletPotvrdaUgovaranjaIzvoda" method="post">
				<input type="hidden" id="odabraniRacun" name="odabraniRacun" value="${odabraniRacun}">
				<input type="hidden" id="odabraniFormat" name="odabraniFormat" value="${odabraniFormat}">
				<input type="hidden" id="odabraniKanal" name="odabraniKanal" value="${odabraniKanal}">
				<input type="hidden" id="iban" name="iban" value="${iban}">
				<input type="submit" value="Potvrđujem zahtjev">
			</form>
		</td>
		<td>
			<form action="PocetnaStranica.jsp" method="post">
				<input type="submit" value="Odustajem od zahtjeva">
			</form>
		</td>
	</tr>
</table>
</body>
</html>