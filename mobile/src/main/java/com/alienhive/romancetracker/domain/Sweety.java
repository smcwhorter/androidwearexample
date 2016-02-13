package com.alienhive.romancetracker.domain;


import java.io.Serializable;

public class Sweety implements Serializable {
    private static final long serialVersionUID = 2L;
    public String sweetyName = "";
    public int smallHugs = 0;
    public int bigHugs = 0;
    public int smallKisses = 0;
    public int bigKisses = 0;

    @Override
    public String toString() {
        if(isZero())
            return sweetyName;
        else
        {
            return sweetyName + getCounts();
        }
    }

    private boolean isZero() {
        return smallHugs + bigHugs + smallKisses + bigKisses == 0;
    }

    private String getCounts()
    {
        return "    " + "Hugs: " + String.valueOf(getHugs()) + " | Kisses: " + String.valueOf(getKisses());
    }

    private int getHugs()
    {
        return smallHugs + bigHugs;
    }

    private int getKisses()
    {
        return smallKisses + bigKisses;
    }
}
