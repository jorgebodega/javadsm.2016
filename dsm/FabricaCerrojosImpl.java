/**
* @author Jorge Bodega
* @author Daniel de Diego
*
* @version 1.2
*/

package dsm;

import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class FabricaCerrojosImpl extends UnicastRemoteObject implements FabricaCerrojos {

	private static final long serialVersionUID = 14L;
	private HashMap<String, Cerrojo> listaCerrojos; //Mapa de cerrojos. Contiene Cerrojos identificados con strings.

	/**
	 * Crea un nuevo objeto.
	 */
    public FabricaCerrojosImpl() throws RemoteException { listaCerrojos = new HashMap<String, Cerrojo>();}

    /**
     * Obtiene del mapa el cerrojo identificado por el String. Si no existe lo crea y lo añade al mapa.
     * 
     * @param s String identificador del cerrojo a iniciar.
     * @return Devuelve el cerrojo identificado con el String.
     * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
     */
    public synchronized	Cerrojo iniciar(String s) throws RemoteException {
		
		Cerrojo result = listaCerrojos.get(s); //Obtiene el cerrojo.

		if(result == null){ //Si no existe el cerrojo en el mapa.
			result = new CerrojoImpl(); //Crea un nuevo cerrojo.
			listaCerrojos.put(s, result); //Lo añade a la lista.
		}

		return result;
    }
}

