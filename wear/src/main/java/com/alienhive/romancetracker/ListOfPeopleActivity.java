package com.alienhive.romancetracker;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
        DataApi.DataListener,
        ListView.OnItemClickListener
{

    /* Constants */
    private static final String LOG_TAG = "ListOfPeopleActivity";
    private static final String SWEETY_LIST_URI_PATH = "/sweetyList";
    private static final String SWEETY_LIST_DATA_MAP_ITEM_KEY = "SweetyListDataMapItemKey";

    /* Private Fields */
    private ListView listView;
    private ArrayAdapter adapter;
    private GoogleApiClient apiClient;
    private ArrayList<String> nodeList;
    private ArrayList<String> sweetyList = new ArrayList<>(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_people);
        setupWatchViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(LOG_TAG, "onResume");
        connectToGoogleApiClient();
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

    private void setupWatchViews() {
        final ListOfPeopleActivity activityContext = this;
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                listView = (ListView) stub.findViewById(R.id.listView);
                TextView emptyView = (TextView) stub.findViewById(android.R.id.empty);
                listView.setEmptyView(emptyView);
                adapter = new ArrayAdapter(stub.getContext(), android.R.layout.simple_list_item_1, sweetyList);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(activityContext);
            }
        });
    }

    private void checkDataMapCache() {
        PendingResult<DataItemBuffer> result = Wearable.DataApi.getDataItems(this.apiClient);
        result.setResultCallback(new ResultCallback<DataItemBuffer>() {
            @Override
            public void onResult(DataItemBuffer dataItems) {
                int items = dataItems.getCount();
                if (items > 0) {
                    DataItem item = dataItems.get(0);
                    if (item.getUri().toString().contains(SWEETY_LIST_URI_PATH)) {
                        Log.d(LOG_TAG, "Found Cached DataItems");
                        DataMap dataMapItem = DataMapItem.fromDataItem(item).getDataMap();
                        if (dataMapItem.containsKey(SWEETY_LIST_DATA_MAP_ITEM_KEY)) {
                            ArrayList<String> sweetyList = dataMapItem.getStringArrayList(SWEETY_LIST_DATA_MAP_ITEM_KEY);
                            updateListView(sweetyList);
                        }
                        dataItems.release();
                    }
                } else {
                    Log.d(LOG_TAG, "NO Cached DataItems");
                }
            }
        });
    }

    private void connectToGoogleApiClient() {
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        apiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(LOG_TAG, "onConnected");
        Wearable.DataApi.addListener(this.apiClient, this);
        checkDataMapCache();//cannot check the cache without a GoogleAPIClient
        if(isSweetyListEmpty()) {
            new GetSweetyListTask().execute();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(LOG_TAG, "GoogleAPIClient - onConnectionFailed");
        Toast.makeText(this, "Cannot connect to GPS", Toast.LENGTH_SHORT).show();
    }

    private boolean isSweetyListEmpty() {
        return (this.sweetyList == null || this.sweetyList.size() == 0);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(ActionCaptureActivity.buildIntent(this));
    }

    private class GetSweetyListTask extends AsyncTask<String, String, String>
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
            Wearable.MessageApi.sendMessage(this.apiClient, this.nodeList.get(0), "/getSweetyList", null).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
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

        if(dataItem.getUri().toString().contains(SWEETY_LIST_URI_PATH))
        {
            DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
            ArrayList<String> data = dataMap.get(SWEETY_LIST_DATA_MAP_ITEM_KEY);
            updateListView(data);
        }
    }

    private void updateListView(ArrayList<String> data)
    {
        sweetyList.clear();
        sweetyList.addAll(data);
        this.adapter.notifyDataSetChanged();
    }
}
