package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.fragments.SarthiFrag;
import com.zuccessful.trueharmony.utilities.Utilities;

public class DietActivity extends AppCompatActivity {
    long startTime;
    long endTime;
    String TAG = "DietModule";
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
////                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
//                    DietModuleFragment dietFrag = new DietModuleFragment();
//                    fragmentTransaction.replace(R.id.fragment_content_diet, dietFrag).commit();
                    return true;
                case R.id.navigation_info:
                    //previous code for cardview
//                    Bundle bundle = new Bundle();
//                    ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_que)));
//                    bundle.putStringArrayList("questions", questions);
//                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_ans)));
//                    bundle.putStringArrayList("answers", answers);
//                    bundle.putString("Module","Diet");
//                    //fragment
//                    InformationFragment infoFrag = new InformationFragment();
//                    infoFrag.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.fragment_content_diet, infoFrag).commit();


                    PdfRenderFragment p= new PdfRenderFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("filename","diet.pdf");
                    bundle.putString("filename_hindi","eating_right_hindi.pdf");

                    p.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content_diet, p).commit();

                    return true;
                case R.id.navigation_progress:
//                    fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
//                    DietStatsFrag dietStatsFrag = new DietStatsFrag();
//                    fragmentTransaction.replace(R.id.fragment_content_diet,dietStatsFrag).commit();
                    return true;
                case R.id.navigation_sarthi:
                    Bundle b1=new Bundle();
                    b1.putString("saarthi",getApplicationContext().getString(R.string.eat_saarthi));
                    SarthiFrag f = new SarthiFrag();
                    f.setArguments(b1);
                    fragmentTransaction.replace(R.id.fragment_content_diet, f).commit();
                    return true;
            }
            return false;

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        setContentView(R.layout.activity_diet);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(2).setVisible(false);
        navigation.getMenu().getItem(0).setVisible(false);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setTitle(getResources().getString(R.string.diet));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Bundle bundle = new Bundle();
//        ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_que)));
//        bundle.putStringArrayList("questions", questions);
//        ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_ans)));
//        bundle.putStringArrayList("answers", answers);
//        bundle.putString("Module","Diet");
//        InformationFragment infoFrag = new InformationFragment();
//        infoFrag.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.fragment_content_diet, infoFrag).commit();

        PdfRenderFragment p= new PdfRenderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filename","diet.pdf");
        bundle.putString("filename_hindi","eating_right_hindi.pdf");

        p.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_content_diet, p).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }}

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }
}
