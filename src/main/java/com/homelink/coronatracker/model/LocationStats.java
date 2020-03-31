package com.homelink.coronatracker.model;

import java.util.*;

public class LocationStats {

    private String stateCountryKey;
    private String countryCode;
    private String state;
    private String country;
    private String subState;
    private int totalCases;
    private int newCases;
    private int totalDeathCases;
    private int newDeathCases;
    private int totalRecoveredCases;
    private int newRecoveredCases;
    private int criticalCases;
    private LinkedHashMap<String , Integer> confirmedByDate = new LinkedHashMap<>();
    private LinkedHashMap<String , Integer> newConfirmedByDate = new LinkedHashMap<>();
    private LinkedHashMap<String , Integer> DeathByDate = new LinkedHashMap<>();
    private LinkedHashMap<String , Integer> newDeathByDate = new LinkedHashMap<>();
    private LinkedHashMap<String , Integer> RecoveredByDate = new LinkedHashMap<>();
    private LinkedHashMap<String , Integer> newRecoveredByDate = new LinkedHashMap<>();
    private List<String> dateSeries;
    private List<LocationStats> children = new ArrayList<>();


    public LocationStats(){

    }

    public LocationStats(String stateCountryKey, String state, String country, int totalCases, int newCases, int totalDeathCases, int newDeathCases, int totalRecoveredCases, int newRecoveredCases, int criticalCases) {
        this.stateCountryKey = stateCountryKey;
        this.state = state;
        this.country = country;
        this.totalCases = totalCases;
        this.newCases = newCases;
        this.totalDeathCases = totalDeathCases;
        this.newDeathCases = newDeathCases;
        this.totalRecoveredCases = totalRecoveredCases;
        this.newRecoveredCases = newRecoveredCases;
        this.criticalCases = criticalCases;
    }


    public String getSubState() {
        return subState;
    }

    public void setSubState(String subState) {
        this.subState = subState;
    }

    public List<LocationStats> getChildren() {
        return children;
    }

    public void setChildren(List<LocationStats> children) {
        this.children = children;
    }

    public LinkedHashMap<String, Integer> getConfirmedByDate() {
        return confirmedByDate;
    }

    public void setConfirmedByDate(LinkedHashMap<String, Integer> confirmedByDate) {
        this.confirmedByDate = confirmedByDate;
    }

    public LinkedHashMap<String, Integer> getDeathByDate() {
        return DeathByDate;
    }

    public void setDeathByDate(LinkedHashMap<String, Integer> deathByDate) {
        DeathByDate = deathByDate;
    }

    public LinkedHashMap<String, Integer> getRecoveredByDate() {
        return RecoveredByDate;
    }

    public void setRecoveredByDate(LinkedHashMap<String, Integer> recoveredByDate) {
        RecoveredByDate = recoveredByDate;
    }

    public List<String> getDateSeries() {
        return dateSeries;
    }

    public void setDateSeries(List<String> dateSeries) {
        this.dateSeries = dateSeries;
    }

    public LinkedHashMap<String, Integer> getNewConfirmedByDate() {
        return newConfirmedByDate;
    }

    public void setNewConfirmedByDate(LinkedHashMap<String, Integer> newConfirmedByDate) {
        this.newConfirmedByDate = newConfirmedByDate;
    }

    public LinkedHashMap<String, Integer> getNewDeathByDate() {
        return newDeathByDate;
    }

    public void setNewDeathByDate(LinkedHashMap<String, Integer> newDeathByDate) {
        this.newDeathByDate = newDeathByDate;
    }

    public LinkedHashMap<String, Integer> getNewRecoveredByDate() {
        return newRecoveredByDate;
    }

    public void setNewRecoveredByDate(LinkedHashMap<String, Integer> newRecoveredByDate) {
        this.newRecoveredByDate = newRecoveredByDate;
    }

    public int getCriticalCases() {
        return criticalCases;
    }

    public void setCriticalCases(int criticalCases) {
        this.criticalCases = criticalCases;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getStateCountryKey() {
        return stateCountryKey;
    }

    public void setStateCountryKey(String stateCountryKey) {
        this.stateCountryKey = stateCountryKey;
    }

    public int getTotalDeathCases() {
        return totalDeathCases;
    }

    public void setTotalDeathCases(int totalDeathCases) {
        this.totalDeathCases = totalDeathCases;
    }

    public int getNewDeathCases() {
        return newDeathCases;
    }

    public void setNewDeathCases(int newDeathCases) {
        this.newDeathCases = newDeathCases;
    }

    public int getTotalRecoveredCases() {
        return totalRecoveredCases;
    }

    public void setTotalRecoveredCases(int totalRecoveredCases) {
        this.totalRecoveredCases = totalRecoveredCases;
    }

    public int getNewRecoveredCases() {
        return newRecoveredCases;
    }

    public void setNewRecoveredCases(int newRecoveredCases) {
        this.newRecoveredCases = newRecoveredCases;
    }

    public int getNewCases() {
        return newCases;
    }

    public void setNewCases(int newCases) {
        this.newCases = newCases;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getTotalCases() {
        return totalCases;
    }

    public void setTotalCases(int totalCases) {
        this.totalCases = totalCases;
    }

    @Override
    public String toString() {
        return "LocationStats{" +
                "stateCountryKey='" + stateCountryKey + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", totalCases=" + totalCases +
                ", newCases=" + newCases +
                ", totalDeathCases=" + totalDeathCases +
                ", newDeathCases=" + newDeathCases +
                ", totalRecoveredCases=" + totalRecoveredCases +
                ", newRecoveredCases=" + newRecoveredCases +
                '}';
    }
}
