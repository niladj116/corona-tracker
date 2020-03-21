package com.homelink.coronatracker.model;

public class LocationStats {

    private String stateCountryKey;
    private String countryCode;
    private String state;
    private String country;
    private int totalCases;
    private int newCases;
    private int totalDeathCases;
    private int newDeathCases;
    private int totalRecoveredCases;
    private int newRecoveredCases;
    private int criticalCases;


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
