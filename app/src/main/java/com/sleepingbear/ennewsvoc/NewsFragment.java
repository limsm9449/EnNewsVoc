package com.sleepingbear.ennewsvoc;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.fsn.cauly.CaulyAdView;
import com.fsn.cauly.CaulyAdViewListener;
import com.fsn.cauly.CaulyInterstitialAd;
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
        items.add(new NewsVo("E027", "Arirang",R.drawable.img_arirang));
        items.add(new NewsVo("E001", "Chosun",R.drawable.img_chosunilbo));
        items.add(new NewsVo("E002", "Joongang Daily",R.drawable.img_joongangdaily));
        items.add(new NewsVo("E003", "Korea Herald",R.drawable.img_koreaherald));
        items.add(new NewsVo("E004", "Korea Times",R.drawable.img_koreatimes));
        items.add(new NewsVo("E005", "ABC",R.drawable.img_abcnews));
        items.add(new NewsVo("E022", "AudioEnglish",R.drawable.img_audioenglish));
        items.add(new NewsVo("E006", "BBC",R.drawable.img_bbc));
        items.add(new NewsVo("E038", "Cambridge News",R.drawable.img_cambridgenews));
        items.add(new NewsVo("E031", "CBS News",R.drawable.img_cbsn));
        items.add(new NewsVo("E028", "Channel News Asia",R.drawable.img_channelnewsasia));
        items.add(new NewsVo("E032", "Chicago Tribune",R.drawable.img_chicagotribune));
        items.add(new NewsVo("E007", "CNN",R.drawable.img_cnn));
        items.add(new NewsVo("E015", "Fast Company",R.drawable.img_fastcompany));
        items.add(new NewsVo("E034", "Guardian",R.drawable.img_guardian));
        items.add(new NewsVo("E037", "Herald",R.drawable.img_theherald));
        items.add(new NewsVo("E035", "Independent",R.drawable.img_independent));
        items.add(new NewsVo("E026", "KBS World radio",R.drawable.img_kbsradio));
        items.add(new NewsVo("E008", "Los Angeles Times",R.drawable.img_losangelestimes));
        items.add(new NewsVo("E036", "Metro",R.drawable.img_metro));
        items.add(new NewsVo("E013", "National Geographic",R.drawable.img_nationalgeographic));
        items.add(new NewsVo("E033", "NewYork Post",R.drawable.img_newyorkpost));
        items.add(new NewsVo("E018", "People",R.drawable.img_people));
        items.add(new NewsVo("E014", "Reader's digest",R.drawable.img_readersdigest));
        items.add(new NewsVo("E024", "Repeat after us",R.drawable.img_repeatafterus));
        items.add(new NewsVo("E010", "Reuters",R.drawable.img_reuters));
        items.add(new NewsVo("E020", "ShortList",R.drawable.img_shortlist));
        items.add(new NewsVo("E021", "Sunset",R.drawable.img_sunset));
        items.add(new NewsVo("E029", "The Economist",R.drawable.img_theeconomist));
        items.add(new NewsVo("E009", "The New Work Times",R.drawable.img_newworktimes));
        items.add(new NewsVo("E025", "The wall street journal",R.drawable.img_thewallstreetjournal));
        items.add(new NewsVo("E016", "Time",R.drawable.img_time));
        items.add(new NewsVo("E017", "Time for kids",R.drawable.img_timeforkids));
        items.add(new NewsVo("E030", "USA Today",R.drawable.img_usatoday));
        items.add(new NewsVo("E039", "Sunday People",R.drawable.img_sundaypeople));
        items.add(new NewsVo("E023", "VOA",R.drawable.img_voa));
        items.add(new NewsVo("E019", "Vogue",R.drawable.img_vogue));
        items.add(new NewsVo("E011", "Washingtone Post",R.drawable.img_washingtonepost));
        items.add(new NewsVo("E012", "ZDNet",R.drawable.img_zdnet));

        adapter = new NewsAdapter(getContext(), 0, items);
        listView.setAdapter(adapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(itemClickListener);

        DicUtils.setAdViewForFragment(this.getActivity(), mainView);

        return mainView;
    }

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            NewsVo cur = (NewsVo) adapter.getItem(position);

            DicUtils.dicLog(cur.getName());

            Intent intent = new Intent(getActivity().getApplication(), WebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("kind", cur.getKind());
            intent.putExtras(bundle);

            getActivity().startActivityForResult(intent, CommConstants.a_news);
        }
    };

    private class NewsVo {
        private String kind;
        private String name;
        private int imageRes;

        public NewsVo(String kind, String name, int imageRes) {
            this.kind = kind;
            this.name = name;
            this.imageRes = imageRes;
        }

        public String getKind() {
            return kind;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
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
