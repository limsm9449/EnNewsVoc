package com.sleepingbear.ennewsvoc;


import android.Manifest;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
//import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager mPager;
    private MainPagerAdapter adapter;

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    private TabLayout tabLayout;

    private int selectedTab = 0;

    private FloatingActionButton fab;

    private Activity mActivity;
    public boolean mIsCategory = true;

    private static final int MY_PERMISSIONS_REQUEST = 0;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        System.out.println("=============================================== App Start ======================================================================");

        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();

        mActivity = this;

        //DB가 새로 생성이 되었으면 이전 데이타를 DB에 넣고 Flag를 N 처리함
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if ( "Y".equals(prefs.getString("db_new", "N")) ) {
            DicUtils.dicLog("backup data import");

            DicUtils.readInfoFromFile(this, db);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("db_new", "N");
            editor.commit();
        };

        //카테고리 추가 기능 구현
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                final View dialog_layout = inflater.inflate(R.layout.dialog_category_add, (ViewGroup) findViewById(R.id.my_d_category_root));

                //dialog 생성..
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setView(dialog_layout);
                final AlertDialog alertDialog = builder.create();

                ((TextView) dialog_layout.findViewById(R.id.my_d_category_add_tv_title)).setText("단어장 추가");
                final EditText et_ins = ((EditText) dialog_layout.findViewById(R.id.my_d_category_add_et_ins));
                ((Button) dialog_layout.findViewById(R.id.my_d_category_add_b_ins)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ("".equals(et_ins.getText().toString())) {
                            Toast.makeText(getApplicationContext(), "단어장 이름을 입력하세요.", Toast.LENGTH_SHORT).show();
                        } else {
                            alertDialog.dismiss();

                            String insCategoryCode = DicQuery.getInsCategoryCode(db);
                            db.execSQL(DicQuery.getInsNewCategory("MY", insCategoryCode, et_ins.getText().toString()));
                            DicUtils.setDbChange(getApplicationContext());

                            ((VocabularyFragment) adapter.getItem(selectedTab)).changeListView();

                            Toast.makeText(getApplicationContext(), "단어장을 추가하였습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                ((Button) dialog_layout.findViewById(R.id.my_d_category_add_b_close)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();
            }
        });

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle(R.string.app_name);
        //ab.setIcon(R.mipmap.ic_launcher);

        // ViewPaper 를 정의한다.
        mPager = (ViewPager) findViewById(R.id.main_pager);
        adapter = new MainPagerAdapter(getSupportFragmentManager(), this);
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //DicUtils.dicLog(this.getClass().toString() + " onPageSelected" + " : " + position);
                selectedTab = position;

                //mPager.setCurrentItem(position);
                setChangeViewPaper(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        //mPager.setCurrentItem(0);
        //setChangeViewPaper(selectedTab, CommConstants.changeKind_title);

        // 상단의 Tab 을 정의한다.
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                DicUtils.dicLog(" onTabSelected" + " : " + tab.getPosition());
                selectedTab = tab.getPosition();
                //tab 변경
                mPager.setCurrentItem(selectedTab);

                //상단 편집 버튼 갱신
                isEditing = false;
                invalidateOptionsMenu();

                if ( selectedTab == 1 ) {
                    ((ClickwordFragment) adapter.getItem(selectedTab)).changeListView();
                    ((ClickwordFragment) adapter.getItem(selectedTab)).changeEdit(isEditing);
                } else if ( selectedTab == 2 ) {
                    ((BookmarkFragment) adapter.getItem(selectedTab)).changeListView();
                    ((BookmarkFragment) adapter.getItem(selectedTab)).changeEdit(isEditing);
                }

                //메뉴 구성
                invalidateOptionsMenu();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

		/*
        String flag_other = "other_20161009";
        if ( "N".equals(prefs.getString(flag_other, "N")) ) {
            DicUtils.writeNewInfoToFile(this, db);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(flag_other, "Y");
            editor.commit();
        };
		*/

		checkPermission();
    }

    public boolean checkPermission() {
        DicUtils.dicLog("checkPermission");
        boolean isCheck = false;
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {
            DicUtils.dicLog("권한 없음");
            if ( ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ) {
                Toast.makeText(this, "(중요)파일로 내보내기, 가져오기를 하기 위해서 권한이 필요합니다.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST);
            DicUtils.dicLog("2222");
        } else {
            DicUtils.dicLog("권한 있음");
            isCheck = true;
        }

        return isCheck;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DicUtils.dicLog("권한 허가");
                } else {
                    DicUtils.dicLog("권한 거부");
                    Toast.makeText(this, "파일 권한이 없기 때문에 파일 내보내기, 가져오기를 할 수 없습니다.\n만일 권한 팝업이 안열리면 '다시 묻지 않기'를 선택하셨기 때문입니다.\n어플을 지우고 다시 설치하셔야 합니다.", Toast.LENGTH_LONG).show();
                }
                return;
        }
    }

    //뷰의 내용이 변경되었을때...
    public void setChangeViewPaper(int position) {
        try {
            fab.setVisibility(View.GONE);

            if ( adapter.getItem(position) == null ) {
                return;
            }

            if (position == 0) {
            } else if (position == 1) {
                //클릭단어
            } else if (position == 2) {
                //북마크
            } else if (position == 3) {
                //단어장
                fab.setVisibility(View.VISIBLE);
                if ( ((VocabularyFragment) adapter.getItem(position)) != null ) {
                    ((VocabularyFragment) adapter.getItem(position)).changeListView();
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            DicUtils.dicLog(e.toString());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    private View.OnClickListener mPagerListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String text = ((Button) v).getText().toString();
            Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 상단 메뉴 구성
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        ((MenuItem)menu.findItem(R.id.action_edit)).setVisible(false);
        ((MenuItem)menu.findItem(R.id.action_exit)).setVisible(false);

        if ( selectedTab == 1 || selectedTab == 2 ) {
            if ( isEditing ) {
                ((MenuItem)menu.findItem(R.id.action_exit)).setVisible(true);
            } else {
                ((MenuItem)menu.findItem(R.id.action_edit)).setVisible(true);
            }
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            if ( selectedTab == 0 ) {
                bundle.putString("SCREEN", "NEWS");
            } else if ( selectedTab == 1 ) {
                bundle.putString("SCREEN", "CLICKWORD");
            } else if ( selectedTab == 2 ) {
                bundle.putString("SCREEN", "BOOKMARK");
            } else if ( selectedTab == 3 ) {
                bundle.putString("SCREEN", "VOCABULARY");
            }

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else if (id == R.id.action_patch) {
            Intent intent = new Intent(getApplication(), PatchActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtras(bundle);

            startActivity(intent);
        } else if (id == R.id.action_edit) {
            isEditing = true;
            invalidateOptionsMenu();

            if ( selectedTab == 1 ) {
                ((ClickwordFragment) adapter.getItem(selectedTab)).changeEdit(isEditing);
            } else if ( selectedTab == 2 ) {
                ((BookmarkFragment) adapter.getItem(selectedTab)).changeEdit(isEditing);
            }
        } else if (id == R.id.action_exit) {
            isEditing = false;
            invalidateOptionsMenu();

            if ( selectedTab == 1 ) {
                ((ClickwordFragment) adapter.getItem(selectedTab)).changeEdit(isEditing);
            } else if ( selectedTab == 2 ) {
                ((BookmarkFragment) adapter.getItem(selectedTab)).changeEdit(isEditing);
            }
        } else if (id == R.id.action_share) {
            Intent msg = new Intent(Intent.ACTION_SEND);
            msg.addCategory(Intent.CATEGORY_DEFAULT);
            msg.putExtra(Intent.EXTRA_SUBJECT, "최고의 영어신문 어플");
            msg.putExtra(Intent.EXTRA_TEXT, "영어.. 참 어렵죠? '최고의 영어신문' 어플을 사용해 보세요. https://play.google.com/store/apps/details?id=com.sleepingbear.ennewsvoc ");
            msg.setType("text/plain");
            startActivity(Intent.createChooser(msg, "어플 공유"));
        } else if (id == R.id.action_settings) {
            startActivityForResult(new Intent(getApplication(), SettingsActivity.class), CommConstants.a_setting);

            return true;
        } else if (id == R.id.action_no_ad) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.sleepingbear.pennewsvoc")));
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        DicUtils.dicLog("onActivityResult : " + requestCode + " : " + resultCode);

        switch ( requestCode ) {
            case CommConstants.a_vocabulary :
                ((VocabularyFragment) adapter.getItem(3)).changeListView();
                break;
            case CommConstants.a_setting :
                ((VocabularyFragment) adapter.getItem(3)).changeListView();
                break;
        }
    }

    public void changeListView() {
        if ( selectedTab == 1 ) {
            ((ClickwordFragment) adapter.getItem(selectedTab)).changeListView();
        } else if ( selectedTab == 2 ) {
            ((BookmarkFragment) adapter.getItem(selectedTab)).changeListView();
        } else if ( selectedTab == 3 ) {
            ((VocabularyFragment) adapter.getItem(selectedTab)).changeListView();
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        //MultiDex.install(this);
    }

    private long backKeyPressedTime = 0;
    @Override
    public void onBackPressed() {
        //종료 시점에 변경 사항을 기록한다.
        if ( "Y".equals(DicUtils.getDbChange(getApplicationContext())) ) {
            DicUtils.writeNewInfoToFile(this, db, "");
            DicUtils.clearDbChange(this);
        }

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();

            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }
}

class MainPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public MainPagerAdapter(FragmentManager fm, AppCompatActivity activity) {
        super(fm);

        mFragmentList.add(new NewsFragment());
        mFragmentTitleList.add("뉴스");

        mFragmentList.add(new ClickwordFragment());
        mFragmentTitleList.add("클릭 단어");

        mFragmentList.add(new BookmarkFragment());
        mFragmentTitleList.add("북마크");

        mFragmentList.add(new VocabularyFragment());
        mFragmentTitleList.add("단어장");
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public Fragment getItem(int position) {
        //DicUtils.dicLog(this.getClass().toString() + " getItem" + " : " + position);
        return mFragmentList.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}


/*
//소프트 키보드 없애기
   InputMethodManager imm= (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
   imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

 */