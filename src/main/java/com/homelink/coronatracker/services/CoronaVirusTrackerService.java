package com.homelink.coronatracker.services;


import com.homelink.coronatracker.connection.HttpsClient;
import com.homelink.coronatracker.model.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CoronaVirusTrackerService {

    public static String SUMMARY_CORONA_REPORT_URL = "https://corona.lmao.ninja/all";
    public static String DETAILED_CORONA_REPORT_URL = "https://corona.lmao.ninja/countries";
    public static String HTTPS_CONFIRMED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    public static String HTTPS_DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";
    public static String HTTPS_RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";

    List<LocationStats> finalList = new ArrayList<>();
//        @PostConstruct
//    @Scheduled(cron = "* * 1 * * *")
    public List<LocationStats> fetchVirusData() {

            return getDetailedData();
        }





    public LocationStats getSummaryData() {
        LocationStats summary = new LocationStats();
        HttpsClient client = new HttpsClient();
        JsonParser parser = JsonParserFactory.getJsonParser();
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        try {
            BufferedReader reader = client.getContent(SUMMARY_CORONA_REPORT_URL, requestProperties);
            Map<String,?> map = parser.parseMap(reader.readLine());
            summary.setTotalCases((Integer) map.get("cases"));
            summary.setTotalDeathCases((Integer) map.get("deaths"));
            summary.setTotalRecoveredCases((Integer) map.get("recovered"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return summary;
    }

    public List<LocationStats> getDetailedData() {
        List<LocationStats> stats = new ArrayList<>();
        List<LocationStats> sortedList = new ArrayList<>();
        HttpsClient client = new HttpsClient();
        JsonParser parser = JsonParserFactory.getJsonParser();
        HashMap<String, String> requestProperties = new HashMap<>();
        requestProperties.put("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
        try {
            BufferedReader reader = client.getContent(DETAILED_CORONA_REPORT_URL, requestProperties);
            parser.parseList(reader.readLine()).forEach(item ->
            {
                Map<String ,?> map = (Map<String, ?>)item;
                LocationStats locationStats = new LocationStats();
                locationStats.setCountry((String) map.get("country"));
                locationStats.setTotalCases((Integer) map.get("cases"));
                locationStats.setNewCases((Integer) map.get("todayCases"));
                locationStats.setTotalDeathCases((Integer) map.get("deaths"));
                locationStats.setNewDeathCases((Integer) map.get("todayDeaths"));
                locationStats.setTotalRecoveredCases((Integer) map.get("recovered"));
                locationStats.setCriticalCases((Integer) map.get("critical"));
                stats.add(locationStats);
            });
            sortedList = stats.stream().sorted(Comparator.comparing(LocationStats::getTotalDeathCases).reversed()).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sortedList;
    }

    public List<LocationStats> combineVirusData() {

        List<LocationStats> finalList = this.finalList;
        List<LocationStats> confirmedList = fetchConfirmedData();
        List<LocationStats> deathList = fetchVirusDeathData();
        List<LocationStats> recoveredList = fetchVirusRecoveredData();

        Map<String, LocationStats> confirmedMap = confirmedList.stream().collect(Collectors.toMap(LocationStats::getStateCountryKey, Function.identity()));
        Map<String, LocationStats> deathMap = deathList.stream().collect(Collectors.toMap(LocationStats::getStateCountryKey, Function.identity()));
        Map<String, LocationStats> recoveredMap = recoveredList.stream().collect(Collectors.toMap(LocationStats::getStateCountryKey, Function.identity()));

        deathList.forEach(s -> confirmedMap.merge(s.getStateCountryKey(), s,
                (s1, s2) -> {
                                  s1.setTotalDeathCases(s2.getTotalDeathCases());
                                  s1.setNewDeathCases(s2.getNewDeathCases());
                                  return s1;
                            }
                ));
        recoveredList.forEach(s -> confirmedMap.merge(s.getStateCountryKey(), s,
                (s1, s2) -> {
                                  s1.setTotalRecoveredCases(s2.getTotalRecoveredCases());
                                  s1.setNewRecoveredCases(s2.getNewRecoveredCases());
                                  return s1;
                            }
                ));


//        confirmedMap.values().forEach(System.out::println);
        finalList = new ArrayList<>(confirmedMap.values());
        this.finalList =  finalList;
        return finalList;
    }

    public List<LocationStats> fetchConfirmedData() {


        List<LocationStats> stats = new ArrayList<>();
        HttpsClient client = new HttpsClient();
        try {

            Reader in = client.getContent(HTTPS_CONFIRMED_URL, null);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                LocationStats locationStats = new LocationStats(
                        record.get("Province/State")+record.get("Country/Region"),
                        record.get("Province/State"),
                        record.get("Country/Region"),
                        Integer.parseInt(record.get(record.size() - 1)),
                        Integer.parseInt(record.get(record.size() - 1)) - Integer.parseInt(record.get(record.size() - 2)),
                        0,0,0,0,0
                );
                stats.add(locationStats);
//                System.out.println(locationStats.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stats;
    }

    public List<LocationStats> fetchVirusDeathData() {
        List<LocationStats> stats = new ArrayList<>();
        HttpsClient client = new HttpsClient();
        try {

            Reader in = client.getContent(HTTPS_DEATH_URL, null);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                LocationStats locationStats = new LocationStats(
                        record.get("Province/State")+record.get("Country/Region"),
                        record.get("Province/State"),
                        record.get("Country/Region"),0,0,
                        Integer.parseInt(record.get(record.size()-1)),
                        Integer.parseInt(record.get(record.size()-1)) - Integer.parseInt(record.get(record.size()-2)),
                        0,0,0
                );
                stats.add(locationStats);
//                System.out.println(locationStats.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    return stats;

    }

    public List<LocationStats> fetchVirusRecoveredData() {
        List<LocationStats> stats = new ArrayList<>();
        HttpsClient client = new HttpsClient();
        try {

            Reader in = client.getContent(HTTPS_RECOVERED_URL, null);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            for (CSVRecord record : records) {
                LocationStats locationStats = new LocationStats(
                        record.get("Province/State")+record.get("Country/Region"),
                        record.get("Province/State"),
                        record.get("Country/Region"),
                        0,0,0,0,
                        Integer.parseInt(record.get(record.size()-1)),
                        Integer.parseInt(record.get(record.size()-1)) - Integer.parseInt(record.get(record.size()-2)),
                        0
                );
                stats.add(locationStats);
//                System.out.println(locationStats.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stats;

    }
}
