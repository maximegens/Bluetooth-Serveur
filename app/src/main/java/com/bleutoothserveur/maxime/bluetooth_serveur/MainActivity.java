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

import adapters.AdapterItemBT;
import utils.Constantes;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter bluetoothAdapter;
    private List<BluetoothDevice> lesDevicesBT;
    private ListView listViewDevicesBT;
    private Button buttonRechercheBT;
    private AdapterItemBT adapterBT;
    private Switch switchActivationBT;
    private TextView stateBluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonRechercheBT = (Button)findViewById(R.id.buttonRechercheBT);
        listViewDevicesBT = (ListView)findViewById(R.id.listViewDevicesBT);
        switchActivationBT = (Switch)findViewById(R.id.switchActivationBT);
        stateBluetooth = (TextView)findViewById(R.id.state_bluetooth);
        lesDevicesBT = new ArrayList<>();
        adapterBT = new AdapterItemBT(this, lesDevicesBT);
        listViewDevicesBT.setAdapter(adapterBT);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Vérification de la présence du Bluetooth.
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Vous ne possédez pas le Bluetooth sur votre appareil", Toast.LENGTH_SHORT).show();
            buttonRechercheBT.setEnabled(false);
            switchActivationBT.setChecked(false);
        }else{
            abonnementActivationDeactivationBT();
            if (!bluetoothAdapter.isEnabled()) {
                switchActivationBT.setChecked(false);
                activationBluetooth();
            }else{
                switchActivationBT.setChecked(true);
            }
        }

        // Clique sur le bouton de recherche.
        buttonRechercheBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isEnabled()){
                    Log.v("-------------", "Lancement de la recherche--------------------");
                    buttonRechercheBT.setText(Constantes.LIBELLE_RECHERCHE_EN_COURS);
                    lesDevicesBT.clear();
                    bluetoothAdapter.startDiscovery();
                    buttonRechercheBT.setEnabled(false);
                }else{
                    activationBluetooth();
                }

            }
        });

        switchActivationBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    bluetoothAdapter.enable();
                }else{
                    bluetoothAdapter.disable();
                }
            }
        });
    }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


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
    /*
    * BroadcastReceiver surveillant la découverte de devices BT et la fin de recherche de devices BT.
    */
    private final BroadcastReceiver bluetoothActionFoundAndFinishReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v("------BroadcastReceiver", "Appareil : "+device.getName()+ " "+device.getAddress());
                lesDevicesBT.add(device);
                adapterBT.notifyDataSetChanged();
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.v("------BroadcastReceiver", "-- Fin de la recherche ");
                buttonRechercheBT.setText(Constantes.LIBELLE_LANCER_RECHERCHE);
                buttonRechercheBT.setEnabled(true);
            }
        }
    };

    /**
     * BroadcastReceiver surveillant l'activation et la désactivation du Bluetooth.
     */
    private final BroadcastReceiver bluetoothReceiverStateChange = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        Log.v("btReceiverStateChange", "Bluetooth off");
                        stateBluetooth.setText("Bluetooth désactivé");
                        switchActivationBT.setChecked(false);
                        buttonRechercheBT.setEnabled(false);
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.v("btReceiverStateChange", "Turning Bluetooth off...");
                        stateBluetooth.setText("Bluetooth en cours de désactivation");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.v("btReceiverStateChange", "Turning Bluetooth on...");
                        stateBluetooth.setText("Bluetooth en cours d'activation");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.v("btReceiverStateChange", "Bluetooth on...");
                        stateBluetooth.setText("Bluetooth activé");
                        buttonRechercheBT.setEnabled(true);
                        switchActivationBT.setChecked(true);
                        break;
                }
            }
        }
    };


    /**
     * S'abonne aux évenements de découvert d'appareil Bluetooth à proximité
     * et de fin de rechercher du Bluetooth.
     */
    private void abonnementActionFoundAndDiscoveryFinishedBT(){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothActionFoundAndFinishReceiver, filter);
    }

    /**
     * S'abonne aux évenements d'activation et de désactivation du Bluetooth.
     */
    private void abonnementActivationDeactivationBT(){
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
}
