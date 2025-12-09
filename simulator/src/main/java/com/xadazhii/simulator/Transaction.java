package com.xadazhii.simulator;

import java.security.PublicKey;
import java.util.Date;

public class Transaction {
    public String transactionId;
    public final PublicKey sender;
    public final PublicKey recipient;
    public final int amount;
    public final long timestamp;
    public final byte[] signature;

    public Transaction(Wallet fromWallet, PublicKey to, int amount) {
        this.sender = fromWallet.publicKey;
        this.recipient = to;
        this.amount = amount;
        this.timestamp = new Date().getTime();
        this.transactionId = calculateHash();
        this.signature = StringUtil.applyECDSASig(fromWallet.privateKey, getDataToSign());
    }

    public boolean isSignatureValid() {
        return StringUtil.verifyECDSASig(sender, getDataToSign(), signature);
    }

    public String calculateHash() {
        return StringUtil.applySha256(
                StringUtil.getKeyString(sender) +
                        StringUtil.getKeyString(recipient) +
                        amount +
                        timestamp
        );
    }

    private String getDataToSign() {
        return this.transactionId;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    @Override
    public String toString() {
        return String.format("ID: %s... From: %s... To: %s... Amount: %d",
                transactionId.substring(0, 10),
                StringUtil.getKeyString(sender).substring(0, 10),
                StringUtil.getKeyString(recipient).substring(0, 10),
                amount);
    }
}