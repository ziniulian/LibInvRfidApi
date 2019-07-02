package com.invengo.lib.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

public class MessageDialog {

	private AlertDialog.Builder dialog;

	public MessageDialog(Context context) {
		this.dialog = new AlertDialog.Builder(context);
	}

	public void show(String msg) {
		show(msg, null, null);
	}

	public void show(String msg, String title) {
		show(msg, title, null);
	}

	public void show(String msg, String title, Drawable icon) {
		this.dialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		if (icon != null)
			this.dialog.setIcon(icon);
		if (title != null)
			this.dialog.setTitle(title);
		this.dialog.setMessage(msg);
		this.dialog.show();
	}

	public void show(int msg) {
		show(msg, 0, 0);
	}

	public void show(int msg, int title) {
		show(msg, title, 0);
	}

	public void show(int msg, int title, int icon) {
		this.dialog.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		if (icon != 0)
			this.dialog.setIcon(icon);
		if (title != 0)
			this.dialog.setTitle(title);
		this.dialog.setMessage(msg);
		this.dialog.show();
	}

	public void showError(String msg, String title) {
		show(msg,
				title,
				this.dialog.getContext().getResources()
						.getDrawable(android.R.drawable.ic_dialog_alert));
	}

	public void showError(String msg) {
		show(msg,
				null,
				this.dialog.getContext().getResources()
						.getDrawable(android.R.drawable.ic_dialog_alert));
	}

	public void showError(int msg, int title) {
		show(msg, title, android.R.drawable.ic_dialog_alert);
	}

	public void showError(int msg) {
		show(msg, 0, android.R.drawable.ic_dialog_alert);
	}
}
