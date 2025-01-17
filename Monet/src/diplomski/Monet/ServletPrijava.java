package diplomski.Monet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletPrijava
 */
@WebServlet("/ServletPrijava")
public class ServletPrijava extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @throws IOException 
	 * @throws ServletException 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			PrijavljeniKorisnik prijavljeniKorisnik = OracleDBConnection.AutentikacijaKorisnika(request.getParameter("korisnickoIme"), request.getParameter("lozinka"));
			if (prijavljeniKorisnik.getKorisnickoIme() != null && prijavljeniKorisnik.aktivneUsluge.size() > 0) {
				getServletContext().setAttribute("prijavljeniKorisnik", prijavljeniKorisnik);
				getServletContext().setAttribute("aktivneUsluge", prijavljeniKorisnik.aktivneUsluge);
				String iban = "";
				List<Integer> listaRacunID = new ArrayList<>();
				for (int i = 0; i < prijavljeniKorisnik.aktivneUsluge.size(); i++) {
					iban += prijavljeniKorisnik.aktivneUsluge.get(i).getIban();
					listaRacunID.add(prijavljeniKorisnik.aktivneUsluge.get(i).getRacunID());
				}
				getServletContext().setAttribute("iban", iban);
				getServletContext().setAttribute("listaRacunID", listaRacunID);
				
				//generiranje prometa po računima
				OracleDBConnection.generirajPrometPoRacunu(listaRacunID);
				
				List<KreiraniIzvod> listaKreiranihIzvoda = OracleDBConnection.dohvatiKreiraneIzvode(listaRacunID);
				getServletContext().setAttribute("listaKreiranihIzvoda", listaKreiranihIzvoda);
				
				String kreiraniIzvodiSelect = "";
				for (int i = 0; i < listaKreiranihIzvoda.size(); i++) {
					if (i == listaKreiranihIzvoda.size()-1) {
						kreiraniIzvodiSelect += "IBAN: " + listaKreiranihIzvoda.get(i).getIban() + ", redni broj izvoda: " + listaKreiranihIzvoda.get(i).getBrojIzvoda() + ", datum izvoda: " + listaKreiranihIzvoda.get(i).getDatumIzvoda() + "ID" + listaKreiranihIzvoda.get(i).getKreiraniIzvodID();
					}
					else {
						kreiraniIzvodiSelect += "IBAN: " + listaKreiranihIzvoda.get(i).getIban() + ", redni broj izvoda: " + listaKreiranihIzvoda.get(i).getBrojIzvoda() + ", datum izvoda: " + listaKreiranihIzvoda.get(i).getDatumIzvoda() + "ID" + listaKreiranihIzvoda.get(i).getKreiraniIzvodID() + "**";
					}
				}
				getServletContext().setAttribute("kreiraniIzvodiSelect", kreiraniIzvodiSelect);
				
				List<UgovoreniIzvod> listaUgovorenihIzvoda = OracleDBConnection.dohvatiPodatkeUgovoreniIzvodi(listaRacunID);
				getServletContext().setAttribute("listaUgovorenihIzvoda", listaUgovorenihIzvoda);
				Klijent klijent = OracleDBConnection.dohvatiKlijenta(prijavljeniKorisnik.getKlijentID());
				getServletContext().setAttribute("klijent", klijent);
				RequestDispatcher dispatcher = request.getRequestDispatcher("PocetnaStranica.jsp");
				dispatcher.forward(request, response);
			}
			else {
				RequestDispatcher dispatcher = request.getRequestDispatcher("NeispravnaPrijava.jsp");
				dispatcher.forward(request, response);;
			}
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
