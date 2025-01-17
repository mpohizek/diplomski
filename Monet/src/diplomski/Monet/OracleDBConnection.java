package diplomski.Monet;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

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
		con.close();
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
		con.close();
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
		con.close();
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
		con.close();
		return postojiUgovoreniIzvod;
	}
	
	public static List<UgovoreniIzvod> dohvatiPodatkeUgovoreniIzvodi (List<Integer> listaRacunID) throws ClassNotFoundException, SQLException {
		List<UgovoreniIzvod> listaUgovorenihIzvoda = new ArrayList<>();
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		for (int racunID : listaRacunID) {
			PreparedStatement ps = con.prepareStatement("select rui.racunUgovoreniIzvodID, r.IBAN, spi.nazivPeriodike, sfi.nazivFormata, ski.nazivKanala, rui.datumVrijemeUgovaranja, ci.cijena from cjenikIzvoda ci, racunUgovoreniIzvod rui, racun r, sifarnikPeriodikaIzvoda spi, sifarnikFormataIzvoda sfi, sifarnikKanalaIzvoda ski where r.racunID = " + racunID + " and rui.racunID = " + racunID + " and r.sifraPeriodikeIzvoda = spi.sifraPeriodikeIzvoda and rui.sifraFormataIzvoda = sfi.sifraFormataIzvoda and rui.sifraKanalaIzvoda = ski.sifraKanalaIzvoda and ci.sifraFormataIzvoda = rui.sifraFormataIzvoda and ci.sifraKanalaIzvoda = rui.sifraKanalaIzvoda");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				UgovoreniIzvod ugovoreniIzvod = new UgovoreniIzvod();
				ugovoreniIzvod.setRacunUgovoreniIzvodID(rs.getInt(1));
				ugovoreniIzvod.setIban(rs.getString(2));
				ugovoreniIzvod.setPeriodikaIzvoda(rs.getString(3));
				ugovoreniIzvod.setFormatIzvoda(rs.getString(4));
				ugovoreniIzvod.setKanalIzvoda(rs.getString(5));
				ugovoreniIzvod.setDatumVrijemeUgovaranja(rs.getString(6));
				ugovoreniIzvod.setCijena(rs.getFloat(7));
				listaUgovorenihIzvoda.add(ugovoreniIzvod);
			}
		}
		con.close();
		return listaUgovorenihIzvoda;
	}
	
	public static List<KreiraniIzvod> dohvatiKreiraneIzvode (List<Integer> listaRacunID) throws ClassNotFoundException, SQLException {
		List<KreiraniIzvod> listaKreiranihIzvoda = new ArrayList<KreiraniIzvod>();
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		for (int racunID : listaRacunID) {
			PreparedStatement ps = con.prepareStatement("select rki.kreiraniIzvodID, r.IBAN, spi.nazivPeriodike, rki.datumIzvoda, rki.brojIzvoda, rki.prethodnoStanje, rki.zavrsnoStanje from racunKreiraniIzvod rki, racun r, sifarnikPeriodikaIzvoda spi, racunUgovoreniIzvod rui where r.racunID = " + racunID + " and rui.racunID = " + racunID + " and rui.racunID = r.racunID and spi.sifraPeriodikeIzvoda = r.sifraPeriodikeIzvoda and rki.racunUgovoreniIzvodID = rui.racunUgovoreniIzvodID order by datumIzvoda");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				KreiraniIzvod ki = new KreiraniIzvod();
				ki.setKreiraniIzvodID(rs.getInt(1));
				ki.setIban(rs.getString(2));
				ki.setPeriodika(rs.getString(3));
				ki.setDatumIzvoda(rs.getString(4));
				ki.setBrojIzvoda(rs.getInt(5));
				ki.setPrethodnoStanje(rs.getFloat(6));
				ki.setZavrsnoStanje(rs.getFloat(7));
				listaKreiranihIzvoda.add(ki);
			}
		}
		for (KreiraniIzvod ki : listaKreiranihIzvoda) {
			PreparedStatement ps2 = con.prepareStatement("select datumVrijemePromjeneSalda, opis, uplata, isplata from kreiraniIzvodStavke where kreiraniIzvodID = " + ki.getKreiraniIzvodID() + " order by 1");
			ResultSet rs2 = ps2.executeQuery();
			while (rs2.next()) {
				StavkaIzvoda stavka = new StavkaIzvoda();
				stavka.setDatumVrijemePromjeneSalda(rs2.getString(1));
				stavka.setOpis(rs2.getString(2));
				stavka.setUplata(rs2.getFloat(3));
				stavka.setIsplata(rs2.getFloat(4));
				ki.stavkeIzvoda.add(stavka);
			}
		}
		con.close();
		return listaKreiranihIzvoda;
	}
	
	public static KreiraniIzvod dohvatiKreiraniIzvod (int kreiraniIzvodID) throws ClassNotFoundException, SQLException {
		KreiraniIzvod izvod = new KreiraniIzvod();
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		PreparedStatement ps = con.prepareStatement("select rki.kreiraniIzvodID, r.IBAN, spi.nazivPeriodike, rki.datumIzvoda, rki.brojIzvoda, rki.prethodnoStanje, rki.zavrsnoStanje from racunKreiraniIzvod rki, racun r, sifarnikPeriodikaIzvoda spi, racunUgovoreniIzvod rui where rki.kreiraniIzvodID = " + kreiraniIzvodID + "and rui.racunID = r.racunID and spi.sifraPeriodikeIzvoda = r.sifraPeriodikeIzvoda and rki.racunUgovoreniIzvodID = rui.racunUgovoreniIzvodID order by datumIzvoda");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			izvod.setKreiraniIzvodID(rs.getInt(1));
			izvod.setIban(rs.getString(2));
			izvod.setPeriodika(rs.getString(3));
			izvod.setDatumIzvoda(rs.getString(4));
			izvod.setBrojIzvoda(rs.getInt(5));
			izvod.setPrethodnoStanje(rs.getFloat(6));
			izvod.setZavrsnoStanje(rs.getFloat(7));
		}
		
		PreparedStatement ps2 = con.prepareStatement("select datumVrijemePromjeneSalda, opis, uplata, isplata from kreiraniIzvodStavke where kreiraniIzvodID = " + kreiraniIzvodID + " order by 1");
		ResultSet rs2 = ps2.executeQuery();
		while (rs2.next()) {
			StavkaIzvoda stavka = new StavkaIzvoda();
			stavka.setDatumVrijemePromjeneSalda(rs2.getString(1));
			stavka.setOpis(rs2.getString(2));
			stavka.setUplata(rs2.getFloat(3));
			stavka.setIsplata(rs2.getFloat(4));
			izvod.stavkeIzvoda.add(stavka);
		}
		con.close();
		return izvod;
	}
	
	//Budući da aplikacija nije povezana sa transakcijskim podacima, ova funkcija služi za generiranje prometa po računima
	public static void generirajPrometPoRacunu (List<Integer> listaRacunID) throws ClassNotFoundException, SQLException {
		//pronađi ugovorene izvode po kanalu Internet bankarstvo za svaki račun po kojem prijavljeni korisnik ima ovlaštenje
		Valuta valuta = new Valuta();
		valuta.setSifraValute("191");
		valuta.setValuta("HRK");
		List<PodaciZaGeneriranjeIzvoda> listaPodataka = new ArrayList<PodaciZaGeneriranjeIzvoda>(); 
		Class.forName(oracleDriver);
		Connection con = DriverManager.getConnection(url, user, pswd);
		for (int racunID : listaRacunID) {
			PreparedStatement ps = con.prepareStatement("select rui.racunUgovoreniIzvodID, spi.nazivPeriodike, rui.datumVrijemeUgovaranja, r.datumOtvaranja, ci.cijena from racunUgovoreniIzvod rui, sifarnikPeriodikaIzvoda spi, racun r, cjenikIzvoda ci, sifarnikKanalaIzvoda ski, sifarnikFormataIzvoda sfi where rui.racunID = " + racunID + " and r.racunID = " + racunID + " and rui.racunID = r.racunID and r.sifraPeriodikeIzvoda = spi.sifraPeriodikeIzvoda and ci.sifraFormataIzvoda = rui.sifraFormataIzvoda and ci.sifraKanalaIzvoda = rui.sifraKanalaIzvoda and ski.sifraKanalaIzvoda = 5 and ski.sifraKanalaIzvoda = ci.sifraKanalaIzvoda and sfi.sifraFormataIzvoda = ci.sifraFormataIzvoda");
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				PodaciZaGeneriranjeIzvoda podaci = new PodaciZaGeneriranjeIzvoda();
				podaci.setRacunUgovoreniIzvodID(rs.getInt(1));
				podaci.setPeriodikaIzvoda(rs.getString(2));
				podaci.setDatumVrijemeUgovaranja(rs.getString(3));
				podaci.setDatumOtvaranjaRacuna(rs.getString(4));
				podaci.setCijenaIzvoda(rs.getFloat(5));
				podaci.setRacunID(racunID);
				listaPodataka.add(podaci);
			}
		}
		//prolaz kroz sve elemente liste s podacima za generiranje izvoda
		for (int i=0; i<listaPodataka.size(); i++) {
			final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			LocalDateTime datumUgovaranjaIzvoda = LocalDateTime.parse(listaPodataka.get(i).getDatumVrijemeUgovaranja(), dateTimeFormatter);
			LocalDateTime datumOtvaranjaRacuna = LocalDateTime.parse(listaPodataka.get(i).getDatumOtvaranjaRacuna(), dateTimeFormatter);
			int brojDanaIzmedjuOtvaranjaRacunaUgovaranjaIzvoda = (int) ChronoUnit.DAYS.between(datumOtvaranjaRacuna.toLocalDate(), datumUgovaranjaIzvoda.toLocalDate());
			
			LocalDate danasnjiDatum = LocalDate.now();

			//izračunati razliku između broja dana od dana ugovaranja izvoda do danas
			int brojDanaIzmjedjuUgovaranjaIzvodaDanasnjegDana = (int) ChronoUnit.DAYS.between(datumUgovaranjaIzvoda.toLocalDate(), danasnjiDatum);
			PreparedStatement ps2 = con.prepareStatement("select * from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
			ResultSet rs2 = ps2.executeQuery();
			
			switch (listaPodataka.get(i).getPeriodikaIzvoda()) {
			//Dnevni izvodi se kreiraju svaki dan za prethodni dan
			case "dnevno": {
				if (rs2.next()) { //postoji neki kreirani izvod
					
					//dohvati redni broj izvoda, datum zadnjeg izvoda
					int redniBrojIzvoda = 0;
					String datumZadnjegIzvoda = "";
					PreparedStatement ps3 = con.prepareStatement("select brojIzvoda, datumIzvoda, zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
					ResultSet rs3 = ps3.executeQuery();
					while (rs3.next()) {
						redniBrojIzvoda = rs3.getInt(1);
						datumZadnjegIzvoda = rs3.getString(2);
					}
					LocalDateTime datumVrijemeZadnjegIzvoda = LocalDateTime.parse(datumZadnjegIzvoda, dateTimeFormatter);
					int brojDanaOdZadnjegKreiranogIzvoda = (int) ChronoUnit.DAYS.between(datumVrijemeZadnjegIzvoda.toLocalDate(), danasnjiDatum);
					
					for (int j = 2, d = brojDanaOdZadnjegKreiranogIzvoda - 1, brIzvoda = redniBrojIzvoda+1; j <= brojDanaOdZadnjegKreiranogIzvoda; j++, d--, brIzvoda++) {
						float zavrsnoStanjePrethodnogIzvoda = 0;
						PreparedStatement ps8 = con.prepareStatement("select zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
						ResultSet rs8 = ps8.executeQuery();
						while (rs8.next()) {
							zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(1);
						}
						
						KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
						kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
						LocalDateTime minusDays = LocalDateTime.now().minusDays(d);
						kreiraniIzvod.setDatumIzvoda(String.valueOf(minusDays));
						kreiraniIzvod.setBrojIzvoda(brIzvoda);
						kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
						
						kreiraniIzvod.valuteIzvoda.add(valuta);
														
						StavkaIzvoda stavka = new StavkaIzvoda();
						stavka.setDatumVrijemePromjeneSalda(minusDays.toString());
						stavka.setValutaStavke(valuta);
						stavka.setDatumValute(String.valueOf(minusDays));
						stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
						stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
						kreiraniIzvod.stavkeIzvoda.add(stavka);
						
						float promet = 0;
						for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
							promet+=item.getUplata();
							promet-=item.getIsplata();
						}
						kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
						
						//spremanje u bazu podataka
						String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
						String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
						String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
						
						PreparedStatement ps4 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
						ps4.executeUpdate();
						
						//dohvati kreiraniIzvodID
						int kreiraniIzvodID = 0;
						PreparedStatement ps5 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
						ResultSet rs5 = ps5.executeQuery();
						while (rs5.next()) {
							kreiraniIzvodID = rs5.getInt(1);
						}
						
						PreparedStatement ps6 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
						ps6.executeUpdate();
					
						String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
						String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
						String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
						
						String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
						String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
						String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
						
						PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
						ps7.executeUpdate();
					}
				}
				else { //kreiranje prvog izvoda i svih ostalih izvoda koji po ugovoru moraju biti prikazani s obzirom na trenutni datum
						for (int j = 1, d = brojDanaIzmjedjuUgovaranjaIzvodaDanasnjegDana; j <= brojDanaIzmjedjuUgovaranjaIzvodaDanasnjegDana; j++, d--) {
							if (j==1) { //prvi izvod
								KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
								kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
								kreiraniIzvod.setDatumIzvoda(String.valueOf(datumUgovaranjaIzvoda));
								kreiraniIzvod.setBrojIzvoda(j);
								kreiraniIzvod.setPrethodnoStanje(0);
								kreiraniIzvod.valuteIzvoda.add(valuta);
								
								StavkaIzvoda stavka1 = new StavkaIzvoda();
								if (brojDanaIzmedjuOtvaranjaRacunaUgovaranjaIzvoda == 0) { //račun otvoren i izvod ugovoren isti dan
									stavka1.setDatumVrijemePromjeneSalda(datumOtvaranjaRacuna.toString());
								}
								else {
									stavka1.setDatumVrijemePromjeneSalda(datumUgovaranjaIzvoda.toString());
								}
								stavka1.setDatumValute(String.valueOf(datumUgovaranjaIzvoda));
								stavka1.setValutaStavke(valuta);
								stavka1.setOpis("Inicijalna uplata kod otvaranja računa");
								stavka1.setUplata(10000);
								kreiraniIzvod.stavkeIzvoda.add(stavka1);
								
								StavkaIzvoda stavka2 = new StavkaIzvoda();
								stavka2.setDatumVrijemePromjeneSalda(datumUgovaranjaIzvoda.toString());
								stavka2.setValutaStavke(valuta);
								stavka2.setDatumValute(String.valueOf(datumUgovaranjaIzvoda));
								stavka2.setOpis("Trošak kreiranja izvoda o prometu računa");
								stavka2.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
								kreiraniIzvod.stavkeIzvoda.add(stavka2);
								
								float promet = 0;
								for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
									promet+=item.getUplata();
									promet-=item.getIsplata();
								}
								kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
								
								//spremanje u bazu podataka
								String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
								String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
								String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
								
								PreparedStatement ps3 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
								ps3.executeUpdate();
								
								//dohvati kreiraniIzvodID
								int kreiraniIzvodID = 0;
								PreparedStatement ps4 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
								ResultSet rs4 = ps4.executeQuery();
								while (rs4.next()) {
									kreiraniIzvodID = rs4.getInt(1);
								}
								
								PreparedStatement ps5 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
								ps5.executeUpdate();
								
								String datumVrijemePromjeneSalda_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
								String[] datumVrijemePromjeneSalda_stavka1_splitted = datumVrijemePromjeneSalda_stavka1.split("T", 2);
								String datumVrijemePromjeneSalda_stavka1_insert = datumVrijemePromjeneSalda_stavka1_splitted[0] + " " + datumVrijemePromjeneSalda_stavka1_splitted[1];
								
								String datumValute_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
								String[] datumValute_stavka1_splitted = datumValute_stavka1.split("T", 2);
								String datumValute_stavka1_insert = datumValute_stavka1_splitted[0] + " " + datumValute_stavka1_splitted[1];
								
								PreparedStatement ps6 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
								ps6.executeUpdate();
								
								String datumVrijemePromjeneSalda_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumVrijemePromjeneSalda();
								String[] datumVrijemePromjeneSalda_stavka2_splitted = datumVrijemePromjeneSalda_stavka2.split("T", 2);
								String datumVrijemePromjeneSalda_stavka2_insert = datumVrijemePromjeneSalda_stavka2_splitted[0] + " " + datumVrijemePromjeneSalda_stavka2_splitted[1];
								
								String datumValute_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumValute();
								String[] datumValute_stavka2_splitted = datumValute_stavka2.split("T", 2);
								String datumValute_stavka2_insert = datumValute_stavka2_splitted[0] + " " + datumValute_stavka2_splitted[1];
								
								PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(1).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(1).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(1).getIsplata() + ")");
								ps7.executeUpdate();
							}
							else { //ostali izvodi
								KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
								kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
								LocalDateTime minusDays = LocalDateTime.now().minusDays(d);
								kreiraniIzvod.setDatumIzvoda(String.valueOf(minusDays));
								kreiraniIzvod.setBrojIzvoda(j);
								
								////dohvati zavrsnoStanje prethodnog izvoda
								float zavrsnoStanjePrethodnogIzvoda = 0;
								PreparedStatement ps8 = con.prepareStatement("select zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
								ResultSet rs8 = ps8.executeQuery();
								while (rs8.next()) {
									zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(1);
								}
								kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
								
								kreiraniIzvod.valuteIzvoda.add(valuta);
																
								StavkaIzvoda stavka = new StavkaIzvoda();
								stavka.setDatumVrijemePromjeneSalda(minusDays.toString());
								stavka.setValutaStavke(valuta);
								stavka.setDatumValute(String.valueOf(minusDays));
								stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
								stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
								kreiraniIzvod.stavkeIzvoda.add(stavka);
								
								float promet = 0;
								for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
									promet+=item.getUplata();
									promet-=item.getIsplata();
								}
								kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
								
								//spremanje u bazu podataka
								String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
								String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
								String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
								
								PreparedStatement ps9 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
								ps9.executeUpdate();
								
								//dohvati kreiraniIzvodID
								int kreiraniIzvodID = 0;
								PreparedStatement ps10 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = "  + kreiraniIzvod.getRacunUgovoreniIzvodID());
								ResultSet rs10 = ps10.executeQuery();
								while (rs10.next()) {
									kreiraniIzvodID = rs10.getInt(1);
								}
								
								PreparedStatement ps11 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
								ps11.executeUpdate();
								
								String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
								String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
								String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
								
								String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
								String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
								String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
								
								PreparedStatement ps12 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
								ps12.executeUpdate();
							}
						}
				}
				break;
			}
			
			//Tjedni izvodi se kreiraju svaki ponedjeljak za prethodno sedmodnevno razdoblje
			case "tjedno": {
				if (rs2.next()) { //kreirani izvodi već postoje
					
					//dohvati redni broj izvoda, datum zadnjeg izvoda
					int redniBrojIzvoda = 0;
					String datumZadnjegIzvoda = "";
					PreparedStatement ps3 = con.prepareStatement("select brojIzvoda, datumIzvoda from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
					ResultSet rs3 = ps3.executeQuery();
					while (rs3.next()) {
						redniBrojIzvoda = rs3.getInt(1);
						datumZadnjegIzvoda = rs3.getString(2);
					}
					LocalDateTime datumVrijemeZadnjegIzvoda = LocalDateTime.parse(datumZadnjegIzvoda, dateTimeFormatter);
					int brojDanaOdZadnjegKreiranogIzvoda = (int) ChronoUnit.DAYS.between(datumVrijemeZadnjegIzvoda.toLocalDate(), danasnjiDatum);
					int brTjedana = brojDanaOdZadnjegKreiranogIzvoda / 8;
					
					for (int j = 1, w = 1, brIzvoda = redniBrojIzvoda+1; j <= brTjedana; j++, w++, brIzvoda++) {
						float zavrsnoStanjePrethodnogIzvoda = 0;
						PreparedStatement ps8 = con.prepareStatement("select zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
						ResultSet rs8 = ps8.executeQuery();
						while (rs8.next()) {
							zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(1);
						}
						KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
						kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
						LocalDateTime iducaNedjelja = datumVrijemeZadnjegIzvoda.plusWeeks(w);
						kreiraniIzvod.setDatumIzvoda(String.valueOf(iducaNedjelja));
						kreiraniIzvod.setBrojIzvoda(brIzvoda);
						kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
						
						kreiraniIzvod.valuteIzvoda.add(valuta);
														
						StavkaIzvoda stavka = new StavkaIzvoda();
						stavka.setDatumVrijemePromjeneSalda(iducaNedjelja.toString());
						stavka.setValutaStavke(valuta);
						stavka.setDatumValute(String.valueOf(iducaNedjelja));
						stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
						stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
						kreiraniIzvod.stavkeIzvoda.add(stavka);
						
						float promet = 0;
						for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
							promet+=item.getUplata();
							promet-=item.getIsplata();
						}
						kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
						
						//spremanje u bazu podataka
						String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
						String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
						String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
						
						PreparedStatement ps4 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
						ps4.executeUpdate();
						
						//dohvati kreiraniIzvodID
						int kreiraniIzvodID = 0;
						PreparedStatement ps5 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
						ResultSet rs5 = ps5.executeQuery();
						while (rs5.next()) {
							kreiraniIzvodID = rs5.getInt(1);
						}
						
						PreparedStatement ps6 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
						ps6.executeUpdate();
					
						String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
						String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
						String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
						
						String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
						String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
						String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
						
						PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
						ps7.executeUpdate();
					}
				}
				else { //prvi izvodi
					int brTjedana = brojDanaIzmjedjuUgovaranjaIzvodaDanasnjegDana / 8;
					for (int j = 1; j <= brTjedana; j++) {
						LocalDateTime prvaNedjelja = datumUgovaranjaIzvoda.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
						
						if (j == 1) {
							KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
							kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
							kreiraniIzvod.setDatumIzvoda(String.valueOf(prvaNedjelja));
							kreiraniIzvod.setBrojIzvoda(j);
							kreiraniIzvod.setPrethodnoStanje(0);
							kreiraniIzvod.valuteIzvoda.add(valuta);
							
							StavkaIzvoda stavka1 = new StavkaIzvoda();
							
							if (brojDanaIzmedjuOtvaranjaRacunaUgovaranjaIzvoda > 7) {
								stavka1.setDatumVrijemePromjeneSalda(prvaNedjelja.toString());
								stavka1.setDatumValute(String.valueOf(prvaNedjelja));
							}
							else {
								stavka1.setDatumVrijemePromjeneSalda(datumOtvaranjaRacuna.toString());
								stavka1.setDatumValute(String.valueOf(datumOtvaranjaRacuna));
							}
							stavka1.setValutaStavke(valuta);
							stavka1.setOpis("Inicijalna uplata kod otvaranja računa");
							stavka1.setUplata(10000);
							kreiraniIzvod.stavkeIzvoda.add(stavka1);
							
							StavkaIzvoda stavka2 = new StavkaIzvoda();
							stavka2.setDatumVrijemePromjeneSalda(prvaNedjelja.toString());
							stavka2.setValutaStavke(valuta);
							stavka2.setDatumValute(String.valueOf(prvaNedjelja));
							stavka2.setOpis("Trošak kreiranja izvoda o prometu računa");
							stavka2.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
							kreiraniIzvod.stavkeIzvoda.add(stavka2);
							
							float promet = 0;
							for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
								promet+=item.getUplata();
								promet-=item.getIsplata();
							}
							kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
							
							//spremanje u bazu podataka
							String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
							String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
							String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
							
							PreparedStatement ps3 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
							ps3.executeUpdate();
							
							//dohvati kreiraniIzvodID
							int kreiraniIzvodID = 0;
							PreparedStatement ps4 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs4 = ps4.executeQuery();
							while (rs4.next()) {
								kreiraniIzvodID = rs4.getInt(1);
							}
							
							PreparedStatement ps5 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
							ps5.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka1_splitted = datumVrijemePromjeneSalda_stavka1.split("T", 2);
							String datumVrijemePromjeneSalda_stavka1_insert = datumVrijemePromjeneSalda_stavka1_splitted[0] + " " + datumVrijemePromjeneSalda_stavka1_splitted[1];
							
							String datumValute_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
							String[] datumValute_stavka1_splitted = datumValute_stavka1.split("T", 2);
							String datumValute_stavka1_insert = datumValute_stavka1_splitted[0] + " " + datumValute_stavka1_splitted[1];
							
							PreparedStatement ps6 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
							ps6.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka2_splitted = datumVrijemePromjeneSalda_stavka2.split("T", 2);
							String datumVrijemePromjeneSalda_stavka2_insert = datumVrijemePromjeneSalda_stavka2_splitted[0] + " " + datumVrijemePromjeneSalda_stavka2_splitted[1];
							
							String datumValute_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumValute();
							String[] datumValute_stavka2_splitted = datumValute_stavka2.split("T", 2);
							String datumValute_stavka2_insert = datumValute_stavka2_splitted[0] + " " + datumValute_stavka2_splitted[1];
							
							PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(1).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(1).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(1).getIsplata() + ")");
							ps7.executeUpdate();
						}
						else {
							LocalDateTime iducaNedjelja = prvaNedjelja.plusWeeks(j-1);
							
							KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
							kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
							kreiraniIzvod.setDatumIzvoda(String.valueOf(iducaNedjelja));
							kreiraniIzvod.setBrojIzvoda(j);
							
							////dohvati zavrsnoStanje prethodnog izvoda
							float zavrsnoStanjePrethodnogIzvoda = 0;
							PreparedStatement ps8 = con.prepareStatement("select zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs8 = ps8.executeQuery();
							while (rs8.next()) {
								zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(1);
							}
							kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
							
							kreiraniIzvod.valuteIzvoda.add(valuta);
															
							StavkaIzvoda stavka = new StavkaIzvoda();
							stavka.setDatumVrijemePromjeneSalda(iducaNedjelja.toString());
							stavka.setValutaStavke(valuta);
							stavka.setDatumValute(String.valueOf(iducaNedjelja));
							stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
							stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
							kreiraniIzvod.stavkeIzvoda.add(stavka);
							
							float promet = 0;
							for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
								promet+=item.getUplata();
								promet-=item.getIsplata();
							}
							kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
							
							//spremanje u bazu podataka
							String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
							String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
							String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
							
							PreparedStatement ps9 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
							ps9.executeUpdate();
							
							//dohvati kreiraniIzvodID
							int kreiraniIzvodID = 0;
							PreparedStatement ps10 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs10 = ps10.executeQuery();
							while (rs10.next()) {
								kreiraniIzvodID = rs10.getInt(1);
							}
							
							PreparedStatement ps11 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
							ps11.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
							String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
							
							String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
							String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
							String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
							
							PreparedStatement ps12 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
							ps12.executeUpdate();
						}
					}
				}
				break;
			}
			
			//Polumjesečni izvodi se kreiraju svakog 1. i 16. mjesecu
			case "polumjesecno": {
				if (rs2.next()) { //kreirani izvodi već postoje
					
					//dohvati redni broj izvoda, datum zadnjeg izvoda
					int redniBrojIzvoda = 0;
					String datumZadnjegIzvoda = "";
					PreparedStatement ps3 = con.prepareStatement("select brojIzvoda, datumIzvoda from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
					ResultSet rs3 = ps3.executeQuery();
					while (rs3.next()) {
						redniBrojIzvoda = rs3.getInt(1);
						datumZadnjegIzvoda = rs3.getString(2);
					}
					LocalDateTime datumVrijemeZadnjegIzvoda = LocalDateTime.parse(datumZadnjegIzvoda, dateTimeFormatter);
					int brojDanaOdZadnjegKreiranogIzvoda = (int) ChronoUnit.DAYS.between(datumVrijemeZadnjegIzvoda.toLocalDate(), danasnjiDatum);
					int brPolumjesecja = brojDanaOdZadnjegKreiranogIzvoda / 16;
					
					for (int j = 1, brIzvoda = redniBrojIzvoda+1; j <= brPolumjesecja; j++, brIzvoda++) {
						//dohvati datum zadnjeg izvoda, završno stanje
						float zavrsnoStanjePrethodnogIzvoda = 0;
						PreparedStatement ps8 = con.prepareStatement("select datumIzvoda, zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
						ResultSet rs8 = ps8.executeQuery();
						while (rs8.next()) {
							datumZadnjegIzvoda = rs8.getString(1);
							zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(2);
						}
						datumVrijemeZadnjegIzvoda = LocalDateTime.parse(datumZadnjegIzvoda, dateTimeFormatter);
						int zadnjiIzvodDan = datumVrijemeZadnjegIzvoda.getDayOfMonth();
						LocalDateTime iduciDatum = null;
						if (zadnjiIzvodDan <= 15) { //od 1. do 15. u mjesecu --> 16.
							iduciDatum = datumVrijemeZadnjegIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusDays(15);
						}
						else { //od 16. do 31. u mjesecu --> 1.
							iduciDatum = datumVrijemeZadnjegIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1);
						}
						KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
						kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
						kreiraniIzvod.setDatumIzvoda(String.valueOf(iduciDatum));
						kreiraniIzvod.setBrojIzvoda(brIzvoda);
						kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
						
						kreiraniIzvod.valuteIzvoda.add(valuta);
														
						StavkaIzvoda stavka = new StavkaIzvoda();
						stavka.setDatumVrijemePromjeneSalda(iduciDatum.toString());
						stavka.setValutaStavke(valuta);
						stavka.setDatumValute(String.valueOf(iduciDatum));
						stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
						stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
						kreiraniIzvod.stavkeIzvoda.add(stavka);
						
						float promet = 0;
						for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
							promet+=item.getUplata();
							promet-=item.getIsplata();
						}
						kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
						
						//spremanje u bazu podataka
						String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
						String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
						String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
						
						PreparedStatement ps4 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
						ps4.executeUpdate();
						
						//dohvati kreiraniIzvodID
						int kreiraniIzvodID = 0;
						PreparedStatement ps5 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
						ResultSet rs5 = ps5.executeQuery();
						while (rs5.next()) {
							kreiraniIzvodID = rs5.getInt(1);
						}
						
						PreparedStatement ps6 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
						ps6.executeUpdate();
					
						String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
						String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
						String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
						
						String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
						String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
						String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
						
						PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
						ps7.executeUpdate();
					}
				}
				else { //prvi izvodi
					int brPolumjesecja = brojDanaIzmjedjuUgovaranjaIzvodaDanasnjegDana / 16;
					for (int j = 1; j <= brPolumjesecja; j++) {
						int dan = datumUgovaranjaIzvoda.getDayOfMonth();
						LocalDateTime prviDatum = null;
						if (dan <= 15) { //od 1. do 15. u mjesecu --> 16.
							prviDatum = datumUgovaranjaIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusDays(15);
						}
						else { //od 16. do 31. u mjesecu --> 1.
							prviDatum = datumUgovaranjaIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1);
						}
						
						if (j == 1) {
							KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
							kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
							kreiraniIzvod.setDatumIzvoda(String.valueOf(prviDatum));
							kreiraniIzvod.setBrojIzvoda(j);
							kreiraniIzvod.setPrethodnoStanje(0);
							kreiraniIzvod.valuteIzvoda.add(valuta);
							
							StavkaIzvoda stavka1 = new StavkaIzvoda();
							
							if (brojDanaIzmedjuOtvaranjaRacunaUgovaranjaIzvoda > 15) {
								stavka1.setDatumVrijemePromjeneSalda(prviDatum.toString());
								stavka1.setDatumValute(String.valueOf(prviDatum));
							}
							else {
								stavka1.setDatumVrijemePromjeneSalda(datumOtvaranjaRacuna.toString());
								stavka1.setDatumValute(String.valueOf(datumOtvaranjaRacuna));
							}
							stavka1.setValutaStavke(valuta);
							stavka1.setOpis("Inicijalna uplata kod otvaranja računa");
							stavka1.setUplata(10000);
							kreiraniIzvod.stavkeIzvoda.add(stavka1);
							
							StavkaIzvoda stavka2 = new StavkaIzvoda();
							stavka2.setDatumVrijemePromjeneSalda(prviDatum.toString());
							stavka2.setValutaStavke(valuta);
							stavka2.setDatumValute(String.valueOf(prviDatum));
							stavka2.setOpis("Trošak kreiranja izvoda o prometu računa");
							stavka2.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
							kreiraniIzvod.stavkeIzvoda.add(stavka2);
							
							float promet = 0;
							for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
								promet+=item.getUplata();
								promet-=item.getIsplata();
							}
							kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
							
							//spremanje u bazu podataka
							String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
							String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
							String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
							
							PreparedStatement ps3 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
							ps3.executeUpdate();
							
							//dohvati kreiraniIzvodID
							int kreiraniIzvodID = 0;
							PreparedStatement ps4 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs4 = ps4.executeQuery();
							while (rs4.next()) {
								kreiraniIzvodID = rs4.getInt(1);
							}
							
							PreparedStatement ps5 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
							ps5.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka1_splitted = datumVrijemePromjeneSalda_stavka1.split("T", 2);
							String datumVrijemePromjeneSalda_stavka1_insert = datumVrijemePromjeneSalda_stavka1_splitted[0] + " " + datumVrijemePromjeneSalda_stavka1_splitted[1];
							
							String datumValute_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
							String[] datumValute_stavka1_splitted = datumValute_stavka1.split("T", 2);
							String datumValute_stavka1_insert = datumValute_stavka1_splitted[0] + " " + datumValute_stavka1_splitted[1];
							
							PreparedStatement ps6 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
							ps6.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka2_splitted = datumVrijemePromjeneSalda_stavka2.split("T", 2);
							String datumVrijemePromjeneSalda_stavka2_insert = datumVrijemePromjeneSalda_stavka2_splitted[0] + " " + datumVrijemePromjeneSalda_stavka2_splitted[1];
							
							String datumValute_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumValute();
							String[] datumValute_stavka2_splitted = datumValute_stavka2.split("T", 2);
							String datumValute_stavka2_insert = datumValute_stavka2_splitted[0] + " " + datumValute_stavka2_splitted[1];
							
							PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(1).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(1).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(1).getIsplata() + ")");
							ps7.executeUpdate();
						}
						else {
							//dohvati datum zadnjeg izvoda, završno stanje
							String datumZadnjegIzvoda = "";
							float zavrsnoStanjePrethodnogIzvoda = 0;
							PreparedStatement ps3 = con.prepareStatement("select datumIzvoda, zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
							ResultSet rs3 = ps3.executeQuery();
							while (rs3.next()) {
								datumZadnjegIzvoda = rs3.getString(1);
								zavrsnoStanjePrethodnogIzvoda = rs3.getFloat(2);
							}
							LocalDateTime datumVrijemeZadnjegIzvoda = LocalDateTime.parse(datumZadnjegIzvoda, dateTimeFormatter);
							
							int zadnjiIzvodDan = datumVrijemeZadnjegIzvoda.getDayOfMonth();
							LocalDateTime iduciDatum = null;
							if (zadnjiIzvodDan <= 15) { //od 1. do 15. u mjesecu --> 16.
								iduciDatum = datumVrijemeZadnjegIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusDays(15);
							}
							else { //od 16. do 31. u mjesecu --> 1.
								iduciDatum = datumVrijemeZadnjegIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1);
							}
							
							KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
							kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
							kreiraniIzvod.setDatumIzvoda(String.valueOf(iduciDatum));
							kreiraniIzvod.setBrojIzvoda(j);
							kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);

							kreiraniIzvod.valuteIzvoda.add(valuta);
															
							StavkaIzvoda stavka = new StavkaIzvoda();
							stavka.setDatumVrijemePromjeneSalda(iduciDatum.toString());
							stavka.setValutaStavke(valuta);
							stavka.setDatumValute(String.valueOf(iduciDatum));
							stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
							stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
							kreiraniIzvod.stavkeIzvoda.add(stavka);
							
							float promet = 0;
							for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
								promet+=item.getUplata();
								promet-=item.getIsplata();
							}
							kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
							
							//spremanje u bazu podataka
							String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
							String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
							String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
							
							PreparedStatement ps9 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
							ps9.executeUpdate();
							
							//dohvati kreiraniIzvodID
							int kreiraniIzvodID = 0;
							PreparedStatement ps10 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs10 = ps10.executeQuery();
							while (rs10.next()) {
								kreiraniIzvodID = rs10.getInt(1);
							}
							
							PreparedStatement ps11 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
							ps11.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
							String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
							
							String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
							String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
							String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
							
							PreparedStatement ps12 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
							ps12.executeUpdate();
						}
						
					}
				}
				break;
			}
			
			//Mjesečni izvodi se kreiraju svakog 1. u mjesecu za prethodni mjesec
			case "mjesecno": {
				if (rs2.next()) { //kreirani izvodi već postoje
					
					//dohvati redni broj izvoda, datum zadnjeg izvoda
					int redniBrojIzvoda = 0;
					String datumZadnjegIzvoda = "";
					PreparedStatement ps3 = con.prepareStatement("select brojIzvoda, datumIzvoda from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
					ResultSet rs3 = ps3.executeQuery();
					while (rs3.next()) {
						redniBrojIzvoda = rs3.getInt(1);
						datumZadnjegIzvoda = rs3.getString(2);
					}
					LocalDateTime datumVrijemeZadnjegIzvoda = LocalDateTime.parse(datumZadnjegIzvoda, dateTimeFormatter);
					int brojDanaOdZadnjegKreiranogIzvoda = (int) ChronoUnit.DAYS.between(datumVrijemeZadnjegIzvoda.toLocalDate(), danasnjiDatum);
					int brMjeseci = brojDanaOdZadnjegKreiranogIzvoda / 32;
					
					for (int j = 1, m = 1, brIzvoda = redniBrojIzvoda+1; j <= brMjeseci; j++, m++, brIzvoda++) {
						float zavrsnoStanjePrethodnogIzvoda = 0;
						PreparedStatement ps8 = con.prepareStatement("select zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + listaPodataka.get(i).getRacunUgovoreniIzvodID());
						ResultSet rs8 = ps8.executeQuery();
						while (rs8.next()) {
							zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(1);
						}
						KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
						kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
						LocalDateTime iduciDatum = datumVrijemeZadnjegIzvoda.plusMonths(m);
						kreiraniIzvod.setDatumIzvoda(String.valueOf(iduciDatum));
						kreiraniIzvod.setBrojIzvoda(brIzvoda);
						kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
						
						kreiraniIzvod.valuteIzvoda.add(valuta);
														
						StavkaIzvoda stavka = new StavkaIzvoda();
						stavka.setDatumVrijemePromjeneSalda(iduciDatum.toString());
						stavka.setValutaStavke(valuta);
						stavka.setDatumValute(String.valueOf(iduciDatum));
						stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
						stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
						kreiraniIzvod.stavkeIzvoda.add(stavka);
						
						float promet = 0;
						for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
							promet+=item.getUplata();
							promet-=item.getIsplata();
						}
						kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
						
						//spremanje u bazu podataka
						String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
						String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
						String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
						
						PreparedStatement ps4 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
						ps4.executeUpdate();
						
						//dohvati kreiraniIzvodID
						int kreiraniIzvodID = 0;
						PreparedStatement ps5 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
						ResultSet rs5 = ps5.executeQuery();
						while (rs5.next()) {
							kreiraniIzvodID = rs5.getInt(1);
						}
						
						PreparedStatement ps6 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
						ps6.executeUpdate();
					
						String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
						String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
						String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
						
						String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
						String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
						String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
						
						PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
						ps7.executeUpdate();
					}
				}
				else { //prvi izvodi
					int brMjeseci = brojDanaIzmjedjuUgovaranjaIzvodaDanasnjegDana / 32;
					for (int j = 1; j <= brMjeseci; j++) {
						LocalDateTime prviDatum = datumUgovaranjaIzvoda.with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1);
						
						if (j == 1) {
							KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
							kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
							kreiraniIzvod.setDatumIzvoda(String.valueOf(prviDatum));
							kreiraniIzvod.setBrojIzvoda(j);
							kreiraniIzvod.setPrethodnoStanje(0);
							kreiraniIzvod.valuteIzvoda.add(valuta);
							
							StavkaIzvoda stavka1 = new StavkaIzvoda();
							
							if (brojDanaIzmedjuOtvaranjaRacunaUgovaranjaIzvoda > 31) {
								stavka1.setDatumVrijemePromjeneSalda(prviDatum.toString());
								stavka1.setDatumValute(String.valueOf(prviDatum));
							}
							else {
								stavka1.setDatumVrijemePromjeneSalda(datumOtvaranjaRacuna.toString());
								stavka1.setDatumValute(String.valueOf(datumOtvaranjaRacuna));
							}
							stavka1.setValutaStavke(valuta);
							stavka1.setOpis("Inicijalna uplata kod otvaranja računa");
							stavka1.setUplata(10000);
							kreiraniIzvod.stavkeIzvoda.add(stavka1);
							
							StavkaIzvoda stavka2 = new StavkaIzvoda();
							stavka2.setDatumVrijemePromjeneSalda(prviDatum.toString());
							stavka2.setValutaStavke(valuta);
							stavka2.setDatumValute(String.valueOf(prviDatum));
							stavka2.setOpis("Trošak kreiranja izvoda o prometu računa");
							stavka2.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
							kreiraniIzvod.stavkeIzvoda.add(stavka2);
							
							float promet = 0;
							for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
								promet+=item.getUplata();
								promet-=item.getIsplata();
							}
							kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
							
							//spremanje u bazu podataka
							String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
							String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
							String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
							
							PreparedStatement ps3 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
							ps3.executeUpdate();
							
							//dohvati kreiraniIzvodID
							int kreiraniIzvodID = 0;
							PreparedStatement ps4 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs4 = ps4.executeQuery();
							while (rs4.next()) {
								kreiraniIzvodID = rs4.getInt(1);
							}
							
							PreparedStatement ps5 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
							ps5.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka1_splitted = datumVrijemePromjeneSalda_stavka1.split("T", 2);
							String datumVrijemePromjeneSalda_stavka1_insert = datumVrijemePromjeneSalda_stavka1_splitted[0] + " " + datumVrijemePromjeneSalda_stavka1_splitted[1];
							
							String datumValute_stavka1 = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
							String[] datumValute_stavka1_splitted = datumValute_stavka1.split("T", 2);
							String datumValute_stavka1_insert = datumValute_stavka1_splitted[0] + " " + datumValute_stavka1_splitted[1];
							
							PreparedStatement ps6 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka1_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
							ps6.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka2_splitted = datumVrijemePromjeneSalda_stavka2.split("T", 2);
							String datumVrijemePromjeneSalda_stavka2_insert = datumVrijemePromjeneSalda_stavka2_splitted[0] + " " + datumVrijemePromjeneSalda_stavka2_splitted[1];
							
							String datumValute_stavka2 = kreiraniIzvod.stavkeIzvoda.get(1).getDatumValute();
							String[] datumValute_stavka2_splitted = datumValute_stavka2.split("T", 2);
							String datumValute_stavka2_insert = datumValute_stavka2_splitted[0] + " " + datumValute_stavka2_splitted[1];
							
							PreparedStatement ps7 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '191', to_date('" + datumValute_stavka2_insert + "', 'yyyy-MM-dd HH24:mi:ss'), '" + kreiraniIzvod.stavkeIzvoda.get(1).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(1).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(1).getIsplata() + ")");
							ps7.executeUpdate();
						}
						else {
							LocalDateTime iduciDatum = prviDatum.plusMonths(j-1);
							
							KreiraniIzvod kreiraniIzvod = new KreiraniIzvod();
							kreiraniIzvod.setRacunUgovoreniIzvodID(listaPodataka.get(i).getRacunUgovoreniIzvodID());
							kreiraniIzvod.setDatumIzvoda(String.valueOf(iduciDatum));
							kreiraniIzvod.setBrojIzvoda(j);
							
							////dohvati zavrsnoStanje prethodnog izvoda
							float zavrsnoStanjePrethodnogIzvoda = 0;
							PreparedStatement ps8 = con.prepareStatement("select zavrsnoStanje from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs8 = ps8.executeQuery();
							while (rs8.next()) {
								zavrsnoStanjePrethodnogIzvoda = rs8.getFloat(1);
							}
							kreiraniIzvod.setPrethodnoStanje(zavrsnoStanjePrethodnogIzvoda);
							
							kreiraniIzvod.valuteIzvoda.add(valuta);
															
							StavkaIzvoda stavka = new StavkaIzvoda();
							stavka.setDatumVrijemePromjeneSalda(iduciDatum.toString());
							stavka.setValutaStavke(valuta);
							stavka.setDatumValute(String.valueOf(iduciDatum));
							stavka.setOpis("Trošak kreiranja izvoda o prometu računa");
							stavka.setIsplata(listaPodataka.get(i).getCijenaIzvoda());
							kreiraniIzvod.stavkeIzvoda.add(stavka);
							
							float promet = 0;
							for (StavkaIzvoda item : kreiraniIzvod.stavkeIzvoda) {
								promet+=item.getUplata();
								promet-=item.getIsplata();
							}
							kreiraniIzvod.setZavrsnoStanje(kreiraniIzvod.getPrethodnoStanje() + promet);
							
							//spremanje u bazu podataka
							String datumIzvoda = kreiraniIzvod.getDatumIzvoda();
							String[] datumIzvoda_splitted = datumIzvoda.split("T", 2);
							String datumIzvoda_insert = datumIzvoda_splitted[0] + " " + "23:59:59";
							
							PreparedStatement ps9 = con.prepareStatement("insert into racunKreiraniIzvod values (racunKreiraniIzvod_SEQ.NEXTVAL, " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ", to_date('" + datumIzvoda_insert + "', 'yyyy-MM-dd HH24:mi:ss'), " + kreiraniIzvod.getBrojIzvoda() + ", " + kreiraniIzvod.getPrethodnoStanje() + ", " + kreiraniIzvod.getZavrsnoStanje() + ", SYSDATE)");
							ps9.executeUpdate();
							
							//dohvati kreiraniIzvodID
							int kreiraniIzvodID = 0;
							PreparedStatement ps10 = con.prepareStatement("select kreiraniIzvodID from racunKreiraniIzvod where datumIzvoda = (select max(datumIzvoda) from racunKreiraniIzvod where racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID() + ") and racunUgovoreniIzvodID = " + kreiraniIzvod.getRacunUgovoreniIzvodID());
							ResultSet rs10 = ps10.executeQuery();
							while (rs10.next()) {
								kreiraniIzvodID = rs10.getInt(1);
							}
							
							PreparedStatement ps11 = con.prepareStatement("insert into izvodValute values (" + kreiraniIzvodID + ", '191')");
							ps11.executeUpdate();
							
							String datumVrijemePromjeneSalda_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumVrijemePromjeneSalda();
							String[] datumVrijemePromjeneSalda_stavka_splitted = datumVrijemePromjeneSalda_stavka.split("T", 2);
							String datumVrijemePromjeneSalda_stavka_insert = datumVrijemePromjeneSalda_stavka_splitted[0] + " " + datumVrijemePromjeneSalda_stavka_splitted[1];
							
							String datumValute_stavka = kreiraniIzvod.stavkeIzvoda.get(0).getDatumValute();
							String[] datumValute_stavka_splitted = datumValute_stavka.split("T", 2);
							String datumValute_stavka_insert = datumValute_stavka_splitted[0] + " " + datumValute_stavka_splitted[1];
							
							PreparedStatement ps12 = con.prepareStatement("insert into kreiraniIzvodStavke values (" + kreiraniIzvodID + ", kreiraniIzvodStavke_SEQ.NEXTVAL, to_date('" + datumVrijemePromjeneSalda_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '191', to_date('" + datumValute_stavka_insert + "', 'yyyy-MM-dd HH24:mi:ss.   '), '" + kreiraniIzvod.stavkeIzvoda.get(0).getOpis() + "', " + kreiraniIzvod.stavkeIzvoda.get(0).getUplata() + ", " + kreiraniIzvod.stavkeIzvoda.get(0).getIsplata() + ")");
							ps12.executeUpdate();
						}
					}
				}
				break;
			}
		}
		}
	con.close();
	}
}