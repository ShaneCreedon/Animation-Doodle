package ca326.com.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static ca326.com.activities.MyCardAdapter.rateValue;
import static ca326.com.activities.Profile_Screen.deciding_string;

public class Top_Rated_Screen extends AppCompatActivity implements MyCardAdapter.ItemClickListener {

    //list of videos
    public static List<Video> listVideos;
    public static Integer position2 = 10;
    public static Integer position =0;
    public static Integer rating_counter;

    public static Float number;

    public static Float averageRating;
    //Creating Views
    private RecyclerView recyclerView;
    private MyCardAdapter adapter;

    //Volley Request Queue
    private RequestQueue requestQueue;

    //This will be used to get the page number, so a number
    // are loaded and then when you scroll more get loaded
    private int pageCount = 1;
    private String newUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_rated__screen);

        //Initializing Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Initializing video list
        listVideos = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(this);

        //method to retrieve data from database
        getData();

        //initializing our adapter with list of videos
        adapter = new MyCardAdapter(listVideos, this);

        adapter.setClickListener(this);
        //Add adapter to recyclerview
        recyclerView.setAdapter(adapter);

        // FRAGMENT OPERATIONS
        // Start Fragment Operations
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = ItemOneFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                selectedFragment = ItemTwoFragment.newInstance();
                                break;
                            case R.id.action_item3:
                                selectedFragment = ItemThreeFragment.newInstance();
                                break;
                        }
                        String tag = selectedFragment.getTag();
                        replaceFragmentWithAnimation(selectedFragment, tag);

                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, ItemOneFragment.newInstance());
        transaction.commit();
    }

    public void replaceFragmentWithAnimation(android.support.v4.app.Fragment fragment, String tag){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.exit_to_right, R.anim.enter_from_left, R.anim.enter_from_right, R.anim.exit_to_left);
        transaction.replace(R.id.frame_layout, fragment);
        transaction.addToBackStack(tag);
        transaction.commit();
    }

    private JsonArrayRequest getVideoFromDB(int pageCount) {
        //Initializing ProgressBar
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Displaying Progressbar
        progressBar.setVisibility(View.VISIBLE);
        setProgressBarIndeterminateVisibility(true);

        //set up jsonArrayRequest as the data retrieved using PHP script will be in a list format
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest("http://animationdoodle2017.com/video_db.php?page=" + String.valueOf(pageCount),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        parseData(response);
                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                });

        //Returning the request
        return jsonArrayRequest;
    }


    private void getData() {
        //Adding the method to the queue by calling the method getVideoFromDB
        requestQueue.add(getVideoFromDB(pageCount));
        //Incrementing the page count
        pageCount++;
    }



    //used to parse the json data returned by the php script
    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            //Creating the video object
            Video video = new Video();
            JSONObject json = null;
            try {
                //Getting json
                json = array.getJSONObject(i);

                //Adding data to the video object
                String ratingCounterTemp = (json.getString("rating counter"));
                rating_counter = Integer.parseInt(ratingCounterTemp);

                String rating =(json.getString("rating"));
                number = Float.parseFloat(rating);
                Log.i("rating value","average is " + averageRating);
                video.setRating(number);
                video.setImageUrl(json.getString("image"));
                video.setVideoUrl(json.getString("video"));
                video.setName(json.getString("name"));
                video.setDescription(json.getString("video description"));
                video.setRatingCounter(rating_counter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Add to the list
            listVideos.add(video);
        }

        //add to adapter
        adapter.notifyDataSetChanged();
    }

    public void rating(View view){
        Video video = listVideos.get(position);
        newUrl = video.getVideoUrl();
        rating_counter = video.getRatingCounter();
        number = video.getRating();
        averageRating = (number * rating_counter);
        Toast.makeText(getApplicationContext(), rateValue, Toast.LENGTH_SHORT).show();
        Log.i("rating value","value is " + rateValue);
        changeRating(rateValue);

        }

    public void changeRating(String rateValue){
        Float ratingInt;
        rating_counter ++;
        Log.i("rating value","average2 is " + averageRating);
        if (!rateValue.equals("0.0")){
            ratingInt = (averageRating + Float.parseFloat(rateValue)) / rating_counter;
            Log.i("rating value","new rating is " + ratingInt);
        }
        else {
            ratingInt = averageRating / rating_counter;
            Log.i("rating value","new rating 2 is " + ratingInt);
        }
        //insert ratingInt into the rating column in database

        new updateRatingValue(this).execute(ratingInt);

    }

    public class updateRatingValue extends AsyncTask<Float, Void, String> {

        Activity instance;

        updateRatingValue(Activity instance) {
            this.instance = instance;

        }


        @Override
        protected String doInBackground(Float... arg0) {

            Float rate = arg0[0];
            String link;

            try {
                link = "http://animationdoodle2017.com/updateRating.php";
                URL url = new URL(link);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);
                OutputStream out = con.getOutputStream();

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
                String post_data = URLEncoder.encode("rating", "UTF-8") + "=" + rate +"&"+
                        URLEncoder.encode("video","UTF-8")+"="+URLEncoder.encode(newUrl,"UTF-8") +"&"+
                        URLEncoder.encode("ratingCounter","UTF-8")+"="+ rating_counter;
                writer.write(post_data);
                writer.flush();
                writer.close();
                out.close();
                InputStream in = con.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));
                String line = "";
                String result = "";
                while ((line = br.readLine()) != null) {
                    result += line;
                }
                return result;

            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            StringBuilder sb = new StringBuilder();
            sb.append(result + "\n");
            String jsonStr = sb.toString();
            Log.i("response", jsonStr);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    String query_result = jsonObj.getString("query_result");
                    if (query_result.equals("SUCCESS")) {
                        Toast.makeText(instance, "Video rated !", Toast.LENGTH_SHORT).show();
                    } else if (query_result.equals("FAILURE")) {
                        Toast.makeText(instance, "Couldn't set rating. Database error", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(instance, "Couldn't set rating2.", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(instance, "Couldn't set rating. Check internet connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(instance, "Couldn't set rating. Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        }
    }





    public void onItemClick(View view, int position) {
        this.position = position;
        deciding_string = "topRated";
        Intent intent = new Intent (Top_Rated_Screen.this, Test_VideoPlayer.class);
        startActivity(intent);

    }

}