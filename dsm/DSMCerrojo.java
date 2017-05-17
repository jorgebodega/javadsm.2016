/**
* @author Jorge Bodega
* @author Daniel de Diego
*
* @version 1.2
*/

package dsm;

import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.*;

public class DSMCerrojo {

    private String nombre, servidor, puerto; //Indican el nombre del cerrojo, y el servidor y puerto al que se conectan.
    private FabricaCerrojos cerrojos; //Fabrica de Cerrojos.
    private Almacen almacen; //Almacén de cerrojos.
    private LinkedList<ObjetoCompartido> objetos; //Lista de objetos.
    private Cerrojo cerrojo; //Cerrojo de la clase.
    private boolean exclusivo; //Modo de ejecución del cerrojo.

    /**
     * Crea el nuevo objeto.
     * 
     * @param nom Nombre del nuevo Cerrojo.
     * @throws RemoteException       Error en la ejecución en la llamada a un método remoto.
     * @throws NotBoundException     Falla la operación de lookup.
     * @throws MalformedURLException La URL donde hacer el lookup esta mal formada.
     */
    public DSMCerrojo (String nom) throws RemoteException, NotBoundException, MalformedURLException {
        objetos = new LinkedList<ObjetoCompartido>();
        nombre = nom; //Adquiere el nombre.
        servidor = System.getenv("SERVIDOR"); //Obtiene el servidor y el puerto de las variables de entorno.
        puerto = System.getenv("PUERTO");
        cerrojos = (FabricaCerrojos) Naming.lookup("rmi://" + servidor + ":" + puerto + "/DSM_cerrojos"); //Obtiene la fábrica de cerrojos.
        almacen = (Almacen) Naming.lookup("rmi://" + servidor + ":" + puerto + "/DSM_almacen"); //Obtiene el almacén de cerrojos.
        cerrojo = cerrojos.iniciar(nombre); //Inicia un nuevo cerrojo de la fábrica.
    }

    /**
     * Incluimos el nuevo objeto a la lista del cerrojo, si no está incluido.
     * 
     * @param o Objeto compartido que asociaremos al cerrojo.
     */
    public void asociar(ObjetoCompartido o) { if (!objetos.contains(o)) objetos.add(o);} //Si el objeto no está en la lista lo añade.

    /**
     * Borramos de la lista asociada al cerrojo, si está incluido.
     * 
     * @param o Objeto compartido que borraremos de la lista asociada al cerrojo.
     */
    public void desasociar(ObjetoCompartido o) { objetos.remove(o);} //Borra el objeto de la lista si está incluida.

    /**
     * Adquiere la linea de ejecución en el cerrojo. Despues, obtiene los objetos del almacen para actualizar en la lista local. 
     * 
     * @param exc Marca el modo de ejecución del cerrojo, si lo llama un escritor o lector.
     * @return FALSE si falla la ejecución al actualizar el Objeto compartido, TRUE en otro caso.
     * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
     */
    public boolean adquirir(boolean exc) throws RemoteException {
        
        this.exclusivo = exc; //Guarda el modo de ejecución.
        this.cerrojo.adquirir(this.exclusivo); //Adquiere el hilo en el cerrojo.
        LinkedList<CabeceraObjetoCompartido> cabeceras = new LinkedList<CabeceraObjetoCompartido>(); //Crea una lista temporal de cabeceras.
        
        if(!objetos.isEmpty()){ //Si la lista no esta vacía, añade todas las cabeceras a la lista temporal.
            for (ObjetoCompartido obj : objetos) cabeceras.add(obj.getCabecera());
        }        

        List<ObjetoCompartido> nuevosObjetos = this.almacen.leerObjetos(cabeceras); //Obtiene los objetos del almacén.       
        if(nuevosObjetos!=null){ //Si no es null.
            for (ObjetoCompartido nuevoObjeto : nuevosObjetos) { //Recorre la lista.
                for (ObjetoCompartido viejoObjeto : objetos) { //Recorre la lista.
                    if (nuevoObjeto.getCabecera().getNombre().equals(viejoObjeto.getCabecera().getNombre())){ //Compara los nombres.
                        if(!viejoObjeto.setObjetoCompartido(nuevoObjeto)) return false; //Si no se puede modificar el objeto, error.
                        break;           
                    }
                }
            }
        }

        return true;
    }
    
    /**
     * Libera el cerrojo, pero antes comprueba el modo de ejecución del cerrojo. Si es escritor, cambia el número de versión de los objetos y los escribe en el almacén.
     * 
     * @return Devuelve el resultado de la liberación de hilos en el cerrojo.
     * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
     */
    public boolean liberar() throws RemoteException {
        
        if (exclusivo) { //Si el modo es del escritor.
            LinkedList<ObjetoCompartido> obs = new LinkedList<ObjetoCompartido>(); //Crea una lista temporal de objetos.

            for (ObjetoCompartido objeto : objetos) { //Recorre todos los objetos locales.
                objeto.incVersion(); //Incrementa su versión.
                obs.add(objeto); //Añade el objeto a la lista temporal.
            }

            almacen.escribirObjetos(obs); //Escribe la lista temporal en el almacén.
        }

        return cerrojo.liberar();
   }
}
