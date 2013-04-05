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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public abstract class AbstractDatabase extends SQLiteOpenHelper {
	protected String TABLE_NAME;
	protected String[] COLUMNS;

	protected static final String DATE_FORMAT = "yyyyMMddHHmmss";

	public AbstractDatabase(final Context context, final String name,
			final CursorFactory factory, final int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(final SQLiteDatabase db) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < COLUMNS.length - 1; i++)
			builder.append(COLUMNS[i] + ", ");

		if (COLUMNS.length > 0)
			builder.append(COLUMNS[COLUMNS.length - 1]);

		db.execSQL("CREATE VIRTUAL TABLE " + TABLE_NAME + " USING fts3("
				+ builder.toString() + ")");
	}

	@Override
	public void onUpgrade(final SQLiteDatabase db, final int oldVersion,
			final int newVersion) {
		if (oldVersion != newVersion)
			clear(db);
	}

	public void clear() {
		clear(getWritableDatabase());
	}

	private void clear(final SQLiteDatabase db) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}

	public final long insertItem(final ContentValues values) {
		return getWritableDatabase().insert(TABLE_NAME, null, values);
	}

	public final Cursor searchItems(final String[] columns,
			final String searchFor, final String orderBy) {
		return getItems(columns, TABLE_NAME + " MATCH ?",
				new String[] { searchFor }, orderBy);
	}

	public final Cursor getItems(final String[] columns, final String orderBy) {
		return getItems(columns, null, new String[] {}, orderBy);
	}

	public final Cursor getItems(final String[] columns,
			final String selection, final String[] selectionArgs,
			final String orderBy) {
		Cursor cursor = getReadableDatabase().query(TABLE_NAME,
				joinStringArrays(columns, new String[] { "rowid as _id" }),
				selection, selectionArgs, null, null, orderBy);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}

	private final static String[] joinStringArrays(final String[]... arrays) {
		int lengh = 0;
		for (String[] array : arrays) {
			lengh += array.length;
		}
		String[] result = new String[lengh];
		int pos = 0;
		for (String[] array : arrays) {
			for (String element : array) {
				result[pos] = element;
				pos++;
			}
		}
		return result;
	}
}