package com.wangsc.mylocation.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.wangsc.mylocation.R;
import com.wangsc.mylocation.SetNotificationViews;

public class _NotificationUtils {

    public static void sendNotification(int notificationId, Context context, int layoutId, SetNotificationViews setNotificationViews) {
        try {
            NotificationManager notificationManager  = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), layoutId);

            setNotificationViews.setNotificationViews(remoteViews);

            if (Build.VERSION.SDK_INT >= 26) {
                NotificationChannel channel = new NotificationChannel("channel_mylocation", "我的定位", NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(true);
//                channel.enableVibration(true);

                notificationManager.createNotificationChannel(channel);
                Notification notification = new Notification.Builder(context, "channel_mylocation").setSmallIcon(R.mipmap.ic_launcher)//通知的构建过程基本与默认相同
                        //                .setTicker("hello world")
                        //                .setWhen(System.currentTimeMillis())
                        .setAutoCancel(false)
                        //                 Notification.FLAG_ONGOING_EVENT;
                        .setContent(remoteViews)//在这里设置自定义通知的内容
                        .build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(notificationId, notification);
            } else {
                Notification notification = new NotificationCompat.Builder(context).setSmallIcon(R.mipmap.ic_launcher)//通知的构建过程基本与默认相同
                        //                .setTicker("hello world")
                        //                .setWhen(System.currentTimeMillis())
                        .setAutoCancel(false)
                        //                 Notification.FLAG_ONGOING_EVENT;
                        .setContent(remoteViews)//在这里设置自定义通知的内容
                        .build();
                notification.flags = Notification.FLAG_ONGOING_EVENT;
                notificationManager.notify(notificationId, notification);
            }
        } catch (Resources.NotFoundException e) {
            _Utils.saveException(context,e);
        }
    }

    public static void closeNotification(Context context, int notificationId){
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(notificationId);
    }
}
