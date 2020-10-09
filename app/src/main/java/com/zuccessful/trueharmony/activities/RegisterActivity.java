package com.zuccessful.trueharmony.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;

import java.util.HashMap;
import java.util.Map;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;

public class RegisterActivity extends AppCompatActivity {

    private Button registerSubmit;
    private SakshamApp app;
    private EditText passText;
    private EditText conpassText;
    private EditText emailText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private final String TAG = "REGISTERTAG";
    CheckBox check,check2;
    SharedPreferences preferences;
    public static String registered_mail;
    Button agree;
    Boolean consent=false;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        changeLanguage(getApplicationContext());

        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        emailText = (EditText) findViewById(R.id.editText_name_register);
        passText = (EditText) findViewById(R.id.editText_password_register);
        conpassText = (EditText) findViewById(R.id.editText_confirmPassword_register);
        registerSubmit = (Button) findViewById(R.id.button_register_submit);
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();

        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        progressDialog= new ProgressDialog(this);
    }

    private void register()
    {

        final String email = emailText.getText().toString().trim();
        final String pass = passText.getText().toString().trim();
        String confirmPass  = conpassText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass) || TextUtils.isEmpty(confirmPass))
        {
            Toast.makeText(RegisterActivity.this,"Field empty",Toast.LENGTH_LONG).show();
        }

        else if (!pass.equals(confirmPass))
        {
            Toast.makeText(RegisterActivity.this,"Passwords don't match",Toast.LENGTH_LONG).show();
        }

        else {

            preferences = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
            Boolean consent = preferences.getBoolean("consent_"+email, false);
            if(!consent)
            {
            app = SakshamApp.getInstance();
             db = app.getFirebaseDatabaseInstance();
            preferences = PreferenceManager.getDefaultSharedPreferences(this);


// Create custom dialog object
            final Dialog dialog = new Dialog(RegisterActivity.this);
            // Include dialog.xml file
            dialog.setContentView(R.layout.dialog);
            // Set dialog title
            dialog.setTitle("Consent Form");

            // set values for custom dialog components - text, image and button
            TextView text = (TextView) dialog.findViewById(R.id.textDialog1);

          /*      ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
                //image.setImageResource(R.drawable.image0);*/

            dialog.show();
                preferences = PreferenceManager.getDefaultSharedPreferences(this);

            check = (CheckBox) dialog.findViewById(R.id.checked_consent);
                check2 = (CheckBox) dialog.findViewById(R.id.checked_consent2);
                agree= (Button)dialog.findViewById(R.id.agree);


                agree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Map<String, Boolean> values = new HashMap<>();
                       // Toast.makeText(RegisterActivity.this,check.isChecked()+"\n"+check2.isChecked(), Toast.LENGTH_SHORT).show();

                        if(check.isChecked() && check2.isChecked()){
                          /*  values.put("consent", true);

                            db.collection("consent_data/" +RegisterActivity.registered_mail + "/patient").document("info").
                                    set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this, "Successfully Saved your consent", Toast.LENGTH_SHORT).show();
                                }
                            });

                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putBoolean("consent",true);
                            editor.apply();
                            Intent i = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(i);*/
                            values.put("consent", true);


//if the email and password are not empty
                            //displaying a progress dialog

                            progressDialog.setMessage("Registering Please Wait...");
                            progressDialog.show();

                            mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()  )
                                    {


                                        db.collection("consent_data/" + RegisterActivity.registered_mail + "/patient").document("info").
                                                set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(RegisterActivity.this, "Successfully Saved your consent", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                        SharedPreferences.Editor editor = preferences.edit();

                                        editor.putBoolean("consent_"+email, true);
                                        editor.apply();
                                        registered_mail=email;
                                        dialog.dismiss();

                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        startActivity(i);
                                        Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                        finish();


                                    }
                                    else
                                    {
                                        dialog.dismiss();
                                        Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();


                                }
                            });


                        }
                        else{
                            values.put("consent", false);

                            db.collection("consent_data/" + RegisterActivity.registered_mail + "/patient").document("info").
                                    set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //  Toast.makeText(CustomDialog.this, "Successfully Saved your consent as false", Toast.LENGTH_SHORT).show();
                                }
                            });
                            Toast.makeText(RegisterActivity.this, "Kindly agree to both PICF and Disclaimer conditions", Toast.LENGTH_SHORT).show();

                            SharedPreferences.Editor editor = preferences.edit();

                            editor.putBoolean("consent",false);
                            editor.apply();
                            dialog.dismiss();
                         //   finish();

                        }
                    }
                });

         /*       check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


                    final Map<String, Boolean> values = new HashMap<>();

                    if (b) {
                        values.put("consent", true);


                        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()  )
                                {


                                    db.collection("consent_data/" + RegisterActivity.registered_mail + "/patient").document("info").
                                            set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(RegisterActivity.this, "Successfully Saved your consent", Toast.LENGTH_SHORT).show();
                                        }
                                    });


                                    SharedPreferences.Editor editor = preferences.edit();

                                    editor.putBoolean("consent_"+email, true);
                                    editor.apply();
                                    registered_mail=email;

                                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(i);
                                    Toast.makeText(RegisterActivity.this,"Registration Successful",Toast.LENGTH_LONG).show();
                                    finish();


                                }
                                else
                                {
                                    dialog.dismiss();
                                    Toast.makeText(RegisterActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                    }
                }
            });*/

        }

            else{

                Toast.makeText(RegisterActivity.this,"Already Registered!Please login",Toast.LENGTH_LONG).show();
                finish();
            }}




        }


}

