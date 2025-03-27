package Main;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConnexion {

	public static Connection connect() {
		Connection conn = null;
		try {
			// Chemin vers votre base de données SQLite
			conn = DriverManager.getConnection("jdbc:sqlite:database.db");
			System.out.println("Connexion à SQLite réussie ✅");
		} catch (SQLException e) {
			System.out.println("Erreur lors de la connexion : " + e.getMessage());
		}
		return conn;
	}
}
