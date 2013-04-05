package com.escoand.android.wceu;

import java.text.SimpleDateFormat;
import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.annotation.SuppressLint;
import android.content.ContentValues;

@SuppressLint("SimpleDateFormat")
public class RSSHandler extends DefaultHandler {
	private NewsDatabase db;
	private SimpleDateFormat dfIn = new SimpleDateFormat(
			"EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
	private SimpleDateFormat dfOut = new SimpleDateFormat(
			NewsDatabase.DATE_FORMAT);

	private StringBuilder buf;
	private ContentValues values = new ContentValues();

	public RSSHandler(NewsDatabase db) {
		this.db = db;
		db.clear();
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (localName.equalsIgnoreCase("item"))
			values.clear();

		if (localName != null && localName.length() > 0)
			buf = new StringBuilder();

		super.startElement(uri, localName, qName, attributes);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		buf.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (localName != null && localName.length() > 0) {
			if (localName.equalsIgnoreCase("pubDate"))
				try {
					values.put(NewsDatabase.COLUMN_DATE,
							dfOut.format(dfIn.parse(buf.toString())));
				} catch (Exception e) {
					e.printStackTrace();
				}
			else if (localName.equalsIgnoreCase("creator"))
				values.put(NewsDatabase.COLUMN_AUTHOR, buf.toString());
			else if (localName.equalsIgnoreCase("title"))
				values.put(NewsDatabase.COLUMN_TITLE, buf.toString());
			else if (localName.equalsIgnoreCase("description"))
				values.put(NewsDatabase.COLUMN_TEXT, buf.toString());
			else if (localName.equalsIgnoreCase("category"))
				values.put(NewsDatabase.COLUMN_CATEGORY, buf.toString());
			else if (localName.equalsIgnoreCase("guid"))
				values.put(NewsDatabase.COLUMN_URL, buf.toString());

			else if (localName.equalsIgnoreCase("item"))
				db.insertItem(values);
		}

		super.endElement(uri, localName, qName);
	}
}
