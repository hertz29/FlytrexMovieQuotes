package com.hfad.flytrexmoviequotes;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    private static final String FLYTREX_SERVER = "http://ec2-52-88-173-47.us-west-2.compute.amazonaws.com:8000/moviequotes/";
    private Button mGetQuotesButton;
    private TextView mTextMessage1;
    private TextView mTextMessage2;
    private TextView mTextMessage3;
    private TextView mTextMessage4;
    private TextView mTextMessage5;
    private TextView mLog;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        mGetQuotesButton = (Button) findViewById(R.id.get_quoets_button);
        mTextMessage1 = (TextView) findViewById(R.id.message_view1);
        mTextMessage2 = (TextView) findViewById(R.id.message_view2);
        mTextMessage3 = (TextView) findViewById(R.id.message_view3);
        mTextMessage4 = (TextView) findViewById(R.id.message_view4);
        mTextMessage5 = (TextView) findViewById(R.id.message_view5);
        mLog = (TextView) findViewById(R.id.log);
    }


    public void getQuotes(View v){
        /*
        getQuotes method first check-up the device service connection.
        if there is service connection, a DownloadQuotesFromServer AsyncTask is created and executed
         */
        clearTextViews();
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadQuotesFromServer(this).execute(FLYTREX_SERVER);
        } else {
            mLog.setText("No network connection available.");
        }

    }

    private void clearTextViews() {
        mTextMessage1.setText(" ");
        mTextMessage2.setText(" ");
        mTextMessage3.setText(" ");
        mTextMessage4.setText(" ");
        mTextMessage5.setText(" ");
        mLog.setText(" ");
    }


    public class DownloadQuotesFromServer extends AsyncTask<String, Void, byte[]> {
        Context ctx;
        private int mConnectionFailedCount;
        private boolean mIsConn =false;
        byte[] contentAsString = {};


        DownloadQuotesFromServer(Context ctx) {
            this.ctx = ctx;
            mConnectionFailedCount = 0;
        }


        @Override

        protected byte[] doInBackground(String... params) {
            String stringUrl = params[0];
            try{
                contentAsString = downloadQuotes(stringUrl); // a HttpUrlConnection is initialized
            } catch (IOException e) {
                e.printStackTrace();
            }
            return contentAsString;
        }

        private byte[] downloadQuotes(String myurl)throws IOException {
            InputStream is = null;

            int responseCode = 0;
            try {
                URL url = new URL(myurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                mIsConn = true;
                contentAsString = readFromStream(is); // store the binary data in a byte array
                return contentAsString;
            } catch (Exception e) {
                //is == null
                if (mConnectionFailedCount < 4 && !mIsConn) {
                    mConnectionFailedCount++;
                    return downloadQuotes(myurl);
                }
                e.printStackTrace();
            }
             finally {

                if (is != null) {
                    is.close();
                }
                return contentAsString;
            }
        }


        public byte[] readFromStream(InputStream inputStream) throws Exception {
            // readFromStream read the data with the inputStream object, and store it in byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            byte[] data = new byte[4096];
            int count = inputStream.read(data);
            while(count != -1)
            {
                dos.write(data, 0, count);
                count = inputStream.read(data);
            }
            return baos.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] result) {
            // create FlytrexQuotes from the data that received from the server and stored them in a FlytrexQuoteContainer

            FlytrexQuoteContainer quotesontainer = new FlytrexQuoteContainer();
            quotesontainer.parseBinaryData(result); // parse the data
            ArrayList<FlytrexQuote> quotesArr = quotesontainer.getQuotes();
            printQuotes(quotesArr);
            mConnectionFailedCount=0;

        }

        private void printQuotes(ArrayList<FlytrexQuote> quotesArr) {
            if(quotesArr.isEmpty()){
                mLog.setText("Connection Failed, Please Try Again");
            }
            else {
                mTextMessage1.setText(quotesArr.get(0).toString());
                if (quotesArr.get(0).getIsSigCorrect()) mTextMessage1.setTextColor(Color.RED);
                mTextMessage2.setText(quotesArr.get(1).toString());
                if (quotesArr.get(1).getIsSigCorrect()) mTextMessage2.setTextColor(Color.RED);
                mTextMessage3.setText(quotesArr.get(2).toString());
                if (quotesArr.get(2).getIsSigCorrect()) mTextMessage3.setTextColor(Color.RED);
                mTextMessage4.setText(quotesArr.get(3).toString());
                if (quotesArr.get(3).getIsSigCorrect()) mTextMessage4.setTextColor(Color.RED);
                mTextMessage5.setText(quotesArr.get(4).toString());
                if (quotesArr.get(4).getIsSigCorrect()) mTextMessage5.setTextColor(Color.RED);
            }
        }



    }

}

