package com.zuccessful.trueharmony.services;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

public class PlayRingtone extends Service {

    private Ringtone ringtone;
    private static int JOB_ID = 10201;
//
//
//    public static void enqueueWork(Context context, Intent work) {
//        enqueueWork(context, PlayRingtone.class, JOB_ID, work);
//    }

//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.d("jobbbbbb---","Job Execution Started");
//
//    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));

        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
        ringtone.play();

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d("jobbbb---------------"," STOPPING RINGTONE SERVICE");
        ringtone.stop();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

//    @Override
//    protected void onHandleWork(@NonNull Intent intent) {
//        Log.d("jobbbb---------------"," onHandleWork method");
//        Uri ringtoneUri = Uri.parse(intent.getExtras().getString("ringtone-uri"));
//        this.ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
//        ringtone.play();
//    }
}
