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

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Clase contenedora de la estructura de datos para una linea de bus
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class BusLinea implements Comparable<BusLinea> {

	private String idlinea;
	private String linea;
    private HashMap<String,String> destinos = new HashMap<String,String>();
    private ArrayList<BusParada> paradas;
    
    /**
     * Constructor 
     * @param linea bus
     * @param destino dirección de de destino

     */
    
    public BusLinea(String idlinea) {
    	this.idlinea = idlinea;
    }
    
    public BusLinea(String idlinea, String linea) {
    	this(idlinea);
    	this.linea = linea;
    }
    
    public BusLinea(String idlinea, String linea, HashMap<String,String> destinos, ArrayList<BusParada> paradas) {
        this(idlinea, linea);
        
        this.destinos = destinos;
        this.paradas = paradas;
    }

	@Override
	public int compareTo(BusLinea bus2) {		
        return linea.compareTo(bus2.linea);
    }

    public String getIdlinea() {
		return idlinea;
	}

	public String getLinea() {
		return linea;
	}

	public ArrayList<BusParada> getParadas() {
		return paradas;
	}
	
	public void setParadas(ArrayList<BusParada> paradas) {
		this.paradas = paradas;
	}
	
	public void putDestino(String id, String destino) {
		destinos.put(id, destino);
	}
	
	public HashMap<String,String> getDestinos() {
		return destinos;
	}

	@Override
	public String toString() {
		return "" + linea + " ("+ idlinea +") \n"
			+ "Destinos: " + destinos +"\n" 
			+ "Paradas: " + paradas ;
	}

}
