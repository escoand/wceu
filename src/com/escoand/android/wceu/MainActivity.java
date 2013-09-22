package com.escoand.android.wceu;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.escoand.android.wceu.CategoryDialog.CategoryDialogListener;

public class MainActivity extends ActionBarActivity implements
		CategoryDialogListener {

	private NewsDatabase dbNews;
	private EventsDatabase dbEvents;
	private ListAdapter adp;

	private String displayType = "news";
	private String displayFilter = "";

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/* load settings */
		if (savedInstanceState != null) {
			displayType = savedInstanceState.getString("displayType");
			displayFilter = savedInstanceState.getString("displayFilter");
		}

		/* data */
		dbNews = new NewsDatabase(getBaseContext());
		dbEvents = new EventsDatabase(getBaseContext());
		adp = new ListAdapter(getBaseContext());

		/* list */
		ListView list = (ListView) findViewById(R.id.listNews);
		list.setEmptyView(findViewById(R.id.listEmpty));
		list.setAdapter(adp);
		refreshDisplay();

		/* listeners */
		findViewById(R.id.listRefresh).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						refreshData();
					}
				});
		if (findViewById(R.id.banner) != null)
			findViewById(R.id.banner).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							CategoryDialog diag = new CategoryDialog();
							diag.show(getSupportFragmentManager(), "");
						}
					});

		/* action bar */
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		TabListener tabl = new ActionBar.TabListener() {
			@Override
			public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			}

			@Override
			public void onTabSelected(Tab tab, FragmentTransaction ft) {
				if (tab.getText().equals(getString(R.string.menuNews)))
					displayType = "news";
				else if (tab.getText().equals(getString(R.string.menuEvents)))
					displayType = "events";
				refreshDisplay();
			}

			@Override
			public void onTabReselected(Tab tab, FragmentTransaction ft) {
				onTabSelected(tab, ft);
			}
		};

		/* tabs */
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.addTab(actionBar.newTab().setText(R.string.menuNews)
					.setTabListener(tabl));
			actionBar.addTab(actionBar.newTab().setText(R.string.menuEvents)
					.setTabListener(tabl));
		}

		/* dropdown */
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			actionBar.setListNavigationCallbacks(ArrayAdapter
					.createFromResource(this, R.array.listSpinner,
							android.R.layout.simple_spinner_dropdown_item),
					new ActionBar.OnNavigationListener() {
						@Override
						public boolean onNavigationItemSelected(int pos, long id) {
							if (pos == 0)
								displayType = "news";
							else if (pos == 1)
								displayType = "events";
							refreshDisplay();
							return true;
						}
					});
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putString("displayType", displayType);
		outState.putString("displayFilter", displayFilter);
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {

		/* category */
		case R.id.menuRegion:
			CategoryDialog diag = new CategoryDialog();
			diag.show(getSupportFragmentManager(), "");
			break;

		/* refresh */
		case R.id.menuRefresh:
			refreshData();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCategorySelected(final String category) {
		displayFilter = category;
		refreshDisplay();
	}

	public void refreshDisplay() {
		final ImageView banner = (ImageView) findViewById(R.id.banner);

		/* news */
		if (displayType.equals("news")) {
			adp.changeCursor(dbNews.getList());
			if (displayFilter == null || displayFilter.equals(""))
				adp.changeCursor(dbNews.getList());
			else {
				adp.changeCursor(dbNews.getList(NewsDatabase.COLUMN_CATEGORY
						+ "=?", new String[] { displayFilter }));
			}
		}

		/* events */
		else if (displayType.equals("events")) {
			adp.changeCursor(dbEvents.getList());
			if (displayFilter == null || displayFilter.equals(""))
				adp.changeCursor(dbEvents.getList());
			else {
				adp.changeCursor(dbEvents.getList(NewsDatabase.COLUMN_CATEGORY
						+ "=?", new String[] { displayFilter }));
			}
		}

		/* banner */
		if (banner != null) {

			/* banner image */
			if (displayFilter.equals("africa"))
				banner.setImageResource(R.drawable.banner_africa);
			else if (displayFilter.equals("america"))
				banner.setImageResource(R.drawable.banner_america);
			else if (displayFilter.equals("asia"))
				banner.setImageResource(R.drawable.banner_asia);
			else if (displayFilter.equals("auspac"))
				banner.setImageResource(R.drawable.banner_auspac);
			else if (displayFilter.equals("europe"))
				banner.setImageResource(R.drawable.banner_europe);
			else if (banner != null)
				banner.setImageResource(R.drawable.banner_wceu);

			/* banner size */
			banner.getLayoutParams().height = (int) ((double) getWindowManager()
					.getDefaultDisplay().getWidth()
					/ (double) banner.getDrawable().getIntrinsicWidth() * (double) banner
					.getDrawable().getIntrinsicHeight());
		}
	}

	public void refreshData() {
		new AsyncTask<Void, Void, Void>() {
			ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 10,
					TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

			String[] urls = getResources().getStringArray(R.array.urlEvents);
			String[] categories = getResources().getStringArray(
					R.array.categorieValues);

			// hide listing
			@Override
			protected void onPreExecute() {
				findViewById(R.id.listProgress).setVisibility(View.VISIBLE);
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {

				// refresh news
				dbNews.clear();
				pool.execute(new RefreshNews(getBaseContext(),
						getString(R.string.urlNews)));

				// refresh events
				dbEvents.clear();
				for (int i = 0; i < urls.length; i++) {
					pool.execute(new RefreshEvents(getBaseContext(), urls[i],
							categories[i]));
				}

				// wait for tasks
				pool.shutdown();
				try {
					pool.awaitTermination(30, TimeUnit.SECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				return null;
			}

			// show listing
			@Override
			protected void onPostExecute(Void result) {
				refreshDisplay();
				findViewById(R.id.listProgress).setVisibility(View.GONE);
				super.onPostExecute(result);
			}
		}.execute();
	}
}