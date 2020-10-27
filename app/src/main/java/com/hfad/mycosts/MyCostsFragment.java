package com.hfad.mycosts;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;

import static com.hfad.mycosts.MainActivity.firstLoad_FOR_myCostsFragmen;
import static com.hfad.mycosts.MainActivity.logFlag;
import static com.hfad.mycosts.MainActivity.myCostsFragmenINfocus;
import static com.hfad.mycosts.MainActivity.period;
import static com.hfad.mycosts.MainActivity.redactorFragmentINfocus;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCostsFragment extends Fragment {

    public interface listenerForMyCostsFragment {
        public void changePeriod(int per, int startDay, int endDay, int moremonth);
        public void createUI();
        public void updateUIforMyCostsFragment();
    }

    listenerForMyCostsFragment listenerForMyCostsFragment;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listenerForMyCostsFragment = (listenerForMyCostsFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement clickListenerForMyCostsFragment");
        }
    }

    private static final int CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY = 0;
    private static final int CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD = 1;

    private Context thiscontext;

    //установки для работы анимации ///////////////////////////////////////////////////////////////////
    private static int[] maxCost = {0, 0, 0, 0, 0, 0, 0};
    private static int[] tempMaxCost = {0, 0, 0, 0, 0, 0, 0};
    private static int[] currentCost = {0, 0, 0, 0, 0, 0, 0};
    private static int[] tempCurrentCost = {0, 0, 0, 0, 0, 0, 0};
    private static boolean[] isChange_maxCost = {false, false, false, false, false, false, false};
    private static boolean[] isChange_currentCost = {false, false, false, false, false, false, false};

    //установка значений макс. и текущих трат для idCategory
    //установка макс. значений
    public void setMaxCosts(int maxValue, int idCategory) {
        setTempMaxCosts(this.maxCost[idCategory], idCategory);
        this.maxCost[idCategory] = maxValue;
        if (tempMaxCost[idCategory] != maxCost[idCategory]) {
            this.isChange_maxCost[idCategory] = true;
        }
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setMaxCosts() - maxCost[" + idCategory + "] = " + maxValue + ", tempMaxCost[" + idCategory + "] = " + tempMaxCost[idCategory] + " ----");
        }
    }

    private void setTempMaxCosts(int tempCosts, int idCategory) {
        this.tempMaxCost[idCategory] = tempCosts;
    }

    //установка значений макс. трат для AllCategory
    public void setMaxCostForAllCategory(Cursor cursor, int per) {
        int[] maxSum = {0, 0, 0, 0, 0, 0, 0};
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setMaxCostForAllCategory() - cursor.getCount() = " + cursor.getCount() + " за период " + period[per] + " ----");
        }
        if (cursor.getCount() == 0) {
            if (logFlag) {
                Log.d("myLog", "---- (1) ---- MyCostsFragment - setMaxCostForAllCategory() - все максимальные суммы равны \"0\" ----");
            }
            for (int i = 0; i < 7; i++) {
                setMaxCosts(maxSum[i], i);
            }
        } else {
            cursor.moveToFirst();
            do {
                maxSum[0] = maxSum[0] + cursor.getInt(per + 2);
                maxSum[cursor.getInt(0) - 1] = maxSum[cursor.getInt(0) - 1] + cursor.getInt(per + 2);
                //setMaxCosts(cursor.getInt(2), cursor.getInt(0) - 1);
            } while (cursor.moveToNext());
            for (int i = 0; i < 7; i++) {
                setMaxCosts(maxSum[i], i);
            }
        }
    }

    //установка тек. значений
    public void setCurrentCosts(int currentValue, int idCategory) {
        setTempCurrentCosts(this.currentCost[idCategory], idCategory);
        this.currentCost[idCategory] = currentValue;
        if (tempCurrentCost[idCategory] != currentCost[idCategory]) {
            this.isChange_currentCost[idCategory] = true;
        }
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setMaxCosts() - currentCost[" + idCategory + "] = " + currentValue + ", tempCurrentCost[" + idCategory + "] = " + tempCurrentCost[idCategory] + " ----");
        }
    }

    private void setTempCurrentCosts(int tempValue, int idCategory) {
        this.tempCurrentCost[idCategory] = tempValue;
    }

    //установка значений текущих. трат для AllCategory
    public void setMaxCostCurrentCostForAllCategory(Cursor cursor, int per) {
        int[] currentSum = {0, 0, 0, 0, 0, 0, 0};
        int[] maxSum = {0, 0, 0, 0, 0, 0, 0};
        if (cursor == null){
            for (int i = 0; i < 7; i++) {
                setMaxCosts(maxSum[i], i);
                setCurrentCosts(currentSum[i], i);
            }
        } else if (cursor.getCount() == 0) {
            for (int i = 0; i < 7; i++) {
                setMaxCosts(maxSum[i], i);
                setCurrentCosts(currentSum[i], i);
            }
        } else {//idCat, sum( price )
            cursor.moveToFirst();
            do {
                //устанавливаем имена категорий
                setTextViewTitelCategory(cursor.getString(1), cursor.getInt(0) - 1);
                //устанавливаем макс. траты
                //maxSum[0] = maxSum[0] + cursor.getInt(2);
                maxSum[cursor.getInt(0) - 1] = maxSum[cursor.getInt(0) - 1] + cursor.getInt(2);
                //устанавливаем тек. траты
                if (!cursor.isNull(3)){
                    currentSum[0] = currentSum[0] + cursor.getInt(3);
                    currentSum[cursor.getInt(0) - 1] = currentSum[cursor.getInt(0) - 1] +  cursor.getInt(3);
                } else {
                    currentSum[0] = currentSum[0] + 0;
                    currentSum[cursor.getInt(0) - 1] = currentSum[cursor.getInt(0) - 1]  + 0;
                }
            } while (cursor.moveToNext());
            for (int i = 0; i < 7; i++) {
                setMaxCosts(maxSum[i], i);
                setCurrentCosts(currentSum[i], i);
            }
        }
    }

    //визуальные компоненты
    ProgressBar[] mProgress;
    TextView[] tvPercent;
    TextView[] textViewCurMax;
    TextView[] arrayTextViewTitel;
    //для работы спиннера
    private Spinner spinnerForPeriod;
    ArrayAdapter<String> arrayAdapterForSpiner;

    //установить имя TextViewTitelCategory для idCategory категории
    public void setTextViewTitelCategory(String titel, int idCategory) {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setTextViewTitelCategory() - для " + idCategory + " категории имя:" + titel + " ----");
        }
        this.arrayTextViewTitel[idCategory].setText(titel);
    }

    //установить имя TextViewTitelCategory для всех категорий
    public void setTvTitelForAllCategory(Cursor cursor) {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setTvTitelForAllCategory - cursor.getCount() = " + cursor.getCount() + " ----");
        }
        cursor.moveToFirst();
        do {
            setTextViewTitelCategory(cursor.getString(1), cursor.getInt(0) - 1);
        } while (cursor.moveToNext());
    }

    //установить Max в textViewCurMAX
    public void setTextViewMaxCosts(String max, int idCategory, int time) {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setTextViewMaxCosts - для " + idCategory + " категории в " + period[time] + " макс. траты = " + max + " ----");
        }
        onStartAnimation(idCategory, false, true, maxCost[idCategory], currentCost[idCategory], tempMaxCost[idCategory], tempCurrentCost[idCategory], tvPercent[idCategory], textViewCurMax[idCategory], mProgress[idCategory]);
    }

    public MyCostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment - onCreateView() ----");
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_costs, container, false);
        //получение контекста
        thiscontext = inflater.getContext();

        Resources res = getResources();
        Drawable[] drawables = new Drawable[]{res.getDrawable(R.drawable.circular), res.getDrawable(R.drawable.circular), res.getDrawable(R.drawable.circular), res.getDrawable(R.drawable.circular), res.getDrawable(R.drawable.circular), res.getDrawable(R.drawable.circular), res.getDrawable(R.drawable.circular)};

        mProgress = new ProgressBar[]{view.findViewById(R.id.circularProgressbar_0), view.findViewById(R.id.circularProgressbar_1), view.findViewById(R.id.circularProgressbar_2), view.findViewById(R.id.circularProgressbar_3), view.findViewById(R.id.circularProgressbar_4), view.findViewById(R.id.circularProgressbar_5), view.findViewById(R.id.circularProgressbar_6)};
        for (int i = 0; i < 7; i++) {
            mProgress[i].setProgress(0);
            mProgress[i].setSecondaryProgress(1000);
            mProgress[i].setMax(1000);
            mProgress[i].setProgressDrawable(drawables[i]);
        }

        tvPercent = new TextView[]{view.findViewById(R.id.textView_percent_0), view.findViewById(R.id.textView_percent_1), view.findViewById(R.id.textView_percent_2), view.findViewById(R.id.textView_percent_3), view.findViewById(R.id.textView_percent_4), view.findViewById(R.id.textView_percent_5), view.findViewById(R.id.textView_percent_6)};
        for (int i = 0; i < 7; i++) {
            registerForContextMenu(tvPercent[i]);
        }

        textViewCurMax = new TextView[]{view.findViewById(R.id.textView_current_max_0), view.findViewById(R.id.textView_current_max_1), view.findViewById(R.id.textView_current_max_2), view.findViewById(R.id.textView_current_max_3), view.findViewById(R.id.textView_current_max_4), view.findViewById(R.id.textView_current_max_5), view.findViewById(R.id.textView_current_max_6)};
        arrayTextViewTitel = new TextView[]{view.findViewById(R.id.textView_Titel_0), view.findViewById(R.id.textView_Titel_1), view.findViewById(R.id.textView_Titel_2), view.findViewById(R.id.textView_Titel_3), view.findViewById(R.id.textView_Titel_4), view.findViewById(R.id.textView_Titel_5), view.findViewById(R.id.textView_Titel_6)};

        spinnerForPeriod = view.findViewById(R.id.spiner_period);
        arrayAdapterForSpiner = new ArrayAdapter<String>(thiscontext, R.layout.layout_for_spiner_mycostfrag, period);


        spinnerForPeriod.setAdapter(arrayAdapterForSpiner);
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment - onCreateView() - spinnerForPeriod.setSelection( " + MainActivity.getPer() + ") ----");
        }
        spinnerForPeriod.setSelection(MainActivity.getPer());

        //слушатель выбора позиции в spinner
        spinnerForPeriod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 3){
                    if (logFlag) {
                        Log.d("myLog", "---- (1) ---- MyCostsFragment - onCreateView() - spinnerForPeriod - onItemSelected() - position = " + position + " ----");
                    }
                    if ((MainActivity.getStartDay() | MainActivity.getEndDay()) == 0){
                        String textError = "Вы не задали дни для периода!";
                        Snackbar snackbar = Snackbar.make(view, textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(5000);
                        snackbar.show();
                        period[3] = "c __.__.__ по __.__.__" ;
                        arrayAdapterForSpiner.notifyDataSetChanged();
                        setMaxCostCurrentCostForAllCategory(null, 3);
                        onStartAnimationAll();
                    } else {
                        listenerForMyCostsFragment.changePeriod(position, MainActivity.getStartDay(), MainActivity.getEndDay(), MainActivity.getMoreMonth());
                    }
                } else {
                    listenerForMyCostsFragment.changePeriod(position, 0, 0, 0);

                }
                if (logFlag) {
                    Log.d("myLog", "---- (1) ---- MyCostsFragment - spinnerForPeriod - получаем дату: " + MainActivity.getDay() + "." + MainActivity.getMonth() + "." + MainActivity.getYear() + " ----");
                    Log.d("myLog", "---- (1) ---- MyCostsFragment  -  spinnerForPeriod - выбран период: " + period[position] + " ----");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        listenerForMyCostsFragment.createUI();
        return view;
    }

    public void onStartAnimationAll() {
//запускаем анимацию
        onStartAnimation(0, isChange_currentCost[0], isChange_maxCost[0], maxCost[0], currentCost[0], tempMaxCost[0], tempCurrentCost[0], tvPercent[0], textViewCurMax[0], mProgress[0]);
        onStartAnimation(1, isChange_currentCost[1], isChange_maxCost[1], maxCost[1], currentCost[1], tempMaxCost[1], tempCurrentCost[1], tvPercent[1], textViewCurMax[1], mProgress[1]);
        onStartAnimation(2, isChange_currentCost[2], isChange_maxCost[2], maxCost[2], currentCost[2], tempMaxCost[2], tempCurrentCost[2], tvPercent[2], textViewCurMax[2], mProgress[2]);
        onStartAnimation(3, isChange_currentCost[3], isChange_maxCost[3], maxCost[3], currentCost[3], tempMaxCost[3], tempCurrentCost[3], tvPercent[3], textViewCurMax[3], mProgress[3]);
        onStartAnimation(4, isChange_currentCost[4], isChange_maxCost[4], maxCost[4], currentCost[4], tempMaxCost[4], tempCurrentCost[4], tvPercent[4], textViewCurMax[4], mProgress[4]);
        onStartAnimation(5, isChange_currentCost[5], isChange_maxCost[5], maxCost[5], currentCost[5], tempMaxCost[5], tempCurrentCost[5], tvPercent[5], textViewCurMax[5], mProgress[5]);
        onStartAnimation(6, isChange_currentCost[6], isChange_maxCost[6], maxCost[6], currentCost[6], tempMaxCost[6], tempCurrentCost[6], tvPercent[6], textViewCurMax[6], mProgress[6]);
    }

    //для определения видимости фрагмента MyCostsFragment необходимо переопределить два ниже следующих метода///////////////////////////////////////////////////////
    @Override
    public void onResume() {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  onResume() ----");
        }
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (!getUserVisibleHint()) {
            return;
        }
        if (logFlag) {
            Log.d("myLog", "!!!!!!!!!!!!!!!!!!!!! ---- (1) ---- MyCostsFragment  - в фокусе !!!!!!!!!!!!!!!!!!!!!!!");
        }
        if (firstLoad_FOR_myCostsFragmen){
            listenerForMyCostsFragment.updateUIforMyCostsFragment();
        }
        firstLoad_FOR_myCostsFragmen = true;
        myCostsFragmenINfocus = true;
        redactorFragmentINfocus = false;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  setUserVisibleHint(" + isVisibleToUser + ") ----");
        }
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && isVisibleToUser) {
            onResume();
        }
    }

    @Override
    public void onStart() {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  onStart() ----");
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  onPause() ----");
        }
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onStop() {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  onStop() ----");
        }
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        firstLoad_FOR_myCostsFragmen = false;
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  onDestroyView() ----");
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment  -  onDestroy() ----");
        }
        super.onDestroy();
    }

    public void onStartAnimation(int idCategory, boolean isChenge_currentCosts, boolean isChenge_maxCosts, int costsMax, int currentCosts, int tempCostsMax, int tempCurrentCosts, TextView tvPercent, TextView tvCurrent_Max, ProgressBar myProgress) {
        float percent = 0;
        float tempPercent = 0;

        ObjectAnimator animator;
        ValueAnimator percerntAnimator;
        DecelerateInterpolator interpolator = new DecelerateInterpolator((float) 3);

        DecimalFormat f = new DecimalFormat("##.0");
        if (logFlag) {
            Log.d("myLog", "---- (1) ---- MyCostsFragment - onStartAnimation() for CATEGORY #" + idCategory + ": " + "isChenge_maxCosts = " + isChenge_maxCosts +
                    ", isChenge_currentCosts = " + isChenge_currentCosts +
                    ", maxCost = " + costsMax +
                    ", currentCost = " + currentCosts +
                    ", tempMaxCost = " + tempCostsMax +
                    ", tempCurrentCost = " + tempCurrentCosts);
        }
        if ((costsMax == 0) & (currentCosts == 0)) {
            tvPercent.setText(costsMax + "%");
            tvCurrent_Max.setText(R.string.textView_current_max);
        } else if ((costsMax == 0) & (currentCosts != 0)) {
            percent = 0;
            tempPercent = 0;
            tvCurrent_Max.setText(R.string.textView_not_set_max);
        } else {
            if (isChenge_currentCosts & !isChenge_maxCosts) {
                percent = (float) ((float) currentCosts / (float) costsMax * 1000.0);
                tempPercent = (float) ((float) tempCurrentCosts / (float) costsMax * 1000.0);
            }
            if (isChenge_maxCosts & !isChenge_currentCosts) {
                if (tempCostsMax == 0) {
                    percent = (float) ((float) currentCosts / (float) costsMax * 1000.0);
                    tempPercent = 0;
                } else {
                    percent = (float) ((float) currentCosts / (float) costsMax * 1000.0);
                    tempPercent = (float) ((float) currentCosts / (float) tempCostsMax * 1000.0);
                }
            }
            if (isChenge_maxCosts & isChenge_currentCosts) {
                if (tempCostsMax == 0) {
                    percent = (float) ((float) currentCosts / (float) costsMax * 1000.0);
                    tempPercent = 0;
                } else {
                    percent = (float) ((float) currentCosts / (float) costsMax * 1000.0);
                    tempPercent = (float) ((float) tempCurrentCosts / (float) tempCostsMax * 1000.0);
                }
            }
            if (logFlag) {
                Log.d("myLog", "---- (1) ---- MyCostsFragment - onStartAnimation: procent = " + (percent / 10) + ", tempProcent1 = " + (tempPercent / 10));
            }
            tvCurrent_Max.setText(currentCosts + " / " + costsMax);
        }
//анимация круга
        animator = ObjectAnimator.ofInt(myProgress, "progress", (int) percent);
        animator.setInterpolator(interpolator);
        animator.setDuration(2000);
//анимация процентов
        percerntAnimator = ValueAnimator.ofFloat((tempPercent / 10), (percent / 10));
        percerntAnimator.setDuration(2000);
        percerntAnimator.setInterpolator(interpolator);
//Старт всей анимации
        animator.start();

        if (isChenge_currentCosts || isChenge_maxCosts) {
            if ((costsMax == 0) & (currentCosts != 0)) {
                tvPercent.setText(currentCosts + getString(R.string.rub));
            } else {
                animateValue(percerntAnimator, tvPercent);
            }

        } else {
            tvPercent.setText(f.format(percent / 10) + "%");
        }
    }

    //метод анимации процентов
    private void animateValue(ValueAnimator valueAnimator, final TextView textView) {
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                DecimalFormat f = new DecimalFormat("##.0");
                textView.setText(f.format(valueAnimator.getAnimatedValue()) + "%");
            }
        });
        valueAnimator.start();
    }

    //создание контекстного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        switch (v.getId()) {
            case R.id.textView_percent_0:
                menu.add(0, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(0, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
                break;
            case R.id.textView_percent_1:
                menu.add(1, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(1, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
                break;
            case R.id.textView_percent_2:
                menu.add(2, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(2, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
                break;
            case R.id.textView_percent_3:
                menu.add(3, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(3, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
                break;
            case R.id.textView_percent_4:
                menu.add(4, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(4, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
                break;
            case R.id.textView_percent_5:
                menu.add(5, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(5, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
                break;
            case R.id.textView_percent_6:
                menu.add(6, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY, 0, "Изменить имя категории");
                menu.add(6, CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD, 0, "Установить макс. траты");
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //обработка нажатия контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (myCostsFragmenINfocus){
            switch (item.getItemId()) {
                case CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_NAME_CATEGORY:
                    MyDialogFragment myDialogFragment = MyDialogFragment.newInstance(0, item.getGroupId(), 0);
                    myDialogFragment.show(getFragmentManager(), "dialog");
                    break;
                case CONTEXT_MENU_FOR_MyCostsFragment_CHANGE_MAX_COST_IN_PERIOD:
                    MyDialogFragment myDialogFragment2 = MyDialogFragment.newInstance(1, item.getGroupId(), MainActivity.getPer());
                    myDialogFragment2.show(getFragmentManager(), "dialog");
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }
}