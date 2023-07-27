package com.bymjk.txtme.Models;

public class Features {

    String first;
    String second;
    String third;
    String forth;
    String fifth;
    String sixth;

    public Features(String first, String second, String third, String forth, String fifth, String sixth) {
        this.first = first;
        this.second = second;
        this.third = third;
        this.forth = forth;
        this.fifth = fifth;
        this.sixth = sixth;
    }

    public Features() {
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }

    public String getThird() {
        return third;
    }

    public String getForth() {
        return forth;
    }

    public String getFifth() {
        return fifth;
    }

    public String getSixth() {
        return sixth;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    public void setThird(String third) {
        this.third = third;
    }

    public void setForth(String forth) {
        this.forth = forth;
    }

    public void setFifth(String fifth) {
        this.fifth = fifth;
    }

    public void setSixth(String sixth) {
        this.sixth = sixth;
    }
}
