package com.simats.strokecare;

public class PatientInfo {
    private String name;
    private String hospitalId;
    private String profileImage;
    private long additionTimeMillis; // New field to store the timestamp when the patient was added

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }
//    public long getAdditionTimeMillis() {
//        return additionTimeMillis;
//    }

//    public void setAdditionTimeMillis(long additionTimeMillis) {
//        this.additionTimeMillis = additionTimeMillis;
//    }
//
//    public static boolean isPatientJustAdded(long patientAddedTimeMillis) {
//        long currentTimeMillis = System.currentTimeMillis();
//        // Assuming if the patient was added within the last hour, it's considered just added
//        return (currentTimeMillis - patientAddedTimeMillis) <= (1 * 60 * 60 * 1000); // 1 hour in milliseconds
//    }
}
