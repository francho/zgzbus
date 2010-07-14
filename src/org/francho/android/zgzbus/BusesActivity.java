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
package org.francho.android.zgzbus;

import java.util.ArrayList;

import org.francho.android.zgzbus.data.BusAdapter;
import org.francho.android.zgzbus.tasks.LoadBusesAsyncTask;
import org.francho.android.zgzbus.tasks.LoadDestinosAsyncTask;
import org.francho.android.zgzbus.tasks.LoadParadasAsyncTask;
import org.francho.android.zgzbus.tasks.LoadBusesAsyncTask.LoadBusesAsyncTaskResponder;
import org.francho.android.zgzbus.tasks.LoadDestinosAsyncTask.LoadDestinosAsyncTaskResponder;
import org.francho.android.zgzbus.tasks.LoadParadasAsyncTask.LoadParadasAsyncTaskResponder;
import org.francho.java.tuzsa.BusLinea;
import org.francho.java.tuzsa.BusParada;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Selector de buses en tres pasos: bus -> direccion -> parada
 * 
 * @author francho - http://francho.org/lab/
 * 
 */

public class BusesActivity extends ListActivity {
	private static final int DIALOG_DESTINOS = 111;
	private static final int DIALOG_PARADAS = 112;
	protected static final int DIALOG_ERROR_TUZSA = 501;
	
	private BusAdapter lineasAdapter;
	ArrayList<BusLinea> lineasBus;

	// Datos bus
	BusLinea linea = null;
	String idDestino = "";
	String destino = "";
	

	/**
	 * Al arrancar la actividad...
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_PROGRESS);
		setContentView(R.layout.buses);
		setTitle(R.string.tit_buses);

		lineasAdapter = new BusAdapter(this,
				android.R.layout.simple_list_item_1);
		setListAdapter(lineasAdapter);

		loadBuses();
	}
		
	
	/**
	 * Configuramos los dialogos emergentes
	 * 
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog;

		switch (id) {
		case DIALOG_DESTINOS:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.tit_direccion);

			if (linea == null) {
				return null;
			}

			CharSequence[] items = new CharSequence[linea.getDestinos().size()];
			linea.getDestinos().values().toArray(items);

			builder.setItems(items, destinosListener);

			alertDialog = builder.create();
			alertDialog.setCancelable(false);

			return alertDialog;

		case DIALOG_PARADAS:
			ArrayList<BusParada> paradas = linea.getParadas();

			final CharSequence[] items2 = new CharSequence[paradas.size()];

			for (int x = 0; x < items2.length; x++) {
				items2[x] = paradas.get(x).toString();
			}

			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle(R.string.tit_paradas);

			builder2.setItems(items2, paradasListener);
			
						
			alertDialog = builder2.create();
			alertDialog.setCancelable(false);

			return alertDialog;

		case DIALOG_ERROR_TUZSA:
			Toast toast = Toast.makeText(getApplicationContext(),
					"Error al obtener los datos\nwww.tuzsa.es",
					Toast.LENGTH_SHORT);
			toast.show();
			finish();
			return null;
			
		default:
			return null;
		}

	}

	/** llamadas a tareas as’ncronas ****************************************************************/
	
	/**
	 * Carga las lineas de bus
	 */
	private void loadBuses() {
		showProgressBar(true);
		
		new LoadBusesAsyncTask(loadBusesAsyncTaskResponder).execute();
	}
	
	/**
	 * Ser‡ llamado cuando la tarea de cargar buses termine
	 */
	LoadBusesAsyncTaskResponder loadBusesAsyncTaskResponder = new LoadBusesAsyncTaskResponder() {
		@Override
		public void busesLoaded(ArrayList<BusLinea> buses) {
			if(buses != null) {
				lineasBus = buses;
				lineasAdapter.clear();
				lineasAdapter.addAll(lineasBus);
				lineasAdapter.notifyDataSetChanged();
			} else {
				showDialog(DIALOG_ERROR_TUZSA);
			}
			showProgressBar(false);
		}
	};
	
	
	/**
	 * Carga los destinos
	 */
	private void loadDestinos() {
		showProgressBar(true);
		
		new LoadDestinosAsyncTask(loadDestinosAsyncTaskResponder).execute(linea.getIdlinea());
	}
	
	/**
	 * Sera llamadao cuando la tarea de cargar destinos termine
	 */
	LoadDestinosAsyncTaskResponder loadDestinosAsyncTaskResponder = new LoadDestinosAsyncTaskResponder() {
		@Override
		public void destinosLoaded(BusLinea bus) {
			if(bus != null) {
				linea = bus;
				showDialog(DIALOG_DESTINOS);
			} else {
				showDialog(DIALOG_ERROR_TUZSA);
			}
			showProgressBar(false);
		}
	};
	
	/**
	 * Carga las paradas
	 */
	private void loadParadas() {
		showProgressBar(true);
		
		new LoadParadasAsyncTask(loadParadasAsyncTaskResponder).execute(linea.getIdlinea(), idDestino);
	}
	
	/**
	 * Se llamar‡ cuando las paradas hayan sido cargadas
	 */
	LoadParadasAsyncTaskResponder loadParadasAsyncTaskResponder = new LoadParadasAsyncTaskResponder() {
		@Override
		public void paradasLoaded(BusLinea bus) {
			if(bus != null){
				linea = bus;
				showDialog(DIALOG_PARADAS);
			} else {
				showDialog(DIALOG_ERROR_TUZSA);
			}
			showProgressBar(false);
		}
	};
	
	/** Funciones auxiliares *********************************************************/
	
	/**
	 * ÀMuestro la barra de progreso?
	 * @param show
	 */
	public void showProgressBar(Boolean show)  {
		Window win = getWindow();
		
		if(show){
			win.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_INDETERMINATE_ON);
			win.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_ON);
		} else {
			win.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_INDETERMINATE_OFF);
			win.setFeatureInt(Window.FEATURE_PROGRESS, Window.PROGRESS_VISIBILITY_OFF);
		}
	}
	
	/** Listeners ********************************************************************/
	
	/**
	 * Escucha las selecciones de la lista principal (lineas bus)
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		linea = (BusLinea) l.getItemAtPosition(position);

		loadDestinos();
	}

	
	/**
	 * Escuchar‡ a los clicks del listado de destinos
	 */
	DialogInterface.OnClickListener destinosListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int item) {
			idDestino = (String) linea.getDestinos().keySet().toArray()[item];
			destino = linea.getDestinos().get(idDestino);

			loadParadas();
		}
	};
	
	/**
	 * Escuchar‡ a los clicks del listado de paradas
	 */
	DialogInterface.OnClickListener paradasListener = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int item) {
			int poste = Integer.parseInt(linea.getParadas().get(item)
					.getPoste());

			Intent intent = new Intent();
			Bundle b = new Bundle();
			b.putInt("POSTE", poste);
			intent.putExtras(b);
			setResult(PosteActivity.SUB_ACTIVITY_RESULT_OK, intent);
			finish();
		}
	};

	
}
