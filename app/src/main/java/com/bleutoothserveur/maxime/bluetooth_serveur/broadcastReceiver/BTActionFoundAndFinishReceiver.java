package com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bleutoothserveur.maxime.bluetooth_serveur.MainActivity;
import com.bleutoothserveur.maxime.bluetooth_serveur.R;
import com.bleutoothserveur.maxime.bluetooth_serveur.adapters.AdapterItemBT;

/**
 * BroadcastReceiver surveillant la détection d'un nouvel appareil Bluetooth.
 */
public class BTActionFoundAndFinishReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MainActivity activity = (MainActivity) context;
        TextView titreAppareilFound = (TextView) activity.findViewById(R.id.titre_list_view);
        TextView titreAucunFound = (TextView) activity.findViewById(R.id.titre_aucun_device);
        ProgressBar progressRecherche = (ProgressBar) activity.findViewById(R.id.progress_recherche);

        Button buttonRechercheBT = (Button) activity.findViewById(R.id.button_rechercheBT);
        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            activity.updateListView(device);
        }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
            progressRecherche.setVisibility(View.VISIBLE);
            buttonRechercheBT.setText(context.getResources().getString(R.string.lancer_la_recherche));
            buttonRechercheBT.setBackground(context.getResources().getDrawable(R.drawable.primary_round));
            buttonRechercheBT.setEnabled(true);
            progressRecherche.setVisibility(View.GONE);
            if(activity.lesDevicesBT.isEmpty()){
                titreAucunFound.setVisibility(View.VISIBLE);
            }
        }else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)){
            titreAppareilFound.setVisibility(View.VISIBLE);
            titreAucunFound.setVisibility(View.GONE);
            progressRecherche.setVisibility(View.VISIBLE);
        }
    }
}
