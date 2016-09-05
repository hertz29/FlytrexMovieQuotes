package com.hfad.flytrexmoviequotes;

import java.util.Arrays;

/**
 * Created by Hertz on 03/09/2016.
 */
public class FlytrexQuote {
    /*
     flytrexQuote is an object that represent a single quote.
     this class include private methods that pasre the byte array and unmasked the quote
     */
    private byte mKey; // holds the mask key
    private byte[] mBytes; // holds the masked bytes
    private byte[] mQuote; // holds the unmasked bytes
    private byte mSig;
    private boolean mIsSigCorrect = false;


    public FlytrexQuote(byte[] input){
        parseBytes(input);
        mQuote = unmaskedQuote(mKey,mBytes);
        mIsSigCorrect = checkSig(mQuote,mKey);
    }

    public boolean getIsSigCorrect(){
        return mIsSigCorrect;
    }


    private void parseBytes(byte[] input) {
        //init the object fields according to input
        mKey = input[2];
        mBytes =  Arrays.copyOfRange(input,3,input.length-1);
        mSig = input[input.length-1];
    }


    private boolean checkSig(byte[] arr,byte key){
        // operate xor between the unmasked bytes and mKey
        int result = arr[0];
        for(int i = 1; i< arr.length; i++){
            result ^= arr[i];
        }
        result^=key;
        return result != mSig;
    }

    private byte[] unmaskedQuote(int key,byte[] arr){
        // operate xor between every byte in arr with mKey
        byte[] ret = new byte[arr.length];
        for(int i = 0 ; i<arr.length; i++){
            ret[i] = (byte) (key^arr[i]);
        }
        return ret;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(new String(mQuote));
        return new String(sb);
    }


}
