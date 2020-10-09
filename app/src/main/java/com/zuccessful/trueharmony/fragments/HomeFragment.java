package com.zuccessful.trueharmony.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.BeginAgain;
import com.zuccessful.trueharmony.activities.DailyRoutineActivity;
import com.zuccessful.trueharmony.activities.DietActivity;
import com.zuccessful.trueharmony.activities.HealthMonitorActivity;
import com.zuccessful.trueharmony.activities.IADLActivity;
import com.zuccessful.trueharmony.activities.LibraryActivity;
import com.zuccessful.trueharmony.activities.MedicalAdherenceActivity;
import com.zuccessful.trueharmony.activities.PSychoeducation;
import com.zuccessful.trueharmony.activities.PhysicalActivity;
import com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper;

import static com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper.sendMapAnalytics;

//import com.zuccessful.trueharmony.activities.DailyRoutineActivity;
//import com.zuccessful.trueharmony.activities.DailyRoutineActivity_GB;

public class HomeFragment extends Fragment {
    private LinearLayout mHealthMonitoring;
    private LinearLayout mMedAdherenceCard;
    private LinearLayout mDailyRoutineCard;
    private LinearLayout mIADLCard;
    private LinearLayout libraryCard;
    private LinearLayout mPsychoEduCard;
    private LinearLayout mPhyActivityCard;
//    private LinearLayout iInjScheduleCard;
    private LinearLayout mDietCard;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        mHealthMonitoring = view.findViewById(R.id.phy_health_linear_layout);
        mHealthMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HealthMonitorActivity.class));
            }
        });

        libraryCard = view.findViewById(R.id.libray_linear_layout);
        libraryCard.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                startActivity(new Intent(getContext(), LibraryActivity.class));
            }
        });

//        iInjScheduleCard = view.findViewById(R.id.injection_schedule_linear_layout);
//        iInjScheduleCard.setOnClickListener(new View.OnClickListener(){
//
//            public void onClick(View view){
//                startActivity(new Intent(getContext(), Injection_Schedule.class));
//            }
//        });
        mMedAdherenceCard = view.findViewById(R.id.medical_adherence_linear_layout);
        mMedAdherenceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MedicalAdherenceActivity.class));
            }
        });


        mPhyActivityCard = view.findViewById(R.id.physical_activity_linear_layout);
        mPhyActivityCard.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                startActivity(new Intent(getContext(), PhysicalActivity.class));
            }
        });

        mDailyRoutineCard = view.findViewById(R.id.daily_routine_linear_layout);
        mDailyRoutineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), DailyRoutineActivity.class));
//        startActivity(new Intent(getContext(), DailyRoutineActivity_new.class));
            }
        });

        mDietCard = view.findViewById(R.id.diet_linear_layout);
        mDietCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), DietActivity.class));
            }
        });

        mIADLCard = view.findViewById(R.id.iadl_linear_layout);
        mIADLCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BeginAgain.class));
            }
        });

        mPsychoEduCard= view.findViewById(R.id.psycho_edu_linear_layout);
        mPsychoEduCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), PSychoeducation.class));
                sendMapAnalytics(FirebaseAnalyticsHelper.FirebaseAnalyConst.ACTIVITY_NAME,
                        getString(R.string.psycho_education));

            }
        });

        return view;
    }




}
