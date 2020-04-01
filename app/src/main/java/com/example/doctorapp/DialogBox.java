package com.example.doctorapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class DialogBox extends AppCompatDialogFragment {

    private DatabaseReference dDatabase,aDatabase,uDatabase;
    private FirebaseAuth dAuth;
    String branch, doctor, day, id;
    int count,token;

    public DialogBox( String day,String token){

        this.day = day.toLowerCase();
        this.token = Integer.parseInt(token);

    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder dBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater dInflator = getActivity().getLayoutInflater();
        final View dView = dInflator.inflate(R.layout.dialog,null);


        dAuth = FirebaseAuth.getInstance();
        aDatabase = FirebaseDatabase.getInstance().getReference().child("doctorName").child(dAuth.getCurrentUser().getUid());

        aDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                branch = dataSnapshot.child("branch").getValue().toString();
                Log.d("branch" ,branch);
                doctor = dataSnapshot.child("name").getValue().toString();
               id = dataSnapshot.child("day").child(day.toLowerCase()).child(token+"").child("id").getValue().toString();
                //Log.d("day",id);
                Log.d("name ",doctor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*aDatabase.child("day").child(day.toLowerCase()).child(token+"").child("id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                id = dataSnapshot.getValue().toString();
                Log.d("id ",id);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //Log.d("id ",id);*/



        dBuilder.setView(dView).setTitle("Updation Confirmation")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {



                dDatabase = FirebaseDatabase.getInstance().getReference().child("doctors").child(branch).child(doctor).child(day);
                uDatabase = FirebaseDatabase.getInstance().getReference().child("User");

                dDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {



                        count = Integer.parseInt(dataSnapshot.child("count").getValue().toString());
                        Log.d("count ",count+"");
                        count=count-1;
                        Log.d("count next ", count+"");

                        dDatabase.child("count").setValue(count);

                        aDatabase.child("day").child(day).child("count").setValue(count);

                        aDatabase.child("day").child(day).child("updation").setValue(token);

                        aDatabase.child("day").child(day).child(token+"").getRef().removeValue();


                       // uDatabase.child(id).child("bookList").child(day).getRef().removeValue();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });

        return dBuilder.create();

    }

}