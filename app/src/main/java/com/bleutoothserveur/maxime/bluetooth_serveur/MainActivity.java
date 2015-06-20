package com.bleutoothserveur.maxime.bluetooth_serveur;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.bleutoothserveur.maxime.bluetooth_serveur.adapters.AdapterItemBT;
import com.bleutoothserveur.maxime.bluetooth_serveur.asyncTask.SendDataTask;
import com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver.BTActionFoundAndFinishReceiver;
import com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver.BTReceiverStateChange;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.Constantes;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> lesDevicesBT;
    private ListView listViewDevicesBT;
    private Button buttonRechercheBT;
    private Button buttonSendData;
    private AdapterItemBT adapterBT;
    private Switch switchActivationBT;
    private TextView stateBluetooth;
    private BroadcastReceiver bluetoothActionFoundAndFinishReceiver;
    private BroadcastReceiver bluetoothReceiverStateChange;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonRechercheBT = (Button)findViewById(R.id.button_rechercheBT);
        buttonSendData = (Button)findViewById(R.id.send_data);
        listViewDevicesBT = (ListView)findViewById(R.id.listview_devicesBT);
        switchActivationBT = (Switch)findViewById(R.id.switch_activationBT);
        stateBluetooth = (TextView)findViewById(R.id.state_bluetooth);
        lesDevicesBT = new ArrayList<>();
        adapterBT = new AdapterItemBT(this, lesDevicesBT);
        listViewDevicesBT.setAdapter(adapterBT);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothActionFoundAndFinishReceiver = new BTActionFoundAndFinishReceiver();
        bluetoothReceiverStateChange = new BTReceiverStateChange();


        // Vérification de la présence du Bluetooth.
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Vous ne possédez pas le Bluetooth sur votre appareil", Toast.LENGTH_SHORT).show();
            buttonRechercheBT.setEnabled(false);
            switchActivationBT.setChecked(false);
            stateBluetooth.setText(Constantes.LIBELLE_BT_INEXISTANT);
        }else{
            abonnementActivationDesactivationBT();
            abonnementActionFoundAndDiscoveryFinishedBT();
            if (!bluetoothAdapter.isEnabled()) {
                stateBluetooth.setText(Constantes.LIBELLE_BT_DESACTIVE);
                switchActivationBT.setChecked(false);
            }else{
                stateBluetooth.setText(Constantes.LIBELLE_BT_ACTIVE);
                switchActivationBT.setChecked(true);
            }
        }

        // Clique sur le bouton de recherche.
        buttonRechercheBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isEnabled()){
                    buttonRechercheBT.setText(Constantes.LIBELLE_RECHERCHE_EN_COURS);
                    lesDevicesBT.clear();
                    bluetoothAdapter.startDiscovery();
                    buttonRechercheBT.setEnabled(false);
                }else{
                    activationBluetooth();
                }

            }
        });

        /**
         * Clique sur le bouton d'activation ou de désactivation du Bluetooth.
         */
        switchActivationBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluetoothAdapter.enable();
                } else {
                    bluetoothAdapter.disable();
                }
            }
        });

        /**
         * Clique sur le bouton d'envoi des données au serveurs.
         */
        buttonSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendDataTask sendDataTask = new SendDataTask();
                sendDataTask.execute(v);
            }
        });
    }

    /**
     * Résultat de l'activité lancer lors de la demande d'activation du Bluetooth via la popup.
     * @param requestCode Code de retour
     * @param resultCode Résultat du choix dans la popup.
     * @param data Les données passées en paramétres.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_ENABLE_BT){
            buttonRechercheBT.setEnabled(false);
            return;
        }
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(),"Vous avez activé le Bluetooth", Toast.LENGTH_SHORT).show();
            abonnementActionFoundAndDiscoveryFinishedBT();
            buttonRechercheBT.setEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(),"Vous devez activer le Bluetooth pour scanner les devices aux alentours.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode onDestroy().
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothActionFoundAndFinishReceiver != null){
            unregisterReceiver(bluetoothActionFoundAndFinishReceiver);
        }
        if(bluetoothReceiverStateChange != null){
            unregisterReceiver(bluetoothReceiverStateChange);
        }

    }

    /**
     * Méthode de création du menu.
     * @param menu Le menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Methode appelée en fonction du choix sélectionné dans le menu.
     * @param item le choix.
     * @return true.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * S'abonne aux évenements de découvert d'appareil Bluetooth à proximité
     * et de fin de rechercher du Bluetooth.
     */
    private void abonnementActionFoundAndDiscoveryFinishedBT(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(bluetoothActionFoundAndFinishReceiver, filter);
    }

    /**
     * S'abonne aux évenements d'activation et de désactivation du Bluetooth.
     */
    private void abonnementActivationDesactivationBT(){
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(bluetoothReceiverStateChange, filter);
    }

    /**
     * Permet de demander à l'utilisateur d'activer le Bluetooth.
     */
    private void activationBluetooth(){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /**
     * Mise à jour de la liste des appareils Bluetooth détectés.
     * @param device L'appareil Bluetooth détecté à afficher.
     */
    public void updateListView(BluetoothDevice device){
         lesDevicesBT.add(device);
         adapterBT.notifyDataSetChanged();
    }
}
