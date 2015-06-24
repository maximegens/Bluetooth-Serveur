package com.bleutoothserveur.maxime.bluetooth_serveur.asyncTask;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bleutoothserveur.maxime.bluetooth_serveur.MainActivity;
import com.bleutoothserveur.maxime.bluetooth_serveur.R;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.Constantes;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.BluetoothUtils;

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
 * Asynctask envoyant les données sur le serveur en arrière plan.
 */
public class SendDataTask extends AsyncTask<View, Integer, Boolean> {

    private Context mContext;
    private MainActivity activity;
    private boolean error ;
    private boolean listeEmpty;
    private ProgressBar progressBar;
    private TextView envoieEnCours;

    /**
     * Constructeur de l'asyntask SendDataTask.
     * @param context Le context de l'activity appellant l'Asynctask.
     */
    public SendDataTask (Context context){
        mContext = context;
        activity = (MainActivity) mContext;
        error = false;
        listeEmpty = false;
        progressBar = (ProgressBar) activity.findViewById(R.id.progressBar_sendData);
        envoieEnCours = (TextView) activity.findViewById(R.id.envoi_en_cours);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(View.VISIBLE);
        envoieEnCours.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Boolean doInBackground(View... v) {
        return send(v[0]);
    }

    @Override
    protected void onPostExecute(Boolean resultat) {
        progressBar.setVisibility(View.GONE);
        envoieEnCours.setVisibility(View.GONE);
        if(resultat){
            Toast.makeText(mContext,"Liste des appareils envoyée",Toast.LENGTH_SHORT).show();
        }else{
            if(listeEmpty){
                Toast.makeText(mContext,"Liste vide. Données non envoyées.",Toast.LENGTH_SHORT).show();
            }
            if(error){
                Toast.makeText(mContext,"Erreur rencontrée - Liste des appareils non envoyée.",Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Envoie les données au serveur.
     * @param v La view
     */
    private boolean send(View v) {

        List<BluetoothDevice> list = activity.lesDevicesBT;

        // Verification que la liste n'est pas vide.
        if (list.size()>0) {

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, d MMM yyyy à HH:mm:ss", Locale.FRANCE);
            String madate = sdf.format(new Date());

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(Constantes.URL_SERVEUR);
            try {
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("date", madate));
                for (int i = 0; i<list.size(); i++){
                    nameValuePairs.add(new BasicNameValuePair("nom"+i, list.get(i).getName()));
                    nameValuePairs.add(new BasicNameValuePair("adress"+i, list.get(i).getAddress()));
                    nameValuePairs.add(new BasicNameValuePair("apparaillement"+i, BluetoothUtils.getBondStateText(list.get(i))));
                    nameValuePairs.add(new BasicNameValuePair("type"+i, BluetoothUtils.getTypeText(list.get(i))));
                }
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    String result = convertStreamToString(instream);
                    return true;
                }else{
                    error = true;
                    return false;
                }
            } catch (ClientProtocolException e) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }else{
            listeEmpty = true;
            return false;
        }
    }

    /**
     * Concertie le Stream reçu depuis le serveur en String.
     * @param inputStream Le Stream à convertir.
     * @return Un String correspondant au Stream reçu.
     * @throws IOException L'exception à levée.
     */
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


