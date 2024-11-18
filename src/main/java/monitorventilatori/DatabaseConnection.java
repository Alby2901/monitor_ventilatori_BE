package monitorventilatori;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

public class DatabaseConnection {

    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());

    public static Connection createConnection(ServletContext context) throws IOException {
        Properties props = new Properties();

        // Usa ServletContext per caricare il file da WEB-INF
        try (InputStream input = context.getResourceAsStream("/WEB-INF/config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("File 'config.properties' non trovato in /WEB-INF.");
            }

            // Carica le proprietà dal file
            props.load(input);

            String dbServer = props.getProperty("dbServer");
            logger.info("Database Server selezionato: " + dbServer);

            String url, username, password;

            // Seleziona i parametri in base al tipo di database
            if ("MySQL".equalsIgnoreCase(dbServer)) {
                url = props.getProperty("mysql.url");
                username = props.getProperty("mysql.username");
                password = props.getProperty("mysql.password");
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if ("ORACLE".equalsIgnoreCase(dbServer)) {
                url = props.getProperty("oracle.url");
                username = props.getProperty("oracle.username");
                password = props.getProperty("oracle.password");
                Class.forName("oracle.jdbc.driver.OracleDriver");
            } else if ("MariaDB".equalsIgnoreCase(dbServer)) {
                url = props.getProperty("mariadb.url");
                username = props.getProperty("mariadb.username");
                password = props.getProperty("mariadb.password");
                Class.forName("org.mariadb.jdbc.Driver");
            } else {
                throw new IllegalArgumentException("Tipo di database non supportato: " + dbServer);
            }

            
            try {
                Class.forName("org.mariadb.jdbc.Driver");
                logger.info("Driver Mariadb caricato correttamente.");
            } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "Driver Mariadb non trovato.", e);
                throw new RuntimeException("Driver Mariadb non trovato.", e);
            }

            
            // Prova a creare una connessione
            try {
                logger.info("Tentativo di connessione al database: " + url);
                return DriverManager.getConnection(url, username, password);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Errore durante la connessione al database: " + e.getMessage(), e);
                // throw e; => modifica fatta localmente anzichè sul
                throw new RuntimeException("Impossibile connettersi al database", e); // Eccezione non controllata
            }

        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Driver JDBC non trovato: " + e.getMessage(), e);
            throw new RuntimeException("Driver JDBC non trovato: " + e.getMessage(), e);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Errore durante il caricamento delle configurazioni: " + e.getMessage(), e);
            throw e;
            
        }
    }
}
