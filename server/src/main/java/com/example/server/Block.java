package com.example.server;
import java.util.List;
import java.util.stream.Collectors;

public class Block {
    public final int id;
    public final long timestamp;
    public final String previousHash;
    public final List<Transaction> transactions;
    public String hash;
    public int nonce;

    public Block(int id, String previousHash, List<Transaction> transactions) {
        this.id = id;
        this.timestamp = System.currentTimeMillis();
        this.previousHash = previousHash;
        this.transactions = transactions;
    }

    public String calculateHash() {
        String txData = transactions.stream().map(Object::toString).collect(Collectors.joining());
        return StringUtil.applySha256(id + timestamp + previousHash + txData + nonce);
    }
}