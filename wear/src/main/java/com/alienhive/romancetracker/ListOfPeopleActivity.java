package com.alienhive.romancetracker;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;

public class ListOfPeopleActivity extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DataApi.DataListener
{

    /* Constants */
    private static final String LOG_TAG = "ListOfPeopleActivity";

    /* Private Fields */
    private ListView listView;
    private GoogleApiClient apiClient;
    private ArrayList<String> nodeList;
    private ArrayList<String> sweetyList = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_people);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (ListView) stub.findViewById(R.id.listView);
                TextView emptyView = (TextView) stub.findViewById(android.R.id.empty);
                listView.setEmptyView(emptyView);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        connectToGoogleApiClient();
        checkDataMapCache();
    }

    private void connectToGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    private void checkDataMapCache() {
        PendingResult<DataItemBuffer> result = Wearable.DataApi.getDataItems(this.apiClient);
        result.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                if (dataItems.getCount() > 0) {

                    DataItem item = dataItems.get(0);
                    if (item.getUri().toString().contains("/SweetyList")) {
                        Log.d(LOG_TAG, "Found Cached DataItems");
                        DataMap dataMapItem = DataMapItem.fromDataItem(item).getDataMap();
                        sweetyList = dataMapItem.getStringArrayList("SweetyList");
                        bindListView();
                        dataItems.release();
                    }
                } else {
                    Log.d(LOG_TAG, "NO Cached DataItems");
                }
            }
        });
    }

    @Override
    protected void onPause() {
        if(apiClient != null)
        {
            Wearable.DataApi.removeListener(this.apiClient, this);
            apiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        Wearable.DataApi.addListener(this.apiClient, this);
        new GetPeopleListTask().execute();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "GoogleAPIClient - onConnectionFailed");
        Toast.makeText(this, "Cannot connect to GPS", Toast.LENGTH_SHORT).show();
    }

    private class GetPeopleListTask extends AsyncTask<String, String, String>
    {
        @Override
        protected String doInBackground(String... params) {
            getConnectedNodes();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            askForSweetyList();
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
        return nodeList;
    }

    private void askForSweetyList()
    {
        if(this.nodeList != null || this.nodeList.size() != 0)
        {
            Wearable.MessageApi.sendMessage(this.apiClient, nodeList.get(0), "/getSweetyList", null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                @Override
                public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                    Log.d(LOG_TAG, "Send Message results: " + sendMessageResult.getStatus().getStatusMessage());
                }
            });
        }
        else
        {
            Log.d(LOG_TAG, "No connected nodes");
            Toast.makeText(this, "No connected nodes", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        DataEvent event = dataEventBuffer.get(0);
        DataItem dataItem = event.getDataItem();

        Log.d(LOG_TAG, "onDataChanged: URI: " +   dataItem.getUri());

        if(dataItem.getUri().toString().contains("/sweetyList"))
        {
            DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
            sweetyList = dataMap.get("SweetyList");
            bindListView();
        }
    }

    private void bindListView()
    {
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, this.sweetyList);
        this.listView.setAdapter(adapter);
    }
}
