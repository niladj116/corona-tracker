package com.homelink.coronatracker.services;

import com.homelink.coronatracker.connection.HttpsClient;
import com.homelink.coronatracker.model.Countries;
import com.homelink.coronatracker.model.LocationStats;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoronaRapidAPIServices {

    public static String DETAILED_CORONA_REPORT_URL = "https://coronavirus-monitor.p.rapidapi.com/coronavirus/cases_by_country.php";
    public static String DETAILED_BY_COUNTRY_REPORT_URL = "https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats";
//    https://covid-19-coronavirus-statistics.p.rapidapi.com/v1/stats


    List<LocationStats> sortedList = new ArrayList<>();

    @PostConstruct
//    @Scheduled(cron = "* * 0 ? * *")
    public List<LocationStats> getDetailedData() {
        List<LocationStats> sortedList = this.sortedList;
//        System.out.println("CoronaRapidAPIServices -> getDetailedData...");
        List<LocationStats> stats = new ArrayList<>();
        HttpsClient client = new HttpsClient();
        JsonParser parser = JsonParserFactory.getJsonParser();
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("x-rapidapi-host", "coronavirus-monitor.p.rapidapi.com");
        requestProperties.put("x-rapidapi-key", "1dbdf75773msh5d8ff64d5df24e3p1783d1jsn46f186225dfe");
        try {
            BufferedReader reader = client.getContent(DETAILED_CORONA_REPORT_URL,requestProperties);
            ArrayList<Map<String, String>> list = (ArrayList) (parser.parseMap(reader.readLine()).get("countries_stat"));
            list.forEach(item ->
            {
                Map<String ,String> map = (Map<String, String>)item;
                LocationStats locationStats = new LocationStats();
                locationStats.setCountry((String) map.get("country_name"));
//                System.out.println(locationStats.getCountry());
                String code = Countries.doubleBraceMap.get(String.valueOf(map.get("country_name")).toLowerCase());
                if (code != null)
                    locationStats.setCountryCode(code);
                else
                    locationStats.setCountryCode(Countries.doubleBraceMap.get("default"));

                locationStats.setTotalCases(Integer.parseInt(map.get("cases").replace(",","")));
                locationStats.setNewCases(Integer.parseInt(map.get("new_cases").replace(",","")));
                locationStats.setTotalDeathCases(Integer.parseInt(map.get("deaths").replace(",","")));
                locationStats.setNewDeathCases(Integer.parseInt(map.get("new_deaths").replace(",","")));
                locationStats.setTotalRecoveredCases(Integer.parseInt(map.get("total_recovered").replace(",","")));
                locationStats.setCriticalCases(Integer.parseInt(map.get("serious_critical").replace(",","")));
                stats.add(locationStats);
            });
            sortedList = stats.stream().sorted(Comparator.comparing(LocationStats::getTotalCases).reversed()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sortedList;
    }

}
