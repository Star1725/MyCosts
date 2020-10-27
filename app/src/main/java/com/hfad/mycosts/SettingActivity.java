package com.hfad.mycosts;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.hfad.mycosts.MainActivity.logFlag;

public class SettingActivity extends AppCompatActivity {

    private Spinner spinnerForDefPeriod;
    ArrayAdapter<String> arrayAdapterForSpinerDefPer;
    private String[] period;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        database = MainActivity.getDatabase();
        database.openConnect("SettingActivity");
        period = (MainActivity.period);
        spinnerForDefPeriod = (Spinner) findViewById(R.id.spinner_for_default_period);
        arrayAdapterForSpinerDefPer = new ArrayAdapter<String>(this, R.layout.layout_for_spinner_settingactiv, period);
        spinnerForDefPeriod.setAdapter(arrayAdapterForSpinerDefPer);
        spinnerForDefPeriod.setSelection(MainActivity.getPer());
        //слушатель выбора позиции в spinner
        spinnerForDefPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    MyDialogFragment myDialogFragment = MyDialogFragment.newInstance(3, 0, 0);
                    myDialogFragment.show( getSupportFragmentManager(), "dialog1");
                } else database.updateDefaultPeriod(position,0, 0, 0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }
    //РЕФЛИЗАЦИЯ ИНТЕРФЕЙСОВ СЛУШАТЕЛЕЙ ФРАГМЕНТОВ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //методы обработки нажатия кнопок "ОК" и "Отмена" в MyDialogFragment/////////////////////////////////////////////////////////////////////////////////////
    public void okClicked(List listDay) {
        if (logFlag) {
            Log.d("myLog", "---- SettingActivity - okClicked() ----");
        }
        Calendar startDAY, endDay;
        startDAY = Calendar.getInstance();
        endDay = Calendar.getInstance();
        if (logFlag) {
            Log.d("myLog", "---- SettingActivity - okClicked() - listDay.size() = " + listDay.size() + "----");
            Log.d("myLog", "---- SettingActivity - okClicked() - listDay.get(0) = " + listDay.get(0).toString() + "----");
            Log.d("myLog", "---- SettingActivity - okClicked() - listDay.get(listDay.size() - 1) = " + listDay.get(listDay.size() - 1).toString() + "----");
        }
        if (listDay.size() > 31){
            String textError = "Период не может быть более 31 дня!";
            Snackbar snackbar = Snackbar.make(spinnerForDefPeriod, textError, Snackbar.LENGTH_LONG);
            snackbar.setDuration(5000);
            snackbar.show();
        }else {
            startDAY.setTime((Date) listDay.get(0));
            endDay.setTime((Date) listDay.get(listDay.size() - 1));
            if (logFlag) {
                Log.d("myLog", "---- SettingActivity - okClicked() - startDAY = " + startDAY.toString() + "----");
                Log.d("myLog", "---- SettingActivity - okClicked() - endDay = " + endDay.toString() + "----");
                Log.d("myLog", "---- (6)MyDialogFragment() - CalendarPickerView - startDAY: " + startDAY.get(Calendar.DAY_OF_MONTH) +
                        "." + startDAY.get(Calendar.MONTH) + "." + startDAY.get(Calendar.YEAR) + "----");
                Log.d("myLog", "---- (6)MyDialogFragment() - CalendarPickerView - endDay: " + endDay.get(Calendar.DAY_OF_MONTH) +
                        "." + endDay.get(Calendar.MONTH) + "." + endDay.get(Calendar.YEAR) + "----");
            }
            int moreMonth = endDay.get(Calendar.MONTH) - startDAY.get(Calendar.MONTH);
            database.updateDefaultPeriod(3,startDAY.get(Calendar.DAY_OF_MONTH), endDay.get(Calendar.DAY_OF_MONTH), moreMonth);
            if (moreMonth == 0){
                period[3] = "c " + startDAY.get(Calendar.DAY_OF_MONTH) + " по " + endDay.get(Calendar.DAY_OF_MONTH) + " текущего месяца";
            }
            else {

            }
            period[3] = "c " + startDAY.get(Calendar.DAY_OF_MONTH) + " текущего месяца по " + endDay.get(Calendar.DAY_OF_MONTH) + " следующего месяца";
            arrayAdapterForSpinerDefPer.notifyDataSetChanged();
        }
    }
    public void cancelClicked() {
    }
}
