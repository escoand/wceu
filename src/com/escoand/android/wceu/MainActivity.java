package com.escoand.android.wceu;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.escoand.android.wceu.CategoryDialog.CategoryDialogListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class MainActivity extends Activity implements CategoryDialogListener {

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
		PullToRefreshListView list = (PullToRefreshListView) findViewById(R.id.listNews);
		list.setEmptyView(findViewById(R.id.listEmpty));
		list.setAdapter(adp);
		list.setShowIndicator(true);
		list.setOnRefreshListener(new OnRefreshListener<ListView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refreshData();
			}
		});
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
			findViewById(R.id.banner).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					CategoryDialog diag = new CategoryDialog();
					diag.show(getFragmentManager(), "");
				}
			});

		/* action bar */
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);

		TabListener tabl = new TabListener() {
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
					new OnNavigationListener() {
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
			diag.show(getFragmentManager(), "");
			break;

		/* contact */
		case R.id.menuContact:
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
					"mailto", "centraloffice@worldsceunion.org", null));
			startActivity(Intent.createChooser(intent,
					getString(R.string.messageEMail)));
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onCategorySelected(final String category) {
		displayFilter = category;
		refreshDisplay();
	}

	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
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
			int width = 0;
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				Point size = new Point();
				getWindowManager().getDefaultDisplay().getSize(size);
				width = size.x;
			} else {
				Display d = getWindowManager().getDefaultDisplay();
				width = d.getWidth();
			}
			banner.getLayoutParams().height = (int) ((double) width
					/ (double) banner.getDrawable().getIntrinsicWidth() * (double) banner
					.getDrawable().getIntrinsicHeight());
		}
	}

	public void refreshData() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... params) {
				return RefreshHandler.refreshAll(getBaseContext(), dbNews,
						dbEvents);
			}

			@Override
			protected void onPostExecute(Boolean result) {

				/* show error */
				if (!result)
					Toast.makeText(getBaseContext(),
							getString(R.string.messageIOException),
							Toast.LENGTH_LONG).show();

				/* refresh listing */
				refreshDisplay();
				((PullToRefreshListView) findViewById(R.id.listNews))
						.onRefreshComplete();

				super.onPostExecute(result);
			}
		}.execute();
	}
}
