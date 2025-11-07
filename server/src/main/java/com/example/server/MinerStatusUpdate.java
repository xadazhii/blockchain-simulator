package com.example.server;

@FunctionalInterface
public interface MinerStatusUpdate {
    void update(int nonce, String hash);
}