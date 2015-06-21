package com.bleutoothserveur.maxime.bluetooth_serveur.adapters;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bleutoothserveur.maxime.bluetooth_serveur.R;

import java.util.List;

import com.bleutoothserveur.maxime.bluetooth_serveur.utils.Constantes;

/**
 * Class adapter permettant d'afficher les données dans la listView personnalisée.
 */
public class AdapterItemBT extends BaseAdapter {

    LayoutInflater inflater;
    List<BluetoothDevice> listDevicesBT;

    public AdapterItemBT(Context context,List<BluetoothDevice> listDevicesBT) {
        inflater = LayoutInflater.from(context);
        this.listDevicesBT = listDevicesBT;
    }

    @Override
    public int getCount() {
        return listDevicesBT.size();
    }

    @Override
    public Object getItem(int position) {
        return listDevicesBT.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView nom_bt;
        TextView adresse_bt;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        holder = new ViewHolder();

        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_bt,null);

            holder.nom_bt = (TextView)convertView.findViewById(R.id.item_nom_bt);
            holder.adresse_bt = (TextView)convertView.findViewById(R.id.item_adress_bt);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        if(listDevicesBT != null && listDevicesBT.get(position) != null){
            if(listDevicesBT.get(position).getName() == null) {
                holder.nom_bt.setText(Constantes.LIBELLE_INCONNU);
            }else{
                holder.nom_bt.setText("" + listDevicesBT.get(position).getName());
            }
            holder.adresse_bt.setText(listDevicesBT.get(position).getAddress());
        }
        return convertView;
    }
}
