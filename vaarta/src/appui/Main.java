package appui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import logic.CommonPool;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setMinWidth(1200.0);
        primaryStage.setMinHeight(850.0);
        Parent root = FXMLLoader.load(getClass().getResource("mainwindow.fxml"));
        primaryStage.setTitle("Vaarta");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        try {
            Controller.loadThread.join();
        } catch (Exception e) {
            Controller.loadThread.interrupt();
            e.printStackTrace();
        }
        Controller.mergeThread.interrupt();
        CommonPool.getExecutor().shutdownNow();
    }
}
