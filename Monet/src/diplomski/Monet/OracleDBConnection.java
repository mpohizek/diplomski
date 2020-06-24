package diplomski.Monet;

import java.sql.*;

public class OracleDBConnection {
	private static String oracleDriver = "oracle.jdbc.driver.OracleDriver";
	private static String url = "jdbc:oracle:thin:@10.24.13.208:1521/XE";
	private static String user = "sys as sysdba";
	private static String pswd = "admin";
	
	public static PrijavljeniKorisnik AutentikacijaKorisnika(String korisnickoIme, String lozinka) throws SQLException {
		try {
			Class.forName(oracleDriver);
			Connection con = DriverManager.getConnection(url, user, pswd);
			PreparedStatement ps = con.prepareStatement("select * from korisnikUsluge where korisnickoIme = ? and lozinka = ?");
			ps.setString(1, korisnickoIme);
			ps.setString(2, lozinka);
			ResultSet rs = ps.executeQuery();
			PrijavljeniKorisnik prijavljeniKorisnik = new PrijavljeniKorisnik();
			while (rs.next()) {
				prijavljeniKorisnik.setKorisnickoIme(korisnickoIme);
				prijavljeniKorisnik.setLozinka(lozinka);
			}
			con.close();
			return prijavljeniKorisnik;
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
