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
 * Servlet implementation class ServletPotvrdaUgovaranjaIzvoda
 */
@WebServlet("/ServletPotvrdaUgovaranjaIzvoda")
public class ServletPotvrdaUgovaranjaIzvoda extends HttpServlet {
	private static final long serialVersionUID = 1L;
	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			OracleDBConnection.unesiUgovoreniIzvod(request.getParameter("odabraniRacun"), request.getParameter("odabraniFormat"), request.getParameter("odabraniKanal"));
			String iban = request.getParameter("iban");
			int brojRacuna = iban.length() / 21;
			List<Integer> listaRacunID = new ArrayList<Integer>();
			List<String> listaIBAN = new ArrayList<String>();
			for (int j=1, i=0; j<=brojRacuna; j++, i+=21) {
				listaIBAN.add(iban.substring(i, i+21));
			}
			for (String item : listaIBAN) {
				listaRacunID.add(OracleDBConnection.dohvatiRacunID(item));
			}
			List<UgovoreniIzvod> listaUgovorenihIzvoda = OracleDBConnection.dohvatiPodatkeUgovoreniIzvodi(listaRacunID);
			getServletContext().setAttribute("listaUgovorenihIzvoda", listaUgovorenihIzvoda);
			RequestDispatcher dispatcher = request.getRequestDispatcher("PocetnaStranica.jsp");
			dispatcher.forward(request, response);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
