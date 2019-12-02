package com.scrap.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Parent root = loader.load();
        UIController controller = loader.getController();
        controller.setWindow(primaryStage);


        primaryStage.setTitle("Twitter Scrapper");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }
}
