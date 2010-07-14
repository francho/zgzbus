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

import org.francho.android.zgzbus.data.ZgzbusDb;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Guarda un nuevo favorito
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class FavoritoNuevoActivity extends Activity {
	private EditText guiDescripcion;
	private EditText guiTitulo;

	private String poste;

	/**
	 * OnCreate....
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Configuramos la vista
		setTheme(R.style.Theme_ZgzBus);
		setContentView(R.layout.favorito_nuevo);
		
		setupView();
	}
	
	/**
	 * Si no hay poste cerramos la actividad
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if ((poste == null) || poste.equals("")) {
			Toast.makeText(FavoritoNuevoActivity.this, R.string.no_poste,
					Toast.LENGTH_SHORT).show();

			finish();
		}
	}
	
	/**
	 * Configura la vista
	 */
	private void setupView() {
		guiTitulo = (EditText) findViewById(R.id.titulo);
		guiDescripcion = (EditText) findViewById(R.id.descripcion);

		// Comprobamos si nos estan pasando como par‡metro el poste y la
		// descripci—n

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			poste = "" + extras.getInt("POSTE");
			guiDescripcion.setText("" + extras.getString("DESCRIPCION"));
		}

		setTitle(String.format(getString(R.string.tit_guardar), poste));

		/*
		 * Asignamos el comprotamiento de los botones
		 */
		Button guiGo = (Button) findViewById(R.id.boton_go);
		guiGo.setOnClickListener(guiGoOnClickListener);

		/*
		 * Asignamos el comprotamiento de los botones
		 */
		Button guiCancel = (Button) findViewById(R.id.boton_cancel);
		guiCancel.setOnClickListener(guiCancelListener);

	}

	/**
	 * Escuchar‡ el bot—n de guardar
	 */
	OnClickListener guiGoOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			ContentValues values = new ContentValues();

			values.put(ZgzbusDb.Favoritos.TITULO, guiTitulo.getText()
					.toString());
			values.put(ZgzbusDb.Favoritos.DESCRIPCION, guiDescripcion
					.getText().toString());
			values.put(ZgzbusDb.Favoritos.POSTE, Integer.valueOf(poste));

			getContentResolver().insert(ZgzbusDb.Favoritos.CONTENT_URI,
					values);

			Intent intent = new Intent();
			setResult(PosteActivity.SUB_ACTIVITY_RESULT_OK, intent);
			finish();
		}
	};
	
	/**
	 * Escuchar‡ el bot—n de cancelar
	 */
	OnClickListener guiCancelListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			setResult(PosteActivity.SUB_ACTIVITY_RESULT_CANCEL, intent);
			finish();
		}
	};
}
