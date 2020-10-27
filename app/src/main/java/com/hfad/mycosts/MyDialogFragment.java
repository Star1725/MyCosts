package com.hfad.mycosts;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

import static com.hfad.mycosts.MainActivity.logFlag;

public class MyDialogFragment extends DialogFragment {
    CalendarPickerView finalDatePicker = null;
    int hint;

    public static MyDialogFragment newInstance(int hint, int groupId, int time) {
        MyDialogFragment frag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("hint", hint);
        args.putInt("groupId", groupId);
        args.putInt("time", time);
        frag.setArguments(args);
        return frag;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        hint = getArguments().getInt("hint");
        final int groupId = getArguments().getInt("groupId");
        final int time = getArguments().getInt("time");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view;
        EditText editText = null;

        switch (hint){
            case 0:
                view = inflater.inflate(R.layout.layout_for_my_dialog_ragment, null);
                editText = view.findViewById(R.id.editText_for_set);
                editText.setHint("Введите новое имя категории");
                builder.setView(view);
                break;
            case 1:
                view = inflater.inflate(R.layout.layout_for_my_dialog_ragment, null);
                editText = view.findViewById(R.id.editText_for_set);
                editText.setHint("Введите новое значение");
                builder.setView(view);
                break;
            case 2:
                view = inflater.inflate(R.layout.layout_for_my_dialog_ragment, null);
                editText = view.findViewById(R.id.editText_for_set);
                editText.setHint("Введите новое имя подкатегории");
                builder.setView(view);
                break;
            case 3:
                if (logFlag) {
                    Log.d("myLog", "---- (6)MyDialogFragment() - CalendarPickerView ----");
                }
                view = inflater.inflate(R.layout.layout_for_dialog_calendar_picker, null);
                Date today = new Date();
                Calendar nextYear = Calendar.getInstance();
                Calendar nextMonth = Calendar.getInstance();;
                Calendar beforeMonth = Calendar.getInstance();;
                nextMonth.add(Calendar.MONTH, 1);
                beforeMonth.add(Calendar.MONTH, -1);
                finalDatePicker = view.findViewById(R.id.calendar_choice);
                finalDatePicker.init(beforeMonth.getTime(), nextMonth.getTime())
                        .inMode(CalendarPickerView.SelectionMode.RANGE)
                        .withHighlightedDate(today);

                builder.setView(view);
                finalDatePicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
                    @Override
                    public void onDateSelected(Date date) {

                        Calendar calSelect = Calendar.getInstance();
                        calSelect.setTime(date);
                        String selectDate = "" + calSelect.get(Calendar.DAY_OF_MONTH)
                                + ":" + (calSelect.get(Calendar.MONTH) + 1)
                                + ":" + calSelect.get(Calendar.YEAR);
                        Toast.makeText(inflater.getContext(), selectDate, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onDateUnselected(Date date) {

                    }
                });
                break;
        }

        final EditText finalEditText = editText;
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (hint != 3){
                            String dataString = finalEditText.getText().toString();
                            try {
                                int dataCount = Integer.parseInt(dataString);
                                //если введено новое имя
                                ((MainActivity) getActivity()).okClicked(dataString, dataCount, hint, groupId, time);
                            } catch (NumberFormatException e) {
                                //если введено новое значение
                                ((MainActivity) getActivity()).okClicked(dataString, 0, hint, groupId, time);
                                e.printStackTrace();
                            }
                        } else {
                            ((SettingActivity) getActivity()).okClicked(finalDatePicker.getSelectedDates());
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (hint != 3) ((MainActivity) getActivity()).cancelClicked();
                    }
                });
        return builder.create();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        String frag = null;
        if (hint == 0) frag = "myCostFragment, change name category";
        else if (hint == 1) frag = "myCostFragment, change max cost for category";
        else if (hint == 2) frag = "redactorFragment, change name subCategory";
        else if (hint == 3) frag = "settingActivity, CalendarPickerView";
        if (logFlag) {
            Log.d("myLog", "---- (6) ---- MyDialogFragment - onDestroy() - фрагмент для " + frag + ") ----");
        }
    }
}
