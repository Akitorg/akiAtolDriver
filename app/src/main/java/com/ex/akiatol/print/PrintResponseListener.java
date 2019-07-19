package com.ex.akiatol.print;

/**
 * Created by Leo on 09/04/2019.
 */
public interface PrintResponseListener {

    void onUpdateListener(String... values);
    void onPostExecute(PrintResult result);

}
