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
    private int difficulty;
    private double minerReward;
    private int blocksize;
    private double maximumCoin;

    Blockchain() {
        chains = new ArrayList<Block>();
        maximumCoin = 3000000;
        chains.add(addGenesisBlock());
        pendingTransactions = new ArrayList<Transaction>();
        difficulty = 1;
        minerReward = 20;
        blocksize = 1;
    }

    private Block getLastBlock() {
        return chains.get(chains.size() - 1);
    }

    public boolean addTransaction(String sender, String receiver, double amount, Key senderKey, Key receiverKey){
        if(sender == null && receiver == null && amount == 0){
            System.out.println("NO DATA!!");
            return false;
        }

        Transaction transaction = new Transaction(sender, receiver, amount);
        transaction.signTransaction(senderKey);

        if(transaction.isValidTransaction(receiverKey)){
            System.out.println("IS NOT VALID!!");
            return false;
        }else{
            System.out.println("IS VALID!!");
        }

        if((getBalance(sender) - amount) > 0)
            pendingTransactions.add(transaction);
        return true;
    }
    
    public boolean isChainValid(){
        for(int i = 1; i < chains.size();i++){
            Block currentBlock = chains.get(i);
            Block previousBlock = chains.get(i-1);

            if(!(currentBlock.hash).equals(currentBlock.calculateHash())){
                return false;
            }
            
            if(!(currentBlock.prev.hash).equals(previousBlock.hash)){
                return false;
            }

        }
        return true;
    }


    private Block addGenesisBlock(){
        final Transaction firstTransaction = new Transaction("System","Totana",maximumCoin);
        ArrayList<Transaction> transactions = new ArrayList<>();
        transactions.add(firstTransaction);
        Block firstBlock = new Block(transactions,System.currentTimeMillis(),0);
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

            maximumCoin -= minerReward;
            
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

        return balance;
    }
}