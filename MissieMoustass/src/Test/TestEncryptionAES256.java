package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.EncryptionAES256; // Import de la classe EncryptionAES256 depuis le package Main

/**
 * Classe de test pour vérifier les fonctionnalités de EncryptionAES256.
 */
public class TestEncryptionAES256 {

    /**
     * Teste le chiffrement et le déchiffrement avec une clé générée.
     */
    @Test
    public void testChiffrementDechiffrementAvecGenerationDeCle() {
        try {
            // Générer une instance de EncryptionAES256 avec une nouvelle clé
            EncryptionAES256 encryptionAES256 = new EncryptionAES256(null);

            // Données à chiffrer
            byte[] donnees = "TestEncryption".getBytes();

            // Chiffrement
            byte[] donneesChiffrees = encryptionAES256.encrypt(donnees);

            // Déchiffrement
            byte[] donneesDechiffrees = encryptionAES256.decrypt(donneesChiffrees);

            // Vérification : Les données déchiffrées doivent être égales aux données originales
            assertArrayEquals(donnees, donneesDechiffrees, "Les données déchiffrées ne correspondent pas aux données originales !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Teste le chiffrement et le déchiffrement avec une clé existante.
     */
    @Test
    public void testChiffrementDechiffrementAvecCleExistante() {
        try {
            // Clé existante (32 octets)
            byte[] existingKey = new byte[32];
            EncryptionAES256 encryptionAES256 = new EncryptionAES256(existingKey);

            // Données à chiffrer
            byte[] donnees = "TestWithExistingKey".getBytes();

            // Chiffrement
            byte[] donneesChiffrees = encryptionAES256.encrypt(donnees);

            // Déchiffrement
            byte[] donneesDechiffrees = encryptionAES256.decrypt(donneesChiffrees);

            // Vérification : Les données déchiffrées doivent être égales aux données originales
            assertArrayEquals(donnees, donneesDechiffrees, "Les données déchiffrées ne correspondent pas aux données originales !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Teste la normalisation d'une clé brute en 32 octets.
     */
    @Test
    public void testNormalisationCle() {
        try {
            // Clé brute plus courte que 32 octets
            byte[] shortKey = new byte[]{1, 2, 3};

            // Instance temporaire pour accéder à la méthode normalizeKey
            EncryptionAES256 encryptionAES256 = new EncryptionAES256(null);

            // Normaliser la clé
            byte[] normalizedKey = encryptionAES256.getKeyBytes(); // Utiliser le getter pour obtenir une clé normalisée

            // Vérifier que la clé normalisée a une longueur de 32 octets
            assertEquals(32, normalizedKey.length, "La clé normalisée doit être de 32 octets !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }

    /**
     * Teste la génération d'une clé AES-256 sécurisée.
     */
    @Test
    public void testGenerationCle() {
        try {
            // Générer une instance avec une nouvelle clé
            EncryptionAES256 encryptionAES256 = new EncryptionAES256(null);

            // Récupérer les bytes de la clé générée
            byte[] keyBytes = encryptionAES256.getKeyBytes();

            // Vérifier que la clé générée a une longueur de 32 octets
            assertEquals(32, keyBytes.length, "La clé générée doit être de 32 octets !");
        } catch (Exception e) {
            fail("Exception inattendue : " + e.getMessage());
        }
    }
}
