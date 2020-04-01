package com.example.doctorapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText rNameEdt;
    private EditText rMailEdt;
    private EditText rKeyEdt;
    private EditText rPasswordEdt;
    private EditText rConfirmPasswordEdt;
    private Button rRegisterBtn;
    private TextView rLoginTxt;
    private FirebaseAuth rAuth;
    private DatabaseReference rDatabase;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        rNameEdt = (EditText)findViewById(R.id.reg_name_edt);
        rMailEdt = (EditText)findViewById(R.id.reg_mail_edt);
        rKeyEdt = (EditText)findViewById(R.id.reg_key_edt);
        rPasswordEdt = (EditText)findViewById(R.id.reg_password_edt);
        rConfirmPasswordEdt = (EditText)findViewById(R.id.reg_confirm_password_edt);
        rRegisterBtn = (Button)findViewById(R.id.reg_register_btn);
        rLoginTxt = (TextView)findViewById(R.id.reg_login_txt);
        rAuth = FirebaseAuth.getInstance();

        rRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = rNameEdt.getText().toString();
                String mail = rMailEdt.getText().toString();
                String phone = rKeyEdt.getText().toString();
                String password = rPasswordEdt.getText().toString();
                String confirmPassword = rConfirmPasswordEdt.getText().toString();

                if( !( TextUtils.isEmpty(name)
                        && TextUtils.isEmpty(mail)
                        && TextUtils.isEmpty(phone)
                        && TextUtils.isEmpty(password)
                        && TextUtils.isEmpty(confirmPassword) ) ) {

                    if( password.length() < 8 ) {

                        Toast.makeText(getApplicationContext(), "Password should have minimum 8 character", Toast.LENGTH_SHORT).show();

                    }else if(!password.equals(confirmPassword ) ){
                        Toast.makeText(getApplicationContext(), "Password does'nt matched...", Toast.LENGTH_SHORT).show();
                    } else {

                        //rRegisterBtn.setClickable(false);
                        progress.setMessage("Signing Up....");
                        progress.show();



                        register(name, mail,password);

                    }

                } else if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "Enter name...", Toast.LENGTH_SHORT).show();
                } else if( TextUtils.isEmpty(mail) ) {
                    Toast.makeText(getApplicationContext(), "Enter mail address...", Toast.LENGTH_SHORT).show();
                } else if( TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "Enter phone number...", Toast.LENGTH_SHORT).show();
                } else if( TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password...", Toast.LENGTH_SHORT).show();
                } else if( TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Password does'nt matched...", Toast.LENGTH_SHORT).show();
                }



            }
        });

        rLoginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent( getApplicationContext(), MainActivity.class );
                startActivity(intent);
                finish();

            }
        });

    }

    private void register(final String name, final String mail, final String password) {

        rAuth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if( task.isSuccessful() ) {

                            Map<String,String> map = new HashMap<String, String>();

                            map.put("email",mail );
                            map.put("name",name);
                            map.put("password",password);

                            rDatabase.child(rAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        Toast.makeText(getApplicationContext(), "Login Successful...", Toast.LENGTH_SHORT).show();
                                        rRegisterBtn.setClickable(true);
                                        progress.dismiss();

                                        Intent intent = new Intent( getApplicationContext(), DayActivity.class );
                                        startActivity(intent);

                                    } else {
                                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progress.dismiss();

                                                if( task.isSuccessful()) {
                                                    Toast.makeText(getApplicationContext(), "Login Failed...Try Again", Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "Server error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                }
                            });



                        } else {
                            progress.dismiss();

                            Toast.makeText(getApplicationContext(), "Sign Up failed..Try Again...", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }
}