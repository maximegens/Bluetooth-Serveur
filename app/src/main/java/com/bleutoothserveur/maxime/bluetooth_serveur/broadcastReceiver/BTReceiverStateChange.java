package com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.bleutoothserveur.maxime.bluetooth_serveur.MainActivity;
import com.bleutoothserveur.maxime.bluetooth_serveur.R;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.Constantes;

/**
 * BroadcastReceiver surveillant l'activation et la désactivation du Bluetooth.
 */
public class BTReceiverStateChange extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        MainActivity activity = (MainActivity) context;
        TextView stateBluetooth = (TextView)activity.findViewById(R.id.state_bluetooth);
        Button buttonRechercheBT = (Button)activity.findViewById(R.id.button_rechercheBT);
        Switch switchActivationBT = (Switch)activity.findViewById(R.id.switch_activationBT);

        if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR);
            switch (state) {
                case BluetoothAdapter.STATE_OFF:
                    Log.v("btReceiverStateChange", "Bluetooth off");
                    stateBluetooth.setText(Constantes.LIBELLE_BT_DESACTIVE);
                    switchActivationBT.setChecked(false);
                    buttonRechercheBT.setEnabled(false);
                    switchActivationBT.setEnabled(true);
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                    Log.v("btReceiverStateChange", "Turning Bluetooth off...");
                    stateBluetooth.setText(Constantes.LIBELLE_BT_DESACTIVE_EN_COURS);
                    switchActivationBT.setEnabled(false);
                    buttonRechercheBT.setEnabled(false);
                    buttonRechercheBT.setBackgroundColor(context.getResources().getColor(R.color.material_gris_300));
                    break;
                case BluetoothAdapter.STATE_TURNING_ON:
                    Log.v("btReceiverStateChange", "Turning Bluetooth on...");
                    stateBluetooth.setText(Constantes.LIBELLE_BT_ACTIVE_EN_COURS);
                    switchActivationBT.setEnabled(false);
                    break;
                case BluetoothAdapter.STATE_ON:
                    Log.v("btReceiverStateChange", "Bluetooth on...");
                    stateBluetooth.setText(Constantes.LIBELLE_BT_ACTIVE);
                    buttonRechercheBT.setEnabled(true);
                    switchActivationBT.setChecked(true);
                    switchActivationBT.setEnabled(true);
                    if(android.os.Build.VERSION.SDK_INT >= 21) {
                        buttonRechercheBT.setBackground(context.getResources().getDrawable(R.drawable.primary_round));
                    }
                    break;
            }
        }
    }
}

