package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.HashingSHA256; // Import de la classe HashingSHA256 depuis le package Main

/**
 * Classe de test pour la méthode calculateHash et la vérification d'intégrité dans HashingSHA256.
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

    /**
     * Teste la vérification de l'intégrité des données en comparant des données identiques.
     */
    @Test
    public void testVerificationIntegriteDonneesIdentiques() {
        try {
            // Initialisation de la classe HashingSHA256
            HashingSHA256 hashingSHA256 = new HashingSHA256();

            // Données identiques
            byte[] donneesOriginales = "Données identiques".getBytes();
            byte[] donneesNouvelle = "Données identiques".getBytes();

            // Vérifier que les données sont intactes
            boolean integrite = hashingSHA256.verifyDataIntegrity(donneesOriginales, donneesNouvelle);

            // L'intégrité doit être vraie pour des données identiques
            assertTrue(integrite, "L'intégrité des données n'a pas été respectée alors que les données sont identiques !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Teste la vérification de l'intégrité des données en comparant des données modifiées.
     */
    @Test
    public void testVerificationIntegriteDonneesModifiees() {
        try {
            // Initialisation de la classe HashingSHA256
            HashingSHA256 hashingSHA256 = new HashingSHA256();

            // Données originales et données modifiées
            byte[] donneesOriginales = "Données d'origine".getBytes();
            byte[] donneesModifiees = "Données modifiées".getBytes();

            // Vérifier que les données ont été modifiées (intégrité compromise)
            boolean integrite = hashingSHA256.verifyDataIntegrity(donneesOriginales, donneesModifiees);

            // L'intégrité doit être fausse pour des données modifiées
            assertFalse(integrite, "L'intégrité des données est incorrectement validée pour des données modifiées !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }
}
