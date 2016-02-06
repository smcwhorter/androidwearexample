package com.alienhive.romancetracker.domain;

public class RomanceAction{

    //Constants
    public static final String Hug = "Hug";
    public static final String Kiss = "Kiss";

    //Public fields
    public final String description;
    public final String title;
    public int value = 0;
    public String actionType;
    public boolean isBig = false;

    //************Factory methods********************//
    public static RomanceAction createHugAction()
    {
        RomanceAction action = new RomanceAction(Hug, "Hugs", "Track big or small hugs", 1);
        return action;
    }

    public static RomanceAction createKissAction()
    {
        RomanceAction action = new RomanceAction(Kiss, "Kisses", "Track big or small kisses", 3);
        return action;
    }

    public static RomanceAction createBigAction(RomanceAction currentAction)
    {
        RomanceAction action = new RomanceAction(currentAction.actionType, currentAction.title, currentAction.description, currentAction.value + 2);
        action.isBig = true;
        return action;
    }

    //************Private Constructor********************//
    private RomanceAction(String actionType, String title, String description, int value)
    {
        this.actionType = actionType;
        this.title = title;
        this.description = description;
        this.value = value;
    }
}
