package Main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe représentant une fenêtre pour l'inscription d'utilisateurs. Cette
 * classe permet de valider un identifiant (email), de hacher un mot de passe et
 * d'insérer ces informations dans une base de données.
 */
public class Inscription extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField champTexteLogin; // Champ de texte pour l'adresse email
	private JPasswordField champMotDePasse; // Champ pour le mot de passe
	private JPasswordField champConfirmationMotDePasse; // Champ pour confirmer le mot de passe

	public Inscription() {
		setTitle("Inscription");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 350);
		JPanel panneauContenu = new JPanel();
		setContentPane(panneauContenu);
		panneauContenu.setLayout(null);

		// Ajouter une étiquette pour l'adresse email
		JLabel lblLogin = new JLabel("Identifiant (email) :");
		lblLogin.setBounds(50, 50, 120, 25);
		panneauContenu.add(lblLogin);

		// Champ de texte pour saisir l'adresse email
		champTexteLogin = new JTextField();
		champTexteLogin.setBounds(180, 50, 200, 25);
		panneauContenu.add(champTexteLogin);

		// Ajouter une étiquette pour le mot de passe
		JLabel lblMotDePasse = new JLabel("Mot de passe :");
		lblMotDePasse.setBounds(50, 100, 100, 25);
		panneauContenu.add(lblMotDePasse);

		// Champ pour saisir le mot de passe
		champMotDePasse = new JPasswordField();
		champMotDePasse.setBounds(180, 100, 200, 25);
		panneauContenu.add(champMotDePasse);

		// Ajouter une étiquette pour la confirmation du mot de passe
		JLabel lblConfirmationMotDePasse = new JLabel("Confirmer mot de passe :");
		lblConfirmationMotDePasse.setBounds(50, 150, 150, 25);
		panneauContenu.add(lblConfirmationMotDePasse);

		// Champ pour confirmer le mot de passe
		champConfirmationMotDePasse = new JPasswordField();
		champConfirmationMotDePasse.setBounds(180, 150, 200, 25);
		panneauContenu.add(champConfirmationMotDePasse);

		// Bouton d'inscription
		JButton btnEnregistrer = new JButton("S'inscrire");
		btnEnregistrer.setBounds(230, 200, 100, 25);
		btnEnregistrer.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String email = champTexteLogin.getText();
					String motDePasse = new String(champMotDePasse.getPassword());
					String confirmationMotDePasse = new String(champConfirmationMotDePasse.getPassword());

					// Validation des champs
					verifierChamps(email, motDePasse, confirmationMotDePasse);

					// Vérification de la correspondance des mots de passe
					if (!motDePasse.equals(confirmationMotDePasse)) {
						throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
					}

					// Validation de la complexité du mot de passe
					if (!validerMotDePasse(motDePasse)) {
						throw new IllegalArgumentException(
								"Le mot de passe doit contenir au moins 12 caractères, une majuscule, un chiffre et un caractère spécial.");
					}

					// Hachage du mot de passe
					String motDePasseHache = hacherMotDePasse(motDePasse);
					createUser(email, motDePasseHache);

					JOptionPane.showMessageDialog(null, "Inscription réussie !", "Succès",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (SQLException ex) {
					JOptionPane.showMessageDialog(null, "Erreur SQL : " + ex.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(null, "Erreur inattendue : " + ex.getMessage(), "Erreur",
							JOptionPane.ERROR_MESSAGE);
					ex.printStackTrace();
				}
			}
		});
		panneauContenu.add(btnEnregistrer);
	}

	/**
	 * Insère un nouvel utilisateur dans la table "users".
	 */
	private void createUser(String email, String motDePasseHache) throws SQLException {
		String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
		try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, email);
			pstmt.setString(2, motDePasseHache);
			pstmt.executeUpdate();
		}
	}

	/**
	 * Hache un mot de passe avec l'algorithme SHA-256.
	 */
	public String hacherMotDePasse(String motDePasse) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(motDePasse.getBytes());
		StringBuilder hexString = new StringBuilder();
		for (byte b : hash) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1)
				hexString.append('0');
			hexString.append(hex);
		}
		return hexString.toString();
	}

	/**
	 * Vérifie si les champs email et mot de passe sont valides.
	 */
	private void verifierChamps(String email, String motDePasse, String confirmationMotDePasse) throws SQLException {
		if (email.isEmpty() || motDePasse.isEmpty() || confirmationMotDePasse.isEmpty()) {
			throw new IllegalArgumentException("Veuillez remplir tous les champs.");
		}
		if (!validerEmail(email)) {
			throw new IllegalArgumentException("Adresse email non valide.");
		}
		if (emailExistant(email)) {
			throw new IllegalArgumentException("Cet email est déjà utilisé.");
		}
	}

	/**
	 * Vérifie si un email existe déjà dans la base de données.
	 */
	private boolean emailExistant(String email) throws SQLException {
		String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
		try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, email);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next() && rs.getInt(1) > 0;
			}
		}
	}

	/**
	 * Valide le format d'une adresse email avec une regex.
	 */
	public boolean validerEmail(String email) {
		String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	/**
	 * Valide la complexité du mot de passe.
	 */
	public boolean validerMotDePasse(String motDePasse) {
		String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-\\[\\]{};':\"\\\\|,.<>/?]).{12,}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(motDePasse);
		return matcher.matches();
	}
}