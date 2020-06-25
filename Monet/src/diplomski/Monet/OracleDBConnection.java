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
			PreparedStatement ps = con.prepareStatement("select * from korisnikUsluge where korisnickoIme = ? and lozinka = ? and rownum = 1");
			ps.setString(1, korisnickoIme);
			ps.setString(2, lozinka);
			ResultSet rs = ps.executeQuery();
			PrijavljeniKorisnik prijavljeniKorisnik = new PrijavljeniKorisnik();
			while (rs.next()) {
				prijavljeniKorisnik.setKorisnickoIme(korisnickoIme);
				prijavljeniKorisnik.setLozinka(lozinka);
				prijavljeniKorisnik.setKlijentOsobaID(rs.getInt(3));
			}
			
			if (prijavljeniKorisnik.getKorisnickoIme()!=null) {
				PreparedStatement ps2 = con.prepareStatement("select * from klijentEvidencijaOsoba where klijentOsobaID = ?");
				ps2.setInt(1, prijavljeniKorisnik.getKlijentOsobaID());
				ResultSet rs2 = ps2.executeQuery();
				while (rs2.next()) {
					prijavljeniKorisnik.setKlijentID(rs2.getInt(2));
					prijavljeniKorisnik.setOsobaID(rs2.getInt(3));
				}
				PreparedStatement ps3 = con.prepareStatement("select * from evidencijaOsoba where osobaID = ?");
				ps3.setInt(1, prijavljeniKorisnik.getOsobaID());
				ResultSet rs3 = ps3.executeQuery();
				while (rs3.next()) {
					prijavljeniKorisnik.setIme(rs3.getString(3));
					prijavljeniKorisnik.setPrezime(rs3.getString(4));
					prijavljeniKorisnik.setOib(rs3.getString(2));
				}
				PreparedStatement ps4 = con.prepareStatement("select u.uslugaID, u.klijentID, u.racunID from usluga u, racun r, klijent k, korisnikUsluge ku where k.sifraAktivnosti = 1 and r.sifraAktivnosti = 1 and u.sifraAktivnosti = 1 and ku.sifraAktivnosti = 1 and k.klijentID = " + prijavljeniKorisnik.getKlijentID() + " and ku.klijentOsobaID = " + prijavljeniKorisnik.getKlijentOsobaID() + " and u.racunID = r.racunID and u.klijentID = k.klijentID and r.klijentID = k.klijentID and ku.uslugaID = u.uslugaID");
				ResultSet rs4 = ps4.executeQuery();
				while (rs4.next()) {
					UslugaInternetBankarstvo usluga = new UslugaInternetBankarstvo();
					usluga.setKlijentID(rs4.getInt(2));
					usluga.setRacunID(rs4.getInt(3));
					usluga.setUslugaID(rs4.getInt(1));
					prijavljeniKorisnik.aktivneUsluge.add(usluga);
				}
				
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
