package com.xadazhii.simulator;

import java.util.function.Consumer;

public class Miner implements Runnable {
    private final Blockchain blockchain;
    private final Consumer<Block> onBlockMined;
    private final MinerStatusUpdate statusUpdater;

    public Miner(Blockchain blockchain, Consumer<Block> onBlockMined, MinerStatusUpdate statusUpdater) {
        this.blockchain = blockchain;
        this.onBlockMined = onBlockMined;
        this.statusUpdater = statusUpdater;
    }

    @Override
    public void run() {
        if (!blockchain.getPendingTransactions().isEmpty()) {
            Block blockCandidate = blockchain.createBlockCandidate();
            ProofOfWork pow = new ProofOfWork(blockCandidate, blockchain.getDifficulty(), statusUpdater, onBlockMined);
            pow.run();
        }
    }
}