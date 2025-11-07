package com.example.server;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Blockchain {
    private final List<Block> chain = new ArrayList<>();
    private final List<Transaction> pendingTransactions = new ArrayList<>();
    private final int difficulty;
    private final int reward = 100;

    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        createGenesisBlock();
    }

    private void createGenesisBlock() {
        Block genesis = new Block(0, "0", new ArrayList<>());
        genesis.hash = genesis.calculateHash();
        chain.add(genesis);
    }

    public void addTransaction(Transaction tx) {
        if (tx != null && tx.isSignatureValid()) {
            pendingTransactions.add(tx);
        }
    }

    public Block createBlockCandidate() {
        return new Block(chain.size(), getLastBlock().hash, new ArrayList<>(pendingTransactions));
    }

    public void addMinedBlock(Block minedBlock) {
        if (minedBlock != null) {
            chain.add(minedBlock);
            pendingTransactions.clear();
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public Map<String, Integer> getBalances() {
        Map<String, Integer> balances = new HashMap<>();
        for (Block block : chain) {
            for (Transaction tx : block.transactions) {
                String sender = StringUtil.getKeyString(tx.sender);
                String recipient = StringUtil.getKeyString(tx.recipient);
                balances.put(sender, balances.getOrDefault(sender, 100) - tx.amount);
                balances.put(recipient, balances.getOrDefault(recipient, 100) + tx.amount);
            }
        }
        return balances;
    }

    public Block getLastBlock() { return chain.isEmpty() ? null : chain.get(chain.size() - 1); }
    public List<Transaction> getPendingTransactions() { return new ArrayList<>(pendingTransactions); }
}