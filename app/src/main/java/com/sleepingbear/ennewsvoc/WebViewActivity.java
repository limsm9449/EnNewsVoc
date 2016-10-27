package com.sleepingbear.ennewsvoc;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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
    private Bundle param;
    private String oldUrl = "";

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

        webView = (WebView) this.findViewById(R.id.my_c_webview_wv);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new AndroidBridge(), "HybridApp");
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        webView.setWebViewClient(new MyWebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                DicUtils.dicLog(url);

                //중복으로 호출이 되지 않도록
                if ( oldUrl.equals(url) ) {
                    return;
                } else {
                    oldUrl = url;

                    String js = ".html(function(index, oldHtml) {return oldHtml.replace(/<br *\\/?>/gi, '\\n')" +
                            ".replace(/<[^>]*>/g, '')" +
                            ".replace(/(<br>)/g, '\\n')" + "" +
                            ".replace(/\\b(\\w+?)\\b/g,'<span class=\"word\">$1</span>')" +
                            ".replace(/\\n/g, '<br>')});" +
                            "$('.word').click(function(event) { window.HybridApp.setWord(event.target.innerHTML) });";

                    //제목
                    webView.loadUrl("javascript:" + param.getString("titleClass") + js);

                    //content
                    webView.loadUrl("javascript:" + param.getString("contentClass") + js);
                }
            }
        });
        webView.loadUrl(param.getString("url"));

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
            DicUtils.dicLog("url = "+ url);
            view.loadUrl(url);

            return true;
        }
    }

    private class AndroidBridge {
        @JavascriptInterface
        public void setWord(final String arg) { // must be final
            handler.post(new Runnable() {
                public void run() {
                    Toast.makeText(WebViewActivity.this, arg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}

//http://stackoverflow.com/questions/6058843/android-how-to-select-texts-from-webview
/*
webView.loadUrl("javascript:window.HybridApp.setMessage(window.getSelection().toString())");
 */