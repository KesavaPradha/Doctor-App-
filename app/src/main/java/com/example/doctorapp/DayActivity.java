package com.example.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.PathEffect;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DayActivity extends AppCompatActivity {

    private ListView aListView;
    private DatabaseReference aDatabase,dDatabase;
    private FirebaseAuth dAuth;

    String doctor,branch;
    String day[]={};
    String start[] = {};
    String count[] = {};

    ArrayAdapter<String> aAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);

        aListView = (ListView)findViewById(R.id.available_lst);
        dAuth = FirebaseAuth.getInstance();

        FirebaseUser user = dAuth.getCurrentUser();
        String currentUser = user.getUid();

        Log.d("user " , currentUser);

        aDatabase = FirebaseDatabase.getInstance().getReference().child("doctorName").child(currentUser);




        aDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                doctor = dataSnapshot.child("name").getValue().toString();
                Log.d("name ", doctor);
                branch = dataSnapshot.child("branch").getValue().toString();
                Log.d("branch " , branch);

                dDatabase = FirebaseDatabase.getInstance().getReference().child("doctors").child(branch);

                dDatabase.child(doctor).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        Long length = dataSnapshot.getChildrenCount()-3;
                        Log.d( "length", " " + length);
                        day = new String[length.intValue()];
                        start = new String[length.intValue()];
                        count = new String[length.intValue()];
                        int i = 0;

                        for( DataSnapshot ds : dataSnapshot.getChildren()){

                            String value = ds.getKey().toUpperCase();
                            if( !( value.equals("EXPERIENCE") || value.equals("PHONE") || value.equals("ID")) ) {
                                day[i] = value;

                                start[i] = ds.child("start").getValue().toString() + " - " + ds.child("end").getValue().toString();
                                count[i] = ds.child("count").getValue().toString();

                                Log.d("day ", start[i]);
                                i++;
                            }


                        }

                        customSimpleAdapterListView();

                        //aAdapter = new ArrayAdapter<>(getApplicationContext(),android.R.layout.simple_list_item_1,day);
                        //aListView.setAdapter(aAdapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //Log.d("doctor ", doctor + "  " + branch);


    }
    private void customSimpleAdapterListView()
    {


        ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

        int titleLen = day.length;
        for(int i =0; i < titleLen; i++) {
            Map<String,Object> listItemMap = new HashMap<String,Object>();
            listItemMap.put("title", day[i]);
            listItemMap.put("description", start[i]);
            listItemMap.put("count", "Patient booked : " +  count[i]);
            itemDataList.add(listItemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,R.layout.activity_day,
                new String[]{"title","description","count"},new int[]{ R.id.userTitle, R.id.userDesc,R.id.userCount});

        aListView.setAdapter(simpleAdapter);

        aListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d("i ", i+"");

                if( Integer.parseInt(count[i] ) == 0 ){

                    Toast.makeText(getApplicationContext(), "No patients Booked", Toast.LENGTH_SHORT).show();

                } else {

                    Intent intent = new Intent( getApplicationContext(), PatientActivity.class );

                    Object clickItemObj = adapterView.getAdapter().getItem(i);
                    HashMap clickItemMap = (HashMap)clickItemObj;
                    String itemTitle = (String)clickItemMap.get("title");

                    intent.putExtra("day", itemTitle );

                    startActivity(intent );

                }


            }
        });

    }
}
