package com.xadazhii.simulator;

public class ProofOfWork implements Runnable {
    private final Block block;
    private final int difficulty;
    private final MinerStatusUpdate statusUpdater;
    private final java.util.function.Consumer<Block> onBlockMined;

    public ProofOfWork(Block block, int difficulty, MinerStatusUpdate updater, java.util.function.Consumer<Block> onBlockMined) {
        this.block = block;
        this.difficulty = difficulty;
        this.statusUpdater = updater;
        this.onBlockMined = onBlockMined;
    }

    @Override
    public void run() {
        String target = new String(new char[difficulty]).replace('\0', '0');
        block.hash = block.calculateHash();

        while (!block.hash.substring(0, difficulty).equals(target) && !Thread.currentThread().isInterrupted()) {
            block.nonce++;
            block.hash = block.calculateHash();
            if (block.nonce % 1000 == 0) {
                statusUpdater.update(block.nonce, block.hash);
            }
        }

        if (!Thread.currentThread().isInterrupted()) {
            statusUpdater.update(block.nonce, block.hash);
            onBlockMined.accept(block);
        }
    }
}