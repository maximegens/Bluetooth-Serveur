package com.bleutoothserveur.maxime.bluetooth_serveur.asyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Asyntask envoyant les données sur le serveur.
 */
public class SendDataTask extends AsyncTask<View, Integer, Void> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("---- SendDataTask", "Début du traitement asynchrone");
    }

    @Override
    protected void onProgressUpdate(Integer... values){
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(View... v) {
        // send(v);
        Log.v("---- SendDataTask", "Traitement en arriere plan en cours");
        for(int i = 0; i<10000000;i++){}
        return null;
    }

    @Override
    protected void onPostExecute(Void feed) {
        Log.v("---- SendDataTask","Traitement en arriere plan est terminé");
        // TODO: check this.exception
        // TODO: do something with the feed
    }


    /**
     * Envoie les données au serveur.
     * @param v La view
     */
    private void send(View v)
    {
        // get the message from the message text box
        String msg = "test";

        // make sure the fields are not empty
        if (msg.length()>0)
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://yourwebsite.com/yourPhpScript.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("id", "12345"));
                nameValuePairs.add(new BasicNameValuePair("message", msg));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                httpclient.execute(httppost);
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }

        }

    }
}


