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
package org.francho.android.zgzbus.tasks;

import java.io.IOException;
import java.util.ArrayList;

import org.francho.java.tuzsa.BusLinea;
import org.francho.java.tuzsa.Tuzsa;

import android.os.AsyncTask;

/**
 * Tarea as’ncrona que se encarga de descargar el listado de buses
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class LoadBusesAsyncTask extends AsyncTask<Void, Void, ArrayList<BusLinea>> {
	
	/**
	 * Interfaz que deber‡n implementar las clases que la quieran usar
	 * Sirve como callback una vez termine la tarea as’ncrona
	 * 
	 */
	public interface LoadBusesAsyncTaskResponder {
	    public void busesLoaded(ArrayList<BusLinea> buses);
	  }
	private LoadBusesAsyncTaskResponder responder;
	
	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadBusesAsyncTask(LoadBusesAsyncTaskResponder responder) {
		this.responder = responder;
	}
	
	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected ArrayList<BusLinea> doInBackground(Void... params) {
		ArrayList<BusLinea> lineasBus = null;
		try {
			lineasBus = Tuzsa.getLineasBus();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return lineasBus;
	}

	/**
	 * Se ha terminado la ejecuci—n comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(ArrayList<BusLinea> result) {
		if(responder != null) {
			responder.busesLoaded(result);
		}
	}

	
}
