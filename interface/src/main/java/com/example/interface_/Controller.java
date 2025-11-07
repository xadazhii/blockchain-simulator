package com.example.interface_;

import com.example.server.*;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Controller {
    @FXML private TextArea logArea;
    @FXML private ListView<String> walletsListView;
    @FXML private ComboBox<String> senderComboBox;
    @FXML private ComboBox<String> recipientComboBox;
    @FXML private TextField amountField;
    @FXML private Button sendButton;
    @FXML private VBox step1_Request, step2_CreateBlock, step3_Broadcast,
            step4_Validate, step5_Reward, step6_AddBlock, step7_Complete;
    @FXML private Label powNonceLabel;
    @FXML private Label powHashLabel;
    @FXML private ListView<String> pendingTxListView;
    private List<Node> steps;
    private Wallet aliceWallet;
    private Wallet bobWallet;
    private Blockchain blockchain;
    private ExecutorService minerExecutor;

    @FXML
    public void initialize() {
        blockchain = new Blockchain(4);
        aliceWallet = new Wallet();
        bobWallet = new Wallet();
        minerExecutor = Executors.newSingleThreadExecutor();
        steps = List.of(step1_Request, step2_CreateBlock, step3_Broadcast,
                step4_Validate, step5_Reward, step6_AddBlock, step7_Complete);
        senderComboBox.setItems(FXCollections.observableArrayList("Alice", "Bob"));
        recipientComboBox.setItems(FXCollections.observableArrayList("Alice", "Bob"));
        sendButton.setOnAction(event -> startTransactionLifecycle());
        updateWalletsUI();
    }

    private void startTransactionLifecycle() {
        String senderName = senderComboBox.getValue();
        String recipientName = recipientComboBox.getValue();
        if (senderName == null || recipientName == null || senderName.equals(recipientName)) {
            log("Invalid selection."); return;
        }
        try {
            int amount = Integer.parseInt(amountField.getText());
            Wallet senderWallet = senderName.equals("Alice") ? aliceWallet : bobWallet;
            PublicKey recipientKey = recipientName.equals("Alice") ? aliceWallet.publicKey : bobWallet.publicKey;
            Transaction tx = new Transaction(senderWallet, recipientKey, amount);
            if (!tx.isSignatureValid()) { log("Invalid signature!"); return; }

            resetSteps();
            sendButton.setDisable(true);

            setActiveStep(step1_Request, () -> showTransactionDetails(tx));
            log("1. Transaction requested. Signature is valid.");
            blockchain.addTransaction(tx);
            updatePendingTxList();

            runAfter(1500, () -> {
                Block blockCandidate = blockchain.createBlockCandidate();
                setActiveStep(step2_CreateBlock, () -> showBlockDetails(blockCandidate, "Block Candidate Details (Not Mined)"));
                log("2. New block candidate created.");
                runAfter(1500, () -> {
                    setActiveStep(step3_Broadcast, () -> showBroadcastDetails());
                    log("3. Broadcasting block to miner...");
                    runAfter(1000, () -> {
                        setActiveStep(step4_Validate, null);
                        Miner miner = new Miner(blockchain,
                                minedBlock -> {
                                    Platform.runLater(() -> {
                                        blockchain.addMinedBlock(minedBlock);
                                        setActiveStep(step5_Reward, () -> showBlockDetails(minedBlock, "Mined Block Details"));
                                        log("4. Miner validated block. Final Nonce: " + minedBlock.nonce);
                                        runAfter(1500, () -> {
                                            setActiveStep(step6_AddBlock, () -> showBlockDetails(minedBlock, "Mined Block Details"));
                                            log("5. Block #" + minedBlock.id + " added to blockchain.");
                                            runAfter(1500, () -> {
                                                setActiveStep(step7_Complete, () -> showTransactionDetails(tx));
                                                log("6. Transaction complete. Balances updated.");
                                                updateUI();
                                                sendButton.setDisable(false);
                                            });
                                        });
                                    });
                                },
                                this::updatePowStatus
                        );
                        minerExecutor.submit(miner);
                    });
                });
            });
            amountField.clear();
        } catch (NumberFormatException e) { log("Invalid amount."); }
    }

    private void setActiveStep(Node activeStep, Runnable onClickAction) {
        steps.forEach(step -> step.getStyleClass().remove("step-active"));
        activeStep.getStyleClass().add("step-active");

        if (onClickAction != null) {
            activeStep.setOnMouseClicked(event -> onClickAction.run());
            activeStep.setStyle("-fx-cursor: hand;");
        }

        ScaleTransition st = new ScaleTransition(Duration.millis(200), activeStep);
        st.setFromX(1.0); st.setFromY(1.0);
        st.setToX(1.05); st.setToY(1.05);
        st.setAutoReverse(true); st.setCycleCount(2);
        st.play();
    }

    private void resetSteps() {
        steps.forEach(step -> {
            step.getStyleClass().remove("step-active");
            step.setOnMouseClicked(null);
            step.setStyle("-fx-cursor: default;");
        });
        logArea.clear();
        powNonceLabel.setText("-");
        powHashLabel.setText("-");
        powHashLabel.setStyle("-fx-text-fill: #8b949e;");
    }

    private void showTransactionDetails(Transaction tx) {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Sender Key:", StringUtil.getKeyString(tx.sender));
        details.put("Recipient Key:", StringUtil.getKeyString(tx.recipient));
        details.put("Amount:", tx.amount + " VC");
        details.put("Signature:", Base64.getEncoder().encodeToString(tx.signature));
        details.put("Signature Valid:", String.valueOf(tx.isSignatureValid()));
        showDetailsWindow("Transaction Details", details);
    }

    private void showBlockDetails(Block block, String title) {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Block ID:", String.valueOf(block.id));
        details.put("Timestamp:", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(block.timestamp)));
        details.put("Nonce (Magic #):", String.valueOf(block.nonce));
        details.put("Transactions:", String.valueOf(block.transactions.size()));
        details.put("Previous Hash:", block.previousHash);
        details.put("Block Hash:", (block.hash != null ? block.hash : "(not yet calculated)"));
        showDetailsWindow(title, details);
    }

    private void showBroadcastDetails() {
        Map<String, String> details = new LinkedHashMap<>();
        details.put("Action:", "The block candidate is sent to a miner.");
        details.put("Next Step:", "The miner will begin the Proof-of-Work process to find a valid hash.");
        showDetailsWindow("Broadcast Details", details);
    }

    private void showDetailsWindow(String title, Map<String, String> data) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("details-view.fxml"));
            VBox page = loader.load();

            Stage detailsStage = new Stage();
            detailsStage.setTitle("Details");
            detailsStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(page);
            scene.getStylesheets().add(App.class.getResource("style.css").toExternalForm());
            detailsStage.setScene(scene);

            DetailsController controller = loader.getController();
            controller.displayDetails(title, data);

            detailsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            log("Error: Could not open details window.");
        }
    }

    private void updatePowStatus(int nonce, String hash) {
        Platform.runLater(() -> {
            powNonceLabel.setText(String.valueOf(nonce));
            String target = new String(new char[blockchain.getDifficulty()]).replace('\0', '0');
            if (hash.startsWith(target)) {
                powHashLabel.setStyle("-fx-text-fill: #7ed321;");
            } else {
                powHashLabel.setStyle("-fx-text-fill: #8b949e;");
            }
            powHashLabel.setText(hash.substring(0, 20) + "...");
        });
    }

    private void runAfter(long millis, Runnable continuation) {
        PauseTransition pause = new PauseTransition(Duration.millis(millis));
        pause.setOnFinished(event -> continuation.run());
        pause.play();
    }

    private void updateUI() {
        updateWalletsUI();
        updatePendingTxList();
    }

    private void updateWalletsUI() {
        Map<String, Integer> balances = blockchain.getBalances();
        String aliceKey = StringUtil.getKeyString(aliceWallet.publicKey);
        String bobKey = StringUtil.getKeyString(bobWallet.publicKey);
        walletsListView.getItems().setAll(
                String.format("Alice: %d VC (Key: %s...)", balances.getOrDefault(aliceKey, 100), aliceKey.substring(0, 15)),
                String.format("Bob: %d VC (Key: %s...)", balances.getOrDefault(bobKey, 100), bobKey.substring(0, 15))
        );
    }

    private void updatePendingTxList() {
        pendingTxListView.getItems().setAll(
                blockchain.getPendingTransactions().stream()
                        .map(Transaction::toString)
                        .collect(Collectors.toList())
        );
    }

    private void log(String message) { logArea.appendText(message + "\n"); }
    public void shutdown() { if (minerExecutor != null) minerExecutor.shutdownNow(); }
}