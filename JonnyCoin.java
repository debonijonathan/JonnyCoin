package com.jonathandeboni.jcoin;

import java.security.Key;

public class JonnyCoin {
    public static void main(String[] args){
        Blockchain blockchain = new Blockchain();


        Wallet walletA = new Wallet();
        Wallet walletB = new Wallet();
        
        
        
        blockchain.addTransaction("Totana",walletB.name, 300, walletA.privateKey, walletB.publicKey);
        blockchain.addTransaction("Totana",walletA.name, 30, walletA.privateKey, walletB.publicKey);
        blockchain.addTransaction(walletA.name,walletB.name, 10, walletA.privateKey, walletB.publicKey);
        blockchain.addTransaction(walletA.name,walletB.name, 100, walletA.privateKey, walletB.publicKey);
        blockchain.addTransaction(walletB.name,walletA.name, 5, walletA.privateKey, walletB.publicKey);


        blockchain.minePendingTransactions("Totana");

        System.out.println(blockchain.isChainValid());

        blockchain.printBlockchain();

        System.out.println("Balance of "+walletA.name +" : "+ blockchain.getBalance(walletA.name));
        System.out.println("Balance of "+walletB.name +" : "+ blockchain.getBalance(walletB.name));

    }
}