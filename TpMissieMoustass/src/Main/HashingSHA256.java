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
     * Calcule le hash d'un tableau de données en utilisant l'algorithme SHA-256.
     * 
     * Cette méthode génère un hash des données fournies sous forme
     * de tableau de bytes et retourne le résultat sous forme de chaîne de caractères
     * en hexadécimal.
     * 
     * @param data les données à hasher.
     * @return une chaîne de caractères représentant le hash des données.
     * @throws Exception si une erreur survient lors du calcul du hash.
     */
    public String calculateHash(byte[] data) throws Exception {
        // Initialisation de l'algorithme de hachage SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // Calcul du hash des données
        byte[] hash = digest.digest(data);

        // Conversion du résultat en format hexadécimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            // Chaque byte est converti en deux chiffres hexadécimaux
            hexString.append(String.format("%02x", b));
        }

        // Retour du hash sous forme de chaîne hexadécimale
        return hexString.toString();
    }

    /**
     * Vérifie l'intégrité des données en comparant deux tableaux de données.
     * 
     * Cette méthode calcule le hash des données d'origine et celui des nouvelles données,
     * puis compare les deux pour vérifier si les données ont été modifiées ou non.
     * 
     * @param originalData les données d'origine.
     * @param newData les nouvelles données à comparer.
     * @return true si les données sont intactes, false si elles ont été modifiées.
     * @throws Exception si une erreur survient lors du calcul du hash.
     */
    public boolean verifyDataIntegrity(byte[] originalData, byte[] newData) throws Exception {
        // Calcul des hashs pour les deux ensembles de données
        String originalHash = calculateHash(originalData);
        String newHash = calculateHash(newData);

        // Comparaison des hashs
        return originalHash.equals(newHash);
    }
}
