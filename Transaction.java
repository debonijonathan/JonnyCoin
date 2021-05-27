
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import org.json.simple.JSONObject;


class Transaction {
    String sender;
    String receiver;
    double amount;
    long time;
    String hash;
    String signature;

    Transaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        time = System.currentTimeMillis();
        hash = calculateHash();
        signature ="";
    }
    
    Transaction(String sender, String receiver, double amount, long time, String hash, String signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.time = time;
        this.hash = hash;
        this.signature = signature;
    }

    private String calculateHash() {
        try {
            String hashString = sender + receiver + amount + time;

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

    boolean isValidTransaction() {
        if (!hash.equals(calculateHash())) {
        	System.out.println("ERR #1 : Hash different");
            return false;
        }
        if (sender.equals(receiver)) {
        	System.out.println("ERR #2 : Sender equals to receiver!");
            return false;
        }
        if (amount < 0) {
        	System.out.println("ERR #3 : Amount under 0!");
            return false;
        }
        if(!sender.equals("MinerReward")) {
	        if (signature.equals("")) {
	        	System.out.println("ERR #4 : Signature don't exist!");
	            return false;
	        }else if (!isValid()) {
	        	System.out.println("ERR #5 : Validation Signature!");
	            return false;
	        }
        }


        return true;
    }

    boolean signTransaction(Key privateKey) {
        if (!hash.equals(calculateHash()))
            return false;

        Signature sign;

        try {
            sign = Signature.getInstance("SHA256withECDSA");
            sign.initSign((PrivateKey) privateKey);
            sign.update((stringJson()).getBytes("UTF8"));
            byte[] signatureBytes = sign.sign();
            signature = Base64.getEncoder().encodeToString(signatureBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
        	System.out.println("ERR #6");
            e.printStackTrace();
        }
        return true;
    }

    boolean isValid() {
        Signature sign;
        boolean result = false;
        try{
        	
        	Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");

        	EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(sender));

        	KeyFactory keyFactory = KeyFactory.getInstance("EC");
        	PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        	
        	ecdsaVerify.initVerify(publicKey);
        	ecdsaVerify.update((stringJson()).getBytes("UTF8"));
        	result = ecdsaVerify.verify(Base64.getDecoder().decode(signature));
        	
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException | InvalidKeySpecException e) {
            return result;
        }
        
        return result;
    }
    
    private String stringJson(){
    	JSONObject transaction = new JSONObject();
    	transaction.put("sender",sender);
    	transaction.put("receiver",receiver);
    	transaction.put("amount",amount);
    	transaction.put("time",time);
    	transaction.put("hash",hash);
        return transaction.toString();
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return stringJson();
    }

    public String toPrint() {

        return stringJson();
    }
}