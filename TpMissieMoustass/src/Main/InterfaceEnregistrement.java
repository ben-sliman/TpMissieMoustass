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
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Classe principale pour gérer l'enregistrement audio, le chiffrement, et le stockage dans une base de données.
 */
public class InterfaceEnregistrement extends JFrame {

    private static final long serialVersionUID = 1L;

    /**
     * Le panneau principal de l'interface utilisateur.
     */
    private JPanel contentPane;

    /**
     * Label affichant le temps écoulé pendant l'enregistrement.
     */
    private final JLabel lblNewLabel = new JLabel("Temps d'enregistrement : 0 s");

    /**
     * Ligne audio utilisée pour capturer le son du micro.
     */
    private TargetDataLine line;

    /**
     * Le format du fichier audio (WAV).
     */
    private AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

    /**
     * Le fichier audio où l'enregistrement est sauvegardé.
     */
    private File audioFile;

    /**
     * Timer utilisé pour suivre la durée de l'enregistrement.
     */
    private Timer timer;

    /**
     * Nombre de secondes écoulées depuis le début de l'enregistrement.
     */
    private int elapsedSeconds = 0;

    /**
     * Point d'entrée principal de l'application.
     * 
     * @param args Arguments de la ligne de commande (non utilisés).
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
     * Configure l'interface utilisateur et les composants.
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
                try {
                    stopRecordingAndSave();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                lblNewLabel.setText("Enregistrement arrêté et sauvegardé. Temps total : " + elapsedSeconds + " s");
            }
        });
        contentPane.add(btnStopRecording);
    }

    /**
     * Démarre l'enregistrement audio et sauvegarde le fichier temporaire.
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
     * Arrête l'enregistrement audio, chiffre les données, calcule un hash
     * et sauvegarde les informations dans la base de données.
     * 
     * @throws IOException Si une erreur se produit lors de la lecture du fichier audio.
     */
    private void stopRecordingAndSave() throws IOException {
        // Arrêter l'enregistrement
        if (line != null) {
            line.stop();
            line.close();
            System.out.println("Enregistrement arrêté");

            if (timer != null) {
                timer.stop();
            }
        }

        // Sauvegarder dans la base de données
        if (audioFile.exists()) {
            try (FileInputStream fis = new FileInputStream(audioFile)) {
                byte[] audioData = fis.readAllBytes();

                // Chiffrement et hashage
                EncryptionAES256 encryption = new EncryptionAES256(audioData);
                byte[] encryptedAudio = encryption.encrypt(audioData);

                HashingSHA256 hashing = new HashingSHA256();
                String audioHash = hashing.calculateHash(encryptedAudio);

                // Sauvegarde dans la base de données
                saveToDatabase(audioFile.getName(), elapsedSeconds, encryptedAudio, audioHash, 1); // Remplacez 1 par l'ID utilisateur approprié
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Erreur : aucun fichier trouvé.");
        }
    }

    /**
     * Insère les métadonnées et le fichier audio chiffré dans la base de données.
     * 
     * @param nomFichier    Le nom du fichier audio.
     * @param duree         La durée de l'enregistrement en secondes.
     * @param chiffre_AES256 Les données audio chiffrées.
     * @param hashage_SHA256 Le hash SHA-256 des données audio.
     * @param idUser        L'identifiant de l'utilisateur associé.
     */
    private void saveToDatabase(String nomFichier, double duree, byte[] chiffre_AES256, String hashage_SHA256, int idUser) {
        try (Connection conn = DataBaseConnexion.connect();
             PreparedStatement stmt = conn.prepareStatement(
                 "INSERT INTO enregistrement (nom_fichier, duree, chiffre_AES256, hashage_SHA256, id_user) VALUES (?, ?, ?, ?, ?)")) {
            stmt.setString(1, nomFichier);
            stmt.setDouble(2, duree);
            stmt.setBytes(3, chiffre_AES256);
            stmt.setString(4, hashage_SHA256);
            stmt.setInt(5, idUser);

            stmt.executeUpdate();
            System.out.println("Enregistrement sauvegardé dans la base de données.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne le format audio utilisé pour les enregistrements.
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
