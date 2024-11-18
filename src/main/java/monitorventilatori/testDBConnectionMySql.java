package monitorventilatori;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Classe di test per la connessione al database
public class testDBConnectionMySql {

	public static void main(String[] args) {
		String url = "jdbc:mysql://10.1.10.152:3306/babysafe";
		String username = "root";
		String password = "Op0p9oll.19";

		try (Connection conn = DriverManager.getConnection(url, username, password)) {
			System.out.println("Connessione a MySQL avvenuta con successo!");
		} catch (SQLException e) {
			System.err.println("Errore di connessione: " + e.getMessage());
		}
	}
}
