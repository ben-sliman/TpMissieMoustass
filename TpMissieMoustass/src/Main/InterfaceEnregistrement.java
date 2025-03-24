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
import java.awt.FlowLayout;
import java.awt.Color; // Gère la disposition des composants dans un panneau
import javax.sound.sampled.*; // API Java pour capturer et gérer les sons
import java.io.File; // Gestion des fichiers
import java.io.IOException; // Gestion des exceptions liées aux entrées/sorties
import javax.swing.Timer; // Timer pour mettre à jour le temps écoulé

/**
 * Classe principale pour créer une interface graphique permettant
 * d'enregistrer, arrêter et sauvegarder des fichiers audio.
 */
public class InterfaceEnregistrement extends JFrame {

    private static final long serialVersionUID = 1L; // Identifiant pour la sérialisation
    /**
     * Le panneau principal qui contient tous les composants de l'interface graphique.
     */
    private JPanel contentPane; // Le panneau principal qui contient tous les composants
    /**
     * Indique l'état de l'enregistrement et affiche le temps écoulé en secondes.
     * Le texte initial est "Temps d'enregistrement : 0 s".
     */
    private final JLabel lblNewLabel = new JLabel("Temps d'enregistrement : 0 s"); // Indique l'état de l'enregistrement et le temps écoulé
    /**
     * Ligne de données cible pour capturer l'audio à partir d'une source telle qu'un microphone.
     */
    private TargetDataLine line; // Ligne de données cible pour capturer l'audio
    /**
     * Type de fichier pour l'audio, ici spécifié comme WAV (format audio Wave).
     */    
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE; // Type de fichier pour l'audio (WAV)
    /**
     * Fichier audio où l'enregistrement sera sauvegardé.
     * Le fichier est initialisé avec le nom "enregistrement.wav".
     */    
    private File audioFile = new File("enregistrement.wav"); // Fichier où l'audio sera sauvegardé
    /**
     * Timer pour mettre à jour le compteur de temps toutes les secondes pendant l'enregistrement.
     */
    private Timer timer; // Timer pour mettre à jour le compteur de temps
    /**
     * Compteur de secondes écoulées depuis le début de l'enregistrement.
     * Initialisé à 0.
     */
    private int elapsedSeconds = 0; // Compteur de secondes écoulées

    /**
     * Méthode principale pour lancer l'application.
     *
     * @param args Les arguments de ligne de commande passés au programme.
     *             Ils peuvent être utilisés pour personnaliser le comportement
     *             de l'application au démarrage.
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
        contentPane.setBackground(new Color(199, 254, 204));
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
                // Action : Démarrer l'enregistrement
                startRecording();
                lblNewLabel.setText("Enregistrement en cours... Temps : 0 s");
            }
        });
        contentPane.add(btnStartRecording); // Ajoute le bouton au panneau

        // Ajouter le bouton "Arrêter l'enregistrement"
        JButton btnStopRecording = new JButton("Arrêter l'enregistrement");
        btnStopRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Action : Arrêter l'enregistrement
                stopRecording();
                lblNewLabel.setText("Enregistrement arrêté. Temps total : " + elapsedSeconds + " s");
            }
        });
        contentPane.add(btnStopRecording); // Ajoute le bouton au panneau

        // Ajouter le bouton "Sauvegarder"
        JButton btnSaveRecording = new JButton("Sauvegarder");
        btnSaveRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Action : Sauvegarder l'enregistrement
                saveRecording();
            }
        });
        contentPane.add(btnSaveRecording); // Ajoute le bouton au panneau
    }

    /**
     * Méthode pour démarrer l'enregistrement audio.
     */
    private void startRecording() {
        try {
            // Définir le format audio
            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            // Ouvrir une ligne pour capturer l'audio
            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start(); // Démarrer la capture audio

            // Créer un thread pour l'enregistrement
            Thread recordingThread = new Thread(() -> {
                AudioInputStream ais = new AudioInputStream(line);
                try {
                    // Écrire l'audio dans le fichier spécifié
                    AudioSystem.write(ais, fileType, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recordingThread.start(); // Démarrer l'enregistrement

            // Démarrer le timer qui met à jour le compteur de temps chaque seconde
            elapsedSeconds = 0;
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    elapsedSeconds++;
                    lblNewLabel.setText("Enregistrement en cours... Temps : " + elapsedSeconds + " s");
                }
            });
            timer.start(); // Démarrer le timer
            System.out.println("Enregistrement démarré");
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Méthode pour arrêter l'enregistrement audio.
     */
    private void stopRecording() {
        if (line != null) {
            line.stop(); // Arrêter la capture audio
            line.close(); // Fermer la ligne
            System.out.println("Enregistrement arrêté");

            if (timer != null) {
                timer.stop(); // Arrêter le timer
            }
        }
    }

    /**
     * Méthode pour sauvegarder l'enregistrement audio.
     */
    private void saveRecording() {
        if (audioFile.exists()) {
            System.out.println("Enregistrement sauvegardé : " + audioFile.getAbsolutePath());
        } else {
            System.out.println("Erreur : aucun fichier trouvé.");
        }
    }

    /**
     * Définition du format audio.
     */
    private AudioFormat getAudioFormat() {
        // Définir le format audio avec fréquence d'échantillonnage, taille, canaux, etc.
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
