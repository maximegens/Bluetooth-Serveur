package com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.bleutoothserveur.maxime.bluetooth_serveur.MainActivity;
import com.bleutoothserveur.maxime.bluetooth_serveur.R;
import com.bleutoothserveur.maxime.bluetooth_serveur.adapters.AdapterItemBT;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.Constantes;

/**
 * BroadcastReceiver surveillant la détection d'un nouvel appareil Bluetooth.
 */
public class BTActionFoundAndFinishReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MainActivity activity = (MainActivity) context;

        Button buttonRechercheBT = (Button) activity.findViewById(R.id.button_rechercheBT);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            Log.v("------BroadcastReceiver", "Appareil : " + device.getName() + " " + device.getAddress());
            activity.updateListView(device);
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            Log.v("------BroadcastReceiver", "-- Fin de la recherche ");
            buttonRechercheBT.setText(Constantes.LIBELLE_LANCER_RECHERCHE);
            buttonRechercheBT.setEnabled(true);
        }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            Log.v("-------------", "Lancement de la recherche--------------------");
        }
    }
}
