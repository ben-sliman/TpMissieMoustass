package Main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface utilisateur pour la gestion de la table `users` dans une base de
 * données SQLite. Cette classe permet d'effectuer des actions CRUD tout en
 * respectant les critères de mot de passe et le hachage SHA-256.
 */
public class AdminUtilisateur extends JFrame {

    /**
     * Le panneau principal de la fenêtre, contenant tous les composants graphiques.
     * Ce panneau est utilisé pour organiser les éléments de l'interface utilisateur.
     */
	private JPanel contentPane;
    /**
     * Le champ de texte permettant à l'utilisateur de saisir son adresse e-mail.
     */
	private JTextField emailField;
    /**
     * Le champ de texte permettant à l'utilisateur de saisir son mot de passe de manière sécurisée.
     */
	private JPasswordField passwordField;
    /**
     * Le champ de texte permettant à l'utilisateur de saisir son identifiant.
     */
	private JTextField idField;
    /**
     * Le tableau affichant les informations des utilisateurs.
     * Il utilise le modèle de données (tableModel) pour afficher les informations dans l'interface graphique.
     */
	private JTable userTable;
    /**
     * Le modèle de données utilisé pour gérer le contenu du tableau.
     * Ce modèle contient les données qui seront affichées dans un tableau JTable.
     */
	private DefaultTableModel tableModel;

	/**
	 * Constructeur de l'interface utilisateur. Configure les composants de
	 * l'interface et les actions CRUD.
	 */
	public AdminUtilisateur() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 500);
		setTitle("Gestion des Utilisateurs (Admin)");
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblTitle = new JLabel("Gestion des Utilisateurs");
		lblTitle.setBounds(300, 10, 200, 30);
		contentPane.add(lblTitle);

		JLabel lblId = new JLabel("ID :");
		lblId.setBounds(20, 50, 80, 25);
		contentPane.add(lblId);

		idField = new JTextField();
		idField.setBounds(112, 49, 150, 25);
		contentPane.add(idField);
		idField.setColumns(10);
		idField.setEnabled(false); // Le champ ID est désactivé pour éviter les modifications

		JLabel lblEmail = new JLabel("Email :");
		lblEmail.setBounds(20, 90, 80, 25);
		contentPane.add(lblEmail);

		emailField = new JTextField();
		emailField.setBounds(110, 89, 150, 25);
		contentPane.add(emailField);
		emailField.setColumns(10);

		JLabel lblPassword = new JLabel("Mot de Passe :");
		lblPassword.setBounds(20, 130, 100, 25);
		contentPane.add(lblPassword);

		passwordField = new JPasswordField();
		passwordField.setBounds(110, 129, 150, 25);
		contentPane.add(passwordField);

		JButton btnAdd = new JButton("Ajouter");
		btnAdd.setBounds(20, 170, 100, 25);
		contentPane.add(btnAdd);

		JButton btnUpdate = new JButton("Mettre à jour");
		btnUpdate.setBounds(130, 170, 120, 25);
		contentPane.add(btnUpdate);

		JButton btnDelete = new JButton("Supprimer");
		btnDelete.setBounds(20, 207, 100, 25);
		contentPane.add(btnDelete);

		JButton btnClear = new JButton("Effacer");
		btnClear.setBounds(140, 207, 100, 25);
		contentPane.add(btnClear);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(300, 50, 460, 300);
		contentPane.add(scrollPane);

		userTable = new JTable();
		tableModel = new DefaultTableModel(new Object[] { "ID", "Email", "Mot de Passe (haché)" }, 0);
		userTable.setModel(tableModel);
		scrollPane.setViewportView(userTable);

		// Charger les données initiales
		loadUsers();

		// Action Ajouter
		btnAdd.addActionListener(e -> addUser());

		// Action Mettre à jour
		btnUpdate.addActionListener(e -> updateUser());

		// Action Supprimer
		btnDelete.addActionListener(e -> deleteUser());

		// Action Effacer
		btnClear.addActionListener(e -> clearFields());

		// Action Sélection d'une ligne dans la table
		userTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int selectedRow = userTable.getSelectedRow();
				idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
				emailField.setText(tableModel.getValueAt(selectedRow, 1).toString());
				passwordField.setText(""); // Le champ mot de passe reste vide pour la sécurité
			}
		});
	}

	/**
	 * Valide le format d'une adresse email avec une regex.
	 *
	 * @param email L'adresse email à valider.
	 * @return true si le format est valide, sinon false.
	 */
	public boolean validerEmail(String email) {
		String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches(); // Retourne true si le format correspond
	}

	/**
	 * Charger les utilisateurs depuis la base de données.
	 */
	private void loadUsers() {
		try (Connection conn = DataBaseConnexion.connect();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

			tableModel.setRowCount(0); // Réinitialiser la table
			while (rs.next()) {
				tableModel.addRow(new Object[] { rs.getInt("id"), rs.getString("email"), rs.getString("password") });
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Erreur lors du chargement des données.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Ajouter un utilisateur dans la base de données.
	 */
	private void addUser() {
		String email = emailField.getText();
		String password = new String(passwordField.getPassword());

		if (email.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!validerEmail(email)) {
			JOptionPane.showMessageDialog(this, "Adresse email invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Demander la confirmation du mot de passe
		JPasswordField confirmPasswordField = new JPasswordField();
		int option = JOptionPane.showConfirmDialog(null, confirmPasswordField, "Confirmez le mot de passe",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			String confirmPassword = new String(confirmPasswordField.getPassword());
			if (!password.equals(confirmPassword)) {
				JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			JOptionPane.showMessageDialog(this, "Opération annulée.", "Annulation", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (!validatePassword(password)) {
			JOptionPane.showMessageDialog(this,
					"Le mot de passe doit contenir au moins 12 caractères, "
							+ "une lettre majuscule, une minuscule, un chiffre et un caractère spécial.",
					"Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try (Connection conn = DataBaseConnexion.connect();
				PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (email, password) VALUES (?, ?)")) {

			pstmt.setString(1, email);
			pstmt.setString(2, hashPassword(password)); // Hachage du mot de passe
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Utilisateur ajouté avec succès.");
			loadUsers();
			clearFields();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de l'utilisateur.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Mettre à jour un utilisateur dans la base de données.
	 */
	private void updateUser() {
		String id = idField.getText();
		String email = emailField.getText();
		String password = new String(passwordField.getPassword());

		if (id.isEmpty() || email.isEmpty() || password.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Tous les champs sont obligatoires.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (!validerEmail(email)) {
			JOptionPane.showMessageDialog(this, "Adresse email invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Demander la confirmation du mot de passe
		JPasswordField confirmPasswordField = new JPasswordField();
		int option = JOptionPane.showConfirmDialog(null, confirmPasswordField, "Confirmez le mot de passe",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (option == JOptionPane.OK_OPTION) {
			String confirmPassword = new String(confirmPasswordField.getPassword());
			if (!password.equals(confirmPassword)) {
				JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas.", "Erreur",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} else {
			JOptionPane.showMessageDialog(this, "Opération annulée.", "Annulation", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		if (!validatePassword(password)) {
			JOptionPane.showMessageDialog(this,
					"Le mot de passe doit contenir au moins 12 caractères, "
							+ "une lettre majuscule, une minuscule, un chiffre et un caractère spécial.",
					"Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try (Connection conn = DataBaseConnexion.connect();
				PreparedStatement pstmt = conn
						.prepareStatement("UPDATE users SET email = ?, password = ? WHERE id = ?")) {

			pstmt.setString(1, email);
			pstmt.setString(2, hashPassword(password)); // Hachage du mot de passe
			pstmt.setInt(3, Integer.parseInt(id));
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Utilisateur mis à jour avec succès.");
			loadUsers();
			clearFields();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour de l'utilisateur.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Valider un mot de passe selon des critères spécifiques.
	 *
	 * @param password Le mot de passe à valider.
	 * @return true si le mot de passe respecte les critères, sinon false.
	 */
	private boolean validatePassword(String password) {
		String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";
		return Pattern.matches(regex, password); // Retourne true si le mot de passe respecte les critères
	}

	/**
	 * Hacher un mot de passe avec SHA-256.
	 *
	 * @param password Le mot de passe à hacher.
	 * @return Le mot de passe haché sous forme de chaîne hexadécimale.
	 */
	private String hashPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(password.getBytes());
			StringBuilder hexString = new StringBuilder();

			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("Erreur : Algorithme SHA-256 non disponible.", e);
		}
	}

	/**
	 * Supprimer un utilisateur de la base de données.
	 */
	private void deleteUser() {
		String id = idField.getText();

		if (id.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur à supprimer.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try (Connection conn = DataBaseConnexion.connect();
				PreparedStatement pstmt = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {

			pstmt.setInt(1, Integer.parseInt(id));
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "Utilisateur supprimé avec succès.");
			loadUsers();
			clearFields();

		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'utilisateur.", "Erreur",
					JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Effacer les champs de saisie.
	 */
	private void clearFields() {
		idField.setText("");
		emailField.setText("");
		passwordField.setText("");
	}
}