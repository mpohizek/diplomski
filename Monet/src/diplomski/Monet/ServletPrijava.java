package diplomski.Monet;

import java.io.IOException;
import java.sql.SQLException;

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
			if (prijavljeniKorisnik.getKorisnickoIme() != null) {
				// TODO PROVJERA IMA LI KOJA AKTIVNA USLUGA
				RequestDispatcher dispatcher = request.getRequestDispatcher("PocetnaStranica.jsp");
				dispatcher.forward(request, response);
			}
			else {
				RequestDispatcher dispatcher = request.getRequestDispatcher("NeispravnaPrijava.jsp");
				dispatcher.forward(request, response);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
