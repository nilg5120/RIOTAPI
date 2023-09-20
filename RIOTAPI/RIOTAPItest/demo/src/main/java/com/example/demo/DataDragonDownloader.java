package com.example.demo;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class DataDragonDownloader {

    private static final String VERSION = "13.18.1";  // サンプルバージョン、適宜更新してください

    public static void main(String[] args) throws Exception {
        JSONObject championsData = fetchChampionsData();
        Iterator<String> keys = championsData.getJSONObject("data").keys();
        
        while (keys.hasNext()) {
            String championKey = keys.next();
            downloadChampionImage(championKey);
        }
    }

    public static JSONObject fetchChampionsData() throws Exception {
        String championsUrl = "https://ddragon.leagueoflegends.com/cdn/" + VERSION + "/data/en_US/champion.json";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(championsUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String jsonStr = EntityUtils.toString(entity, StandardCharsets.UTF_8);
                    return new JSONObject(jsonStr);
                }
            }
        }
        return new JSONObject();
    }

    public static void downloadChampionImage(String championKey) throws Exception {
        String imageUrl = "https://ddragon.leagueoflegends.com/cdn/" + VERSION + "/img/champion/" + championKey + ".png";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(imageUrl);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (InputStream inputStream = entity.getContent()) {
                        String outputPath = "championart/" + championKey + ".png";
                        try (FileOutputStream outputStream = new FileOutputStream(outputPath)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
                EntityUtils.consume(entity);
            }
        }
    }
}
