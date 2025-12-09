package com.xadazhii.application;

import com.xadazhii.simulator.Block;
import com.xadazhii.simulator.Transaction;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

public class BlockDetailsController {
    @FXML private Label headerLabel;
    @FXML private Label blockIdLabel;
    @FXML private Label timestampLabel;
    @FXML private Label nonceLabel;
    @FXML private Label hashLabel;
    @FXML private Label prevHashLabel;
    @FXML private ListView<String> transactionListView;

    public void setBlock(Block block) {
        headerLabel.setText("Block #" + block.id + " Details");
        blockIdLabel.setText(String.valueOf(block.id));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        timestampLabel.setText(sdf.format(new Date(block.timestamp)));

        nonceLabel.setText(String.valueOf(block.nonce));
        hashLabel.setText(block.hash);
        prevHashLabel.setText(block.previousHash);

        transactionListView.getItems().setAll(
                block.transactions.stream()
                        .map(Transaction::toString)
                        .collect(Collectors.toList())
        );
    }
}