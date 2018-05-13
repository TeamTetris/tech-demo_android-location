package com.example.julianweise.locationapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class PermissionDialogFragment extends DialogFragment {

    public interface PermissionDialogListener {
        void onDialogGotItClicked(DialogFragment dialog);
    }

    private PermissionDialogListener mListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_permissions_required)
            .setPositiveButton(R.string.dialog_permissions_got_it, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    mListener.onDialogGotItClicked(PermissionDialogFragment.this);
                }
            });
        return builder.create();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (PermissionDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement NoticeDialogListener");
        }
    }
}