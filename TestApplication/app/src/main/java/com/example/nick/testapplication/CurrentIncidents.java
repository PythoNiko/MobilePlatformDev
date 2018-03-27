package com.example.nick.testapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import android.widget.Toast;


public class CurrentIncidents extends AppCompatActivity {

    /*
        FILE:   CurrentIncidents.java
        CLASS:  CurrentIncidents

        NAME:   NICK CONNELL
        ID:     S1623944

        THIS CLASS IS RESPONSIBLE FOR PROCESSING AND STORING OF DATA FROM XML FEED
     */

    // LIST VIEW OF INCIDENTS
    ListView currentIncidentsRSS; // DYNAMIC NATURE OF LIST VIEW WILL POPULATE AT EXECUTION/RUNTIME
    ArrayList<String> currentIncident;

    //SEARCH OF INCIDENTS
    ArrayList<String> currentIncidentSearch;
    AutoCompleteTextView searchFilter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_incidents);

        // LIST VIEW OF INCIDENTS
        currentIncidentsRSS = (ListView) findViewById(R.id.listViewCurrIncRSS);
        currentIncident = new ArrayList<String>();

        //SEARCH OF INCIDENTS
        searchFilter = (AutoCompleteTextView) findViewById(R.id.searchBar);
        currentIncidentSearch = new ArrayList<String>();


        // ON CLICK METHOD FOR LIST ITEMS
        currentIncidentsRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                // ALERT FOR USER - POSSIBLY TAKE TO INCIDENT ON MAP
                new AlertDialog.Builder(CurrentIncidents.this)
                        .setTitle("Incident Title")
                        .setMessage("View Map Location?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // OPEN MAP LOCATION
                                Log.i("infoLocationAlertYes", "User has selected to view location on map. Launch map activity.");
                            }
                        }).setNegativeButton("No", null).show();
                                // N/A
                                Log.i("infoLocationAlertNo", "User has selected not to view location on map.");
            }
        });

        new ProcessCurrIncFeed().execute();

    }

    // OPEN CONNECTION TO SUPPLIED URL
    public InputStream getInputStream(URL url){
            try {
                Log.i("infoConnectSuccess", "Connected to stream Successfully.");
                return url.openConnection().getInputStream();
            }
            catch (IOException e) {
                Log.e("errorConnectFailure", "Did NOT connect to stream successfully.");
                return null;
            }
    }

    public class ProcessCurrIncFeed extends AsyncTask<Integer, Void, Exception>{

        /*
        FILE:   CurrentIncidents.java
        CLASS:  ProcessCurrIncFeed

        NAME:   NICK CONNELL
        ID:     S1623944

        THIS CLASS IS RESPONSIBLE FOR HANDLING DATA SUPPLIED FROM THE XML FEED
        */

        ProgressDialog progressDialog = new ProgressDialog(CurrentIncidents.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // MESSAGE TO DISPLAY TO USER
            progressDialog.setMessage("Loading Feed...");
            Log.i("FeedLoader", "Loading feed to ListView.");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {

            /*
                TRY CATCH TO WORK THROUGH RSS FEED
             */

            try{
                // GET URL OF SPECIFIC RSS FEED
                URL url = new URL("https://trafficscotland.org/rss/feeds/currentincidents.aspx");

                // CREATE INSTANCE OF PARSER FACTORY
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                // SPECIFIES THAT THE PRODUCED PARSER WILL PROVIDE SUPPORT FOR XML NAMESPACES
                factory.setNamespaceAware(false);

                // CREATE INSTANCE OF PARSER
                XmlPullParser xpp = factory.newPullParser();

                // TARGET TO PULL DATA FROM INCLUDING ENCODING SCHEME
                xpp.setInput(getInputStream(url), "iso-8859-1");

                // SER DEFAULT TO FALSE TO CHECK WHEN INSIDE EACH XML ITEM
                boolean insideItem = false;

                // HOLDER VAR FOR STORING EVENT TYPE
                int eventType = xpp.getEventType();

                // COUNT OF ITEMS IN LIST
                int listCount = 0;

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equalsIgnoreCase("item")){
                            insideItem = true;
                        }
                        else if(xpp.getName().equalsIgnoreCase("title")) {
                            if (insideItem) {
                                // ADD TO LIST
                                currentIncident.add(xpp.nextText());
                                listCount++;
                                Log.i("ItemAdded", "Item has been added to currentIncident List.");
                                //currentIncidentSearch.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                currentIncident.add(xpp.nextText());
                                Log.i("infoDescriptionAdded", "Description has been added to currentIncident List.");
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                currentIncident.add(xpp.nextText());
                                Log.i("infoPubDateAdded", "pubDate has been added to currentIncident List.");
                                currentIncident.add("---------------------------------------------------------------------------");
                            }
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
                if(listCount == 0){
                    currentIncident.add("No items available");
                    Log.w("warningEmptyFeed", "No items populated from RRS Feed. May be empty.");
                }
                else if(listCount > 0){
                    Log.i("infoListCounter", "Items in List: " + listCount);
                }
            }
            catch(MalformedURLException e){
                exception = e;
            }
            catch(XmlPullParserException e){
                exception = e;
            }
            catch(IOException e){
                exception = e;
            }

            return exception;
        }

        @Override
        protected void onPostExecute(Exception s) {
            super.onPostExecute(s);

            ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(CurrentIncidents.this, android.R.layout.simple_list_item_1, currentIncidentSearch);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(CurrentIncidents.this, android.R.layout.simple_list_item_1, currentIncident);

            currentIncidentsRSS.setAdapter(adapter);
            searchFilter.setAdapter(adapter);

            progressDialog.dismiss();
            Log.i("infoClosingConnection", "Connection closed.");
            // END OF CON
        }
    }

}















