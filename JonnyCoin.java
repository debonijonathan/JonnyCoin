import java.security.Key;

public class JonnyCoin {
    public static void main(String[] args){
        Blockchain blockchain = new Blockchain();
        Key key = blockchain.generateKey();
        blockchain.addTransaction("Totana","Alessandro", 50, key, key);
        blockchain.addTransaction("Totana","Gino", 50, key, key);
        blockchain.addTransaction("Totana","Paolo", 50, key, key);
        blockchain.addTransaction("Totana","Si", 50, key, key);
        blockchain.addTransaction("MinerReward","Totana", 50, key, key);
        blockchain.addTransaction("MinerReward","Totana", 50, key, key);
        blockchain.addTransaction("Totana","Paolo", 50, key, key);
        blockchain.addTransaction("Paolo","Totana", 50, key, key);

        blockchain.minePendingTransactions("Totana");

        blockchain.printBlockchain();

        System.out.println("Balance of Totana "+ blockchain.getBalance("Totana"));
    }
}