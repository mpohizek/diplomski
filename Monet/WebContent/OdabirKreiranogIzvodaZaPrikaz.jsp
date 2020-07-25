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
	<h2 align="center">Odabir kreirnog izvoda za prikaz</h2>
	<p>Iz padajućeg izbornika odaberite kreirani izvod čiji sadržaj želite vidjeti. Ako je padajući izbornik prazan, ne postoji kreirani izvod. U padajućem izborniku se mogu nalaziti samo oni izvodi čiji je kanal Internet bankarstvo.</p>
	<form action="ServletOdabirKreiranogIzvodaZaPrikaz" method="post">
		<table align="center">
			<tr>
				<td>Kreirani izvod: </td>
				<td><select name="izvod" id="izvod">
						<!--<c:forEach var="listaKreiranihIzvoda" items="${listaKreiranihIzvoda}">
							<option value="${listaKreiranihIzvoda.getKreiraniIzvodID()}">${listaKreiranihIzvoda.getIban()}</option>
						</c:forEach> -->
					</select></td>
			</tr>
			<tr>
				<td colspan="2" align="right"><input type="submit" value="Odaberi"></td>
			</tr>
		</table>
	</form>
	<script type="text/javascript">
		var podaci = '${kreiraniIzvodiSelect}';
		var splitted = podaci.split('**');
		var opcijeZaOdabir = [];
		for (var i = 0; i < splitted.length; i++) {
			var red = splitted[i].split('ID');
			var opcija = new Array (red[0], red[1]);
			opcijeZaOdabir.push(opcija);
		}
		
		var select = document.getElementById("izvod");
		for (var j = 0; j < opcijeZaOdabir.length; j++){
			var option = document.createElement("option");
			option.text = opcijeZaOdabir[j][0];
			option.value = opcijeZaOdabir[j][1];
			select.add(option);
		}
	</script>
</body>
</html>