package Main;

import java.security.MessageDigest;
 
/**
 * Constructeur par défaut.
 * 
 * Ce constructeur crée une nouvelle instance de la classe <code>HashingSHA256</code>.
 * Il est utilisé pour initialiser l'objet sans paramètres. Il peut être appelé pour
 * instancier l'objet avant d'utiliser ses méthodes, comme <code>calculateHash()</code>,
 * pour calculer un hash SHA-256 à partir des données fournies.
 * 
 * Ce constructeur est généralement utilisé lorsqu'aucune donnée ou configuration spécifique
 * n'est nécessaire au moment de l'instanciation de l'objet.
 * 
 * @author Aslan
 * @version 1.0
 */
public class HashingSHA256 {

	/**
	 * Calcule le hash d'un tableau de données en utilisant un algorithme de hachage.
	 * 
	 * Cette méthode génère un hash (par exemple, SHA-256) des données fournies sous forme
	 * de tableau de bytes et retourne le résultat sous forme de chaîne de caractères.
	 * 
	 * @param data les données à hasher.
	 * @return une chaîne de caractères représentant le hash des données.
	 * @throws Exception si une erreur survient lors du calcul du hash.
	 */
    public String calculateHash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
