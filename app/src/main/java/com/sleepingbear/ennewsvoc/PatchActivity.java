package com.sleepingbear.ennewsvoc;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class PatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle("패치 내용");
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        StringBuffer patch = new StringBuffer();

        patch.append("* 신규 패치" + CommConstants.sqlCR);
        patch.append("" + CommConstants.sqlCR);
        patch.append("- 영어 뉴스 기능 개선" + CommConstants.sqlCR);
        patch.append("" + CommConstants.sqlCR);
        patch.append("" + CommConstants.sqlCR);


        patch.append("- 환경설정에서 단어상세 화면의 상단에 있는 콤보값을 설정하도록 수정(Naver, Daum, 예제)" + CommConstants.sqlCR);
        patch.append("- 환경설정에서 폰트 사이즈 변경 가능하도록 수정" + CommConstants.sqlCR);
        patch.append("- 단어장 상세 부분을 네이버 검색, 다움 검색으로 변경하였습니다." + CommConstants.sqlCR);
        patch.append("- 영어사전, 영어회화, 영어신문을 하나로 통합하여 사용하면 좋을듯해서 '최고의 영어학습' 어플을 새로 만들었습니다. 한개의 어플로 계속 기능개선을 할 예정입니다." + CommConstants.sqlCR);
        patch.append("- 단어장에서 TTS로 단어, 뜻을 듣는 기능 추가 - 상단 Context Menu에서 TTS 선택" + CommConstants.sqlCR);
        patch.append("- 단어학습에서 '카드형 4지선다 TTS 학습' 기능 추가" + CommConstants.sqlCR);
        patch.append("- 단어장에서 선택을 해서 삭제하거나, 다른 단어장으로 복사, 이동하는 기능 추가" + CommConstants.sqlCR);
        patch.append("- 2016.11.21 : 영어뉴스 어플 개발" + CommConstants.sqlCR);

        ((TextView) this.findViewById(R.id.my_c_patch_tv1)).setText(patch.toString());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
