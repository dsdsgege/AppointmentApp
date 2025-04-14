package com.example.appointmentapp.utils;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Dialog {

    // method for making an AlertDialog with a message
    public static AlertDialog createDialog(Context c, String msg, String btnMsg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        if(msg.isEmpty()) {
            builder.setMessage("Valami nem jó.");
        } else {
            builder.setMessage(msg);
        }
        builder.setPositiveButton(btnMsg, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        return builder.create();
    }
}
