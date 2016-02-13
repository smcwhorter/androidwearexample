package com.alienhive.romancetracker.domain;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;

public class TrackerResults implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<Sweety> sweeties;

    public TrackerResults()
    {}

    public List<Sweety> getSweetyList()
    {
        return sweeties;
    }

    public List<String> getSweetyListOfNames()
    {
        List<String> nameList = null;
        if(this.sweeties != null)
        {
            nameList = new ArrayList<>(this.sweeties.size());
            for(int x =0; x < this.sweeties.size(); x++)
            {
                String name = this.sweeties.get(x).sweetyName;
                nameList.add(name);
            }
        }
        return nameList;
    }


    public void addSweety(String name)
    {
        addToObjectList(name);
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
        Sweety sweetyFound = null;

        for (Sweety sweety : this.sweeties) {

            if(sweety.sweetyName.equals(name.trim()))
            {
                sweetyFound = sweety;
                break;
            }
        }
        return sweetyFound;
    }

    private void countActionOnSweety(Sweety sweety, String actionType, String actionValue)
    {
        if((actionType.toLowerCase().equals("hug")) && (actionValue.toLowerCase().equals("small")))
        {
            sweety.smallHugs++;
        }
        if((actionType.toLowerCase().equals("hug")) && (actionValue.toLowerCase().equals("big")))
        {
            sweety.bigHugs++;
        }
        if((actionType.toLowerCase().equals("kiss")) && (actionValue.toLowerCase().equals("small")))
        {
            sweety.smallKisses++;
        }
        if((actionType.toLowerCase().equals("kiss")) && (actionValue.toLowerCase().equals("big")))
        {
            sweety.bigKisses++;
        }
    }
}
