package com.homelink.coronatracker.connection;

import com.homelink.coronatracker.model.LocationStats;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class HttpsClient {

    public static void main(String[] args) {
        try {
            HttpsClient httpsClient = new HttpsClient();
            HashMap<String, String> requestProperties = new HashMap<>();
//            requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            requestProperties.put("x-rapidapi-host", "coronavirus-monitor.p.rapidapi.com");
            requestProperties.put("x-rapidapi-key", "1dbdf75773msh5d8ff64d5df24e3p1783d1jsn46f186225dfe");
            httpsClient.printJson("https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_country.php", requestProperties);
//            httpsClient.printJson("https://corona.lmao.ninja/all");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private HttpsURLConnection connect2URL(String paramURL, HashMap<String, String> requestProperties) throws IOException {
        URL url;

            url = new URL(paramURL);
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection)url.openConnection();
            if(requestProperties != null && !requestProperties.isEmpty())
                requestProperties.forEach((k,v) -> httpsURLConnection.addRequestProperty(k,v));

        return httpsURLConnection;
    }

    private void printJson(String url, HashMap<String, String> requestProperties) throws IOException {

        HttpsURLConnection con = connect2URL(url, requestProperties);

        JsonParser parser = JsonParserFactory.getJsonParser();
        BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String json = reader.readLine();
        ArrayList<Map<String, String>> list = (ArrayList) (parser.parseMap(json).get("countries_stat"));
//        List<?> list = parser.parseList(json);
        list.forEach(item ->
                {
                    System.out.println("------------------");
                    Map<String, ?> map = (Map<String, ?>) item;
                    map.forEach((k,v) -> System.out.println(k+" : "+v));
                });
        System.out.println("Total Confirmed : " + list.stream().map(x -> Integer.parseInt(x.get("cases").replace(",",""))).reduce((i, x) -> i+x));

    }

    private void printContent(String url, HashMap<String, String> requestProperties) throws IOException {
        HttpsURLConnection con = connect2URL(url, requestProperties);
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

    public BufferedReader getContent(String url, HashMap<String, String> requestProperties) throws IOException {
        HttpsURLConnection con = connect2URL(url, requestProperties);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
           return reader;
    }

}
