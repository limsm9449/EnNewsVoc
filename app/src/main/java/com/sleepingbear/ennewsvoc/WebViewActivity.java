package com.sleepingbear.ennewsvoc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.ClipboardManager;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class WebViewActivity extends AppCompatActivity {
    public SQLiteDatabase mDb;
    public ArrayAdapter urlAdapter;
    private WebView webView;
    private TextView mean;
    private RelativeLayout meanRl;
    private Bundle param;
    private String oldUrl = "";

    private ProgressDialog mProgress;

    private ActionMode mActionMode = null;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_webview);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        param = getIntent().getExtras();

        ActionBar ab = (ActionBar) getSupportActionBar();
        ab.setTitle(param.getString("name"));
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        mDb = (new DbHelper(this)).getWritableDatabase();

        //하단 뜻 영역을 숨김
        meanRl = (RelativeLayout) this.findViewById(R.id.my_c_webview_rl);
        meanRl.setVisibility(View.GONE);
        meanRl.setClickable(true);  //클릭시 하단 광고가 클릭되는 문제로 rl이 클릭이 되게 해준다.
        mean = (TextView) this.findViewById(R.id.my_c_webview_mean);

        webView = (WebView) this.findViewById(R.id.my_c_webview_wv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "HybridApp");
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        //webView.setContextClickable(true);
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(param.getString("url"));
        DicUtils.dicLog("First : " + param.getString("url"));

        //registerForContextMenu(webView);

        AdView av = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new  AdRequest.Builder().build();
        av.loadAd(adRequest);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // 상단 메뉴 구성
        getMenuInflater().inflate(R.menu.menu_help, menu);

        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu){
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        //Webview Context Menu를 가져온다.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_webview, menu);

        //클릭시 onContextItemSelected를 호출해주도록 이벤트를 걸어준다.
        MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onContextItemSelected(item);
                return true;
            }
        };
        for (int i = 0, n = menu.size(); i < n; i++) {
            menu.getItem(i).setOnMenuItemClickListener(listener);
        }
    }
    */

    /*
    @Override
    public boolean onContextItemSelected(MenuItem item){
        super.onContextItemSelected(item);

        switch(item.getItemId()){
            case R.id.action_copy:
                Toast.makeText(this,"action_copy",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_word_view:
                Toast.makeText(this,"action_word_view",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_sentence_view:
                Toast.makeText(this,"action_sentence_view",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_tts:
                Toast.makeText(this,"action_tts",Toast.LENGTH_SHORT).show();
                return true;
            default:
                super.onContextItemSelected(item);
        }

        return false;
    }
    */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_help) {
            Bundle bundle = new Bundle();
            bundle.putString("SCREEN", "WEB_VIEW");

            Intent intent = new Intent(getApplication(), HelpActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if ( webView.canGoBack() ) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getUrlSource(final String site)  {
        DicUtils.dicLog(site);

        new Thread(new Runnable() {
            public void run() {
                StringBuilder a = new StringBuilder();
                try {
                    //GNU Public, from ZunoZap Web Browser
                    URL url = new URL(site);
                    URLConnection urlc = url.openConnection();
                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            urlc.getInputStream(), "UTF-8"));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        a.append(inputLine);
                        DicUtils.dicLog(inputLine);
                    }
                    in.close();
                } catch ( Exception e ) {
                    DicUtils.dicLog(e.toString());
                }
            }
        }).start();
    }

    @Override
    public void onActionModeStarted(ActionMode mode) {
        DicUtils.dicLog("onActionModeStarted");
        if (mActionMode == null) {
            mActionMode = mode;
            Menu menu = mode.getMenu();
            // Remove the default menu items (select all, copy, paste, search)
            menu.clear();

            // If you want to keep any of the defaults,
            // remove the items you don't want individually:
            // menu.removeItem(android.R.id.[id_of_item_to_remove])

            // Inflate your own menu items
            mode.getMenuInflater().inflate(R.menu.menu_webview, menu);

            //클릭시 onContextItemSelected를 호출해주도록 이벤트를 걸어준다.
            MenuItem.OnMenuItemClickListener listener = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    onContextualMenuItemClicked(item);
                    return true;
                }
            };
            for (int i = 0, n = menu.size(); i < n; i++) {
                menu.getItem(i).setOnMenuItemClickListener(listener);
            }
        }

        super.onActionModeStarted(mode);
    }

    // This method is what you should set as your item's onClick
    // <item android:onClick="onContextualMenuItemClicked" />
    public void onContextualMenuItemClicked(MenuItem item) {
        DicUtils.dicLog("onContextualMenuItemClicked");
        switch (item.getItemId()) {
            case R.id.action_copy:
                Toast.makeText(this,"action_copy",Toast.LENGTH_SHORT).show();
                // do some stuff
                break;
            case R.id.action_word_view:
                Toast.makeText(this,"action_word_view",Toast.LENGTH_SHORT).show();
                // do some different stuff
                break;
            case R.id.action_sentence_view:
                Toast.makeText(this,"action_sentence_view",Toast.LENGTH_SHORT).show();
                // do some different stuff
                break;
            case R.id.action_tts:
                Toast.makeText(this,"action_tts",Toast.LENGTH_SHORT).show();
                // do some different stuff
                break;
            default:
                // ...
                break;
        }

        // This will likely always be true, but check it anyway, just in case
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        DicUtils.dicLog("onActionModeFinished");
        mActionMode = null;
        super.onActionModeFinished(mode);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            //return super.shouldOverrideUrlLoading(view, url);

            //The New Work Times 에서 다음 url을 호출할때 화면이 안나오는 문제가 있음
            if ( "data:text/html,".equals(url) ) {
                return false;
            } else {
                DicUtils.dicLog("url = " + url);
                view.loadUrl(url);

                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if (mProgress == null) {
                mProgress = new ProgressDialog(WebViewActivity.this);
                mProgress.setIndeterminate(true);
                mProgress.setCancelable(false);
                mProgress.show();
                mProgress.setContentView(R.layout.custom_progress);
                mProgress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            }

            DicUtils.dicLog("onPageStarted : " + url);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);

            if (mProgress.isShowing()) {
                mProgress.dismiss();
                mProgress = null;
            }

            DicUtils.dicLog("onReceivedError : " + error.toString());
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            DicUtils.dicLog("onPageFinished : " + url);

            //중복으로 호출이 되지 않도록
            if ( oldUrl.equals(url) ) {
                return;
            } else {
                if ( !"data:text/html,".equals(url) ) {
                    oldUrl = url;
                    DicUtils.dicLog("onPageFinished : " + url);

                    // CNN은 $를 사용안하고 jQuery를 사용한다.
                    String js1 = ".html(function(index, oldHtml) {return oldHtml.replace(/<br *\\/?>/gi, '\\n')" +
                            ".replace(/<[^>]*>/g, '')" +
                            ".replace(/(<br>)/g, '\\n')" + "" +
                            ".replace(/\\b(\\w+?)\\b/g,'<span class=\"word\">$1</span>')" +
                            ".replace(/\\n/g, '<br>')});";
                    String js2 = "('.word').click(function(event) { window.HybridApp.setWord(event.target.innerHTML) });";

                    //html 단어 기능 변경
                    String[] changeClass = param.getStringArray("changeClass");
                    for (int i = 0; i < changeClass.length; i++) {
                        if ( "$".equals(changeClass[i].substring(0, 1)) ) {
                            webView.loadUrl("javascript:" + changeClass[i] + js1 + "$" + js2);
                            DicUtils.dicLog("javascript:" + changeClass[i] + js1 + "$" + js2);
                        } else {
                            webView.loadUrl("javascript:" + changeClass[i] + js1 + "jQuery" + js2);
                            DicUtils.dicLog("javascript:" + changeClass[i] + js1 + "jQuery" + js2);
                        }
                    }

                    //광고 제거
                    String[] removeClass = param.getStringArray("removeClass");
                    for (int i = 0; i < removeClass.length; i++) {
                        if ( "$".equals(removeClass[i].substring(0, 1)) ) {
                            webView.loadUrl("javascript:" + removeClass[i] + ".html('')");
                            DicUtils.dicLog("javascript:" + removeClass[i] + ".html('')");
                        } else {
                            webView.loadUrl("javascript:" + removeClass[i] + ".html('')");
                            DicUtils.dicLog("javascript:" + removeClass[i] + ".html('')");
                        }
                    }

                    if (mProgress.isShowing()) {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                }
            }
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setWord(final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    meanRl.setVisibility(View.VISIBLE);

                    mean.setText(arg + " : " + DicDb.getMean(mDb, arg));
                    //Toast.makeText(WebViewActivity.this, arg + " : " + DicDb.getMean(mDb, arg), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

//http://stackoverflow.com/questions/6058843/android-how-to-select-texts-from-webview
/*
webView.loadUrl("javascript:window.HybridApp.setMessage(window.getSelection().toString())");
 */