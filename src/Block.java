import java.util.List;
import java.util.stream.Collectors;

class Block {
    private final int id;
    private final long timestamp;
    private final String previousHash;
    private String hash;
    private int magicNumber;
    private long generationTime;
    private int createdByMinerId;
    private final List<Transaction> transactions;
    private final String transactionDataString;
    private String minerRewardInfo;

    public Block(int id, long timestamp, String previousHash, List<Transaction> transactions) {
        this.id = id;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactions = transactions;
        if (transactions == null || transactions.isEmpty()) {
            this.transactionDataString = "No transactions";
        } else {
            this.transactionDataString = transactions.stream().map(Transaction::toString).collect(Collectors.joining("\n"));
        }
    }

    public String calculateHash() {
        String reward = (minerRewardInfo == null) ? "" : minerRewardInfo;
        String dataToHash = id + timestamp + previousHash + reward + transactionDataString + magicNumber;
        return StringUtil.applySha256(dataToHash);
    }

    public void mineBlock(int difficulty) {
        this.minerRewardInfo = "miner" + createdByMinerId + " gets 100 VC";
        long startTime = System.currentTimeMillis();
        String target = (difficulty > 0) ? new String(new char[difficulty]).replace('\0', '0') : "";
        this.hash = calculateHash();

        while (difficulty > 0 && !hash.startsWith(target)) {
            magicNumber++;
            this.hash = calculateHash();
        }

        long endTime = System.currentTimeMillis();
        this.generationTime = (endTime - startTime) / 1000;
    }

    public int getCreatedByMinerId() { return createdByMinerId; }
    public String getTransactionDataString() { return transactionDataString; }
    public List<Transaction> getTransactions() { return transactions; }
    public void setCreatedByMinerId(int minerId) { this.createdByMinerId = minerId; }
    public String getMinerRewardInfo() { return minerRewardInfo; }
    public int getMagicNumber() { return magicNumber; }
    public long getGenerationTime() { return generationTime; }
    public int getId() { return id; }
    public long getTimestamp() { return timestamp; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
}