<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Monet</title>
</head>
<body>
	<h1 align="center">Prijava</h1>
	<form action="ServletPrijava" method="post">
		<table align="center">
			<tr><td>Korisničko ime: </td><td><input type="text" name="korisnickoIme" size="30"></td></tr>
			<tr><td>Lozinka: </td><td><input type="password" name="lozinka" size="30"></td></tr>
			<tr><td colspan="2" align="right"><input type="submit" value="Prijava"></td></tr>
		</table>
	</form>
	<table align="center" style="padding-top:30px">
		<tr>
			<td align="center"><img id="logo" src="https://i.imgur.com/HQ3BJan.png" width="150px" height="150px"/></td>
		</tr>
	</table>
</body>
</html>