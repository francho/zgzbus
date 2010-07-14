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
package org.francho.java.tuzsa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.francho.java.web.PaginaWeb;

/**
 * Descarga los datos de la web de Tuzsa y los parsea en sus correspondientes clases contenedoras
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class Tuzsa {
	/**
	 * Url para el listado de líneas
	 * @return
	 */
	public static String getUrlLineas() {
		return "http://www.tuzsa.es/tuzsa_frm_esquemaparadas.php";
	}
	
	/**
	 * Url para el listado de llegadas de un determinado poste
	 * @param poste
	 * @return
	 */
	public static String getUrlPoste(int poste) {
		return "http://www.tuzsa.es/tuzsa_frm_esquemaparadatime.php?poste="+ poste;
	}
	
	/**
	 * Url para el listado de direcciones de una determinada linea
	 * @param linea
	 * @return
	 */
	public static String getUrlDirecciones(String linea) {
		return "http://www.tuzsa.es/tuzsa_frm_esquemaparadas.php?LINEASEL="+linea;
	}
	
	/**
	 * Url para el listado de paradas de una linea
	 * @param linea
	 * @param direccion
	 * @return
	 */
	public static String getUrlParadas(String linea,String direccion) {
		return "http://www.tuzsa.es/tuzsa_frm_esquemaparadas.php?LINEASEL="+linea+"&SENTIDOSEL="+direccion;
	}

	
	/**
	 * Descarga las lineas de bus
	 * @return lineas de autobus
	 * @throws IOException
	 */
	public static ArrayList<BusLinea> getLineasBus() throws IOException {
		PaginaWeb webTuzsa = new PaginaWeb(getUrlLineas());
		String datos = webTuzsa.getPage();

		// <form method="post" action="tuzsa_frm_esquemaparadas.php"
		// name="paradas" id="paradas">

		Pattern p = Pattern.compile("<form [^>]+id=\"paradas\">(.*)</form>");
		Matcher m = p.matcher(datos);

		ArrayList<BusLinea> lineasBus = new ArrayList<BusLinea>();
		if (m.find()) {
			String form = m.group();

			Pattern p2 = Pattern
					.compile("<option value=\"([^\"]+)\">([^<]+)</option>");
			Matcher m2 = p2.matcher(form);

			while (m2.find()) {
				if(! m2.group(2).equals("Elija una linea")) {
					lineasBus.add(new BusLinea(m2.group(1), m2.group(2)));
				}
			}
		}

		return lineasBus;
	}
	
	/**
	 * Devuelve los posibles destinos de una determinada linea
	 * @param linea
	 * @return los destinos
	 * @throws IOException
	 */
	public static BusLinea getDestinos(String linea)  throws IOException {
		BusLinea bus = new BusLinea(linea);
		
		String datos="";		
		PaginaWeb webTuzsa = new PaginaWeb(getUrlDirecciones(linea));
		
		datos = webTuzsa.getPage();
	
		Pattern p = Pattern.compile("<input name=\"SENTIDOSEL\" type=\"radio\" value=\"([^\"]+)\"[^>]*></input>([^<]+)");
		Matcher m = p.matcher(datos);
		
		while(m.find()) {
			String destino = m.group(2).replaceAll("[\n\t\r]+"," ").trim();
			bus.putDestino(m.group(1).trim(), destino.trim());
		}
		
	
		
		return bus;
	}

	/**
	 * Devuelve las paradas para una determinada linea
	 * 
	 * @param linea
	 * @param sentido
	 * @return
	 * @throws IOException
	 */
	public static BusLinea getParadas(String linea, String sentido)  throws IOException {
		BusLinea bus = new BusLinea(linea);
		
		String datos="";		
		PaginaWeb webTuzsa = new PaginaWeb(getUrlParadas(linea, sentido));
		
		datos = webTuzsa.getPage();
		
		
		Pattern p = Pattern.compile("<tr><td><div align=\"right\">([^<]+)</div></td><td><img src=\"images/([^\"]+)[^>]+></img></td><td><a [^>]+>([^<]+)</a>");
		Matcher m = p.matcher(datos);
		
		ArrayList<BusParada> paradas = new ArrayList<BusParada>();
		
		while(m.find()) {
			String direccion = m.group(1);
			if(m.group(2).equals("CirculoCadAzul.gif")) { direccion+= " (l√≠nea desviada)"; }			
			String poste = m.group(3);
			
			paradas.add(new BusParada(poste,direccion));
			
		}
		
		bus.setParadas(paradas);
		
		return bus;
	}	
	
	/**
	 * Descarga las llegadas para un determinado poste
	 * 
	 * @param poste
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<BusLlegada> getPoste(int poste) throws IOException {

		PaginaWeb webTuzsa = null;
		String datos = "";
		try {
			webTuzsa = new PaginaWeb(getUrlPoste(poste));
			datos = webTuzsa.getPage();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ArrayList<BusLlegada>();
		} catch (UnknownHostException e) {
			return new ArrayList<BusLlegada>();
		}

		Pattern p = Pattern
				.compile("<tr><td class=\"digital\">([^<]+)</td><td class=\"digital\">([^<]+)</td><td class=\"digital\">([^<]+)</td></tr>");
		Matcher m = p.matcher(datos);

		ArrayList<BusLlegada> buses = new ArrayList<BusLlegada>();
		while (m.find()) {
			buses.add(new BusLlegada(m.group(1), m.group(2), m.group(3)));
		}

		Collections.sort(buses);
		return buses;
	}

	
	/**
	 * metodo para poder probar la clase
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// tuzsa.getLineasBus();
			//ArrayList<BusLlegada> b = Tuzsa.getPoste(392);

			//System.out.println(Tuzsa.getDirecciones("028"));
			System.out.println(Tuzsa.getParadas("028","2"));
			
		} catch (IOException ex) {
			Logger.getLogger(Tuzsa.class.getName()).log(Level.SEVERE, null, ex);
		}

	}
}
