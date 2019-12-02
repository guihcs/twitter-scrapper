package com.scrap.app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Scrapper {

    private final Map<String, String> selectors = Map.of(
            "name", ".ProfileHeaderCard-nameLink",
            "profile", ".ProfileHeaderCard-bio",
            "location", ".ProfileHeaderCard-locationText > a:nth-child(1)",
            "date", ".ProfileHeaderCard-joinDateText",
            "tweets", "li.ProfileNav-item:nth-child(1) > a:nth-child(1) > span:nth-child(3)",
            "following", "li.ProfileNav-item:nth-child(2) > a:nth-child(1) > span:nth-child(3)",
            "followers", "li.ProfileNav-item:nth-child(3) > a:nth-child(1) > span:nth-child(3)",
            "likes", "li.ProfileNav-item:nth-child(4) > a:nth-child(1) > span:nth-child(3)"
    );

    private Path path;
    private int currentIndex;
    private Map<String, Boolean> selectionMap;
    private List<String> lines;
    private List<String> resultLines;
    private List<String> selectorOrder;

    public Scrapper(Path path, Map<String, Boolean> selectionMap) {
        this.path = path;
        this.selectionMap = selectionMap;

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultLines = new LinkedList<>();
        selectorOrder = new ArrayList<>();
        for (String key : this.selectionMap.keySet()) {
            if (this.selectionMap.get(key)) selectorOrder.add(key);
        }
    }



    public float getNext(){
        try {

            String user = lines.get(currentIndex++).replaceAll("@", "");
            Document doc = Jsoup.connect("https://twitter.com/" + user).get();
            StringBuilder builder = new StringBuilder();
            builder.append("\"").append(user).append("\",");
            for (String key : selectorOrder) {
                if (selectionMap.get(key)){
                    String selector = selectors.get(key);
                    Elements select = doc.body().select(selector);

                    if (select.hasAttr("data-count")){
                        String attr = select.attr("data-count");
                        builder.append(attr).append(",");
                    }else {
                        builder.append("\"").append(select.text()).append("\",");
                    }
                }
            }

            resultLines.add(builder.deleteCharAt(builder.length()-1).toString());

        } catch (IOException ignored) {

        }

        return (float)currentIndex / lines.size();
    }

    public boolean hasNext(){
        return currentIndex < lines.size();
    }

    public void saveDocument(){
        StringBuilder header = new StringBuilder();
        header.append("\"user\",");
        for (String s : selectorOrder) {
            header.append("\"").append(s).append("\",");
        }

        header.deleteCharAt(header.length()-1);

        resultLines.add(0, header.toString());
        Path newPath = path.getParent().resolve(path.getFileName().toString().replaceAll("\\..+", ".csv"));
        try {
            Files.write(newPath, resultLines);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
