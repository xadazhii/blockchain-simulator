# Blockchain Simulator

**Blockchain Simulator** is an educational desktop application built with **Java** and **JavaFX**. It visualizes the internal workings of a blockchain in real-time, demonstrating cryptographic principles, transaction lifecycles, and the **Proof of Work (PoW)** consensus algorithm.

Unlike static diagrams, this simulator performs actual mining using **SHA-256** hashing and secures transactions with **ECDSA** signatures.

### **Table of Contents**
- [Visual Overview](#-visual-overview)
- [Project Structure](#project-structure)
- [Key Features](#key-features)
- [How It Works](#how-it-works)
- [Tech Stack](#tech-stack)
- [Getting Started](#getting-started)

---

## **Visual Overview**

> *See the simulator in action: creating transactions and mining blocks.*

<img width="1388" height="880" alt="Screenshot 2025-12-09 at 21 58 51" src="https://github.com/user-attachments/assets/189cffb1-2073-41ce-8459-027cc0d7e129" />
<img width="781" height="375" alt="Screenshot 2025-12-09 at 21 59 02" src="https://github.com/user-attachments/assets/ff100413-9348-4dcb-8cfe-8b31b3694123" />

---

## **Project Structure**

The project is modularized into **UI** (`application`) and **Core Logic** (`simulator`).

```bash
blockchain-simulator/
├── application/                  # JavaFX UI Module
│   ├── src/main/java/com/xadazhii/application/
│   │   ├── App.java              # Entry point
│   │   ├── Controller.java       # Main UI logic
│   │   └── BlockDetailsController.java
│   └── pom.xml
├── simulator/                    # Backend Logic Module
│   ├── src/main/java/com/xadazhii/simulator/
│   │   ├── Block.java            # Block structure & hashing
│   │   ├── Blockchain.java       # Chain management
│   │   ├── Miner.java            # Mining worker
│   │   ├── ProofOfWork.java      # Consensus algorithm
│   │   ├── Transaction.java      # ECDSA signed transfers
│   │   └── Wallet.java           # Key pair management
│   └── pom.xml
└── pom.xml                       # Parent POM

## Key Features

```

* ** Real-Time Mining:** Watch the `Nonce` update live as the miner solves the cryptographic puzzle.
* ** Digital Signatures:** Simulates "Alice" and "Bob" wallets. Every transaction is signed with a Private Key and verified with a Public Key.
* ** Block Inspection:** Drill down into mined blocks to view hashes, timestamps, and transaction data.
* ** Step-by-Step UI:** The interface breaks the lifecycle into 7 stages: *Request → Create → Broadcast → Validate → Reward → Add → Complete*.

## How It Works

### 1. The Consensus (Proof of Work)
The simulator mimics Bitcoin's mining process. A `Miner` must find a hash for the block that starts with a specific number of zeros (Difficulty). 

> **Logic:** `SHA-256(PrevHash + Data + Timestamp + Nonce) < Target`

### 2. Multithreading
Mining is computationally intensive. The application uses `ExecutorService` to run the `ProofOfWork` task on a background thread, keeping the JavaFX interface responsive.

### 3. Cryptography
* **SHA-256:** Used for linking blocks (immutability).
* **Elliptic Curve (ECDSA):** Used for wallet security and transaction authorization. 

## Tech Stack

* **Language:** Java 17+
* **GUI:** JavaFX (Modular)
* **Cryptography:** Bouncy Castle (`bcprov`)
* **Build Tool:** Maven

## Getting Started

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/blockchain-simulator.git](https://github.com/your-username/blockchain-simulator.git)
    ```

2.  **Build the project:**
    ```bash
    mvn clean install
    ```

3.  **Run the application:**
    Navigate to the application module and run:
    ```bash
    cd application
    mvn javafx:run
    ```

---
**License:** This project is owned by *Adazhii Kristina*.

