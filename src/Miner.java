import java.util.Date;
import java.util.List;

public class Miner implements Runnable {
    private final Blockchain blockchain;
    private final int id;

    public Miner(int id, Blockchain blockchain) {
        this.id = id;
        this.blockchain = blockchain;
    }

    @Override
    public void run() {
        while (blockchain.getChainSize() < 15) {
            Block lastBlock = blockchain.getLatestBlock();
            int nextId = (lastBlock == null) ? 1 : lastBlock.getId() + 1;
            if (nextId > 15) break;

            String previousHash = (lastBlock == null) ? "0" : lastBlock.getHash();
            int difficulty = blockchain.getN();

            List<Transaction> transactions = blockchain.getPendingTransactions();
            Block newBlock = new Block(nextId, new Date().getTime(), previousHash, transactions);
            newBlock.setCreatedByMinerId(this.id);

            newBlock.mineBlock(difficulty);

            blockchain.addBlock(newBlock);
        }
    }
}