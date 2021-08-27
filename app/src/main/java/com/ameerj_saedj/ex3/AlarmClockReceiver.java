/**************************************************************************************************/
package com.ameerj_saedj.ex3;
/**************************************************************************************************/
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
/**************************************************************************************************/
public class AlarmClockReceiver extends BroadcastReceiver
{
    private static final String CHANNEL_ID = "channel_main";
    private static  int NOTIFICATION_ID = 112;
/**************************************************************************************************/
    @Override
    public void onReceive(Context context, Intent intent)
    {
        String title=intent.getStringExtra("alarmTitle");
        String username=intent.getStringExtra("username");

     NotificationCompat.Builder builder= new NotificationCompat.Builder(context, CHANNEL_ID)
                //Notification Icon
                .setSmallIcon(R.drawable.ic_baseline_notifications_active_24)
                //Notification Title
                .setContentTitle(username)
                //Notification Text
                .setContentText(title)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID++,builder.build());

        String alarmType = intent.getStringExtra("alarmType");
        int alarmId = intent.getIntExtra("alarmId", -1);
        Calendar now = Calendar.getInstance();  // get time now from cmputer
        String timeNow = new SimpleDateFormat("HH:mm:ss").format(now.getTime());
    }
/**************************************************************************************************/
}
/**************************************************************************************************/
