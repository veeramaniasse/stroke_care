// physio_patient_info.java

package com.simats.strokecare;

public class physio_patient_info {
    private String Day;
    private String Month;
    private String Year;

    public physio_patient_info() {
        // Default constructor
    }

    public String getDay() {
        return Day;
    }

    public void setDay(String day) {
        this.Day = day;
    }

    public String getMonth() {
        return Month;
    }

    public void setMonth(String month) {
        this.Month = month;
    }

    public String getYear() {
        return Year;
    }

    public void setYear(String year) {
        this.Year = year;
    }
}
