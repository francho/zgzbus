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

import org.francho.java.tuzsa.BusLinea;
import org.francho.java.tuzsa.Tuzsa;

import android.os.AsyncTask;

/**
 * Tarea as’ncrona que se encarga de descargar los destinos de una l’nea
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class LoadParadasAsyncTask extends AsyncTask<String, Void, BusLinea> {

	/**
	 * Interfaz que deber‡n implementar las clases que la quieran usar Sirve
	 * como callback una vez termine la tarea as’ncrona
	 * 
	 */
	public interface LoadParadasAsyncTaskResponder {
		public void paradasLoaded(BusLinea bus);
	}

	private LoadParadasAsyncTaskResponder responder;

	/**
	 * Constructor. Es necesario que nos pasen un objeto para el callback
	 * 
	 * @param responder
	 */
	public LoadParadasAsyncTask(LoadParadasAsyncTaskResponder responder) {
		this.responder = responder;
	}

	/**
	 * Ejecuta el proceso en segundo plano
	 */
	@Override
	protected BusLinea doInBackground(String... linea) {
		BusLinea busLinea = null;
		try {
			busLinea = Tuzsa.getParadas(linea[0],linea[1]);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return busLinea;
	}

	/**
	 * Se ha terminado la ejecuci—n comunicamos el resultado al llamador
	 */
	@Override
	protected void onPostExecute(BusLinea result) {
		if (responder != null) {
			responder.paradasLoaded(result);
		}
	}

}
