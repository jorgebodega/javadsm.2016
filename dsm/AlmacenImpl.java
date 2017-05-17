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

public class AlmacenImpl extends UnicastRemoteObject implements Almacen{

  private static final long serialVersionUID = 14L;
  private LinkedList<ObjetoCompartido> objectsList; //Lista de objetos compartidos.

  /**
   * Crea un nuevo objeto.
   * 
   * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
   */
  public AlmacenImpl() throws RemoteException { objectsList = new LinkedList<ObjetoCompartido>();}

  /**
   * Obtendrá los objetos contenidos en la lista basandose en las cabeceras de la lista parámetro.
   * 
   * @param lcab Lista de cabeceras de objetos compartidos.
   * @return Una lista de objetos con cabeceras iguales a las del parámetro. NULL si la lista queda vacía.
   * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
   */
  public synchronized List<ObjetoCompartido> leerObjetos(List<CabeceraObjetoCompartido> lcab) throws RemoteException {

    List<ObjetoCompartido> tmp = new LinkedList<ObjetoCompartido>(); //Lista temporal donde almacenaremos los objetos que devolveremos.

    for (CabeceraObjetoCompartido cabecera : lcab) { //Recorre toda la lista con iteradores.
      for (ObjetoCompartido objeto : objectsList) { //Recorre toda la lista con iteradores.
        if (objeto.getCabecera().getNombre().equals(cabecera.getNombre()) && cabecera.getVersion() < objeto.getCabecera().getVersion()){ //Si ambos nombres son iguales y la versión de la cabecerá del parámetro es menor.
          tmp.add(objeto); break; //Añadimos a la lista temporal y paramos el segundo bucle.
        }
      }
    }

    return tmp.isEmpty()? null : tmp;
  }

  /**
   * Escribe los objetos de la lista parámetro en la lista del almacen.
   * 
   * @param loc Lista de objetos compartidos.
   * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
   */
  public synchronized void escribirObjetos(List<ObjetoCompartido> loc) throws RemoteException  {

    ObjetoCompartido tmp; //Objeto compartido temporal.
      
    for (ObjetoCompartido objetoComp : loc) { //Recorre toda la lista con iteradores.
      tmp = null; //Marcamos el objeto a null.
      for (ObjetoCompartido objetoEnLista : objectsList) { //Recorre toda la lista con iteradores.
        if (objetoComp.getCabecera().getNombre().equals(objetoEnLista.getCabecera().getNombre())) { //Si ambos nombres son iguales.
          tmp = objetoEnLista; break; //Colocamos el objeto de la lista en la variable temporal.
        }
      }
      if (tmp == null) objectsList.add(objetoComp); //Si no había objeto, lo añadimos a la lista.
      else{ //Si existía el objeto, modificamos su campo Objeto y la versión de la cabecera por la nueva versión.
        tmp.setObjeto(objetoComp.getObjeto());
        tmp.setVersion(objetoComp.getCabecera().getVersion());
      }
    }
  }

}

