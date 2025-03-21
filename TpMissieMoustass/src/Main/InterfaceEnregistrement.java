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

public class InterfaceEnregistrement extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private final JLabel lblNewLabel = new JLabel("Enregistrement en cours...");

    /**
     * Launch the application.
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
     * Create the frame.
     */
    public InterfaceEnregistrement() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

        setContentPane(contentPane);
        contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // Ajouter le JProgressBar
        JProgressBar progressBar = new JProgressBar();
        contentPane.add(progressBar);

        // Ajouter le label indiquant l'enregistrement
        contentPane.add(lblNewLabel);

        // Ajouter le bouton "Démarrer l'enregistrement"
        JButton btnStartRecording = new JButton("Démarrer l'enregistrement");
        btnStartRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code pour démarrer l'enregistrement ici
                System.out.println("Enregistrement démarré");
            }
        });
        contentPane.add(btnStartRecording);

        // Ajouter le bouton "Arrêter l'enregistrement"
        JButton btnStopRecording = new JButton("Arrêter l'enregistrement");
        btnStopRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code pour arrêter l'enregistrement ici
                System.out.println("Enregistrement arrêté");
            }
        });
        contentPane.add(btnStopRecording);

        // Ajouter le bouton "Sauvegarder"
        JButton btnSaveRecording = new JButton("Sauvegarder");
        btnSaveRecording.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Code pour sauvegarder l'enregistrement ici
                System.out.println("Enregistrement sauvegardé");
            }
        });
        contentPane.add(btnSaveRecording);
    }
}
