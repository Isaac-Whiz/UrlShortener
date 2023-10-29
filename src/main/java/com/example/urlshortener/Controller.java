package com.example.urlshortener;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@org.springframework.stereotype.Controller
public class Controller {

    public Button btnShortenUrl;
    public TextField txtUrl;
    public TextField txtAliasUrl;
    public Button btnCopyUrl;
    public Label labelResult;
    public Pane panel;

    private String fullLink;
    private String aliasName;
    private int userDomain;
    private String key;

    private UrlShortenerResponse shortenerResponse;
    private Clipboard clipboard;
    private ClipboardContent content;


    @FXML
    public void initialize() {
        panel.setBackground(new Background( new BackgroundImage(
                new Image("/glob.jpg"),
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT)));
        initViews();
        handleEvents();

    }

    private void sendRequestAndGetResponse() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest
                .newBuilder()
                .uri(URI.create("https://cutt.ly/api/api.php?key=" + key + "&short=" +
                        fullLink
                        + "&name=" + aliasName + "&userDomain=" + userDomain))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> response = HttpClient
                .newHttpClient().
                send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper mapper = new ObjectMapper();
        mapper.readValue(response.body(), UrlShortenerResponse.class);
        validateStatus(shortenerResponse.getStatus());
        manageResults();
    }

    private void manageResults() {
        labelResult.setText(null);
        labelResult.setText(shortenerResponse.getShortLink());
    }

    private void resetViews() {
        txtUrl.setText(null);
        txtAliasUrl.setText(null);
    }

    private void initViews() {
        content = new ClipboardContent();
        shortenerResponse = new UrlShortenerResponse();
        clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
        fullLink = txtUrl.getText().trim();
        userDomain = 0;
        aliasName = txtAliasUrl.getText().trim();
        key = "ee7e1f3e3ff9ec71ad2f42e46696fcf45ec4d";
    }

    private void handleEvents() {
        btnShortenUrl.setOnAction(actionEvent ->
        {
            try {
                sendRequestAndGetResponse();
                resetViews();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        btnCopyUrl.setOnAction(actionEvent ->
                {
                    if (labelResult.getText() != null && !labelResult.getText().equals("--Shortened Url--")) {
                        content.putString(labelResult.getText());
                        clipboard.setContent(content);
                        showInformationAlert("Shortened link copied to clipboard.");
                    } else {
                        showInformationAlert("First enter the url to shorten please.");
                    }
                }
        );
    }

    private void showInformationAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void validateStatus(int status) {
        switch (status) {
            case 1:
                showInformationAlert("The link has already been shortened.");
                break;
            case 2:
                showErrorAlert("The entered link is not a link.");
                break;
            case 3:
                showInformationAlert("The preferred link name is already taken.");
                break;
            case 5:
                showErrorAlert("The link has not passed the validation. Includes invalid characters.");
                break;
            case 6:
                showErrorAlert("The link is from a blocked domain.");
                break;
            case 8:
                showInformationAlert("Thank you for using this app, " +
                        "contact the developer for an updated version");
                break;
            default:
                break;
        }
    }
}
