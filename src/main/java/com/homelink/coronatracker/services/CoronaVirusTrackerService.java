package com.homelink.coronatracker.services;


import com.homelink.coronatracker.connection.HttpsClient;
import com.homelink.coronatracker.model.Countries;
import com.homelink.coronatracker.model.LocationStats;
import com.homelink.coronatracker.model.Type;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class CoronaVirusTrackerService {

    public static String SUMMARY_CORONA_REPORT_URL = "https://corona.lmao.ninja/all";
    public static String DETAILED_CORONA_REPORT_URL = "https://corona.lmao.ninja/countries";
    public static String HTTPS_CONFIRMED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
//    public static String HTTPS_CONFIRMED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    public static String HTTPS_DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
//    public static String HTTPS_DEATH_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";
    public static String HTTPS_RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
//    public static String HTTPS_RECOVERED_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";
    public static String HTTPS_US_LATEST_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_daily_reports/";

    List<LocationStats> finalList = new ArrayList<>();
    List<LocationStats> USList = new ArrayList<>();

//    @PostConstructonstruct
//    @Scheduled(cron = "0 0 1/12 ? * *")
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
//        Map<String, LocationStats> deathMap = deathList.stream().collect(Collectors.toMap(LocationStats::getStateCountryKey, Function.identity()));
//        Map<String, LocationStats> recoveredMap = recoveredList.stream().collect(Collectors.toMap(LocationStats::getStateCountryKey, Function.identity()));

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

    public List<LocationStats> combineVirusData(Map<String, List<LocationStats>> map) {

        List<LocationStats> finalList = this.finalList;
        List<LocationStats> confirmedList = map.get("confirmedList");
        List<LocationStats> deathList = map.get("deathList");
        List<LocationStats> recoveredList = map.get("recoveredList");

        Map<String, LocationStats> confirmedMap = confirmedList.stream().collect(Collectors.toMap(LocationStats::getStateCountryKey, Function.identity()));

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


        finalList = new ArrayList<>(confirmedMap.values());
        this.finalList =  finalList.stream().sorted(Comparator.comparingInt(LocationStats::getTotalCases).reversed()).collect(Collectors.toList());
        return finalList;
    }

    public LocationStats fetchDataBySpecificCountry(String country, String URL) { //US

        LocationStats locationStat = new LocationStats();
        LocalDate localDate = LocalDate.now();

        String prevDateString = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(localDate.minusDays(1));
        String dayBeforePrevDateString = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(localDate.minusDays(2));
        String twoDaysBeforePrevDateString = DateTimeFormatter.ofPattern("MM-dd-yyyy").format(localDate.minusDays(3));

        HttpsClient client = new HttpsClient();
        List<LocationStats> locationStats = new ArrayList<>();
//        List<LocationStats> prefLocationStats = new ArrayList<>();
        try {
            Reader in, in_prev = null;
            try {
                in = client.getContent(URL.concat(prevDateString).concat(".csv"), null);
//                in_prev = client.getContent(URL.concat(dayBeforePrevDateString).concat(".csv"), null);
            }catch (IOException e) {
                in = client.getContent(URL.concat(dayBeforePrevDateString).concat(".csv"), null);
//                in_prev = client.getContent(URL.concat(twoDaysBeforePrevDateString).concat(".csv"), null);

            }
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            records.forEach( record -> {
                if(country.equalsIgnoreCase(record.get("Country_Region"))) {
                    LocationStats stats = new LocationStats();
                    stats.setStateCountryKey(record.get("Combined_Key"));
                    stats.setCountry(record.get("Country_Region"));
                    stats.setState(record.get("Province_State"));
                    stats.setSubState(record.get("Admin2"));
                    stats.setTotalCases(Integer.parseInt(record.get("Confirmed")));
                    stats.setTotalDeathCases(Integer.parseInt(record.get("Deaths")));
                    stats.setTotalRecoveredCases(Integer.parseInt(record.get("Recovered")));
                    locationStats.add(stats);
                }
            });
//            Iterable<CSVRecord> prevRecords = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in_prev);
//            prevRecords.forEach( record -> {
//                if(country.equalsIgnoreCase(record.get("Country_Region"))) {
//                    LocationStats stats = new LocationStats();
//                    stats.setStateCountryKey(record.get("Combined_Key"));
//                    stats.setCountry(record.get("Country_Region"));
//                    stats.setState(record.get("Province_State"));
//                    stats.setSubState(record.get("Admin2"));
//                    stats.setTotalCases(Integer.parseInt(record.get("Confirmed")));
//                    stats.setTotalDeathCases(Integer.parseInt(record.get("Deaths")));
//                    stats.setTotalRecoveredCases(Integer.parseInt(record.get("Recovered")));
//                    locationStats.add(stats);
//                }
//            });
//            List<Map<String, String>> listOfMaps = new ArrayList<>();
//            List<Map<String, String>> pervListOfMaps = new ArrayList<>();
//
//            records.forEach(record -> {
//                if(country.equalsIgnoreCase(record.get("Country_Region")))
//                    listOfMaps.add(record.toMap());
//            });
//            prevRecords.forEach(record -> {
//                if(country.equalsIgnoreCase(record.get("Country_Region")))
//                    pervListOfMaps.add(record.toMap());
//            });
//            listOfMaps.stream().collect(Collectors.groupingBy(map -> map.get("Province_State")));

        } catch (IOException e) {
            e.printStackTrace();
        }
        this.USList = locationStats.stream().sorted(Comparator.comparingInt(LocationStats::getTotalCases).reversed()).collect(Collectors.toList());
        locationStat.setChildren(USList);

        return locationStat;
    }

    public LocationStats fetchDataByCountry(String country, String URL) {


        LocationStats locationStats = new LocationStats();
        HttpsClient client = new HttpsClient();
        try {

            Reader in = client.getContent(URL, null);
            Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(in);
            records.forEach( record -> {

//                System.out.println(map.get("Country/Region"));
                if(country.matches(record.get("Country/Region"))) {
                    locationStats.setCountry(record.get("Country/Region"));
                    LocationStats child = new LocationStats();
                    if (URL.equals(HTTPS_CONFIRMED_URL)) {
                         child = new LocationStats(
                                record.get("Province/State") + record.get("Country/Region"),
                                record.get("Province/State"),
                                record.get("Country/Region"),
                                Integer.parseInt(record.get(record.size() - 1)),
                                Integer.parseInt(record.get(record.size() - 1)) - Integer.parseInt(record.get(record.size() - 2)),
                                0, 0, 0, 0, 0
                        );
                    } else if(URL.equals(HTTPS_DEATH_URL)) {
                         child = new LocationStats(
                                record.get("Province/State")+record.get("Country/Region"),
                                record.get("Province/State"),
                                record.get("Country/Region"),0,0,
                                Integer.parseInt(record.get(record.size()-1)),
                                Integer.parseInt(record.get(record.size()-1)) - Integer.parseInt(record.get(record.size()-2)),
                                0,0,0
                        );
                    } else if(URL.equals(HTTPS_RECOVERED_URL)) {
                         child = new LocationStats(
                                record.get("Province/State")+record.get("Country/Region"),
                                record.get("Province/State"),
                                record.get("Country/Region"),
                                0,0,0,0,
                                Integer.parseInt(record.get(record.size()-1)),
                                Integer.parseInt(record.get(record.size()-1)) - Integer.parseInt(record.get(record.size()-2)),
                                0
                        );
                    }
                    locationStats.getChildren().add(child);

                    Map<String, String> map = record.toMap();
                    map.forEach((k,v) -> {
                        if(k.split("/").length == 3) {
//                            System.out.println(map.get("Province/State") + " : "+ k + " :-> " + map.get(k));
                            int currValue = Integer.parseInt("".equals(map.get(k))? "0" : map.get(k));
                            int rollingSum = 0;
                            if(locationStats.getConfirmedByDate().get(k) != null)
                                rollingSum =  locationStats.getConfirmedByDate().get(k);
                            locationStats.getConfirmedByDate().put(k, (currValue+rollingSum));
                        }
                    });
                    Object [] headerArray = locationStats.getConfirmedByDate().keySet().toArray();
//                    locationStats.getNewConfirmedByDate().put(headerArray[0].toString(), locationStats.getConfirmedByDate().get(headerArray[0].toString()));

                    IntStream.range(1, headerArray.length).forEach(i -> {
                            String currHeader = headerArray[i].toString();
                            String prevHeader = headerArray[i-1].toString();
                            Integer newCases = locationStats.getConfirmedByDate().get(currHeader) - locationStats.getConfirmedByDate().get(prevHeader);
                            locationStats.getNewConfirmedByDate().put(currHeader, newCases);

                    });
                }

            });

        } catch (IOException e) {
            e.printStackTrace();
        }
        return locationStats;
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
