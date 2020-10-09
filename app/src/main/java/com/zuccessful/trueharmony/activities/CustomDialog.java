package com.zuccessful.trueharmony.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;

import java.util.HashMap;
import java.util.Map;


public class CustomDialog extends Activity {
 
    private Button buttonClick;
    private FirebaseFirestore db;
    private SakshamApp app;
    private Patient patient;
    CheckBox check,check2;
    Button agree;
      SharedPreferences preferences;
    public void onCreate(Bundle savedInstanceState) {
 
        super.onCreate(savedInstanceState);
     //   setContentView(R.layout.custom_dialog_main);

        app = SakshamApp.getInstance();
        patient = app.getAppUser(null);
        db = app.getFirebaseDatabaseInstance();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);


// Create custom dialog object
        final Dialog dialog = new Dialog(CustomDialog.this);
        // Include dialog.xml file
        dialog.setContentView(R.layout.dialog);
        // Set dialog title
        dialog.setTitle("Consent Form");

        // set values for custom dialog components - text, image and button
        TextView text = (TextView) dialog.findViewById(R.id.textDialog1);

          /*      ImageView image = (ImageView) dialog.findViewById(R.id.imageDialog);
                //image.setImageResource(R.drawable.image0);*/

        dialog.show();
        check2=(CheckBox)dialog.findViewById(R.id.checked_consent);

        check=(CheckBox)dialog.findViewById(R.id.checked_consent2);
        agree= (Button)dialog.findViewById(R.id.agree);
       /* check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {


            }
        });*/

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode==KeyEvent.KEYCODE_BACK){
            Map<String, Boolean> values = new HashMap<>();

            values.put("consent", false);

            db.collection("consent_data/" + RegisterActivity.registered_mail + "/patient").document("info").
                    set(values).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(CustomDialog.this, "Successfully Saved your consent as false", Toast.LENGTH_SHORT).show();
                }
            });
            SharedPreferences.Editor editor = preferences.edit();

            editor.putBoolean("consent",false);
            editor.apply();
            finish();
        }
        return super.onKeyDown(keyCode, event);

    }
}