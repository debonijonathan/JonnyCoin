package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import structure.Wallet;
import structure.Blockchain;

public class JonnyCoin {
    public static void main(String[] args) throws IOException{
        Blockchain blockchain = new Blockchain();


        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        Wallet walletC = new Wallet();

        // Enter data using BufferReader
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
 
        Wallet walletUsed = walletA;
        Wallet walletUsed2 = walletC;
        boolean flag = false;
        while(true) {
	        System.out.println ("Menu :");
	        System.out.println ("1 - Transaction");
	        System.out.println ("2 - Mine");
	        System.out.println ("3 - Change Wallet");
	        System.out.println ("4 - Show Blockchain");
	        System.out.println ("5 - Get Balance");
	        System.out.println ("6 - Quit");
	
	        // Reading data using readLine
	        String name = reader.readLine();
	 
	        if(name.equals("2")) {
	            blockchain.minePendingTransactions(walletUsed.name);
	        }else if(name.equals("1")) {
	        	
	        	while(true) {
		 	        System.out.println ("1 - Wallet 1");
		 	        System.out.println ("2 - Wallet 2");
		 	        System.out.println ("3 - Wallet 3");
		 	        String tmp = reader.readLine();
		 	        if(tmp.equals("1")) {
		 	        	walletUsed2 = walletA;
		 	        }else if(tmp.equals("2")) {
		 	        	walletUsed2 = walletB;
		 	        }else if(tmp.equals("3")) {
		 	        	walletUsed2 = walletC;
		 	        }
		 	        
		 	        if(walletUsed2.name.equals(walletUsed.name)) {
			 	        System.out.println ("Use Another Wallet!");

		 	        }else {
		 	        	break;
		 	        }
		 	        System.out.println ();

	        	}
	        	
	 	        System.out.print ("Set Amount: ");
		        double amount = Double.parseDouble(reader.readLine());

	            if(blockchain.addTransaction(walletUsed.name,walletUsed2.name, amount, walletUsed.privateKey)) {
	            	System.out.println("Transaction sended!");
	            }else {
	            	System.out.println("Error! Transaction didn't work!");
	            }
	        }else if(name.equals("3")) {
	        	System.out.println ("1 - Wallet 1");
	 	        System.out.println ("2 - Wallet 2");
	 	        System.out.println ("3 - Wallet 3");
	 	        String tmp = reader.readLine();
	 	        if(tmp.equals("1")) {
	 	        	walletUsed2 = walletA;
	 	        }else if(tmp.equals("2")) {
	 	        	walletUsed2 = walletB;
	 	        }else if(tmp.equals("3")) {
	 	        	walletUsed2 = walletC;
	 	        }else {
	            	System.out.println("Error! Change didn't work!");
	 	        }
	        }else if(name.equals("4")) {
	            String encoded = blockchain.chainJSonEncode();
	            System.out.println(encoded);
	        }else  if(name.equals("5")) {
	            System.out.println("Balance of "+walletUsed.name +" : "+ blockchain.getBalance(walletUsed.name));
	        }else  if(name.equals("6")) {
	        	break;
	        }
	        System.out.println("\n");
	        
        }

        
    }
}