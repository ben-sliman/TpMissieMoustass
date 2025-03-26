package Main;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.sql.*;

/**
 * Classe représentant l'interface graphique pour l'enregistrement des données.
 * Cette classe étend JFrame pour fournir une interface utilisateur pour effectuer des opérations CRUD.
 */
public class CrudEnregistrement extends JFrame {

    private static final long serialVersionUID = 1L;
    /**
     * Le panneau principal de la fenêtre, contenant tous les composants graphiques.
     * Ce panneau est utilisé pour organiser les éléments de l'interface utilisateur.
     */
    private JPanel contentPane;
    /**
     * Le tableau affichant des données dans l'interface graphique.
     * Ce tableau est utilisé pour afficher les informations sous forme de lignes et de colonnes.
     */
    private JTable table;
    /**
     * Le modèle de données utilisé pour gérer le contenu du tableau.
     * Ce modèle contient les données qui seront affichées dans un tableau JTable.
     */
    private DefaultTableModel tableModel;

    /**
     * Constructeur de la classe CrudEnregistrement..
     */
    public CrudEnregistrement() {
        setTitle("Gestion des Enregistrements Audio");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);

        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Gestion des Enregistrements Audio");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(lblTitle, BorderLayout.NORTH);

        // Table pour afficher les enregistrements
        tableModel = new DefaultTableModel(new Object[]{"ID", "Nom Fichier", "Durée", "Hash SHA-256"}, 0);
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        contentPane.add(new JScrollPane(table), BorderLayout.CENTER);

        // Boutons (Lire + Supprimer)
        JPanel buttonPanel = new JPanel(new FlowLayout());
        
        JButton btnLire = new JButton("Lire");
        btnLire.addActionListener(e -> lireDonnéesChiffrées());
        buttonPanel.add(btnLire);

        JButton btnSupprimer = new JButton("Supprimer");
        btnSupprimer.addActionListener(e -> supprimerEnregistrement());
        buttonPanel.add(btnSupprimer);

        contentPane.add(buttonPanel, BorderLayout.SOUTH);

        chargerEnregistrements();
    }

    /**
     * Charge les enregistrements depuis la base de données et les affiche dans la table.
     */
    private void chargerEnregistrements() {
        String query = "SELECT id, nom_fichier, duree, hashage_SHA256 FROM enregistrement";
        try (Connection conn = DataBaseConnexion.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            tableModel.setRowCount(0); // Réinitialiser la table
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("nom_fichier"),
                    rs.getInt("duree"),
                    rs.getString("hashage_SHA256") // Ajouter hashage_SHA256 dans la table
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des enregistrements.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Lit les données chiffrées (BLOB) d'un enregistrement sélectionné dans la base de données
     * et affiche des informations à leur sujet.
     */
    private void lireDonnéesChiffrées() {
        // Vérifier si un enregistrement est sélectionné dans la table
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à lire.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Récupérer le nom du fichier sélectionné depuis la table
        String selectedFile = (String) tableModel.getValueAt(selectedRow, 1);

        // Requête SQL pour récupérer le BLOB chiffré
        String query = "SELECT chiffre_AES256 FROM enregistrement WHERE nom_fichier = ?";
        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, selectedFile); // Associer le nom du fichier
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                byte[] encryptedData = rs.getBytes("chiffre_AES256"); // Récupérer les données chiffrées

                // Vérifier si le BLOB a été récupéré avec succès
                if (encryptedData == null || encryptedData.length == 0) {
                    JOptionPane.showMessageDialog(this, "Les données chiffrées sont introuvables ou invalides.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Afficher les informations sur les données chiffrées
                JOptionPane.showMessageDialog(this, "Données chiffrées récupérées avec succès. Taille : " + encryptedData.length + " octets.", "Succès", JOptionPane.INFORMATION_MESSAGE);

                // Optionnel : Écriture dans un fichier binaire pour analyse ultérieure
                File tempEncryptedFile = new File("encrypted_" + selectedFile);
                try (FileOutputStream fos = new FileOutputStream(tempEncryptedFile)) {
                    fos.write(encryptedData);
                    JOptionPane.showMessageDialog(this, "Les données chiffrées ont été sauvegardées dans : " + tempEncryptedFile.getAbsolutePath(), "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Aucun enregistrement trouvé pour ce fichier.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException sqlError) {
            sqlError.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la récupération des données chiffrées.", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IOException ioError) {
            ioError.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la sauvegarde des données chiffrées.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Supprime un enregistrement sélectionné dans la base de données.
     */
    private void supprimerEnregistrement() {
        // Vérifier si un enregistrement est sélectionné dans la table
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un enregistrement à supprimer.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Récupérer l'ID de l'enregistrement sélectionné
        int id = (int) tableModel.getValueAt(selectedRow, 0);

        // Requête SQL pour supprimer l'enregistrement
        String query = "DELETE FROM enregistrement WHERE id = ?";
        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id); // Associer l'ID à la requête
            pstmt.executeUpdate(); // Exécuter la requête

            JOptionPane.showMessageDialog(this, "Enregistrement supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            chargerEnregistrements(); // Recharger la table
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors de la suppression de l'enregistrement.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
