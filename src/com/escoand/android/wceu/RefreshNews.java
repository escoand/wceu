package com.escoand.android.wceu;

import java.net.URL;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;

public class RefreshNews implements Runnable {
	private NewsDatabase dbNews;
	private final String url;

	public RefreshNews(final Context context, final String url) {
		dbNews = new NewsDatabase(context);
		this.url = url;
	}

	@Override
	public void run() {
		SAXParserFactory factory;
		SAXParser parser;
		XMLReader reader;
		RSSHandler handler;
		InputSource input;

		try {
			factory = SAXParserFactory.newInstance();
			parser = factory.newSAXParser();
			reader = parser.getXMLReader();
			handler = new RSSHandler(dbNews);
			input = new InputSource(new URL(url).openStream());

			reader.setContentHandler(handler);
			reader.parse(input);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
