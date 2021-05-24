import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Wallet{
    public Key privateKey;
    public Key publicKey;
    public String name;

    public Wallet(){
        generateKeyPair();	
        name = calculateHash();
	}
		
	public void generateKeyPair() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            final KeyPair kp = kpg.generateKeyPair();
            publicKey = kp.getPublic();
            privateKey = kp.getPrivate();


        } catch (final NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    
    
    private String calculateHash() {
        try {

            final Base64.Encoder encoder = Base64.getEncoder();

            String hashString = encoder.encodeToString(privateKey.getEncoded())+ encoder.encodeToString(publicKey.getEncoded());

            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(hashString.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1)
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public String toString() {
        return name;
    }
}