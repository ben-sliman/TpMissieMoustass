package Main;
 
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * La classe <code>DataBaseConnexion</code> gère la connexion à la base de données.
 * Elle fournit des méthodes pour établir et fermer une connexion à la base de données.
 * Cette classe peut être utilisée pour effectuer des opérations de base de données telles que
 * les requêtes SQL, l'insertion, la mise à jour, et la suppression de données.
 * 
 * 
 * @author Aslan
 * @version 1.0
 */
public class DataBaseConnexion {

	 /**
	  * Établit une connexion à la base de données.
	  * 
	  * Cette méthode crée et retourne un objet <code>Connection</code> qui permet
	  * de communiquer avec la base de données. Elle gère les exceptions possibles
	  * lors de la connexion, telles que les erreurs de configuration de la base de données.
	  * 
	  * @return une instance de <code>Connection</code> si la connexion est réussie.
	  */
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
