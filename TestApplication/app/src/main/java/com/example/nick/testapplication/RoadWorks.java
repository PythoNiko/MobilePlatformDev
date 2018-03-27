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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class RoadWorks extends AppCompatActivity {

    /*
        FILE:   RoadWorks.java
        CLASS:  RoadWorks

        NAME:   NICK CONNELL
        ID:     S1623944

        THIS CLASS IS RESPONSIBLE FOR PROCESSING AND STORING OF DATA FROM XML FEED
     */

    // LIST VIEW OF INCIDENTS
    ListView RoadworksRSS;
    ArrayList<String> RoadWorks;

    //SEARCH OF INCIDENTS
    ArrayList<String> roadworkSearch;
    AutoCompleteTextView searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_road_works);

        // LIST VIEW OF INCIDENTS
        RoadworksRSS = (ListView) findViewById(R.id.listViewRoadWorksRSS);
        RoadWorks = new ArrayList<String>();

        //SEARCH OF INCIDENTS
        searchFilter = (AutoCompleteTextView) findViewById(R.id.searchBar);
        roadworkSearch = new ArrayList<String>();

        RoadworksRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

        new ProcessRoadworkFeed().execute();

    }

    public InputStream getInputStream(URL url){
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public class ProcessRoadworkFeed extends AsyncTask<Integer, Void, Exception> {

        /*
        FILE:   CurrentIncidents.java
        CLASS:  ProcessCurrIncFeed

        NAME:   NICK CONNELL
        ID:     S1623944

        THIS CLASS IS RESPONSIBLE FOR HANDLING DATA SUPPLIED FROM THE XML FEED
        */

        ProgressDialog progressDialog = new ProgressDialog(RoadWorks.this);
        Exception exception = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading Feed...");
            progressDialog.show();
        }

        @Override
        protected Exception doInBackground(Integer... integers) {
            try{
                // GET URL OF SPECIFIC RSS FEED
                URL url = new URL("https://trafficscotland.org/rss/feeds/roadworks.aspx");

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
                                RoadWorks.add(xpp.nextText());
                                listCount++;
                                Log.i("ItemAdded", "Item has been added to currentIncident List.");
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                RoadWorks.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                RoadWorks.add(xpp.nextText());
                                RoadWorks.add("---------------------------------------------------------------------------");

                            }
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
                if(listCount == 0){
                    RoadWorks.add("No items available");
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

            ArrayAdapter<String> searchAdapter = new ArrayAdapter<String>(RoadWorks.this, android.R.layout.simple_list_item_1, roadworkSearch);

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(RoadWorks.this, android.R.layout.simple_list_item_1, RoadWorks);

            RoadworksRSS.setAdapter(adapter);
            searchFilter.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

}