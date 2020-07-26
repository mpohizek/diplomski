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
	<h2 align="center">Pregled ugovorenih izvoda i pripadajućih troškova</h2>
	<table border="1" align="center">
			<tr>
				<td align="center"><b>Račun</b></td>
				<td align="center"><b>Periodika izvoda</b></td>
				<td align="center"><b>Format izvoda</b></td>
				<td align="center"><b>Kanal izvoda</b></td>
				<td align="center"><b>Datum i vrijeme ugovaranja</b></td>
				<td align="center"><b>Jedinični trošak</b></td>
			</tr>
			<c:forEach var="ugovoreniIzvod" items="${listaUgovorenihIzvoda}">
				<tr>
					<td align="center"><c:out value="${ugovoreniIzvod.getIban()}"></c:out></td>
					<td align="center"><c:out value="${ugovoreniIzvod.getPeriodikaIzvoda()}"></c:out></td>
					<td align="center"><c:out value="${ugovoreniIzvod.getFormatIzvoda()}"></c:out></td>
					<td align="center"><c:out value="${ugovoreniIzvod.getKanalIzvoda()}"></c:out></td>
					<td align="center"><c:out value="${ugovoreniIzvod.getDatumVrijemeUgovaranja()}"></c:out></td>
					<td align="center"><c:out value="${ugovoreniIzvod.getCijena()}"></c:out></td>
				</tr>		
			</c:forEach>
		</table>
</body>
</html>