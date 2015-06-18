package com.bleutoothserveur.maxime.bluetooth_serveur;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.AdapterItemBT;
import utils.Constantes;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_ENABLE_BT = 0;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> lesDevicesBT;
    private ListView listViewDevicesBT;
    private Button buttonRechercheBT;
    private AdapterItemBT adapterBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonRechercheBT = (Button)findViewById(R.id.buttonRechercheBT);
        listViewDevicesBT = (ListView)findViewById(R.id.listViewDevicesBT);
        lesDevicesBT = new ArrayList<>();
        adapterBT = new AdapterItemBT(this, lesDevicesBT);
        listViewDevicesBT.setAdapter(adapterBT);

        // Vérification de la présence du Bluetooth.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Vous ne possédez pas le Bluetooth sur votre appareil", Toast.LENGTH_SHORT).show();
            buttonRechercheBT.setEnabled(false);
        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Clique sur le bouton de recherche.
        buttonRechercheBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("-------------", "Lancement de la recherche--------------------");
                buttonRechercheBT.setText(Constantes.LIBELLE_RECHERCHE_EN_COURS);
                lesDevicesBT.clear();
                mBluetoothAdapter.startDiscovery();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_ENABLE_BT)
            return;
        if (resultCode == RESULT_OK) {
            // L'utilisation a activé le bluetooth
            Toast.makeText(getApplicationContext(),"Vous avez activé le Bluetooth", Toast.LENGTH_SHORT).show();
            // Abonnement au BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothReceiver, intentFilter);
            registerReceiver(bluetoothReceiver, filter);
            buttonRechercheBT.setEnabled(true);
        } else {
            // L'utilisation n'a pas activé le bluetooth
            Toast.makeText(getApplicationContext(),"Vous devez activer le Bluetooth pour scanner les devices aux alentours.", Toast.LENGTH_SHORT).show();
            buttonRechercheBT.setEnabled(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothReceiver != null){
            unregisterReceiver(bluetoothReceiver);
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

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
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
            }
        }
    };
}
