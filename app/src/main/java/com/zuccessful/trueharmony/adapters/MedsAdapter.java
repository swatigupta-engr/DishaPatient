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
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;


public class MedsAdapter extends RecyclerView.Adapter<MedsAdapter.MedsViewHolder> {
    private ArrayList<Medication> mMeds;
    Context ctxt;

    public MedsAdapter() {
        mMeds = new ArrayList<>();
    }

    public MedsAdapter(ArrayList<Medication> mMeds) {
        this.mMeds = mMeds;
    }

    @NonNull
    @Override
    public MedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ctxt=parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.med_reminder_list_item, parent, false);
        if(mMeds==null)
        {
            mMeds = new ArrayList<>();
        }
        return new MedsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedsViewHolder holder, int position) {


        final Medication med = mMeds.get(position);
        holder.mItem = med;
        final String name = med.getName();
        holder.mNameView.setText(name);
        holder.mTimesView.setText(getRemindersFormatted(med.getReminders()));
        final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();

        holder.mDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("saumya","deleting --"+name);
                AlarmManager alarmManager = (AlarmManager) v.getContext().getSystemService(Context.ALARM_SERVICE);
                Intent myIntent = new Intent(v.getContext(), AlarmReceiver.class);

                final ArrayList<Integer> alarmIds = med.getAlarmIds();

                for(int id : alarmIds) {
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(v.getContext(), id, myIntent, PendingIntent.FLAG_NO_CREATE);
                    if(pendingIntent!=null)
                    {
                        Log.d("saumya","ALARM EXISTS WITH ID"+id+" of "+med.getName());
                        alarmManager.cancel(pendingIntent);
                        Log.d("saumya","ALARM CANCELLED");
                        Toast.makeText(SakshamApp.getInstance().getApplicationContext(), "ALARM REMOVED ",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Log.d("saumya","No alarm with this ID found");
                    }
                    SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();


                    String date = sdf2.format(new Date());
                    Utilities.removeMed(med);
                    removeMeds(med);
                    try{
                    Utilities.removemedprogressLog(med.getName(),date,ctxt);}
                    catch (Exception e){}

                }
                SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" + SakshamApp.getInstance().getAppUser(null).getId() + "/medication").document(med.getName()).delete();
                for(String t: med.getReminders())
                {
                    SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("patient_med_reports/" + SakshamApp.getInstance().getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                            "/" + sdf.format(new Date())).document(med.getName()+" "+t).delete();
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
        return mMeds.size();
    }

    public void updateMeds(ArrayList<Medication> medList) {
        mMeds.addAll(medList);
        notifyDataSetChanged();
    }

    public void replaceMeds(ArrayList<Medication> medList) {
        mMeds = medList;
        notifyDataSetChanged();
    }

    public void removeMeds(Medication med) {
        mMeds.remove(med);
        notifyDataSetChanged();
    }

    public ArrayList<Medication> getMeds() {
        return mMeds;
    }


    class MedsViewHolder extends RecyclerView.ViewHolder {

        TextView mNameView;
        TextView mTimesView;
        Medication mItem;
        ImageView mDeleteIcon;

        MedsViewHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.med_item_name);
            mTimesView = itemView.findViewById(R.id.med_item_times);
            mDeleteIcon = itemView.findViewById(R.id.deleteIv);

//            mDeleteIcon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//
//                    SakshamApp.getInstance().getFirebaseDatabaseInstance().
//                            collection("alarms/" + SakshamApp.getInstance().getAppUser(null).getId() +
//                            "/medication").document(mItem.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Toast.makeText(SakshamApp.getInstance().getApplicationContext(), "Deleted " +mItem.getName()+" successfully.",Toast.LENGTH_SHORT).show();
//                            removeMeds(mItem);
//                        }
//
//                    });
//                }
//            });

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(SakshamApp.getInstance().getApplicationContext(), "layout listener activated ",Toast.LENGTH_SHORT).show();
//                    Intent editMedIntent = new Intent(v.getContext(), AddMedRecActivity.class);
//                    editMedIntent.putExtra(Constants.MED_OBJ,mItem);
//                    editMedIntent.putExtra(Constants.CALLED_FROM,Constants.MED_ADAPTER);
//                    v.getContext().startActivity(editMedIntent);
//
//                }
//            });
        }
    }
}
