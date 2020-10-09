package com.zuccessful.trueharmony.receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.ALARM_SERVICE;

public class MyCustomBroadcastReceiver extends BroadcastReceiver {
    private AlarmManager alarmManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        Context c=context;
        Log.v("saurabh","on receive");
        String action = intent.getAction();
         alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if(action != null) {
            if (action.equals(Intent.ACTION_BOOT_COMPLETED) ) {
                // TO-DO: Code to handle BOOT COMPLETED EVENT
                // TO-DO: I can start an service.. display a notification... start an activity

                // Retrieve the persisted alarms
              //  List<SMSSchedule> persistedAlarms = getStoredSchedules();
                // Set again the alarms


                setDailyAlarms(context);


            }
        }


    }

    private void setDailyAlarms(Context c){
        ArrayList<DailyRoutine> alarms =  Utilities.getListOfDailyRoutineAlarms();

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



                DailyRoutine dailyRoutineObj=new DailyRoutine();
                ArrayList<String> reminders=new ArrayList<>();
                reminders=alarms.get(k).getReminders();
                if (weekdays.size() * alarms.get(k).getReminders().size() == alarm_ids.size())
                {      // Check
                    for (int slot = 0; slot < dailyRoutineObj.getReminders().size(); slot++)
                    {
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
    private void scheduleAlarm(AlarmManager alarmManager, Calendar calendar, Intent intent, int request_code, Integer day,Context context) {
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