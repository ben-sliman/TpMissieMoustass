package Main;

import java.security.MessageDigest;

public class HashingSHA256 {

    /**
     * Méthode pour calculer le hash SHA-256.
     * 
     * @param data Les données à hasher.
     * @return Le hash sous forme hexadécimale.
     */
    public String calculateHash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
