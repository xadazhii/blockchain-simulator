package com.xadazhii.simulator;

@FunctionalInterface
public interface MinerStatusUpdate {
    void update(int nonce, String hash);
}