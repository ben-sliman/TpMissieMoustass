package Main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe Login représentant l'interface graphique de connexion. Cette classe
 * permet aux utilisateurs de se connecter ou de réinitialiser leur mot de
 * passe.
 */
public class Login extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel panneauContenu; // Panneau principal contenant les composants de la fenêtre
	private JTextField champTexteLogin; // Champ pour saisir l'adresse email
	private JPasswordField champMotDePasse; // Champ pour saisir le mot de passe

	/**
	 * Point d'entrée de l'application. Initialise la fenêtre de connexion.
	 *
	 * @param args Arguments de la ligne de commande (non utilisés).
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login cadre = new Login(); // Créer et afficher la fenêtre de connexion
					cadre.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace(); // Afficher les erreurs pour le débogage
				}
			}
		});
	}

	/**
	 * Constructeur de la classe Login. Configure les composants de l'interface
	 * utilisateur.
	 */
	public Login() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Définit l'action de fermeture
		setBounds(100, 100, 450, 300); // Définit la taille et la position initiales de la fenêtre
		setTitle("Connexion");

		// Initialisation du panneau de contenu
		panneauContenu = new JPanel();
		panneauContenu.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(panneauContenu);
		panneauContenu.setLayout(null);

		// Label et champ pour l'identifiant (email)
		JLabel lblLogin = new JLabel("Identifiant (email) :");
		lblLogin.setBounds(50, 50, 120, 25);
		panneauContenu.add(lblLogin);

		champTexteLogin = new JTextField();
		champTexteLogin.setBounds(180, 50, 200, 25);
		panneauContenu.add(champTexteLogin);
		champTexteLogin.setColumns(10);

		// Label et champ pour le mot de passe
		JLabel lblMotDePasse = new JLabel("Mot de passe :");
		lblMotDePasse.setBounds(50, 100, 100, 25);
		panneauContenu.add(lblMotDePasse);

		champMotDePasse = new JPasswordField();
		champMotDePasse.setBounds(180, 100, 200, 25);
		panneauContenu.add(champMotDePasse);

		// Bouton de connexion
		JButton btnConnexion = new JButton("Connexion");
		btnConnexion.setBounds(210, 150, 100, 25);
		panneauContenu.add(btnConnexion);

		btnConnexion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Récupérer les valeurs saisies
					String login = champTexteLogin.getText().trim();
					String motDePasse = new String(champMotDePasse.getPassword()).trim();

					// Valider les champs
					if (login.isEmpty() || motDePasse.isEmpty()) {
						throw new IllegalArgumentException("Tous les champs doivent être remplis.");
					}
					if (!validerEmail(login)) {
						throw new IllegalArgumentException("Adresse email invalide.");
					}

					// Hachage du mot de passe
					String motDePasseHache = hacherMotDePasse(motDePasse);

					// Vérification des identifiants via la base de données
					if (validerLogin(login, motDePasseHache)) {
						JOptionPane.showMessageDialog(null, "Connexion réussie !", "Succès",
								JOptionPane.INFORMATION_MESSAGE);

						// Ouvrir la fenêtre adminUtilisateur
						AdminUtilisateur adminFrame = new AdminUtilisateur();
						adminFrame.setVisible(true);

						// Fermer la fenêtre de connexion
						dispose();

					} else {
						JOptionPane.showMessageDialog(null, "Identifiant ou mot de passe incorrect.", "Erreur",
								JOptionPane.ERROR_MESSAGE);
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		});

		// Bouton "Mot de Passe Oublié"
		JButton btnMotDePasseOublie = new JButton("Mot de Passe Oublié");
		btnMotDePasseOublie.setBounds(172, 176, 187, 25);
		panneauContenu.add(btnMotDePasseOublie);

		btnMotDePasseOublie.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Saisie de l'email de l'utilisateur
					String email = JOptionPane.showInputDialog(null, "Entrez votre adresse email :",
							"Mot de Passe Oublié", JOptionPane.QUESTION_MESSAGE);

					if (email == null || email.trim().isEmpty()) {
						throw new IllegalArgumentException("Veuillez entrer une adresse email.");
					}

					// Création des champs pour saisir et confirmer le nouveau mot de passe
					JPasswordField champNouveauMotDePasse = new JPasswordField();
					JPasswordField champConfirmerMotDePasse = new JPasswordField();

					JPanel panel = new JPanel();
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					panel.add(new JLabel("Nouveau mot de passe :"));
					panel.add(champNouveauMotDePasse);
					panel.add(new JLabel("Confirmer le mot de passe :"));
					panel.add(champConfirmerMotDePasse);

					int option = JOptionPane.showConfirmDialog(null, panel, "Entrez un nouveau mot de passe",
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

					if (option == JOptionPane.OK_OPTION) {
						// Récupération des mots de passe saisis
						String nouveauMotDePasse = new String(champNouveauMotDePasse.getPassword()).trim();
						String confirmerMotDePasse = new String(champConfirmerMotDePasse.getPassword()).trim();

						// Vérification simple : les mots de passe doivent être identiques
						if (!nouveauMotDePasse.equals(confirmerMotDePasse)) {
							throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
						}

						// Mettre à jour le mot de passe dans la base de données
						String motDePasseHache = hacherMotDePasse(nouveauMotDePasse);
						mettreAJourMotDePasse(email, motDePasseHache);

						JOptionPane.showMessageDialog(null, "Votre mot de passe a été mis à jour.", "Succès",
								JOptionPane.INFORMATION_MESSAGE);
					} else {
						JOptionPane.showMessageDialog(null, "Opération annulée.", "Annulation",
								JOptionPane.INFORMATION_MESSAGE);
					}

				} catch (IllegalArgumentException ex) {
					JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		});

		// Bouton pour ouvrir la page d'inscription
		JButton btnInscription = new JButton("S'inscrire");
		btnInscription.setBounds(191, 213, 150, 25);
		panneauContenu.add(btnInscription);

		btnInscription.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Ouvrir une nouvelle fenêtre pour l'inscription
					Inscription inscriptionFrame = new Inscription();
					inscriptionFrame.setVisible(true); // Afficher la fenêtre
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Erreur : " + ex.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	/**
	 * Valide un login et un mot de passe dans la base de données.
	 *
	 * @param login           L'adresse email de l'utilisateur.
	 * @param motDePasseHache Le mot de passe haché.
	 * @return true si les identifiants sont corrects, false sinon.
	 * @throws SQLException En cas d'erreur SQL.
	 */
	private boolean validerLogin(String login, String motDePasseHache) throws SQLException {
		String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
		try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, login);
			pstmt.setString(2, motDePasseHache);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		}
	}

	/**
	 * Vérifie si une adresse email existe dans la base de données.
	 *
	 * @param email L'adresse email à vérifier.
	 * @return true si l'email existe, false sinon.
	 * @throws SQLException En cas d'erreur SQL.
	 */
	private boolean emailExistant(String email) throws SQLException {
		String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
		try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, email);
			ResultSet rs = pstmt.executeQuery();
			return rs.next() && rs.getInt(1) > 0;
		}
	}

	/**
	 * Met à jour le mot de passe d'un utilisateur.
	 *
	 * @param email                  L'adresse email de l'utilisateur.
	 * @param nouveauMotDePasseHache Le nouveau mot de passe haché.
	 * @throws SQLException En cas d'erreur SQL.
	 */
	private void mettreAJourMotDePasse(String email, String nouveauMotDePasseHache) throws SQLException {
		String sql = "UPDATE users SET password = ? WHERE email = ?";
		try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, nouveauMotDePasseHache);
			pstmt.setString(2, email);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Valide le format d'une adresse email à l'aide d'une expression régulière.
	 *
	 * @param email L'adresse email à valider.
	 * @return true si le format est valide, sinon false.
	 */
	public boolean validerEmail(String email) {
		String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Définition d'un format d'email valide
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches(); // Retourne true si le format correspond
	}

	/**
	 * Hache un mot de passe en utilisant l'algorithme SHA-256.
	 *
	 * @param motDePasse Le mot de passe à hacher.
	 * @return Le mot de passe haché sous forme de chaîne hexadécimale.
	 * @throws NoSuchAlgorithmException si l'algorithme SHA-256 n'est pas
	 *                                  disponible.
	 */
	public String hacherMotDePasse(String motDePasse) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256"); // Initialisation de l'algorithme de hachage
		byte[] hash = digest.digest(motDePasse.getBytes()); // Transformation du mot de passe en tableau d'octets
		StringBuilder hexString = new StringBuilder(); // Stockage du résultat hexadécimal

		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b); // Conversion des octets en hexadécimal
			if (hex.length() == 1) {
				hexString.append('0'); // Ajout d'un zéro pour garder un format cohérent
			}
			hexString.append(hex);
		}
		return hexString.toString(); // Renvoi du mot de passe haché
	}
}