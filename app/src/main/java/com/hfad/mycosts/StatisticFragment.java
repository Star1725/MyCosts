package com.hfad.mycosts;


import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.hfad.mycosts.MainActivity.logFlag;
import static com.hfad.mycosts.MainActivity.period;


/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {

    Context thiscontext;
    private Database database;
    Date[] date_Months = new Date[12];
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM ");
    private int dayBuy;
    private int weekBuy;
    private int monthBuy;
    private int yearBuy;
    private  GraphView graphCat;
    private  GraphView graphSubCat;
    private LineGraphSeries<DataPoint> series1;
    private BarGraphSeries<DataPoint> series2;
    private BarGraphSeries<DataPoint> series3;
    private BarGraphSeries<DataPoint> series4;
    private BarGraphSeries<DataPoint> series5;
    private BarGraphSeries<DataPoint> series6;
    private BarGraphSeries<DataPoint> series7;
    private CheckBox checkBox;
    private CheckBox checkBox2;
    private CheckBox checkBox3;
    private CheckBox checkBox4;
    private CheckBox checkBox5;
    private CheckBox checkBox6;
    private CheckBox checkBox7;
    private String[] NameCat;
    private Spinner spinnerForCat;
    ArrayAdapter<String> arrayAdapterForSpinnerCat;
    private Spinner spinnerForSubCat;
    ArrayAdapter<String> arrayAdapterForSpinnerSubCcat;

    public StatisticFragment() {
        // Required empty public constructor
    }

    public void uploadNameCat(){
        NameCat = new String[7];
        MainActivity.getCursor_For_NAME_CATEGORY().moveToFirst();
        for (int i = 0; i < 7; i++){
            NameCat[i] = MainActivity.getCursor_For_NAME_CATEGORY().getString(1);
            MainActivity.getCursor_For_NAME_CATEGORY().moveToNext();
        }
    }

    private DataPoint[] getDataPoint (int [][] array, int category){
        DataPoint[] dp = new DataPoint[12];
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i <= 11; i++){
            calendar.set(Calendar.MONTH, i);
            date_Months[i] = calendar.getTime();
            dp[i] = new DataPoint(date_Months[i], array[category][i]);
        }
        return dp;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (logFlag) {
            Log.d("myLog", "---- (3)StatisticFragment  -  onCreateView() ----");
        }
        //получение контекста
        thiscontext = inflater.getContext();
        database = new Database(thiscontext);
        database.openConnect("RedactorFragment");
        dayBuy = MainActivity.getDay();
        monthBuy = MainActivity.getWeek();
        yearBuy = MainActivity.getYear();


        //построение графика
        View view = inflater.inflate(R.layout.fragment_statistic, container, false);

        checkBox = view.findViewById(R.id.checkBox);
        checkBox2 = view.findViewById(R.id.checkBox2);
        checkBox3 = view.findViewById(R.id.checkBox3);
        checkBox4 = view.findViewById(R.id.checkBox4);
        checkBox5 = view.findViewById(R.id.checkBox5);
        checkBox6 = view.findViewById(R.id.checkBox6);
        checkBox7 = view.findViewById(R.id.checkBox7);

        checkBox.setOnCheckedChangeListener(this);
        checkBox2.setOnCheckedChangeListener(this);
        checkBox3.setOnCheckedChangeListener(this);
        checkBox4.setOnCheckedChangeListener(this);
        checkBox5.setOnCheckedChangeListener(this);
        checkBox6.setOnCheckedChangeListener(this);
        checkBox7.setOnCheckedChangeListener(this);

        uploadNameCat();

//настройка graph1
        graphCat = view.findViewById(R.id.graph_category);
        graphCat.setTitle("Статистика по категориям за " + MainActivity.getCalendar().get(Calendar.YEAR) + " год");
        graphCat.getViewport().setYAxisBoundsManual(true);
        graphCat.getViewport().setXAxisBoundsManual(true);
        graphCat.getViewport().setMaxY(MainActivity.getMaxOfArray1() + 1000);
        graphCat.getLegendRenderer().setVisible(true);//разрешает показывать лейблы графиков
        graphCat.setBackgroundColor(Color.LTGRAY);
        graphCat.getGridLabelRenderer().setNumHorizontalLabels(12);
        graphCat.getGridLabelRenderer().setLabelHorizontalHeight(50);
        graphCat.getGridLabelRenderer().setPadding(30);
        graphCat.getGridLabelRenderer().setHumanRounding(false, true);
        graphCat.getGridLabelRenderer().setHorizontalLabelsAngle(90);//- угол поворота lebelsX
        graphCat.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX){
                    return simpleDateFormat.format(new Date((long) value));
                }
                else return super.formatLabel(value, isValueX);
            }
        });

        series1 = new LineGraphSeries<>(
                getDataPoint(MainActivity.getArrayForCategory(), 0)
        );
        series1.setColor(Color.parseColor("#33000000"));// +
        series1.setAnimated(true);
        //присваиваем графикам лейблы
        series1.setTitle(NameCat[0]);
//!!!!!! ДЛЯ series ТИПА  LineGraphSeries !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //зарисовывание пространства под графиком и изменение его цвета
        series1.setDrawBackground(true);
        series1.setBackgroundColor(Color.parseColor("#1A000000"));//Рекомендуется использовать полупрозрачный цвет.
        /**                                            #40 - прозоачность 000000 - цвет
         И ниже для кода непрозрачности:
         100% – FF, 95% – F2, 90% – E6, 85% – D9, 80% – CC, 75% – BF, 70% – B3, 65% – A6, 60% – 99, 55% – 8oC, 50% – 80,
         45% – 73, 40% – 66, 35% – 59, 30% – 4D, 25% – 40, 20% – 33, 15% – 26, 10% – 1А, 5% – 0D, 0% – 00
         Если вы хотите его в коде, просто:
         Используйте нижеприведенный код для чистого черного:
         mComponentName.setBackgroundColor(Color.parseColor("#000000")); - непрозрачный чурный
         Теперь, если вы хотите использовать непрозрачность, вы можете использовать приведенный ниже код:
         mComponentName.setBackgroundColor(Color.parseColor("#000000")); - на 50% прозрачный чурный
         */
        series1.setDrawDataPoints(true);//разрешение изменения точек
        series1.setDataPointsRadius(10);//установка радиуса точек
        series1.setThickness(10);//толщина линии
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

        series2 = new BarGraphSeries<DataPoint>(
                getDataPoint(MainActivity.getArrayForCategory(), 1)
        );
        series2.setColor(Color.RED);
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.BLACK);
        series2.setValuesOnTopSize(30);
        series2.setSpacing(40);
        series2.setAnimated(true);
        MainActivity.getCursor_For_NAME_CATEGORY().moveToNext();
        series2.setTitle(NameCat[1]);

        series3 = new BarGraphSeries<DataPoint>(
                getDataPoint(MainActivity.getArrayForCategory(), 2)
        );
        series3.setColor(Color.GREEN);
        series3.setDrawValuesOnTop(true);
        series3.setValuesOnTopColor(Color.BLACK);
        series3.setValuesOnTopSize(30);
        series3.setSpacing(40);
        series3.setAnimated(true);
        series3.setTitle(NameCat[2]);

        series4 = new BarGraphSeries<DataPoint>(
                getDataPoint(MainActivity.getArrayForCategory(), 3)
        );
        series4.setColor(Color.BLUE);
        series4.setDrawValuesOnTop(true);
        series4.setValuesOnTopColor(Color.BLACK);
        series4.setValuesOnTopSize(30);
        series4.setSpacing(40);
        series4.setAnimated(true);
        series4.setTitle(NameCat[3]);

        series5 = new BarGraphSeries<DataPoint>(
                getDataPoint(MainActivity.getArrayForCategory(), 4)
        );
        series5.setColor(Color.YELLOW);
        series5.setDrawValuesOnTop(true);
        series5.setValuesOnTopColor(Color.BLACK);
        series5.setValuesOnTopSize(30);
        series5.setSpacing(40);
        series5.setAnimated(true);
        series5.setTitle(NameCat[4]);

        series6 = new BarGraphSeries<DataPoint>(
                getDataPoint(MainActivity.getArrayForCategory(), 5)
        );
        series6.setColor(Color.MAGENTA);
        series6.setDrawValuesOnTop(true);
        series6.setValuesOnTopColor(Color.BLACK);
        series6.setValuesOnTopSize(30);
        series6.setSpacing(40);
        series6.setAnimated(true);
        series6.setTitle(NameCat[5]);

        series7 = new BarGraphSeries<DataPoint>(
                getDataPoint(MainActivity.getArrayForCategory(), 6)
        );
        series7.setColor(Color.CYAN);
        series7.setDrawValuesOnTop(true);
        series7.setValuesOnTopColor(Color.BLACK);
        series7.setValuesOnTopSize(30);
        series7.setSpacing(40);
        series7.setAnimated(true);
        series7.setTitle(NameCat[6]);

        graphCat.getViewport().setMinX(date_Months[0].getTime());
        graphCat.getViewport().setMaxX(date_Months[11].getTime());
        checkBox.setChecked(true);

        spinnerForCat = view.findViewById(R.id.spinner_cat);
        arrayAdapterForSpinnerCat = new ArrayAdapter<String>(inflater.getContext(), R.layout.layout_for_spiner_mycostfrag, NameCat);
        spinnerForCat.setAdapter(arrayAdapterForSpinnerCat);
        //слушатель выбора позиции в spinnerForCat
        spinnerForCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //настройка graphSubCat
        graphSubCat = view.findViewById(R.id.graph_subcategory);
        graphSubCat.setTitle("Статистика по подкатегориям за " + MainActivity.getCalendar().get(Calendar.YEAR) + " год");
        graphSubCat.getViewport().setYAxisBoundsManual(true);
        graphSubCat.getViewport().setXAxisBoundsManual(true);
        graphSubCat.getViewport().setMaxY(MainActivity.getMaxOfArray1() + 1000);
        graphSubCat.getLegendRenderer().setVisible(true);//разрешает показывать лейблы графиков
        graphSubCat.setBackgroundColor(Color.LTGRAY);
        graphSubCat.getGridLabelRenderer().setNumHorizontalLabels(12);
        graphSubCat.getGridLabelRenderer().setLabelHorizontalHeight(50);
        graphSubCat.getGridLabelRenderer().setPadding(30);
        graphSubCat.getGridLabelRenderer().setHumanRounding(false, true);
        graphSubCat.getGridLabelRenderer().setHorizontalLabelsAngle(90);//- угол поворота lebelsX
        graphSubCat.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX){
                    return simpleDateFormat.format(new Date((long) value));
                }
                else return super.formatLabel(value, isValueX);
            }
        });
        graphSubCat.getViewport().setMinX(date_Months[0].getTime());
        graphSubCat.getViewport().setMaxX(date_Months[11].getTime());

        return view;
    }



    //слушатель для radioButton
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (logFlag) {
            Log.d("myLog", "---- (3)StatisticFragment  -  onCheckedChanged() ----");
        }
        switch (compoundButton.getId()){
            case R.id.checkBox:
                if (b){
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series1);
                } else {
                    graphCat.removeSeries(series1);
                }
                break;
            case R.id.checkBox2:
                if (b){
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                    checkBox6.setChecked(false);
                    checkBox7.setChecked(false);
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series2);
                } else {
                    graphCat.removeSeries(series2);
                }
                break;
            case R.id.checkBox3:
                if (b){
                    checkBox2.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                    checkBox6.setChecked(false);
                    checkBox7.setChecked(false);
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series3);
                } else {
                    graphCat.removeSeries(series3);
                }
                break;
            case R.id.checkBox4:
                if (b){
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox5.setChecked(false);
                    checkBox6.setChecked(false);
                    checkBox7.setChecked(false);
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series4);
                } else {
                    graphCat.removeSeries(series4);
                }
                break;
            case R.id.checkBox5:
                if (b){
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox6.setChecked(false);
                    checkBox7.setChecked(false);
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series5);
                } else {
                    graphCat.removeSeries(series5);
                }
                break;
            case R.id.checkBox6:
                if (b){
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                    checkBox7.setChecked(false);
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series6);
                } else {
                    graphCat.removeSeries(series6);
                }
                break;
            case R.id.checkBox7:
                if (b){
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);
                    checkBox6.setChecked(false);
                    graphCat.getLegendRenderer().resetStyles();
                    graphCat.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
                    graphCat.addSeries(series7);
                } else {
                    graphCat.removeSeries(series7);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        if (logFlag) {
            Log.d("myLog", "---- (3)StatisticFragment  -  onResume() ----");
        }
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (!getUserVisibleHint())
        {
            return;
        }
        if (logFlag) {
            Log.d("myLog", "!!!!!!!!!!!!!!!!!!!!! (3)StatisticFragment  - в фокусе !!!!!!!!!!!!!!!!!!!!!!!");
        }
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (logFlag) {
            Log.d("myLog", "---- (3)StatisticFragment  -  setUserVisibleHint(boolean isVisibleToUser) ----");
        }
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && isVisibleToUser) {
            onResume();
            series1.resetData(getDataPoint(MainActivity.getArrayForCategory(), 0));
            series2.resetData(getDataPoint(MainActivity.getArrayForCategory(), 1));
            series3.resetData(getDataPoint(MainActivity.getArrayForCategory(), 2));
            series4.resetData(getDataPoint(MainActivity.getArrayForCategory(), 3));
            series5.resetData(getDataPoint(MainActivity.getArrayForCategory(), 4));
            series6.resetData(getDataPoint(MainActivity.getArrayForCategory(), 5));
            series7.resetData(getDataPoint(MainActivity.getArrayForCategory(), 6));
        }
    }

    @Override
    public void onPause() {
        if (logFlag) {
            Log.d("myLog", "---- (3)StatisticFragment()  -  onPause() ----");
        }
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }
}
