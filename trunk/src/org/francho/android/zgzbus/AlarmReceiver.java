/**
 *  ZgzBus - Consulta cuando llega el autobus urbano en Zaragoza
 *  Copyright (C) 2010 Francho Joven
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.francho.android.zgzbus;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

/**
 * Receiver que escucha los avisos y muestra el mensaje en la barra de estatus
 * 
 * @author francho
 * 
 */
public class AlarmReceiver extends BroadcastReceiver {
	private static final int ALARM_ID = 1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();

		// Get a reference to the notification manager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);

		// Instantiate the Notification
		int icon = R.drawable.icon_notification;
		CharSequence tickerText = extras.getCharSequence("alarmTxt");
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		
		// Define the Notification's expanded message and Intent:
		CharSequence contentTitle = context.getString(R.string.notification_title);
		CharSequence contentText = "" + extras.getString("alarmTxt");
		
		// play a sound
		Uri alertSound = RingtoneManager.getDefaultUri( Notification.DEFAULT_SOUND );
		if(alertSound!=null) {
			notification.sound = alertSound;
		}
		
		// vibrate
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.vibrate = new long[] {0,100,200,300};
		
		// leds
		notification.ledARGB = Color.YELLOW;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;	

		// the asociated item
		Intent notificationIntent = new Intent(context, PosteActivity.class);
		notificationIntent.putExtra("poste", extras.getInt("poste"));
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		
		// Rock&Roll
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		mNotificationManager.notify(ALARM_ID, notification);
	}
}