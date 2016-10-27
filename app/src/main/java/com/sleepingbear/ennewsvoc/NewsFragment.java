package com.sleepingbear.ennewsvoc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;


public class NewsFragment extends Fragment {
    private View  mainView;
    private ListView listView;
    private NewsAdapter adapter;

    public NewsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_news, container, false);

        listView = (ListView)mainView.findViewById(R.id.my_f_news_lv);

        ArrayList<NewsVo> items = new ArrayList<>();
        items.add(new NewsVo("ABC","http://abcnews.go.com","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_abcnews));
        items.add(new NewsVo("BBC","abc뉴스","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_bbc));
        items.add(new NewsVo("Chosun","http://english.chosun.com","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_chosunilbo));
        items.add(new NewsVo("CNN","http://edition.cnn.com","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_cnn));
        items.add(new NewsVo("Joongang Daily","http://mengnews.joins.com/","$($('h4')[0])","$('.en')", R.drawable.img_joongangdaily));
        items.add(new NewsVo("Korea Herald","http://www.koreaherald.com","$($('#detail h2')[0])","$('.article')", R.drawable.img_koreaherald));
        items.add(new NewsVo("Korea Times","http://www.koreatimes.co.kr","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_koreatimes));
        items.add(new NewsVo("LA Times","http://www.latimes.com","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_losangelestimes));
        items.add(new NewsVo("Newwork Times","http://www.nytimes.com","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_newworktimes));
        items.add(new NewsVo("Reuters","http://www.reuters.com/","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_reuters));
        items.add(new NewsVo("WallStreet Journal","http://asia.wsj.com/home-page","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_wallstreetjournal));
        items.add(new NewsVo("Washingtone Post","http://www.washingtonpost.com","$('#first_big_news strong .english_mode')","$('#startts div .english_mode')", R.drawable.img_washingtonepost));

        adapter = new NewsAdapter(getContext(), 0, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);


        AdView av = (AdView)mainView.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);

        return mainView;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewsVo cur = (NewsVo) adapter.getItem(position);

            DicUtils.dicLog(cur.getName());

            Intent intent = new Intent(getActivity().getApplication(), WebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", cur.getName());
            bundle.putString("url", cur.getUrl());
            bundle.putString("titleClass", cur.getTitleClass());
            bundle.putString("contentClass", cur.getContentClass());
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };

    private class NewsVo {
        private String name;
        private String url;
        private String titleClass;
        private String contentClass;
        private int imageRes;

        public NewsVo(String name, String url, String titleClass, String contentClass, int imageRes) {
            this.name = name;
            this.url = url;
            this.titleClass = titleClass;
            this.contentClass = contentClass;
            this.imageRes = imageRes;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getTitleClass() {
            return titleClass;
        }

        public void setTitleClass(String titleClass) {
            this.titleClass = titleClass;
        }

        public String getContentClass() {
            return contentClass;
        }

        public void setContentClass(String contentClass) {
            this.contentClass = contentClass;
        }

        public int getImageRes() {
            return imageRes;
        }

        public void setImageRes(int imageRes) {
            this.imageRes = imageRes;
        }
    }

    private class NewsAdapter extends ArrayAdapter<NewsVo> {
        private ArrayList<NewsVo> items;

        public NewsAdapter(Context context, int textViewResourceId, ArrayList<NewsVo> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.fragment_news_item, null);
            }

            // ImageView 인스턴스
            ImageView imageView = (ImageView)v.findViewById(R.id.my_f_news_item_iv);
            imageView.setImageResource(items.get(position).imageRes);

            return v;
        }
    }

}
