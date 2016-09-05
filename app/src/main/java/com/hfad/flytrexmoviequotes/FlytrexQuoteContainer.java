package com.hfad.flytrexmoviequotes;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hertz on 04/09/2016.
 */
public class FlytrexQuoteContainer {

    private ArrayList<FlytrexQuote> mQuotes;

    public FlytrexQuoteContainer(){
        mQuotes = new ArrayList<FlytrexQuote>();
    }

    public void parseBinaryData(byte[] result) {
        int i = 0;
        while(i<result.length){
            int size = result[i+1];
            mQuotes.add(new FlytrexQuote(Arrays.copyOfRange(result, i, i + size)));
            i += size;
        }
    }

    public ArrayList<FlytrexQuote> getQuotes(){
        return mQuotes;
    }

}
