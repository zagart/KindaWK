package com.vvsemir.kindawk.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.vvsemir.kindawk.R;
import com.vvsemir.kindawk.auth.AuthManager;

public class SettingsDialogFragment extends DialogFragment {
    Spinner newsDateSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_settings, null);
        builder.setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        AuthManager.getAppPreferences().set(AuthManager.PREFERENCE_NEWS_DATE_POSTED, getSpinnerSelection());
                        SettingsDialogFragment.this.getDialog().cancel();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SettingsDialogFragment.this.getDialog().cancel();
                    }
                });

        newsDateSpinner = (Spinner) view.findViewById(R.id.spinnerDateRange);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.settingsDatePosted_array, R.layout.spinner_item_settings);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_settings);
        newsDateSpinner.setAdapter(spinnerAdapter);

        newsDateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                  int currentSpinnerPos = parent.getSelectedItemPosition();
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {
              }
          });

        int newsDatePostedIndx = (int)AuthManager.getAppPreferences().get(AuthManager.PREFERENCE_NEWS_DATE_POSTED, 0);
        newsDateSpinner.setSelection(newsDatePostedIndx);

        return builder.create();
    }

    private int getSpinnerSelection() {
        return newsDateSpinner.getSelectedItemPosition();
    }
}
