# Java Cryptocurrency Blockchain Simulation

This project is a desktop application developed in Java that simulates the core principles of a cryptocurrency blockchain. It provides a real-time, multi-threaded environment where virtual "miners" compete to create new blocks, validate transactions, and earn rewards.

The key to its uniqueness is the implementation of a **concurrent mining system** combined with **dynamic difficulty adjustment**. Multiple `Miner` threads run in parallel, simulating a decentralized network. The `Blockchain` class acts as a central authority that validates incoming blocks and adjusts the mining difficulty (`N` — the required number of leading zeros in a hash) based on the time it took to generate the previous block. This mimics how real-world blockchains like Bitcoin maintain a stable block creation rate.

This project showcases how fundamental concepts like Proof-of-Work, consensus mechanisms, and transaction validation can be implemented from scratch to create a functional cryptocurrency model.

---

### ⛓️ Table of Contents

-   [Project Structure](#project-structure)
-   [Features](#features)
-   [How It Works](#how-it-works)
-   [Technical Dependencies](#technical-dependencies)
-   [License](#license)

---

## Project Structure

```bash
.
└── src/
    ├── Block.java
    ├── Blockchain.java
    ├── Main.java
    ├── Miner.java
    ├── StringUtil.java
    └── Transaction.java
```

* **`Main.java`**: The entry point. Initializes the blockchain and starts the miner and user simulation threads.
* **`Blockchain.java`**: The core class. Manages the chain of blocks, validates new blocks, handles pending transactions (mempool), calculates user balances, and adjusts mining difficulty.
* **`Block.java`**: Defines the structure of a single block, including its data (transactions, rewards) and metadata (ID, timestamp, hashes).
* **`Miner.java`**: A `Runnable` class representing a single miner. Competes with other miners to find the next valid block.
* **`Transaction.java`**: A simple class representing a financial transaction between two parties.
* **`StringUtil.java`**: A utility class for generating SHA-256 hashes.

## Features

* **Concurrent Mining**: Simulates a real-world network with multiple miners (Java `Threads`) competing to solve the cryptographic puzzle and create the next block.
* **Proof-of-Work (PoW)**: Miners must find a "magic number" that, when combined with block data, produces a hash with a specific number of leading zeros. This process is computationally intensive.
* **Dynamic Difficulty Adjustment**: The blockchain automatically increases or decreases the required number of leading zeros (`N`) to maintain a stable block generation time, regardless of the number of active miners.
* **Transaction and Wallet Simulation**: Supports basic financial transactions. The system validates transactions by checking the sender's balance before adding them to the mempool. Every user starts with a balance of 100 virtual coins (VC).
* **Miner Rewards**: The miner who successfully creates a new block is automatically awarded 100 VC, which is recorded as the first piece of data in the new block.

## How It Works

The project is a multi-threaded simulation orchestrated by the `Main` class.

### The Mining Race

Upon startup, an `ExecutorService` creates and runs multiple `Miner` threads. Each miner enters a loop where it:

1.  Fetches the latest block and the current difficulty (`N`) from the central `Blockchain` instance.
2.  Pulls pending transactions from the blockchain's "mempool."
3.  Creates a new block candidate containing these transactions.
4.  Starts the Proof-of-Work process by calling `mineBlock(difficulty)`. This method iteratively increments a `magicNumber` and recalculates the block's hash until it finds one that starts with `N` leading zeros.

```java
// Inside Miner.java's run() method
Block lastBlock = blockchain.getLatestBlock();
int difficulty = blockchain.getN();
List<Transaction> transactions = blockchain.getPendingTransactions();

Block newBlock = new Block(nextId, new Date().getTime(), previousHash, transactions);
newBlock.mineBlock(difficulty); // Computationally intensive work

```

### Validation and Consensus

The first miner who finds a valid hash submits the block to the `Blockchain` by calling the `addBlock` method.  
This method is declared as `synchronized` to prevent **race conditions** and ensure that only one thread can add a block to the chain at a time.

```java
// Inside Blockchain.java
public synchronized boolean addBlock(Block newBlock) {
    // 1. Verify that the block continues the chain
    if (!previousHash.equals(newBlock.getPreviousHash())) {
        return false;
    }
    // 2. Verify the Proof-of-Work
    if (N > 0 && !newBlock.getHash().startsWith(target)) {
        return false;
    }
    // 3. Add the block if all checks pass
    chain.add(newBlock);
    return true;
}

```
If the block is accepted:

- It becomes part of the **official blockchain**  
- The miner who submitted it **is rewarded**  
- All other miners must **discard their current work** and start mining the next block

## Technical Dependencies

- **Java Development Kit (JDK 8+)**  
- **Java Concurrency** (`java.util.concurrent`)  
- **Java Cryptography** (`java.security.MessageDigest`)  

## **License**

This project is owned by **Adazhii Kristina**.
