package me.zkk.kkapp;

import java.io.Serializable;

public class TestClass implements Serializable {
    private String a = "abcd";

    public void setA(String a1) {
        this.a = a1;
    }

    public String getA() {
        return this.a;
    }
}
