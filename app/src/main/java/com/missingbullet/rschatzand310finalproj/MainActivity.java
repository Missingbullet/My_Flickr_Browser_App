package com.missingbullet.rschatzand310finalproj;

import android.drm.ProcessedData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;



import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "Main Activity";
    private List<Photo> mPhotosList = new ArrayList<Photo>();
    private RecyclerView mRecyclerView;
    private FlickrRecyclerViewAdapter flickrRecyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //        GetRawData theRawData = new GetRawData("https://api.flickr.com/services/feeds/photos_public.gne?tags=android,lollipop&format=json&nojsoncallback=1");
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ProcessPhotos processPhotos = new ProcessPhotos("google",true);
        processPhotos.execute();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //So after we GetRawData, process it in GetFlickrJsonData, we want a 3rd step which will put the data into the view adapter
    public class ProcessPhotos extends GetFlickrJsonData{
        //constructor
        public ProcessPhotos(String searchCriteria, boolean matchAll) {
            super(searchCriteria, matchAll);
        }
        public void execute() {
            super.execute();
            ProcessData processData = new ProcessData();
            processData.execute();
        }

        public class ProcessData extends DownloadJsonData{
            protected void onPostExecute(String webData){
                super.onPostExecute(webData);
                //Now that the data is present the adapter will be set up in theory, anyway
                flickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(MainActivity.this, getMPhotos());
                mRecyclerView.setAdapter(flickrRecyclerViewAdapter);
            }



        }
    }
}
