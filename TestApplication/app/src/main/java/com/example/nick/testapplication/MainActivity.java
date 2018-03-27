package com.example.nick.testapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void CurrentIncidents(View view){
        Intent openCurrentIncidents = new Intent(this, CurrentIncidents.class);
        startActivity(openCurrentIncidents);
    }

    public void RoadWorks(View view){
        Intent openRoadWorks = new Intent(this, RoadWorks.class);
        startActivity(openRoadWorks);
    }

    public void PlannedRoadWorks(View view){
        Intent openPlannedRoadWorks = new Intent(this, PlannedRoadWorks.class);
        startActivity(openPlannedRoadWorks);
    }

    public void Floodline(View view){
        Intent openFloodline = new Intent(this, Floodline.class);
        startActivity(openFloodline);
    }

    public void PlanYourTJourney(View view){
        Intent openPlanYourJourney = new Intent(this, PlanYourJourney.class);
        startActivity(openPlanYourJourney);
    }

    /* The ViewGroup subclass is the base class for layouts, which are invisible containers
        that hold other Views (or other ViewGroups) and define their layout properties. */
}
