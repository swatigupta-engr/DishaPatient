package com.zuccessful.trueharmony.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class ActivityAlarmAdapter extends RecyclerView.Adapter<ActivityAlarmAdapter.AlarmViewHolder>
{
    private ArrayList<DailyRoutine> mAlarms;

    public ActivityAlarmAdapter() {
        mAlarms = new ArrayList<>();
    }
    ImageView image;
    Context context;
    final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

    public ActivityAlarmAdapter(Context c,ArrayList<DailyRoutine> mAlarms) {
        this.mAlarms = mAlarms;
        context=c;
    }

    @NonNull
    @Override
    public AlarmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_reminder_list_item, parent, false);
        return new AlarmViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmViewHolder holder, int i) {
       final DailyRoutine act = mAlarms.get(i);
       Log.d("saumya","DAILYROUTINE OBJ: "+act.getName()+act.getReminders().size()+" "+act.getAlarmIds().size());
        holder.mItem = act;
        String name = act.getName();
        holder.icon.setVisibility(View.GONE);
        holder.mNameView.setText(name);
        holder.mTimesView.setText(getRemindersFormatted(act.getReminders()));
//        holder.clock.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_alarm));
        holder.mDeleteIcon.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlarmManager alarmManager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
                Intent myIntent = new Intent(v.getContext(), AlarmReceiver.class);
                ArrayList<Integer> alarmIds = act.getAlarmIds();
                Log.d("saumya","size of alarmID after pressing the delete: "+alarmIds.size()+" of "+act.getName());
                for(int id : alarmIds) {
                    Log.d("saumya","id is :"+id);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(), id, myIntent, PendingIntent.FLAG_NO_CREATE);
                    if(pendingIntent!=null)
                    {
                        Log.d("saumya","ALARM EXISTS WITH ID"+id+" of "+act.getName());
                        alarmManager.cancel(pendingIntent);
                        Log.d("saumya","ALARM CANCELLED");

                    }
                }


                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                String t_date=sdf.format(new Date());
                Utilities.removeEntryDailyLog(act.getName(),t_date, SakshamApp.getInstance().getApplicationContext());
                Utilities.removeFromListOfDailyRoutineAlarms(act);
                removeAlarm(act);

                Toast.makeText(SakshamApp.getInstance().getApplicationContext(), "ALARM REMOVED ",Toast.LENGTH_SHORT).show();

                SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" + SakshamApp.getInstance().getAppUser(null).getId() + "/daily_routine").document(act.getName()).delete();
                for(String t: act.getReminders())
                {
                    SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("patient_daily_routine_reports/" + SakshamApp.getInstance().getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                            "/" + sdf.format(new Date())).document(act.getName()+" "+t).delete();
                }
             }
        });
    }


    private String getRemindersFormatted(ArrayList<String> reminders) {
        StringBuilder a = new StringBuilder();
        for (String time : reminders) {
            a.append(time).append(", ");
        }
        return a.toString().substring(0, a.length() - 2);
    }

    @Override
    public int getItemCount() {
        return mAlarms.size();
    }

    public void updateAlarms(ArrayList<DailyRoutine>list) {
        mAlarms.addAll(list);
        notifyDataSetChanged();
    }

    public void replaceAlarms(ArrayList<DailyRoutine> list) {
        mAlarms = list;
        notifyDataSetChanged();
    }

    public void removeAlarm(DailyRoutine alarm) {
        mAlarms.remove(alarm);
        notifyDataSetChanged();
    }

    public ArrayList<DailyRoutine> getAlarms() {
        return mAlarms;
    }


    class AlarmViewHolder extends RecyclerView.ViewHolder
    {

        TextView mNameView;
        TextView mTimesView;
        DailyRoutine mItem;
        ImageView mDeleteIcon;
        ImageView clock;
        ImageView icon;
        AlarmViewHolder(View itemView)
        {
            super(itemView);
            icon=itemView.findViewById(R.id.leftIv);
            mNameView = itemView.findViewById(R.id.med_item_name);
            mTimesView = itemView.findViewById(R.id.med_item_times);
            mDeleteIcon = itemView.findViewById(R.id.deleteIv);
            clock= itemView.findViewById(R.id.leftIv);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    Intent editMedIntent = new Intent(v.getContext(), AddMedRecActivity.class);
//                    editMedIntent.putExtra(Constants.MED_OBJ,mItem);
//                    editMedIntent.putExtra(Constants.CALLED_FROM,Constants.MED_ADAPTER);
//                    v.getContext().startActivity(editMedIntent);

                }
            });
        }
    }
}
