package com.ropr.mcroute;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcroute.R;
import com.ropr.mcroute.models.SessionData;
import com.ropr.mcroute.sources.StaticResources;

import java.io.Console;

/**
 * Created by NIJO7810 on 2016-05-03.
 */
public class Login_Activity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle b = getIntent().getExtras();
        String url = (String) b.get(StaticResources.EXTRA_URL);

        if (url != null && url != "") {
            WebView view = (WebView) findViewById(R.id.login_webview);
            WebSettings settings = view.getSettings();
            settings.setJavaScriptEnabled(true);

            view.addJavascriptInterface(new ScriptInterface(this), "Android");
            view.setWebChromeClient(new WebChromeClient());
            view.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
            view.loadUrl(url);
        }
    }

    public void setResult(SessionData result) {
        Intent intent = new Intent();
        intent.putExtra("result", result);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private class ScriptInterface {
        private Context context;

        public ScriptInterface(Context context) {
            this.context = context;
        }

        @JavascriptInterface
        public void authCompletedCB(String fragment) {
            try {
                Gson gson = new GsonBuilder().create();
                SessionData data = gson.fromJson(fragment, SessionData.class);
                ((Login_Activity) context).setResult(data);
            } catch (Exception ex) {
                Log.e("GSON", ex.getMessage());
                return;
            }
        }
    }
}
