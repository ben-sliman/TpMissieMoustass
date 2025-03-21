package Main;

import java.awt.EventQueue; // Utilisé pour s'assurer que l'interface graphique est créée sur le thread principal
import javax.swing.JFrame; // Classe de base pour créer une fenêtre
import javax.swing.JPanel; // Conteneur pour organiser les composants
import javax.swing.border.EmptyBorder; // Définit une bordure vide autour du JPanel
import javax.swing.JButton; // Boutons pour les interactions utilisateur
import java.awt.event.ActionListener; // Interface pour gérer les événements des boutons
import java.awt.event.ActionEvent; // Classe pour représenter un événement utilisateur
import javax.swing.JLabel; // Composant pour afficher du texte statique ou dynamique
import javax.swing.JProgressBar; // Barre de progression
import java.awt.FlowLayout; // Gère la disposition des composants dans un panneau

/**
 * Classe principale pour créer une interface graphique permettant
 * d'enregistrer, arrêter et sauvegarder des fichiers audio.
 */
public class InterfaceEnregistrement extends JFrame {

	private static final long serialVersionUID = 1L; // Identifiant pour la sérialisation
	private JPanel contentPane; // Le panneau principal qui contient tous les composants
	private final JLabel lblNewLabel = new JLabel("Enregistrement en cours..."); // Indique l'état de l'enregistrement

	/**
	 * Méthode principale pour lancer l'application.
	 */
	public static void main(String[] args) {
		// Utiliser EventQueue pour exécuter le programme sur le thread d'interface
		// utilisateur
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Crée une instance de la fenêtre et l'affiche
					InterfaceEnregistrement frame = new InterfaceEnregistrement();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace(); // Affiche les éventuelles erreurs dans la console
				}
			}
		});
	}

	/**
	 * Constructeur de la classe. Configure la fenêtre principale et ses composants.
	 */
	public InterfaceEnregistrement() {
		// Définir le comportement de fermeture de la fenêtre
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Définir la taille et la position de la fenêtre
		setBounds(100, 100, 450, 300);

		// Crée le panneau principal et ajoute une bordure vide
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		// Spécifie le panneau principal comme conteneur de contenu
		setContentPane(contentPane);

		// Définit un gestionnaire de disposition pour organiser les composants dans le
		// panneau
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// Ajouter une barre de progression (peut être utilisée pour indiquer le statut
		// d'enregistrement)
		JProgressBar progressBar = new JProgressBar();
		contentPane.add(progressBar);

		// Ajouter le label qui informe de l'état de l'enregistrement
		contentPane.add(lblNewLabel);

		// Ajouter le bouton "Démarrer l'enregistrement"
		JButton btnStartRecording = new JButton("Démarrer l'enregistrement");
		btnStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Action : Démarrer l'enregistrement (code fonctionnel à ajouter ici)
				System.out.println("Enregistrement démarré"); // Affiche un message dans la console
			}
		});
		contentPane.add(btnStartRecording); // Ajoute le bouton au panneau

		// Ajouter le bouton "Arrêter l'enregistrement"
		JButton btnStopRecording = new JButton("Arrêter l'enregistrement");
		btnStopRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Action : Arrêter l'enregistrement (code fonctionnel à ajouter ici)
				System.out.println("Enregistrement arrêté"); // Affiche un message dans la console
			}
		});
		contentPane.add(btnStopRecording); // Ajoute le bouton au panneau

		// Ajouter le bouton "Sauvegarder"
		JButton btnSaveRecording = new JButton("Sauvegarder");
		btnSaveRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Action : Sauvegarder l'enregistrement (code fonctionnel à ajouter ici)
				System.out.println("Enregistrement sauvegardé"); // Affiche un message dans la console
			}
		});
		contentPane.add(btnSaveRecording); // Ajoute le bouton au panneau
	}
}
