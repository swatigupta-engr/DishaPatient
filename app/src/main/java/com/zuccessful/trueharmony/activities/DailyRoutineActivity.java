package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.ActivityAlarmFragment;
import com.zuccessful.trueharmony.fragments.AddActivityPopUpFragment;
import com.zuccessful.trueharmony.fragments.DailyFragmentActivity;
import com.zuccessful.trueharmony.fragments.DailyRoutine_gb;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.fragments.SarthiFrag;
import com.zuccessful.trueharmony.utilities.Utilities;

//import com.zuccessful.trueharmony.fragments.DailyRoutineManageFrag;

public class DailyRoutineActivity extends AppCompatActivity implements AddActivityPopUpFragment.OnFragmentInteractionListener , ActivityAlarmFragment.OnFragmentInteractionListener {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.fragment_content, new DailyRoutine_gb()).commit();
                    return true;
                case R.id.navigation_info:
                    //putting extra info to fragment
//                    Bundle bundle = new Bundle();
//                    ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.daily_routine_que)));
//                    bundle.putStringArrayList("questions", questions);
//                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.daily_routine_ans)));
//                    bundle.putStringArrayList("answers", answers);
//                    String module = "Daily Routine";
//                    bundle.putString("Module", module);
//                    Log.d("DailyRoutineActivity", "onNavigationItemSelected: navigation_dashboard");
//                    //fragment
//                    InformationFragment infoFrag = new InformationFragment();
//                    infoFrag.setArguments(bundle);
//                    fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();


                    PdfRenderFragment p= new PdfRenderFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("filename", "daily_routine.pdf");
                    bundle.putString("filename_hindi", "daily_routine_hindi.pdf");

                    p.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, p).commit();
                    return true;
                case R.id.navigation_progress:
                // fragmentTransaction.replace(R.id.fragment_content, new ActivityStatsFrag()).commit();
                    fragmentTransaction.replace(R.id.fragment_content, new DailyFragmentActivity()).commit();

                    return true;
                case R.id.navigation_sarthi:
                    Bundle b1=new Bundle();
                    b1.putString("saarthi",getApplicationContext().getString(R.string.daily_saarthi));
                    SarthiFrag f = new SarthiFrag();
                    f.setArguments(b1);
                    fragmentTransaction.replace(R.id.fragment_content, f).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(Utilities.onAttach(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.changeLanguage(this);
        setContentView(R.layout.activity_iadl);
        Log.d("DailyRoutineActivity", "onCreate: started");

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.daily_routine));
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Log.d("DailyRoutineActivity", "onCreate: Initialized the fragment transaction");
        fragmentTransaction.replace(R.id.fragment_content, new DailyRoutine_gb()).commit();
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
    public void onFragmentInteraction(Uri uri) {

    }
}