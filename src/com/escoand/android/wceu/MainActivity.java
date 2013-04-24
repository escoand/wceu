package com.escoand.android.wceu;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.escoand.android.wceu.CategoryDialog.CategoryDialogListener;

public class MainActivity extends FragmentActivity implements
		CategoryDialogListener {

	private NewsDatabase dbNews;
	private EventsDatabase dbEvents;
	private ListAdapter adp;

	private String displayType = "news";
	private String displayFilter = "";

	@SuppressLint("NewApi")
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
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
				actionBar.setDisplayShowHomeEnabled(false);

			TabListener tabl = new ActionBar.TabListener() {
				@Override
				public void onTabUnselected(Tab tab, FragmentTransaction ft) {
				}

				@SuppressLint("NewApi")
				@Override
				public void onTabSelected(Tab tab, FragmentTransaction ft) {
					if (tab.getText().equals(getString(R.string.menuNews)))
						displayType = "news";
					else if (tab.getText().equals(
							getString(R.string.menuEvents)))
						displayType = "events";
					refreshDisplay();
				}

				@Override
				public void onTabReselected(Tab tab, FragmentTransaction ft) {
					onTabSelected(tab, ft);
				}
			};

			/* tabs */
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
			actionBar.addTab(actionBar.newTab().setText(R.string.menuNews)
					.setTabListener(tabl));
			actionBar.addTab(actionBar.newTab().setText(R.string.menuEvents)
					.setTabListener(tabl));
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
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {

		/* news */
		case R.id.menuNews:
			displayType = "news";
			refreshDisplay();
			break;

		/* events */
		case R.id.menuEvents:
			displayType = "events";
			refreshDisplay();
			break;

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
		final RefreshTask task = new RefreshTask();
		task.activity = this;
		task.dbNews = dbNews;
		task.dbEvents = dbEvents;
		task.execute();
	}
}
