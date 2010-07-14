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
package org.francho.java.web;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Helper que gestiona la descarga de una página web
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class PaginaWeb {
    private static final int CONNECT_TIMEOUT = 30 * 1000;
	private static final int READ_TIMEOUT = 30 * 1000;
	private URLConnection conn;
    private URL url;
    HashMap<String, String> params;
    private StringBuilder content;

    /**
     * Constructor
     */
    public PaginaWeb() {
        params = new HashMap<String, String>();
    }

    /**
     * Constructor 
     * @param url
     * @throws MalformedURLException
     */
    public PaginaWeb(String url) throws MalformedURLException {
        this();
        this.url = new URL(url);
    }

    /**
     * Añade un parámetro que se pasará a la url
     * @param key
     * @param value
     */
    public void putParam(String key, String value) {
        params.put(key, value);
    }


    /**
     * Descarga una página y la devuelve como string
     * @return
     * @throws IOException
     * @throws UnknownHostException
     */
    public String getPage() throws IOException, UnknownHostException {

        conn = url.openConnection();
        conn.setConnectTimeout(CONNECT_TIMEOUT);
        conn.setReadTimeout(READ_TIMEOUT);

        content = new StringBuilder();
        

        //conn.setAllowUserInteraction(false);

        conn.setDoOutput(true);

        // Abrimos el canal de comunicaci√≥n de env√≠o
        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        // Mandamos los par√°metros de la acci√≥n que (los ha tenido que precargar el m√©todo correspondiente)
        //out.writeBytes(parametros.toString());
        // Nos aseguramos de que todo se env√≠e
        out.flush();
        // Ya hemos dicho lo que ten√≠amos que decir, as√≠ que cerramos la conexi√≥n de envio
        out.close();

        // Capturamos el charset para evitarnos problemas de encode
        String ct = conn.getContentType();  // Ej: text/html; charset=iso-8859-1
        String charset = null;
        if (ct != null && ct.indexOf("charset") != -1) {
            charset = ct.substring(ct.indexOf("charset") + 8);

            int idx2 = charset.indexOf(";");
            if (idx2 != -1) {
                charset = charset.substring(0, idx2).trim();
            } else {
                charset = charset.trim();
            }

        }

        // Capturamos la respuesta
        BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));

        String l = "";


        while ((l = input.readLine()) != null) {
            content.append(l + "\n");
        }

        return content.toString();

    }

}
