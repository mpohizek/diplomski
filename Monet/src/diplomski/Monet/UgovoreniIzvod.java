package diplomski.Monet;

public class UgovoreniIzvod {
	private int racunUgovoreniIzvodID;
	private String iban;
	private String periodikaIzvoda;
	private String formatIzvoda;
	private String kanalIzvoda;
	private String datumVrijemeUgovaranja;
	private float cijena;
	
	public int getRacunUgovoreniIzvodID() {
		return racunUgovoreniIzvodID;
	}
	public void setRacunUgovoreniIzvodID(int racunUgovoreniIzvodID) {
		this.racunUgovoreniIzvodID = racunUgovoreniIzvodID;
	}
	public String getIban() {
		return iban;
	}
	public void setIban(String iban) {
		this.iban = iban;
	}
	public String getFormatIzvoda() {
		return formatIzvoda;
	}
	public void setFormatIzvoda(String formatIzvoda) {
		this.formatIzvoda = formatIzvoda;
	}
	public String getKanalIzvoda() {
		return kanalIzvoda;
	}
	public void setKanalIzvoda(String kanalIzvoda) {
		this.kanalIzvoda = kanalIzvoda;
	}
	public String getDatumVrijemeUgovaranja() {
		return datumVrijemeUgovaranja;
	}
	public void setDatumVrijemeUgovaranja(String datumVrijemeUgovaranja) {
		this.datumVrijemeUgovaranja = datumVrijemeUgovaranja;
	}
	public float getCijena() {
		return cijena;
	}
	public void setCijena(float cijena) {
		this.cijena = cijena;
	}
	public String getPeriodikaIzvoda() {
		return periodikaIzvoda;
	}
	public void setPeriodikaIzvoda(String periodikaIzvoda) {
		this.periodikaIzvoda = periodikaIzvoda;
	}
}
