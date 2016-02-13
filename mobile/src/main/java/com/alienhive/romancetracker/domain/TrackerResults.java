package com.alienhive.romancetracker.domain;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class TrackerResults implements Serializable {
    private List<Sweety> sweeties;
    private List<String> sweetyNames;

    public TrackerResults()
    {}

    public List<String> getSweetyNameList()
    {
        return sweetyNames;
    }

    public void addSweety(String name)
    {
        addToObjectList(name);
        addToNameList(name);
    }

    private void addToObjectList(String name) {
        Sweety sweety = new Sweety();
        sweety.sweetyName = name.trim();

        if(sweeties == null)
        {
            sweeties = new ArrayList<Sweety>();
        }
        sweeties.add(sweety);
    }

    private void addToNameList(String name) {
        if(this.sweetyNames == null)
        {
            this.sweetyNames = new ArrayList<>(sweeties.size());
        }
        this.sweetyNames.add(name);
    }

    public void recordAction(String name, String actionType, String actionValue)
    {
        if(this.sweeties != null) {
            Sweety sweety = findSweetyInList(name);
            if(sweety != null)
            {
                countActionOnSweety(sweety, actionType, actionValue);
            }
        }
    }

    private Sweety findSweetyInList(String name) {
        Sweety sweety = null;

        for (int x = 0; x < sweeties.size(); x++) {
            sweety = this.sweeties.get(x);
            if(sweety.equals(name.trim()))
            {
                break;
            }
        }
        return sweety;
    }

    private void countActionOnSweety(Sweety sweety, String actionType, String actionValue)
    {
        if((actionType.equals("hug")) && (actionValue.equals("small")))
        {
            sweety.smallHugs++;
        }
        if((actionType.equals("hug")) && (actionValue.equals("big")))
        {
            sweety.bigHugs++;
        }
        if((actionType.equals("kiss")) && (actionValue.equals("small")))
        {
            sweety.smallKisses++;
        }
        if((actionType.equals("kiss")) && (actionValue.equals("big")))
        {
            sweety.bigKisses++;
        }
    }
}
