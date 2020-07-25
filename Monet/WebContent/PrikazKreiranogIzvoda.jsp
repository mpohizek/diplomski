<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Monet</title>
</head>
<body>
	<table align="center">
		<tr><td style="padding:15px"><a href="PocetnaStranica.jsp">Otvoreni računi</a></td><td style="padding:15px"><a href="UgovaranjeIzvoda.jsp">Ugovaranje izvoda</a></td><td style="padding:15px"><a href="PregledUgovorenihIzvoda.jsp">Pregled ugovorenih izvoda</a></td><td style="padding:15px"><a href="OdabirKreiranogIzvodaZaPrikaz.jsp">Prikaz kreiranih izvoda</a></td></tr>
	</table>
	<h2 align="center">Prikaz kreiranih izvoda</h2>
	<table>
			<tr>
				<td><b>IBAN:</b></td><td>${kreiraniIzvod.getIban()}</td>
			</tr>
			<tr>
				<td><b>Periodika izvoda:</b></td><td>${kreiraniIzvod.getPeriodika()}</td>
			</tr>
			<tr>
				<td><b>Datum izvoda:</b></td><td>${kreiraniIzvod.getDatumIzvoda()}</td>
			</tr>
			<tr>
				<td><b>Redni broj izvoda:</b></td><td>${kreiraniIzvod.getBrojIzvoda()}</td>
			</tr>
			<tr>
				<td><b>Prethodno stanje:</b></td><td>${kreiraniIzvod.getPrethodnoStanje()}</td>
			</tr>
			<tr>
				<td><b>Završno stanje:</b></td><td>${kreiraniIzvod.getZavrsnoStanje()} HRK</td>
			</tr>
	</table>
	<table border="1" align="center">
		<tr bgcolor="rgb(187, 195, 227)">
			<td align="center"><b>Datum i vrijeme promjene salda</b></td>
			<td align="center"><b>Opis</b></td>
			<td align="center"><b>Uplata</b></td>
			<td align="center"><b>Isplata</b></td>
			<td align="center"><b>Valuta</b></td>
		</tr>
		<c:forEach var="stavka" items="${stavkeIzvoda}">
			<tr>
				<td align="center"><c:out value="${stavka.getDatumVrijemePromjeneSalda()}"></c:out></td>
				<td align="center"><c:out value="${stavka.getOpis()}"></c:out></td>
				<td align="center"><c:out value="${stavka.getUplata()}"></c:out></td>
				<td align="center"><c:out value="${stavka.getIsplata()}"></c:out></td>
				<td align="center"><c:out value="HRK"></c:out></td>
			</tr>		
		</c:forEach>
	</table>
	<p align="center"></p>
</body>
</html>