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
import android.view.KeyEvent;
import android.view.Menu;
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
        mean = (TextView) this.findViewById(R.id.my_c_webview_mean);

        webView = (WebView) this.findViewById(R.id.my_c_webview_wv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "HybridApp");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl(param.getString("url"));
        DicUtils.dicLog("First : " + param.getString("url"));

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
                mProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgress.setTitle("알림");
                mProgress.setMessage("페이지 로딩 및 변환 중입니다.\n잠시만 기다려 주세요.");
                mProgress.setCancelable(false);
                mProgress.setButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mProgress.dismiss();
                        mProgress = null;
                    }
                });
                mProgress.show();
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

                    //html 변경
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