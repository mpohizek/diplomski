package diplomski.Monet;

import java.sql.*;

public class OracleDBConnection {
	private static String oracleDriver = "oracle.jdbc.driver.OracleDriver";
	private static String url = "jdbc:oracle:thin:@DESKTOP-GU61KPT:1521/XE";
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
				PreparedStatement ps4 = con.prepareStatement("select u.uslugaID, u.klijentID, u.racunID, r.IBAN from usluga u, racun r, klijent k, korisnikUsluge ku where k.sifraAktivnosti = 1 and r.sifraAktivnosti = 1 and u.sifraAktivnosti = 1 and ku.sifraAktivnosti = 1 and k.klijentID = " + prijavljeniKorisnik.getKlijentID() + " and ku.klijentOsobaID = " + prijavljeniKorisnik.getKlijentOsobaID() + " and u.racunID = r.racunID and u.klijentID = k.klijentID and r.klijentID = k.klijentID and ku.uslugaID = u.uslugaID");
				ResultSet rs4 = ps4.executeQuery();
				while (rs4.next()) {
					UslugaInternetBankarstvo usluga = new UslugaInternetBankarstvo();
					usluga.setKlijentID(rs4.getInt(2));
					usluga.setRacunID(rs4.getInt(3));
					usluga.setUslugaID(rs4.getInt(1));
					usluga.setIban(rs4.getString(4));
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
	
	public static Klijent dohvatiKlijenta(int klijentID) throws SQLException, ClassNotFoundException {
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		PreparedStatement ps = con.prepareStatement("select k.klijentID, k.OIB, k.MBS, k.naziv, k.ulica, k.kbr, k.brPoste, sm.mjesto, sd.nazivDrzave from klijent k, sifarnikMjesta sm, sifarnikDrzava sd where k.klijentID = '" + klijentID + "' and k.brPoste = sm.brPoste and k.sifraDrzave = sd.sifraDrzave");
		ResultSet rs = ps.executeQuery();
		Klijent klijent = new Klijent();
		while (rs.next()) {
			klijent.setKlijentID(rs.getInt(1));
			klijent.setOib(rs.getString(2));
			klijent.setMbs(rs.getString(3));
			klijent.setNaziv(rs.getString(4));
			klijent.setUlica(rs.getString(5));
			klijent.setKbr(rs.getString(6));
			klijent.setBrPoste(rs.getInt(7));
			klijent.setMjesto(rs.getString(8));
			klijent.setDrzava(rs.getString(9));
		}
		con.close();
		return klijent;
	}
	
	public static float dohvatiCijenuIzvoda (String formatIzvoda, String kanalIzvoda) throws ClassNotFoundException, SQLException {
		float cijena = 0;
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		PreparedStatement ps = con.prepareStatement("select ci.cijena from cjenikIzvoda ci, sifarnikFormataIzvoda sfi, sifarnikKanalaIzvoda ski where sfi.nazivFormata ='" + formatIzvoda + "' and ski.nazivKanala = '" + kanalIzvoda + "' and ci.sifraFormataIzvoda = sfi.sifraFormataIzvoda and ci.sifraKanalaIzvoda = ski.sifraKanalaIzvoda");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			cijena = rs.getFloat(1);
		}
		con.close();
		return cijena;
	}
	
	public static int dohvatiRacunID (String iban) throws ClassNotFoundException, SQLException {
		int racunID = 0;
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		PreparedStatement ps = con.prepareStatement("select racunID from racun where IBAN = '" + iban + "'");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			racunID = rs.getInt(1);
		}
		return racunID;
	}
	
	public static int dohvatiSifruFormataIzvoda (String formatIzvoda) throws ClassNotFoundException, SQLException {
		int sifraFormataIzvoda = 0;
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		PreparedStatement ps = con.prepareStatement("select sifraFormataIzvoda from sifarnikFormataIzvoda where nazivFormata = '" + formatIzvoda + "'");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			sifraFormataIzvoda = rs.getInt(1);
		}
		return  sifraFormataIzvoda;
	}
	
	public static int dohvatiSifrkuKanalaIzvoda (String kanalIzvoda) throws ClassNotFoundException, SQLException {
		int sifraKanalaIzvoda = 0;
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		PreparedStatement ps = con.prepareStatement("select sifraKanalaIzvoda from sifarnikKanalaIzvoda where nazivKanala = '" + kanalIzvoda + "'");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			sifraKanalaIzvoda = rs.getInt(1);
		}
		return sifraKanalaIzvoda;
	}
	
	public static void unesiUgovoreniIzvod (String iban, String formatIzvoda, String kanalIzvoda) throws ClassNotFoundException, SQLException {
		int racunID = 0;
		int sifraFormataIzvoda = 0;
		int sifraKanalaIzvoda = 0;
		
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		
		//Dohvaćanje atributa racunID
		racunID = dohvatiRacunID(iban);
		
		//Dohvaćanje atributa sifraFormataIzvoda
		sifraFormataIzvoda = dohvatiSifruFormataIzvoda(formatIzvoda);
		
		//Dohvaćanje atributa sifraKanalaIzvoda
		sifraKanalaIzvoda = dohvatiSifrkuKanalaIzvoda(kanalIzvoda);
		
		//Insert
		PreparedStatement ps = con.prepareStatement("insert into racunUgovoreniIzvod values (racunUgovoreniIzvod_SEQ.NEXTVAL, " + racunID + ", " + sifraFormataIzvoda + ", " + sifraKanalaIzvoda + ", SYSDATE)");
		ps.executeUpdate();
		con.close();
	}

	public static boolean dohvatiUgovoreniIzvod (String iban, String formatIzvoda, String kanalIzvoda) throws ClassNotFoundException, SQLException {
		int racunID = 0;
		int sifraFormataIzvoda = 0;
		int sifraKanalaIzvoda = 0;
		boolean postojiUgovoreniIzvod = false;
		
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		
		//Dohvaćanje atributa racunID
		racunID = dohvatiRacunID(iban);
		
		//Dohvaćanje atributa sifraFormataIzvoda
		sifraFormataIzvoda = dohvatiSifruFormataIzvoda(formatIzvoda);
		
		//Dohvaćanje atributa sifraKanalaIzvoda
		sifraKanalaIzvoda = dohvatiSifrkuKanalaIzvoda(kanalIzvoda);
		
		PreparedStatement ps = con.prepareStatement("select * from racunUgovoreniIzvod where racunID = " + racunID + " and sifraFormataIzvoda = " + sifraFormataIzvoda + " and sifraKanalaIzvoda = " + sifraKanalaIzvoda);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			postojiUgovoreniIzvod = true;
		}
		return postojiUgovoreniIzvod;
	}
}
