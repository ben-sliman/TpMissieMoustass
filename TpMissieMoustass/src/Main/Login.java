package Main;

import java.awt.EventQueue;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
 * Classe principale permettant la gestion de la connexion utilisateur.
 * Elle inclut des fonctionnalités comme la validation des identifiants, 
 * le hachage de mots de passe, la mise à jour des informations et 
 * l'accès aux pages spécifiques (AdminUtilisateur et CrudEnregistrement).
 */
public class Login extends JFrame {

    private static final long serialVersionUID = 1L;

    /** Panneau principal de l'interface graphique. */
    private JPanel panneauContenu;

    /** Champ de texte pour saisir l'identifiant (email). */
    private JTextField champTexteLogin;

    /** Champ de saisie pour le mot de passe (caché). */
    private JPasswordField champMotDePasse;

    /** Liste déroulante permettant de choisir la page après connexion. */
    private JComboBox<String> comboPages;

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Login cadre = new Login();
                cadre.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructeur de la classe Login.
     * Configure l'interface graphique et ajoute les composants nécessaires.
     */
    public Login() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 400);
        setTitle("Connexion");

        panneauContenu = new JPanel();
        panneauContenu.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(panneauContenu);
        panneauContenu.setLayout(null);

        // Label pour l'identifiant
        JLabel lblLogin = new JLabel("Identifiant (email) :");
        lblLogin.setBounds(50, 50, 120, 25);
        panneauContenu.add(lblLogin);

        // Champ de texte pour l'identifiant
        champTexteLogin = new JTextField();
        champTexteLogin.setBounds(180, 50, 200, 25);
        panneauContenu.add(champTexteLogin);
        champTexteLogin.setColumns(10);

        // Label pour le mot de passe
        JLabel lblMotDePasse = new JLabel("Mot de passe :");
        lblMotDePasse.setBounds(50, 100, 100, 25);
        panneauContenu.add(lblMotDePasse);

        // Champ de saisie pour le mot de passe
        champMotDePasse = new JPasswordField();
        champMotDePasse.setBounds(180, 100, 200, 25);
        panneauContenu.add(champMotDePasse);

        // Label pour le choix de la page
        JLabel lblChoixPage = new JLabel("Choisir une page :");
        lblChoixPage.setBounds(50, 150, 120, 25);
        panneauContenu.add(lblChoixPage);

        // Liste déroulante pour choisir la page
        comboPages = new JComboBox<>(new String[] { "", "AdminUtilisateur", "CrudEnregistrement" });
        comboPages.setBounds(180, 150, 200, 25);
        panneauContenu.add(comboPages);

        // Bouton de connexion
        JButton btnConnexion = new JButton("Connexion");
        btnConnexion.setBounds(180, 190, 100, 25);
        panneauContenu.add(btnConnexion);

        // Action associée au bouton de connexion
        btnConnexion.addActionListener(e -> {
            try {
                String login = champTexteLogin.getText().trim();
                String motDePasse = new String(champMotDePasse.getPassword()).trim();
                String pageChoisie = (String) comboPages.getSelectedItem();

                // Vérifications et validation
                if (login.isEmpty() || motDePasse.isEmpty()) {
                    throw new IllegalArgumentException("Tous les champs doivent être remplis.");
                }
                if (!validerEmail(login)) {
                    throw new IllegalArgumentException("Adresse email invalide.");
                }
                if (pageChoisie == null || pageChoisie.isEmpty()) {
                    throw new IllegalArgumentException("Veuillez sélectionner une page.");
                }

                // Hachage du mot de passe
                String motDePasseHache = hacherMotDePasse(motDePasse);

                // Validation des identifiants
                if (validerLogin(login, motDePasseHache)) {
                    JOptionPane.showMessageDialog(null, "Connexion réussie !", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                    ouvrirPage(pageChoisie);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Identifiant ou mot de passe incorrect.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Bouton "Mot de Passe Oublié"
        JButton btnMotDePasseOublie = new JButton("Mot de Passe Oublié");
        btnMotDePasseOublie.setBounds(140, 230, 170, 25);
        panneauContenu.add(btnMotDePasseOublie);

        // Action associée au bouton "Mot de Passe Oublié"
        btnMotDePasseOublie.addActionListener(e -> {
            try {
                String email = JOptionPane.showInputDialog(null, "Entrez votre adresse email :", "Mot de Passe Oublié",
                        JOptionPane.QUESTION_MESSAGE);

                if (email == null || email.trim().isEmpty()) {
                    throw new IllegalArgumentException("Veuillez entrer une adresse email.");
                }

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
                    String nouveauMotDePasse = new String(champNouveauMotDePasse.getPassword()).trim();
                    String confirmerMotDePasse = new String(champConfirmerMotDePasse.getPassword()).trim();

                    if (!nouveauMotDePasse.equals(confirmerMotDePasse)) {
                        throw new IllegalArgumentException("Les mots de passe ne correspondent pas.");
                    }

                    String motDePasseHache = hacherMotDePasse(nouveauMotDePasse);
                    mettreAJourMotDePasse(email, motDePasseHache);

                    JOptionPane.showMessageDialog(null, "Votre mot de passe a été mis à jour.", "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        // Bouton d'inscription
        JButton btnInscription = new JButton("S'inscrire");
        btnInscription.setBounds(180, 270, 100, 25);
        panneauContenu.add(btnInscription);

        // Action associée au bouton d'inscription
        btnInscription.addActionListener(e -> {
            Inscription inscriptionFrame = new Inscription();
            inscriptionFrame.setVisible(true);
        });
    }

    /**
     * Ouvre la page sélectionnée par l'utilisateur.
     *
     * @param page La page à ouvrir ("AdminUtilisateur" ou "CrudEnregistrement").
     */
    private void ouvrirPage(String page) {
        try {
            if (page.equals("AdminUtilisateur")) {
                new AdminUtilisateur().setVisible(true);
            } else if (page.equals("CrudEnregistrement")) {
                new CrudEnregistrement().setVisible(true);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Erreur lors de l'ouverture de la page " + page, "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    /**
     * Valide les identifiants utilisateur (email et mot de passe haché) avec la base de données.
     *
     * @param login          L'identifiant (email) saisi par l'utilisateur.
     * @param motDePasseHache Le mot de passe haché correspondant à l'utilisateur.
     * @return {@code true} si les identifiants sont valides, {@code false} sinon.
     * @throws SQLException En cas d'erreur dans la requête SQL.
     */
    private boolean validerLogin(String login, String motDePasseHache) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ? AND password = ?";
        try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Associer les paramètres pour la requête SQL
            pstmt.setString(1, login); // Associer l'email de l'utilisateur
            pstmt.setString(2, motDePasseHache); // Associer le mot de passe haché
            ResultSet rs = pstmt.executeQuery(); // Exécuter la requête
            // Vérifier si un utilisateur avec ces identifiants existe
            return rs.next() && rs.getInt(1) > 0;
        }
    }

    /**
     * Valide si l'adresse email entrée respecte un format standard.
     *
     * @param email L'adresse email saisie par l'utilisateur.
     * @return {@code true} si l'adresse email est valide, {@code false} sinon.
     */
    public boolean validerEmail(String email) {
        // Expression régulière pour vérifier la validité de l'email
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"; // Modèle pour valider une adresse email
        Pattern pattern = Pattern.compile(regex); // Compiler l'expression régulière
        Matcher matcher = pattern.matcher(email); // Vérifier l'email donné
        return matcher.matches(); // Retourner si l'email correspond au modèle
    }

    /**
     * Met à jour le mot de passe d'un utilisateur dans la base de données.
     *
     * @param email                L'adresse email de l'utilisateur.
     * @param nouveauMotDePasseHache Le nouveau mot de passe haché à sauvegarder.
     * @throws SQLException En cas d'erreur lors de la mise à jour dans la base de données.
     */
    private void mettreAJourMotDePasse(String email, String nouveauMotDePasseHache) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (Connection conn = DataBaseConnexion.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Associer les valeurs pour l'email et le nouveau mot de passe haché
            pstmt.setString(1, nouveauMotDePasseHache); // Nouveau mot de passe
            pstmt.setString(2, email); // Email de l'utilisateur
            pstmt.executeUpdate(); // Exécuter la requête SQL pour mettre à jour
        }
    }

    /**
     * Hache un mot de passe utilisateur avec l'algorithme SHA-256.
     *
     * @param motDePasse Le mot de passe brut à hacher.
     * @return Une chaîne hexadécimale représentant le mot de passe haché.
     * @throws NoSuchAlgorithmException Si l'algorithme SHA-256 n'est pas disponible.
     */
    public String hacherMotDePasse(String motDePasse) throws NoSuchAlgorithmException {
        // Instancier l'algorithme SHA-256 pour le hachage
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(motDePasse.getBytes()); // Effectuer le hachage du mot de passe
        StringBuilder hexString = new StringBuilder();
        // Convertir chaque byte du hash en une chaîne hexadécimale
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString(); // Retourner le mot de passe haché
    }
}
 
