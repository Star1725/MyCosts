package com.hfad.mycosts;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.design.widget.TabLayout;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Objects;
import static com.hfad.mycosts.Database.CATEGORY_COLUMN_NAME;
import static com.hfad.mycosts.Database.DB_TABLE_SUBCATEGORY;
import static com.hfad.mycosts.Database.SUBCATEGORY_COLUMN_NAME;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        DialogFragmentAddNewBuying.onClickListener,
        MyCostsFragment.listenerForMyCostsFragment,
        RedactorFragment.listenerForRedactorFragment
{
    public static boolean logFlag = false;
    private SectionsPagerAdapter sectionsPagerAdapter;
    ViewPager pager;
    static MyCostsFragment myCostsFragment;
    static RedactorFragment redactorFragment;
    static StatisticFragment statisticFragment;

    public static Calendar getCalendar() {
        return calendar;
    }

    private static Calendar calendar;

    public static String[] period = {"За неделю", "За месяц", "За год", "\"мой\" период"};

    public static void setPer(int per) {
        MainActivity.per = per;
    }
    public static int getPer() {
        return per;
    }
    private static int per;

    public static int getStartDay() {
        return startDay;
    }

    public static int getEndDay() {
        return endDay;
    }

    public static int getMoreMonth() {
        return moreMonth;
    }

    private static int startDay;
    private static int endDay;
    private static int moreMonth;


    private static String currentData; public static String getCurrentData() {
        return currentData;
    }
    private static int day; public static int getDay() {
        return day;
    }
    private static int week; public static int getWeek() {
        return week;
    }
    private static int month; public static int getMonth() {
        return month;
    }
    private static int year; public static int getYear() {
        return year;
    }

    private static int DIALOGE_DATE_ID = 1;

    public static Database getDatabase() {
        return database;
    }

    private static Database database;
    private Loader<Cursor> myLoader;

    private static Cursor cursor_For_NAME_CATEGORY;
    //метод для передачи имен категорий в фрагменты: MyCostsFragment, RedactorFragment, DialogFragmentAddNewBuying
    public static Cursor getCursor_For_NAME_CATEGORY() {
        return cursor_For_NAME_CATEGORY;
    }

    //для MyCostsFragment:
    private static Cursor cursor_For_DEFAULT_PERIOD;//лоадер не требуется, так как необходима синхронная загрузка периода по-умолчанию
    //метод для установки периода в спиннере для MyCostsFragment
    public static Cursor getCursor_For_DEFAULT_PERIOD(){
        return cursor_For_DEFAULT_PERIOD;
    }


    //для проверки
    private static final int LOADER_FOR_ALL_BUYING = 4;
    private static final int LOADER_CATEGORYS_FOR_myCostsFragment = 10;
    public static boolean firstLoad_FOR_myCostsFragmen = false;
    public static boolean myCostsFragmenINfocus = false;
    private static final int LOADER_CATEGORYS_FOR_redactorFragment = 11;
    public static boolean redactorFragmentINfocus = false;

    private static final int LOADER_FOR_StatisticFragment_GrafCat = 12;
    public static int[][] getArrayForCategory() {
        return arrayForCategory;
    }
    private static int [][] arrayForCategory = new int[7][12];//массив для отображения 6 категорий на графике по месяцам
    private static int maxOfArrayCat;
    public static int getMaxOfArray1() {
        return maxOfArrayCat;
    }

    private static final int LOADER_FOR_StatisticFragment_GrafSubCat = 13;
    private static int [] arrayForSubCategory = new int[12];//массив для отображения категории и одной её подкатегории на графике по месяцам

    private static int idCatForLoader;


    private static int idSubCatForLoader;


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//РЕФЛИЗАЦИЯ ИНТЕРФЕЙСОВ СЛУШАТЕЛЕЙ ФРАГМЕНТОВ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //методы обработки нажатия кнопок "ОК" и "Отмена" в MyDialogFragment/////////////////////////////////////////////////////////////////////////////////////
    public void okClicked(String dataString, int dataCount,  int hint, int groupId, int time) {
        Log.d("myLog", "---- MainActivity - okClicked() ----");
        switch (hint){
            case 0:
                myCostsFragment.setTextViewTitelCategory(dataString, groupId);
                if (logFlag){
                    Log.d("myLog", "---- MainActivity - okClicked() - установить новое имя \"" + dataString + "\" для " + groupId + " категории ----");
                }
                //записываем новое имя выбранной категории в БД
                database.updateDataForTableName(Database.DB_TABLE_CATEGORY, CATEGORY_COLUMN_NAME, groupId + 1, dataString);
                getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
                break;
            case 1:
                myCostsFragment.setMaxCosts(dataCount, groupId);
                myCostsFragment.setTextViewMaxCosts(dataString, groupId, time);
                switch (time){
                    case 0:
                        database.updateDataForTableName(Database.DB_TABLE_CATEGORY, Database.CATEGORY_COLUMN_MAX_COSTS_WEEK, groupId + 1, Integer.toString(dataCount));
                        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
                        if (logFlag){
                            Log.d("myLog", "---- MainActivity - okClicked() - установить макс. траты = " + dataCount + " для " + groupId + " категории за период = " + period[time] + " ----");
                        }
                        break;
                    case 1:
                        database.updateDataForTableName(Database.DB_TABLE_CATEGORY, Database.CATEGORY_COLUMN_MAX_COSTS_MONTH, groupId + 1, Integer.toString(dataCount));
                        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
                        if (logFlag) {
                            Log.d("myLog", "---- MainActivity - okClicked() - установить макс. траты = " + dataCount + " для " + groupId + " категории за период = " + period[time] + " ----");
                        }
                        break;
                    case 2:
                        database.updateDataForTableName(Database.DB_TABLE_CATEGORY, Database.CATEGORY_COLUMN_MAX_COSTS_YEAR, groupId + 1, Integer.toString(dataCount));
                        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
                        if (logFlag) {
                            Log.d("myLog", "---- MainActivity - okClicked() - установить макс. траты = " + dataCount + " для " + groupId + " категории за период = " + period[time] + " ----");
                        }
                        break;
                    case 3:
                        database.updateDataForTableName(Database.DB_TABLE_CATEGORY, Database.CATEGORY_COLUMN_CURRENT_COSTS_WEEK, groupId + 1, Integer.toString(dataCount));
                        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
                        if (logFlag) {
                            Log.d("myLog", "---- MainActivity - okClicked() - установить макс. траты = " + dataCount + " для " + groupId + " категории за период = " + period[time] + " ----");
                        }
                        break;
                }
                break;
            case 2:
                database.updateDataForTableName(DB_TABLE_SUBCATEGORY, SUBCATEGORY_COLUMN_NAME, groupId, dataString);
                getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_redactorFragment).forceLoad();
                break;
        }

    }
    public void cancelClicked() {
    }
    //реализацтя интерфейса слушателя из DialogFragmentAddNewBuying для обновления макета для RedactorFragment после добавления новой покупки
    @Override
    public void updateLoaderOnMainActivity() {
        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_redactorFragment).forceLoad();
        getSupportLoaderManager().getLoader(LOADER_FOR_StatisticFragment_GrafCat).forceLoad();
    }
    //реализацтя интерфейса слушателя из RedactorFragment для обновления макета для RedactorFragment после фокуксировки на RedactorFragment
    @Override
    public void updateUIforRedactorFragment() {
        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_redactorFragment).forceLoad();
        getSupportLoaderManager().getLoader(LOADER_FOR_StatisticFragment_GrafCat).forceLoad();
    }
    //реализацтя интерфейса слушателя из MyCostsFragment для выбора периода отображения и соответствующей загрузки myCursorLoader

    @Override
    public void changePeriod(int per, int startDay, int endDay, int moreMonth) {
        setPer(per);
        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
        getSupportLoaderManager().getLoader(LOADER_FOR_StatisticFragment_GrafCat).forceLoad();
    }

    @Override
    public void createUI() {
        //создаём LOADER - ы

        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() - создаём LOADER_CATEGORYS_FOR_myCostsFragment ----");
        }
        myLoader = getSupportLoaderManager().initLoader(LOADER_CATEGORYS_FOR_myCostsFragment, null, this);

        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() - создаём LOADER_CATEGORYS_FOR_redactorFragment ----");
        }
        myLoader = getSupportLoaderManager().initLoader(LOADER_CATEGORYS_FOR_redactorFragment, null, this);

        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() - создаём LOADER_FOR_ALL_BUYING ----");
        }
        myLoader = getSupportLoaderManager().initLoader(LOADER_FOR_ALL_BUYING, null, this);

        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() - создаём LOADER_FOR_StatisticFragment ----");
        }
        myLoader = getSupportLoaderManager().initLoader(LOADER_FOR_StatisticFragment_GrafCat, null, this);
    }

    @Override
    public void updateUIforMyCostsFragment() {
        getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment).forceLoad();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
    /////методы для работы лоадера загрузки данных через simpleCursorAdapterForSpinner в mySpiner///////
    // Будет вызван, если до этого не существовал
    // Это значит, что при повороте не будет вызываться
    // так как предыдущий загрузчик с данным ID уже был создан ранее
    // Будет также вызван при рестарте через метод LoaderManager.restartLoader()
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
        switch (id) {
            case LOADER_CATEGORYS_FOR_myCostsFragment:
                myLoader = new myCursorLoader(this, database, id);
                break;
            case LOADER_CATEGORYS_FOR_redactorFragment:
                myLoader = new myCursorLoader(this, database, id);
                break;
            case LOADER_FOR_ALL_BUYING:
                myLoader = new myCursorLoader(this, database, id);
                break;
            case LOADER_FOR_StatisticFragment_GrafCat:
                myLoader = new myCursorLoader(this, database, id);
                break;
            case LOADER_FOR_StatisticFragment_GrafSubCat:
                myLoader = new myCursorLoader(this, database, id);
                break;
        }
        return myLoader;
    }
    // Вызовется, когда загрузчик закончит свою работу. Вызывается в основном потоке
    // Может вызываться несколько раз при изменении данных
    // Также вызывается при поворотах
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        switch (loader.getId()) {
            case LOADER_CATEGORYS_FOR_myCostsFragment:
                if (logFlag) {
                    Log.d("myLog", "---- (0) ---- MainActivity - onLoadFinished() - записали cursor_For_LOADER_CATEGORYS_FOR_myCostsFragment ----");
                }
                //Database.getOnTerminalDataOfCursor(cursor, "for myCostsFragment за " + period[per], 111111);
                myCostsFragment.setMaxCostCurrentCostForAllCategory(cursor, per);
                myCostsFragment.onStartAnimationAll();
                cursor_For_NAME_CATEGORY = cursor;
                break;
            case LOADER_CATEGORYS_FOR_redactorFragment:
                if (logFlag) {
                    Log.d("myLog", "---- (0) ---- MainActivity - onLoadFinished() - записали cursor_For_LOADER_FOR_redactorFragment ----");
                }
                //Database.getOnTerminalDataOfCursor(cursor, "for redactorFragment за " + day + "." + month + "." + year, 222222);
                redactorFragment.setExpandableListViewTree(cursor);
                break;
            case LOADER_FOR_ALL_BUYING:
                if (logFlag) {
                    Log.d("myLog", "---- (0) ---- MainActivity - onLoadFinished() - записали cursor_FOR_ALL_BUYING ----");
                }
                break;
            case LOADER_FOR_StatisticFragment_GrafCat:
                int month;
                int category;
                int sum;
                for (int i = 0; i <= 11; i ++){
                    arrayForCategory[0][i] = 0;
                }
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            month = cursor.getInt(0);
                            category = cursor.getInt(1);
                            sum = cursor.getInt(2);
                            for (int i = 1; i <= 12; i++){
                                if (month == i){
                                    for (int j = 2; j <= 7; j++){
                                        if (category == j){
                                            arrayForCategory[j - 1][i - 1] = sum;
                                            arrayForCategory[0][i - 1] = arrayForCategory[0][i - 1] + sum;
                                        }
                                    }
                                }
                            }
                        } while (cursor.moveToNext());
                    }
                } else {
                    if (logFlag) {
                        Log.d("myLog", " Cursor is null ");
                    }
                }
                if (logFlag) {
                    Log.d("myLog", "---- (0) ---- MainActivity - onLoadFinished() - записали cursor_FOR_StatisticFragment и получили матрицу : ----");
                    Log.d("myLog", "Всего - " + arrayForCategory[0][0] + "  " + arrayForCategory[0][1] + "  " + arrayForCategory[0][2] + "  " + arrayForCategory[0][3] + "  " + arrayForCategory[0][4] + "  " + arrayForCategory[0][5] + "  " + arrayForCategory[0][6] + "  " + arrayForCategory[0][7] + "  " + arrayForCategory[0][8] + "  " + arrayForCategory[0][9] + "  " + arrayForCategory[0][10] + "  " + arrayForCategory[0][11]);
                    Log.d("myLog", "Cat 1 - " + arrayForCategory[1][0] + "  " + arrayForCategory[1][1] + "  " + arrayForCategory[1][2] + "  " + arrayForCategory[1][3] + "  " + arrayForCategory[1][4] + "  " + arrayForCategory[1][5] + "  " + arrayForCategory[1][6] + "  " + arrayForCategory[1][7] + "  " + arrayForCategory[1][8] + "  " + arrayForCategory[1][9] + "  " + arrayForCategory[1][10] + "  " + arrayForCategory[1][11]);
                    Log.d("myLog", "Cat 2 - " + arrayForCategory[2][0] + "  " + arrayForCategory[2][1] + "  " + arrayForCategory[2][2] + "  " + arrayForCategory[2][3] + "  " + arrayForCategory[2][4] + "  " + arrayForCategory[2][5] + "  " + arrayForCategory[2][6] + "  " + arrayForCategory[2][7] + "  " + arrayForCategory[2][8] + "  " + arrayForCategory[2][9] + "  " + arrayForCategory[2][10] + "  " + arrayForCategory[2][11]);
                    Log.d("myLog", "Cat 3 - " + arrayForCategory[3][0] + "  " + arrayForCategory[3][1] + "  " + arrayForCategory[3][2] + "  " + arrayForCategory[3][3] + "  " + arrayForCategory[3][4] + "  " + arrayForCategory[3][5] + "  " + arrayForCategory[3][6] + "  " + arrayForCategory[3][7] + "  " + arrayForCategory[3][8] + "  " + arrayForCategory[3][9] + "  " + arrayForCategory[3][10] + "  " + arrayForCategory[3][11]);
                    Log.d("myLog", "Cat 4 - " + arrayForCategory[4][0] + "  " + arrayForCategory[4][1] + "  " + arrayForCategory[4][2] + "  " + arrayForCategory[4][3] + "  " + arrayForCategory[4][4] + "  " + arrayForCategory[4][5] + "  " + arrayForCategory[4][6] + "  " + arrayForCategory[4][7] + "  " + arrayForCategory[4][8] + "  " + arrayForCategory[4][9] + "  " + arrayForCategory[4][10] + "  " + arrayForCategory[4][11]);
                    Log.d("myLog", "Cat 5 - " + arrayForCategory[5][0] + "  " + arrayForCategory[5][1] + "  " + arrayForCategory[5][2] + "  " + arrayForCategory[5][3] + "  " + arrayForCategory[5][4] + "  " + arrayForCategory[5][5] + "  " + arrayForCategory[5][6] + "  " + arrayForCategory[5][7] + "  " + arrayForCategory[5][8] + "  " + arrayForCategory[5][9] + "  " + arrayForCategory[5][10] + "  " + arrayForCategory[5][11]);
                    Log.d("myLog", "Cat 6 - " + arrayForCategory[6][0] + "  " + arrayForCategory[6][1] + "  " + arrayForCategory[6][2] + "  " + arrayForCategory[6][3] + "  " + arrayForCategory[6][4] + "  " + arrayForCategory[6][5] + "  " + arrayForCategory[6][6] + "  " + arrayForCategory[6][7] + "  " + arrayForCategory[6][8] + "  " + arrayForCategory[6][9] + "  " + arrayForCategory[6][10] + "  " + arrayForCategory[6][11]);
                }
                maxOfArrayCat = arrayForCategory[0][0];
                for (int i = 0; i <= 11; i++){
                    if (arrayForCategory[0][i] >= maxOfArrayCat) maxOfArrayCat = arrayForCategory[0][i];
                }
                break;
            case LOADER_FOR_StatisticFragment_GrafSubCat:

                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    static class myCursorLoader extends CursorLoader {
        Database db;
        private final int LoaderID;
        private Cursor cursor;

        public myCursorLoader(Context context, Database db, int id) {
            super(context);
            this.db = db;
            this.LoaderID = id;
        }

        @Override
        public Cursor loadInBackground() {
            switch (LoaderID){
                case LOADER_FOR_ALL_BUYING:
                    if (logFlag) {
                        Log.d("myLog", "---- (0) ---- MainActivity ---- начинаем считывать cursor для LOADER_FOR_ALL_BUYING за " + period[per] + "(" + day + "." + month + "." + year + ") ----");
                    }
                    cursor = db.checkAndGetData(null, 0, 0, per, day, week, month, year,3);
                    break;
                case LOADER_CATEGORYS_FOR_myCostsFragment:
                    if (logFlag) {
                        Log.d("myLog", "---- (0) ---- MainActivity ---- начинаем считывать cursor для LOADER_FOR_myCostsFragment за период: " + period[per] + " ----");
                    }
                    cursor = db.getAllData_for_myCostsFragment(per, day, week, month, year, startDay, endDay, moreMonth);
                    break;
                case LOADER_CATEGORYS_FOR_redactorFragment:
                    if (logFlag) {
                        Log.d("myLog", "---- (0) ---- MainActivity ---- начинаем считывать cursor для LOADER_FOR_redactorFragment за дату: " + day + "." + month + "." + year + " ----");
                    }
                    cursor = db.getAllData_for_redactorFragment(day, month, year,2000000);
                    break;
                case LOADER_FOR_StatisticFragment_GrafCat:
                    if (logFlag) {
                        Log.d("myLog", "---- (0) ---- MainActivity ---- начинаем считывать cursor для LOADER_FOR_StatisticFragment для per: " + per + " ----");
                    }
                    cursor = db.getAllData_for_StatisticFragment(0, 0);
                case LOADER_FOR_StatisticFragment_GrafSubCat:
                    if (logFlag) {
                        Log.d("myLog", "---- (0) ---- MainActivity ---- начинаем считывать cursor для case LOADER_FOR_StatisticFragment_GrafSubCat: " + per + " ----");
                    }
                    cursor = db.getAllData_for_StatisticFragment(idCatForLoader,idSubCatForLoader);
            }
            return cursor;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() ----");
        }
        //определяем меню
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        //1 - Связывание SectionsPagerAdapter с ViewPager
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(sectionsPagerAdapter);
        //Связывание ViewPager с TabLayout для отображения вкладок
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(pager);

        database = new Database(this);
        database.openConnect("MainActivity");
        cursor_For_DEFAULT_PERIOD = database.getAllData(Database.DB_TABLE_DEFAULT_PERIOD);
        cursor_For_DEFAULT_PERIOD.moveToFirst();                                                                    //   0      1     2       3
        per = cursor_For_DEFAULT_PERIOD.getInt(1);//по умолчанию - месяц (per = 1), в настройках можно установить: неделя, месяц, год, мой период

        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() - из БД загружен период - " + period[per] + "( per = " + per + ") ----");
        }
        //получаем текущую дату: день, номер недели, месяц, год
        calendar = Calendar.getInstance();
        setDate(calendar);
        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onCreate() - текущая дата - " + currentData + " ----");
        }
        startDay = cursor_For_DEFAULT_PERIOD.getInt(2);
        endDay = cursor_For_DEFAULT_PERIOD.getInt(3);
        moreMonth = cursor_For_DEFAULT_PERIOD.getInt(4);
        setMyPeriod(day, startDay, endDay, moreMonth);
    }

    public void setMyPeriod(int currentDay, int startDay, int endDay, int moreMonth){
        if ((startDay | endDay) == 0){
            period[3] = "c __.__.__ по __.__.__" ;
        } else if (currentDay >= startDay){
            period[3] = startDay + "." + (month) + "." + year + " по " +
                    endDay + "." + (month + moreMonth) + "." + year;
        } else {
            period[3] = startDay + "." + (month -1) + "." + year + " по " +
                    endDay + "." + (month - 1 + moreMonth)  + "." + year;
        }
    }

    @Override
    protected void onDestroy() {
        if (logFlag) {
            Log.d("myLog", "---- (0) ---- MainActivity - onDestroy() ----");
        }
        database.closeConnect();
        super.onDestroy();
    }

    public void setDate(Calendar calendar){
        day = calendar.get(Calendar.DAY_OF_MONTH);
        week = calendar.get(Calendar.WEEK_OF_YEAR);
        month = (calendar.get(Calendar.MONTH) + 1);
        year = calendar.get(Calendar.YEAR);
        currentData = getDateMyFormatRUS(day, month, year);
    }

    //метод для получения даты формата "1 января 2019"
    static public String getDateMyFormatRUS (int day, int month, int year){
        String dateMyFormat;
        String rusMonth = "";
        switch (month){
            case 1:
                rusMonth = "января";
                break;
            case 2:
                rusMonth = "февраля";
                break;
            case 3:
                rusMonth = "марта";
                break;
            case 4:
                rusMonth = "апреля";
                break;
            case 5:
                rusMonth = "мая";
                break;
            case 6:
                rusMonth = "июня";
                break;
            case 7:
                rusMonth = "июля";
                break;
            case 8:
                rusMonth = "августа";
                break;
            case 9:
                rusMonth = "сентября";
                break;
            case 10:
                rusMonth = "октября";
                break;
            case 11:
                rusMonth = "ноября";
                break;
            case 12:
                rusMonth = "декабря";
                break;
        }
        dateMyFormat = day + " " + rusMonth + " " + year;
        return dateMyFormat;
    }
//метод создания элементов меню панели приложения///////////////////////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //устанавливаем текущую дату
        MenuItem menuItemShowText = menu.findItem(R.id.menu_item_show_date);
        menuItemShowText.setTitle(currentData);
        return super.onCreateOptionsMenu(menu);
    }
    //метод обработки нажатия на элемент меню панели приложения
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_show_calendar:
                showDialog(DIALOGE_DATE_ID);
                return true;
            case R.id.menu_item_create_order:
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
//метод обновления меню приложения(для смены даты)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_item_show_date).setTitle(currentData);
        return super.onPrepareOptionsMenu(menu);
    }
 ///////////////////////////////////////////////////////////////////////////////////////////////////
//метод создания DatePickerDialog///////////////////////////////////////////////////////////////////
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOGE_DATE_ID){
            return new DatePickerDialog(this, myCallBack, year, month - 1, day);
        }
        return super.onCreateDialog(id);
    }
    //обработчик нажатия на "ОК" в DatePickerDialog
    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(year, month, dayOfMonth);
            setDate(calendar);
            setMyPeriod(day, startDay, endDay, moreMonth);
            if (logFlag) {
                Log.d("myLog", "---- (0) ---- MainActivity - DatePickerDialog.OnDateSetListener - onDateSet() - установили дату: " + currentData + " ----");
            }
            invalidateOptionsMenu();
            myCostsFragment.arrayAdapterForSpiner.notifyDataSetChanged();


            if (myCostsFragmenINfocus) {
                Objects.requireNonNull(getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_myCostsFragment)).forceLoad();
                if (logFlag) {
                    Log.d("myLog", "---- (0) ---- MainActivity - DatePickerDialog.OnDateSetListener - onDateSet() - перезагрузили LOADER_CATEGORYS_FOR_myCostsFragment ----");
                }
                redactorFragment.textView_shopping_list.setText("Список покупок за " + dayOfMonth + "." + (month + 1) + "." + year);
            }
            if (redactorFragmentINfocus) {
                Objects.requireNonNull(getSupportLoaderManager().getLoader(LOADER_CATEGORYS_FOR_redactorFragment)).forceLoad();
                if (logFlag) {
                    Log.d("myLog", "---- (0) ---- MainActivity - DatePickerDialog.OnDateSetListener - onDateSet() - перезагрузили LOADER_CATEGORYS_FOR_redactorFragment ----");
                }
                redactorFragment.textView_shopping_list.setText("Список покупок за " + dayOfMonth + "." + (month + 1) + "." + year);
            }

            if (logFlag) {
                Log.d("myLog", "---- (0) ---- MainActivity - DatePickerDialog.OnDateSetListener - onDateSet() - установили новую дату для фрагментов ----");
            }
            redactorFragment.setCalendar();
            getSupportLoaderManager().getLoader(LOADER_FOR_StatisticFragment_GrafCat).forceLoad();

        }
    };
////////////////////////////////////////////////////////////////////////////////////////////////////
    
    //1 - устанавливаем для  SectionsPagerAdapter кол-во страниц, какие фрагменты за ними закреплены, и названия этих страниц
    private class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return 3;
        }
        @Override
        public Fragment getItem(int position) {
            if (logFlag) {
                Log.d("myLog", "---- (0) ---- MainActivity - class SectionsPagerAdapter - Fragment getItem(" + position + ") ----");
            }
            switch (position) {
                case 0:
                    return myCostsFragment = new MyCostsFragment();
                case 1:
                    return redactorFragment = new RedactorFragment();
                case 2:
                    return statisticFragment = new StatisticFragment();
            }
            return null;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            if (logFlag) {
                Log.d("myLog", "---- (0) ---- MainActivity - class SectionsPagerAdapter - Fragment getPageTitle(" + position + ") ----");
            }
            switch (position) {
                case 0:
                    return getResources().getText(R.string.costs_tab);
                case 1:
                    return getResources().getText(R.string.redactor_tab);
                case 2:
                    return getResources().getText(R.string.statistic_tab);
                }
            return null;
        }
    }
}

