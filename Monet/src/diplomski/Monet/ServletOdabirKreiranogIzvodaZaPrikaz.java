package diplomski.Monet;

import java.awt.List;
import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class ServletOdabirKreiranogIzvodaZaPrikaz
 */
@WebServlet("/ServletOdabirKreiranogIzvodaZaPrikaz")
public class ServletOdabirKreiranogIzvodaZaPrikaz extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int parametar = Integer.parseInt(request.getParameter("izvod"));
		try {
			KreiraniIzvod kreiraniIzvod = OracleDBConnection.dohvatiKreiraniIzvod(parametar);
			getServletContext().setAttribute("kreiraniIzvod", kreiraniIzvod);
			getServletContext().setAttribute("stavkeIzvoda", kreiraniIzvod.stavkeIzvoda);
			RequestDispatcher dispatcher = request.getRequestDispatcher("PrikazKreiranogIzvoda.jsp");
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
