package diplomski.Monet;

import java.util.ArrayList;

public class KreiraniIzvod {
	private int racunUgovoreniIzvodID;
	private String datumIzvoda;
	private int brojIzvoda;
	private float prethodnoStanje;
	private float zavrsnoStanje;
	private String datumVrijemeKreiranja;
	
	public ArrayList<Valuta> valuteIzvoda;
	public ArrayList <StavkaIzvoda> stavkeIzvoda;
	
	public KreiraniIzvod () {
		valuteIzvoda = new ArrayList<>();
		stavkeIzvoda = new ArrayList<>();
	}

	public int getRacunUgovoreniIzvodID() {
		return racunUgovoreniIzvodID;
	}

	public void setRacunugovoreniIzvodID(int racunugovoreniIzvodID) {
		this.racunUgovoreniIzvodID = racunugovoreniIzvodID;
	}

	public String getDatumIzvoda() {
		return datumIzvoda;
	}

	public void setDatumIzvoda(String datumIzvoda) {
		this.datumIzvoda = datumIzvoda;
	}

	public int getBrojIzvoda() {
		return brojIzvoda;
	}

	public void setBrojIzvoda(int brojIzvoda) {
		this.brojIzvoda = brojIzvoda;
	}

	public float getPrethodnoStanje() {
		return prethodnoStanje;
	}

	public void setPrethodnoStanje(float prethodnoStanje) {
		this.prethodnoStanje = prethodnoStanje;
	}

	public float getZavrsnoStanje() {
		return zavrsnoStanje;
	}

	public void setZavrsnoStanje(float zavrsnoStanje) {
		this.zavrsnoStanje = zavrsnoStanje;
	}

	public String getDatumVrijemeKreiranja() {
		return datumVrijemeKreiranja;
	}

	public void setDatumVrijemeKreiranja(String datumVrijemeKreiranja) {
		this.datumVrijemeKreiranja = datumVrijemeKreiranja;
	}
	
	
}
