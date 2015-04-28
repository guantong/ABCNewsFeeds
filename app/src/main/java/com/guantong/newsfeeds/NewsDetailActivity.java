package com.guantong.newsfeeds;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class NewsDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WebView webView;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        // Get the intent, setup ImageView and the ActionBar title
        NewsEntity newsEntity = (NewsEntity)(getIntent().getSerializableExtra("result"));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(newsEntity.getArticleUrl());
        setTitle(newsEntity.getArticleTitle());

    }
}
