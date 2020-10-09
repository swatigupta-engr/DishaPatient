package com.zuccessful.trueharmony.fragments;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.DailyRoutineAdapterLocal;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class DailyRoutine_gb extends Fragment {
    private SakshamApp app;
    private static AlarmManager alarmManager;
    private FirebaseFirestore db;
    private FirestoreRecyclerAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    private RecyclerView activityList;
//    private ProgressBar progressBar;
    private Button addActivity,setAlarm;
    RecyclerView.LayoutManager mLayoutManager1;
    Context c;
    ArrayList<String> phyActList, leisureActList;
    String pa[];
    String la[];
    private HashMap<String,ArrayList<Integer>> map;
    private ArrayList<DailyRoutine> alarms ;


    public DailyRoutine_gb() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        c = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_routine_gb, container, false);
        activityList = view.findViewById(R.id.rvActs);
        activityList.setLayoutManager(linearLayoutManager);
//        progressBar = view.findViewById(R.id.progress_bar);
        addActivity = view.findViewById(R.id.add_activity);
        setAlarm=view.findViewById(R.id.set_alarm);
        mLayoutManager1 = new LinearLayoutManager(c);
        activityList.setLayoutManager(mLayoutManager1);
//        progressBar.setVisibility(View.VISIBLE);
        alarms = Utilities.getListOfDailyRoutineAlarms();

        phyActList = Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST);
        leisureActList = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST);
        Log.d("saumya", "original lists: " + Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST) + "\n" + Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST));
        if (phyActList == null) {
            phyActList = new ArrayList<>();
        }

        if (leisureActList == null) {
            leisureActList = new ArrayList<>();
        }

        addActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pa = getResources().getStringArray(R.array.phy_act_pref_arrays);
                la = getResources().getStringArray(R.array.basic_act_pref_arrays);
                ArrayList<String> b_list = new ArrayList<String>();
                Collections.addAll(b_list, la);
                ArrayList<String> p_list = new ArrayList<String>();
                Collections.addAll(p_list, pa);
                Bundle bundle= new Bundle();
                bundle.putStringArrayList("basic",b_list);
                bundle.putStringArrayList("physical",p_list);
                Log.d("saumya",b_list.size()+" "+p_list.size());
                Fragment f=new AddActivityPopUpFragment();
                f.setArguments(bundle);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_content,f).commit();
            }
        });


        fetchActivities_sa();

        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_content,new ActivityAlarmFragment()).commit();
            }
        });
        return view;
    }

//    private void createAddPhysicalActDialoge(final String[] phyActChoices) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(c);
//        builder.setTitle(R.string.pick_activity);
//        builder.setMessage("BASIC ACTIVITIES");
//        builder.setItems(phyActChoices, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                Log.d("saumya", phyActChoices[which] + " " + "selected");
//                String chosen = phyActChoices[which];
//                String type = null;
//                for (int i = 0; i < pa.length; i++) {
//                    if (pa[i] == chosen) {
//                        type = "PHYSICAL_ACTIVITY";
//                    }
//                }
//                if (type == null) {
//                    type = "LEISURE_ACTIVITY";
//                }
//                Log.d("saumya", "chosen type is : " + type);
//
//                if (phyActChoices[which].equalsIgnoreCase(getString(R.string.other))) {
//                    addNewAddPhysicalActDialoge("PHYSICAL_ACTIVITY");
//                } else if (type == "PHYSICAL_ACTIVITY" && phyActList.contains(phyActChoices[which])) {
//                    Toast.makeText(c, R.string.activity_already_added, Toast.LENGTH_SHORT).show();
//                } else if (type == "LEISURE_ACTIVITY" && leisureActList.contains(phyActChoices[which])) {
//                    Toast.makeText(c, R.string.activity_already_added, Toast.LENGTH_SHORT).show();
//                } else {
//                    savePhysicalActData(phyActChoices[which], type);
//                }
//            }
//        });
//        builder.show();
//    }

//    private void addNewAddPhysicalActDialoge(final String type) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(c);
//        builder.setTitle(R.string.enter_activity_name);
//        final EditText input = new EditText(c);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//
//        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String m_Text = input.getText().toString();
//                savePhysicalActData(m_Text, type);
//            }
//        });
//        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }

//    private void savePhysicalActData(String activity, String type) {
//        ArrayList<String> actList = new ArrayList<>();
//        if (type == "PHYSICAL_ACTIVITY") {
//            actList = phyActList;
//        } else if (type == "LEISURE_ACTIVITY") {
//            actList = leisureActList;
//        }
//        if (actList != null) {
//            actList.add(activity);
//        } else {
//            actList = new ArrayList<>();
//            actList.add(activity);
//        }
//        if (type == "PHYSICAL_ACTIVITY") {
//            Utilities.saveListToSharedPref(actList, Constants.KEY_PHY_ACT_LIST);
//            Map<String, Object> data = new HashMap<>();
//            data.put("name", activity);
//
//
//        } else if (type == "LEISURE_ACTIVITY") {
//            Utilities.saveListToSharedPref(actList, Constants.KEY_LEISURE_ACT_LIST);
//
//            Map<String, Object> data = new HashMap<>();
//            data.put("name", activity);
//
//        }
//        Log.d("saumya", "Updated lists: " + Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST) + "\n" + Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST));
//        fetchActivities_sa();
//    }

    private void fetchActivities_sa() {

        Log.d("saumya", "in fetch activities sa");
        phyActList = Utilities.getListFromSharedPref(Constants.KEY_PHY_ACT_LIST);
        leisureActList = Utilities.getListFromSharedPref(Constants.KEY_LEISURE_ACT_LIST);
        ArrayList<String> both = new ArrayList<>();
        both.addAll(phyActList);
        both.addAll(leisureActList);
        Log.d("saumya", " " + both);
        DailyRoutineAdapterLocal mAdapter = new DailyRoutineAdapterLocal(c, both);
        activityList.setAdapter(mAdapter);
        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
        String t_date = sdf.format(new Date());
        try {
            map = Utilities.getDailyLog().get(t_date);
            Log.d("logging map", map + "");

        } catch (Exception e) {
        }
        int v = 0;
        int itr=0;
        if(map!=null){
        Iterator myVeryOwnIterator = map.keySet().iterator();
        while (myVeryOwnIterator.hasNext()) {
            String key = (String) myVeryOwnIterator.next();
            ArrayList<Integer> value =  map.get(key);

            for(int i=0;i<value.size();i++){
                if(value.get(i)==1){
                    v=1;
                }
            }
           // v = value;
            Log.d("logging", "key:" + key + "..value++v" + value);
         /*   try{
            Utilities.saveDailyLog(key, t_date, v, mContext);
                Log.d("logging", "key:" + key + "..value++v" + value);
            }
            catch (Exception e){}*/


            try{
                if(alarms.size()>0)
                    for (int j = 0; j < alarms.size(); j++)
                    {
                        Map<String, Boolean> days = alarms.get(j).getDays();
                        {
                            ArrayList<String> slot_list = alarms.get(j).getReminders();
                            int slots = slot_list.size();
                             for (int k = 0; k < slots; k++)
                            { final String time=slot_list.get(k);
                               // Utilities.saveDailyProgress(key, t_date,0, time,v, mContext);   //0 means NO checkbox selected
                                //Utilities.saveDailyLog(key, t_date, time,v, mContext);   //0 means NO checkbox selected

                                Log.v("data", alarms.get(j).getName()+"..."+key);
                                if(alarms.get(j).getName().equalsIgnoreCase(key))


                                    db.collection("patient_daily_routine_reports/"+app.getAppUser(null).getId() +
                                            "/" + t_date+"/").document(alarms.get(j).getName()+" "+time).update("done",v);
                            }
                        }
                    }}
            catch(Exception e){}
        }}
   /*     for(int i=0;i<mAdapter.getItemCount();i++)
        {
try{
             v= map.get(i);

}

catch (Exception e){}

    */
    }
}


