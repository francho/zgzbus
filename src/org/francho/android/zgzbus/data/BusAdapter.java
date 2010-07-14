package org.francho.android.zgzbus.data;

import java.util.ArrayList;

import org.francho.java.tuzsa.BusLinea;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adaptador que sirve de puente entre la clase Tuzsa y el ArrayAdapter
 * 
 * Definido como inner class para poder acceder a propiedades de la
 * actividad
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class BusAdapter extends ArrayAdapter<BusLinea> {
	/**
	 * Constructor
	 * 
	 * @param context
	 * @param textViewResourceId
	 */
	public BusAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	/**
	 * Genera la vista de cada uno de los items del listado
	 */
	public View getView(int position, View v, ViewGroup parent) {		
		if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			v = vi.inflate(android.R.layout.simple_list_item_1, null);
		}

		if (this.getCount() > 0) {
			BusLinea bus = getItem(position);
			if (bus != null) {
				TextView busLinea = (TextView) v.findViewById(android.R.id.text1);
				busLinea.setText(bus.getLinea());
			}
		}

		return v;
	}

	/**
	 * A–ade todas las lineas al adapter
	 * 
	 * @param lineasBus
	 */
	public void addAll(ArrayList<BusLinea> lineasBus) {
		if(lineasBus == null) { return; }
		
		for (int i = 0; i < lineasBus.size(); i++) {
			add(lineasBus.get(i));
		}
	}

}

