package com.alienhive.romancetracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.Toast;

import com.alienhive.romancetracker.gridpager.ActionPickerGridPagerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class ActionCaptureActivity extends Activity implements ActionPageFragment.ActionPageFragmentListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    //Constants
    private static final String LOG_TAG = "ActionCaptureActivity";
    private static final String EXTRA_SWEETY_NAME = "EXTRA_SWEETY_NAME";

    public static Intent buildIntent(Context context, String selectedSweety) {
        Intent i= new Intent(context, ActionCaptureActivity.class);
        i.putExtra(EXTRA_SWEETY_NAME, selectedSweety);
        return i;
    }

    //Private fields
    private String selectedSweety;
    private GridViewPager gridViewPager;
    private GoogleApiClient apiClient;

    //****************Activity Life cycle****************?/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_capture);
        this.selectedSweety = getIntent().getStringExtra(EXTRA_SWEETY_NAME);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                gridViewPager = (GridViewPager) stub.findViewById(R.id.pager);
                setupGridViewPager();
            }
        });
    }

    @Override
    protected void onPause() {
        if(apiClient != null)
        {
            apiClient.disconnect();
        }

        super.onPause();
    }

    private void setupGridViewPager() {
        ActionPickerGridPagerAdapter adapter = new ActionPickerGridPagerAdapter(this, getFragmentManager());
        gridViewPager.setAdapter(adapter);
    }

    //***************Action Listener******************//
    @Override
    public void actionButtonClicked() {
        Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();

        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    //***************Google API Clientr******************//
    @Override
    public void onConnected(Bundle bundle) {
        if (ListOfPeopleActivity.nodeList != null) {
            sendActionMessage();
        }
    }

    private void sendActionMessage() {
        String uri = "/action" + "/" + this.selectedSweety;
        Wearable.MessageApi.sendMessage(this.apiClient, ListOfPeopleActivity.nodeList.get(0), uri, null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            //Note: The a success message does not mean the client app received the message
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                Log.d(LOG_TAG, "Send Message results: " + sendMessageResult.getStatus().getStatusMessage());
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot connect to Goggle Play Services", Toast.LENGTH_SHORT).show();
    }
}
