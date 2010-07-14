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
package org.francho.android.zgzbus.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Adaptador que sirve de puente entre la clase Tuzsa y el ArrayAdapter
 * 
 * @author francho - http://francho.org/lab/
 * 
 */
public class ZgzbusDb {
	 public static final String AUTHORITY = "org.francho.android.zgzbus";

	    // This class cannot be instantiated
	    private ZgzbusDb() {}
	    
	    /**
	     * Tabla de favoritos
	     */
	    public static final class Favoritos implements BaseColumns {
	    	// This class cannot be instantiated
	        private Favoritos() {}
	    	
	    	/**
		     *  content:// estilo URL para esta tabla
		     */
		    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favoritos");
	    	
		    public static final String DEFAULT_SORT_ORDER = "poste DESC";
	    	
		    /**
	         * The MIME type of {@link #CONTENT_URI} providing a directory of notes.
	         */
	        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.google.favoritos";

	        /**
	         * The MIME type of a {@link #CONTENT_URI} sub-directory of a single note.
	         */
	        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.google.favoritos";

	        /**
	         * The titulo del favorito
	         * <P>Type: TEXT</P>
	         */
	        //public static final String _ID = "_ID";
		    
		    /**
	         * The titulo del favorito
	         * <P>Type: TEXT</P>
	         */
	        public static final String POSTE = "poste";
		    
		    /**
	         * The titulo del favorito
	         * <P>Type: TEXT</P>
	         */
	        public static final String TITULO = "titulo";
	        
	        /**
	         * The title of the note
	         * <P>Type: TEXT</P>
	         */
	        public static final String DESCRIPCION = "descripcion";

	    }
	}


