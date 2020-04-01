package com.example.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private EditText mEmailEdt;
    private EditText mPasswordEdt;
    private Button mLoginBtn;
    private TextView mRegisterBtn;


    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEmailEdt = (EditText)findViewById(R.id.main_email_edt);
        mPasswordEdt = (EditText)findViewById(R.id.main_password_edt);
        mLoginBtn = (Button)findViewById(R.id.main_login_btn);
        mRegisterBtn = (TextView)findViewById(R.id.main_register_txt);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(MainActivity.this);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( getApplicationContext(), RegisterActivity.class );
            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = mEmailEdt.getText().toString();
                String password = mPasswordEdt.getText().toString();

                if( !(mail.equals("") && password.equals("") )) {

                    mProgress.setMessage("Signing In...");
                    mProgress.show();
                    signIn( mail, password );


                } else if( mail.equals("")){

                    Toast.makeText(getApplicationContext(), "Enter valid mail Id", Toast.LENGTH_SHORT).show();

                } else if ( password.equals( "")) {
                    Toast.makeText(getApplicationContext(), "Enter password", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Enter email and password", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void signIn(final String mail, String password) {

        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if( task.isSuccessful()) {

                    Toast.makeText(getApplicationContext(), "Login Successful..", Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                    Intent intent = new Intent( getApplicationContext(), DayActivity.class );
                    Log.d("id ", mAuth.getCurrentUser().getUid()+"");
                    startActivity(intent);

                    finish();

                }else {

                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Failed...Try Again", Toast.LENGTH_SHORT).show();

                }

            }
        });

    }

}
