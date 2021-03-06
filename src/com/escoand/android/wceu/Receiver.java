package com.escoand.android.wceu;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final NetworkInfo ni = ((ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE))
				.getActiveNetworkInfo();
		final SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		final Long now = new Date().getTime() / 1000;

		/* check if connection */
		if (ni == null || !ni.isConnectedOrConnecting())
			return;

		/* check if refresh needed */
		if (now - prefs.getLong("lastrefresh", 0) < 24 * 60 * 60)
			return;

		/* refresh */
		new AsyncTask<Object, Void, Boolean>() {
			Context context;
			NewsDatabase dbNews;
			EventsDatabase dbEvents;
			Long prevRefresh;

			@Override
			protected Boolean doInBackground(Object... params) {
				context = (Context) params[0];
				dbNews = new NewsDatabase(context);
				dbEvents = new EventsDatabase(context);
				prevRefresh = ((SharedPreferences) params[1]).getLong(
						"lastrefresh", 0);
				return RefreshHandler.refreshAll(context, dbNews, dbEvents);
			}

			@Override
			protected void onPostExecute(Boolean result) {

				/* check if new data */
				int cnt = dbNews.getList(
						NewsDatabase.COLUMN_DATE + ">" + prevRefresh,
						new String[] {}).getCount();
				cnt += dbEvents.getList(
						EventsDatabase.COLUMN_DATE + ">" + prevRefresh,
						new String[] {}).getCount();
				if (cnt < 1)
					return;

				/* create pending intent */
				// TODO re-start activity to get updated list
				PendingIntent pIntent = PendingIntent.getActivity(
						context,
						0,
						new Intent(context, MainActivity.class).setFlags(
								Intent.FLAG_ACTIVITY_NEW_TASK).setFlags(
								Intent.FLAG_ACTIVITY_CLEAR_TASK),
						PendingIntent.FLAG_UPDATE_CURRENT);

				/* create notification */
				// TODO use .build() instead of .getNotification() with api16+
				@SuppressWarnings("deprecation")
				Notification notification = new Notification.Builder(context)
						.setSmallIcon(R.drawable.ic_launcher)
						.setTicker(context.getString(R.string.txtRefreshed))
						.setContentTitle(context.getString(R.string.app_name))
						.setContentText(
								context.getString(R.string.txtRefreshed))
						.setContentIntent(pIntent).setAutoCancel(true)
						.getNotification();

				/* show notification */
				((NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE))
						.notify(0, notification);

				super.onPostExecute(result);
			}
		}.execute(context, prefs);
	}
}
