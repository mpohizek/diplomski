<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="diplomski.Monet.PrijavljeniKorisnik"%>
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
		<tr><td style="padding:15px"><a href="PocetnaStranica.jsp">Otvoreni računi</a></td><td style="padding:15px"><a href="UgovaranjeIzvoda.jsp">Ugovaranje izvoda</a></td><td style="padding:15px"><a href="PregledUgovorenihIzvoda.jsp">Pregled ugovorenih izvoda</a></td><td style="padding:15px"><a href="PrikazKreiranihIzvoda.jsp">Prikaz kreiranih izvoda</a></td></tr>
	</table>
	<div style="width: 300px; float:left; height:235px; margin:10px">
		<strong>Podaci o poduzeću:</strong>
		<br/>
		<table>
			<tr>
				<td><b>Naziv:</b></td><td>${klijent.getNaziv()}</td>
			</tr>
			<tr>
				<td><b>OIB:</b></td><td>${klijent.getOib()}</td>
			</tr>
			<tr>
				<td><b>MBS:</b></td><td>${klijent.getMbs()}</td>
			</tr>
			<tr>
				<td><b>Ulica:</b></td><td>${klijent.getUlica()}</td>
			</tr>
			<tr>
				<td><b>Kućni broj:</b></td><td>${klijent.getKbr()}</td>
			</tr>
			<tr>
				<td><b>Broj pošte:</b></td><td>${klijent.getBrPoste()}</td>
			</tr>
			<tr>
				<td><b>Mjesto:</b></td><td>${klijent.getMjesto()}</td>
			</tr>
			<tr>
				<td><b>Država:</b></td><td>${klijent.getDrzava()}</td>
			</tr>
		</table>
	</div>
	<div style="width: 300px; float:left; height:235px; margin:10px">
		<strong>Podaci o opunomoćeniku:</strong>
		<br/>
		<table>
			<tr>
				<td><b>Ime:</b></td><td>${prijavljeniKorisnik.getIme()}</td>
			</tr>
			<tr>
				<td><b>Prezime:</b></td><td>${prijavljeniKorisnik.getPrezime()}</td>
			</tr>
		</table>
	</div>
	<div style="width: 300px; float:left; height:235px; background:rgb(187, 195, 227); margin:10px">
		<strong>Otvoreni računi:</strong>
		<br/>
		<table>
			<tr>
				<td><b>IBAN:</b></td>
			</tr>
				<c:forEach var="usluga" items="${aktivneUsluge}">
					<tr>
						<td><a href="UgovaranjeIzvoda.jsp"><c:out value="${usluga.getIban()}"></c:out></a></td>
					</tr>		
				</c:forEach>
		</table>
	</div>
	
</body>
</html>