package Main;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionAES256 {

    private SecretKey key;

    /**
     * Constructeur avec clé existante.
     * Si une clé est fournie, elle est utilisée. Sinon, une nouvelle clé est générée.
     * 
     * @param existingKey La clé AES-256 existante (ou null pour en générer une nouvelle).
     * @throws Exception En cas d'erreur dans la génération ou l'utilisation de la clé.
     */
    public EncryptionAES256(byte[] existingKey) throws Exception {
        if (existingKey != null) {
            // Utiliser une clé existante
            this.key = new SecretKeySpec(existingKey, "AES");
        } else {
            // Générer une nouvelle clé
            this.key = generateAESKey();
        }
    }

    /**
     * Méthode pour chiffrer les données.
     * 
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
     * Méthode pour déchiffrer les données.
     * 
     * @param encryptedData Les données chiffrées à déchiffrer.
     * @return Les données déchiffrées.
     * @throws Exception En cas d'erreur de déchiffrement.
     */
    public byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        return cipher.doFinal(encryptedData);
    }

    /**
     * Méthode pour générer une nouvelle clé AES-256.
     * 
     * @return La clé AES-256 générée.
     * @throws Exception En cas d'erreur lors de la génération de la clé.
     */
    private SecretKey generateAESKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256); // Taille de la clé
        return keyGen.generateKey();
    }

    /**
     * Méthode pour obtenir la clé AES-256 en tant que tableau de bytes.
     * 
     * @return La clé AES-256 sous forme de tableau de bytes.
     */
    public byte[] getKeyBytes() {
        return this.key.getEncoded();
    }

    /**
     * Méthode pour obtenir la clé AES-256 utilisée.
     * 
     * @return L'objet SecretKey AES-256.
     */
    public SecretKey getKey() {
        return this.key;
    }
}
