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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;

import org.francho.android.zgzbus.data.PosteAdapter;
import org.francho.android.zgzbus.data.ZgzbusDb;
import org.francho.java.tuzsa.BusLlegada;
import org.francho.java.tuzsa.Tuzsa;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * Actividad principal, muestra el listado de llegadas para un determinado poste
 * También sirve de menú para acceder al resto de actividade 
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class PosteActivity extends ListActivity {
	protected static final int SUB_ACTIVITY_REQUEST_POSTE = 1000;
	protected static final int SUB_ACTIVITY_REQUEST_ADDFAV = 1001;
	public static final int SUB_ACTIVITY_RESULT_OK = 1002;
	public static final int SUB_ACTIVITY_RESULT_CANCEL = 1003;

	private static final int MENU_QUIT = 10;
	private static final int MENU_RECARGAR = 11;
	private static final int MENU_ACERCADE = 12;
	private static final int MENU_POSTE = 13;
	private static final int MENU_GUARDAR = 14;
	private static final int MENU_WEB_POSTE = 15;
	
	protected static final int DIALOG_CARGANDO = 100;
	
	protected static final int MSG_CLOSE_CARGANDO = 200;
	protected static final int MSG_ERROR_TUZSA = 201;
	protected static final int MSG_FRECUENCIAS_ACTUALIZADAS = 202;
	protected static final int MSG_RECARGA = 203;
	private static final long DELAY_RECARGA = 750; // Milisegundos antes de hacer la recarga (mejor dar un peque√±o margen para que se cargue la GUI)

	private ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();
	private PosteAdapter posteAdapter;
	private TextView guiHora;
	//private TextView guiTitulo;

	Calendar ahora = new GregorianCalendar();
	private int poste = 392;	
	final PosteHandler handler = new PosteHandler();
	private boolean verNotaDesarrollo = true;
	private TuzsaInfoUpdater posteUpdater = new TuzsaInfoUpdater();
	AlarmManager alarmManager;
	private Button botonPoste;
	private Button botonGuardar;
	private Button botonBuses;

	/**
	 * Se llama cuando se crea la actividad
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_PROGRESS);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		// Configuramos la vista
		setTheme(R.style.Theme_ZgzBus);
		setContentView(R.layout.poste);
		getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.icon_title);

		showProgressBar(true);
		
		if (savedInstanceState != null) {
			poste = savedInstanceState.getInt("poste");
		}
		
		setupView();
	}
	
	/**
	 * Una vez este creada la actividad obtenemos el servicio para fijar las alarmas
	 */
	
	@Override
	protected void onStart() {
		super.onStart();

		// Miramos si nos pasan el poste a cargar
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			poste = extras.getInt("poste");
		}

		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
	}


	/**
	 * Después de crear la actividad
	 */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		
		// Una vez cargado todo... recargamos datos
		handler.sendEmptyMessageDelayed(MSG_RECARGA,DELAY_RECARGA);
	}

	/**
	 * Configura los elementos de la GUI
	 */
	private void setupView() {	
		// Para facilitar su uso guardamos como atributos las partes del
		// interfaz que luego usaremos
		// guiPoste = (EditText) findViewById(R.id.campo_poste);
		guiHora = (TextView) findViewById(R.id.ultima_act);
		//guiTitulo = (TextView) findViewById(R.id.titulo);

		/**
		 * Configuramos la lista de resultados
		 */
		posteAdapter = new PosteAdapter(this, R.layout.poste_item);


		// Pie para la lista de resultados
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.poste_footer, null);
		getListView().addFooterView(v);

		// Al pulsar sobre un item abriremos el diálogo de poner alarma
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View arg1, int position,
					long arg3) {
				 BusLlegada bus = (BusLlegada) view.getItemAtPosition(position);
				 
				 setAlarm(bus);
			}} );
		
		// Asignamos el adapter a la lista
		setListAdapter(posteAdapter);
		posteAdapter.notifyDataSetChanged();
		
		//registerForContextMenu(guiTitulo);
		registerForContextMenu(getListView());
		
		/**
		 * Definimos el comportamiento de los botones
		 */

		// boton poste
		botonPoste = (Button) findViewById(R.id.boton_subposte);
		botonPoste.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// A peticion de los usuarios volvemos a sacar el buscador aqu√≠
				// launchPoste();
				EditText txtPoste = (EditText) findViewById(R.id.campo_poste);

				try {
					int tmpPoste = Integer.parseInt(txtPoste.getText()
							.toString());
					if (tmpPoste > 0 && tmpPoste < 9999) {
						poste = tmpPoste;
						handler.sendEmptyMessageDelayed(MSG_RECARGA,DELAY_RECARGA);
					}
				} catch (NumberFormatException e) {
					// Si no ha metido un numero correcto no hacemos nada
				}

			}
		});

		// Guardar
		botonGuardar = (Button) findViewById(R.id.boton_subguardar);
		botonGuardar.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchNuevoFavorito();
			}
		});

		// Boton favoritos
		Button botonFavoritos = (Button) findViewById(R.id.boton_subfavoritos);
		botonFavoritos.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				launchFavoritos();
			}
		});

		// Buses
		botonBuses = (Button) findViewById(R.id.boton_subfbuses);
		botonBuses.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				launchBuses();
			}
		});

	}

	/**
	 * Guardamos el poste en el estado para cuando el proceso se mate y reinicie
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);
		savedInstanceState.putInt("poste", poste);
		savedInstanceState.putBoolean("verNotaDesarrollo", verNotaDesarrollo);
	}

	/**
	 * Recuperamos el estado previo si ha sido guardado
	 * 
	 * @param savedInstanceState
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null) {
			poste = savedInstanceState.getInt("poste");
			verNotaDesarrollo = savedInstanceState
					.getBoolean("verNotaDesarrollo");
		}
	}

	/**
	 * Define el menú contextual
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		//menu.add(0, MENU_ALARMA, 0, R.string.menu_alarma);
		menu.add(0, MENU_GUARDAR, 0, R.string.menu_guardar);
		menu.add(0, MENU_POSTE, 0, R.string.menu_poste);
		menu.add(0, MENU_WEB_POSTE, 0, R.string.menu_tuzsa);
	}
	
	

	/**
	 * Gestionamos la pulsaci√≥n de un menú contextual
	 */
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_GUARDAR:
			launchNuevoFavorito();
			return true;
		case MENU_POSTE:
			launchPoste();
			return true;
		case MENU_WEB_POSTE:
			Uri uri = Uri.parse(Tuzsa.getUrlPoste(poste));
			startActivity(new Intent(Intent.ACTION_VIEW, uri));
			return true;
		default:
			return false;
		}
	}

	/**
	 * Encargada de capturar los datos que nos devuelven las subactividades
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == SUB_ACTIVITY_RESULT_OK) {
			switch (requestCode) {
			case SUB_ACTIVITY_REQUEST_POSTE:
				Bundle b = data.getExtras();
				poste = b.getInt("POSTE");
				handler.sendEmptyMessageDelayed(MSG_RECARGA,DELAY_RECARGA);
				break;
			case SUB_ACTIVITY_REQUEST_ADDFAV:
				launchFavoritos();
				break;
			}
		}
	}

	/**
	 * Definimos el menú de la actividad
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_RECARGAR, 0, "Recargar").setIcon(
				R.drawable.ic_menu_refresh);
		menu.add(0, MENU_ACERCADE, 0, "Acerca de...").setIcon(
				R.drawable.ic_menu_info_details);
		menu.add(0, MENU_QUIT, 0, "Salir").setIcon(
				R.drawable.ic_menu_close_clear_cancel);
		return true;
	}

	/**
	 * Manejamos las opciones del menú
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_RECARGAR:
			handler.sendEmptyMessageDelayed(MSG_RECARGA,DELAY_RECARGA);
			return true;
		case MENU_ACERCADE:
			showAboutDialog();
			return true;
		case MENU_QUIT:
			finish();
			return true;
		default:
			return false;
		}
	}
	
	/**
	 * Coloca una alarma
	 * 
	 * @param bus
	 */
	private void setAlarm(BusLlegada bus) {
		final CharSequence[] items = { "5 min.", "10 min.", "15 min."};
		
		final BusLlegada theBus = bus;
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.tit_choose_alarm);
		
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	Context context = getApplicationContext();
		    	
		    	Intent intent = new Intent(context, AlarmReceiver.class);
		    	
				int eta = theBus.getProximoMinutos();
				int mins = ((item+1)*5);
				
				if(eta < mins) {
					Toast.makeText(context, String.format(getString(R.string.err_bus_cerca), eta), Toast.LENGTH_SHORT).show();
					return;
				}
				
				String txt = String.format(getString(R.string.alarm_bus), ""+theBus.getLinea(), ""+poste );
				intent.putExtra("alarmTxt", txt);
				intent.putExtra("poste", poste);
				
				PendingIntent alarmReceiver = PendingIntent.getBroadcast(PosteActivity.this, 0, intent, 0);

				alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + ( mins * 60 * 1000), alarmReceiver);
				Toast.makeText(context, "Alarm set", Toast.LENGTH_LONG).show();
		    }
		});
		
		AlertDialog alert = builder.create();
		
		alert.show();
	}

	/**
	 * Coloca el icono de favorito si la parada está guardada
	 */
	private void setIcoFavorito() {
		// 
		//ImageView icoFavView = (ImageView) findViewById(R.id.icoFavorito);
		Uri miUri = ContentUris.withAppendedId(Uri.withAppendedPath(
				ZgzbusDb.Favoritos.CONTENT_URI, "poste"), poste);

		Drawable icoFav = getBaseContext().getResources().getDrawable(
				R.drawable.icon_favorito);
		icoFav.mutate();

		Cursor cur = managedQuery(miUri, null, null, null, null);

		// Si no ya está guardado no permitimos volver a guardarlo
		if (cur.getCount() > 0) {
			botonGuardar.setEnabled(false);
		} else {
			botonGuardar.setEnabled(true);
		}
	}

	/**
	 * Lanza la subactividad de poste
	 */
	private void launchPoste() {
		Intent i = new Intent(PosteActivity.this, PostePickerActivity.class);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
	}

	/**
	 * Lanza la subactivididad de favoritos
	 */
	private void launchFavoritos() {
		Intent i = new Intent(PosteActivity.this, FavoritosActivity.class);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
	}

	/**
	 * Lanza la subactivididad de Buses
	 */
	private void launchBuses() {
		Intent i = new Intent(PosteActivity.this, BusesActivity.class);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_POSTE);
	}

	/**
	 * Lanza la subactividad de añadir favorito. Le pasa el poste y la
	 * descripción
	 */
	private void launchNuevoFavorito() {
		Intent i = new Intent(PosteActivity.this, FavoritoNuevoActivity.class);

		Bundle extras = new Bundle();
		extras.putInt("POSTE", poste); // Pasamos el poste actual
		// Preparamos una descripción automática para el favorito
		HashSet<String> h = new HashSet<String>();
		for (BusLlegada bus : buses) {
			h.add(bus.getLinea() + " a " + bus.getDestino());
		}
		extras.putString("DESCRIPCION", h.toString());

		i.putExtras(extras);
		startActivityForResult(i, SUB_ACTIVITY_REQUEST_ADDFAV);
	}

	/**
	 * Ventana emergente que se muestra con el "Acerca de..."
	 */
	public void showAboutDialog() {
		/*
		 * Basado en http://www.anddev.org/viewtopic.php?p=12814
		 */

		AlertDialog dialogAbout = null;
		final AlertDialog.Builder builder;

		LayoutInflater li = LayoutInflater.from(this);
		View view = li.inflate(R.layout.about, null);

		builder = new AlertDialog.Builder(this)
				.setIcon(R.drawable.icon_zgzbus)
				.setTitle(getString(R.string.app_name) + " " + getAppRevision())
				.setPositiveButton("Ok", null).setView(view);

		dialogAbout = builder.create();

		dialogAbout.show();

	}

	/**
	 * Devuelve la version de la aplicacion
	 * 
	 * @return
	 */
	public String getAppRevision() {
		
		PackageInfo pInfo = null; 
        try{ 
           pInfo = getPackageManager().getPackageInfo("org.francho.android.zgzbus",PackageManager.GET_META_DATA); 
        } catch (NameNotFoundException e) { 
                pInfo = null; 
        } 
        
        String r="";
        if(pInfo != null) {
                r+= "v" + pInfo.versionName;
                r+= " (rev " + pInfo.versionCode +")";
        }
		
        return r;
		
	}

	/**
	 * Ventana emergente para mostrar los mensajes de error
	 * 
	 * @param err
	 *            mensaje de error a mostrar
	 */
	public void showError(String err) {
		new AlertDialog.Builder(this).setMessage(err).setCancelable(false)
				.setPositiveButton(R.string.ok, new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// finish(); // Al pulsar sobre el boton la alicacion se
						// cerrar√°
					}
				}).show();
	}

	/**
	 * ¿Muestro la barra de progreso?
	 * @param show
	 */
	public void showProgressBar(Boolean show)  {
		if(show){
			getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
	                Window.PROGRESS_INDETERMINATE_ON);

			getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
	                Window.PROGRESS_VISIBILITY_ON);

		} else {
			getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
	                Window.PROGRESS_INDETERMINATE_OFF);

			getWindow().setFeatureInt(Window.FEATURE_PROGRESS,
	                Window.PROGRESS_VISIBILITY_OFF);
		}
	}

	/**
	 * Clase encargada de coger los datos del poste
	 * 
	 * Es observable y puede ser lanzada en otro thread
	 * 
	 * @author francho
	 * 
	 */
	class TuzsaInfoUpdater implements Runnable {
		@Override
		public void run() {
			try {
				buses = Tuzsa.getPoste(poste);
				handler.sendEmptyMessage(MSG_FRECUENCIAS_ACTUALIZADAS);
			} catch (Exception e) {
				handler.sendEmptyMessage(MSG_ERROR_TUZSA);
			} finally {
				handler.sendEmptyMessage(MSG_CLOSE_CARGANDO);				
			}
		}
	}
	
	/**
	 * Handler para intercambiar mensajes entre los hilos
	 * 
	 * @author francho
	 *
	 */
	class PosteHandler extends Handler {
		public void handleMessage(Message msg) {
			// int total = msg.getData().getInt("total");
			// progressDialog.setProgress(total);

			switch (msg.what) {

			case MSG_ERROR_TUZSA:
				Toast toast = Toast.makeText(getApplicationContext(),
						"Error al obtener los datos\nwww.tuzsa.es",
						Toast.LENGTH_SHORT);
				toast.show();
				showProgressBar(false);
				break;
				
			case MSG_CLOSE_CARGANDO:
				showProgressBar(false);
				break;
				
			case MSG_RECARGA:				
				showProgressBar(true);
				
				removeCallbacks(posteUpdater);
				removeMessages(MSG_RECARGA);
				post(posteUpdater);			
				sendEmptyMessageDelayed(MSG_RECARGA, 60*1000);
				break;
				
			case MSG_FRECUENCIAS_ACTUALIZADAS:
				setTitle("Tuzsa, parada " + poste);
				final Calendar c = Calendar.getInstance();
				setIcoFavorito();

				SimpleDateFormat df = new SimpleDateFormat("hh:mm");
				String updated = String.format(getString(R.string.updated_at), df.format(c.getTime()));
				
				guiHora.setText(updated);

				// Limpiamos la lista
				posteAdapter.clear();

				// La rellenamos con los nuevos datos
				if (buses != null && buses.size() > 0) {
					int n = buses.size();
					
					for (int i = 0; i < n; i++) {
						posteAdapter.add(buses.get(i));
					}
				}

				posteAdapter.notifyDataSetChanged();
				break;

			}
		}
	}
}