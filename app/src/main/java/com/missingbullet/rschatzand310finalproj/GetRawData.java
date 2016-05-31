package com.missingbullet.rschatzand310finalproj;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by robschatz on 5/29/16.
 */

enum DownloadStatus {IDLE, PROCESSING, NOT_INITALIZED, FAILED_OR_EMPTY, OK; }


public class GetRawData {
    private String LOG_TAG = GetRawData.class.getSimpleName();
    private String mRawUrl;
    private String mData;
    private DownloadStatus mDownloadStatus;

    public GetRawData(String mRawUrl) {
        this.mRawUrl = mRawUrl;

        //this will initialize the state of the data download, which will always be idle
        this.mDownloadStatus = DownloadStatus.IDLE;
    }

    //A reset method to rest the data
    public void reset() {
        this.mDownloadStatus = DownloadStatus.IDLE;
        this.mRawUrl = null;
        this.mData = null;
    }

    public String getmData() {
        return mData;
    }

    public DownloadStatus getmDownloadStatus() {
        return mDownloadStatus;
    }

    //this setter is necessary so that the URL is properly set and in the correct format
    public void setmRawUrl(String mRawUrl) {
        this.mRawUrl = mRawUrl;
    }

    public void execute() {
        this.mDownloadStatus = DownloadStatus.PROCESSING;
        DownloadRawData downloadRawData = new DownloadRawData();
        downloadRawData.execute(mRawUrl);
    }

    public class DownloadRawData extends AsyncTask<String, Void, String> {

        protected void onPostExecute(String webData){
            //Here is where the data that is returned will be stored
            mData = webData;
            Log.v(LOG_TAG, "Data returned was: " +mData);

            //Here we're going to make sure to do some testing to let us know why an exception occurred, in this case
            //because the data was not initialized
            if(mData == null) {
                if (mRawUrl == null) {
                    mDownloadStatus = DownloadStatus.NOT_INITALIZED;
                } else {
                    mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
                }
                //And if no error occurred then we have successfully loaded the data - yay!
            } else {
                mDownloadStatus = DownloadStatus.OK;
            }

        }

        protected String doInBackground(String... params){

            HttpURLConnection urlConnection = null;
            BufferedReader reader  = null;

            if (params == null)
                    return null;

            try {

                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                if (inputStream == null){
                    return  null;
                }

                //This string buffer is going to store our data that it gets from the urlConnection
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                //this variable stores each line of text
                String line;

                //this will test to see if we are at the end of the variable "line"
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();


            } catch(IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;

            } finally {
                //This will make sure we are disconnecting from the server so that the app doesn't remain connected.
                //Best practice = connection clean-up
                if (urlConnection !=null) {
                    urlConnection.disconnect();
                    if(reader != null){
                        try {
                            reader.close();
                        } catch (final IOException e){
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }


            }
        }
    }
}
