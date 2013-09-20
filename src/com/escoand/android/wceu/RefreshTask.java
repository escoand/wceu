package com.escoand.android.wceu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class RefreshTask extends AsyncTask<Void, Void, Exception> {
	public MainActivity activity;
	public NewsDatabase dbNews;
	public EventsDatabase dbEvents;

	private final SimpleDateFormat dfInDate = new SimpleDateFormat("yyyyHHdd");
	private final SimpleDateFormat dfInDateTime = new SimpleDateFormat(
			"yyyyMMdd'T'HHmmss'Z'");
	private final SimpleDateFormat dfOut = new SimpleDateFormat(
			EventsDatabase.DATE_FORMAT);

	@Override
	protected void onPreExecute() {
		activity.findViewById(R.id.listProgress).setVisibility(View.VISIBLE);
		super.onPreExecute();
	}

	@Override
	protected Exception doInBackground(Void... arg0) {
		readNews();
		readEvents();
		return null;
	}

	private Exception readNews() {
		SAXParserFactory factory;
		SAXParser parser;
		XMLReader reader;
		RSSHandler handler;
		InputSource input;
		URL url;

		dbNews.clear();

		try {
			url = new URL(activity.getString(R.string.urlNews));

			factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			reader = parser.getXMLReader();
			handler = new RSSHandler(dbNews);
			input = new InputSource(url.openStream());

			reader.setContentHandler(handler);
			reader.parse(input);

		} catch (MalformedURLException e) {
			e.printStackTrace();
			return e;
		} catch (IOException e) {
			e.printStackTrace();
			return e;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return e;
		} catch (SAXException e) {
			e.printStackTrace();
			return e;
		}

		return null;
	}

	private Exception readEvents() {
		BufferedReader reader;
		String line;
		ContentValues values = new ContentValues();

		dbEvents.clear();

		try {
			String[] urls = activity.getResources().getStringArray(
					R.array.urlEvents);
			for (int i = 0; i < urls.length; i++) {
				reader = new BufferedReader(new InputStreamReader(new URL(
						urls[i]).openStream()));

				while ((line = reader.readLine()) != null) {

					try {

						/* new item */
						if (line.equals("BEGIN:VEVENT")) {
							values.clear();
							values.put(
									EventsDatabase.COLUMN_CATEGORY,
									activity.getResources().getStringArray(
											R.array.categorieValues)[i]);
						}

						/* read data */
						else if (line.startsWith("DTSTART")) {
							if (line.split(":")[1].indexOf('T') > 0)
								values.put(EventsDatabase.COLUMN_DATE, dfOut
										.format(dfInDateTime.parse(line
												.split(":")[1])));
							else
								values.put(EventsDatabase.COLUMN_DATE,
										dfOut.format(dfInDate.parse(line
												.split(":")[1])));
						} else if (line.startsWith("DTEND")) {
							if (line.split(":")[1].indexOf('T') > 0)
								values.put(EventsDatabase.COLUMN_DATEEND, dfOut
										.format(dfInDateTime.parse(line
												.split(":")[1])));
							else
								values.put(EventsDatabase.COLUMN_DATEEND,
										dfOut.format(dfInDate.parse(line
												.split(":")[1])));
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
			}
		} catch (IOException e) {
			e.printStackTrace();
			return e;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Exception e) {

		/* error */
		if (e instanceof MalformedURLException || e instanceof IOException
				|| e instanceof FileNotFoundException)
			Toast.makeText(activity.getBaseContext(),
					R.string.messageIOException, Toast.LENGTH_LONG).show();
		else if (e instanceof ParserConfigurationException
				|| e instanceof SAXException)
			Toast.makeText(activity.getBaseContext(),
					R.string.messageRSSException, Toast.LENGTH_LONG).show();
		else if (e != null)
			Toast.makeText(
					activity.getBaseContext(),
					activity.getString(R.string.messageUnknownException)
							+ e.getLocalizedMessage(), Toast.LENGTH_LONG)
					.show();

		/* show list */
		activity.refreshDisplay();
		activity.findViewById(R.id.listProgress).setVisibility(View.GONE);

		super.onPostExecute(e);
	}
}
