

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


class Blockchain {
    private ArrayList<Block> chains;
    private final ArrayList<URL> nodes;
    ArrayList<Transaction> pendingTransactions;
    private int difficulty;
    private double minerReward;
    private int blocksize;
    private double maximumCoin;

    Blockchain() {
        chains = new ArrayList<Block>();
        nodes = new ArrayList<URL>();
        maximumCoin = 3000000;
        chains.add(addGenesisBlock());
        pendingTransactions = new ArrayList<Transaction>();
        difficulty = 1;
        minerReward = 20;
        blocksize = 5;
    }

    public void registerNode(String address){
        URL url;
		try {
			url = new URL(address);
	        if(!nodes.contains(url))
	            nodes.add(url);
		} catch (MalformedURLException e) {
			System.err.println("ERROR address!");
		}
    }

    private String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
      }
    
    public String readJsonFromUrl(URL url) throws IOException, ParseException {
        InputStream is = url.openStream();
        try {
          BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
          String jsonText = readAll(rd);
          return jsonText;
        } finally {
          is.close();
        }
      }
    
    public boolean checkChain(String jsonText) throws ParseException {
    	ArrayList<Block> newChain = new ArrayList<>();
    	boolean correctTrans = true;

        int maxLength = chains.size();
        JSONParser parser = new JSONParser();
		JSONArray json = (JSONArray) parser.parse(jsonText);
		if(maxLength < json.size()) {
			for(int i = 0; i < json.size();i++) {
				JSONObject obj = (JSONObject) json.get(i);
		        long index = (long) obj.get("index");
		        String hash = (String) obj.get("hash");
		        Block prev = null;
		        if(!obj.get("prev").equals("null"))
		        	prev = newChain.get(i-1);		        

		        long time = (long) obj.get("time");
		        long nonce = (long) obj.get("nonce");
		        			
		        JSONArray array = (JSONArray) obj.get("transactions");
		        ArrayList<Transaction> transactions = new ArrayList<>();
		        for(int j = 0; j <array.size();j++) {
					JSONObject obj2 = (JSONObject) array.get(j);
					Transaction temporaryT = new Transaction((String)obj2.get("sender"),(String)obj2.get("receiver"),(double)obj2.get("amount"),(long)obj2.get("time"),(String)obj2.get("hash"),(String)obj2.get("signature"));
					transactions.add(temporaryT);
					if(temporaryT.isValidTransaction())
						correctTrans = false;
		        }
		        newChain.add(new Block(transactions, time, (int)index, prev, hash, (int)nonce));

			}
			if(isChainValid(newChain) && correctTrans)
				chains = newChain;
		}

		return true;
				
    }
    
    public boolean resolveConflicts(){
    	for(URL url : nodes) {
	    	try {
				if(checkChain(readJsonFromUrl(url))) {
					
				}
			} catch (IOException e) {
				System.out.println("ERROR #531 : Connection IOException");
				return false;
			} catch (ParseException e) {
				System.out.println("ERROR #531 : Connection ParseException");
				return false;
			}
    	}
    	return true;
    }

    private Block getLastBlock() {
        return chains.get(chains.size() - 1);
    }

    public boolean addTransaction(String sender, String receiver, double amount, Key senderKey){
        if(sender == null && receiver == null && amount == 0){
            System.out.println("NO DATA!!");
            return false;
        }

        Transaction transaction = new Transaction(sender, receiver, amount);
        transaction.signTransaction(senderKey);

        if(transaction.isValidTransaction()){
            pendingTransactions.add(transaction);
        }else {
        	return false;
        }

        return true;
    }
    
    public boolean isChainValid(ArrayList<Block> blockchain){
        for(int i = 1; i < blockchain.size();i++){
            Block currentBlock = blockchain.get(i);
            Block previousBlock = blockchain.get(i-1);

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

                List<Transaction> partial = new ArrayList<>(pendingTransactions.subList(i, end));
                
                
                final Block tmpBlock = new Block(new ArrayList<Transaction>(partial), System.currentTimeMillis(),this.chains.size());
                tmpBlock.prev = getLastBlock();
                tmpBlock.mineBlock(difficulty);
                
                for(Transaction t : partial) {
                	pendingTransactions.remove(t);
                }
                chains.add(tmpBlock);
                
            }

            maximumCoin -= minerReward;
            
            final Transaction payMiner = new Transaction("MinerReward", miner,minerReward);
            pendingTransactions.add(payMiner);
        }
        return true;
    }


    public String chainJSonEncode() {
    	JSONArray jarray = new JSONArray();
        for(int i = 0; i < chains.size();i++){
            final Block tmp = chains.get(i);
	        JSONObject obj = new JSONObject();
	        obj.put("index",tmp.index);
	        obj.put("hash",tmp.hash);
	        if(tmp.prev != null)
	        	obj.put("prev",tmp.prev.hash);
	        else
	        	obj.put("prev","null");

	        obj.put("time",tmp.time);
	        obj.put("nonce",tmp.nonce);

	        JSONArray jarrayTransaction = new JSONArray();
            for(int j = 0;j < tmp.transactions.size();j++) {
            	Transaction tmpTransaction = tmp.transactions.get(j);
    	        JSONObject trans = new JSONObject();
            	trans.put("sender",tmpTransaction.sender);
            	trans.put("receiver",tmpTransaction.receiver);
            	trans.put("amount",tmpTransaction.amount);
            	trans.put("time",tmpTransaction.time);
            	trans.put("hash",tmpTransaction.hash);
            	trans.put("signature",tmpTransaction.signature);

            	jarrayTransaction.add(trans);
            }
	        obj.put("transactions",jarrayTransaction);

	        jarray.add(obj);

            	
        }
        return jarray.toString();

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