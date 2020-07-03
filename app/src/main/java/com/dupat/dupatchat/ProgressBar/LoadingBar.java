package com.dupat.dupatchat.ProgressBar;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import com.dupat.dupatchat.R;

public class LoadingBar {

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingBar(Activity activity) {
        this.activity = activity;
    }

    public void startLoading()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.progress_layout, null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();
    }

    public void stopLoading()
    {
        alertDialog.dismiss();
    }
}
