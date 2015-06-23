package com.bleutoothserveur.maxime.bluetooth_serveur.utils;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Permet de connaitre l'état status internet du téléphone.
 *
 */
public class InternetUtils {
    /**
     * Indique si le device est connecté à Internet.
     * @param context Le context qui demande le test de connexion.
     * @return Un boolean, true si il existe une connection, false si il n'y a pas de connection internet.
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected();
    }
}
