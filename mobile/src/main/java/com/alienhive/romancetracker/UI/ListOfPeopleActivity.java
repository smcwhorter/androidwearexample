package com.alienhive.romancetracker.UI;

import android.app.AlertDialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.alienhive.romancetracker.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListOfPeopleActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        MessageApi.MessageListener

{
    //Constants
    private static final String LOG_TAG = "ListOfPeopleActivity";
    public static final String SWEETYLIST = "SWEETYLIST";
    private static final String SWEETY_LIST_URI_PATH = "/sweetyList";
    public static final String SWEETY_LIST_DATA_MAP_ITEM_KEY = "SweetyListDataMapItemKey";

    //Private fields
    private ArrayList<String> sweetyList;
    private ArrayAdapter sweetyListAdapter;
    private GoogleApiClient apiClient;

    private ListView listView;
    private AlertDialog appPartnerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_people);
        setupToolbar();
        setupFAB();
        extractSweetyListData(savedInstanceState);
        setupListView();
    }


    @Override
    protected void onResume() {
        super.onResume();
        int status = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getApplicationContext());
        if (status != ConnectionResult.API_UNAVAILABLE) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            apiClient.connect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState");
        outState.putStringArrayList(SWEETYLIST, this.sweetyList);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (apiClient != null) {
            Wearable.MessageApi.removeListener(this.apiClient, this);
            apiClient.disconnect();
            apiClient = null;
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFAB() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddNewPartnerDialog();
            }
        });
    }

    private void extractSweetyListData(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            this.sweetyList = (ArrayList<String>) savedInstanceState.get(SWEETYLIST);
        }
        if(this.sweetyList == null)
        {
            loadDefaultData();
        }
    }

    private void loadDefaultData() {
        String[] myResArray = getResources().getStringArray(R.array.people);
        List<String> defaultData = Arrays.asList(myResArray);
        this.sweetyList = new ArrayList<>(defaultData.size());
        this.sweetyList.addAll(defaultData);
    }

    private void setupListView() {
        this.listView = (ListView)findViewById(R.id.listView);
        this.sweetyListAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, sweetyList);
        listView.setAdapter(sweetyListAdapter);
    }

    private void showAddNewPartnerDialog() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Add new Partner");
        dialogBuilder.setCancelable(true);

        View view = this.getLayoutInflater().inflate(R.layout.add_partner_dialog, null);
        final EditText partnerNameEditText = (EditText) view.findViewById(R.id.partnerNameEditText);

        Button partnerNameAddButton = (Button) view.findViewById(R.id.partnerNameAddButton);
        partnerNameAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = partnerNameEditText.getText().toString();
                addPartnerName(newName);
                appPartnerDialog.dismiss();

            }
        });
        dialogBuilder.setView(view);
        appPartnerDialog = dialogBuilder.create();
        appPartnerDialog.show();
    }

    private void addPartnerName(String newName) {
        this.sweetyList.add(newName);
        this.sweetyListAdapter.notifyDataSetChanged();
        syncSweetyList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_of_people, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /* Google client API */
    @Override
    public void onConnected(Bundle bundle) {
        Snackbar.make(listView, "Google API Client - Connected", Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        Wearable.MessageApi.addListener(this.apiClient, this);
        syncSweetyList();
    }

    /* Google client API */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /* Google client API */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO: Look at the connection results
        Snackbar.make(listView, "Google API Client - Connection failed with status: " + connectionResult.getErrorMessage(), Snackbar.LENGTH_SHORT).show();
    }

    /* Message API - in this case the activity must be running to receive messages*/
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(LOG_TAG, "onMessageReceived: " + messageEvent.getPath());
        if(messageEvent.getPath().equals("/getSweetyList"))
        {
            syncSweetyList();
        }
        else if(messageEvent.getPath().contains("/action"))
        {
            processActionEvent(messageEvent.getPath());
        }
    }

    private void syncSweetyList()
    {
        Log.d(LOG_TAG, "Sending data");
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(SWEETY_LIST_URI_PATH);//internally the URI looks like wear://<nodeid>/sweetyList

        //http://android-developers.blogspot.nl/2015/11/whats-new-in-google-play-services-83.html:
        // Non-urgent DataItems may be delayed for up to 30 minutes, but you can expect that in most
        // cases they will be delivered within a few minutes.
        // Low priority is now the default, so setUrgent() is needed to obtain the previous timing.
        putDataMapRequest.setUrgent();

        DataMap dataMap = putDataMapRequest.getDataMap();//DataMap is kinda like a Bundle (key/value pairs)
        ArrayList<String> dataList = new ArrayList<String>(this.sweetyList.size());
        dataList.addAll(this.sweetyList);
        dataMap.putStringArrayList(SWEETY_LIST_DATA_MAP_ITEM_KEY, dataList);
        //dataMap.putLong("TimeStamp", System.currentTimeMillis());


        Wearable.DataApi.putDataItem(this.apiClient, putDataMapRequest.asPutDataRequest()).setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
            @Override
            public void onResult(@NonNull DataApi.DataItemResult dataItemResult) {
                Log.d(LOG_TAG, dataItemResult.getStatus().getStatusMessage());
            }
        });
    }

    private void processActionEvent(String uri)
    {

    }
}
