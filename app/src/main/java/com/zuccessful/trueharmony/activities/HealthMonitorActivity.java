package com.zuccessful.trueharmony.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.MeasurementFragment;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.fragments.SarthiFrag;
import com.zuccessful.trueharmony.fragments.StatsFragment;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HealthMonitorActivity extends AppCompatActivity {
    long startTime;
    long endTime;
    String TAG = "HealthMonitorModule";
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    FragmentManager fragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
                    return true;
                case R.id.navigation_info:
//                    ---previous card layout code---
                    //putting extra info to fragment
//                    Bundle bundle = new Bundle();
//                    ArrayList<String> questions= new ArrayList<>();
//                    questions.addAll(Arrays.asList( getResources().getStringArray(R.array.questions)));
//                    bundle.putStringArrayList("questions",questions);
//                    ArrayList<String> answers= new ArrayList<>();
//                    answers.addAll(Arrays.asList( getResources().getStringArray(R.array.answers)));
//                    bundle.putStringArrayList("answers", answers);
//                    bundle.putString("Module","Physical Health Monitor");
//                    //fragment
//                    InformationFragment infoFrag = new InformationFragment();
//                    infoFrag.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();
/*
                    PdfRenderFragment p= new PdfRenderFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("filename","health.pdf");
                    bundle.putString("filename_hindi","physical_health_monitoring_hindi.pdf");

                    p.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, p).commit();*/
                    Intent pIntent = new Intent(HealthMonitorActivity.this, PDFRenderActivity.class);
                    pIntent.putExtra("filename","health.pdf");
                    pIntent.putExtra("filename_hindi","physical_health_monitoring_hindi.pdf");

                    startActivity(pIntent);
                    return true;
                case R.id.navigation_progress:
                    fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
                    return true;
                case R.id.navigation_sarthi:
                    Bundle b1=new Bundle();
                    b1.putString("saarthi",getApplicationContext().getString(R.string.health_saarthi));
                    SarthiFrag f = new SarthiFrag();
                    f.setArguments(b1);
                    fragmentTransaction.replace(R.id.fragment_content, f).commit();
                    return true;

            }
            return false;
        }
    };
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
         Utilities.changeLanguage(this);

        setContentView(R.layout.activity_health_monitor_tabbed);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_health_monitor_tabbed));

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment(),"measurement_frag_tag").commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = fragmentManager.findFragmentByTag("measurement_frag_tag");
        if(fragment==null){
            Toast.makeText(this, "fragment is null", Toast.LENGTH_SHORT).show();
        }else {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private interface  FirestroreCallBack{
        void onCallBack(Long time);
    }
    protected void fetchObject(final FirestroreCallBack firestroreCallBack){



        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

//        final DocumentReference documentReference;
//        documentReference = db.collection("time_spent/")
//                .document(app.getAppUser(null)
//                        .getId())
//                .collection(sdf.format(new Date())).document("Physical Health Monitor");

//        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot documentSnapshot=task.getResult();
//                    Activity_Usage activity_usage = documentSnapshot.toObject(Activity_Usage.class);
//                    if(activity_usage==null){
//                        long time = 0;
//                        firestroreCallBack.onCallBack(time);
//                        return;
//                    }
//                    Log.d(TAG, "oldtime on database : " + Long.toString(activity_usage.getTime()));
//                    firestroreCallBack.onCallBack(activity_usage.getTime());
//
//                }else{
//                    Log.d(TAG,"Error: Can't get Activity_Usage",task.getException());
//                }
//
//            }
//
//        });

    }
    @Override
    protected void onStop()
    {
        endTime = System.currentTimeMillis();
        final long timeSpend = endTime - startTime;
        //update time
        fetchObject(new FirestroreCallBack() {
            @Override
            public void onCallBack(Long totaltime) {
                totaltime += timeSpend;
                //change format of time
                String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(totaltime),
                        TimeUnit.MILLISECONDS.toMinutes(totaltime) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(totaltime)),
                        TimeUnit.MILLISECONDS.toSeconds(totaltime) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(totaltime)));


                //Log.d(TAG, "OLDTIME " + Long.toString(oldtimespend));

                //create object
                Activity_Usage au = new Activity_Usage(hms,totaltime,"Physical Health Monitor");
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                //add to database
                try {db.collection("time_spent/")
                        .document(app.getAppUser(null)
                                .getId())
                        .collection(sdf.format(new Date()))
                        .document("Physical Health Monitor")
                        .set(au);
                    Map<String, Object> today_date = new HashMap<>();
                    SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                    today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());
                    Map<String, Object> module_name = new HashMap<>();
                    module_name.put(au.getActivity_name(), au.getActivity_name());
                    db.collection("time_dates/"+app.getPatientID()+"/dates").document("module").set(module_name, SetOptions.merge());
                } catch (Exception e) { Log.d(TAG ,"ERROR : can't get object", e); }

            }
        });





        super.onStop();
    }
}
