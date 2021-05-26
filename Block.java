package com.jonathandeboni.jcoin;


import java.security.MessageDigest;
import java.util.ArrayList;

class Block {
    ArrayList<Transaction> transactions;
    long time;
    int index;
    Block prev;
    String hash;
    int nonce;

    Block(ArrayList<Transaction> transactions, long time, int index) {
        this.transactions = transactions;
        this.time = time;
        this.index = index;
        this.prev = null;
        this.nonce = 0;
        this.hash = this.calculateHash();
    }

    boolean mineBlock(int difficulty) {
        String hashPuzzle = "";
        String subHash = hash.substring(0, difficulty);
        for (int i = 1; i <= difficulty; i++) {
            hashPuzzle += i + "";
        }

        while (!subHash.equals(hashPuzzle)) {
            nonce += 1;
            hash = calculateHash();
            subHash = hash.substring(0, difficulty);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Block Mined! With the Nonce = "+ nonce +" HASH "+ hash);
        return true;
    }
    String calculateHash() {
        try{
            String data ="";
            for(Transaction t :transactions)
                data += t.toString();

            if(prev != null)
                data += time+""+index+""+""+this.prev.hash + nonce;
            else
                data += time+""+index+"" + nonce;

            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hash = digest.digest(data.getBytes("UTF-8"));
            final StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < hash.length; i++) {
                final String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) 
                    hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
        
    }

    
}