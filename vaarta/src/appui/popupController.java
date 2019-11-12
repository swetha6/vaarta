package appui;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import logic.Feed;
import logic.Parser;

public class popupController {
    @FXML
    public Button URLok;
    @FXML
    public TextField EnterUrl;
    @FXML
    public AnchorPane anchor;
    private ObservableList<Feed> feedList;

    public void initdata(ObservableList<Feed> feedList) {
        this.feedList = feedList;
    }

    public void handleOK(ActionEvent actionEvent) {
        if (EnterUrl.getText() != null && !EnterUrl.getText().isEmpty()) {
            String s = EnterUrl.getText();
            System.out.println(s);
            Data.getInstance().addFeed(s);
            Data.getInstance().writeProperties();
            Parser p = new Parser(Data.getInstance().getFeeds().get(Data.getInstance().getFeeds().size() - 1),
                    Data.getInstance().getMergeStrategy());
            Feed f = p.call();
            Platform.runLater(() -> feedList.add(f));
        }
        Stage stage = (Stage) URLok.getScene().getWindow();
        stage.close();
    }
}
