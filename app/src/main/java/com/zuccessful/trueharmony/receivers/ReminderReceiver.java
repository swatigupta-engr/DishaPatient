package com.zuccessful.trueharmony.receivers;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.AppConstant;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.pojo.InjectionRecord;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static android.content.Context.ALARM_SERVICE;

public class ReminderReceiver extends BroadcastReceiver {
    private final String TAG = "ReminderReceiver";
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    private AlarmManager alarmManager;

    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub

        Context c=context;
        String action = intent.getAction();
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if(action != null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED) ) {
                // TO-DO: Code to handle BOOT COMPLETED EVENT
                // TO-DO: I can start an service.. display a notification... start an activity

                // Retrieve the persisted alarms
                //  List<SMSSchedule> persistedAlarms = getStoredSchedules();
                // Set again the alarms

                Log.v("reseting alarm","on receive");

                setMedicationAlarms(context);
                setDailyAlarms(context);


            }
        }
        Log.d("ReminderReceiver", "onReceive: inside Reminder");
        Bundle args = intent.getBundleExtra("DATA");
        final Injection injection = (Injection) args.getSerializable("INJECTION");
        injection.setStatus("Have to take");
        try {db.collection("alarms")
                .document(app.getAppUser(null)
                        .getId())
                .collection("injection")
                .document(injection.getName())
                .set(injection);
        } catch (Exception e) { e.printStackTrace(); }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Injection";
            String description = "Injection alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("injection", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
            Intent yesIntent = new Intent(context, ReminderActionReceiver.class);
            yesIntent.setAction(AppConstant.YES_ACTION);
            Bundle args2 = new Bundle();
            args2.putSerializable("INJECTION",injection);
            yesIntent.putExtra("DATA",args2);

            Intent noIntent = new Intent(context, ReminderActionReceiver.class);
            yesIntent.setAction(AppConstant.NO_ACTION);
            PendingIntent yesPendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(injection.getReqCode()),yesIntent,PendingIntent.FLAG_UPDATE_CURRENT);
            //PendingIntent noPendingIntent = PendingIntent.getBroadcast(context,Integer.parseInt(injection.getReqCode()),noIntent,PendingIntent.FLAG_UPDATE_CURRENT);


            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "injection")
                    .setSmallIcon(R.drawable.common_google_signin_btn_icon_dark)
                    .setContentTitle(injection.getName())
                    .setContentText("Did you take the injection")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.drawable.common_google_signin_btn_icon_dark,"YES",yesPendingIntent)
                    //.addAction(R.drawable.common_google_signin_btn_icon_light,"NO",noPendingIntent)
                    .setOngoing(true);

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(Integer.parseInt(injection.getReqCode()),builder.build());

            final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

            //Start service which will wait for 3 days for you to take the injection and then dismiss
            // the notification if you don't take the injection
            Handler h = new Handler();
            long delay = 86400*3*1000;
            long delay2 = 5000;
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //missed the notification
                    StatusBarNotification[] notifications = new StatusBarNotification[0];
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notifications = notificationManager.getActiveNotifications();
                    }
                    for (StatusBarNotification notification : notifications) {
                        if (notification.getId() == Integer.parseInt(injection.getReqCode())) {
                            // Do something.
                            notificationManager.cancel(Integer.parseInt(injection.getReqCode()));
                            injection.setStatus("Missed");
                            Toast.makeText(context, injection.getName()+" Missed!", Toast.LENGTH_LONG).show();
                            String timeStamp = String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
                            InjectionRecord injectionRecord = new InjectionRecord(injection.getName(),timeStamp,"Missed",injection.getRepeated());

                            //update date
                            SimpleDateFormat sdf2 = Utilities.getSimpleDateFormat();
                            Map<String, Object> today_date = new HashMap<>();
                            today_date.put(sdf2.format(new Date()), sdf2.format(new Date()));
                            db.collection("patient_injr_dates/"+app.getPatientID()+"/dates").document("dates").set(today_date, SetOptions.merge());

                            //update record in firebase
                            SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                            try {db.collection("patient_inj_logs/")
                                    .document(app.getAppUser(null)
                                            .getId())
                                    .collection(sdf.format(new Date()))
                                    .document(injection.getName())
                                    .set(injectionRecord);
                            } catch (Exception e) { e.printStackTrace(); }


                            try {db.collection("alarms")
                                    .document(app.getAppUser(null)
                                            .getId())
                                    .collection("injection")
                                    .document(injection.getName())
                                    .set(injection);
                            } catch (Exception e) { e.printStackTrace(); }
                        }
                    }
                }
            },delay);

        }

    private void setDailyAlarms(Context c){
        ArrayList<DailyRoutine> alarms =  Utilities.getListOfDailyRoutineAlarms();
         Log.v("reseting alarm",alarms.size()+"");
        if (alarms.size() > 0) {


            for(int k=0;k<alarms.size();k++){

                  ArrayList<Integer> alarm_ids = new ArrayList<>();
                final ArrayList<Integer> weekdays = new ArrayList<>();
                Map<String, Boolean> days = new HashMap<>();

                weekdays.add(Calendar.SUNDAY);
                days.put("sun", true);

                weekdays.add(Calendar.MONDAY);
                days.put("mon", true);

                weekdays.add(Calendar.TUESDAY);
                days.put("tue", true);

                weekdays.add(Calendar.WEDNESDAY);
                days.put("wed", true);

                weekdays.add(Calendar.THURSDAY);
                days.put("thu", true);

                weekdays.add(Calendar.FRIDAY);
                days.put("fri", true);

                weekdays.add(Calendar.SATURDAY);
                days.put("sat", true);


                alarm_ids= alarms.get(k).getAlarmIds();
                DailyRoutine dailyRoutineObj=new DailyRoutine();
                dailyRoutineObj= Utilities.getListOfDailyRoutineAlarms().get(k);
                ArrayList<String> reminders=new ArrayList<>();
                reminders=alarms.get(k).getReminders();
                if (weekdays.size() * alarms.get(k).getReminders().size() == alarm_ids.size())
                {      // Check
                    for (int slot = 0; slot < dailyRoutineObj.getReminders().size(); slot++)
                    {
                        Log.v("reseting alarm",dailyRoutineObj.getReminders().get(slot)+"length....."+dailyRoutineObj.getReminders().size()+"");

                        for (int i = 0; i < weekdays.size(); i++) {
                            Intent mIntent = new Intent(c, AlarmReceiver.class);
                            Utilities.setExtraForIntent(mIntent, "dailyRoutRaw", dailyRoutineObj);
                            Log.d("saumya"," setting in intent id: ----"+ alarm_ids.get(slot* weekdays.size() + i));
                            mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                            mIntent.putExtra("dailyRoutSlot", slot);
                            int h = Integer.parseInt(reminders.get(slot).split(":")[0]);
                            int m = Integer.parseInt(reminders.get(slot).split(":")[1]);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, h);
                            calendar.set(Calendar.MINUTE, m);
                            calendar.set(Calendar.SECOND, 0);
                            scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i),c);
                            Log.d("saumya", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                        }
                    }
                }
            }
        }

    }

    private void setMedicationAlarms(Context c){
        ArrayList<Medication> alarms =  Utilities.getListOfMedication();
//        Log.v("reseting alarm 2",alarms.size()+"");
        if (alarms.size() > 0) {


            for(int k=0;k<alarms.size();k++){

                final ArrayList<Integer> alarm_ids = new ArrayList<>();
                final ArrayList<Integer> weekdays = new ArrayList<>();
                Map<String, Boolean> days = new HashMap<>();

                weekdays.add(Calendar.SUNDAY);
                days.put("sun", true);

                weekdays.add(Calendar.MONDAY);
                days.put("mon", true);

                weekdays.add(Calendar.TUESDAY);
                days.put("tue", true);

                weekdays.add(Calendar.WEDNESDAY);
                days.put("wed", true);

                weekdays.add(Calendar.THURSDAY);
                days.put("thu", true);

                weekdays.add(Calendar.FRIDAY);
                days.put("fri", true);

                weekdays.add(Calendar.SATURDAY);
                days.put("sat", true);

                ArrayList<String> reminders;

                reminders=alarms.get(k).getReminders();

                Log.v("reseting alarm med size",reminders.size()+"------------"+reminders.toString());


                for(int j=0;j<7;j++)   // as alarm set for all 7 days
                {
                    for (int i = 0; i < reminders.size(); i++)
                    {
                        alarm_ids.add(Utilities.getNextAlarmId(c));
                    }
                }

                Log.v("reseting alarm med",alarm_ids.toString()+"-------"+alarm_ids.size());

                Medication medicationObj;
                medicationObj= Utilities.getListOfMedication().get(k);


                if (weekdays.size() * alarms.get(k).getReminders().size() == alarm_ids.size())
                {      // Check

                    Log.v("reseting alarm","yessss");

                    for (int slot = 0; slot <medicationObj.getReminders().size(); slot++)
                    {

                        Log.v("reseting alarm",medicationObj.getReminders().get(slot));

                        for (int i = 0; i < weekdays.size(); i++)
                        {

                            Intent mIntent = new Intent(c, AlarmReceiver.class);
                            Utilities.setExtraForIntent(mIntent, "medRaw", medicationObj);
                            mIntent.putExtra("alarm_id", alarm_ids.get(slot * weekdays.size() + i));
                            mIntent.putExtra("medSlot", slot);
                            DateFormat df = new SimpleDateFormat("hh:mm aa");
                            //Date/time pattern of desired output date
                            DateFormat outputformat = new SimpleDateFormat("HH:mm");
                            Date date = null;
                            String output = null;
                            try {
                                //Conversion of input String to date
                                date = df.parse(reminders.get(slot));
                                //old date format to new date format
                                output = outputformat.format(date);
                            } catch (ParseException pe) {
                                pe.printStackTrace();
                            }
                            int h = Integer.parseInt(output.split(":")[0]);
                            int m = Integer.parseInt(output.split(":")[1]);
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(Calendar.HOUR_OF_DAY, h);
                            calendar.set(Calendar.MINUTE, m);
                            calendar.set(Calendar.SECOND, 0);
                            scheduleAlarm(alarmManager, calendar, mIntent, alarm_ids.get(slot * weekdays.size() + i), weekdays.get(i),c);
                            Log.d("saumya", "Setting alarm, Alarm Id: " + alarm_ids.get(slot * weekdays.size() + i) + " Slot: " + slot + " : weekday: " + weekdays.get(i));
                        }
                    }


                }
            }
        }

    }

    private void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, Intent intent, int request_code, Integer day, Context context) {
        Log.d("saumya", " In ScheduleAlarm  ");
        calendar.set(Calendar.DAY_OF_WEEK, day);
        // Check we aren't setting it in the past which would trigger it to fire instantly
        if (calendar.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()) {
            Log.d("saumya--"," correct time...");
            calendar.add(Calendar.DAY_OF_YEAR, 7);
        }

        // Set this to whatever you were planning to do at the given time
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, request_code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);

    }
    }

