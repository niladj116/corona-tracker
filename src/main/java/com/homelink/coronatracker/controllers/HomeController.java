package com.homelink.coronatracker.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.homelink.coronatracker.model.LocationStats;
import com.homelink.coronatracker.model.iLocationStatsMapView;
import com.homelink.coronatracker.services.CoronaRapidAPIServices;
import com.homelink.coronatracker.services.CoronaVirusTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    CoronaVirusTrackerService service;
    @Autowired
    CoronaRapidAPIServices rapidAPIServices;

    @GetMapping("/")
    public String landingController(Model model) {

        List<LocationStats> statsList = rapidAPIServices.getCachedDetailedData();
        LocationStats summaryStats = statsList.stream().reduce(new LocationStats(), (s1,s2) -> {
            s1.setTotalCases(s1.getTotalCases()+s2.getTotalCases());
            s1.setTotalDeathCases(s1.getTotalDeathCases()+s2.getTotalDeathCases());
            s1.setTotalRecoveredCases(s1.getTotalRecoveredCases()+s2.getTotalRecoveredCases());
            return s1;
        });
        //Back up call if rapidAPIServices is not available
        if(statsList != null && statsList.size() == 0) {
            statsList = service.getDetailedData();
            summaryStats = service.getSummaryData();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);
        String jsonString = "";
        try {
            jsonString = objectMapper.writerWithView(iLocationStatsMapView.class).writeValueAsString(statsList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        model.addAttribute("statsList",statsList);
        model.addAttribute("statsListJSON",jsonString);
        model.addAttribute("allTotalCases",summaryStats.getTotalCases());
        model.addAttribute("allTotalDeathCases",summaryStats.getTotalDeathCases());
        model.addAttribute("allTotalRecoveredCases",summaryStats.getTotalRecoveredCases());

        return "home";
    }

//    public String home(Model model, @RequestParam("id") Optional<Integer> id) {
//        Integer sortId =  id.orElse(new Integer(-3));
//        List<LocationStats> statsList = service.fetchVirusData();
//        List<LocationStats> sortedList = new ArrayList<>();
//        switch (sortId) {
//            case 1 : //Country
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparing(LocationStats::getCountry))
//                        .collect(Collectors.toList());
//                break;
//            case 2 : //State
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparing(LocationStats::getState))
//                        .collect(Collectors.toList());
//                break;
//            case 3 : //Total Case
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getTotalCases))
//                        .collect(Collectors.toList());
//                break;
//            case 4 : //New Cases
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getNewCases))
//                        .collect(Collectors.toList());
//                break;
//            case 5 : //New Cases
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getTotalDeathCases))
//                        .collect(Collectors.toList());
//                break;
//
//            case 6 : //New Cases
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getNewCases))
//                        .collect(Collectors.toList());
//                break;
//
//            case -1 : //Country
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparing(LocationStats::getCountry).reversed())
//                        .collect(Collectors.toList());
//                break;
//            case -2 : //State
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparing(LocationStats::getState).reversed())
//                        .collect(Collectors.toList());
//                break;
//            case -3 : //Total Case
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getTotalCases).reversed())
//                        .collect(Collectors.toList());
//                break;
//            case -4 : //New Cases
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getNewCases).reversed())
//                        .collect(Collectors.toList());
//                break;
//            case -5 : //New Cases
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getTotalDeathCases).reversed())
//                        .collect(Collectors.toList());
//                break;
//
//            case -6 : //New Cases
//                sortedList = statsList.stream()
//                        .sorted(Comparator.comparingInt(LocationStats::getTotalRecoveredCases).reversed())
//                        .collect(Collectors.toList());
//                break;
//
//        }
//
//        int allTotalCases = statsList.stream().mapToInt(LocationStats::getTotalCases).sum();
//        int allTotalNewCases = statsList.stream().mapToInt(LocationStats::getNewCases).sum();
//        int allTotalDeathCases = statsList.stream().mapToInt(LocationStats::getTotalDeathCases).sum();
//        int allTotalRecoveredCases = statsList.stream().mapToInt(LocationStats::getTotalRecoveredCases).sum();
//
//        model.addAttribute("statsList",sortedList);
//        model.addAttribute("allTotalCases",allTotalCases);
//        model.addAttribute("allTotalNewCases",allTotalNewCases);
//        model.addAttribute("allTotalDeathCases",allTotalDeathCases);
//        model.addAttribute("allTotalRecoveredCases",allTotalRecoveredCases);
//        return "home";
//    }
}
