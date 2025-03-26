package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.Inscription;

import java.security.NoSuchAlgorithmException;

class TestInscription {

	@Test
	void testValiderEmail() {
		Inscription inscription = new Inscription();

		// Cas de test pour les emails valides
		assertTrue(inscription.validerEmail("exemple@domaine.fr"),
				"L'email valide exemple@domaine.fr devrait être accepté.");
		assertTrue(inscription.validerEmail("user.name@domain.com"),
				"L'email valide user.name@domain.com devrait être accepté.");
		assertTrue(inscription.validerEmail("user_name@sub.domain.co"),
				"L'email valide user_name@sub.domain.co devrait être accepté.");

		// Cas de test pour les emails invalides
		assertFalse(inscription.validerEmail("utilisateur@domaine"),
				"L'email utilisateur@domaine sans TLD ne devrait pas être accepté.");
		assertFalse(inscription.validerEmail("exempledomaine.com"),
				"L'email exempledomaine.com sans @ ne devrait pas être accepté.");
		assertFalse(inscription.validerEmail("user@domain..com"),
				"L'email user@domain..com avec un TLD incorrect ne devrait pas être accepté.");
		assertFalse(inscription.validerEmail("user@.com"),
				"L'email user@.com avec un domaine incorrect ne devrait pas être accepté.");
		assertFalse(inscription.validerEmail("user@domain_com"),
				"L'email user@domain_com avec un caractère non valide (_) ne devrait pas être accepté.");
	}

	@Test
	void testHacherMotDePasse() {
		Inscription inscription = new Inscription();

		try {
			// Cas de test pour hacher un mot de passe
			String motDePasse = "MotDePasse123!";
			String hashCalcule = inscription.hacherMotDePasse(motDePasse);

			// Vérifier que le hash n'est pas nul ou vide
			assertNotNull(hashCalcule, "Le hachage ne doit pas être nul.");
			assertFalse(hashCalcule.isEmpty(), "Le hachage ne doit pas être vide.");

			// Vérifier que le hachage est une chaîne hexadécimale de 64 caractères (32
			// octets en SHA-256)
			assertEquals(64, hashCalcule.length(), "La longueur du hachage SHA-256 doit être de 64 caractères.");
			assertTrue(hashCalcule.matches("^[a-fA-F0-9]+$"),
					"Le hachage doit contenir uniquement des caractères hexadécimaux.");
		} catch (NoSuchAlgorithmException e) {
			fail("L'algorithme SHA-256 est introuvable.");
		}
	}
}