package com.example.appointmentapp.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class FormatString {

    public static void makeTajFormat(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            boolean isFormatting = false;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (isFormatting) return;

                isFormatting = true;

                // generate the string from digits only
                String raw = editable.toString().replace("-", "");
                StringBuilder digitsOnly = new StringBuilder();
                int i;
                for (i = 0; i< raw.length() && digitsOnly.length() < 9; i++) {
                    char c = raw.charAt(i);
                    // checking if the char is digit
                    if (Character.isDigit(c)) {
                        digitsOnly.append(c);
                    }
                }

                // Formázás: XXX-XXX-XXX
                StringBuilder stringToShow = new StringBuilder();
                i = 0;
                for (Character ch: digitsOnly.toString().toCharArray() ) {
                    i++;
                    stringToShow.append(ch);
                    if(i == 4) {
                        char chTemp = stringToShow.charAt(i-1);
                        stringToShow.delete(i-1,i);
                        stringToShow.append("-");
                        stringToShow.append(chTemp);
                    }
                    else if(i == 7) {
                        char chTemp = stringToShow.charAt(i-2);
                        stringToShow.delete(i-2,i-1);
                        stringToShow.append("-");
                        stringToShow.append(chTemp);                    }
                }

                editText.setText(stringToShow.toString());
                editText.setSelection(stringToShow.toString().length()); // the end of the string

                isFormatting = false;
            }
        });
    }
}
