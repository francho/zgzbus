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

import android.app.ListActivity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Muestra los favoritos guardados
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class FavoritosActivity extends ListActivity {
	private static final String[] PROJECTION = new String[] {
			ZgzbusDb.Favoritos._ID, // 0
			ZgzbusDb.Favoritos.POSTE, // 1
			ZgzbusDb.Favoritos.TITULO, // 2
			ZgzbusDb.Favoritos.DESCRIPCION, // 3
	};

	private static final int MENU_BORRAR = 2;

	private ListView favoritosView;

	SimpleCursorAdapter adapter;

	/**
	 * On Create
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.favoritos);
		setTitle(R.string.tit_favoritos);
		
		/*
		 * Si no ha sido cargado con anterioridad, cargamos nuestro
		 * "content provider"
		 */
		Intent intent = getIntent();
		if (intent.getData() == null) {
			intent.setData(ZgzbusDb.Favoritos.CONTENT_URI);
		}

		/*
		 * Query "managed": la actividad se encargar√° de cerrar y volver a
		 * cargar el cursor cuando sea necesario
		 */
		Cursor cursor = managedQuery(getIntent().getData(), PROJECTION, null,
				null, ZgzbusDb.Favoritos.DEFAULT_SORT_ORDER);

		/*
		 * Mapeamos las querys SQL a los campos de las vistas
		 */
		String[] camposDb = new String[] { ZgzbusDb.Favoritos.POSTE,
				ZgzbusDb.Favoritos.TITULO, ZgzbusDb.Favoritos.DESCRIPCION };
		int[] camposView = new int[] { R.id.poste, R.id.titulo,
				R.id.descripcion };

		adapter = new SimpleCursorAdapter(this, R.layout.favoritos_item,
				cursor, camposDb, camposView);

		setListAdapter(adapter);

		/*
		 * Preparamos las acciones a realizar cuando pulsen un favorito
		 */

		favoritosView = (ListView) findViewById(android.R.id.list);
		favoritosView.setOnItemClickListener(favoritoClickedHandler);
		registerForContextMenu(favoritosView);
		
		

	}
    
	/**
	 * Si no hay favoritos cerramos la actividad
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		if(adapter.getCount() == 0) {
            Toast.makeText(FavoritosActivity.this, R.string.no_favs , Toast.LENGTH_SHORT).show();
            
            finish();
		}
	}


	/**
	 * Listener encargado de gestionar las pulsaciones sobre los items
	 */
	private OnItemClickListener favoritoClickedHandler = new OnItemClickListener() {
		@Override
		/**
		 * @param l The ListView where the click happened
		 * @param v The view that was clicked within the ListView
		 * @param position The position of the view in the list
		 * @param id The row id of the item that was clicked
		 */
		public void onItemClick(AdapterView<?> l, View v, int position, long id) {
			Cursor c = (Cursor) l.getItemAtPosition(position);
			int poste = c.getInt(c.getColumnIndex(ZgzbusDb.Favoritos.POSTE));

			Intent intent = new Intent();
			Bundle b = new Bundle();
			b.putInt("POSTE", poste);
			intent.putExtras(b);
			setResult(PosteActivity.SUB_ACTIVITY_RESULT_OK, intent);
			finish();

		}
	};

	/**
	 * Menú contextual
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		menu.add(0, MENU_BORRAR, 0, "Borrar");

	}	
	
	/**
	 * Gestionamos la pulsación de un menú contextual
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		
		switch (item.getItemId()) {
		case MENU_BORRAR:
			Uri miUri = ContentUris.withAppendedId(
					ZgzbusDb.Favoritos.CONTENT_URI, info.id);

			getContentResolver().delete(miUri, null, null);
			
			return true;
		default:
			// return super.onContextItemSelected(item);
		}
		return false;
	}

}
