package com.guantong.newsfeeds;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;


public class NewsActivity extends Activity {

    private static final String ABC_NEWS_FEED_URI
            = "http://ajax.googleapis.com/ajax/services/feed/load?v=1.0&q=http://www.abc.net.au/news/feed/51120/rss.xml&num=-1";

    private ListView listView;
    private ArrayList<NewsEntity> mResults;
    private NewsAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Using the Transition framework that was made available in Lollipop
                Intent i = new Intent(NewsActivity.this, NewsDetailActivity.class);
                i.putExtra("result", mResults.get(position));
                //ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NewsActivity.this, view, "imageTransition");
                startActivity(i);
            }
        });

        // Start download request
        mResults = new ArrayList<>();
        new NewsDownloadJSONTask().execute();
    }

    private class NewsDownloadJSONTask extends AsyncTask<Void, Void, Void> {
        JSONObject json;
        ProgressBar progressBar;

        @Override
        protected Void doInBackground(Void... voids) {

//            try {
                // Request our JSON data
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(ABC_NEWS_FEED_URI);
//            }
//            catch (HttpException http)
//            {
//
//            }

            try {
                // Execute the GET request and get input
                HttpResponse response = client.execute(request);
                InputStream input = response.getEntity().getContent();

                // Parse input into a String, line by line
                String content = "";
                StringWriter writer = new StringWriter();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder sb = new StringBuilder();
                while ((content = reader.readLine()) != null) {
                    sb.append(content);
                }

                // Set our JSON object by passing the StringBuilder result
                //System.out.println(sb.toString());

                // Need to remove a 'function' that is appended on the JSON string for some reason
                String output = sb.toString();

                json = new JSONObject(output);

                // Create an array list to hold our abc news results
                JSONObject responseData = json.getJSONObject("responseData");
                JSONObject feed = responseData.getJSONObject("feed");
                JSONArray items = feed.getJSONArray("entries");

                for(int i = 10; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);

                    // Get the article details e.g title snippet link imageUrl
                    String articleTitle = item.getString("title");
                    String articleUrl = item.getString("link");

                    JSONArray mediaGroup = item.getJSONArray("mediaGroups");
                    JSONObject group0 = mediaGroup.getJSONObject(0);
                    JSONArray contents = group0.getJSONArray("contents");
                    JSONObject content2 = contents.getJSONObject(2);

                    String articleDesc = content2.getString("description");

                    String thumbnailImageUrl = content2.getString("url");

                    // Setup network request for image and decode into Bitmap
                    request = new HttpGet(thumbnailImageUrl);
                    response = client.execute(request);
                    InputStream is = response.getEntity().getContent();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    // Create model and add to results
                    NewsEntity result = new NewsEntity(articleTitle, articleDesc, new SerialBitmap(bitmap), articleUrl);
                    mResults.add(result);
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
            return null;
        }

        // Before we retrieve the JSON, let's activate a progress bar
        @Override
        protected void onPreExecute() {
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setIndeterminate(true);

            // Hide grid view for the moment
            listView.setVisibility(View.GONE);
        }

        // Execute once our background job is complete
        @Override
        protected void onPostExecute(Void aVoid) {
            // Setup the adapter
            mAdapter = new NewsAdapter(getBaseContext(), mResults);
            listView.setAdapter(mAdapter);

            // Hide the progress bar
            progressBar.setVisibility(View.GONE);
            // Make GridView visible again
            listView.setVisibility(View.VISIBLE);

        }
    }
}