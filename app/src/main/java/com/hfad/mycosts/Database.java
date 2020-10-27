package com.hfad.mycosts;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import static com.hfad.mycosts.MainActivity.logFlag;


public class Database {
    private static final String DB_NAME = "myDB";
    private static final int DB_VERSION = 4;

    public static final String COLUMN_ID = "_id";

    public static final String DB_TABLE_BUYING = "Buyings";
    public static final String BUYING_COLUMN_ID = "_id";
    public static final String BUYING_COLUMN_NAME = "nameBuying";
    public static final String BUYING_COLUMN_ID_CATEGORY = "IdCategory";
    public static final String BUYING_COLUMN_ID_SUBCATEGORY = "IdSubCategory";
    public static final String BUYING_COLUMN_PRICE = "price";
    public static final String BUYING_COLUMN_DAY_OF_MONTH_BUY = "dayOfMonth_buy";
    public static final String BUYING_COLUMN_WEEK_OF_YEAR_BUY = "weekOfYear_buy";
    public static final String BUYING_COLUMN_MONTH_BUY = "month_buy";
    public static final String BUYING_COLUMN_YEAR_BUY = "year_buy";

    public static final String DB_TABLE_CATEGORY = "Category";
    public static final String CATEGORY_COLUMN_ID = "_id";
    public static final String CATEGORY_COLUMN_NAME = "nameCategory";
    private static String[] nameCategory = {"Общие", "Еда", "Транспорт", "Для дома", "Медицина", "Досуг", "Другое"};
    public static final String CATEGORY_COLUMN_MAX_COSTS_WEEK = "maxCosts_week";
    public static final String CATEGORY_COLUMN_MAX_COSTS_MONTH = "maxCosts_month";
    public static final String CATEGORY_COLUMN_MAX_COSTS_YEAR = "maxCosts_year";
    public static final String CATEGORY_COLUMN_CURRENT_COSTS_WEEK = "currentCosts_week";
    public static final String CATEGORY_COLUMN_CURRENT_COSTS_MONTH = "currentCosts_month";
    public static final String CATEGORY_COLUMN_CURRENT_COSTS_YEAR  = "currentCosts_year";
    public static final String CATEGORY_COLUMN_CURRENT_COSTS_DAY  = "currentCosts_day";
    private static int[] Cost = {0, 0, 0, 0, 0, 0, 0};

    public static final String DB_TABLE_SUBCATEGORY = "MySubCtegory";
    public static final String SUBCATEGORY_COLUMN_ID = "_id";
    public static final String SUBCATEGORY_COLUMN_ID_PARRENT_CATEGORY = "IdParrentCategory";
    public static final String SUBCATEGORY_COLUMN_NAME = "nameSubCategory";

    public static String DB_TABLE_MAX_COSTS = "Max_Current_Costs";

    public static final String DB_TABLE_DEFAULT_PERIOD = "Period";
    public static final String DEFAULT_PERIOD_COLUMN_ID = "_id";
    public static final String DEFAULT_PERIOD_COLUMN_TIME = "time";
    //ver. 4
    public static final String DEFAULT_PERIOD_COLUMN_START_DAY = "start_day";
    public static final String DEFAULT_PERIOD_COLUMN_END_DAY = "end_day";
    public static final String DEFAULT_PERIOD_COLUMN_MORE_MONTH = "more_month";

    private static final String CREATE_DB_TABLE_BUYING = "CREATE table " + DB_TABLE_BUYING + " ( " +
            BUYING_COLUMN_ID + " integer primary key autoincrement, " +
            BUYING_COLUMN_NAME + " text, " +
            BUYING_COLUMN_ID_CATEGORY + " integer, " +
            BUYING_COLUMN_ID_SUBCATEGORY + " integer, " +
            BUYING_COLUMN_PRICE + " integer, " +
            BUYING_COLUMN_DAY_OF_MONTH_BUY + " integer, " +
            BUYING_COLUMN_WEEK_OF_YEAR_BUY + " integer, " +
            BUYING_COLUMN_MONTH_BUY + " integer, " +
            BUYING_COLUMN_YEAR_BUY + " integer);";

    private static final String CREATE_DB_TABLE_CATEGORY = "CREATE table " + DB_TABLE_CATEGORY + " ( " +
            CATEGORY_COLUMN_ID + " integer primary key autoincrement, " +
            CATEGORY_COLUMN_NAME + " text," +
            CATEGORY_COLUMN_MAX_COSTS_WEEK + " integer, " +
            CATEGORY_COLUMN_MAX_COSTS_MONTH + " integer, " +
            CATEGORY_COLUMN_MAX_COSTS_YEAR + " integer, " +
            CATEGORY_COLUMN_CURRENT_COSTS_WEEK + " integer, " +
            CATEGORY_COLUMN_CURRENT_COSTS_MONTH + " integer, " +
            CATEGORY_COLUMN_CURRENT_COSTS_YEAR + " integer, " +
            CATEGORY_COLUMN_CURRENT_COSTS_DAY + " integer );";

    private static final String CREATE_DB_TABLE_SUBCATEGORY = "CREATE table " + DB_TABLE_SUBCATEGORY + " ( " +
            SUBCATEGORY_COLUMN_ID + " integer primary key autoincrement, " +
            SUBCATEGORY_COLUMN_ID_PARRENT_CATEGORY + " integer, " +
            SUBCATEGORY_COLUMN_NAME + " text);";

    private static final String CREATE_DB_TABLE_DEFAULT_PERIOD = "CREATE table " + DB_TABLE_DEFAULT_PERIOD + " ( " +
            DEFAULT_PERIOD_COLUMN_ID + " integer primary key autoincrement, " +
            DEFAULT_PERIOD_COLUMN_TIME + " integer, " +
            DEFAULT_PERIOD_COLUMN_START_DAY + " integer, " +
            DEFAULT_PERIOD_COLUMN_END_DAY + " integer, " +
            DEFAULT_PERIOD_COLUMN_MORE_MONTH + " integer);";

    private static String tableName;
    private static String[] columns;          //= new String[]{Database.COLUMN_NAME_CATEGORY};
    private static String selection;          //= Database.COLUMN_ID + " = ?";
    private static String whereClauseForUpdate;          //= Database.COLUMN_ID + " = ?";
    private static String[] selectionArgs;    //= new String[]{Integer.toString(idCategory)};;
    private static String groupBy;            //= null;
    private static String having;             //= null;
    private static String orderBy;            //= null;

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;
    private final Context myContext;

    public Database(Context myContext) {
        this.myContext = myContext;
    }

    //окрыть подключение
    public void openConnect(String fragment) {
        dbHelper = new DBHelper(myContext, DB_NAME, null, DB_VERSION);
        sqLiteDatabase = dbHelper.getWritableDatabase();
        if (logFlag) {
            Log.d("myLog", "Database - " + fragment + " connect to DataBase ver." + sqLiteDatabase.getVersion() + " --------");
        }
    }

    //закрыть подключение
    public void closeConnect() {
        if (logFlag) {
            Log.d("myLog", "--------    Disconnect to DataBase    --------");
        }
        if (dbHelper != null) dbHelper.close();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //получить все даннае уазанной таблицы из БД
    public Cursor getAllData(String tableName) {
        String loader = "всех данных из таблицы " + tableName;
        Cursor cursor = sqLiteDatabase.query(tableName, null, null, null, null, null, null);
        getOnTerminalDataOfCursor(cursor, loader);
        return cursor;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor getAllData_for_myCostsFragment(int per, int day, int week, int month, int year, int startDay, int endDay, int moreMonth) {
        String MAX_CURRENT_COST_FOR_PERIOD = "";
        switch (per) {
            case 0://для недели
                MAX_CURRENT_COST_FOR_PERIOD = CATEGORY_COLUMN_MAX_COSTS_WEEK;
                selection = BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_WEEK_OF_YEAR_BUY + " = ? ";
                selectionArgs = new String[]{Integer.toString(year), Integer.toString(week)};
                break;
            case 1://для месяца
                MAX_CURRENT_COST_FOR_PERIOD = CATEGORY_COLUMN_MAX_COSTS_MONTH;
                selection = BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? ";
                selectionArgs = new String[]{Integer.toString(year), Integer.toString(month)};
                break;
            case 2://для года
                MAX_CURRENT_COST_FOR_PERIOD = CATEGORY_COLUMN_MAX_COSTS_YEAR;
                selection = BUYING_COLUMN_YEAR_BUY + " = ? ";
                selectionArgs = new String[]{Integer.toString(year)};
                break;
            case 3://для пользовательского периода
                MAX_CURRENT_COST_FOR_PERIOD = CATEGORY_COLUMN_CURRENT_COSTS_WEEK;
                if (moreMonth == 0) {
                    if (logFlag) {
                        Log.d("myLog", "-------- DataBase - getAllData_for_myCostsFragment for per = " + per + " and moreMonth = " + moreMonth + " --------");
                    }
                    selection = BUYING_COLUMN_DAY_OF_MONTH_BUY + " >= ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " < ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? ";
                    selectionArgs = new String[]{Integer.toString(startDay), Integer.toString(endDay), Integer.toString(month)};
                } else {
                    if (logFlag) {
                        Log.d("myLog", "-------- DataBase - getAllData_for_myCostsFragment for per = " + per + " and moreMonth = " + moreMonth + " --------");
                    }
                    selection = "(" + BUYING_COLUMN_DAY_OF_MONTH_BUY + " >= ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? ) OR " +
                            "(" + BUYING_COLUMN_DAY_OF_MONTH_BUY + " < ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? )";
                    if (day >= startDay) selectionArgs = new String[]{Integer.toString(startDay), Integer.toString(month), Integer.toString(endDay), Integer.toString(month + moreMonth)};
                    else                 selectionArgs = new String[]{Integer.toString(startDay), Integer.toString(month - 1), Integer.toString(endDay), Integer.toString(month - 1 + moreMonth)};
                }
                break;
        }
        groupBy = DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY;

        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID + ", " + CATEGORY_COLUMN_NAME + ", " + MAX_CURRENT_COST_FOR_PERIOD + ", GROUPED_PRICE.SUM_FOR_PRICE " +
                        " FROM " + DB_TABLE_CATEGORY + " LEFT JOIN " +
                        "(SELECT " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " IDCATEGORY, " + "sum (" + BUYING_COLUMN_PRICE + ") SUM_FOR_PRICE " +
                        " FROM " + DB_TABLE_BUYING +
                        " WHERE " + selection +
                        " GROUP BY " + groupBy + ") GROUPED_PRICE" + " ON " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID + " = " + "GROUPED_PRICE.IDCATEGORY",
                selectionArgs);
        getOnTerminalDataOfCursor(cursor, "myCostsFragment");
        return cursor;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor getAllData_for_redactorFragment(int day, int month, int year, int flag) {
       //для дня
        selection = BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month), Integer.toString(day) };
        groupBy = DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY;
        Cursor cursor = sqLiteDatabase.rawQuery(
                "SELECT " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID + ", " + CATEGORY_COLUMN_NAME + ", GROUPED_PRICE.SUM_FOR_PRICE " +
                        " FROM " + DB_TABLE_CATEGORY + " LEFT JOIN " +
                        "(SELECT " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " IDCATEGORY, " + "sum (" + BUYING_COLUMN_PRICE + ") SUM_FOR_PRICE " +
                        " FROM " + DB_TABLE_BUYING +
                        " WHERE " + selection +
                        " GROUP BY " + groupBy + ") GROUPED_PRICE" + " ON " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID + " = " + "GROUPED_PRICE.IDCATEGORY",
                selectionArgs);
        getOnTerminalDataOfCursor(cursor, "redactorFragment");
        return cursor;
    }
//метод для RedactorFragment, чтобы отобразить список категорий и подкатегорий
    public Cursor getNameForSubCategory (int idCategory, String For){
        String tableForInnerJoin = DB_TABLE_SUBCATEGORY;
        columns = new String[]{DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID, SUBCATEGORY_COLUMN_NAME};
        selection = SUBCATEGORY_COLUMN_ID_PARRENT_CATEGORY + " = ? ";
        selectionArgs = new String[]{Integer.toString(idCategory)};
        Cursor cursor = sqLiteDatabase.query(tableForInnerJoin, columns, selection, selectionArgs, null, null, null);
        getOnTerminalDataOfCursor(cursor, For);
        return cursor;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
public Cursor getAllData_for_StatisticFragment(int idCategory, int idSubCategory){
    String tableForSelect = DB_TABLE_BUYING;
    //по месяцам (per = 1)

    if (idCategory == 0){//для графика по категориям
        columns = new String[]{BUYING_COLUMN_MONTH_BUY, BUYING_COLUMN_ID_CATEGORY, " SUM(" + BUYING_COLUMN_PRICE + ")"};
        groupBy = BUYING_COLUMN_MONTH_BUY + ", " + BUYING_COLUMN_ID_CATEGORY;
    } else {//для графика по подкатегориям
        columns = new String[]{BUYING_COLUMN_MONTH_BUY, BUYING_COLUMN_ID_CATEGORY,  BUYING_COLUMN_ID_SUBCATEGORY, " SUM(" + BUYING_COLUMN_PRICE + ")"};
        selection = BUYING_COLUMN_ID_CATEGORY + " = ? AND " + BUYING_COLUMN_ID_SUBCATEGORY + " = ? ";
        selectionArgs = new String[]{Integer.toString(idCategory), Integer.toString(idSubCategory)};
        groupBy = BUYING_COLUMN_MONTH_BUY;
    }
    Cursor cursor = sqLiteDatabase.query(tableForSelect, columns, null, null, groupBy, null, null);
    getOnTerminalDataOfCursor(cursor, "statisticFragment");
    return cursor;
}
////////////////////////////////////////////////////////////////////////////////////////////////////
    //поиск по cheeckStr либо IDcategory и получаем записи в конкретной колонке из связанных таблиц БД
    public Cursor checkAndGetData(String cheeckStr, int IDcategory, int idBuying, int per, int day, int week, int month, int year, int flag) {
        String tableForInnerJoin = "";
        String loader = "";
        // 0 - в DialogFragmentAddNewBuying для поиска совпадения введенной подкатегории с уже созданными подкатегориями
        // 1 - достать все покупки за выбранный день для указанной категории для с
        // 2 - для StaticFragment
        // 3 - для вывода всех покупок за выбранный период
        // 4 - вывод всех данных о покупке по idBuying
        // 5 - суммировать по подкатегориям и группировать по подкатегориям
        // 6 - суммировать по категориям и группировать по категориям
        // 7 - суммировать все покупки за выбранный день для всех категории
        switch (flag){
            case 0:
                tableForInnerJoin = DB_TABLE_SUBCATEGORY + " inner join " + DB_TABLE_CATEGORY + " on " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID_PARRENT_CATEGORY + " = " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID;
                columns = new String[]{DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID, SUBCATEGORY_COLUMN_NAME, DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID, CATEGORY_COLUMN_NAME};
                selection = SUBCATEGORY_COLUMN_NAME + " LIKE ? ";
                selectionArgs = new String[]{cheeckStr};
                groupBy = null;
                loader = "для DialogFragmentAddNewBuying для поиска совпадения";
                break;
            case 1: // 1 - достать все покупки за выбранный день для указанной категории
                tableForInnerJoin = DB_TABLE_BUYING +
                        " inner join " + DB_TABLE_CATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " = " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID +
                        " inner join " + DB_TABLE_SUBCATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_SUBCATEGORY + " = " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID;
                columns = new String[]{DB_TABLE_BUYING + "." + BUYING_COLUMN_ID, BUYING_COLUMN_NAME, SUBCATEGORY_COLUMN_NAME, BUYING_COLUMN_PRICE};
                selection = BUYING_COLUMN_ID_CATEGORY + " = ? AND " + BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
                selectionArgs = new String[]{Integer.toString(IDcategory), Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                groupBy = null;
                loader = "для RedactorFragment для всех покупок за выбранный день для указанной категории";
                break;
            case 2:  // 2 - для StaticFragment
                tableForInnerJoin = DB_TABLE_BUYING + " inner join " + DB_TABLE_SUBCATEGORY + " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_SUBCATEGORY + " = " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID;
                columns = new String[]{DB_TABLE_BUYING + "." + BUYING_COLUMN_ID, BUYING_COLUMN_NAME, SUBCATEGORY_COLUMN_NAME, BUYING_COLUMN_PRICE};
                selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
                selectionArgs = new String[]{Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                groupBy = null;
                loader = "для StaticFragment";
                break;
            case 3: // 3 - для вывода всех покупок за выбранный период
                tableForInnerJoin = DB_TABLE_BUYING +
                    " inner join " + DB_TABLE_CATEGORY +
                    " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " = " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID +
                    " inner join " + DB_TABLE_SUBCATEGORY +
                    " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_SUBCATEGORY + " = " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID;
                columns = new String[]{DB_TABLE_BUYING + "." + BUYING_COLUMN_ID,
                        BUYING_COLUMN_NAME,
                        BUYING_COLUMN_ID_SUBCATEGORY,
                        SUBCATEGORY_COLUMN_NAME,
                        BUYING_COLUMN_ID_CATEGORY,
                        CATEGORY_COLUMN_NAME,
                        BUYING_COLUMN_PRICE,
                        BUYING_COLUMN_DAY_OF_MONTH_BUY,
                        BUYING_COLUMN_WEEK_OF_YEAR_BUY,
                        BUYING_COLUMN_MONTH_BUY,
                        BUYING_COLUMN_YEAR_BUY};
                //groupBy = BUYING_COLUMN_ID_CATEGORY;
                switch (per){
                    case 3://для дня
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                        break;
                    case 0://для недели
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_WEEK_OF_YEAR_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(week)};
                        break;
                    case 1://для месяца
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month)};
                        break;
                    case 2://для года
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year)};
                        break;
                }
                loader = "для вывода всех покупок(ALL_BUYING) за период = " + per + "( " + day + "." + month + "." + year + ")";
                break;
            case 4: // 4 - вывод всех данных о покупке по idBuying
                tableForInnerJoin = DB_TABLE_BUYING +
                        " inner join " + DB_TABLE_CATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " = " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID +
                        " inner join " + DB_TABLE_SUBCATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_SUBCATEGORY + " = " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID;
                columns = new String[]{DB_TABLE_BUYING + "." + BUYING_COLUMN_ID,
                        BUYING_COLUMN_NAME,
                        BUYING_COLUMN_ID_SUBCATEGORY,
                        SUBCATEGORY_COLUMN_NAME,
                        BUYING_COLUMN_ID_CATEGORY,
                        CATEGORY_COLUMN_NAME,
                        BUYING_COLUMN_PRICE,
                        BUYING_COLUMN_DAY_OF_MONTH_BUY,
                        BUYING_COLUMN_WEEK_OF_YEAR_BUY,
                        BUYING_COLUMN_MONTH_BUY,
                        BUYING_COLUMN_YEAR_BUY};
                        selection = DB_TABLE_BUYING + "." + BUYING_COLUMN_ID + " = ? ";
                        selectionArgs = new String[]{Integer.toString(idBuying)};
                loader = "для вывода покупоки (BUYING) по ID = " + idBuying + "";
                break;
            case 5: // 5 - суммировать по подкатегориям и группировать по подкатегориям
                tableForInnerJoin = DB_TABLE_BUYING +
                        " inner join " + DB_TABLE_CATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " = " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID +
                        " inner join " + DB_TABLE_SUBCATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_SUBCATEGORY + " = " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID;
                columns = new String[]{SUBCATEGORY_COLUMN_ID, SUBCATEGORY_COLUMN_NAME,CATEGORY_COLUMN_ID, CATEGORY_COLUMN_NAME, " sum ( " + BUYING_COLUMN_PRICE + " ) "};
                groupBy = SUBCATEGORY_COLUMN_ID;
                switch (per){
                    case 0://для дня
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                        break;
                    case 1://для недели
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_WEEK_OF_YEAR_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(week)};
                        break;
                    case 2://для месяца
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month)};
                        break;
                    case 3://для года
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year)};
                        break;
                }
                loader = "для суммирования затрат по подкатегориям за период " + per;
                break;
            case 6: // 6 - суммировать по категориям и группировать по категориям
                tableForInnerJoin = DB_TABLE_BUYING +
                        " inner join " + DB_TABLE_CATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_CATEGORY + " = " + DB_TABLE_CATEGORY + "." + CATEGORY_COLUMN_ID +
                        " inner join " + DB_TABLE_SUBCATEGORY +
                        " on " + DB_TABLE_BUYING + "." + BUYING_COLUMN_ID_SUBCATEGORY + " = " + DB_TABLE_SUBCATEGORY + "." + SUBCATEGORY_COLUMN_ID;
                columns = new String[]{BUYING_COLUMN_ID_CATEGORY,
                        " sum ( " + BUYING_COLUMN_PRICE + " ) "};
                groupBy = BUYING_COLUMN_ID_CATEGORY;
                switch (per){
                    case 3://для дня
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                        break;
                    case 0://для недели
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_WEEK_OF_YEAR_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(week)};
                        break;
                    case 1://для месяца
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year), Integer.toString(month)};
                        break;
                    case 2://для года
                        selection =BUYING_COLUMN_YEAR_BUY + " = ? ";
                        selectionArgs = new String[]{Integer.toString(year)};
                        break;
                }
                loader = "для суммирования затрат по КАТЕГОРИЯМ за период " + per;
                break;
            case 7: // 7 - суммировать все покупки за выбранный день для всех категории
                tableForInnerJoin = DB_TABLE_BUYING;
                columns = new String[]{" sum ( " + BUYING_COLUMN_PRICE + " ) "};
                selection = BUYING_COLUMN_YEAR_BUY + " = ? AND " + BUYING_COLUMN_MONTH_BUY + " = ? AND " + BUYING_COLUMN_DAY_OF_MONTH_BUY + " = ? ";
                selectionArgs = new String[]{Integer.toString(year), Integer.toString(month), Integer.toString(day)};
                groupBy = null;
                loader = "для RedactorFragment сумма всех покупок за выбранный день - " + day + "." + month + "." + year + ("per = " + per);
                break;
        }
        Cursor cursor = sqLiteDatabase.query(tableForInnerJoin, columns, selection, selectionArgs, groupBy, null, null);
        getOnTerminalDataOfCursor(cursor, loader);
        return cursor;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //отображение в терминале всех данных из cursor
    public static void getOnTerminalDataOfCursor(Cursor cursor, String loader){
        if (cursor != null) {
            if (logFlag) {
                Log.d("myLog", "Database - for " + loader + " - получено " + cursor.getCount() + " строк ----");
            }
            if (cursor.moveToFirst()) {
                String str1;
                do {
//                         "Database 3 -" +
                    str1 = "         " + loader + " ";

                    for (String str2 : cursor.getColumnNames()) {
                        str1 = str1.concat(str2 + " = " + cheeckCursor(cursor, str2) + "; ");
                    }
                } while (cursor.moveToNext());
            }
        }
    }
    public static String cheeckCursor(Cursor cursor, String columnName){
        if (!cursor.isNull(cursor.getColumnIndex(columnName))) return cursor.getString(cursor.getColumnIndex(columnName));
        else return "0";
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //получить конкретные данные указанной таблицы из БД за
    public Cursor getData(String tableName, String[] columns, String selection, String[] args) {
        Cursor cursor = sqLiteDatabase.query(tableName, columns, selection, args, null, null, null);
        getOnTerminalDataOfCursor(cursor, tableName);
        return cursor;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //получить имена и id категорий для "Список" из БД
    public Cursor getCategoryForSPINNER() {
        tableName = DB_TABLE_CATEGORY;
        selection = CATEGORY_COLUMN_ID + " > ? ";
        selectionArgs = new String[]{Integer.toString(1)};
        Cursor cursor = sqLiteDatabase.query(tableName, null, selection, selectionArgs, null, null, null);
        getOnTerminalDataOfCursor(cursor, tableName);
        return cursor;
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //ОБНОВИТЬ данные в БД
    public void updateDataForTableName(String tableName, String column, int id, String newStr) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column, newStr);

        int updCount = sqLiteDatabase.update(tableName, contentValues, tableName + "." + COLUMN_ID + " = ? ", new String[]{Integer.toString(id)});
        if (logFlag) {
            Log.d("myLog", "Database - updates rows count = " + updCount + "where id = " + id);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //ОБНОВИТЬ данные периода по умолчанию в БД
    public void updateDefaultPeriod(int per, int startDay, int endDday, int moreMonth) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DEFAULT_PERIOD_COLUMN_TIME, per);
        if (per != 3){
            int updCount = sqLiteDatabase.update(DB_TABLE_DEFAULT_PERIOD, contentValues, DB_TABLE_DEFAULT_PERIOD + "." + COLUMN_ID + " = ? ", new String[]{Integer.toString(1)});
            contentValues.clear();
            if (logFlag) {
                Log.d("myLog", "Database - updates row for SettingActivity where id = " + updCount);
            }
        } else {
            contentValues.put(DEFAULT_PERIOD_COLUMN_TIME, per);
            contentValues.put(DEFAULT_PERIOD_COLUMN_START_DAY, startDay);
            contentValues.put(DEFAULT_PERIOD_COLUMN_END_DAY, endDday);
            contentValues.put(DEFAULT_PERIOD_COLUMN_MORE_MONTH, moreMonth);
            int updCount = sqLiteDatabase.update(DB_TABLE_DEFAULT_PERIOD, contentValues, DB_TABLE_DEFAULT_PERIOD + "." + COLUMN_ID + " = ? ", new String[]{Integer.toString(1)});
            if (logFlag) {
                Log.d("myLog", "Database - updates row for SettingActivity where id = " + updCount);
            }
        }

    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //добавить данные в БД
    public void addDataforSubCategory(String tableName, String column1, String column2, int newStr1, String newStr2) {
        ContentValues contentValues = new ContentValues();
        if (!column1.isEmpty()) contentValues.put(column1, Integer.toString(newStr1));
        if (!column2.isEmpty()) contentValues.put(column2, newStr2);
        sqLiteDatabase.insert(tableName, null, contentValues);
        if (logFlag) {
            Log.d("myLog", "Database - добавлена новая подкатегория = " + newStr2 + " в категорию № " + newStr1);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    //добавить данные в таблицу DB_TABLE_BUYING БД
    public void addDataforBuyings(
            String tableName,
            String column1, //COLUMN_NAME_BUYING
            String column2, //COLUMN_ID_CATEGORY
            String column3, //COLUMN_ID_SUBCATEGORY
            String column4, //COLUMN_PRICE
            String column5, //COLUMN_DAY_OF_MONTH_BUY
            String column6, //COLUMN_WEEK_OF_YEAR_BUY
            String column7, //COLUMN_MONTH_BUY
            String column8, //COLUMN_YEAR_BUY
            String name ,
            int idCat,
            int idSubCat,
            int price,
            int day,
            int week,
            int month,
            int year) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column1, name);
        contentValues.put(column2, Integer.toString(idCat));
        contentValues.put(column3, Integer.toString(idSubCat));
        contentValues.put(column4, Integer.toString(price));
        contentValues.put(column5, Integer.toString(day));
        contentValues.put(column6, Integer.toString(week));
        contentValues.put(column7, Integer.toString(month));
        contentValues.put(column8, Integer.toString(year));
        sqLiteDatabase.insert(tableName, null, contentValues);
        if (logFlag) {
            Log.d("myLog", "Database - добавлена новая покупка = " + name);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////
    //обновить данные в таблицу DB_TABLE_BUYING БД
    public void updateDataforBuyings(
            String tableName,
            String column, //BUYING_COLUMN_ID
            String column1, //COLUMN_NAME_BUYING
            String column2, //COLUMN_ID_CATEGORY
            String column3, //COLUMN_ID_SUBCATEGORY
            String column4, //COLUMN_PRICE
            String column5, //COLUMN_DAY_OF_MONTH_BUY
            String column6, //COLUMN_WEEK_OF_YEAR_BUY
            String column7, //COLUMN_MONTH_BUY
            String column8, //COLUMN_YEAR_BUY
            String name ,
            int idCat,
            int idSubCat,
            int price,
            int day,
            int week,
            int month,
            int year,
            int idBuying) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column1, name);
        contentValues.put(column2, Integer.toString(idCat));
        contentValues.put(column3, Integer.toString(idSubCat));
        contentValues.put(column4, Integer.toString(price));
        contentValues.put(column5, Integer.toString(day));
        contentValues.put(column6, Integer.toString(week));
        contentValues.put(column7, Integer.toString(month));
        contentValues.put(column8, Integer.toString(year));
        int updCount = sqLiteDatabase.update(tableName, contentValues, tableName + "." + column + " = ? ", new String[]{String.valueOf(idBuying)});

        if (logFlag) {
            Log.d("myLog", "Database - обновлена " + updCount + " покупка name = " + name + " и с idBuying = " + idBuying);
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
    public void delBuying(Integer idItem) {
        int delCount = sqLiteDatabase.delete(DB_TABLE_BUYING, BUYING_COLUMN_ID + " = ?", new String[]{Integer.toString(idItem)});
        if (logFlag) {
            Log.d("myLog", "deleted rows count = " + delCount);
        }
    }

    private class DBHelper extends SQLiteOpenHelper {
        public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DB_NAME, factory, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            updateMyDatabase(db, 0, DB_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            updateMyDatabase(db, oldVersion, newVersion);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion == 2){
                updateMyDatabase(db, oldVersion, newVersion);
            }
        }

        private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion){

            if (oldVersion < 1){//для метода onCreate()
                ContentValues contentValues = new ContentValues();
                Cursor cursor;
                //создание DB_TABLE_BUYING
                db.execSQL(CREATE_DB_TABLE_BUYING);

                //создание DB_TABLE_CATEGORY
                db.execSQL(CREATE_DB_TABLE_CATEGORY);
                //заполнение DB_TABLE_CATEGORY
                for (int i = 0; i < 7; i++ ){
                    contentValues.put(CATEGORY_COLUMN_NAME, nameCategory[i]);
                    contentValues.put(CATEGORY_COLUMN_MAX_COSTS_WEEK, Cost[i]);
                    contentValues.put(CATEGORY_COLUMN_MAX_COSTS_MONTH, Cost[i]);
                    contentValues.put(CATEGORY_COLUMN_MAX_COSTS_YEAR, Cost[i]);
                    contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_WEEK, Cost[i]);
                    contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_MONTH, Cost[i]);
                    contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_YEAR, Cost[i]);
                    contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_DAY, Cost[i]);
                    db.insert(DB_TABLE_CATEGORY, null, contentValues);
                }
                contentValues.clear();
                cursor =db.query(DB_TABLE_CATEGORY, null, null, null, null, null, null);
                getOnTerminalDataOfCursor(cursor, DB_TABLE_CATEGORY);

                //создание DB_TABLE_SUBCATEGORY
                db.execSQL(CREATE_DB_TABLE_SUBCATEGORY);

                //создание DB_TABLE_DEFAULT_PERIOD
                db.execSQL(CREATE_DB_TABLE_DEFAULT_PERIOD);
                //заполнение DB_TABLE_DEFAULT_PERIOD
                contentValues.put(DEFAULT_PERIOD_COLUMN_TIME, 1);// - месяц
                db.insert(DB_TABLE_DEFAULT_PERIOD, null, contentValues);
                contentValues.clear();
                if (logFlag) {
                    Log.d("myLog", "---- Create tables for Database ver. " + newVersion + " ----");
                }
            }
            if (oldVersion > 0 & oldVersion < 3){//для вер. 3
                ContentValues contentValues = new ContentValues();
                Cursor cursor;
                cursor = db.query(DB_TABLE_MAX_COSTS, null, null, null, null, null, null);
                //добавим новые столбцы
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_MAX_COSTS_WEEK + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_MAX_COSTS_MONTH  + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_MAX_COSTS_YEAR  + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_CURRENT_COSTS_WEEK  + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_CURRENT_COSTS_MONTH  + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_CURRENT_COSTS_YEAR  + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_CATEGORY + " ADD COLUMN " + CATEGORY_COLUMN_CURRENT_COSTS_DAY  + " INTEGER");
//                заполняем новые столбцы
                if (cursor.getCount() != 0){
                    cursor.moveToFirst();
                    do {
                        contentValues.put(CATEGORY_COLUMN_MAX_COSTS_WEEK, cursor.getInt(1));
                        contentValues.put(CATEGORY_COLUMN_MAX_COSTS_MONTH, cursor.getInt(3));
                        contentValues.put(CATEGORY_COLUMN_MAX_COSTS_YEAR, cursor.getInt(2));
                        contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_WEEK, cursor.getInt(5));
                        contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_MONTH, cursor.getInt(6));
                        contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_YEAR, cursor.getInt(7));
                        contentValues.put(CATEGORY_COLUMN_CURRENT_COSTS_DAY, cursor.getInt(8));
                        db.update(DB_TABLE_CATEGORY, contentValues, CATEGORY_COLUMN_ID + " = ? ", new String[]{Integer.toString(cursor.getInt(0))});
                        contentValues.clear();
                    } while (cursor.moveToNext());
                }

                cursor =db.query(DB_TABLE_CATEGORY, null, null, null, null, null, null);
                getOnTerminalDataOfCursor(cursor, DB_TABLE_CATEGORY);
                //удаляем таблицу DB_TABLE_MAX_COSTS
                db.execSQL("DROP TABLE " + DB_TABLE_MAX_COSTS);
                if (logFlag) {
                    Log.d("myLog", "---- Database - onUpgrade() - table " + DB_TABLE_MAX_COSTS + " - delete ----");
                    Log.d("myLog", "---- Database - onUpgrade() - Update Database from " + oldVersion + " to " +  newVersion + " ----");
                }
            }
            if (oldVersion > 2 & oldVersion < 4) {//для вер. 4
                //добавим новые столбцы
                db.execSQL("ALTER TABLE " + DB_TABLE_DEFAULT_PERIOD + " ADD COLUMN " + DEFAULT_PERIOD_COLUMN_START_DAY + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_DEFAULT_PERIOD + " ADD COLUMN " + DEFAULT_PERIOD_COLUMN_END_DAY  + " INTEGER");
                db.execSQL("ALTER TABLE " + DB_TABLE_DEFAULT_PERIOD + " ADD COLUMN " + DEFAULT_PERIOD_COLUMN_MORE_MONTH  + " INTEGER");
                if (logFlag) {
                    Log.d("myLog", "---- Database - onUpgrade() - in table " + DB_TABLE_DEFAULT_PERIOD + " add columns ----");
                    Log.d("myLog", "---- Database - onUpgrade() - Update Database from " + oldVersion + " to " +  newVersion + " ----");
                }
            }
        }
    }
}