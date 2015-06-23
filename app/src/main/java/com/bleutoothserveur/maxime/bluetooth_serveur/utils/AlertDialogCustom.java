package com.bleutoothserveur.maxime.bluetooth_serveur.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

public class AlertDialogCustom {

    Context context;

    /**
     * Constructeur de la class AlertDialogCustom
     * @param context Le contexte de l'activity dans laquelle afficher l'AlertDialog
     */
    public AlertDialogCustom(Context context){
        this.context = context;
    }

    /**
     * Affiche une AlertDialog proposant d'ouvrir une page internet vers le serveur.
     */
    public void showWebSite(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog
                .setMessage("Voulez vous consulter le serveur web ayant reçu la liste des appareils Bluetooth ?")
                .setCancelable(false)
                .setPositiveButton("Oui",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                if(InternetUtils.isConnected(context)){
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constantes.URL_SERVEUR));
                                    context.startActivity(browserIntent);
                                }else{
                                    Toast.makeText(context,Constantes.CONNEXION_REQUIRED,Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                )
                .setNegativeButton("Non",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                                paramDialogInterface.cancel();
                            }
                        }
                );
        alertDialog.create().show();
    }

    public void showItemDescription(){

    }
}
