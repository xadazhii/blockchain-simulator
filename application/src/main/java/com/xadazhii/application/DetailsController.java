package com.xadazhii.application;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import java.util.Map;

public class DetailsController {
    @FXML private Label titleLabel;
    @FXML private GridPane detailsGrid;

    public void displayDetails(String title, Map<String, String> data) {
        titleLabel.setText(title);
        detailsGrid.getChildren().clear();
        int rowIndex = 0;
        for (Map.Entry<String, String> entry : data.entrySet()) {
            Label keyLabel = new Label(entry.getKey());
            keyLabel.getStyleClass().add("detail-label");

            Label valueLabel = new Label(entry.getValue());
            valueLabel.getStyleClass().add("detail-value-mono");
            valueLabel.setWrapText(true);

            detailsGrid.addRow(rowIndex++, keyLabel, valueLabel);
        }
    }
}