package com.homelink.coronatracker.controllers;

import com.homelink.coronatracker.model.Countries;
import com.homelink.coronatracker.model.LocationStats;
import com.homelink.coronatracker.services.CoronaRapidAPIServices;
import com.homelink.coronatracker.services.CoronaVirusTrackerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class DetailsController {
    @Autowired
    CoronaVirusTrackerService service;
    @Autowired
    CoronaRapidAPIServices rapidAPIServices;

    @GetMapping("/details")
    public String detailsController(Model model, @RequestParam("id") Optional<String> id) {
        System.out.println("Controller ID : " + id);
        if (id.isPresent()) {
            String [] param = id.get().split(",");
            LocalDate localDate = LocalDate.now();
            String dateString = DateTimeFormatter.ofPattern("MM/dd/yy").format(localDate);
//            System.out.println(dateString);     //07/15/2018
            String code = Countries.doubleBraceMap.get(param[0].trim().toLowerCase());
            if (code == null)
                code = Countries.doubleBraceMap.get("default");

            LocationStats confirmedByCountry = service.fetchDataByCountry(param[0].trim(), CoronaVirusTrackerService.HTTPS_CONFIRMED_URL);
//            confirmedByCountry.getNewConfirmedByDate().put(dateString, Integer.parseInt(param[1].trim()));
            confirmedByCountry.getNewConfirmedByDate().remove(dateString);
            LocationStats deathByCountry = service.fetchDataByCountry(param[0].trim(), CoronaVirusTrackerService.HTTPS_DEATH_URL);
//            deathByCountry.getNewConfirmedByDate().put(dateString, Integer.parseInt(param[2].trim()));
            deathByCountry.getNewConfirmedByDate().remove(dateString);
            LocationStats recoveredByCountry = service.fetchDataByCountry(param[0].trim(), CoronaVirusTrackerService.HTTPS_RECOVERED_URL);
//            recoveredByCountry.getNewConfirmedByDate().put(dateString, Integer.parseInt(param[3].trim()));
            recoveredByCountry.getNewConfirmedByDate().remove(dateString);

            model.addAttribute("confirmedByCountryByDate",confirmedByCountry.getNewConfirmedByDate());
            model.addAttribute("deathByCountryByDate",deathByCountry.getNewConfirmedByDate());
            model.addAttribute("recoveredByCountryByDate",recoveredByCountry.getNewConfirmedByDate());

            model.addAttribute("countryCode",code);
            model.addAttribute("countryName",param[0].trim());

//            if("US".equals(param[0].trim())) {
//                //List<LocationStats> regionByCountryList = service.combineVirusData(map);
//                LocationStats regionByCountry = service.fetchDataBySpecificCountry(param[0].trim(), CoronaVirusTrackerService.HTTPS_US_LATEST_URL);
//                model.addAttribute("regionByCountryList",regionByCountry.getChildren());
//            }
//            else{
//                Map<String, List<LocationStats>> map = new HashMap<>();
//                map.put("confirmedList", confirmedByCountry.getChildren());
//                map.put("deathList", deathByCountry.getChildren());
//                map.put("recoveredList", recoveredByCountry.getChildren());
//                List<LocationStats> regionByCountryList = service.combineVirusData(map);
//                model.addAttribute("regionByCountryList",regionByCountryList);
//            }

            return "details";
        }
        return "home";
    }

}
