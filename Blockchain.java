import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;

class Blockchain {
    private final ArrayList<Block> chains;
    ArrayList<Transaction> pendingTransactions;
    private final int difficulty;
    private final double minerReward;
    private int blocksize;
    private HashSet nodes;

    Blockchain() {
        chains = new ArrayList<Block>();
        chains.add(addGenesisBlock());
        pendingTransactions = new ArrayList<Transaction>();
        difficulty = 1;
        minerReward = 20;
        blocksize = 1;
    }

    private void register_node(String address) {
        try {
            URL url = new URL(address);
            nodes.add(url);
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private boolean resolveConflicts(){
        /*Hashset neighbors = nodes;
        ArrayList<Block> newChain = null;

        for(Url node : nodes){
            HttpURLConnection con = (HttpURLConnection) node.openConnection();
            con.setRequestMethod("GET");
            int status = con.getResponseCode();

            if(status == 200){

            }
        }*/
        return true;
    }
    private Block getLastBlock() {
        return chains.get(chains.size() - 1);
    }

    public Key generateKey() {
        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            final KeyPair kp = kpg.generateKeyPair();
            Key pub = kp.getPublic();
            Key pvt = kp.getPrivate();

            final Base64.Encoder encoder = Base64.getEncoder();
 
            Writer out = new FileWriter("private.key");
            out.write("-----BEGIN RSA PRIVATE KEY-----\n");
            out.write(encoder.encodeToString(pvt.getEncoded()));
            out.write("\n-----END RSA PRIVATE KEY-----\n");
            out.close();
           
            out = new FileWriter("public.pub");
            out.write("-----BEGIN RSA PUBLIC KEY-----\n");
            out.write(encoder.encodeToString(pub.getEncoded()));
            out.write("\n-----END RSA PUBLIC KEY-----\n");
            out.close();

            return pub;

        } catch (final NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch(IOException io){
            io.printStackTrace();
        }
        return null;
    }

    public boolean addTransaction(String sender, String receiver, double amount, Key senderKey, Key receiverKey){
        if(sender == null && receiver == null && amount == 0){
            System.out.println("NO DATA!!");
            return false;
        }

        Transaction transaction = new Transaction(sender, receiver, amount);
        transaction.signTransaction(senderKey, receiverKey);

        if(transaction.isValidTransaction()){
            System.out.println("IS NOT VALID!!");
            return false;
        }

        
        pendingTransactions.add(transaction);
        return true;
    }
    
    private Block addGenesisBlock(){
        final Transaction firstTransaction = new Transaction("System","Totana",100);
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(firstTransaction);
        final Block firstBlock = new Block(transactions,System.currentTimeMillis(),0);
        firstBlock.prev = null;
        return firstBlock;

    }

    public boolean minePendingTransactions(final String miner){
        if(pendingTransactions.size() < 1){
            System.out.println("Not enough transaction to mine");
            return false;
        }else{
            
            for(int i = 0; i <pendingTransactions.size();i = i+blocksize){
                int end = i + blocksize;
                if(end >= pendingTransactions.size())
                    end = pendingTransactions.size();

                
                final Block tmpBlock = new Block(new ArrayList<Transaction>(pendingTransactions.subList(i, end)), System.currentTimeMillis(),this.chains.size());
                tmpBlock.prev = getLastBlock();
                tmpBlock.mineBlock(difficulty);
                
                chains.add(tmpBlock);
                
            }
            final Transaction payMiner = new Transaction("MinerReward", miner,minerReward);
            pendingTransactions.add(payMiner);
        }
        return true;
    }


    public void printBlockchain(){
        for(int i = 0; i < chains.size();i++){
            final Block tmp = chains.get(i);
            String prev_tmp ="";
            String transaction_tmp = "";
            if(tmp.prev == null)
                prev_tmp = "null";
            else
                prev_tmp = tmp.prev.hash;

            for(int j = 0;j < tmp.transactions.size();j++)
                transaction_tmp += "\n"+tmp.transactions.get(j).toPrint()+"\n";
            
            System.out.println("BLOCK INFORMATION{\nnr: "+tmp.index+", \nhash:"+tmp.hash+", \nprev:"+prev_tmp+", \ntransactions:["+transaction_tmp+"]}");


        }
    }

    public double getBalance(String wallet){
        double balance = 0;

        for(int i = 0; i < chains.size();i++){
            Block tmpBlock = chains.get(i);
            for(int j = 0; j < tmpBlock.transactions.size();j++){
                Transaction tmp = tmpBlock.transactions.get(j);
                if(tmp.sender.equals(wallet)){
                    balance -= tmp.amount;
                }else if(tmp.receiver.equals(wallet)){
                    balance += tmp.amount;
                }
            }  
        }

        //Gift for all new users
        return balance;
    }
}