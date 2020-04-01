package com.example.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientActivity extends AppCompatActivity {

    private String day;
    private DatabaseReference pDatabase;
    private FirebaseAuth pAuth;
    private ListView pListView;


    private String name[],phone[],token[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);

        Intent intent = getIntent();
        day = intent.getStringExtra("day");
        pListView = (ListView)findViewById(R.id.available_lst);
        pAuth = FirebaseAuth.getInstance();
        pDatabase = FirebaseDatabase.getInstance().getReference().child("doctorName").child(pAuth.getCurrentUser().getUid()).child("day").child(day.toLowerCase());

        pDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Long length = dataSnapshot.getChildrenCount();
                name = new String[length.intValue()-2];
                phone = new String[length.intValue()-2];
                token = new String[length.intValue()-2];
                Log.d("length" , length+"");

                for(DataSnapshot ds : dataSnapshot.getChildren() ){

                    String temp = ds.getKey();

                    if( !temp.isEmpty() && !temp.equals("count") && !temp.equals("updation")) {
                        token[Integer.parseInt(temp)-1] = temp;
                        name[Integer.parseInt(temp)-1] = ds.child("patient").getValue().toString();

                        phone[Integer.parseInt(temp)-1] = ds.child("contact").getValue().toString();
                    }
                }

                customSimpleAdapterListView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    private void customSimpleAdapterListView()
    {


        ArrayList<Map<String,Object>> itemDataList = new ArrayList<Map<String,Object>>();;

        int titleLen = name.length;
        for(int i =0; i < titleLen; i++) {
            Map<String,Object> listItemMap = new HashMap<String,Object>();
            listItemMap.put("token", "Token Number : " + token[i]);
            listItemMap.put("name", "Name : " + name[i].toUpperCase());
            listItemMap.put("phone", "Contact : " + phone[i]);
            itemDataList.add(listItemMap);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this,itemDataList,R.layout.activity_patient,
                new String[]{"token","name","phone"},new int[]{ R.id.userTitle, R.id.userDesc,R.id.userPhone});

        pListView.setAdapter(simpleAdapter);

        pListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

                Log.d("i ", i+"");

                DialogBox dialogBox = new DialogBox(day,token[i]);
                dialogBox.show(getSupportFragmentManager(),"dialog");

            }
        });

    }
}
