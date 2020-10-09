package com.zuccessful.trueharmony.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.MedicineRecord_s;
import com.zuccessful.trueharmony.pojo.WeeklyTask;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;


public class BeginAgainAdapter extends RecyclerView.Adapter<BeginAgainAdapter.MedsViewHolder> {
    private ArrayList<WeeklyTask> mMeds;
    Context ctxt;
    private SimpleDateFormat sdf;
    String t_date;
    private FirebaseFirestore db;
  SakshamApp app;
    ListAdapter adapters;
   // ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,String>>();
    ArrayList<String> array_items=new ArrayList<String>();
    ArrayAdapter arrayAdapter;

    public BeginAgainAdapter() {
        mMeds = new ArrayList<>();
        sdf = Utilities.getSimpleDateFormat();

        t_date=sdf.format(new Date());
    }

    public BeginAgainAdapter(ArrayList<WeeklyTask> mMeds) {
        this.mMeds = mMeds;

    }

    @NonNull
    @Override
    public MedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.begin_reminder_list_item, parent, false);
        ctxt=parent.getContext();
        array_items.clear();

        if(mMeds==null)
        {
            mMeds = new ArrayList<>();


        }
        return new MedsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MedsViewHolder holder, int position) {
        final WeeklyTask med = mMeds.get(position);
        holder.mItem = med;
        final String name = med.getName();


        holder.mNameView.setText(name);



        holder.dates.setText(med.getDate_start()+" to "+med.getDate_end());

Log.v("Tasks______", Utilities.getListofSubtasks(med)+"");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                ctxt,
                android.R.layout.simple_list_item_1,
                Utilities.getListofSubtasks(med) );


        holder.list_of_items.setAdapter(arrayAdapter);
        Log.v("count",holder.list_of_items.getAdapter().getCount()+"");
         final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
        ArrayList<WeeklyTask> tasks = Utilities.getListOfWeeklytasks();
        final HashMap<String ,String >subtasks=   med.getSubtasks();

        String current_date_cap;
        Calendar calendar = Calendar.getInstance();

        Date date = calendar.getTime();

         current_date_cap  =capitalize(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()).toLowerCase());

        // String status=Boolean.getBoolean(subtasks.get(current_date_cap).split("\\$")[0]);
        String status= (subtasks.get(current_date_cap).split("\\$")[1]);

         Log.v("__ subtasks_____",subtasks+"\n current cdate"+current_date_cap);

         try {
             Log.v("Status:::", status + "");
             if(status.equalsIgnoreCase(("true")))
             holder.status.setChecked(true);
             else holder.status.setChecked(false);
         }catch (Exception e){
             e.printStackTrace();
         }

        holder.status.setOnClickListener(new View.OnClickListener(){
                                             @Override


                                             public void onClick(View view) {

                                                 Boolean status=holder.status.isChecked();
                                                 Log.d("xxv bn","task done --"+name);


                                                 ArrayList<WeeklyTask> tasks = Utilities.getListOfWeeklytasks();
                                                 Log.v("Tasks__66666_:",tasks.toString());
                                                 for (int j = 0; j < tasks.size(); j++)
                                                 {
                                                     HashMap<String, Boolean> days =(HashMap) tasks.get(j).getDays();
                                                     final String name=tasks.get(j).getName();
                                                     final HashMap<String ,String >subtasks=   tasks.get(j).getSubtasks();
                                                      Calendar calendar = Calendar.getInstance();

                                                     Date date = calendar.getTime();

                                                      String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
                                                     String current_date_cap=capitalize(new SimpleDateFormat("EEEE", Locale.ENGLISH).format(date.getTime()).toLowerCase());

                                                      if (days.containsKey(current_date)){
                                                         Log.v("current date",current_date+"___________"+current_date_cap);
                                                          Log.v("in click",subtasks.get(current_date_cap)+"");

                                                          if(status==true){
                                                              Log.v("subtasks::",subtasks.get(current_date_cap).split("\\$")[0]+"");
                                                              String current_task=subtasks.get(current_date_cap).split("\\$")[0]+"$true";
                                                              subtasks.put(current_date_cap,current_task);
                                                              updateData(subtasks,name,days,med,true);
                                                          }
                                                          else{
                                                              Log.v("subtasks::",subtasks.get(current_date_cap).split("\\$")[0]+"");
                                                              String current_task=subtasks.get(current_date_cap).split("\\$")[0]+"$false";
                                                              subtasks.put(current_date_cap,current_task);
                                                              updateData(subtasks,name,days,med,false);
                                                          }

                                                     }
                                                 }
                                             }
                                         });


                holder.mDeleteIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("saumya","deleting --"+name);
  /*final ArrayList<Integer> alarmIds = med.getAlarmIds();

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
                    Utilities.removeMed(med);
                    removeMeds(med);
                } */
                Utilities.removeTasks(med);
                removeMeds(med);
                SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" + SakshamApp.getInstance().getAppUser(null).getId() + "/weekly_tasks").document(med.getName()).delete();
               /* for(String t: med.getReminders())
                {
                    SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("patient_med_reports/" + SakshamApp.getInstance().getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                            "/" + sdf.format(new Date())).document(med.getName()+" "+t).delete();
                }*/
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.v("count",mMeds.size()+"");
        return mMeds.size();
    }

    public void updateMeds(ArrayList<WeeklyTask> medList) {
        mMeds.addAll(medList);
        notifyDataSetChanged();
    }

    public void replaceMeds(ArrayList<WeeklyTask> medList) {
        mMeds = medList;
        notifyDataSetChanged();
    }

    public void removeMeds(WeeklyTask med) {
        mMeds.remove(med);
        notifyDataSetChanged();
    }

    public ArrayList<WeeklyTask> getMeds() {
        return mMeds;
    }


    class MedsViewHolder extends RecyclerView.ViewHolder {

        TextView mNameView;
        TextView mTimesView;
        WeeklyTask mItem;
        ImageView mDeleteIcon;
        ListView list_of_items;
        TextView dates;
        CheckBox status;

        MedsViewHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.med_item_name);
            mTimesView = itemView.findViewById(R.id.med_item_times);
            mDeleteIcon = itemView.findViewById(R.id.deleteIv);
            list_of_items=itemView.findViewById(R.id.list_subtasks);
            dates=itemView.findViewById(R.id.dates);
            status=itemView.findViewById(R.id.chk);

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
    public static String capitalize(String str)
    {
        if(str == null) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public void updateData(HashMap<String,String>sub_tasks , String name, HashMap<String, Boolean> days, WeeklyTask med, Boolean status) {
         String mon,tue,wed,thu,fri,sat,sun;
       // String name;
        // name = taskSlected;
        final ArrayList<Integer> weekdays = new ArrayList<>();


          {

            setTaskData(name,days,sub_tasks,status);
              Activity activity = (Activity) ctxt;
             // Medication mItem = (Medication) activity.getIntent().getSerializableExtra(Constants.MED_OBJ);
                  /*  SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" +
                            SakshamApp.getInstance().getAppUser(null).getId() +
                            "/weekly_tasks").document(med.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //finish();
                        }

                    });*/
                }

        }


    private void setTaskData( final String name, HashMap<String,Boolean> days,HashMap<String, String >subtasks,Boolean status ) {


        final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
        Date today=new Date();
        String  datetoday  = sdf.format(today);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE,  6);
        Date todate1 = cal.getTime();
        String post_date = sdf.format(todate1);
        final WeeklyTask taskObj = new WeeklyTask(name,  days, subtasks, datetoday,post_date,status);

       Utilities.saveWeeklyTasksToList(taskObj);

        ArrayList<WeeklyTask> tasks = Utilities.getListOfWeeklytasks();
        Log.v("TASKS",tasks+"");
        Log.d("Download_Data", " selected tasks are: " + tasks.size());
        if (tasks.size() > 0) {
            updateMedToFireBase(tasks);
        }

    }


    public void updateMedToFireBase(final ArrayList<WeeklyTask> tasks)
    {
        final ArrayList<MedicineRecord_s> medicationArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();

        if (Utilities.isInternetOn(ctxt))
        {
            Log.d("firebase", "Internet present");
            for (final WeeklyTask m : tasks)
            {


                DocumentReference documentReference = SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" + SakshamApp.getInstance().getAppUser(null).getId() + "/weekly_tasks").document(m.getName());
                documentReference.set(m).addOnFailureListener(new OnFailureListener()
                {

                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("firebase","failure in adding "+m.getName());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("firebase","sucessss in adding  weekly tasks"+m.getName());
                    }
                });

            }
            for (int j = 0; j < tasks.size(); j++)
            {
                Map<String, Boolean> days = tasks.get(j).getDays();
                final String name=tasks.get(j).getName();
                final HashMap<String ,String >subtasks=   tasks.get(j).getSubtasks();

                if (days.containsKey(current_date))
                {
                    final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                    // for (int k = 0; k < slots; k++)
                    //  {
                    //    final String time=slot_list.get(k);
                    //   final MedicineRecord_s med = new MedicineRecord_s(meds.get(j).getName(), slot_list.get(k), days);
                    DocumentReference dr= SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("tasks_weekly_reports/" + ctxt.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                            "/" + sdf.format(new Date())).document(tasks.get(j).getName() );
                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) { if(task.isSuccessful()) {
                            DocumentSnapshot ds= task.getResult();
                            Log.d("logss",ds.getId()+" "+ds.exists());
                            if (ds.exists())
                            {
                                Log.d("logss","ALREADY ENTRY PRESENT");
                            }
                            else
                            {   Log.d("logss","again setting data to firebase");
                                db.collection("tasks_weekly_reports/" + ctxt.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                        "/" + sdf.format(new Date())).document(name).set(subtasks, SetOptions.merge());
                            }
                        }
                        }
                    });
                    //  }
                } else { Log.d("logss", "NO MED ON THIS DAY"); }
            }
        }


    }

}





