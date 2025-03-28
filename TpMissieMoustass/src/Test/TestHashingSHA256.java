package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.HashingSHA256; // Import de la classe HashingSHA256 depuis le package Main

/**
 * Classe de test pour la méthode calculateHash dans HashingSHA256.
 * Ce constructeur par défaut est utilisé par la JVM pour instancier la classe.
 */
public class TestHashingSHA256 {

    /**
     * Teste le hash SHA-256 pour des données connues.
     */
    @Test
    public void testHashageDonneesConnues() {
        try {
            // Initialisation de la classe HashingSHA256
            HashingSHA256 hashingSHA256 = new HashingSHA256();

            // Données connues et hash attendu
            byte[] donnees = "testSHA256".getBytes();
            String hashAttendu = "f0860fa09a033eab8a378c9dd06a351f83ed82ffafd2ba5082a8f81c64afd208";

            // Calculer le hash
            String hashReel = hashingSHA256.calculateHash(donnees);

            // Vérifier que le hash généré correspond au hash attendu
            assertEquals(hashAttendu, hashReel, "Le hash SHA-256 généré est incorrect !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Teste le hash SHA-256 pour des données vides.
     */
    @Test
    public void testHashageDonneesVides() {
        try {
            // Initialisation de la classe HashingSHA256
            HashingSHA256 hashingSHA256 = new HashingSHA256();

            // Données vides et hash attendu
            byte[] donnees = "".getBytes();
            String hashAttendu = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

            // Calculer le hash
            String hashReel = hashingSHA256.calculateHash(donnees);

            // Vérifier que le hash généré correspond au hash attendu
            assertEquals(hashAttendu, hashReel, "Le hash SHA-256 pour des données vides est incorrect !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

}