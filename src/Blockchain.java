import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Blockchain implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Block> chain = Collections.synchronizedList(new ArrayList<>());
    private final List<Transaction> pendingTransactions = new CopyOnWriteArrayList<>();
    private volatile int N = 0;

    public synchronized int getBalance(String user) {
        int balance = 100;
        for (Block block : chain) {
            if (("miner" + block.getCreatedByMinerId()).equals(user)) {
                balance += 100;
            }
            for (Transaction tx : block.getTransactions()) {
                if (tx.getFrom().equals(user)) {
                    balance -= tx.getAmount();
                }
                if (tx.getTo().equals(user)) {
                    balance += tx.getAmount();
                }
            }
        }
        for (Transaction tx : pendingTransactions) {
            if (tx.getFrom().equals(user)) {
                balance -= tx.getAmount();
            }
        }
        return balance;
    }

    public boolean addTransaction(Transaction transaction) {
        if (getBalance(transaction.getFrom()) >= transaction.getAmount()) {
            pendingTransactions.add(transaction);
            return true;
        }
        return false;
    }

    public List<Transaction> getPendingTransactions() {
        List<Transaction> transactions = new ArrayList<>(pendingTransactions);
        this.pendingTransactions.removeAll(transactions);
        return transactions;
    }

    public synchronized boolean addBlock(Block newBlock) {
        if (chain.size() >= 15) {
            return false;
        }
        String previousHash = chain.isEmpty() ? "0" : getLatestBlock().getHash();
        if (!previousHash.equals(newBlock.getPreviousHash())) {
            newBlock.getTransactions().forEach(this::addTransaction); // Повертаємо транзакції
            return false;
        }
        String target = (N > 0) ? new String(new char[N]).replace('\0', '0') : "";
        if (N > 0 && !newBlock.getHash().startsWith(target)) {
            return false;
        }

        chain.add(newBlock);
        printBlock(newBlock);
        adjustDifficulty(newBlock);
        return true;
    }

    private void adjustDifficulty(Block block) {
        long generationTime = block.getGenerationTime();
        if (generationTime < 10 && N < 5) {
            N++;
            System.out.println("N was increased to " + N + "\n");
        } else if (generationTime > 60 && N > 0) {
            N--;
            System.out.println("N was decreased by 1" + "\n");
        } else {
            System.out.println("N stays the same\n");
        }
    }

    private void printBlock(Block block) {
        System.out.println("Block:");
        System.out.println("Created by: miner" + block.getCreatedByMinerId());
        System.out.println(block.getMinerRewardInfo());
        System.out.println("Id: " + block.getId());
        System.out.println("Timestamp: " + block.getTimestamp());
        System.out.println("Magic number: " + block.getMagicNumber());
        System.out.println("Hash of the previous block:\n" + block.getPreviousHash());
        System.out.println("Hash of the block:\n" + block.getHash());
        System.out.println("Block data:\n" + block.getTransactionDataString());
        System.out.println("Block was generating for " + block.getGenerationTime() + " seconds");
    }

    public synchronized Block getLatestBlock() {
        return chain.isEmpty() ? null : chain.get(chain.size() - 1);
    }

    public synchronized int getChainSize() {
        return chain.size();
    }

    public int getN() {
        return N;
    }
}