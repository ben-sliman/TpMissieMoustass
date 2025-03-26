package Test;

import static org.junit.Assert.*;
import org.junit.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import Main.HashingSHA256; // Importation de la classe HashingSHA256 depuis le package Main

public class TestHashingSHA256 {

    /**
     * Test du calcul du hash SHA-256 sur un fichier audio (par exemple un fichier .wav).
     */
    @Test
    public void testHashingSHA256AudioFile() {
        try {
            // Remplacer par le chemin d'un fichier audio que tu veux tester
            Path audioFilePath = Paths.get("C:/Users/askai/Desktop/TpMissieMoustass/TpMissieMoustass/enregistrement_2025-03-26_09-33-17");
            
            // Charger le fichier audio en tableau de bytes
            byte[] audioData = Files.readAllBytes(audioFilePath);
            
            // Créer une instance de HashingSHA256
            HashingSHA256 hashing = new HashingSHA256();
            
            // Calculer le hash du fichier audio
            String hash = hashing.calculateHash(audioData);
            
            // Vérifier que le hash n'est pas null
            assertNotNull("Le hash calculé ne doit pas être nul", hash);
            
            // Vérification que le hash calculé est bien de 64 caractères (SHA-256)
            assertEquals("Le hash SHA-256 doit être de 64 caractères", 64, hash.length());

            // Optionnel : vérifier que le hash est le même que celui que tu attends pour ce fichier
            String expectedHash = "attends-le-hash-que-tu-veux-comparer"; // Remplacer par le hash attendu
            assertEquals("Le hash calculé doit correspondre au hash attendu", expectedHash, hash);
            
        } catch (IOException e) {
            e.printStackTrace();
            fail("Une erreur d'entrée/sortie s'est produite : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("Une exception s'est produite pendant le test : " + e.getMessage());
        }
    }
}
