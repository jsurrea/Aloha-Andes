package uniandes.isis2304.alohandes.persistencia;


import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.JDODataStoreException;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.apache.log4j.Logger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import uniandes.isis2304.alohandes.negocio.Cliente;
import uniandes.isis2304.alohandes.negocio.OfertaAlojamiento;
import uniandes.isis2304.alohandes.negocio.Operador;
import uniandes.isis2304.alohandes.negocio.Reserva;

/**
 * Clase para el manejador de persistencia del proyecto 
 * Traduce la información entre objetos Java y tuplas de la base de datos, en ambos sentidos
 * Sigue un patrón SINGLETON (Sólo puede haber UN objeto de esta clase) para comunicarse de manera correcta
 * con la base de datos
 * Se apoya en las clases SQLBar, SQLBebedor, SQLBebida, SQLGustan, SQLSirven, SQLTipoBebida y SQLVisitan, que son 
 * las que realizan el acceso a la base de datos
 * 
 * @author Germán Bravo
 */
public class PersistenciaAlohandes 
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(PersistenciaAlohandes.class.getName());
	
	/**
	 * Cadena para indicar el tipo de sentencias que se va a utilizar en una consulta
	 */
	public final static String SQL = "javax.jdo.query.SQL";
	
	/* ****************************************************************
	 * 			Otras Constantes
	 *****************************************************************/
	
	/**
	 * Instancia de AlohAndes necesaria para esta etapa del negocio
	 */
	public final static String UNIVERSIDAD = "Universidad de los Andes";

	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * Atributo privado que es el único objeto de la clase - Patrón SINGLETON
	 */
	private static PersistenciaAlohandes instance;
	
	/**
	 * Fábrica de Manejadores de persistencia, para el manejo correcto de las transacciones
	 */
	private PersistenceManagerFactory pmf;
	
	/**
	 * Arreglo de cadenas con los nombres de las tablas de la base de datos, en su orden:
	 * Secuenciador, tipoBebida, bebida, bar, bebedor, gustan, sirven y visitan
	 */
	private List <String> tablas;
	
	/**
	 * Atributo para el acceso a las sentencias SQL propias a Persistencia
	 */
	private SQLUtil sqlUtil;
	private SQLAlohAndes sqlAlohAndes;
	private SQLCliente sqlCliente;
	private SQLOperador sqlOperador;
	private SQLOfertaAlojamiento sqlOfertaAlojamiento;
	private SQLReserva sqlReserva;
	private SQLServicio sqlServicio;
	private SQLServicioAdquirido sqlServicioAdquirido;
	private SQLReservaColectiva sqlReservaColectiva;
	
	/* ****************************************************************
	 * 			Métodos del MANEJADOR DE PERSISTENCIA
	 *****************************************************************/

	/**
	 * Constructor privado con valores por defecto - Patrón SINGLETON
	 */
	private PersistenciaAlohandes ()
	{
		pmf = JDOHelper.getPersistenceManagerFactory("Alohandes");		
		crearClasesSQL ();
		
		// Define los nombres por defecto de las tablas de la base de datos
		tablas = new LinkedList<String> ();
		tablas.add("index_sequence");
		tablas.add("AlohAndes");
		tablas.add("Cliente");
		tablas.add("Operador");
		tablas.add("OfertaAlojamiento");
		tablas.add("Reserva");
		tablas.add("Servicio");
		tablas.add("ServicioAdquirido");
		tablas.add("ReservaColectiva");

}

	/**
	 * Constructor privado, que recibe los nombres de las tablas en un objeto Json - Patrón SINGLETON
	 * @param tableConfig - Objeto Json que contiene los nombres de las tablas y de la unidad de persistencia a manejar
	 */
	private PersistenciaAlohandes (JsonObject tableConfig)
	{
		crearClasesSQL ();
		tablas = leerNombresTablas (tableConfig);
		
		String unidadPersistencia = tableConfig.get ("unidadPersistencia").getAsString ();
		log.trace ("Accediendo unidad de persistencia: " + unidadPersistencia);
		pmf = JDOHelper.getPersistenceManagerFactory (unidadPersistencia);
	}

	/**
	 * @return Retorna el único objeto Persistencia existente - Patrón SINGLETON
	 */
	public static PersistenciaAlohandes getInstance ()
	{
		if (instance == null)
		{
			instance = new PersistenciaAlohandes ();
		}
		return instance;
	}
	
	/**
	 * Constructor que toma los nombres de las tablas de la base de datos del objeto tableConfig
	 * @param tableConfig - El objeto JSON con los nombres de las tablas
	 * @return Retorna el único objeto Persistencia existente - Patrón SINGLETON
	 */
	public static PersistenciaAlohandes getInstance (JsonObject tableConfig)
	{
		if (instance == null)
		{
			instance = new PersistenciaAlohandes (tableConfig);
		}
		return instance;
	}

	/**
	 * Cierra la conexión con la base de datos
	 */
	public void cerrarUnidadPersistencia ()
	{
		pmf.close ();
		instance = null;
	}
	
	/**
	 * Genera una lista con los nombres de las tablas de la base de datos
	 * @param tableConfig - El objeto Json con los nombres de las tablas
	 * @return La lista con los nombres del secuenciador y de las tablas
	 */
	private List <String> leerNombresTablas (JsonObject tableConfig)
	{
		JsonArray nombres = tableConfig.getAsJsonArray("tablas") ;

		List <String> resp = new LinkedList <String> ();
		for (JsonElement nom : nombres)
		{
			resp.add (nom.getAsString ());
		}
		
		return resp;
	}
	
	/**
	 * Crea los atributos de clases de apoyo SQL
	 */
	private void crearClasesSQL ()
	{
		sqlAlohAndes = new SQLAlohAndes(this);
		sqlCliente = new SQLCliente(this);
		sqlOperador = new SQLOperador(this);
		sqlOfertaAlojamiento = new SQLOfertaAlojamiento(this);
		sqlReserva = new SQLReserva(this);
		sqlServicio = new SQLServicio(this);
		sqlServicioAdquirido = new SQLServicioAdquirido(this);
		sqlUtil = new SQLUtil(this);
		sqlReservaColectiva = new SQLReservaColectiva(this);
	}
	
	/**
	 * Transacción para el generador de secuencia de Alohandes
	 * Adiciona entradas al log de la aplicación
	 * @return El siguiente número del secuenciador de Alohandes
	 */
	private long nextval ()
	{
        long resp = sqlUtil.nextval (pmf.getPersistenceManager());
        log.trace ("Generando secuencia: " + resp);
        return resp;
    }
	
	private String darDetalleException(Exception e) 
	{
		String resp = "";
		if (e.getClass().getName().equals("javax.jdo.JDODataStoreException"))
		{
			JDODataStoreException je = (javax.jdo.JDODataStoreException) e;
			return je.getNestedExceptions() [0].getMessage();
		}
		return resp;
	}

	/* ****************************************************************
	 * 			Métodos de los REQUERIMIENTOS FUNCIONALES
	 *****************************************************************/
	
	public Operador crearOperador(String nombre, String correo, String contrasena, String tipo_operador) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long id_convenio = nextval ();
			long tuplasInsertadas = sqlOperador.crearOperador(pm, id_convenio, nombre, correo, contrasena, tipo_operador, UNIVERSIDAD);
			tx.commit();

			log.trace ("Inserción de Operador: " + id_convenio + ": " + tuplasInsertadas + " tuplas insertadas");

			return new Operador (id_convenio, nombre, correo, contrasena, tipo_operador, UNIVERSIDAD);
		}
		catch (Exception e)
		{
//      	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public OfertaAlojamiento crearOfertaAlojamiento(String tipo, int costo, int capacidad, String periodicidad, int tiempo_minimo, String ubicacion, int tamano, Timestamp retiro, long operador) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long id_oferta = nextval ();
			long tuplasInsertadas = sqlOfertaAlojamiento.crearOfertaAlojamiento(pm, id_oferta, tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador);
			tx.commit();

			log.trace ("Inserción de OfertaAlojamiento: " + id_oferta + ": " + tuplasInsertadas + " tuplas insertadas");

			return new OfertaAlojamiento (id_oferta, tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador);
		}
		catch (Exception e)
		{
//      	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public Cliente crearCliente(long cedula, String nombre, String correo, String contrasena, String tipo) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long tuplasInsertadas = sqlCliente.crearCliente(pm, cedula, nombre, correo, contrasena, tipo, UNIVERSIDAD);
			tx.commit();

			log.trace ("Inserción de Cliente: " + cedula + ": " + tuplasInsertadas + " tuplas insertadas");

			return new Cliente (cedula, nombre, correo, contrasena, tipo, UNIVERSIDAD);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public Reserva crearReserva(Timestamp creacion, Timestamp inicio, int periodos, int costo, long cliente, long oferta) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long id_reserva = nextval ();
			long tuplasInsertadas = sqlReserva.crearReserva(pm, id_reserva, creacion, inicio, periodos, costo, cliente, oferta);
			tx.commit();

			log.trace ("Inserción de Reserva: " + id_reserva + ": " + tuplasInsertadas + " tuplas insertadas");

			return new Reserva (id_reserva, creacion, inicio, periodos, costo, cliente, oferta);
		}
		catch (Exception e)
		{
//      	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public long borrarReserva (long id_reserva) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long resp = sqlReserva.borrarReserva (pm, id_reserva);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return -1;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public long borrarOfertaAlojamiento (long id_oferta) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long resp = sqlOfertaAlojamiento.borrarOfertaAlojamiento (pm, id_oferta);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return -1;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public Reserva[] crearReservaColectiva(String tipo, int cantidad) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			if (!sqlReserva.existeCapacidad(pm, tipo, cantidad)) {
				return null;
			}
			long[] ofertas = sqlReserva.ofertasDisponibles(pm, tipo);
			Reserva[] rta = new Reserva[ofertas.length];
			for(int i=0; i<ofertas.length; i++) {
				long oferta = ofertas[i];
				long id_reserva = nextval ();
				Timestamp now = new Timestamp(System.currentTimeMillis());
				sqlReserva.crearReserva(pm, id_reserva, now, now, 1, 50, 1001, oferta);
				rta[i] = new Reserva (id_reserva, now, now, 1, 50, 1001, oferta);
			}
			long[] id_reservas = new long[rta.length];
			for(int i = 0; i < rta.length; i++) {
				id_reservas[i] = rta[i].getId_reserva();
			}
			sqlReservaColectiva.crearReservaColectiva(pm, nextval (), id_reservas);
			tx.commit();
			log.trace ("Inserción de Reservas: " + ofertas.length + " tuplas insertadas");

			return rta;
		}
		catch (Exception e)
		{
//      	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public long borrarReservaColectiva (long id_reserva_colectiva) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long[] reservas = sqlReservaColectiva.reservasAsociadas(pm, id_reserva_colectiva);
			for(int i=0; i<reservas.length; i++) {
				long id_reserva = reservas[i];
				sqlReserva.borrarReserva (pm, id_reserva);
			}
			tx.commit();
			return reservas.length;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return -1;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public Reserva[] deshabilitarOferta(long id_oferta) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			int cantidad = sqlOfertaAlojamiento.cantidadReservas(pm, id_oferta);
			if (!sqlReserva.existeCapacidad(pm, null, cantidad)) {
				return null;
			}
			sqlOfertaAlojamiento.deshabilitarOferta(pm, id_oferta);
			long[] ofertas = sqlReserva.ofertasDisponibles(pm, null);
			Reserva[] rta = new Reserva[ofertas.length];
			for(int i=0; i<ofertas.length; i++) {
				long oferta = ofertas[i];
				long id_reserva = nextval ();
				Timestamp now = new Timestamp(System.currentTimeMillis());
				sqlReserva.crearReserva(pm, id_reserva, now, now, 1, 50, 1001, oferta);
				rta[i] = new Reserva (id_reserva, now, now, 1, 50, 1001, oferta);
			}
			tx.commit();
			log.trace ("Inserción de Reservas: " + ofertas.length + " tuplas insertadas");

			return rta;
		}
		catch (Exception e)
		{
//      	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return null;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public long habilitarOferta (long id_oferta) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			long rows = sqlOfertaAlojamiento.habilitarOferta(pm, id_oferta);
			tx.commit();
			return rows;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return -1;
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	/* ****************************************************************
	 * 			Métodos de los REQUERIMIENTOS CONSULTA
	 *****************************************************************/
	
	public String ingresosRecibidos() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOperador.ingresosRecibidos(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String ofertasPopulares() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.ofertasPopulares(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String indiceOcupacion() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.indiceOcupacion(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String alojamientosDisponibles(String[] servicios) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.alojamientosDisponibles(pm, servicios);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}	
	
	public String usoTipoUsuario() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlCliente.usoTipoUsuario(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String usoUsuario(long cedula) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlCliente.usoUsuario(pm, cedula);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String analizarOperacion(String tipo) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.analizarOperacion(pm, tipo);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String clientesFrecuentes(long id_oferta) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.clientesFrecuentes(pm, id_oferta);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String ofertasBajaDemanda() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.ofertasBajaDemanda(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String consultarConsumo1(Long id_oferta, String date1, String date2) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.consultarConsumo1(pm, id_oferta, date1, date2);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String consultarConsumo2(Long id_oferta, String date1, String date2) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.consultarConsumo2(pm, id_oferta, date1, date2);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String consultarFuncionamiento() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlOfertaAlojamiento.consultarFuncionamiento(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
	
	public String consultarBuenosClientes() {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx=pm.currentTransaction();
		try
		{
			tx.begin();
			String resp = sqlCliente.consultarBuenosClientes(pm);
			tx.commit();

			return resp;
		}
		catch (Exception e)
		{
			//       	e.printStackTrace();
			log.error ("Exception : " + e.getMessage() + "\n" + darDetalleException(e));
			return "No se pudo consultar el requerimiento";
		}
		finally
		{
			if (tx.isActive())
			{
				tx.rollback();
			}
			pm.close();
		}
	}
 }
