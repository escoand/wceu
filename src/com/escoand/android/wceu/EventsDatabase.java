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

public class EventsDatabase extends AbstractDatabase {
	public static final String DATABASE_NAME = "events";
	public static final int DATABASE_VERSION = 2;

	protected static final String COLUMN_DATE = "date";
	protected static final String COLUMN_DATEEND = "dateend";
	protected static final String COLUMN_ALLDAY = "allday";
	protected static final String COLUMN_TITLE = "title";
	protected static final String COLUMN_TEXT = "text";
	protected static final String COLUMN_URL = "url";
	protected static final String COLUMN_CATEGORY = "category";

	public EventsDatabase(final Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		TABLE_NAME = "events";
		COLUMNS = new String[] { COLUMN_DATE, COLUMN_DATEEND, COLUMN_ALLDAY,
				COLUMN_TITLE, COLUMN_TEXT, COLUMN_URL, COLUMN_CATEGORY };
	}

	public Cursor getList() {
		return getItems(new String[] { COLUMN_DATE, COLUMN_DATEEND,
				COLUMN_TITLE, COLUMN_URL, COLUMN_CATEGORY }, COLUMN_DATE
				+ " desc");
	}

	public Cursor getList(String selection, String[] selectionArgs) {
		return getItems(new String[] { COLUMN_DATE, COLUMN_DATEEND,
				COLUMN_TITLE, COLUMN_URL, COLUMN_CATEGORY }, selection,
				selectionArgs, COLUMN_DATE + " desc");
	}
}