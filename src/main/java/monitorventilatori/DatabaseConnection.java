package monitorventilatori;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;


public class DatabaseConnection {

    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    public static Connection createConnection(ServletContext context, String dbServerP) throws IOException {
        
    	ConfigLoader configLoader = new ConfigLoader(context);
        
        String dbServer = dbServerP;
        logger.info("Database Server selezionato: " + dbServer);

        String url, username, password;

        try {
	        // Seleziona i parametri in base al tipo di database
	        if ("MySQL".equalsIgnoreCase(dbServer)) {
	            url = configLoader.getProperty("mysql.url");
	            username = configLoader.getProperty("mysql.username");
	            password = configLoader.getProperty("mysql.password");
	            Class.forName("com.mysql.cj.jdbc.Driver");
	        } else if ("ORACLE".equalsIgnoreCase(dbServer)) {
	            url = configLoader.getProperty("oracle.url");
	            username = configLoader.getProperty("oracle.username");
	            password = configLoader.getProperty("oracle.password");
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	        } else if ("MariaDB".equalsIgnoreCase(dbServer)) {
	            url = configLoader.getProperty("mariadb.url");
	            username = configLoader.getProperty("mariadb.username");
	            password = configLoader.getProperty("mariadb.password");
	            Class.forName("org.mariadb.jdbc.Driver");
	        } else {
	            throw new IllegalArgumentException("Tipo di database non supportato: " + dbServer);
	        }

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Driver JDBC non trovato: " + e.getMessage(), e);
            throw new RuntimeException("Driver JDBC non trovato: " + e.getMessage(), e);
        }
	        
	        
        try {
            logger.info("Tentativo di connessione al database: " + url);
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Errore durante la connessione al database: " + e.getMessage(), e);
            throw new RuntimeException("Impossibile connettersi al database", e);
        }
    }
}




