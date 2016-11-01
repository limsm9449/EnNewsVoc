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
        items.add(new NewsVo("Chosun","http://english.chosun.com",
                new String[]{"$('.art_headline')","$('.news_body .par')"}, new String[]{}, R.drawable.img_chosunilbo));
        items.add(new NewsVo("Joongang Daily","http://mengnews.joins.com/",
                new String[]{"$($('h4')[0])","$('.en')"}, new String[]{}, R.drawable.img_joongangdaily));
        items.add(new NewsVo("Korea Herald","http://www.koreaherald.com",
                new String[]{"$($('#detail h2')[0])","$('.article')"}, new String[]{}, R.drawable.img_koreaherald));
        items.add(new NewsVo("Korea Times","http://m.koreatimes.co.kr/phone/",
                new String[]{"$('#first_big_news strong .english_mode')","$('#startts div .english_mode')"}, new String[]{}, R.drawable.img_koreatimes));
        items.add(new NewsVo("ABC","http://abcnews.go.com",
                new String[]{"$('.container .article-header h1')","$('.container .article-body')"}, new String[]{}, R.drawable.img_abcnews));
        items.add(new NewsVo("BBC","http://www.bbc.com/news",
                new String[]{"$('.story-body .story-body__h1')","$('.story-body .story-body__inner p')"}, new String[]{}, R.drawable.img_bbc));
        items.add(new NewsVo("CNN","http://edition.cnn.com",
                new String[]{"jQuery('.pg-headline')","jQuery('.l-container .zn-body__paragraph')"}, new String[]{}, R.drawable.img_cnn));
        items.add(new NewsVo("Los Angeles Times","http://www.latimes.com",
                new String[]{"$('.trb_ar_hl_t')","$('.trb_ar_page p')"}, new String[]{}, R.drawable.img_losangelestimes));
        items.add(new NewsVo("The New Work Times","http://mobile.nytimes.com/?referer=",
                new String[]{"$('.headline')","$('.article-body p')"}, new String[]{}, R.drawable.img_newworktimes));
        items.add(new NewsVo("Reuters","http://mobile.reuters.com/",
                new String[]{"$('.article-info h1')","$('#articleText p')"}, new String[]{}, R.drawable.img_reuters));
        items.add(new NewsVo("Washingtone Post","http://www.washingtonpost.com",
                new String[]{"$('#topper-headline-wrapper h1')","$('#article-body article p')"}, new String[]{}, R.drawable.img_washingtonepost));

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
            bundle.putStringArray("changeClass", cur.getChangeClass());
            bundle.putStringArray("removeClass", cur.getRemoveClass());
            intent.putExtras(bundle);

            startActivity(intent);
        }
    };

    private class NewsVo {
        private String name;
        private String url;
        private String[] changeClass;
        private String[] removeClass;
        private int imageRes;

        public NewsVo(String name, String url, String[] changeClass, String[] removeClass, int imageRes) {
            this.name = name;
            this.url = url;
            this.changeClass = changeClass;
            this.removeClass = removeClass;
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

        public String[] getChangeClass() {
            return changeClass;
        }

        public void setChangeClass(String[] changeClass) {
            this.changeClass = changeClass;
        }

        public String[] getRemoveClass() {
            return removeClass;
        }

        public void setRemoveClass(String[] removeClass) {
            this.removeClass = removeClass;
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
