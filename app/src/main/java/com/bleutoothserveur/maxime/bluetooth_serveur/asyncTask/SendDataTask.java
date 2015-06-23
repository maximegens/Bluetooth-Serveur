package com.bleutoothserveur.maxime.bluetooth_serveur.asyncTask;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bleutoothserveur.maxime.bluetooth_serveur.MainActivity;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.DevicesBluetoothUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Asyntask envoyant les données sur le serveur.
 */
public class SendDataTask extends AsyncTask<View, Integer, Boolean> {

    private Context mContext;
    private MainActivity activity;

    public SendDataTask (Context context){
        mContext = context;
        activity = (MainActivity) mContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.v("---- SendDataTask", "Début du traitement asynchrone");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Boolean doInBackground(View... v) {
        Log.v("---- SendDataTask", "Traitement en arriere plan en cours");
        return send(v[0]);
    }

    @Override
    protected void onPostExecute(Boolean resultat) {
        if(resultat){
            Toast.makeText(mContext,"Données envoyé",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(mContext,"Données non envoyé",Toast.LENGTH_SHORT).show();
        }
        Log.v("---- SendDataTask", "Traitement en arriere plan est terminé");
        // TODO: check this.exception
        // TODO: do something with the feed
    }

    /**
     * Envoie les données au serveur.
     * @param v La view
     */
    private boolean send(View v) {
        // get the message from the message text box
        List<BluetoothDevice> list = activity.getLesDevicesBT();

        // make sure the fields are not empty
        if (list.size()>0)
        {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM yyyy à HH:mm:ss", Locale.FRANCE);
            String madate = sdf.format(new Date());

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("http://maximegens.alwaysdata.net/serveur.php");
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("date", madate));
                for (int i = 0; i<list.size(); i++){
                    nameValuePairs.add(new BasicNameValuePair("nom"+i, list.get(i).getName()));
                    nameValuePairs.add(new BasicNameValuePair("adress"+i, list.get(i).getAddress()));
                    nameValuePairs.add(new BasicNameValuePair("apparaillement"+i, DevicesBluetoothUtils.getBondStateText(list.get(i))));
                    nameValuePairs.add(new BasicNameValuePair("type"+i, DevicesBluetoothUtils.getTypeText(list.get(i))));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = convertStreamToString(instream);
                    Log.i("--Read from server", result);
                    return true;
                }else{
                    Log.i("--Read from server", "error entity = null");
                    return false;
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                return false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                return false;
            }
        }
        return false;
    }

    public String convertStreamToString(InputStream inputStream) throws IOException {
        if (inputStream != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            } finally {
                inputStream.close();
            }
            return sb.toString();
        } else {
            return "";
        }
    }
}


