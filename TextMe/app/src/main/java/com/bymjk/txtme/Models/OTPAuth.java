package com.bymjk.txtme.Models;

public class OTPAuth {

    private String companyName;
    private String companyIcon;
    private int otp_size;
    private String otp;

    public OTPAuth() {
    }

    public OTPAuth(String companyName, String companyIcon, int otp_size, String otp) {
        this.companyName = companyName;
        this.companyIcon = companyIcon;
        this.otp_size = otp_size;
        this.otp = otp;
    }

    // Getters and Setters
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyIcon() {
        return companyIcon;
    }

    public void setCompanyIcon(String companyIcon) {
        this.companyIcon = companyIcon;
    }

    public int getOtp_size() {
        return otp_size;
    }

    public void setOtp_size(int otp_size) {
        this.otp_size = otp_size;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

}
