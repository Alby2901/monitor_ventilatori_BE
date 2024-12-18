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
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;

@WebServlet("/statoventilatori")
public class StrumentiLactaServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private static final Logger logger = Logger.getLogger(StrumentiLactaServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Imposta il tipo di contenuto come JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        ConfigLoader configLoader = new ConfigLoader(getServletContext());
        String dbServer;
        
        
        // Ottieni il parametro "db" dall'URL
        String db = request.getParameter("db");
        if (db == null || db.isEmpty()) {
        	
        	dbServer = configLoader.getProperty("dbServer").toLowerCase();
        	
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            out.print("{\"error\":\"Parametro 'db' mancante\"}");
//            return;
        } else {
        	
        	dbServer = db.toLowerCase();
        }
        
        logger.log(Level.INFO, " ==>> Database Server selezionato: " + dbServer);
        // System.out.println("Database Server selezionato: " + dbServer);
        
        String viewProp = dbServer + ".view";
        logger.log(Level.INFO, " ==>> View property: " + viewProp);
        // System.out.println("View proporties: " + viewProp);
        
        String view = configLoader.getProperty(viewProp);
        logger.log(Level.INFO, " ==>> Vista utilizzata: " + view);
        // System.out.println(" ==>> vista utilizzata: " + view);
        
        
        String query = "SELECT MODELLO, STRUMENTO, MIN_DATA_INIZIO, MAX_DATA_INIZIO FROM " + view;
        logger.log(Level.INFO, " ==>> Query completa utilizzata: " + query);
        // System.out.println(" ==>> Query completa utilizzata: " + query);
        
        
        // Logica per connettersi al database (MySQL o Oracle)
        List<HashMap<String, Object>> dati = new ArrayList<>();
        logger.log(Level.INFO, " ==>> Inizio della connessione alla API...");
        // System.out.println(" ==>> Inizio della connessione alla API... ");

        try (Connection conn = DatabaseConnection.createConnection(getServletContext(), dbServer);
                
            Statement stmt = conn.createStatement();
        	
            ResultSet rs = stmt.executeQuery(query)) {
            
        	logger.log(Level.INFO, " ==>> Query Eseguita!");
            //System.out.println(" ==>> Query Eseguita! ");

//        	Timestamp timestamp;
        	
            while (rs.next()) {
                HashMap<String, Object> record = new HashMap<>();
//                timestamp = rs.getTimestamp("MIN_DATA_INIZIO");
//                System.out.println("Data completa: " + timestamp);
//                System.out.println("Data: " + timestamp.toLocalDateTime().toLocalDate());
//                System.out.println("Ora: " + timestamp.toLocalDateTime().toLocalTime());
                record.put("modello", rs.getString("MODELLO"));
                record.put("strumento", rs.getString("STRUMENTO"));
                record.put("min_data_iniz", rs.getTimestamp("MIN_DATA_INIZIO"));
                record.put("max_data_iniz", rs.getTimestamp("MAX_DATA_INIZIO"));
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
