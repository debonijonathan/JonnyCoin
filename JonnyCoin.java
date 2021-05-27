
import java.security.Key;

public class JonnyCoin {
    public static void main(String[] args){
        Blockchain blockchain = new Blockchain();


        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        
        
        
        blockchain.addTransaction("Totana",walletB.name, 75, walletA.privateKey);
        blockchain.addTransaction("Totana",walletA.name, 100, walletA.privateKey);
        blockchain.addTransaction(walletA.name,walletB.name, 10, walletA.privateKey);
        blockchain.addTransaction(walletA.name,walletB.name, 40, walletA.privateKey);
        blockchain.addTransaction(walletB.name,walletA.name, 5, walletB.privateKey);


        blockchain.minePendingTransactions("Totana");

        String encoded = blockchain.chainJSonEncode();
        System.out.println(encoded);

        System.out.println("Balance of "+walletA.name +" : "+ blockchain.getBalance(walletA.name));
        System.out.println("Balance of "+walletB.name +" : "+ blockchain.getBalance(walletB.name));

        

    }
}