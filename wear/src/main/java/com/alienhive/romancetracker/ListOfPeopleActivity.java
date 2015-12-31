package com.alienhive.romancetracker;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class ListOfPeopleActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
{

    private static final String LOG_TAG = "ListOfPeopleActivity";
    private TextView mTextView;
    private GoogleApiClient apiClient;
    private ArrayList<String> nodeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_people);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    @Override
    protected void onPause() {
        if(apiClient != null)
        {
            apiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        new GetPeopleListTask().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "GoogleAPIClient - onConnectionFailed");
    }

    private class GetPeopleListTask extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            getConnectedNodes();
            getSweetyList();
            return null;
        }
    }

    private void getConnectedNodes()
    {
        Log.d(LOG_TAG, "getConnectedNodes");
        NodeApi.GetConnectedNodesResult resultOfNodes = Wearable.NodeApi.getConnectedNodes(this.apiClient).await();
        this.nodeList = buildNodeList(resultOfNodes);
    }

    private ArrayList<String> buildNodeList(NodeApi.GetConnectedNodesResult result) {
        ArrayList<String> nodeList = new ArrayList<String>();
        int size = result.getNodes().size();
        if(size > 0) {
            for (int i = 0; i < size; i++) {
                Node node = result.getNodes().get(i);
                String nName = node.getDisplayName();
                String nId = node.getId();
                nodeList.add(node.getId());
                Log.d(LOG_TAG, "Node name and ID: " + nName + " | " + nId);
            }
        }
        else
        {
            Log.d(LOG_TAG, "No connected nodes");
        }
        return nodeList;
    }

    private void getSweetyList()
    {
        Wearable.MessageApi.sendMessage(this.apiClient, nodeList.get(0), "/getSweetyList", null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
            @Override
            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                Log.d(LOG_TAG, "Send Message results: " + sendMessageResult.getStatus().getStatusMessage());
            }
        });
    }
}
