package com.escoand.android.wceu;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends CursorAdapter {
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
	public ListAdapter(Context context) {
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
			e.printStackTrace();
		}
		title.setText(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

		/* open url */
		if (!cursor.isNull(cursor.getColumnIndex(COLUMN_URL))) {
			view.setTag(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse((String) v.getTag()));
					v.getContext().startActivity(intent);
				}
			});
		}

		/* open date */
		else {
			view.setTag(cursor.getString(cursor.getColumnIndex(COLUMN_DATE)));
			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(),
							ArticleActivity.class);
					intent.putExtra("date", (String) v.getTag());
					v.getContext().startActivity(intent);
				}
			});
		}
	}
}
