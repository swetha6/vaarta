package logic;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

public class Parser implements Callable<Feed> {

    private final Feed feed;
    private final Strategy mergeStrategy;
    private static final String CHANNEL = "channel";
    private static final String ITEM = "item";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String LINK = "link";
    private static final String PUB_DATE = "pubDate";

    private void fillItems(Document document, Feed feed) {
        document.getDocumentElement().normalize();
        NodeList nodeList = document.getElementsByTagName(Parser.CHANNEL);
        Node channelNode = nodeList.item(0);
        Element channelElement = (Element) channelNode;
        NodeList items = channelElement.getElementsByTagName(Parser.ITEM);
        feed.setTitle(channelElement.getElementsByTagName(Parser.TITLE).item(0).getTextContent());
        feed.setDescription(channelElement.getElementsByTagName(Parser.DESCRIPTION).item(0).getTextContent());
        for(int i = 0; i < items.getLength(); i++) {
            Node itemNode = items.item(i);
            Item item;
            try {
                Element itemElement = (Element) itemNode;
                String itemTitle = itemElement.getElementsByTagName(Parser.TITLE).item(0).getTextContent();
                String itemDescription = itemElement.getElementsByTagName(Parser.DESCRIPTION).item(0).getTextContent();
                itemDescription = itemDescription.replaceAll("(?s)<[^>]*>(\\s*<[^>]*>)*", "");
                String itemLink = itemElement.getElementsByTagName(Parser.LINK).item(0).getTextContent();
                LocalDateTime itemTime = LocalDateTime.from(DateTimeFormatter.RFC_1123_DATE_TIME
                        .parse(itemElement.getElementsByTagName(Parser.PUB_DATE).item(0).getTextContent()));
                item = new Item(itemTitle, itemDescription, itemLink, itemTime);
            } catch(Exception ex) {
//                System.out.println(feed.getUrl());
//                ex.printStackTrace();
                continue;
            }

            feed.addItem(item);
            if (mergeStrategy == Strategy.MULTI_CONCURRENT) {
                CommonPool.getMergeHeap().add(item);
            }
        }
    }

    public Parser(Feed feed, Strategy mergeStrategy) {
        this.feed = feed;
        this.mergeStrategy = mergeStrategy;
    }

    public Feed call() {
        Document document;
        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            document = builder.parse(new URL(feed.getUrl()).openStream());
        } catch(Exception exception) {
            System.out.println(feed.getUrl());
            exception.printStackTrace();
            return null;
        }

        feed.emptyItemsList();
        fillItems(document, feed);
        Sort.sortList(feed.getItems());
        return feed;
    }

}
