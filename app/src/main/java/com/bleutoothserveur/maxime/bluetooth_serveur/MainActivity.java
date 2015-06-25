package com.bleutoothserveur.maxime.bluetooth_serveur;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.bleutoothserveur.maxime.bluetooth_serveur.adapters.AdapterItemBT;
import com.bleutoothserveur.maxime.bluetooth_serveur.asyncTask.SendDataTask;
import com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver.BTActionFoundAndFinishReceiver;
import com.bleutoothserveur.maxime.bluetooth_serveur.broadcastReceiver.BTReceiverStateChange;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.AlertDialogCustom;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.Constantes;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.BluetoothUtils;
import com.bleutoothserveur.maxime.bluetooth_serveur.utils.InternetUtils;


public class MainActivity extends AppCompatActivity {

    public List<BluetoothDevice> lesDevicesBT;
    private BluetoothAdapter bluetoothAdapter;
    private Button buttonRechercheBT;
    private AdapterItemBT adapterBT;
    private BroadcastReceiver bluetoothActionFoundAndFinishReceiver;
    private BroadcastReceiver bluetoothReceiverStateChange;
    private boolean actif = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button buttonSendData = (Button)findViewById(R.id.send_data);
        ListView listViewDevicesBT = (ListView)findViewById(R.id.listview_devicesBT);
        Switch switchActivationBT = (Switch)findViewById(R.id.switch_activationBT);
        TextView stateBluetooth = (TextView)findViewById(R.id.state_bluetooth);
        buttonRechercheBT = (Button)findViewById(R.id.button_rechercheBT);
        lesDevicesBT = new ArrayList<>();
        adapterBT = new AdapterItemBT(this, lesDevicesBT);
        listViewDevicesBT.setAdapter(adapterBT);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothActionFoundAndFinishReceiver = new BTActionFoundAndFinishReceiver();
        bluetoothReceiverStateChange = new BTReceiverStateChange();

        if (savedInstanceState != null && savedInstanceState.containsKey(Constantes.KEY_SAVEINSTANCE_LISTE_BT)) {
            lesDevicesBT = savedInstanceState.getParcelableArrayList(Constantes.KEY_SAVEINSTANCE_LISTE_BT);
            if(!lesDevicesBT.isEmpty()) {
                findViewById(R.id.titre_list_view).setVisibility(View.VISIBLE);
                adapterBT.swapItems(lesDevicesBT);
            }
        }

        // Vérification de la présence du Bluetooth.
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Vous ne possédez pas le Bluetooth sur votre appareil", Toast.LENGTH_SHORT).show();
            buttonRechercheBT.setEnabled(false);
            switchActivationBT.setChecked(false);
            stateBluetooth.setText(Constantes.LIBELLE_BT_INEXISTANT);
        }else{
            actif = true;
            BluetoothUtils.abonnementActivationDesactivationBT(MainActivity.this, bluetoothReceiverStateChange);
            BluetoothUtils.abonnementActionFoundAndDiscoveryFinishedBT(MainActivity.this, bluetoothActionFoundAndFinishReceiver);
            if (!bluetoothAdapter.isEnabled()) {
                stateBluetooth.setText(Constantes.LIBELLE_BT_DESACTIVE);
                buttonRechercheBT.setBackgroundColor(getResources().getColor(R.color.material_gris_300));
                switchActivationBT.setChecked(false);
            }else{
                stateBluetooth.setText(Constantes.LIBELLE_BT_ACTIVE);
                switchActivationBT.setChecked(true);
            }
        }

         // Clique sur le bouton de recherche.
        buttonRechercheBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bluetoothAdapter.isEnabled()){
                    buttonRechercheBT.setText(getResources().getString(R.string.recherche_en_cours));
                    lesDevicesBT.clear();
                    bluetoothAdapter.startDiscovery();
                    buttonRechercheBT.setEnabled(false);
                    buttonRechercheBT.setBackgroundColor(getResources().getColor(R.color.material_gris_300));
                }else{
                    BluetoothUtils.activationBluetooth(MainActivity.this);
                }

            }
        });

        // Clique sur le bouton d'activation ou de désactivation du Bluetooth.
        switchActivationBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluetoothAdapter.enable();
                } else {
                    bluetoothAdapter.disable();
                }
            }
        });

        // Clique sur le bouton d'envoi des données au serveurs.
        buttonSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(InternetUtils.isConnected(getApplication())){
                    SendDataTask sendDataTask = new SendDataTask(MainActivity.this);
                    sendDataTask.execute(v);
                }else{
                    Toast.makeText(getApplicationContext(),Constantes.CONNEXION_REQUIRED,Toast.LENGTH_SHORT).show();
                }
            }
        });

         // Clique un item dans la liste des devices bluetooth détectés.
        listViewDevicesBT.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(MainActivity.this);
                dialog.setContentView(R.layout.custom_dialog_item_bt);
                dialog.setTitle(getResources().getString(R.string.detail_bluetooth));
                Button dialogButton = (Button)dialog.findViewById(R.id.button_dialog_item_ok);
                TextView dialogNom = (TextView) dialog.findViewById(R.id.name_value_dialog_item);
                TextView dialogAdress = (TextView) dialog.findViewById(R.id.adress_value_dialog_item);
                TextView dialogBonded = (TextView) dialog.findViewById(R.id.bonded_value_dialog_item);
                TextView dialogType = (TextView) dialog.findViewById(R.id.type_value_dialog_item);
                BluetoothDevice device = lesDevicesBT.get(position);

                if(device != null){
                    if(device.getName() == null){
                        dialogNom.setText(Constantes.LIBELLE_INCONNU);
                    }else{
                        dialogNom.setText(device.getName());
                    }
                    dialogBonded.setText(BluetoothUtils.getBondStateText(device));
                    dialogType.setText(BluetoothUtils.getTypeText(device));
                    dialogAdress.setText(device.getAddress());
                }

                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }
        });
    }


    /**
     * Sauvegarde des données de l'écran lors d'un changement de cycle Android.
     * @param outState Le bundle dans lequel stocker les valeurs.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(Constantes.KEY_SAVEINSTANCE_LISTE_BT, (ArrayList<? extends Parcelable>) lesDevicesBT);
        super.onSaveInstanceState(outState);
    }

    /**
     * Résultat de l'activité lancer lors de la demande d'activation du Bluetooth via la popup.
     * @param requestCode Code de retour
     * @param resultCode Résultat du choix dans la popup.
     * @param data Les données passées en paramétres.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != Constantes.REQUEST_ENABLE_BT){
            buttonRechercheBT.setEnabled(false);
            return;
        }
        if (resultCode == RESULT_OK) {
            Toast.makeText(getApplicationContext(),"Vous avez activé le Bluetooth", Toast.LENGTH_SHORT).show();
            BluetoothUtils.abonnementActionFoundAndDiscoveryFinishedBT(MainActivity.this, bluetoothActionFoundAndFinishReceiver);
            buttonRechercheBT.setEnabled(true);
        } else {
            Toast.makeText(getApplicationContext(),"Vous devez activer le Bluetooth pour scanner les devices aux alentours.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Méthode onDestroy().
     */
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
    protected void onResume(){
        super.onResume();
        if(bluetoothActionFoundAndFinishReceiver == null && actif){
            BluetoothUtils.abonnementActionFoundAndDiscoveryFinishedBT(MainActivity.this,bluetoothActionFoundAndFinishReceiver);
        }
        if(bluetoothReceiverStateChange == null && actif){
            BluetoothUtils.abonnementActivationDesactivationBT(MainActivity.this,bluetoothReceiverStateChange);
        }
    }

    /**
     * Méthode de création du menu.
     * @param menu Le menu
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * Methode appelée en fonction du choix sélectionné dans le menu.
     * @param item le choix.
     * @return true.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        AlertDialogCustom alert = new AlertDialogCustom(this);
        int id = item.getItemId();

        if (id == R.id.webpage_serveur) {
            alert.showWebSite();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mise à jour de la liste des appareils Bluetooth détectés.
     * @param device L'appareil Bluetooth détecté à afficher.
     */
    public void updateListView(BluetoothDevice device){
         lesDevicesBT.add(device);
         adapterBT.notifyDataSetChanged();
    }
}