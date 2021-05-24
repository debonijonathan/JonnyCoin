import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Date;

import jdk.nashorn.internal.runtime.JSONFunctions;

class Transaction {
    String sender;
    String receiver;
    double amount;
    long time;
    String hash;
    byte[] signature;

    Transaction(String sender, String receiver, double amount) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        time = System.currentTimeMillis();
        hash = calculateHash();
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

    boolean isValidTransaction(Key pub) {
        if (hash != calculateHash())
            return false;
        if (sender.equals(receiver))
            return false;
        if (amount < 0)
            return false;
        if (signature.length <= 0)
            return false;
        if (!isValid(pub)) {
            return false;
        }

        return true;
    }

    boolean signTransaction(Key privateKey) {
        if (hash != calculateHash())
            return false;

        Signature sign;
        try {
            sign = Signature.getInstance("SHA256withRSA");
            sign.initSign((PrivateKey) privateKey);
            sign.update((stringJson()).getBytes("UTF8"));
            byte[] signatureBytes = sign.sign();
            signature = signatureBytes;
            System.out.println("Transaction Signed!");
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return true;
    }

    boolean isValid(Key publicKey) {
        Signature sign;
        try{
            sign = Signature.getInstance("SHA256withRSA");
            sign.initVerify((PublicKey) publicKey);
            sign.update((stringJson()).getBytes("UTF8"));
            if(sign.verify(signature)){
                return true;
            }else{
                return false;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException | UnsupportedEncodingException e) {
            return false;
        }
    }
    
    private String stringJson(){
        return "{sender: "+sender+", receiver:"+receiver+", amount:"+amount+", time:"+time+"}";
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