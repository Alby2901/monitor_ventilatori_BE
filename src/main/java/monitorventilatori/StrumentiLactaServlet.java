package monitorventilatori;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.google.gson.Gson;

@WebServlet("/statoventilatori")
public class StrumentiLactaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// Imposta il tipo di contenuto come JSON
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();

		// Logica per connettersi al database (MySQL o Oracle)
		List<HashMap<String, Object>> dati = new ArrayList<>();

		System.out.println(" ==>> Inizio della connessione alla API... ");

		try (Connection conn = DatabaseConnection.createConnection(getServletContext());
				
			Statement stmt = conn.createStatement();

			ResultSet rs = stmt.executeQuery("SELECT MODELLO, STRUMENTO, MIN_DATA_INIZIO, MAX_DATA_INIZIO FROM STRUMENTI_DATI_VV_LACTA")) {
			
			System.out.println(" ==>> Query Eseguita! ");

			while (rs.next()) {
				HashMap<String, Object> record = new HashMap<>();
				record.put("modello", rs.getString("MODELLO"));
				record.put("strumento", rs.getString("STRUMENTO"));
				record.put("min_data_iniz", rs.getDate("MIN_DATA_INIZIO"));
				record.put("max_data_iniz", rs.getDate("MAX_DATA_INIZIO"));
				dati.add(record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			out.print("{\"error\":\"Database error\"}");
			return;
		}

		// Converte i dati in JSON e li invia come risposta
		Gson gson = new Gson();
		out.print(gson.toJson(dati));
	}

	@Override
	public void destroy() {
		// Rilascia le risorse, se necessario
	}

}
