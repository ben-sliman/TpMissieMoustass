package Main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.sound.sampled.*;
import java.io.*;
import java.sql.*;
import java.util.Vector;

/**
 * Classe principale pour gérer les enregistrements audio dans la base de données.
 */
public class CrudEnregistrement extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Panneau principal de l'interface graphique.
     */
    private JPanel contentPane;

    /**
     * Tableau pour afficher les enregistrements dans la base de données.
     */
    private JTable table;

    /**
     * Modèle de table pour gérer les données dynamiquement.
     */
    private DefaultTableModel tableModel;

    /**
     * Champs pour entrer ou modifier les données.
     */
    private JTextField txtNomFichier, txtDuree, txtIdUser;

    /**
     * Identifiant de l'enregistrement sélectionné pour mise à jour ou suppression.
     */
    private int selectedId = -1;

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                CrudEnregistrement frame = new CrudEnregistrement();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Constructeur : configure l'interface utilisateur.
     */
    public CrudEnregistrement() {
        setTitle("Gestion des Enregistrements Audio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        // Configuration du panneau principal
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        // Titre de l'interface
        JLabel lblTitle = new JLabel("Gestion des Enregistrements Audio");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // Tableau des enregistrements
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom Fichier", "Durée", "ID Utilisateur"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.getSelectedRow();
                if (row != -1) {
                    selectedId = (int) tableModel.getValueAt(row, 0);
                    txtNomFichier.setText((String) tableModel.getValueAt(row, 1));
                    txtDuree.setText(String.valueOf(tableModel.getValueAt(row, 2)));
                    txtIdUser.setText(String.valueOf(tableModel.getValueAt(row, 3)));
                }
            }
        });
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // Panneau de formulaire
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Nom Fichier :"));
        txtNomFichier = new JTextField();
        formPanel.add(txtNomFichier);

        formPanel.add(new JLabel("Durée :"));
        txtDuree = new JTextField();
        formPanel.add(txtDuree);

        formPanel.add(new JLabel("ID Utilisateur :"));
        txtIdUser = new JTextField();
        formPanel.add(txtIdUser);

        contentPane.add(formPanel, BorderLayout.NORTH);

        // Panneau de boutons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnAdd = new JButton("Ajouter");
        btnAdd.addActionListener(e -> ajouterEnregistrement());
        buttonPanel.add(btnAdd);

        JButton btnUpdate = new JButton("Modifier");
        btnUpdate.addActionListener(e -> modifierEnregistrement());
        buttonPanel.add(btnUpdate);

        JButton btnDelete = new JButton("Supprimer");
        btnDelete.addActionListener(e -> supprimerEnregistrement());
        buttonPanel.add(btnDelete);

        JButton btnPlay = new JButton("Lire");
        btnPlay.addActionListener(e -> lireEnregistrement());
        buttonPanel.add(btnPlay);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        // Charger les enregistrements depuis la base
        chargerEnregistrements();
    }

    /**
     * Charge tous les enregistrements depuis la base de données et les affiche dans la table.
     */
    private void chargerEnregistrements() {
        String query = "SELECT id, nom_fichier, duree, id_user FROM enregistrement";
        try (Connection conn = DataBaseConnexion.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0); // Réinitialiser la table
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nom_fichier"),
                    rs.getInt("duree"),
                    rs.getInt("id_user")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lit un enregistrement sélectionné dans la base de données et le joue.
     */
    private void lireEnregistrement() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à lire.");
            return;
        }

        String query = "SELECT chiffre_AES256 FROM enregistrement WHERE id = ?";
        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selectedId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                byte[] encryptedData = rs.getBytes("chiffre_AES256");

                // Déchiffrement (remplacez par la clé AES correcte si nécessaire)
                EncryptionAES256 encryption = new EncryptionAES256(encryptedData);
                byte[] decryptedData = encryption.decrypt(encryptedData);

                // Écriture dans un fichier temporaire pour lecture
                File tempFile = new File("temp_audio.wav");
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    fos.write(decryptedData);
                }

                // Jouer l'audio temporaire
                playAudio1(tempFile);
            } else {
                JOptionPane.showMessageDialog(this, "Aucun fichier trouvé pour cet enregistrement.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Joue un fichier audio.
     *
     * @param file Fichier audio à jouer.
     */
    private void playAudio1(File file) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            JOptionPane.showMessageDialog(this, "Lecture en cours...");

            // Attendre que l'audio se termine
            while (clip.isRunning()) {
                Thread.sleep(100);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la lecture du fichier audio.");
        }
    }

    /**
     * Ajoute un nouvel enregistrement dans la base de données.
     */
    private void ajouterEnregistrement() {
        String nomFichier = txtNomFichier.getText();
        int duree = Integer.parseInt(txtDuree.getText());
        int idUser = Integer.parseInt(txtIdUser.getText());
        String query = "INSERT INTO enregistrement (nom_fichier, duree, chiffre_AES256, hashage_SHA256, id_user) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nomFichier);
            stmt.setInt(2, duree);
            stmt.setBytes(3, new byte[]{0}); // Données fictives pour chiffrement
            stmt.setString(4, "HASH_PLACEHOLDER"); // Hash fictif
            stmt.setInt(5, idUser);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Enregistrement ajouté avec succès !");
            chargerEnregistrements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifie l'enregistrement sélectionné dans la base de données.
     */
    private void modifierEnregistrement() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à modifier.");
            return;
        }

        String nomFichier = txtNomFichier.getText();
        int duree = Integer.parseInt(txtDuree.getText());
        int idUser = Integer.parseInt(txtIdUser.getText());
        String query = "UPDATE enregistrement SET nom_fichier = ?, duree = ?, id_user = ? WHERE id = ?";

        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, nomFichier);
            stmt.setInt(2, duree);
            stmt.setInt(3, idUser);
            stmt.setInt(4, selectedId);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Enregistrement modifié avec succès !");
            chargerEnregistrements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime l'enregistrement sélectionné dans la base de données.
     */
    private void supprimerEnregistrement() {
        if (selectedId == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à supprimer.");
            return;
        }

        String query = "DELETE FROM enregistrement WHERE id = ?";
        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, selectedId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Enregistrement supprimé avec succès !");
            chargerEnregistrements();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Joue un fichier audio temporairement enregistré.
     * Cette méthode est appelée pour jouer l'enregistrement après déchiffrement.
     *
     * @param file Fichier temporaire contenant les données audio.
     */
    private void playAudio(File file) {
        try (AudioInputStream ais = AudioSystem.getAudioInputStream(file)) {
            Clip clip = AudioSystem.getClip();
            clip.open(ais);
            clip.start();
            JOptionPane.showMessageDialog(this, "Lecture en cours...");

            while (clip.isRunning()) {
                Thread.sleep(100); // Attendre que l'audio se termine
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la lecture du fichier audio.");
        }
    }
}
