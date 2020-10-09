package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DailyRoutineAdapterLocal extends RecyclerView.Adapter<DailyRoutineAdapterLocal.ViewHolder>
{
    private List<String> activitylist;
    private LayoutInflater mInflater;
    private Context mContext;
    private HashMap<String,ArrayList<Integer>> map;
    private SimpleDateFormat sdf;
    String t_date;
    boolean flag;
    private FirebaseFirestore db;
    private SakshamApp app;
    private ArrayList<DailyRoutine> alarms ;


    public DailyRoutineAdapterLocal(Context context, List<String> data) {
        Log.d("saumya","IN ADAPTER"+" SIZE: "+data.size());
        this.mInflater = LayoutInflater.from(context);
        this.activitylist = data;
        mContext = context;
        sdf = Utilities.getSimpleDateFormat();
        t_date=sdf.format(new Date());

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        try{
        if(Utilities.getDailyLog()!=null && Utilities.getDailyLog().containsKey(t_date)==true)
        {
            Log.d("saumya","Entry found for today");

            map= Utilities.getDailyLog().get(t_date);
        }}
        catch (Exception e){}
        alarms = Utilities.getListOfDailyRoutineAlarms();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        Log.d("saumya","IN ADAPTER"+" SIZE: "+activitylist.size());
        View view = mInflater.inflate(R.layout.activity_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {
        final String name = activitylist.get(i);
        final String time;
       // Utilities.saveDailyLog(name, t_date, 1, mContext);   //0 means NO checkbox selected

        viewHolder.activityName.setText(name);
        flag=false;

        if(map!=null && map.containsKey(name))
        {
            Log.d("saumya","Entry for today exists");
            ArrayList <Integer>v= map.get(name);
             int chked=0;
            for(int k=0;k<v.size();k++){
            if(v.get(k)==1){

                chked=1;
            }

            if(chked==1){
                viewHolder.toggle.setChecked(true);

            }
            else                 viewHolder.toggle.setChecked(false);

            }


        }

    /*    viewHolder.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked())
                {
                    Log.d("saumya",name+ " checkbox-yes tick");
                    Utilities.saveDailyLog(name,t_date,1,mContext);   // 1 means YES checkbox selected
                }
                else
                {
                    Log.d("saumya",name+ " checkbox-yes Un-tick");
                    Utilities.removeEntryDailyLog(name,t_date,mContext);     // delete the entry from savedpreference for this activity
                }

            }
        });
*/
         viewHolder.toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true)
                {Log.d("saumya",name+" checkbox-no tick");

                      Toast.makeText(mContext,"Saved your activity "+viewHolder.activityName.getText()+" as done",Toast.LENGTH_LONG).show();
                    try {
                        if (alarms.size() > 0)
                            for (int j = 0; j < alarms.size(); j++) {
                                Map<String, Boolean> days = alarms.get(j).getDays();
                                {
                                    ArrayList<String> slot_list = alarms.get(j).getReminders();
                                    int slots = slot_list.size();
                                    final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                                    for (int k = 0; k < slots; k++) {

                                        final String time = slot_list.get(k);
                                       // Utilities.saveDailyLog(alarms.get(j).getName(), t_date, time,1, mContext);   //0 means NO checkbox selected
                                  //        Utilities.saveDailyProgress(alarms.get(j).getName(), t_date,0, time,1, mContext);   //0 means NO checkbox selected

                                        Log.v("data", alarms.get(j).getName() + "..." + name);
                                        if (alarms.get(j).getName().equalsIgnoreCase(name))
                                            db.collection("patient_daily_routine_reports/" + app.getAppUser(null).getId() +
                                                    "/" + t_date + "/").document(alarms.get(j).getName() + " " + time).update("done", 1);
                                    }
                                }
                            }

                    }catch (Exception e){}

                }
                else if(b==false){
                 //   Utilities.saveDailyLog(name, t_date, time,1, mContext);   //0 means NO checkbox selected

                  //  Utilities.saveDailyLog(name,t_date,0,mContext);   // 1 means YES checkbox selected
                    Toast.makeText(mContext,"Saved your activity "+viewHolder.activityName.getText()+" as not done",Toast.LENGTH_LONG).show();


           try{
                    if(alarms.size()>0)
                    for (int j = 0; j < alarms.size(); j++)
                    {
                        Map<String, Boolean> days = alarms.get(j).getDays();
                         {
                            ArrayList<String> slot_list = alarms.get(j).getReminders();
                            int slots = slot_list.size();
                            final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                            for (int k = 0; k < slots; k++)
                            { final String time=slot_list.get(k);
                             //  Utilities.saveDailyLog(alarms.get(j).getName(), t_date, time,0, mContext);   //0 means NO checkbox selected
                            //    Utilities.saveDailyProgress(alarms.get(j).getName(), t_date,0, time,0, mContext);   //0 means NO checkbox selected

                                Log.v("data", alarms.get(j).getName()+"..."+name);
                                if(alarms.get(j).getName().equalsIgnoreCase(name))


                                db.collection("patient_daily_routine_reports/"+app.getAppUser(null).getId() +
                                        "/" + t_date+"/").document(alarms.get(j).getName()+" "+time).update("done",0);
                            }
                        }
                     }}
                    catch(Exception e){}

                }

            }
        });
        /*viewHolder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked())
                {
                Log.d("saumya",name+" checkbox-no tick");
                Utilities.saveDailyLog(name,t_date,0,mContext);   //0 means NO checkbox selected
                }
                else
                {
                    Log.d("saumya",name+ " checkbox-no Un-tick");
                    Utilities.removeEntryDailyLog(name,t_date,mContext);  //  delete the entry from savedpreference for this activity
                }
            }
        });*/
        if (name.equalsIgnoreCase("walk"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_walk)); }
        else if (name.equalsIgnoreCase("run"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_walk)); }
        else if (name.equalsIgnoreCase("play"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.leisureactivities)); }
        else if (name.equalsIgnoreCase("swimming"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_smiley)); }
        else if (name.equalsIgnoreCase("cycling"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_walk)); }
        else if (name.equalsIgnoreCase("exercise"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_physical_health)); }
        else if (name.equalsIgnoreCase("wake-up"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wakingup)); }
        else if (name.equalsIgnoreCase("brush"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.brushingteeth)); }
        else if (name.equalsIgnoreCase("bath"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bath)); }
        else if (name.equalsIgnoreCase("sleep"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sleeping)); }
        else if (name.equalsIgnoreCase("breakfast"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eating)); }
        else if (name.equalsIgnoreCase("lunch"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eating)); }
        else if (name.equalsIgnoreCase("dinner"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eating)); }
        else
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_avatar)); }

    }

    @Override
    public int getItemCount() {
        return  activitylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView activityName;
        ImageView imageView;
        //  CheckBox yes,no;
        Switch toggle;
        String t_date;
        String name;
        LinearLayout act_item;
        ViewHolder(View itemView) {
            super(itemView);
            act_item=itemView.findViewById(R.id.act_item);
            activityName = itemView.findViewById(R.id.activity_name);
            imageView = itemView.findViewById(R.id.activityImage);
            // yes= itemView.findViewById(R.id.checkbox_yes);
            //  no= itemView.findViewById(R.id.checkbox_no);
            toggle=itemView.findViewById(R.id.dailyRoutineSwitch);

        }

        @Override
        public void onClick(View view) {

        }
    }


}
