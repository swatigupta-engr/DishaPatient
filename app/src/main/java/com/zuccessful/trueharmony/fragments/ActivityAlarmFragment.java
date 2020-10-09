package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.adapters.ActivityAlarmAdapter;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.ArrayList;

public class ActivityAlarmFragment extends Fragment {
    private ArrayList<DailyRoutine> alarms ;
    private LinearLayout addAlarmView;
    private RecyclerView mAlarmListView;
    private ActivityAlarmAdapter adapter;
    private Context mContext;

    public ActivityAlarmFragment() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v= inflater.inflate(R.layout.fragment_activity_alarm, container, false);
        addAlarmView = v.findViewById(R.id.add_alarm_btn);
        mAlarmListView = v.findViewById(R.id.alarm_record_list);
        mContext=getContext();
        adapter = new ActivityAlarmAdapter(mContext,alarms);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mAlarmListView.setLayoutManager(layoutManager);
        mAlarmListView.setAdapter(adapter);


        addAlarmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), AddDailyRoutActivity.class));
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        alarms = Utilities.getListOfDailyRoutineAlarms();
        if (alarms!=null && alarms.size() > 0)
        {
            Log.d("saumya","setting alarms of size "+alarms.size()+" in adapter");
            adapter.replaceAlarms(alarms);
            mAlarmListView.setVisibility(View.VISIBLE);

        }
    }

    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
           // mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
