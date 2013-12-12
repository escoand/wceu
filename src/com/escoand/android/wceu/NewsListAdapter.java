package com.escoand.android.wceu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListAdapter extends CursorAdapter {
	private final String COLUMN_DATE = NewsDatabase.COLUMN_DATE;
	private final String COLUMN_DATEEND = EventsDatabase.COLUMN_DATEEND;
	private final String COLUMN_TITLE = NewsDatabase.COLUMN_TITLE;
	private final String COLUMN_URL = NewsDatabase.COLUMN_URL;
	private final String COLUMN_CATEGORY = NewsDatabase.COLUMN_CATEGORY;

	private final SimpleDateFormat dfIn = new SimpleDateFormat(
			NewsDatabase.DATE_FORMAT);
	private final DateFormat dfOutShort = DateFormat
			.getDateInstance(DateFormat.SHORT);
	private final DateFormat dfOutLong = DateFormat
			.getDateInstance(DateFormat.FULL);

	@SuppressLint("SimpleDateFormat")
	public NewsListAdapter(Context context) {
		super(context, null, true);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return ((LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.listitem, parent, false);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ImageView bar = (ImageView) view.findViewById(R.id.newsBar);
		TextView date = (TextView) view.findViewById(R.id.newsDate);
		TextView title = (TextView) view.findViewById(R.id.newsTitle);

		/* bar */
		String[] names = context.getResources().getStringArray(
				R.array.categoryValues);
		String[] colors = context.getResources().getStringArray(
				R.array.categoryColors);
		bar.setBackgroundColor(Color.parseColor(colors[0]));
		for (int i = 0; i < names.length; i++) {
			if (names[i].equals(cursor.getString(cursor
					.getColumnIndex(COLUMN_CATEGORY))))
				bar.setBackgroundColor(Color.parseColor(colors[i]));
		}

		/* date */
		try {
			view.setTag(dfIn.parse(cursor.getString(cursor
					.getColumnIndex(COLUMN_DATE))));
			if (cursor.getColumnIndex(COLUMN_DATEEND) != -1) {
				date.setText(dfOutShort.format(dfIn.parse(cursor
						.getString(cursor.getColumnIndex(COLUMN_DATE))))
						+ " - "
						+ dfOutShort.format(dfIn.parse(cursor.getString(cursor
								.getColumnIndex(COLUMN_DATEEND)))));
			} else {
				date.setText(dfOutLong.format(dfIn.parse(cursor
						.getString(cursor.getColumnIndex(COLUMN_DATE)))));
			}
		} catch (ParseException e) {
			// e.printStackTrace();
		}

		/* url */
		if (!cursor.isNull(cursor.getColumnIndex(COLUMN_URL)))
			view.setTag(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));

		/* title */
		title.setText(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
	}
}
