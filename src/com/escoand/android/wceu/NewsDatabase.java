/*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.escoand.android.wceu;

import android.content.Context;
import android.database.Cursor;

public class NewsDatabase extends AbstractDatabase {
	public static final String DATABASE_NAME = "news";
	public static final int DATABASE_VERSION = 1;

	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_TITLE = "title";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_CATEGORY = "category";
	public static final String COLUMN_URL = "url";

	public NewsDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		TABLE_NAME = "news";
		COLUMNS = new String[] { COLUMN_DATE, COLUMN_AUTHOR, COLUMN_TITLE,
				COLUMN_TEXT, COLUMN_URL, COLUMN_CATEGORY };
	}

	public final Cursor getDate(final String date) {
		return getItems(COLUMNS, COLUMN_DATE + "=?", new String[] { date },
				COLUMN_DATE + " desc");
	}

	public final Cursor getList() {
		return getItems(new String[] { COLUMN_DATE, COLUMN_TITLE,
				COLUMN_CATEGORY }, COLUMN_DATE + " desc");
	}

	public final Cursor getList(final String selection,
			final String[] selectionArgs) {
		return getItems(new String[] { COLUMN_DATE, COLUMN_TITLE,
				COLUMN_CATEGORY }, selection, selectionArgs, COLUMN_DATE
				+ " desc");
	}
}