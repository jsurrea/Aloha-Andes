package uniandes.isis2304.parranderos.negocio;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;
import uniandes.isis2304.parranderos.persistencia.PersistenciaParranderos;


public class Parranderos 
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(Parranderos.class.getName());
	
	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * El manejador de persistencia
	 */
	private PersistenciaParranderos pp;
	
	/* ****************************************************************
	 * 			Métodos
	 *****************************************************************/
	/**
	 * El constructor por defecto
	 */
	public Parranderos ()
	{
		pp = PersistenciaParranderos.getInstance ();
	}
	
	/**
	 * El constructor qye recibe los nombres de las tablas en tableConfig
	 * @param tableConfig - Objeto Json con los nombres de las tablas y de la unidad de persistencia
	 */
	public Parranderos (JsonObject tableConfig)
	{
		pp = PersistenciaParranderos.getInstance (tableConfig);
	}
	
	/**
	 * Cierra la conexión con la base de datos (Unidad de persistencia)
	 */
	public void cerrarUnidadPersistencia ()
	{
		pp.cerrarUnidadPersistencia ();
	}
	
	/* ****************************************************************
	 * 			Requerimientos Funcionales
	 *****************************************************************/

	public Operador crearOperador (String nombre, String correo, String contrasena, String tipo_operador, String alohandes) {
        log.info ("Adicionando Operador");
        Operador operador = pp.crearOperador(nombre, correo, contrasena, tipo_operador);		
        log.info ("Adicionado Operador");
        return operador;
	}
	
	public OfertaAlojamiento crearOfertaAlojamiento (String tipo, int costo, int capacidad, String periodicidad, int tiempo_minimo, String ubicacion, int tamano, Timestamp retiro, long operador) {
        log.info ("Adicionando OfertaAlojamiento");
        OfertaAlojamiento ofertaAlojamiento = pp.crearOfertaAlojamiento(tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador);
        log.info ("Adicionado OfertaAlojamiento");
        return ofertaAlojamiento;
	}
	
	public Cliente crearCliente (long cedula, String nombre, String correo, String contrasena, String tipo, String alohandes) {
        log.info ("Adicionando Cliente");
        Cliente cliente = pp.crearCliente(cedula, nombre, correo, contrasena, tipo);		
        log.info ("Adicionado Cliente");
        return cliente;
	}
	
	public Reserva crearReserva (Timestamp creacion, Timestamp inicio, int periodos, int costo) {
        log.info ("Adicionando Reserva");
        Reserva reserva = pp.crearReserva(creacion, inicio, periodos, costo);		
        log.info ("Adicionado Reserva");
        return reserva;
	}
	
	public long borrarReserva (long id_reserva) {
		log.info ("Eliminando Reserva por id: " + id_reserva);
        long resp = pp.borrarReserva(id_reserva);
        log.info ("Eliminando Reserva por id: " + resp + " tuplas eliminadas");
        return resp;
	}
	
	public long borrarOfertaAlojamiento (long id_oferta) {
		log.info ("Eliminando OfertaAlojamiento por id: " + id_oferta);
        long resp = pp.borrarOfertaAlojamiento(id_oferta);
        log.info ("Eliminando OfertaAlojamiento por id: " + resp + " tuplas eliminadas");
        return resp;
	}
}