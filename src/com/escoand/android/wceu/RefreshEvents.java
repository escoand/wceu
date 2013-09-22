package com.escoand.android.wceu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;

@SuppressLint("SimpleDateFormat")
public class RefreshEvents implements Runnable {
	private EventsDatabase dbEvents;
	private final String url;
	private final String category;

	private final SimpleDateFormat dfInDate = new SimpleDateFormat("yyyyHHdd");
	private final SimpleDateFormat dfInDateTime = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss'Z'");
	private final SimpleDateFormat dfOut = new SimpleDateFormat(
			EventsDatabase.DATE_FORMAT);

	RefreshEvents(final Context context, final String url, final String category) {
		dbEvents = new EventsDatabase(context);
		this.url = url;
		this.category = category;
	}

	@Override
	public void run() {
		BufferedReader reader;
		String line;
		ContentValues values = new ContentValues();

		try {
			reader = new BufferedReader(new InputStreamReader(
					new URL(url).openStream()));

			while ((line = reader.readLine()) != null) {

				try {

					/* new item */
					if (line.equals("BEGIN:VEVENT")) {
						values.clear();
						values.put(EventsDatabase.COLUMN_CATEGORY, category);
					}

					/* read data */
					else if (line.startsWith("DTSTART")) {
						if (line.split(":")[1].indexOf('T') > 0)
							values.put(EventsDatabase.COLUMN_DATE,
									dfOut.format(dfInDateTime.parse(line
											.split(":")[1])));
						else
							values.put(EventsDatabase.COLUMN_DATE, dfOut
									.format(dfInDate.parse(line.split(":")[1])));
					} else if (line.startsWith("DTEND")) {
						if (line.split(":")[1].indexOf('T') > 0)
							values.put(EventsDatabase.COLUMN_DATEEND,
									dfOut.format(dfInDateTime.parse(line
											.split(":")[1])));
						else
							values.put(EventsDatabase.COLUMN_DATEEND, dfOut
									.format(dfInDate.parse(line.split(":")[1])));
					} else if (line.startsWith("SUMMARY"))
						values.put(EventsDatabase.COLUMN_TITLE,
								line.split(":")[1]);
					else if (line.startsWith("DESCRIPTION"))
						values.put(EventsDatabase.COLUMN_TEXT,
								line.split(":")[1]);
					else if (line.startsWith("LOCATION"))
						values.put(
								EventsDatabase.COLUMN_URL,
								"https://maps.google.com/maps?q="
										+ line.split(":")[1]);

					/* save item */
					else if (line.equals("END:VEVENT"))
						dbEvents.insertItem(values);

					/* error */
				} catch (IndexOutOfBoundsException e) {
					// e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
