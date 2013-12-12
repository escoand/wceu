package com.escoand.android.wceu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CategoryDialog extends DialogFragment {
	CategoryDialogListener listener;

	public interface CategoryDialogListener {
		public void onCategorySelected(String category);
	}

	@Override
	public void onAttach(Activity activity) {
		try {
			listener = (CategoryDialogListener) activity;
		} catch (ClassCastException e) {
			e.printStackTrace();
		}

		super.onAttach(activity);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

		adb.setTitle(R.string.menuRegion);
		adb.setItems(R.array.categories, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				listener.onCategorySelected(getResources().getStringArray(
						R.array.categoryValues)[which]);
			}
		});
		adb.setCancelable(true);

		return adb.create();
	}
}
