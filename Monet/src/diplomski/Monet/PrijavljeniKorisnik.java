package diplomski.Monet;

import java.util.ArrayList;

public class PrijavljeniKorisnik {
	private String ime;
	private String prezime;
	private String korisnickoIme;
	private String lozinka;
	private String oib;
	private int klijentID;
	private int klijentOsobaID;
	private int osobaID;
	
	public ArrayList<UslugaInternetBankarstvo> aktivneUsluge;
	
	public PrijavljeniKorisnik() {
		aktivneUsluge = new ArrayList<>();
	}
	
	public String getIme() {
		return ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getPrezime() {
		return prezime;
	}
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}
	public String getKorisnickoIme() {
		return korisnickoIme;
	}
	public void setKorisnickoIme(String korisnickoIme) {
		this.korisnickoIme = korisnickoIme;
	}
	public String getLozinka() {
		return lozinka;
	}
	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}
	public String getOib() {
		return oib;
	}
	public void setOib(String oib) {
		this.oib = oib;
	}
	public int getKlijentID() {
		return klijentID;
	}
	public void setKlijentID(int klijentId) {
		this.klijentID = klijentId;
	}

	public int getKlijentOsobaID() {
		return klijentOsobaID;
	}

	public void setKlijentOsobaID(int klijentOsobaId) {
		this.klijentOsobaID = klijentOsobaId;
	}

	public int getOsobaID() {
		return osobaID;
	}

	public void setOsobaID(int osobaID) {
		this.osobaID = osobaID;
	}
}
