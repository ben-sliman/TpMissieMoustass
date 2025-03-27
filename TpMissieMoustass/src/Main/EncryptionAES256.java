package Main;
 
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;

public class EncryptionAES256 {
    private SecretKey key;

    /**
     * Constructeur avec clé existante ou génération d'une nouvelle clé.
     * @param existingKey La clé AES-256 existante (ou null pour en générer une nouvelle).
     * @throws Exception En cas d'erreur dans la génération ou l'utilisation de la clé.
     */
    public EncryptionAES256(byte[] existingKey) throws Exception {
        if (existingKey != null) {
            this.key = new SecretKeySpec(normalizeKey(existingKey), "AES");
        } else {
            this.key = generateAESKey();
        }
    }

    /**
     * Normalise une clé AES en 32 octets (256 bits).
     * @param keyBytes La clé brute à normaliser.
     * @return Une clé de 32 octets.
     */
    private byte[] normalizeKey(byte[] keyBytes) {
        return Arrays.copyOf(keyBytes, 32);
    }

    /**
     * Chiffre les données avec AES-256.
     * @param data Les données brutes à chiffrer.
     * @return Les données chiffrées.
     * @throws Exception En cas d'erreur de chiffrement.
     */
    public byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(data);
    }

    /**
     * Déchiffre les données chiffrées.
     * @param encryptedData Les données chiffrées.
     * @return Les données déchiffrées.
     * @throws Exception En cas d'erreur de déchiffrement.
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    /**
     * Génère une clé AES-256 sécurisée.
     * @return La clé AES-256 générée.
     * @throws Exception En cas d'erreur lors de la génération de la clé.
     */
    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256, new SecureRandom());
        return keyGen.generateKey();
    }

    /**
     * Retourne la clé AES sous forme de tableau de bytes.
     * @return La clé AES-256 en tableau de bytes.
     */
    public byte[] getKeyBytes() {
        return this.key.getEncoded();
    }
}
