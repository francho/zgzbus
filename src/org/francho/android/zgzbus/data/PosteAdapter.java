/**
 *  ZgzBus - Consulta cuando llega el autobus urbano en Zaragoza
 *  Copyright (C) 2010 Francho Joven
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.francho.android.zgzbus.data;

import org.francho.android.zgzbus.R;
import org.francho.java.tuzsa.BusLlegada;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Adaptador que sirve de puente entre la clase Tuzsa y el ArrayAdapter
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class PosteAdapter extends ArrayAdapter<BusLlegada> {
	
	/**
	 * Constructor
	 * @param context
	 * @param textViewResourceId
	 */
	public PosteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	/**
	 * Genera la vista de cada uno de los items 
	 */
	@Override
	public View getView(int position, View v, ViewGroup parent) {
		// Si no tenemos la vista de la fila creada componemos una
		if (v == null) {
			Context ctx = this.getContext().getApplicationContext();
			LayoutInflater vi = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			v = vi.inflate(R.layout.poste_item, null);

			v.setTag(new ViewHolder(v));
		}

		// Accedemos a la vista cacheada y la rellenamos
		ViewHolder tag = (ViewHolder) v.getTag();

		
		BusLlegada bus = getItem(position);
		if (bus != null) {
			tag.busLinea.setText(bus.getLinea());
			tag.busDestino.setText(bus.getDestino());
			tag.busProximo.setText(bus.getProximo());
		}

		return v;
	}

	/*
	 * Clase contendora de los elementos de la vista de fila para agilizar su
	 * acceso
	 */
	private class ViewHolder {
		TextView busLinea;
		TextView busDestino;
		TextView busProximo;

		public ViewHolder(View v) {
			busLinea = (TextView) v.findViewById(R.id.bus_linea);
			busDestino = (TextView) v.findViewById(R.id.bus_destino);
			busProximo = (TextView) v.findViewById(R.id.bus_proximo);
		}

	}

}
