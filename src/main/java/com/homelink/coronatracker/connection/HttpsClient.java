package com.homelink.coronatracker.connection;

import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class HttpsClient {

    public static void main(String[] args) {
        try {
            HttpsClient httpsClient = new HttpsClient();
//            httpsClient.printJson("https://corona.lmao.ninja/all");
            httpsClient.printJson("https://corona.lmao.ninja/countries");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private HttpsURLConnection connect2URL(String paramURL) throws IOException {
        URL url;

            url = new URL(paramURL);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
            httpsURLConnection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        return httpsURLConnection;
    }

    private void printJson(String url) throws IOException {
        HttpsURLConnection con = connect2URL(url);
        JsonParser parser = JsonParserFactory.getJsonParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        List<?> list = parser.parseList(reader.readLine());
        list.forEach(item ->
                {
                    System.out.println("------------------");
                    Map<String, ?> map = (Map<String, ?>) item;
                    map.forEach((k,v) -> System.out.println(k+" : "+v));
                });

    }

    private void printContent(String url) throws IOException {
        HttpsURLConnection con = connect2URL(url);
        if(con!=null){
            BufferedReader br = null;
            try {

                System.out.println("****** Content of the URL ********");
                br = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));

                String input;

                while ((input = br.readLine()) != null){
                    System.out.println(input);

                }
                br.close();

            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            } finally {
                br.close();
            }

        }

    }

    public BufferedReader getContent(String url) throws IOException {
        HttpsURLConnection con = connect2URL(url);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
           return reader;
    }

}
