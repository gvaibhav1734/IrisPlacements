package com.example.vaibhav.irisplacements;

import com.google.gson.Gson;

/**
 * GSON Singleton
 */
public class GsonHelper
{
    private static final String TAG = "GsonHelper";
    private static GsonHelper mInstance;
    private static Gson gson;

    private GsonHelper()
    {
        gson = new Gson();
    }
    public static synchronized GsonHelper getInstance()
    {
        // If Instance is null then initialize new Instance
        if(mInstance == null){
            mInstance = new GsonHelper();
        }
        // Return MySingleton new Instance
        return mInstance;
    }
    public Gson getGson()
    {
        //If gson null create a new one
        if(gson==null)
            return new Gson();
        // Return existing gson
        return gson;
    }
}
