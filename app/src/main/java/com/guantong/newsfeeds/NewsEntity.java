package com.guantong.newsfeeds;

import java.io.Serializable;

/**
 * Created by Alien on 4/24/2015.
 */
public class NewsEntity implements Serializable {
    private String articleTitle;
    private String articleDesc;
    private SerialBitmap articleImageUrl;
    private String articleUrl;

    public NewsEntity (String articleTitle, String articleDesc, SerialBitmap articleImageUrl, String articleUrl){
        this.articleTitle = articleTitle;
        this.articleDesc = articleDesc;
        this.articleImageUrl = articleImageUrl;
        this.articleUrl = articleUrl;
    }

    public void setArticleImageUrl(SerialBitmap articleImageUrl) {
        this.articleImageUrl = articleImageUrl;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public void setArticleDesc(String articleDesc) {
        this.articleDesc = articleDesc;
    }

    public void setArticleUrl(String articleUrl) {
        this.articleUrl = articleUrl;
    }

    public String getArticleDesc() {
        return articleDesc;
    }

    public String getArticleUrl() {
        return articleUrl;
    }

    public SerialBitmap getArticleImageUrl() {
        return articleImageUrl;
    }

    public String getArticleTitle(){
        return articleTitle;
    }
}
