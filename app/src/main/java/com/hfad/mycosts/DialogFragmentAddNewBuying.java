package com.hfad.mycosts;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Spinner;
import android.widget.TextView;

import static com.hfad.mycosts.MainActivity.logFlag;


/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentAddNewBuying extends DialogFragment implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {

    private AutoCompleteTextView myAutoCompleteTextView;

    //программные комп. для mySpinner
    SimpleCursorAdapter simpleCursorAdapterForSpinner;
    static final int LOADER_FOR_SPINNER = 0;
    //программные комп. для mAutoCompleteText
    SimpleCursorAdapter simpleCursorAdapterForAutoCompleteText;

    private String nameCategory;
    private String nameCategory2;
    private static String newAddSubCategory;
    private Database database;
    private static int idCategory;
    private static int idSubCategory;
    private Loader<Cursor> myLoader;

    private Context thiscontext;

    EditText userInputName;
    EditText userInputPrice;
    EditText userInputCount;
    private static int idBuying;
    private String nameBuying;
    private String nameSubCategoryBuying;
    private String nameCategoryBuying;
    private int idCategoryBuying;
    private int dayBuy;
    private int weekBuy;
    private int priceBuy;
    private int monthBuy;
    private int yearBuy;

    private View dialogView;
    private Spinner mySpinner;

    public interface onClickListener{
        public void updateLoaderOnMainActivity();
    }
    onClickListener clickListener;

    public DialogFragmentAddNewBuying() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            clickListener = (onClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    public static DialogFragmentAddNewBuying newInstance(int idBuying) {
        DialogFragmentAddNewBuying frag = new DialogFragmentAddNewBuying();
        Bundle args = new Bundle();
        args.putInt("idBuying", idBuying);
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - newInstance() - передали в диалог idBuying = " + idBuying + "----");
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateView() ----");
        }
        //получение контекста
        thiscontext = inflater.getContext();
        database = new Database(thiscontext);
        database.openConnect("DialogFragmentAddNewBuying");
        //получаем аргументы из Bundle savedInstanceState
        try {
            idBuying = getArguments().getInt("idBuying");
            if (logFlag) {
                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateView() - получаем аргументы из Bundle savedInstanceState - idBuying = " + idBuying + "----");
            }
            Cursor cursor;
            cursor = database.checkAndGetData(null, 0, idBuying, 0, 0, 0, 0, 0, 4);
            cursor.moveToFirst();
            nameBuying = cursor.getString(1);
            nameSubCategoryBuying = cursor.getString(3);
            idCategoryBuying = cursor.getInt(4);
            nameCategoryBuying = cursor.getString(5);
            priceBuy = cursor.getInt(6);
            dayBuy = cursor.getInt(7);
            weekBuy = cursor.getInt(8);
            monthBuy = cursor.getInt(9);
            yearBuy = cursor.getInt(10);
            if (logFlag) {
                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateView() - nameBuying = " + nameBuying +
                        ", nameSubCategoryBuying = " + nameSubCategoryBuying +
                        ", idCategoryBuying = " + idCategoryBuying +
                        ", nameCategoryBuying = " + nameCategoryBuying +
                        ", priceBuy = " + priceBuy +
                        ", dayBuy = " + dayBuy +
                        ", monthBuy = " + monthBuy +
                        ", yearBuy = " + yearBuy + " ----");
            }
        } catch (Exception e){
            e.printStackTrace();
            idBuying = -1;
            if (logFlag) {
                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateView() - idBuying = null ----");
            }
        }

        dialogView = inflater.inflate(R.layout.fragment_dialog_fragment_add_new_buying, null);
        //определяем текстовые поля заполнения
        userInputName = (EditText) dialogView.findViewById(R.id.editText_name);
        userInputPrice = (EditText) dialogView.findViewById(R.id.editText_price);
        userInputCount = (EditText) dialogView.findViewById(R.id.editText_count);
        TextView dateBuying = (TextView) dialogView.findViewById(R.id.textView_data);
        //находим и установливаем слушатель для кнопок
        dialogView.findViewById(R.id.buttonOk).setOnClickListener(this);
        dialogView.findViewById(R.id.buttonCancel).setOnClickListener(this);
//SPINNER!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        //графические компоненты
        mySpinner = (Spinner) dialogView.findViewById(R.id.spiner_categoty);
        //формируем массивы сопоставления
        String[] fromSpiner ={Database.CATEGORY_COLUMN_NAME};
        int[] toSpiner ={R.id.textView_for_name};
        //создаём adapter для Spinner и настраиваем список
        simpleCursorAdapterForSpinner = new SimpleCursorAdapter(thiscontext, R.layout.layout_for_spiner_mycostfrag, null, fromSpiner, toSpiner, 0);
        mySpinner.setAdapter(simpleCursorAdapterForSpinner);
        // Инициализируем загрузчик с идентификатором для Спиннера
        // Если загрузчик не существует, то он будет создан,
        // иначе он будет перезапущен.
        myLoader = getLoaderManager().initLoader(LOADER_FOR_SPINNER, null, this);
        //слушатель выбора позиции в spinner
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - mySpinner.setOnItemSelectedListener - id = " + id + " ----");
                }
                if (idBuying != -1){
                    if (logFlag) {
                        Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - mySpinner.setOnItemSelectedListener - idCategoryBuying = " + id + " ----");
                    }
                }
                idCategory = (int) id;
                MainActivity.getCursor_For_NAME_CATEGORY().moveToPosition(idCategory - 1);
                nameCategory = MainActivity.getCursor_For_NAME_CATEGORY().getString(1);
                hideKeyboardFrom(getActivity(), dialogView);
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - mySpinner - onItemSelected() - Вы выбрали " + "\"" + nameCategory + "\" и \"" + nameCategory2 + "\"");
                }
                ////////////////////////////////////////////////////////////////////////////////////
                Snackbar snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), "Вы выбрали " + "\"" + nameCategory + "\"", Snackbar.LENGTH_LONG);
                snackbar.show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//Автозаполнение!!!///////////////////////////////////////////////////////////////////////////////////////////////////
        myAutoCompleteTextView = (AutoCompleteTextView) dialogView.findViewById(R.id.autoCompleteTextView_subCategory);
        //формируем массивы сопоставления и присваиваем адаптер
        String[] fromAutoComplete ={Database.SUBCATEGORY_COLUMN_NAME};
        int[] toAutoComplete ={android.R.id.text1};
        simpleCursorAdapterForAutoCompleteText = new SimpleCursorAdapter(thiscontext,android.R.layout.simple_dropdown_item_1line,null, fromAutoComplete, toAutoComplete, 0);
        myAutoCompleteTextView.setAdapter(simpleCursorAdapterForAutoCompleteText);
        //установки для динамического побуквенного чтения из БД
        simpleCursorAdapterForAutoCompleteText.setFilterQueryProvider(new FilterQueryProvider() {
            public Cursor runQuery(CharSequence str) {
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - simpleCursorAdapterForAutoCompleteText(1) - передаем в 2 str:" + str + "----");
                }
                return getCursor(str);
            } });
        simpleCursorAdapterForAutoCompleteText.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            public CharSequence convertToString(Cursor cur) {
                if (logFlag) {
                    Log.d("myLog", "---- 3 ----");
                }
                int index = cur.getColumnIndex(Database.SUBCATEGORY_COLUMN_NAME);
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - simpleCursorAdapterForAutoCompleteText(3) - из полученного курсора достаём index для COLUMN_NAME_SUBCATEGORY: " + index + "----");
                }
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - simpleCursorAdapterForAutoCompleteText(3) - по полученному index возвращаем имя SubCategory: " + cur.getString(index) + "----");
                }
                hideKeyboardFrom(getActivity(), dialogView);
                return cur.getString(index);
            }});

        if (idBuying == -1){
            getDialog().setTitle("Ваша новая покупка");
            if (logFlag) {
                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateView() - новая покупка ----");
            }
            dateBuying.setText(MainActivity.getCurrentData());
            mySpinner.setSelection(0);
        } else {
            getDialog().setTitle("Обновление данных о покупке");
            if (logFlag) {
                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateView() - Обновление данных о старой покупке ----");
            }
            dateBuying.setText(MainActivity.getDateMyFormatRUS(dayBuy, monthBuy, yearBuy));
            userInputName.setText(nameBuying);
            userInputPrice.setText(priceBuy + "");
            myAutoCompleteTextView.setText(nameSubCategoryBuying);
        }

        return dialogView;
    }
    //метод для побуквенного чтения для AutoCompleteText
    public Cursor getCursor(CharSequence str) {
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - simpleCursorAdapterForAutoCompleteText(2) - приняли от 1 str:" + str + "----");
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - simpleCursorAdapterForAutoCompleteText(2) - Отправляем запрос с данными - idCategory:" + idCategory + ", str:" + str + " ----");
        }
        Cursor cursor = database.getData(Database.DB_TABLE_SUBCATEGORY,
                new String[]{Database.COLUMN_ID,Database.SUBCATEGORY_COLUMN_NAME},
                Database.SUBCATEGORY_COLUMN_ID_PARRENT_CATEGORY + " = ? AND " + Database.SUBCATEGORY_COLUMN_NAME + " LIKE ? ",
                new String[]{Integer.toString(idCategory), "%" + str + "%"});
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - simpleCursorAdapterForAutoCompleteText(2) - передаём cursor в 3 ----");
        }
        return cursor;
    }

//Метод onClick(View v) срабатывает, когда пользователь нажимает на одну из двух кнопок
    @Override
    public void onClick(View v) {
        Snackbar snackbar;
        CharSequence textError;
        boolean cheeckData = true;
        int cheeckName = 0;
        int cheeckPrice = 0;
        int cheeckSubCategory = 0;
        int cheeckAll = 0;
        switch (v.getId()){
            case R.id.buttonOk:
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying -  onClick() - buttonOk --------");
                }
                //общие комп.
                String nameBuying = null;
                int priceBuying = 0;
                int countBuying = 1;
//проверка вводимых данных о покупке
                nameBuying = (userInputName.getText()).toString().trim();
                newAddSubCategory = (myAutoCompleteTextView.getText()).toString().trim();
                try {
                    priceBuying = Integer.parseInt((userInputPrice.getText()).toString().trim());
                } catch (NumberFormatException e) {
                    cheeckPrice = 5;
                }
                try {
                    countBuying = Integer.parseInt((userInputCount.getText()).toString().trim());
                } catch (NumberFormatException e) {
                    countBuying = 1;
                }
                if (nameBuying.isEmpty() ){
                    cheeckName = 1;
                }
                if (newAddSubCategory.isEmpty()){
                    cheeckSubCategory = 3;
                }              //1           5                3
                cheeckAll = cheeckName + cheeckPrice + cheeckSubCategory;

                switch (cheeckAll){
                    case 1:
                        textError = "Вы не ввели наименование покупки!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(5000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                    case 5:
                        textError = "Введена некорректная цена!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(5000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                    case 3:
                        textError = "Вы не выбрали подкатегорию!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                    case 6: textError = "Вы не ввели наименование и цену!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                    case 4: textError = "Вы не ввели наименование и подкатегорию!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                    case 8: textError = "Вы не ввели цену и подкатегорию!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                    case 9: textError = "Вы ввели не все данные о покупке!!!";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(3000);
                        snackbar.show();
                        cheeckData = false;
                        break;
                }
                hideKeyboardFrom(getActivity(), dialogView);//скрыть клавиатуру
                if (cheeckData){
                    hideKeyboardFrom(getActivity(), dialogView);//скрыть клавиатуру
                    Cursor cursor = database.checkAndGetData(newAddSubCategory,0, 0, 0, 0, 0, 0, 0, 0);

                    cursor.moveToFirst();
                    if ((cursor.getCount() == 0)) {
                        //Snackbar:
                        textError = "Вы хотите создать новую подкатегорию в категории " + "\"" + nameCategory + "\"" + "?";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(5000);
                        snackbar.setAction("Да!", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.addDataforSubCategory(Database.DB_TABLE_SUBCATEGORY, Database.SUBCATEGORY_COLUMN_ID_PARRENT_CATEGORY, Database.SUBCATEGORY_COLUMN_NAME, idCategory, newAddSubCategory);
                                CharSequence text = "Подкатегория создана. Нажмите " + "\"OK\"";
                                Snackbar snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), text, Snackbar.LENGTH_LONG);
                                snackbar.setDuration(2500);
                                snackbar.show();
                            }
                        });
                        snackbar.show();
                    } else if (nameCategory.equals(cursor.getString(3))){
                        idCategory = cursor.getInt(2);
                        idSubCategory = cursor.getInt(0);
                        if (idBuying == -1){
                            if (logFlag) {
                                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onClick - cоздаём запись о новой покупке: ----");
                                Log.d("myLog", "                nameCategory - " + nameCategory + ", ID = " + idCategory + " ----");
                                Log.d("myLog", "                nameBuying - " + nameBuying + " ----");
                                Log.d("myLog", "                priceBuying - " + priceBuying + " ----");
                                Log.d("myLog", "                countBuying - " + countBuying + " ----");
                                Log.d("myLog", "                newAddSubCategory - " + newAddSubCategory + ", ID = " + idSubCategory + " ----");
                                Log.d("myLog", "                dayBuy - " + MainActivity.getDay() + " ----");
                                Log.d("myLog", "                weekBuy - " + MainActivity.getWeek() + " ----");
                                Log.d("myLog", "                monthBuy - " + MainActivity.getMonth() + " ----");
                                Log.d("myLog", "                yearBuy - " + MainActivity.getYear() + " ----");
                            }
                            if (countBuying != 1) priceBuying = countBuying * priceBuying;
                            database.addDataforBuyings(
                                    Database.DB_TABLE_BUYING,
                                    Database.BUYING_COLUMN_NAME,
                                    Database.BUYING_COLUMN_ID_CATEGORY,
                                    Database.BUYING_COLUMN_ID_SUBCATEGORY,
                                    Database.BUYING_COLUMN_PRICE,
                                    Database.BUYING_COLUMN_DAY_OF_MONTH_BUY,
                                    Database.BUYING_COLUMN_WEEK_OF_YEAR_BUY,
                                    Database.BUYING_COLUMN_MONTH_BUY,
                                    Database.BUYING_COLUMN_YEAR_BUY,
                                    nameBuying,
                                    idCategory,
                                    idSubCategory,
                                    priceBuying,
                                    MainActivity.getDay(),
                                    MainActivity.getWeek(),
                                    MainActivity.getMonth(),
                                    MainActivity.getYear());
                            textError = "Вы добавили новую покупку в подкатегорию " + "\"" + newAddSubCategory + "\"";
                            snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                            snackbar.setDuration(5000);
                            snackbar.show();
                            clickListener.updateLoaderOnMainActivity();
                            dismiss();
                        } else {
                            if (logFlag) {
                                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onClick - обновляем запись о покупке с idBuying = " + idBuying + " ----");
                                Log.d("myLog", "                nameCategory - " + nameCategory + ", ID = " + idCategory + " ----");
                                Log.d("myLog", "                nameBuying - " + nameBuying + " ----");
                                Log.d("myLog", "                priceBuying - " + priceBuying + " ----");
                                Log.d("myLog", "                countBuying - " + countBuying + " ----");
                                Log.d("myLog", "                newAddSubCategory - " + newAddSubCategory + ", ID = " + idSubCategory + " ----");
                                Log.d("myLog", "                dayBuy - " + dayBuy + " ----");
                                Log.d("myLog", "                weekBuy - " + weekBuy + " ----");
                                Log.d("myLog", "                monthBuy - " + monthBuy + " ----");
                                Log.d("myLog", "                yearBuy - " + yearBuy + " ----");
                            }
                            if (countBuying != 1) priceBuying = countBuying * priceBuying;
                            database.updateDataforBuyings(
                                    Database.DB_TABLE_BUYING,
                                    Database.BUYING_COLUMN_ID,
                                    Database.BUYING_COLUMN_NAME,
                                    Database.BUYING_COLUMN_ID_CATEGORY,
                                    Database.BUYING_COLUMN_ID_SUBCATEGORY,
                                    Database.BUYING_COLUMN_PRICE,
                                    Database.BUYING_COLUMN_DAY_OF_MONTH_BUY,
                                    Database.BUYING_COLUMN_WEEK_OF_YEAR_BUY,
                                    Database.BUYING_COLUMN_MONTH_BUY,
                                    Database.BUYING_COLUMN_YEAR_BUY,
                                    nameBuying,
                                    idCategory,
                                    idSubCategory,
                                    priceBuying,
                                    dayBuy,
                                    weekBuy,
                                    monthBuy,
                                    yearBuy,
                                    idBuying);
                            textError = "Вы обновили покупку в подкатегории " + "\"" + newAddSubCategory + "\"";
                            snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                            snackbar.setDuration(5000);
                            snackbar.show();
                            clickListener.updateLoaderOnMainActivity();
                            dismiss();
                        }

                    } else{
                        cursor.moveToFirst();
                        nameCategory = cursor.getString(3);
                        cursor.close();
                        textError = "Данная подкатегория уже существует в категории " + "\"" + nameCategory + "\"";
                        snackbar = Snackbar.make(dialogView.findViewById(R.id.dialog_linearlayout), textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(5000);
                        snackbar.show();
                    }
                }
                break;
            case R.id.buttonCancel:
                dismiss();
        }
    }
//Метод onDismiss срабатывает, когда диалог закрывается.
    @Override
    public void onDismiss(DialogInterface dialog) {
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onDismiss() ----");
        }
        super.onDismiss(dialog);
        database.closeConnect();
    }
//Метод onCancel срабатывает, когда диалог отменяют кнопкой Назад.

    @Override
    public void onCancel(DialogInterface dialog) {
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCancel() ----");
        }

        super.onCancel(dialog);
    }

    @Override
    public void onResume() {
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onResume() ----");
        }
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        if (logFlag) {
            Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onPause() ----");
        }
        super.onPause();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /////методы для работы лоадера загрузки данных через simpleCursorAdapterForSpinner в mySpiner///////
/////методы для работы лоадера загрузки данных через simpleCursorAdapterForSpinner в mySpiner///////
// Будет вызван, если до этого не существовал
// Это значит, что при повороте не будет вызываться
// так как предыдущий загрузчик с данным ID уже был создан ранее
// Будет также вызван при рестарте через метод LoaderManager.restartLoader()
@NonNull
@Override
public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle bundle) {
    switch (id){
        case LOADER_FOR_SPINNER:
            myLoader = new myCursorLoader(thiscontext, database, id);
            if (logFlag) {
                Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onCreateLoader() - LOADER_FOR_SPINNER ----");
            }
            break;
    }
    return myLoader;
}
    // Вызовется, когда загрузчик закончит свою работу. Вызывается в основном потоке
    // Может вызываться несколько раз при изменении данных
    // Также вызывается при поворотах
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, final Cursor c) {
        switch (loader.getId()) {
            // Если используется несколько загрузчиков, то удобнее через оператор switch-case
            case LOADER_FOR_SPINNER:
                simpleCursorAdapterForSpinner.swapCursor(c);
                if (logFlag) {
                    Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onLoadFinished - LOADER_FOR_SPINNER ----");
                }
                if (idBuying != -1){
                    if (logFlag) {
                        Log.d("myLog", "---- (4) ---- DialogFragmentAddNewBuying - onLoadFinished() - Обновление данных о старой покупке - idCategoryBuying " + idCategoryBuying + "----");
                    }
                    mySpinner.setSelection(idCategoryBuying - 2);
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
    }

    static class myCursorLoader extends CursorLoader {
        Database db;
        final int LoaderID;
        Cursor cursor;

        public myCursorLoader(Context context, Database db, int id) {
            super(context);
            this.db = db;
            LoaderID = id;
        }

        @Override
        public Cursor loadInBackground() {
            switch (LoaderID){
                case LOADER_FOR_SPINNER:
                    cursor = db.getCategoryForSPINNER();

                    break;
            }
            return cursor;
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
//метод для скрытия клавиатуры
    public static void hideKeyboardFrom(Activity activity, View view) {

        if (activity != null && view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
