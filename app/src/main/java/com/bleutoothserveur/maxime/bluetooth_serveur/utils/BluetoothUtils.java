package com.bleutoothserveur.maxime.bluetooth_serveur.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.bleutoothserveur.maxime.bluetooth_serveur.MainActivity;
import com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver.BTActionFoundAndFinishReceiver;
import com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver.BTReceiverStateChange;

public class BluetoothUtils {

    /**
     * S'abonne aux évenements de découvert d'appareil Bluetooth à proximité
     * et de fin de rechercher du Bluetooth.
     */
    public static void abonnementActionFoundAndDiscoveryFinishedBT(Context context, BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        context.registerReceiver(receiver, filter);
    }

    /**
     * S'abonne aux évenements d'activation et de désactivation du Bluetooth.
     */
    public static void abonnementActivationDesactivationBT(Context context, BroadcastReceiver receiver){
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        context.registerReceiver(receiver, filter);
    }

    /**
     * Permet de demander à l'utilisateur d'activer le Bluetooth.
     */
    public static void activationBluetooth(Context context){
        MainActivity activity = (MainActivity)context;
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        activity.startActivityForResult(enableBtIntent, Constantes.REQUEST_ENABLE_BT);
    }


    /**
     * Indique le type de Bluetooth de l'appareil.
     * @param device Le device à vérifie.
     * @return Le type de Bluetooth.
     */
    public static String getTypeText(BluetoothDevice device) {
        if(android.os.Build.VERSION.SDK_INT >= 18) {
            switch (device.getType()){
                case BluetoothDevice.DEVICE_TYPE_CLASSIC :
                    return "Bluetooth classique";
                case BluetoothDevice.DEVICE_TYPE_DUAL :
                    return "Bluetooth classique et Low Energy";
                case BluetoothDevice.DEVICE_TYPE_LE :
                    return "Bluetooth Low Energy";
                case BluetoothDevice.DEVICE_TYPE_UNKNOWN :
                    return Constantes.LIBELLE_INCONNU;
                default:
                    return Constantes.LIBELLE_INCONNU;
            }
        }
        else {
            return Constantes.LIBELLE_NON_DISPONIVLE;
        }
    }

    /**
     * Indique l'etat de l'apparaillage du device.
     * @param device Le device à vérifie.
     * @return l'etat d'apparaillage.
     */
    public static String getBondStateText(BluetoothDevice device) {
        switch (device.getBondState()){
            case BluetoothDevice.BOND_NONE :
                return "Non appareillé";
            case BluetoothDevice.BOND_BONDING :
                return"Appareillement en cours";
            case BluetoothDevice.BOND_BONDED :
                return"Appareillé";
            default:
                return Constantes.LIBELLE_INCONNU;
        }
    }
}
