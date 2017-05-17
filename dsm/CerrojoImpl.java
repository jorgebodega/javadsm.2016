/**
* @author Jorge Bodega
* @author Daniel de Diego
*
* @version 1.2
*/

package dsm;

import java.rmi.*;
import java.rmi.server.*;

class CerrojoImpl extends UnicastRemoteObject implements Cerrojo {

	private static final long serialVersionUID = 14L;
	private int lectores; //Número de lectores.
	private boolean escritor; //Marca si hay un escritor modificando.

    /**
     * Crea un nuevo objeto.
     * 
     * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
     */
    public CerrojoImpl() throws RemoteException {}

    /**
     * Adquiere la linea de ejecución del cerrojo, dictando quien puede seguir ejecutando y quien se queda esperando.
     * 
     * @param exc Varieble que marca quien intenta acceder al cerrojo. <b>TRUE</b> si Escritor.
     * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
     */
    public synchronized void adquirir (boolean exc) throws RemoteException {
    
    	try{ //Comprueba que no salte ninguna excepción en la parada de los hilos.
    		while(true){
    			if(exc){ //Si el modo de ejecución es TRUE, un escritor intenta acceder.
    				if(lectores != 0 || escritor) wait(); //Si hay algún lector o escritor, mandamos esperar al hilo.
    				else{
    					escritor = true; //Activa el flag de escritor.
    					return; //Paramos la ejecución.
    				}
    			}
    			else{
    				if(escritor) wait(); //Si hay algun escritor, los lectores se quedan parados.
    				else{
    					lectores++; //Aumentamos el número de lectores.
    					return; //Paramos la ejecución.
    				}
    			}
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}

    }

    /**
     * Libera un hilo de ejecución atrapado en el cerrojo. Si hay un escritor libera a todos, si hay lectores libera un hilo aleatorio.
     * 
     * @return TRUE si se ha liberado un hilo de ejecución, FALSE en otro caso.
     * @throws RemoteException Error en la ejecución en la llamada a un método remoto.
     */
    public synchronized boolean liberar() throws RemoteException {
        
        if(!escritor && lectores == 0) return false; //Si no existen ni lectores ni escritor, no hay nada que liberar.

        if(escritor){ //Si hay algun escritor.
        	escritor = false;
        	notifyAll(); //Liberamos todos los procesos parados.
        }
        else{
        	lectores--; //Reducimos el número de lectores.
        	notify(); //Liberamos un hilo de ejecución aleatorio de todos los que están parados.
        }

        return true;
    }
}
