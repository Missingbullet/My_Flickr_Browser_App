package com.missingbullet.rschatzand310finalproj;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by robschatz on 5/30/16.
 */

//By extending GetRawData we are re-using some of the functionality that we created in the GetRawData class in order to
//process the JSON data. It's Inheritance, like Trump inheriting the wealth his Dad slaved for lol

public class GetFlickrJsonData extends GetRawData {

    private String LOG_TAG = GetFlickrJsonData.class.getSimpleName();

    //This is going to store the photos we get from the GetRawData
    private List<Photo> mPhotos;
    private Uri mDestinationUri;

    //If matchAll = true then the tagmode=ALL, if matchAll = false, then tagmode=ANY
    // see https://www.flickr.com/services/feeds/docs/photos_public/
    public GetFlickrJsonData (String searchCriteria, boolean matchAll){
        super(null);
        createAndUpdateUri(searchCriteria,matchAll);
        mPhotos = new ArrayList<Photo>();
    }

    public void execute(){
        super.setmRawUrl(mDestinationUri.toString());
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.v(LOG_TAG, "Built URI = " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String searchCriteria, boolean matchAll){
        //This final string will use the base URL from flickr so that CLASS will plug in the parameters
        //automatically like the ampersands and question marks
        final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";

        //Now we specify the query string params stated in the requirements here https://www.flickr.com/services/feeds/docs/photos_public/
        final String TAGS_PARAM = "tags";
        final String TAGMODE_PARAM = "tagsmode";
        final String FORMAT_PARAM = "format";
        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";

        //this method will be taking the data gotten above and parsing it
        mDestinationUri = Uri.parse(FLICKR_API_BASE_URL).buildUpon()
            .appendQueryParameter(TAGS_PARAM, searchCriteria)

                //Tag re-operator that is allowing to use booolean-like functionality.
                // The "?" is like an IF so IF matchAll is ALL or if not ":" then it is ANY
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();

        //We set this to a boolean since that's what we did above in public boolean createAndUpdateUri
        //If we do get something back as null then it's likely there's an error with the API (maybe)
        return mDestinationUri != null;
    }

    public void processResult(){
        if(getmDownloadStatus() != DownloadStatus.OK){
            Log.e(LOG_TAG, "Error downloading raw file" );
            return;
        }

        final String FLICKR_ITEMS = "items";
        final String FLICKR_TITLE = "title";
        final String FLICKR_MEDIA = "media";
        final String FLICKR_PHOTO_URL = "m";
        final String FLICKR_AUTHOR = "author";
        final String FLICKR_AUTHOR_ID = "author id";
        final String FLICKR_LINK = "link";
        final String FLICKR_TAGS = "tags";

        try {

            //mData is in GetRawData
            JSONObject jsonData = new JSONObject(getmData());

            //itemsArray will have all of the items from the photo
            JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS);

            //now we are going to iterate through each of the photo objects
            for(int i=0; i<itemsArray.length(); i++ ){
                JSONObject jsonPhoto =itemsArray.getJSONObject(i);
                String title = jsonPhoto.getString(FLICKR_TITLE);
                String author = jsonPhoto.getString(FLICKR_AUTHOR);
                String authorId = jsonPhoto.getString(FLICKR_AUTHOR_ID);
                String link = jsonPhoto.getString(FLICKR_LINK);
                String tags = jsonPhoto.getString(FLICKR_TAGS);

                JSONObject jsonMedia = jsonPhoto.getJSONObject(FLICKR_MEDIA);
                String photoUrl = jsonMedia.getString(FLICKR_PHOTO_URL);

                Photo photoObject = new Photo(title, author, authorId, link, tags, photoUrl); //photoUrl is the image
                this.mPhotos.add(photoObject);
            }

            for (Photo singlePhoto: mPhotos) {
                Log.v(LOG_TAG, singlePhoto.toString()); //the toString method was created in the Photo class object public String toString()
            }

        }catch(JSONException jsone){
            jsone.printStackTrace();
         Log.e(LOG_TAG, "Error processing json data");
        }
    }

    //See the DownloadRawData in the GetRawData class
    public class DownloadJsonData extends DownloadRawData {
        protected void onPostExecute (String webData){
            super.onPostExecute(webData);
            processResult();
        }

        //This method is going into the GetRawData class and using the DoInBackground method of that class to download the raw json data FILE
        protected String doInBackground(String...params){
            return super.doInBackground(params);
        }


    }
}
