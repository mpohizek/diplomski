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
	<h2 align="center">Ugovaranje izvoda o prometu računa</h2>
	<form action="ServletUgovaranjeIzvoda" method="post">
		<table align="center">
			<tr>
				<td>Račun: </td>
				<td><select name="racun" id="racun"></select></td>
			</tr>
			<tr>
				<td>Format izvoda: </td>
				<td>
					<select name="formatIzvoda" id="formatIzvoda" onchange="odabirFormataIzvoda()">
						<option value="MN izvod">MN izvod</option>
						<option value="PDF izvod">PDF izvod</option>
						<option value="TXT izvod">TXT izvod</option>
						<option value="MT940">MT940</option>
						<option value="CAMT.053">CAMT.053</option>
						<option value="RTF izvod">RTF izvod</option>
					</select>
				</td>
			</tr>
			<tr>
				<td>Kanal izvoda: </td>
				<td><select name="kanalIzvoda" id="kanalIzvoda">
					<option value="E-mail">E-mail</option>
					<option value="Posta">Posta</option>
					<option value="ConnectDirect (SFTP)">ConnectDirect (SFTP)</option>
					<option value="Swift">Swift</option>
					<option value="Internet bankarstvo">Internet bankarstvo</option>
				</select></td>
			</tr>
			<tr>
				<td colspan="2" align="right"><input type="submit" value="Ugovori"></td>
			</tr>
		</table>
		<input type="hidden" id="iban" name="iban" value="${iban}">
	</form>
<script type="text/javascript">
	var duljinaStringa = '${iban}'.length;
	var brojRacuna = duljinaStringa / 21;
	var racuni = [];
	for (var j=1, i=0; j<=brojRacuna; j++, i+=21) {
		racuni.push('${iban}'.slice(i, i+21));
	}
	var select = document.getElementById("racun");
	for (var i=0; i<brojRacuna; i++){
		var option = document.createElement("option");
		option.text = racuni[i];
		select.add(option);
	}
	var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
	for (var i = 0; i < op.length; i++) {
			if (i==1) {
				continue;
			}
    		op[i].disabled = true;
		}
	
	function odabirFormataIzvoda() {
		var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
		for (var i = 0; i < op.length; i++) {
	    		op[i].disabled = true;
			}
		var select = document.getElementById("formatIzvoda");
		var odabraniFormat = select.options[select.selectedIndex].value;
		switch(odabraniFormat){
		case "MN izvod": {
			var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
			op[1].disabled=false;
			break;
		}
		case "PDF izvod": {
			var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
			op[0].disabled=false;
			op[1].disabled=false;
			op[2].disabled=false;
			op[4].disabled=false;
			break;
		}
		case "TXT izvod": {
			var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
			op[0].disabled=false;
			op[2].disabled=false;
			op[4].disabled=false;
			break;
		}
		case "MT940": {
			var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
			op[3].disabled=false;
			break;
		}
		case "CAMT.053": {
			var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
			op[0].disabled=false;
			op[2].disabled=false;
			break;
		}
		case "RTF izvod": {
			var op = document.getElementById("kanalIzvoda").getElementsByTagName("option");
			op[0].disabled=false;
			op[2].disabled=false;
			op[4].disabled=false;
			break;
		}	
		}
	}
</script>
</body>
</html>