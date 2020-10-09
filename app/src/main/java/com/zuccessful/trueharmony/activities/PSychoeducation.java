package com.zuccessful.trueharmony.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.pojo.Activity_Usage;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;

public class PSychoeducation extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] psychoQueDataset;
    long startTime;
    long endTime;
    String TAG = "PsychoEducationModule";

    // private long oldtimespend;
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        changeLanguage(getApplicationContext());

        setContentView(R.layout.activity_psychoeducation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.psycho_education));
        /*Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.psycho_edu_ques);
        setSupportActionBar(myToolbar);*/

    /*
        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        psychoQueDataset = getResources().getStringArray(R.array.psycho_edu_que);

        // specify an adapter (see also next example)
        mAdapter = new PsychoAdapter(psychoQueDataset,this);
        mRecyclerView.setAdapter(mAdapter);*/

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Bundle bundle = new Bundle();
//        ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.psycho_edu_que)));
//        bundle.putStringArrayList("questions", questions);
//        ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.psycho_edu_ans)));
//        bundle.putStringArrayList("answers", answers);
//        ArrayList<String> images = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.psycho_edu_images)));
//        bundle.putStringArrayList("images", answers);
//        bundle.putString("Module","Psycho Education");
//        //fragment
//        InformationFragment infoFrag = new InformationFragment();
//        infoFrag.setArguments(bundle);
//        fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();

        PdfRenderFragment p= new PdfRenderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filename","about_illness.pdf");
        bundle.putString("filename_hindi","about_illness_hindi.pdf");

        p.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_content, p).commit();
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
    private interface  FirestroreCallBack{
        void onCallBack(Long time);
    }

    protected void fetchObject(final FirestroreCallBack firestroreCallBack){



        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        final DocumentReference documentReference;
        documentReference = db.collection("time_spent/")
                .document(app.getAppUser(null)
                        .getId())
                .collection(sdf.format(new Date())).document("Psycho Education");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot=task.getResult();
                    Activity_Usage activity_usage = documentSnapshot.toObject(Activity_Usage.class);
                    if(activity_usage==null){
                        long time = 0;
                        firestroreCallBack.onCallBack(time);
                        return;
                    }
                    Log.d(TAG, "oldtime on database : " + Long.toString(activity_usage.getTime()));
                    firestroreCallBack.onCallBack(activity_usage.getTime());

                }else{
                    Log.d(TAG,"Error: Can't get Activity_Usage",task.getException());
                }

            }

        });

    }
    @Override
    protected void onStop()
    {
        endTime = System.currentTimeMillis();
        final long timeSpend = endTime - startTime;
        //update time
        try {
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
                    Activity_Usage au = new Activity_Usage(hms, totaltime, "Psycho Education");
                    SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

                    //add to database
                    try {
                        db.collection("time_spent/")
                                .document(app.getAppUser(null)
                                        .getId())
                                .collection(sdf.format(new Date()))
                                .document("Psycho Education")
                                .set(au);
                        Map<String, Object> today_date = new HashMap<>();
                        SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                        today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
                        db.collection("time_dates/" + app.getPatientID() + "/dates").document("dates").set(today_date, SetOptions.merge());
                        Map<String, Object> module_name = new HashMap<>();
                        module_name.put(au.getActivity_name(), au.getActivity_name());
                        db.collection("time_dates/" + app.getPatientID() + "/dates").document("module").set(module_name, SetOptions.merge());
                    } catch (Exception e) {
                        Log.d(TAG, "ERROR : can't get object", e);
                    }

                }
            });


        }catch (Exception e){}


        super.onStop();
    }
}


