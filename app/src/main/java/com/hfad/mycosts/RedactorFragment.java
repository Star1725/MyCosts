package com.hfad.mycosts;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.TextView;
import java.util.Objects;

import static com.hfad.mycosts.MainActivity.logFlag;
import static com.hfad.mycosts.MainActivity.myCostsFragmenINfocus;
import static com.hfad.mycosts.MainActivity.redactorFragmentINfocus;


/**
 * A simple {@link Fragment} subclass.
 */
public class RedactorFragment extends Fragment implements View.OnClickListener{

    public interface listenerForRedactorFragment {
        public void updateUIforRedactorFragment();
    }

    listenerForRedactorFragment listenerForRedactorFragment;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            listenerForRedactorFragment = (listenerForRedactorFragment) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement clickListenerRedactorFragment");
        }
    }

    private static final int CONTEXT_MENU_FOR_RedactorFragment_DELETE = 3;
    private static final int CONTEXT_MENU_FOR_RedactorFragment_UPGRADE = 4;
    private static final int CONTEXT_MENU_FOR_RedactorFragment_SHOW = 5;
    Button buttonAddBuying;
    ExpandableListView ExpandableListViewTreeBuying;
    int showSimpleCursorTreeAdapter = 0;
    SimpleCursorTreeAdapter simpleCursorTreeAdapterForBuying;
    SimpleCursorTreeAdapter simpleCursorTreeAdapterForSubCategory;

    public void setCostCategory(Cursor cursor) {
        costCategory[0] = 0;
        costCategory[1] = 0;
        costCategory[2] = 0;
        costCategory[3] = 0;
        costCategory[4] = 0;
        costCategory[5] = 0;
        costCategory[6] = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    if (!cursor.isNull(2)) {
                        costCategory[0] = costCategory[0] + cursor.getInt(2);
                        costCategory[cursor.getInt(0) - 1] = costCategory[cursor.getInt(0) - 1] + cursor.getInt(2);
                    } else {
                        costCategory[0] = costCategory[0] + 0;
                        costCategory[cursor.getInt(0) - 1] = costCategory[cursor.getInt(0) - 1] + 0;
                    }
                } while (cursor.moveToNext());
            }
            if (logFlag) {
                Log.d("myLog", " cost[0] = " + costCategory[0]);
                Log.d("myLog", " cost[1] = " + costCategory[1]);
                Log.d("myLog", " cost[2] = " + costCategory[2]);
                Log.d("myLog", " cost[3] = " + costCategory[3]);
                Log.d("myLog", " cost[4] = " + costCategory[4]);
                Log.d("myLog", " cost[5] = " + costCategory[5]);
                Log.d("myLog", " cost[6] = " + costCategory[6]);
            }
        }
    }

    private int[] costCategory = {0, 0, 0, 0, 0, 0, 0};
    private Cursor cursorGroup;

    public void setCalendar() {
        this.dayBuy = MainActivity.getDay();
        this.monthBuy = MainActivity.getMonth();
        this.yearBuy = MainActivity.getYear();
    }

    private int dayBuy;
    private int monthBuy;
    private int yearBuy;
    TextView textView_shopping_list;

    private Database database;
    Context thiscontext;
    public RedactorFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (logFlag) {
            Log.d("myLog", "---- (2)RedactorFragment()  -  onCreateView() ----");
        }
        View view = inflater.inflate(R.layout.fragment_redactor, container, false);

        //получение контекста
        thiscontext = inflater.getContext();
        database = MainActivity.getDatabase();

        //получение текущей даты
        dayBuy = MainActivity.getDay();
        monthBuy = MainActivity.getMonth();
        yearBuy = MainActivity.getYear();
        textView_shopping_list = view.findViewById(R.id.textView_shopping_list);
        textView_shopping_list.setText("Список покупок за " + dayBuy + "." + monthBuy + "." + yearBuy);
        buttonAddBuying = view.findViewById(R.id.button_add_buy);
        buttonAddBuying.setOnClickListener(this);

        ExpandableListViewTreeBuying = view.findViewById(R.id.listview_shopping_list);
        //регистрируем контекстное меню для списка
        registerForContextMenu(ExpandableListViewTreeBuying);
        return view;
    }

    public void setExpandableListViewTree(Cursor cursor){
        //данные для групп
        String[] groupFrom = {Database.CATEGORY_COLUMN_NAME};
        int[] groupTo = {R.id.textView_category_buying};
        //данные для элементов групп
        String[] childFromBuying = {Database.BUYING_COLUMN_NAME, Database.SUBCATEGORY_COLUMN_NAME, Database.BUYING_COLUMN_PRICE};
        int[] childToBuying = {R.id.textView_for_name_buying, R.id.textView_subCategory, R.id.textView_price};
        String[] childFromSubCat = {Database.SUBCATEGORY_COLUMN_NAME};
        int[] childToSubCat = {R.id.textView_subCategory2};
        if (logFlag) {
            Log.d("myLog", "---- (2) ---- RedactorFragment()  -  setExpandableListViewTree() - получаем курсор для категорий за " + dayBuy + "." + monthBuy + "." + yearBuy + " ----");
        }
        cursorGroup = cursor;
        setCostCategory(cursor);
        //создаем адаптер
        if (logFlag) {
            Log.d("myLog", "---- (2) ---- RedactorFragment()  -  onCreateView() - создаем адаптеры ----");
        }
        //создаем адаптер для древовидного списка
        simpleCursorTreeAdapterForBuying = new MyAdapter(thiscontext, cursorGroup, R.layout.layout_for_list_category, groupFrom, groupTo, R.layout.layout_for_list_buyings, childFromBuying, childToBuying);
        simpleCursorTreeAdapterForSubCategory = new MyAdapter(thiscontext, cursorGroup, R.layout.layout_for_list_category, groupFrom, groupTo, R.layout.layout_for_list_subcategory, childFromSubCat, childToSubCat);

        if (showSimpleCursorTreeAdapter == 0){
            if (logFlag) {
                Log.d("myLog", "0");
                Log.d("myLog", "---- (2) ---- RedactorFragment()  -  onCreateView() - установили адаптер simpleCursorTreeAdapterForBuying ----");
            }
            ExpandableListViewTreeBuying.setAdapter(simpleCursorTreeAdapterForBuying);
            //раскрываем полностью древовидный список
            for (int i = 0; i <= 6; i++ ){
                ExpandableListViewTreeBuying.expandGroup(i);
            }
        } else {
            if (logFlag) {
                Log.d("myLog", "1");
                Log.d("myLog", "---- (2) ---- RedactorFragment()  -  onCreateView() - установили адаптер simpleCursorTreeAdapterForSubCategory ----");
            }
            ExpandableListViewTreeBuying.setAdapter(simpleCursorTreeAdapterForSubCategory);
            //раскрываем полностью древовидный список
            for (int i = 0; i <= 6; i++) {
                ExpandableListViewTreeBuying.expandGroup(i);
            }
        }
    }
    @Override
    public void onResume() {
        if (logFlag) {
            Log.d("myLog", "---- (2) ---- RedactorFragment()  -  onResume() ----");
        }
        super.onResume();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        if (!getUserVisibleHint())
        {
            return;
        }
        if (logFlag) {
            Log.d("myLog", "!!!!!!!!!!!!!!!!!!!!! ---- (2) ---- RedactorFragment()  - в фокусе !!!!!!!!!!!!!!!!!!!!!!!");
            Log.d("myLog", "!!!!!!!!!!!!!!!!!!!!! ---- (2) ---- RedactorFragment()  - обновляем UI !!!!!!!!!!!!!!!!!!!!!!!");
        }
        listenerForRedactorFragment.updateUIforRedactorFragment();
        myCostsFragmenINfocus = false;
        redactorFragmentINfocus = true;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (logFlag) {
            Log.d("myLog", "---- (2) ---- RedactorFragment()  -  setUserVisibleHint(" +  isVisibleToUser +" ) ----");
        }
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed() && isVisibleToUser) {
            onResume();
        }
    }

    @Override
    public void onStart() {
        if (logFlag) {
            Log.d("myLog", "---- (2) ---- RedactorFragment()  -  onStart() ----");
        }
        super.onStart();
    }

    @Override
    public void onPause() {
        if (logFlag) {
            Log.d("myLog", "---- (2) ---- RedactorFragment() -  onPause() ----");
        }
        super.onPause();
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_add_buy) {//С помощью этих трех строк вы объявляете свой DialogFragment
            DialogFragment dialogFragADD = new DialogFragmentAddNewBuying();
            //устанавливая requestCode (который будет вызывать onActivityResult (...) после закрытия Dialog,
            dialogFragADD.setTargetFragment(this, 1);
            // и затем вы показываете диалоговое окно
            assert getFragmentManager() != null;
            dialogFragADD.show(getFragmentManager(), "dialog");
        }
    }

    class MyAdapter extends SimpleCursorTreeAdapter{
        MyAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
            super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        }

        @Override
        protected Cursor getChildrenCursor(Cursor groupCursor) {
            int IDgroup = groupCursor.getInt(groupCursor.getColumnIndex(Database.CATEGORY_COLUMN_ID));
            if (logFlag) {
                Log.d("myLog", "---- (2) ---- RedactorFragment()  -  class MyAdapter - getChildrenCursor() - ID выбранной категории = " + IDgroup + " ----");
                Log.d("myLog", "---- (2) ---- RedactorFragment()  -  class MyAdapter - getChildrenCursor() - текущая дата: " + dayBuy + "." + monthBuy + "." + yearBuy + " ----");
            }
            Cursor cursor;
            if (showSimpleCursorTreeAdapter == 0){
                cursor = database.checkAndGetData(null, IDgroup, 0, 0, dayBuy, 0, monthBuy, yearBuy, 1);
            } else {
                cursor = database.getNameForSubCategory(IDgroup,"RedactorFragment()");
            }
            return cursor;
        }

        @Override
        protected void bindGroupView(View view, Context context, Cursor cursor, boolean isExpanded) {
            if (logFlag) {
                Log.d("myLog", "---- (2) ---- RedactorFragment() - class MyAdapter - bindGroupView()");
            }
            int IDgroup = cursor.getInt(cursor.getColumnIndex(Database.CATEGORY_COLUMN_ID));
            TextView titelCat = view.findViewById(R.id.textView_category_buying);
            if (showSimpleCursorTreeAdapter == 0){
                if (costCategory[IDgroup - 1] == 0){
                    titelCat.setText(cursor.getString(cursor.getColumnIndex(Database.CATEGORY_COLUMN_NAME)));
                    if (logFlag) {
                        Log.d("myLog", "---- (2) ---- RedactorFragment() - class MyAdapter - bindGroupView() - titelCat - " + titelCat.getText().toString() + " IDgroup = " + (IDgroup));
                    }
                } else {
                    titelCat.setText(cursor.getString(cursor.getColumnIndex(Database.CATEGORY_COLUMN_NAME)) + " - " + costCategory[IDgroup - 1] + " р");
                    if (logFlag) {
                        Log.d("myLog", "---- (2) ---- RedactorFragment() - class MyAdapter - bindGroupView() - titelCat - " + titelCat.getText().toString()+ " IDgroup = " + (IDgroup) + " cost = " + costCategory[IDgroup - 1] + " р");
                    }
                }
            } else {
                titelCat.setText(cursor.getString(cursor.getColumnIndex(Database.CATEGORY_COLUMN_NAME)));
            }

        }

        @Override
        public void setViewText(TextView v, String text) {
            if (logFlag) {
                Log.d("myLog", "---- (2) ---- RedactorFragment() - class MyAdapter - setViewText() - text - " + text);
            }
            super.setViewText(v, text);
            if (v.getId() == R.id.textView_price){
                v.setText(text + " р");
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////
//создание контекстного меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        if (v.getId() == R.id.listview_shopping_list) {
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) menuInfo;
            Integer idItem = (int) info.id;
            long packedPosition = ExpandableListView.getPackedPositionType(info.packedPosition);
            if (logFlag) {
                Log.d("myLog", "---- (2) ---- RedactorFragment() - onCreateContextMenu() - packedPosition = " + packedPosition);
            }

            if (packedPosition == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                //кликнули на группе}
                if (logFlag) {
                    Log.d("myLog", "---- (2) ---- RedactorFragment() - onCreateContextMenu() - TYPE_GROUP");
                    Log.d("myLog", "---- (2) ---- RedactorFragment() - onCreateContextMenu() - idItem_GROUP = " + idItem);
                }
                menu.add(0, CONTEXT_MENU_FOR_RedactorFragment_UPGRADE, 0, "Обновить выбранную запись");
                if (showSimpleCursorTreeAdapter == 0) menu.add(0, CONTEXT_MENU_FOR_RedactorFragment_SHOW, 0, "Показать подкатегории");
                else menu.add(0, CONTEXT_MENU_FOR_RedactorFragment_SHOW, 0, "Показать cписок покупок");

            }
            if (packedPosition == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                //кликнули на чайлде
                if (logFlag) {
                    Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - TYPE_CHILD");
                    Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - idItem_CHILD = " + idItem);
                }
                if (showSimpleCursorTreeAdapter == 0) menu.add(0, CONTEXT_MENU_FOR_RedactorFragment_DELETE, 0, "Удалить выбранную запись");
                menu.add(0, CONTEXT_MENU_FOR_RedactorFragment_UPGRADE, 0, "Обновить выбранную запись");
            }
        }
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //обработка нажатия контекстного меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (redactorFragmentINfocus){
            ExpandableListView.ExpandableListContextMenuInfo info = (ExpandableListView.ExpandableListContextMenuInfo) item.getMenuInfo();
            Integer idItem = (int) info.id;
            long packedPosition = ExpandableListView.getPackedPositionType(info.packedPosition);
            if (logFlag) {
                Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - packedPosition = " + packedPosition);
            }
            switch (item.getItemId()){
                case CONTEXT_MENU_FOR_RedactorFragment_DELETE:
                    if (packedPosition == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                        //кликнули на чайлде
                        if (logFlag) {
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - TYPE_CHILD");
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - idItem_CHILD = " + idItem);
                        }
                        database.delBuying(idItem);
                        if (logFlag) {
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - Delete CHILD");
                        }
                        simpleCursorTreeAdapterForBuying.notifyDataSetChanged();
                    }
                    Boolean isGroup = ExpandableListView.getPackedPositionType(info.packedPosition) == ExpandableListView.PACKED_POSITION_TYPE_GROUP;
                    break;
                case CONTEXT_MENU_FOR_RedactorFragment_UPGRADE:
                    if (packedPosition == ExpandableListView.PACKED_POSITION_TYPE_GROUP) {
                        String textError;
                        final Snackbar snackbar;
                        //кликнули на группе}
                        if (logFlag) {
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - TYPE_GROUP");
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - idItem_GROUP = " + idItem);
                        }
                        textError = "Контекстноe меню соответствующей категории во вкладке \"Расходы\"";
                        snackbar = Snackbar.make(ExpandableListViewTreeBuying, textError, Snackbar.LENGTH_LONG);
                        snackbar.setDuration(5000);
                        snackbar.setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                        snackbar.show();
                        //Toast.makeText(thiscontext, "Имя категории можно обновить в контекстном меню соответствующей категории вкладки \"Расходы\"", Toast.LENGTH_SHORT).show();
                    }
                    if (packedPosition == ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                        //кликнули на чайлде
                        if (logFlag) {
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - TYPE_CHILD");
                            Log.d("myLog", "---- (2) ---- RedactorFragment() - onContextItemSelected() - idItem_CHILD = " + idItem);
                        }
                        if (showSimpleCursorTreeAdapter == 0){
                            //передаем в DialogFragmentAddNewBuying данные о покупке(idItem)
                            //DialogFragment dialogFragUPDATE = new DialogFragmentAddNewBuying();
                            DialogFragment dialogFragUPDATE = DialogFragmentAddNewBuying.newInstance(idItem);
                            dialogFragUPDATE.show(getFragmentManager(), "dialog");
                        } else {
                            DialogFragment myDialogFragment = MyDialogFragment.newInstance(2, idItem, 0);
                            myDialogFragment.show(getFragmentManager(), "dialog");
                        }
                    }
                    break;
                case CONTEXT_MENU_FOR_RedactorFragment_SHOW:
                    switch (showSimpleCursorTreeAdapter){
                        case 0:
                            showSimpleCursorTreeAdapter = 1;
                            if (logFlag) {
                                Log.d("myLog", "0");
                            }
                            textView_shopping_list.setText("Список категорий и подкатегоий");
                            simpleCursorTreeAdapterForSubCategory.notifyDataSetChanged();
                            ExpandableListViewTreeBuying.setAdapter(simpleCursorTreeAdapterForSubCategory);
                            //раскрываем полностью древовидный список
                            for (int i = 0; i <= 6; i++ ){
                                ExpandableListViewTreeBuying.expandGroup(i);
                            }
                            break;
                        case 1:
                            showSimpleCursorTreeAdapter = 0;
                            if (logFlag) {
                                Log.d("myLog", "1");
                            }
                            textView_shopping_list.setText("Список покупок за " + dayBuy + "." + monthBuy + "." + yearBuy);
                            simpleCursorTreeAdapterForBuying.notifyDataSetChanged();
                            ExpandableListViewTreeBuying.setAdapter(simpleCursorTreeAdapterForBuying);
                            //раскрываем полностью древовидный список
                            for (int i = 0; i <= 6; i++ ){
                                ExpandableListViewTreeBuying.expandGroup(i);
                            }
                            break;
                    }
                    break;
            }
        }
        return super.onContextItemSelected(item);
    }
}