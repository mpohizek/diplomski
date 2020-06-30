package diplomski.Monet;

public class UslugaInternetBankarstvo {
	private int uslugaID;
	private int klijentID;
	private int racunID;
	private String iban;
	
	public int getUslugaID() {
		return uslugaID;
	}
	public void setUslugaID(int uslugaID) {
		this.uslugaID = uslugaID;
	}
	public int getKlijentID() {
		return klijentID;
	}
	public void setKlijentID(int klijentID) {
		this.klijentID = klijentID;
	}
	public int getRacunID() {
		return racunID;
	}
	public void setRacunID(int racunID) {
		this.racunID = racunID;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
}
