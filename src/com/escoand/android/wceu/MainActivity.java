package com.escoand.android.wceu;

import android.database.Cursor;
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

	private enum DIPLAY_TYPE {
		DISPLAY_NEWS, DISPLAY_EVENTS
	};

	private NewsDatabase dbNews;
	private EventsDatabase dbEvents;
	private ListAdapter adp;
	private DIPLAY_TYPE type = DIPLAY_TYPE.DISPLAY_NEWS;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/* data */
		dbNews = new NewsDatabase(getBaseContext());
		dbEvents = new EventsDatabase(getBaseContext());
		adp = new ListAdapter(getBaseContext());

		/* list */
		ListView list = (ListView) findViewById(R.id.listNews);
		list.setEmptyView(findViewById(R.id.listEmpty));
		list.setAdapter(adp);
		onCategorySelected(null);

		/* listeners */
		findViewById(R.id.listRefresh).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						refreshData();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {

		/* categories */
		case R.id.menuCategory:
			CategoryDialog diag = new CategoryDialog();
			diag.show(getSupportFragmentManager(), "test");
			break;

		/* news */
		case R.id.menuNews:
			onCategorySelected(null);
			break;

		/* events */
		case R.id.menuEvents:
			adp.changeCursor(dbEvents.getList());
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
		final ImageView banner = (ImageView) findViewById(R.id.banner);

		/* news */
		if (type == DIPLAY_TYPE.DISPLAY_NEWS) {
			if (category == null || category.equals(""))
				adp.changeCursor(dbNews.getList());
			else {
				adp.changeCursor(dbNews.getList(NewsDatabase.COLUMN_CATEGORY
						+ "=?", new String[] { category }));
			}
		}

		/* events */
		else if (type == DIPLAY_TYPE.DISPLAY_EVENTS) {
			if (category == null || category.equals(""))
				adp.changeCursor(dbEvents.getList());
			else {
				adp.changeCursor(dbEvents.getList(NewsDatabase.COLUMN_CATEGORY
						+ "=?", new String[] { category }));
			}
		}

		final Cursor cursor = adp.getCursor();

		/* banner */
		if (cursor.getCount() > 0) {
			if (cursor.getString(
					cursor.getColumnIndex(NewsDatabase.COLUMN_CATEGORY))
					.equals("africa"))
				banner.setImageResource(R.drawable.banner_africa);
			else if (cursor.getString(
					cursor.getColumnIndex(NewsDatabase.COLUMN_CATEGORY))
					.equals("america"))
				banner.setImageResource(R.drawable.banner_america);
			else if (cursor.getString(
					cursor.getColumnIndex(NewsDatabase.COLUMN_CATEGORY))
					.equals("asia"))
				banner.setImageResource(R.drawable.banner_asia);
			else if (cursor.getString(
					cursor.getColumnIndex(NewsDatabase.COLUMN_CATEGORY))
					.equals("auspac"))
				banner.setImageResource(R.drawable.banner_auspac);
			else if (cursor.getString(
					cursor.getColumnIndex(NewsDatabase.COLUMN_CATEGORY))
					.equals("europe"))
				banner.setImageResource(R.drawable.banner_europe);
			else if (banner != null)
				banner.setImageResource(R.drawable.banner_wceu);
		}

		/* banner size */
		banner.getLayoutParams().height = (int) ((double) getWindowManager()
				.getDefaultDisplay().getWidth()
				/ (double) banner.getDrawable().getIntrinsicWidth() * (double) banner
				.getDrawable().getIntrinsicHeight());
	}

	public void refreshData() {
		final RefreshTask task = new RefreshTask();
		task.activity = this;
		task.dbNews = dbNews;
		task.dbEvents = dbEvents;
		task.execute();
	}
}
