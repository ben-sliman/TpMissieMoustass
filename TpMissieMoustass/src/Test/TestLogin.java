package Test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import Main.Login;
import java.security.NoSuchAlgorithmException;

class TestLogin {

	@Test
	void testValiderEmail() {
		// Création d'une instance de la classe Login
		Login login = new Login();

		// Cas de test pour les emails valides
		assertTrue(login.validerEmail("test@test.fr"), "L'email test@test.fr doit être considéré comme valide.");
		assertTrue(login.validerEmail("ben@ben.fr"), "L'email ben@ben.fr doit être considéré comme valide.");
		assertTrue(login.validerEmail("user.name@domain.com"), "L'email user.name@domain.com doit être valide.");
		assertTrue(login.validerEmail("user-name@sub.domain.co"), "L'email user-name@sub.domain.co doit être valide.");

		// Cas de test pour les emails invalides
		assertFalse(login.validerEmail("utilisateur@domaine"),
				"L'email utilisateur@domaine (manque TLD) ne doit pas être valide.");
		assertFalse(login.validerEmail("utilisateurdomaine.com"),
				"L'email utilisateurdomaine.com (manque @) ne doit pas être valide.");
		assertFalse(login.validerEmail("@domaine.com"),
				"L'email @domaine.com (manque partie locale) ne doit pas être valide.");
		assertFalse(login.validerEmail("user@.com"), "L'email user@.com (manque domaine) ne doit pas être valide.");
		assertFalse(login.validerEmail("user@domain..com"),
				"L'email user@domain..com (double point) ne doit pas être valide.");
	}

	@Test
	void testHacherMotDePasse() {
		// Création d'une instance de la classe Login
		Login login = new Login();

		try {
			// Cas de test pour hacher un mot de passe valide
			String motDePasse = "MotDePasse123!";
			String hashCalcule = login.hacherMotDePasse(motDePasse);

			// Vérification de la longueur et du format du hash
			assertNotNull(hashCalcule, "Le hachage du mot de passe ne doit pas être nul.");
			assertEquals(64, hashCalcule.length(), "Le hachage SHA-256 doit contenir 64 caractères.");
			assertTrue(hashCalcule.matches("^[a-fA-F0-9]+$"),
					"Le hachage doit contenir uniquement des caractères hexadécimaux.");

			// Cas de test pour un mot de passe vide
			String motDePasseVide = "";
			String hashMotDePasseVide = login.hacherMotDePasse(motDePasseVide);
			assertNotNull(hashMotDePasseVide, "Le hachage d'un mot de passe vide doit produire un résultat.");
			assertEquals(64, hashMotDePasseVide.length(), "Même un mot de passe vide doit générer un hash SHA-256.");

		} catch (NoSuchAlgorithmException e) {
			fail("L'algorithme SHA-256 est introuvable : " + e.getMessage());
		}
	}
}