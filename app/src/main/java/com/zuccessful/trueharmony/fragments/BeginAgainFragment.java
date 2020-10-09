package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddBeginAgainActivity;
import com.zuccessful.trueharmony.adapters.BeginAgainAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.WeeklyTask;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class BeginAgainFragment extends Fragment {

    private SakshamApp app;
    private FirebaseFirestore db;
    private ArrayList<WeeklyTask> tasks=new ArrayList<>();
    private ArrayList<WeeklyTask> tasks_edited=new ArrayList<>();

    private ArrayList<WeeklyTask> tasks_filtered_by_subtasks=new ArrayList<>();

    private LinearLayout addMedView;
    private RecyclerView mMedListView;
    private TextView mBlankMedRecord;
    private ProgressBar progressBar;
    private BeginAgainAdapter adapter;
    private Context mContext;
    long startTime;
    long endTime;
    String TAG = "MedManageFrag";

    public BeginAgainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startTime = System.currentTimeMillis();
        mContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_manage, container, false);

        addMedView = view.findViewById(R.id.add_med_btn);
        mMedListView = view.findViewById(R.id.med_record_list);
        mBlankMedRecord = view.findViewById(R.id.blank_med_record);
        progressBar = view.findViewById(R.id.progressbar);


        Log.v("Resume",tasks+"");
        adapter = new BeginAgainAdapter(tasks);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mMedListView.setLayoutManager(layoutManager);
        mMedListView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);

        addMedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMedRecord(view);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Resume","resume");

        updateMedRecords();

    }

    public void updateMedRecords() {

        tasks_edited.clear();
        tasks_filtered_by_subtasks.clear();
        tasks = Utilities.getListOfWeeklytasks();
        Log.v("swati______________",tasks+"");

        try {
            for (int i = 0; i < tasks.size(); i++) {
                if (Utilities.getListofSubtasks(tasks.get(i)).size() > 0) {
                    tasks_edited.add(tasks.get(i));
                }
            }
        }catch (Exception e){}
        tasks_filtered_by_subtasks=new ArrayList<>();
        if(tasks==null)
        {
            tasks = new ArrayList<>();
        }
        if(tasks_edited==null)
        {
            tasks_edited = new ArrayList<>();
        }

        for(int i=0;i<tasks_edited.size();i++){
            Log.v("tasks______________",tasks_edited.get(i)+"");

            if(Utilities.getListofSubtasks(tasks_edited.get(i)).size()>0){
                tasks_filtered_by_subtasks.add(tasks_edited.get(i));
            }
        }
        progressBar.setVisibility(View.GONE);
         adapter.replaceMeds(tasks_filtered_by_subtasks);
        mBlankMedRecord.setVisibility(View.GONE);
        mMedListView.setVisibility(View.VISIBLE);
/*
        for(int i=0;i<tasks_filtered_by_subtasks.size();i++){
            Log.d("saumya"," tasks filtered:"+tasks.get(i).getName()+"-------"+tasks_filtered_by_subtasks.get(i).getSubtasks());

        }*/
//        Log.d("saumya"," Medications: "+tasks.get(0).getSubtasks());
    }

    public void addNewMedRecord(View view) {
        startActivity(new Intent(getContext(), AddBeginAgainActivity.class));
     }

    private interface  FirestroreCallBack{
        void onCallBack(Long time);
    }
    protected void fetchObject(final FirestroreCallBack firestroreCallBack)
    {
        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        final DocumentReference documentReference;


    }
    @Override
    public void onStop()
    {
        endTime = System.currentTimeMillis();
        final long timeSpend = endTime - startTime;


        super.onStop();
    }
}