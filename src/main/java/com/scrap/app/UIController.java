package com.scrap.app;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UIController {

    @FXML
    private CheckBox nameCheck;
    @FXML
    private CheckBox profileCheck;
    @FXML
    private CheckBox locationCheck;
    @FXML
    private CheckBox startDateCheck;
    
    @FXML
    private CheckBox tweetsCheck;
    @FXML
    private CheckBox followingCheck;
    @FXML
    private CheckBox followersCheck;
    @FXML
    private CheckBox likesCheck;

    @FXML
    private Label pathLabel;
    @FXML
    private Label progressLabel;

    @FXML
    private ProgressBar progressBar;

    private Path selectedPath;
    private Window window;

    @FXML
    private void openList(){
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(window);
        if (file != null){
            selectedPath = file.toPath();
            pathLabel.setText(selectedPath.toString());
            pathLabel.setVisible(true);
        }

    }

    @FXML
    private void startMining(){
        if (selectedPath != null){
            progressBar.setVisible(true);
            progressBar.setProgress(0);
            progressLabel.setVisible(true);
            progressLabel.setText("0%");

            new Thread(() -> {
                Scrapper scrapper = new Scrapper(selectedPath, getSelection());
                while (scrapper.hasNext()){
                    float progress = scrapper.getNext();
                    Platform.runLater(() -> {
                        progressBar.setProgress(progress);
                        progressLabel.setText((int)(progress * 100) + "%");
                    });
                }

                scrapper.saveDocument();

                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Concluído.");
                    alert.setHeaderText("A mineração terminou.");
                    alert.setContentText("O arquivo csv está salvo na mesma pasta\ndo arquivo de usuários.");
                    alert.getDialogPane().setMinHeight(Region.USE_COMPUTED_SIZE);
                    alert.showAndWait();
                });


            }).start();

        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING, "Selecione uma lista de usuários para começar.");
            alert.setHeaderText(null);
            alert.setTitle("Alerta");
            alert.showAndWait();

        }
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    private Map<String, Boolean> getSelection(){
        Map<String, Boolean> map = new HashMap<>();
        map.put("name", nameCheck.isSelected());
        map.put("profile", profileCheck.isSelected());
        map.put("location", locationCheck.isSelected());
        map.put("date", startDateCheck.isSelected());

        map.put("tweets", tweetsCheck.isSelected());
        map.put("following", followingCheck.isSelected());
        map.put("followers", followersCheck.isSelected());
        map.put("likes", likesCheck.isSelected());

        return map;
    }
}
