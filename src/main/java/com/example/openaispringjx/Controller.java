package com.example.openaispringjx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


@org.springframework.stereotype.Controller
public class Controller {

    @FXML
    private Button button;

    @FXML
    private Label label;


    @FXML
    public void initialize() {
        // Add a mouse click listener to the button
        button.setOnAction(actionEvent -> label.setText("Text from button click"));
    }


    public void handleButtonClick(ActionEvent actionEvent) {
        System.out.println("Button clicked");
    }
}
