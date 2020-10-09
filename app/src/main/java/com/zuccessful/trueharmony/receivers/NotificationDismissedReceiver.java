package com.zuccessful.trueharmony.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zuccessful.trueharmony.services.AlarmService;

public class NotificationDismissedReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
      int notificationId = intent.getExtras().getInt("com.zuccessful.trueharmony.notificationId");
      /* Your code to handle the event here */

     // Toast.makeText(this,"Cancelling",Toast.LENGTH_LONG).show();
      Log.v("TAGging","cancelling");

      try{


          if(AlarmService.ringtone.isPlaying()){
              AlarmService.ringtone.stop();
          }
      }catch (Exception e){}

  }
}