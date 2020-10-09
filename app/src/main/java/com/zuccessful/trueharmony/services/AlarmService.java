package com.zuccessful.trueharmony.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.DialogeActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.receivers.AlarmActionReceiver;
import com.zuccessful.trueharmony.receivers.NotificationDismissedReceiver;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.zuccessful.trueharmony.receivers.AlarmActionReceiver.DIALOGE_MESSAGE;

//import com.zuccessful.trueharmony.activities.DailyRoutineActivity;

public class  AlarmService extends JobIntentService {

    private Medication medication = null;
    private Injection injection = null;
    private DailyRoutine dailyRoutine = null;
    private int alarm_id, medSlot, dailyRoutSlot;
    private String name;
    public static Ringtone ringtone;

    private static int JOB_ID = 10101;

    public static Boolean isPlaying=false;

    public static void enqueueWork(Context context, int alarmID, Intent work) {
        enqueueWork(context, AlarmService.class, JOB_ID, work);

       final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            //Stop ringtone after 2 minutes
            @Override
            public void run() {
                if(AlarmService.isPlaying)
                { AlarmService.ringtone.stop();
                    AlarmService.isPlaying=false;
                }
            }
        }, 1*60*1000);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId)
    {
        if (intent != null)
        {

            if (intent.hasCategory("cancel_timer")) {
                cancelTimer();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onHandleWork(@NonNull Intent intent)
    {
        Log.d("saumya", "Inside the Service");

    /*    Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        AlarmService.ringtone = RingtoneManager.getRingtone( getApplicationContext(), alarmUri);*/

         //   ringtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);

        medication = (Medication) Utilities.getExtraFromIntent(intent, "medRaw");
        dailyRoutine = (DailyRoutine) Utilities.getExtraFromIntent(intent, "dailyRoutRaw");

        name = intent.getStringExtra("name");
        alarm_id = intent.getIntExtra("alarm_id", 0);
        medSlot = intent.getIntExtra("medSlot", 0);
        dailyRoutSlot = intent.getIntExtra("dailyRoutSlot", 0);

        if (medication != null) {
            Log.d("alarm", "Medication object passed normally"+ medication.getName());
            name = medication.getName();
            sendMedNotification("Consume \"" + medication.getName() + "\" at " + medication.getReminders().get(medSlot), intent);

        } else if (dailyRoutine != null) {
            Log.d("alarm", "Daily Routine task object passed normally"+ dailyRoutine.getName()+" ALARM ID IN ALARM SERVICE: "+alarm_id);
//            alarm_id = dailyRoutine.getAlarmId();
            name = dailyRoutine.getName();
            sendDailyRoutNotification(dailyRoutine.getName() + " at " + dailyRoutine.getReminders().get(dailyRoutSlot));
        } else
        {Log.e("alarm", "Nothing Being Passed Properly");}
    }

    Timer timer1 = new Timer();

    private void setTwoTimers(final DailyRoutine dailyRoutine, final String slotId, final String msg) {
        // todo also check if task action already taken, then cancel timer
        timer1.schedule(new TimerTask() {
            @Override
            public void run() {
                sendDailyRoutNotification(msg);
                timer1.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (Utilities.getTimerCount(getApplicationContext()) >=2){
                            cancelTimer();
                            updateDb(dailyRoutine,slotId);
                            return;
                        }
                        Utilities.incrementTimerCount(getApplicationContext());
                        sendDailyRoutNotification(msg);
                    }
                }, 10 * 60 * 1000);
            }
        }, 10 * 60 * 1000);
    }

    private void setTimerForDailyMedNotificaion(final Medication medication, final int notId) {
        // todo also check if task action already taken, then cancel timer
        final Timer medTimer = new Timer();
        medTimer.schedule(new ScheduleSomething(medication,notId), 1 * 60 * 1000);
    }


    private void updateDb(DailyRoutine dailyRoutine, String slotId) {

        DailyRoutineRecord dailyRoutRecord = null;
        String dailyRoutId = slotId;
        String dailyRoutName = dailyRoutine.getName();
        int dailyRoutSlot = Integer.parseInt(slotId);

        Log.d("alarm", "Action Missed for slot: " + dailyRoutSlot);
        dailyRoutRecord = new DailyRoutineRecord(dailyRoutId, dailyRoutName,
                Calendar.getInstance().getTimeInMillis(), dailyRoutSlot, -1);
        SakshamApp app = SakshamApp.getInstance();
        FirebaseFirestore db = app.getFirebaseDatabaseInstance();
        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
//        db.collection("patient_daily_rout_logs/" +
//                getApplicationContext().getSharedPreferences(PREF_PID, MODE_PRIVATE).getString(PREF_PID, "error") +
//                "/" + sdf.format(new Date())+"/"+name+"/details").document(String.valueOf(dailyRoutSlot))
//                .set(dailyRoutRecord, SetOptions.merge());
    }

    private void cancelTimer() {
        if (timer1 == null) return;
        timer1.cancel();
        Utilities.resetTimerCount(getApplicationContext());
    }

    private void sendDailyRoutNotification(String msg) {
        Log.d(" alarm ", "Preparing to send notification...: " + msg);


        Intent intent = new Intent(this, AlarmActionReceiver.class);
        intent.setAction(AlarmActionReceiver.ACTION_DAILY_DONE);
        intent.putExtra("alarm_id", alarm_id);
        intent.putExtra("dailyRoutId", dailyRoutine.getId());
        intent.putExtra("dailyRoutName", dailyRoutine.getName());

        // TODO: Update Slot
        intent.putExtra("dailyRoutSlot", dailyRoutSlot);
        intent.putExtra("dailyRemTime",dailyRoutine.getReminders().get(dailyRoutSlot));
        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent, 0);


        Intent intent2 = new Intent(this, AlarmActionReceiver.class);
        intent2.setAction(AlarmActionReceiver.ACTION_DAILY_MISSED);
        intent2.putExtra("alarm_id", alarm_id);
        intent2.putExtra("dailyRoutId", dailyRoutine.getId());
        intent2.putExtra("dailyRoutName", dailyRoutine.getName());

        intent2.putExtra("dailyRoutSlot", dailyRoutSlot);
        intent2.putExtra("dailyRemTime",dailyRoutine.getReminders().get(dailyRoutSlot));

        PendingIntent missedPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent2, 0);




        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Uri alarmSound = Settings.System.DEFAULT_RINGTONE_URI;
        SharedPreferences sp = getSharedPreferences("alarmAudio" ,Context.MODE_PRIVATE);
        //if condition - for type of alarm
        String uri = sp.getString("URI","0");
        alarmSound = Uri.parse(uri);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "alarm")
                .setSmallIcon(R.drawable.ic_med_notif)
                .setContentTitle("Daily Routine Task Reminder")
                .setContentText(msg)
                .addAction(R.drawable.ic_info, "DONE", takenPendingIntent)
                .addAction(R.drawable.ic_info, "MISSED", missedPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setDeleteIntent((createOnDismissedIntent(this,alarm_id)));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(alarm_id, mBuilder.build());
        Log.d("alarm ", "Notification sent: " + alarm_id);
    }

    private void sendMedNotification(String msg, Intent workIntent) {
        Log.d("alarm ", " Preparing to send notification...: " + msg);

      //  PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, MedicalAdherenceActivity.class), 0);
        Intent intent = new Intent(this, AlarmActionReceiver.class);
        intent.setAction(AlarmActionReceiver.ACTION_TAKEN);
        intent.putExtra("alarm_id", alarm_id);
        intent.putExtra("medName", medication.getName());
        intent.putExtra("medSlot", medSlot);
        intent.putExtra("medRemTime",medication.getReminders().get(medSlot));
        PendingIntent takenPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent, 0);


        Intent intent2 = new Intent(this, AlarmActionReceiver.class);
        intent2.setAction(AlarmActionReceiver.ACTION_MISSED);
        intent2.putExtra("alarm_id", alarm_id);
        intent2.putExtra("medName", medication.getName());
        intent2.putExtra("medSlot", medSlot);
        intent2.putExtra("medRemTime",medication.getReminders().get(medSlot));
        PendingIntent missedPendingIntent = PendingIntent.getBroadcast(this, alarm_id, intent2, 0);

        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmUri == null) {
            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Uri alarmSound = Settings.System.DEFAULT_RINGTONE_URI;
        SharedPreferences sp = getSharedPreferences("alarmAudio" ,Context.MODE_PRIVATE);

        String uri = sp.getString("URI","0");
        alarmSound = Uri.parse(uri);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this,
                "alarm")
                .setSmallIcon(R.drawable.ic_med_notif)
                .setContentTitle(getString(R.string.medicine_reminder))
                .setContentText(msg)
                .addAction(R.drawable.ic_info, "TAKEN", takenPendingIntent)
                .addAction(R.drawable.ic_info, "MISSED", missedPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                //.setSound(alarmSound, AudioManager.STREAM_ALARM)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setAutoCancel(false)
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setDeleteIntent((createOnDismissedIntent(this,alarm_id)));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        createNotificationChannel();
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(alarm_id, mBuilder.build());
        setTimerForDailyMedNotificaion(medication,alarm_id);
        Utilities.addToTimersList(String.valueOf(alarm_id));

        Log.d("alarm", "Notification sent: " + alarm_id);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Alarm";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("alarm", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null)
                notificationManager.createNotificationChannel(channel);
        }
    }

    private class ScheduleSomething extends TimerTask {

        Medication medication;
        int notId;

        public ScheduleSomething(Medication medication, int notId) {
            this.medication = medication;
            this.notId = notId;
        }

        @Override
        public void run() {
            Log.d("alarm","timer says med : " +medication.toString());

            if(Utilities.getArrayListTimers().contains(String.valueOf(notId))) {
                Context context = SakshamApp.getInstance();
                Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.medicine_missed_message));
                context.startActivity(dialogeActivityIntent);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(notId);
                Utilities.removeFromTimersList(notId);

try{
                if(!AlarmService.isPlaying)
                { AlarmService.ringtone.play();
                    AlarmService.isPlaying=true;
                }
            }catch(Exception e){}
        }}
    }
    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {
        Intent intent = new Intent(context, NotificationDismissedReceiver.class);
        intent.putExtra("com.zuccessful.trueharmony.notificationId", notificationId);

        PendingIntent pendingIntent =
                PendingIntent.getBroadcast(context.getApplicationContext(),
                        notificationId, intent, 0);
        return pendingIntent;
    }
//


}