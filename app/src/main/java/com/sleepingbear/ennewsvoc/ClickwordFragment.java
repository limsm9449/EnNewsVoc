package com.sleepingbear.ennewsvoc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ClickwordFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private ClickwordCursorAdapter adapter;

    private AppCompatActivity mMainActivity;

    public ClickwordFragment() {
    }

    public void setMainActivity(AppCompatActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_clickword, container, false);

        dbHelper = new DbHelper(getContext());
        db = dbHelper.getWritableDatabase();

        //리스트 내용 변경
        changeListView();

        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    public void changeListView() {
        DicUtils.dicLog(this.getClass().toString() + " changeListView");

        Cursor listCursor = db.rawQuery(DicQuery.getClickword(), null);
        ListView listView = (ListView) mainView.findViewById(R.id.my_f_clickword_lv);
        adapter = new ClickwordCursorAdapter(getContext(), listCursor, 0);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelection(0);
    }

    @Override
    public void onClick(View v) {
    }

}

class ClickwordCursorAdapter extends CursorAdapter {
    public ClickwordCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_clickword_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_f_ci_tv_word)).setText(cursor.getString(cursor.getColumnIndexOrThrow("WORD")));
        ((TextView) view.findViewById(R.id.my_f_ci_tv_spelling)).setText(cursor.getString(cursor.getColumnIndexOrThrow("SPELLING")));
        ((TextView) view.findViewById(R.id.my_f_ci_tv_date)).setText(cursor.getString(cursor.getColumnIndexOrThrow("INS_DATE")));
        ((TextView) view.findViewById(R.id.my_f_ci_tv_mean)).setText(cursor.getString(cursor.getColumnIndexOrThrow("MEAN")));
    }
}



