package com.guantong.newsfeeds;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


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

        mResults = new ArrayList<>();
        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Using the Transition framework that was made available in Lollipop
                Intent i = new Intent(NewsActivity.this, NewsDetailActivity.class);
                i.putExtra("result", mResults.get(position));
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(NewsActivity.this, view, "imageTransition");
                startActivity(i, options.toBundle());
            }
        });
        checkNetworkError();
    }

    private void checkNetworkError(){
        final NewsDownloadJSONTask t = new NewsDownloadJSONTask();
        t.execute();
//        // Start download request
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    t.execute().get(300000, TimeUnit.MILLISECONDS);;
//                } catch (Exception  e) {
//                    t.cancel(true);
//                    (NewsActivity.this).runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(NewsActivity.this,"Network Error Please check your internet connection",Toast.LENGTH_LONG).show();
//                            checkNetworkError();
//                        }
//                    });
//                    e.printStackTrace();
//                }
//            }
//        });
////        thread.start();

    }
    private class NewsDownloadJSONTask extends AsyncTask<Void, Void, Void> {
        JSONObject json;
        ProgressBar progressBar;

        @Override
        protected Void doInBackground(Void... voids) {
                // Request our JSON data
                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(ABC_NEWS_FEED_URI);

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

                //change here according to valid jason objects
                for(int i = 0; i < 4; i++) {
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
                cancel(true);
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