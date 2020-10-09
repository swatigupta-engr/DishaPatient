package com.zuccessful.trueharmony.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class PackageChangeReceiver extends BroadcastReceiver
{
        private ArrayList<Medication> medications=new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent)
    {
    //this.context=context;
    Uri data = intent.getData();
    Log.d("hi", "Action: " + intent.getAction());
    Log.d("hi", "The DATA: " + data);

    String action=intent.getAction();
        medications = Utilities.getListOfMedication();
          Medication med;
          int size=0;
          try {
               size = medications.size();
          }catch (Exception e){}
        final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();


        if(Intent.ACTION_PACKAGE_REMOVED.equalsIgnoreCase(action)) {


        for(int i=0;i<size;i++){
            med = medications.get(i);
            SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" + SakshamApp.getInstance().getAppUser(null).getId() + "/medication").document(med.getName()).delete();
            for(String t: med.getReminders())
            {
                SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("patient_med_reports/" + SakshamApp.getInstance().getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                        "/" + sdf.format(new Date())).document(med.getName()+" "+t).delete();
            }
        }


    }}}