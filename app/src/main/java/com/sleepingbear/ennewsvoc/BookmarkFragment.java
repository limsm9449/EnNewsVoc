package com.sleepingbear.ennewsvoc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class BookmarkFragment extends Fragment implements View.OnClickListener {
    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private View mainView;
    private BookmarkCursorAdapter adapter;

    private AppCompatActivity mMainActivity;

    public BookmarkFragment() {
    }

    public void setMainActivity(AppCompatActivity mainActivity) {
        mMainActivity = mainActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_bookmark, container, false);

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
        Cursor listCursor = db.rawQuery(DicQuery.getBookmark(), null);
        ListView listView = (ListView) mainView.findViewById(R.id.my_f_bookmark_lv);
        adapter = new BookmarkCursorAdapter(getContext(), listCursor, 0);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);
        listView.setSelection(0);
    }

    /**
     * 북마크가 선택되면은 뉴스 상세창을 열어준다.
     */
    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Cursor cur = (Cursor) adapter.getItem(position);
            //cur.moveToPosition(position);

            String kind = cur.getString(cur.getColumnIndexOrThrow("KIND"));
            String title = cur.getString(cur.getColumnIndexOrThrow("TITLE"));
            String url = cur.getString(cur.getColumnIndexOrThrow("URL"));

            Intent intent = new Intent(getActivity().getApplication(), WebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("kind", kind);
            bundle.putString("title", title);
            bundle.putString("url", url);
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };


    @Override
    public void onClick(View v) {
    }


}

class BookmarkCursorAdapter extends CursorAdapter {
    public BookmarkCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fragment_bookmark_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ((TextView) view.findViewById(R.id.my_f_bi_tv_bookmark)).setText(cursor.getString(cursor.getColumnIndexOrThrow("TITLE")));
        ((TextView) view.findViewById(R.id.my_f_bi_tv_date)).setText(cursor.getString(cursor.getColumnIndexOrThrow("INS_DATE")));
    }
}



