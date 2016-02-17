package com.alienhive.romancetracker.service;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.alienhive.romancetracker.UI.ListOfPeopleActivity;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.WearableListenerService;

public class ActionWearListenerService extends WearableListenerService {

    private static final String LOG_TAG = "ActionWearLS";
    public static final String ACTION = "action";
    public static final String INTENT_ACTION = "UPDATE-VIEW";

    @Override
    public void onPeerConnected(Node peer) {
        super.onPeerConnected(peer);
        String id = peer.getId();
        String name = peer.getDisplayName();

        Log.d(LOG_TAG, "Connected peer name & ID: " + name + "|" + id);
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(LOG_TAG, "onMessageReceived");
        super.onMessageReceived(messageEvent);
        if (messageEvent.getPath().contains("/action")) {
            processActionEvent(messageEvent.getPath());
        }
    }

    private void processActionEvent(String uri) {
        String[] uriParts = uri.split("/");
        if (uriParts.length == 5) {
            String action = uriParts[1];
            if (action.equals(ACTION)) {
                recordActionOnSweety(uriParts);
            }
        }
    }

    private void recordActionOnSweety(String[] uriParts) {
        String sweetyName = uriParts[2];
        String actionType = uriParts[3];
        String actionValue = uriParts[4];

        recordAction(sweetyName, actionType, actionValue);
    }

    private void recordAction(String name, String actionType, String actionValue) {

        saveDataToSharedPreferces(name, actionType, actionValue);
        sendBroadcast();
    }

    private void saveDataToSharedPreferces(String name, String actionType, String actionValue) {
        SharedPreferences preferences = getSharedPreferences("pendingactions-pref", Context.MODE_PRIVATE);
        String currentPendingActions = loadCurrentSavedData(preferences);
        saveNewData(name, actionType, actionValue, preferences, currentPendingActions);
    }

    @NonNull
    private String loadCurrentSavedData(SharedPreferences preferences) {
        String currentPendingActions = "";
        if (preferences.contains("pendingactions")) {
            currentPendingActions = preferences.getString("pendingactions", "");
        }
        return currentPendingActions;
    }

    private void saveNewData(String name, String actionType, String actionValue, SharedPreferences preferences, String currentPendingActions) {
        SharedPreferences.Editor editor = preferences.edit();

        String pendingAction = currentPendingActions + name + "/" + actionType + "/" + actionValue + "#";
        editor.putString("pendingactions", pendingAction);
        editor.commit();
        Log.d(LOG_TAG, "saving pending data to SP");
    }

    private void sendBroadcast() {
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        Intent i = new Intent(getApplicationContext(), ListOfPeopleActivity.class);
        i.setAction(INTENT_ACTION);
        localBroadcastManager.sendBroadcast(i);
    }
}
