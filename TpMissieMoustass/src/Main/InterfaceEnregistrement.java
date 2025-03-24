package Main;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import java.awt.FlowLayout;
import java.awt.Color;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe principale pour créer une interface graphique permettant
 * d'enregistrer, arrêter, sauvegarder et supprimer des fichiers audio.
 */
public class InterfaceEnregistrement extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Le panneau principal qui contient tous les composants de l'interface graphique.
     */
    private JPanel contentPane;

    /**
     * Label pour afficher le temps écoulé pendant l'enregistrement audio.
     * Indique l'état actuel et le temps écoulé au format texte.
     */
    private final JLabel lblNewLabel = new JLabel("Temps d'enregistrement : 0 s");

    /**
     * Ligne de données cible utilisée pour capturer l'audio à partir d'une source externe.
     * Cette ligne permet de collecter et de traiter le signal audio enregistré.
     */
    private TargetDataLine line;

    /**
     * Type de fichier audio dans lequel l'enregistrement sera sauvegardé.
     * Le format utilisé est WAV, adapté à la plupart des utilisations audio.
     */
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    /**
     * Fichier audio où sera sauvegardé l'enregistrement.
     * Le nom du fichier inclut la date et l'heure actuelles pour éviter les doublons.
     */
    private File audioFile;

    /**
     * Timer utilisé pour suivre le temps écoulé pendant l'enregistrement.
     * Met à jour l'affichage chaque seconde.
     */
    private Timer timer;

    /**
     * Compteur représentant le temps écoulé en secondes depuis le début de l'enregistrement.
     * Initialisé à 0 au démarrage.
     */
    private int elapsedSeconds = 0;

    /**
     * Méthode principale pour lancer l'application.
     * 
     * @param args Arguments de la ligne de commande.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    InterfaceEnregistrement frame = new InterfaceEnregistrement();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Constructeur de la classe InterfaceEnregistrement.
     * Configure l'interface graphique et initialise les composants nécessaires.
     */
    public InterfaceEnregistrement() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(199, 254, 204));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JProgressBar progressBar = new JProgressBar();
        contentPane.add(progressBar);

        contentPane.add(lblNewLabel);

        JButton btnStartRecording = new JButton("Démarrer l'enregistrement");
        btnStartRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startRecording();
                lblNewLabel.setText("Enregistrement en cours... Temps : 0 s");
            }
        });
        contentPane.add(btnStartRecording);

        JButton btnStopRecording = new JButton("Arrêter l'enregistrement");
        btnStopRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                stopRecording();
                lblNewLabel.setText("Enregistrement arrêté. Temps total : " + elapsedSeconds + " s");
            }
        });
        contentPane.add(btnStopRecording);

        JButton btnSaveRecording = new JButton("Sauvegarder");
        btnSaveRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveRecording();
            }
        });
        contentPane.add(btnSaveRecording);

        JButton btnDeleteRecording = new JButton("Supprimer l'enregistrement");
        btnDeleteRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteRecording();
            }
        });
        contentPane.add(btnDeleteRecording);
    }

    /**
     * Méthode pour démarrer l'enregistrement audio.
     * Configure le fichier avec un nom unique basé sur la date et l'heure actuelles.
     * Initialise la ligne audio, démarre l'enregistrement et met à jour le temps écoulé.
     */
    private void startRecording() {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            audioFile = new File("enregistrement_" + timestamp + ".wav");

            AudioFormat format = getAudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            line = (TargetDataLine) AudioSystem.getLine(info);
            line.open(format);
            line.start();

            Thread recordingThread = new Thread(() -> {
                AudioInputStream ais = new AudioInputStream(line);
                try {
                    AudioSystem.write(ais, fileType, audioFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            recordingThread.start();

            elapsedSeconds = 0;
            timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    elapsedSeconds++;
                    lblNewLabel.setText("Enregistrement en cours... Temps : " + elapsedSeconds + " s");
                }
            });
            timer.start();
            System.out.println("Enregistrement démarré");
        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Méthode pour arrêter l'enregistrement audio.
     * Arrête la capture audio et le compteur de temps.
     */
    private void stopRecording() {
        if (line != null) {
            line.stop();
            line.close();
            System.out.println("Enregistrement arrêté");

            if (timer != null) {
                timer.stop();
            }
        }
    }

    /**
     * Méthode pour sauvegarder l'enregistrement audio.
     * Affiche le chemin du fichier si la sauvegarde est réussie.
     */
    private void saveRecording() {
        if (audioFile.exists()) {
            System.out.println("Enregistrement sauvegardé : " + audioFile.getAbsolutePath());
        } else {
            System.out.println("Erreur : aucun fichier trouvé.");
        }
    }

    /**
     * Méthode pour supprimer l'enregistrement audio.
     * Supprime le fichier audio si présent et met à jour l'état de l'interface graphique.
     */
    private void deleteRecording() {
        if (audioFile.exists()) {
            if (audioFile.delete()) {
                System.out.println("Enregistrement supprimé avec succès.");
                lblNewLabel.setText("Aucun enregistrement disponible.");
            } else {
                System.out.println("Erreur : impossible de supprimer l'enregistrement.");
            }
        } else {
            System.out.println("Aucun fichier à supprimer.");
        }
    }

    /**
     * Méthode pour définir le format audio.
     * Spécifie les paramètres tels que la fréquence d'échantillonnage, la taille des échantillons et les canaux audio.
     * 
     * @return AudioFormat Le format audio spécifié.
     */
    private AudioFormat getAudioFormat() {
        float sampleRate = 16000;
        int sampleSizeInBits = 16;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = false;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
    }
}
