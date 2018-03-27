package com.example.nick.testapplication;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Floodline extends AppCompatActivity {

    /*
        FILE:   Floodline.java
        CLASS:  Floodline

        NAME:   NICK CONNELL
        ID:     S1623944

        THIS CLASS IS RESPONSIBLE FOR PROCESSING AND STORING OF DATA FROM XML FEED
     */

    ListView FloodlineRSS;
    ArrayList<String> floodline;
    ArrayList<String> description;
    ArrayList<String> link;
    ArrayList<String> georsspoint;
    ArrayList<String> author;
    ArrayList<String> comments;
    ArrayList<String> pubDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floodline);

        FloodlineRSS = (ListView) findViewById(R.id.listViewFloodlineRSS);

        floodline = new ArrayList<String>();
        description= new ArrayList<String>();
        link = new ArrayList<String>();
        georsspoint = new ArrayList<String>();
        author = new ArrayList<String>();
        comments = new ArrayList<String>();
        pubDate = new ArrayList<String>();

        FloodlineRSS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

        new ProcessFloodlineFeed().execute();

    }

    public InputStream getInputStream(URL url){
        try {
            return url.openConnection().getInputStream();
        }
        catch (IOException e) {
            return null;
        }
    }

    public class ProcessFloodlineFeed extends AsyncTask<Integer, Void, Exception> {

        /*
        FILE:   Floodline.java
        CLASS:  ProcessFloodlineFeed

        NAME:   NICK CONNELL
        ID:     S1623944

        THIS CLASS IS RESPONSIBLE FOR HANDLING DATA SUPPLIED FROM THE XML FEED
        */

        ProgressDialog progressDialog = new ProgressDialog(Floodline.this);
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
                URL url = new URL("http://floodline.sepa.org.uk/feed/");

                // CREATE INSTANCE OF PARSER FACTORY
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

                // SPECIFIES THAT THE PRODUCED PARSER WILL PROVIDE SUPPORT FOR XML NAMESPACES
                factory.setNamespaceAware(false);

                // CREATE INSTANCE OF PARSER
                XmlPullParser xpp = factory.newPullParser();

                // TARGET TO PULL DATA FROM INCLUDING ENCODING SCHEME
                xpp.setInput(getInputStream(url), "utf-8");

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
                                floodline.add(xpp.nextText());
                                listCount++;
                                Log.i("ItemAdded", "Item has been added to currentIncident List.");
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("description")) {
                            if (insideItem) {
                                floodline.add(xpp.nextText());
                            }
                        }
                        else if(xpp.getName().equalsIgnoreCase("pubDate")) {
                            if (insideItem) {
                                floodline.add(xpp.nextText());
                                floodline.add("---------------------------------------------------------------------------");
                            }
                        }
                    }
                    else if(eventType == XmlPullParser.END_TAG && xpp.getName().equalsIgnoreCase("item")){
                        insideItem = false;
                    }
                    eventType = xpp.next();
                }
                if(listCount == 0){
                    floodline.add("No items available");
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

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Floodline.this, android.R.layout.simple_list_item_1, floodline);

            FloodlineRSS.setAdapter(adapter);

            progressDialog.dismiss();
        }
    }

}
