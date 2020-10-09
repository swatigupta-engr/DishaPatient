package com.zuccessful.trueharmony.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.DialogeActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.services.AlarmService;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class AlarmActionReceiver extends BroadcastReceiver {

    public final static String ACTION_TAKEN = "com.zuccessful.trueharmony.ACTION_TAKEN";
    public final static String ACTION_DAILY_DONE = "com.zuccessful.trueharmony.ACTION_DAILY_DONE";
    public final static String ACTION_DAILY_MISSED = "com.zuccessful.trueharmony.ACTION_DAILY_MISSED";
    public final static String ACTION_MISSED = "com.zuccessful.trueharmony.ACTION_MISSED";
    public static final String DIALOGE_MESSAGE = "dialogeMessage";
    private AlarmManager alarmManager;
    Intent stopIntent;
    private ArrayList<DailyRoutine> alarms ;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("alarm----------"," ALARM ACTION RECEIVER");
        int alarmId = intent.getIntExtra("alarm_id", 0);
        Log.d("alarm", " Something is clicked in the notification : " + alarmId);
        alarms = Utilities.getListOfDailyRoutineAlarms();

        //turn off the alarm here
     // stopIntent = new Intent(context.getApplicationContext(), PlayRingtone.class);
      //  context.getApplicationContext().stopService(stopIntent);


        if (alarmId >= 0) {
            String action = intent.getAction();

            if (action != null) {
                SakshamApp app = SakshamApp.getInstance();
                FirebaseFirestore db = app.getFirebaseDatabaseInstance();
                SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                final   int RQS_1 = 1;

                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmManager != null) {
                    switch (action) {
                        case ACTION_TAKEN: {

                            MedicineRecord medicineRecord = null;
                            String medId = intent.getStringExtra("medId");
                            String medName = intent.getStringExtra("medName");
                            int medSlot = intent.getIntExtra("medSlot", 0);
                            int alarmID = intent.getIntExtra("alarm_id", 0);
                            String remTime = intent.getStringExtra("medRemTime");
                            Utilities.removeFromTimersList(alarmID);
                            Log.d("alarm", "Action Taken for slot: " + medSlot + " ::: " + remTime);

                            medicineRecord = new MedicineRecord(medId, medName, Calendar.getInstance().getTimeInMillis(), medSlot, true);
                            Map<String, Object> data = new HashMap<>();
                            data.put(sdf.format(new Date()), sdf.format(new Date()));

                            Log.d("alarm ", medName + " " + medSlot + " " + Calendar.getInstance().getTimeInMillis() + " " + alarmID);
                            SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                            String date = sdf2.format(new Date());
                            final String today = sdf.format(new Date());
                            Utilities.saveMedProgress(medName, date, medSlot, remTime, 1, context);
                            db.collection("patient_med_reports/" + app.getAppUser(null).getId() + "/" + today + "/").document(medName + " " + remTime).update("taken", true);
                            Log.d("alarm", "Medication Taken " + date);

                            Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                            dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.medicine_taken_message));
                            dialogeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(dialogeActivityIntent);
                            try {
                                closeNotificationBar(context);
                            } catch (Exception e) {
                                Log.d("alarm ", "Closing not bar exception : " + e);
                            }

////start from here
                           /* PendingIntent mAlarmPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                        mAlarmPendingIntent.cancel();

*//*

                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                    context, 1, intent, 0);

                            alarmManager.cancel(pendingIntent);*/


                            Intent intent1 = new Intent(context, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), RQS_1, intent1, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                            try {
                                if (AlarmService.ringtone.isPlaying()) {
                                    AlarmService.ringtone.stop();
                                }
                                AlarmService.isPlaying = false;
                            } catch (Exception e) {
                            }

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.cancel(alarmId);
                            break;
                        }
                        case ACTION_MISSED: {
                            MedicineRecord medicineRecord = null;
                            String medId = intent.getStringExtra("medId");
                            String medName = intent.getStringExtra("medName");
                            int alarmID = intent.getIntExtra("alarm_id", 0);
                            Utilities.removeFromTimersList(alarmID);
                            int medSlot = intent.getIntExtra("medSlot", 0);
                            String remTime = intent.getStringExtra("medRemTime");
                            Log.d("alarm", "Action Missed for slot: " + medSlot);
                            final String today = sdf.format(new Date());
                            medicineRecord = new MedicineRecord(medId, medName, Calendar.getInstance().getTimeInMillis(), medSlot, false);
//
                            Map<String, Object> data = new HashMap<>();
                            data.put(sdf.format(new Date()), sdf.format(new Date()));
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");

                            Log.d("alarm ", medName + " " + medSlot + " " + Calendar.getInstance().getTimeInMillis() + " " + alarmID);
                            // db.collection("patient_med_reports/"+app.getAppUser(null).getId() + "/" + today+"/").document(medName+" "+remTime).update("taken",true);
                            db.collection("patient_med_reports/" + app.getAppUser(null).getId() + "/" + today + "/").document(medName + " " + remTime).update("taken", false);


                            Log.d("alarm", "Medication Missed");
                            SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                            String date = sdf2.format(new Date());
                            Utilities.saveMedProgress(medName, date, medSlot, remTime, 0, context);

                            Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                            dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.medicine_missed_message));
                            dialogeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(dialogeActivityIntent);
                            try {
                                closeNotificationBar(context);
                            } catch (Exception e) {
                                Log.d("alarm", "Closing not bar exception : " + e);
                            }
                            cancelTimerInService(context);/*
                            PendingIntent mAlarmPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
                            mAlarmPendingIntent.cancel();*/


                            Intent intent1 = new Intent(context, AlarmReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), RQS_1, intent1, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.cancel(pendingIntent);
                            try {
                                if (AlarmService.ringtone.isPlaying()) {
                                    AlarmService.ringtone.stop();
                                }
                                AlarmService.isPlaying = false;
                            } catch (Exception e) {
                            }

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.cancel(alarmId);
                            break;
                        }
                        case ACTION_DAILY_DONE: {

                            DailyRoutineRecord dailyRoutRecord = null;
                            String dailyRoutId = intent.getStringExtra("dailyRoutId");
                            String dailyRoutName = intent.getStringExtra("dailyRoutName");

                            int alarmID = intent.getIntExtra("alarm_id", 0);
                            Utilities.removeFromTimersList(alarmID);
                            int dailyRoutSlot = intent.getIntExtra("dailyRoutSlot", 0);
                            String remTime = intent.getStringExtra("dailyRemTime");

                            Log.d("alarm", "Action Taken for slot: " + dailyRoutSlot);


                            dailyRoutRecord = new DailyRoutineRecord(dailyRoutId, dailyRoutName,
                                    Calendar.getInstance().getTimeInMillis(), dailyRoutSlot, 1);
//
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");
                            Map<String, Object> today_date = new HashMap<>();
                            today_date.put(sdf.format(new Date()), sdf.format(new Date()));
                            db.collection("patient_dr_dates/" + patient_id + "/dates").document("dates").set(today_date, SetOptions.merge());


                            Map<String, Object> task_name = new HashMap<>();
                            task_name.put(dailyRoutName, dailyRoutName);
                            SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();

                            String date = sdf2.format(new Date());


                            Log.d("alarm", "Action Taken for slot: " + "Date:" + date + "");

                            Log.d("alarm", "Action Taken for name " + "\n" + dailyRoutName);
                            Log.d("alarm", "Action Taken for remtime: " + remTime);


                            Log.d("alarm", "Daily Routine Task Done");
                        //  Utilities.saveDailyLog(dailyRoutName, date,remTime, 1, context);   //0 means NO checkbox selected

                         Utilities.saveDailyProgress(dailyRoutName, date, dailyRoutSlot,remTime,1, context);   //0 means NO checkbox selected

                            try {
                               // Utilities.saveDailyProgress(dailyRoutName,date,dailyRoutSlot,remTime,1,context);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Log.d("err", e.getMessage());
                            }

                            Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                            dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.medicine_taken_message));
                            dialogeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(dialogeActivityIntent);
                            try {
                                closeNotificationBar(context);
                            } catch (Exception e) {
                                Log.d("alarm", "Closing not bar exception : " + e);
                            }
                            try {
                                if (AlarmService.ringtone.isPlaying()) {
                                    AlarmService.ringtone.stop();
                                }
                                AlarmService.isPlaying = false;
                            } catch (Exception e) {
                            }


                            try {
                                Log.v("swati size:", alarms.size() + "");
                                if (alarms.size() > 0)
                                    for (int j = 0; j < alarms.size(); j++) {
                                        Map<String, Boolean> days = alarms.get(j).getDays();
                                        {
                                            ArrayList<String> slot_list = alarms.get(j).getReminders();
                                            int slots = slot_list.size();
                                            sdf = Utilities.getSimpleDateFormat();
                                            for (int k = 0; k < slots; k++) {
                                                final String time = slot_list.get(k);
                                                if (alarms.get(j).getName().equalsIgnoreCase(dailyRoutName))
                                                    db.collection("patient_daily_routine_reports/" + app.getAppUser(null).getId() +
                                                            "/" + date + "/").document(alarms.get(j).getName() + " " + time).update("done", 1);
                                            }
                                        }
                                    }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.cancel(alarmId);

                            break;
                        }
                        case ACTION_DAILY_MISSED: {
                            DailyRoutineRecord dailyRoutRecord = null;
                            String dailyRoutId = intent.getStringExtra("dailyRoutId");
                            String dailyRoutName = intent.getStringExtra("dailyRoutName");
                            int dailyRoutSlot = intent.getIntExtra("dailyRoutSlot", 0);
                            String remTime = intent.getStringExtra("dailyRemTime");

                            Log.d("alarm", "Action Missed for slot: " + dailyRoutSlot);
                            dailyRoutRecord = new DailyRoutineRecord(dailyRoutId, dailyRoutName,
                                    Calendar.getInstance().getTimeInMillis(), dailyRoutSlot, 0);
//
                            Log.d("alarm", "Daily Routine Task Missed");
                            Map<String, Object> data = new HashMap<>();
                            data.put(sdf.format(new Date()), sdf.format(new Date()));
                            String patient_id = context.getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error");


                            Map<String, Object> task_name = new HashMap<>();
                            task_name.put(dailyRoutName, dailyRoutName);
                            SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();

                            String date = sdf2.format(new Date());
                           // Utilities.saveDailyLog(dailyRoutName, date, remTime,0, context);   //0 means NO checkbox selected
                          //  Utilities.saveDailyProgress(dailyRoutName, date, dailyRoutSlot,remTime,0, context);   //0 means NO checkbox selected

                            try {
                                 Utilities.saveDailyProgress(dailyRoutName, date, dailyRoutSlot, remTime, 0, context);
                            }catch (Exception e){}

                            Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                            dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.medicine_missed_daily_alarm));
                            dialogeActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(dialogeActivityIntent);
                            try {
                                closeNotificationBar(context);
                            } catch (Exception e) {
                                Log.d("alarm", "Closing not bar exception : " + e);
                            }

                            try {
                                if (AlarmService.ringtone.isPlaying()) {
                                    AlarmService.ringtone.stop();
                                }
                                AlarmService.isPlaying = false;

                            } catch (Exception e) {
                            }


                            try {
                                if (alarms.size() > 0)
                                    for (int j = 0; j < alarms.size(); j++) {
                                        Map<String, Boolean> days = alarms.get(j).getDays();
                                        {
                                            ArrayList<String> slot_list = alarms.get(j).getReminders();
                                            int slots = slot_list.size();
                                            sdf = Utilities.getSimpleDateFormat();
                                            for (int k = 0; k < slots; k++) {
                                                final String time = slot_list.get(k);
                                                Log.v("data", alarms.get(j).getName() + "..." + dailyRoutName);
                                                if (alarms.get(j).getName().equalsIgnoreCase(dailyRoutName))


                                                    db.collection("patient_daily_routine_reports/" + app.getAppUser(null).getId() +
                                                            "/" + date + "/").document(alarms.get(j).getName() + " " + time).update("done", 0);
                                            }
                                        }
                                    }
                            } catch (Exception e) {
                            }


                            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
                            notificationManager.cancel(alarmId);
                            break;
                        }

                        default:

                            try {
                                if (AlarmService.ringtone.isPlaying()) {
                                    AlarmService.ringtone.stop();
                                    AlarmService.isPlaying = false;

                                }
                            }catch (Exception e){}




                    }
                } else {
                    Log.d("alarm ", "Alarm Manager Null");
                }
            }
        } else {
            Log.e("alarm ", "Object Issue");
        }


    }

    private void cancelTimerInService(Context context){

      //  Intent serviceIntent = new Intent(context, AlarmService.class);
     //   serviceIntent.addCategory("cancel_timer");
       // context.getApplicationContext().stopService(stopIntent);
      //  context.startService(serviceIntent);
    }

    private void closeNotificationBar(Context context) {
        RingtoneManager ringMan = new RingtoneManager(context.getApplicationContext());
        ringMan.stopPreviousRingtone();
        Log.d("alarm"," CLOSING THE NOTIFICATION BAR");
            Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.sendBroadcast(closeIntent);

    }
}
