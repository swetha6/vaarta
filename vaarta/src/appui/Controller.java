package appui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import logic.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.*;


public class Controller {
    static final int QUEUE_SIZE = 100;
    static final int TIMEOUT_SECS = 5;
    static final int QUICK_TIMEOUT = 1;
    long startTime;
    private ArrayBlockingQueue<List<Item>> mergeQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
    static Thread mergeThread;
    static Thread loadThread;
    private ObservableList<Feed> feedList = FXCollections.observableArrayList();
    private ObservableList<Item> itemList = FXCollections.observableArrayList();
    @FXML
    private ListView<Feed> feedListView;
    @FXML
    private ListView<Item> itemListView;
    @FXML
    private TextArea body;


    public void initialize() {

        AnchorPane.setLeftAnchor(feedListView, 50.0);
        AnchorPane.setRightAnchor(itemListView, 50.0);
        AnchorPane.setLeftAnchor(itemListView, 500.0);
//        AnchorPane.setBottomAnchor(feedListView,500.0);
        AnchorPane.setTopAnchor(feedListView, 50.0);
//        AnchorPane.setBottomAnchor(body,100.0);
        AnchorPane.setRightAnchor(body, 50.0);
        AnchorPane.setLeftAnchor(body, 500.0);
        feedListView.setItems(feedList);
        feedListView.setCellFactory(new Callback<ListView<Feed>, ListCell<Feed>>() {
            @Override
            public ListCell<Feed> call(ListView<Feed> p) {
                return new ListCell<Feed>() {
                    @Override
                    protected void updateItem(Feed t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getTitle());
                        }
                    }
                };
            }
        });
        startTime = System.currentTimeMillis();

        if (Data.getInstance().getMergeStrategy() == Strategy.SINGLE_THREADED) {
            simpleLoadData();
        } else {
            loadThread = new Thread(this::loadData);
            loadThread.start();

            if (Data.getInstance().getMergeStrategy() == Strategy.MULTI_CONCURRENT) {
                mergeThread = new Thread(this::concurrentMerge);
            } else {
                mergeThread = new Thread(this::iterativeMerge);
            }

            mergeThread.start();
        }
    }

    private void simpleLoadData() {
        feedList.add(Data.getInstance().getFeeds().get(0));
        for (int i = 1; i < Data.getInstance().getFeeds().size(); i++) {
            Parser p = new Parser(Data.getInstance().getFeeds().get(i), Data.getInstance().getMergeStrategy());
            Feed f = p.call();
            Platform.runLater(() -> feedList.add(f));
        }

        PriorityQueue<Item> pq = new PriorityQueue<>();
        for (int i = 1; i < Data.getInstance().getFeeds().size(); i++) {
            pq.addAll(Data.getInstance().getFeeds().get(i).getItems());
        }
        System.out.println("Simple merge time: " + (System.currentTimeMillis() - startTime));

        System.out.println("Total feed size: " + pq.size());
        Data.getInstance().getFeeds().get(0).getItems().clear();
        Data.getInstance().getFeeds().get(0).getItems().addAll(pq);
    }

    private void loadData() {
        ExecutorCompletionService<Feed> ecs = new ExecutorCompletionService<>(CommonPool.getExecutor());
        for (int i = 1; i < Data.getInstance().getFeeds().size(); i++) {
            ecs.submit(new Parser(Data.getInstance().getFeeds().get(i), Data.getInstance().getMergeStrategy()));
        }

        Future<Feed> f;
        feedList.add(Data.getInstance().getFeeds().get(0));
        try {
            while ((f = ecs.poll(TIMEOUT_SECS, TimeUnit.SECONDS)) != null) {
                Feed feed = f.get();
                if (feed != null) {
                    if (Data.getInstance().getMergeStrategy() == Strategy.MULTI_ITERATIVE) {
                        mergeQueue.add(feed.getItems());
                    }
                    Platform.runLater(() -> feedList.add(feed));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void concurrentMerge() {
        Feed af = Data.getInstance().getFeeds().get(0);
        PriorityBlockingQueue<Item> mergeHeap = CommonPool.getMergeHeap();
        try {
            ArrayList<Item> newList;
            while (!Thread.currentThread().isInterrupted()) {
                newList = new ArrayList<>();
                Item i = mergeHeap.poll(QUICK_TIMEOUT, TimeUnit.SECONDS);
                if (i == null) {
                    continue;
                }
                newList.add(i);
                System.out.println("Concurrent merge time: " + (System.currentTimeMillis() - startTime));
                mergeHeap.drainTo(newList);
                mergeToFeed(af, newList);
            }
        } catch (Exception ex) {
            if (!(ex instanceof InterruptedException)) {
                ex.printStackTrace();
            }
        }
    }

    private void iterativeMerge() {
        Feed af = Data.getInstance().getFeeds().get(0);
        try {
            ArrayList<Item> newList;
            while (!Thread.currentThread().isInterrupted()) {
                List<Item> l = mergeQueue.poll(QUICK_TIMEOUT, TimeUnit.SECONDS);
                if (l == null) {
                    continue;
                }
                newList = new ArrayList<>(l);
                System.out.println("Iterative merge time: " + (System.currentTimeMillis() - startTime));
                mergeToFeed(af, newList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mergeToFeed(Feed af, ArrayList<Item> newList) {
        af.lock();
        ArrayList<Item> oldList = new ArrayList<>(af.getItems());
        List<Item> merged = new ArrayList<>();
        Merge.simpleMerge(oldList, newList, merged);
        System.out.println("Total feed size: " + merged.size());
        Platform.runLater(() -> af.replaceAll(merged));
    }

    public void displayEntry(MouseEvent mouseEvent) {
        itemListView.getItems().clear();
        List<Item> temp = feedListView.getSelectionModel().getSelectedItem().getItems();
        for (Item i : temp) {
            itemList.addAll(i);
        }
        itemListView.setItems(itemList);
        itemListView.setCellFactory(new Callback<ListView<Item>, ListCell<Item>>() {
            @Override
            public ListCell<Item> call(ListView<Item> p) {
                return new ListCell<Item>() {
                    @Override
                    protected void updateItem(Item t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getTitle());
                        }
                    }
                };
            }
        });
    }

    public void displayBody(MouseEvent mouseEvent) {
        body.clear();
        body.setWrapText(true);
        String temp = itemListView.getSelectionModel().getSelectedItem().getDescription();
        body.setText(temp);
    }

    public void addFeedsPopup(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("popup.fxml"));
        Parent root = fxmlLoader.load();
        Stage stage = new Stage();
        stage.setMinWidth(400.0);
        stage.setMinHeight(200.0);
        stage.setTitle("Popup window");
        stage.setScene(new Scene(root));
        stage.show();
        popupController controller = fxmlLoader.getController();
        controller.initdata(feedList);
    }
}