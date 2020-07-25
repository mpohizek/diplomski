package diplomski.Monet;

public class StavkaIzvoda {
	private String datumVrijemePromjeneSalda;
	private Valuta valutaStavke;
	private String datumValute;
	private String opis;
	private float uplata;
	private float isplata;
	
	public String getDatumVrijemePromjeneSalda() {
		return datumVrijemePromjeneSalda;
	}
	public void setDatumVrijemePromjeneSalda(String datumVrijemePromjeneSalda) {
		this.datumVrijemePromjeneSalda = datumVrijemePromjeneSalda;
	}
	public Valuta getValutaStavke() {
		return valutaStavke;
	}
	public void setValutaStavke(Valuta valutaStavke) {
		this.valutaStavke = valutaStavke;
	}
	public String getDatumValute() {
		return datumValute;
	}
	public void setDatumValute(String datumValute) {
		this.datumValute = datumValute;
	}
	public String getOpis() {
		return opis;
	}
	public void setOpis(String opis) {
		this.opis = opis;
	}
	public float getUplata() {
		return uplata;
	}
	public void setUplata(float uplata) {
		this.uplata = uplata;
	}
	public float getIsplata() {
		return isplata;
	}
	public void setIsplata(float isplata) {
		this.isplata = isplata;
	}
	
}
