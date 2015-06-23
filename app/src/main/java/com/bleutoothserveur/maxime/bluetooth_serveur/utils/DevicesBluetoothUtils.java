package com.bleutoothserveur.maxime.bluetooth_serveur.utils;

import android.bluetooth.BluetoothDevice;

public class DevicesBluetoothUtils {

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
