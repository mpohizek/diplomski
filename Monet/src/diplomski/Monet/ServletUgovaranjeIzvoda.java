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
 * Servlet implementation class ServletUgovaranjeIzvoda
 */
@WebServlet("/ServletUgovaranjeIzvoda")
public class ServletUgovaranjeIzvoda extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			float cijena = OracleDBConnection.dohvatiCijenuIzvoda(request.getParameter("formatIzvoda"), request.getParameter("kanalIzvoda"));
			boolean postojiUgovoreniIzvod = OracleDBConnection.dohvatiUgovoreniIzvod(request.getParameter("racun"), request.getParameter("formatIzvoda"), request.getParameter("kanalIzvoda"));
			if (postojiUgovoreniIzvod) {
				getServletContext().setAttribute("odabraniRacun", request.getParameter("racun"));
				getServletContext().setAttribute("odabraniFormat", request.getParameter("formatIzvoda"));
				getServletContext().setAttribute("odabraniKanal", request.getParameter("kanalIzvoda"));
				RequestDispatcher dispatcher = request.getRequestDispatcher("ObavijestDupliciranjeIzvoda.jsp");
				dispatcher.forward(request, response);
			}
			else {
				if (cijena > 0) {
					getServletContext().setAttribute("odabraniRacun", request.getParameter("racun"));
					getServletContext().setAttribute("odabraniFormat", request.getParameter("formatIzvoda"));
					getServletContext().setAttribute("odabraniKanal", request.getParameter("kanalIzvoda"));
					getServletContext().setAttribute("cijena", cijena);
					RequestDispatcher dispatcher = request.getRequestDispatcher("PotvrdaUgovaranjaIzvoda.jsp");
					dispatcher.forward(request, response);
				}
				else {
					RequestDispatcher dispatcher = request.getRequestDispatcher("NeispravnoPopunjenZahtjevZaUgovaranjemIzvoda.jsp");
					dispatcher.forward(request, response);
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
