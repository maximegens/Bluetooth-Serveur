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
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private final static int REQUEST_CODE_ENABLE_BLUETOOTH = 0;
    private BluetoothAdapter mBluetoothAdapter;
    private Button buttonScanner;
    private TextView listBT;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScanner = (Button)findViewById(R.id.buttonScanner);
        listBT = (TextView)findViewById(R.id.listBT);

        // Vérification de la présence du Bluetooth.
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Vous ne possédez pas le Bluetooth sur votre appareil", Toast.LENGTH_SHORT).show();
            buttonScanner.setEnabled(false);
        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_CODE_ENABLE_BLUETOOTH);
            }
        }

        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Button","Lancement de la recherche");
                mBluetoothAdapter.startDiscovery();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE_ENABLE_BLUETOOTH)
            return;
        if (resultCode == RESULT_OK) {
            // L'utilisation a activé le bluetooth
            Toast.makeText(getApplicationContext(),"Vous avez activé le Bluetooth", Toast.LENGTH_SHORT).show();
            // Abonnement au BroadcastReceiver
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            IntentFilter intentFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(bluetoothReceiver, intentFilter);
            registerReceiver(bluetoothReceiver, filter);
            buttonScanner.setEnabled(true);
        } else {
            // L'utilisation n'a pas activé le bluetooth
            Toast.makeText(getApplicationContext(),"Vous devez activer le Bluetooth pour scanner les devices aux alentours.", Toast.LENGTH_SHORT).show();
            buttonScanner.setEnabled(false);
        }
    }

    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.v("BroadcastReceiver", "nom : "+device.getName()+ " "+device.getAddress());
                listBT.append(device.getName()+ " "+device.getAddress()+"\n");
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                Log.v("BroadcastReceiver","Fin de la recherche ");
                mBluetoothAdapter.startDiscovery();
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothAdapter.cancelDiscovery();
        unregisterReceiver(bluetoothReceiver);
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
}
