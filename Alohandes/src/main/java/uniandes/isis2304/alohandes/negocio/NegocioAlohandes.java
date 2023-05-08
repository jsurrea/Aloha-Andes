package uniandes.isis2304.alohandes.negocio;

import java.sql.Timestamp;

import org.apache.log4j.Logger;
import com.google.gson.JsonObject;

import uniandes.isis2304.alohandes.persistencia.PersistenciaAlohandes;


public class NegocioAlohandes 
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(NegocioAlohandes.class.getName());
	
	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * El manejador de persistencia
	 */
	private PersistenciaAlohandes pp;
	
	/* ****************************************************************
	 * 			Métodos
	 *****************************************************************/
	/**
	 * El constructor por defecto
	 */
	public NegocioAlohandes ()
	{
		pp = PersistenciaAlohandes.getInstance ();
	}
	
	/**
	 * El constructor qye recibe los nombres de las tablas en tableConfig
	 * @param tableConfig - Objeto Json con los nombres de las tablas y de la unidad de persistencia
	 */
	public NegocioAlohandes (JsonObject tableConfig)
	{
		pp = PersistenciaAlohandes.getInstance (tableConfig);
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

	public Operador crearOperador (String nombre, String correo, String contrasena, String tipo_operador) {
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
	
	public Cliente crearCliente (long cedula, String nombre, String correo, String contrasena, String tipo) {
        log.info ("Adicionando Cliente");
        Cliente cliente = pp.crearCliente(cedula, nombre, correo, contrasena, tipo);		
        log.info ("Adicionado Cliente");
        return cliente;
	}
	
	public Reserva crearReserva (Timestamp creacion, Timestamp inicio, int periodos, int costo, long cliente, long oferta) {
        log.info ("Adicionando Reserva");
        Reserva reserva = pp.crearReserva(creacion, inicio, periodos, costo, cliente, oferta);		
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
	
	public Reserva[] crearReservaColectiva (String tipo, int cantidad) {
        log.info ("Adicionando ReservaColectiva");
        Reserva[] reservas = pp.crearReservaColectiva(tipo, cantidad);		
        log.info ("Adicionado ReservaColectiva");
        return reservas;
	}
	
	public long borrarReservaColectiva (long id_reserva_colectiva) {
        log.info ("Borrando ReservaColectiva");
        long reservas = pp.borrarReservaColectiva(id_reserva_colectiva);		
        log.info ("Borrando ReservaColectiva");
        return reservas;
	}
	
	public Reserva[] deshabilitarOferta (long id_oferta) {
		log.info ("Ejecutando deshabilitarOferta");
		Reserva[] reservas = pp.deshabilitarOferta(id_oferta);		
		log.info ("Ejecutando deshabilitarOferta");
		return reservas;
	}
	
	public long habilitarOferta (long id_oferta) {
        log.info ("Ejecutando habilitarOferta");
        long rows = pp.habilitarOferta(id_oferta);		
        log.info ("Ejecutando habilitarOferta");
        return rows;
	}
	
	/* ****************************************************************
	 * 			Requerimientos Consulta
	 *****************************************************************/
	
	public String ingresosRecibidos() {
		log.info("Ejecutando ingresos recibidos");
		String resp = pp.ingresosRecibidos();
		log.info("Terminó ingresos recibidos");
		return resp;
	}
	
	public String ofertasPopulares() {
		log.info("Ejecutando ofertasPopulares");
		String resp = pp.ofertasPopulares();
		log.info("Terminó ofertasPopulares");
		return resp;
	}
	
	public String indiceOcupacion() {
		log.info("Ejecutando indiceOcupacion");
		String resp = pp.indiceOcupacion();
		log.info("Terminó indiceOcupacion");
		return resp;
	}
	
	public String alojamientosDisponibles(String[] servicios) {
		log.info("Ejecutando alojamientosDisponibles");
		String resp = pp.alojamientosDisponibles(servicios);
		log.info("Terminó alojamientosDisponibles");
		return resp;
	}
	
	public String usoTipoUsuario() {
		log.info("Ejecutando usoTipoUsuario");
		String resp = pp.usoTipoUsuario();
		log.info("Terminó usoTipoUsuario");
		return resp;
	}
	
	public String usoUsuario(long cedula) {
		log.info("Ejecutando usoUsuario");
		String resp = pp.usoUsuario(cedula);
		log.info("Terminó usoUsuario");
		return resp;
	}

	public String analizarOperacion(String tipo) {
		log.info("Ejecutando analizarOperacion");
		String resp = pp.analizarOperacion(tipo);
		log.info("Terminó analizarOperacion");
		return resp;
	}
	
	public String clientesFrecuentes(long id_oferta) {
		log.info("Ejecutando clientesFrecuentes");
		String resp = pp.clientesFrecuentes(id_oferta);
		log.info("Terminó clientesFrecuentes");
		return resp;
	}
	
	public String ofertasBajaDemanda() {
		log.info("Ejecutando ofertasBajaDemanda");
		String resp = pp.ofertasBajaDemanda();
		log.info("Terminó ofertasBajaDemanda");
		return resp;
	}
}