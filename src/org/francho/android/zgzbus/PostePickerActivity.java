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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Permite cambiar de poste
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class PostePickerActivity extends Activity {
	EditText txtPoste;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.poste_picker);

		txtPoste = (EditText) findViewById(R.id.campo_poste);
		Button botonPoste = (Button) findViewById(R.id.boton_go);

		botonPoste.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					int poste = Integer.parseInt(txtPoste.getText().toString());
					if (poste > 0) {
						Intent intent = new Intent();
						Bundle b = new Bundle();

						b.putInt("POSTE", poste);
						intent.putExtras(b);
						setResult(PosteActivity.SUB_ACTIVITY_RESULT_OK, intent);
						finish();
					}
				} catch (NumberFormatException e) {
					// Si no ha metido un numero correcto no hacemos nada
				}

			}
		});

		Button botonCancel = (Button) findViewById(R.id.boton_cancel);
		botonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();

				setResult(PosteActivity.SUB_ACTIVITY_RESULT_CANCEL, intent);
				finish();
			}
		});

	}
}
