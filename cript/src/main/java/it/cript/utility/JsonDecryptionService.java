package it.cript.utility;


public class JsonDecryptionService {

    public static String processEncryptedJson(String encryptedJson, boolean condition, String decryptionKey) {
        try {
            if (condition) {
                String decryptedJson = CryptoUtils.decrypt(encryptedJson, decryptionKey);
                return decryptedJson +"\n"+"Funziona";
            } else {
                return "Condition not met, decryption not performed.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Decryption failed: " + e.getMessage();
        }
    }
}
