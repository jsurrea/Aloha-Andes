/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Universidad	de	los	Andes	(Bogotá	- Colombia)
 * Departamento	de	Ingeniería	de	Sistemas	y	Computación
 * Licenciado	bajo	el	esquema	Academic Free License versión 2.1
 * 		
 * Curso: isis2304 - Sistemas Transaccionales
 * Proyecto: Parranderos Uniandes
 * @version 1.0
 * @author Germán Bravo
 * Julio de 2018
 * 
 * Revisado por: Claudia Jiménez, Christian Ariza
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */

package uniandes.isis2304.parranderos.persistencia;


import java.math.BigDecimal;
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

import uniandes.isis2304.parranderos.negocio.Cliente;
import uniandes.isis2304.parranderos.negocio.OfertaAlojamiento;
import uniandes.isis2304.parranderos.negocio.Operador;
import uniandes.isis2304.parranderos.negocio.Reserva;

/**
 * Clase para el manejador de persistencia del proyecto Parranderos
 * Traduce la información entre objetos Java y tuplas de la base de datos, en ambos sentidos
 * Sigue un patrón SINGLETON (Sólo puede haber UN objeto de esta clase) para comunicarse de manera correcta
 * con la base de datos
 * Se apoya en las clases SQLBar, SQLBebedor, SQLBebida, SQLGustan, SQLSirven, SQLTipoBebida y SQLVisitan, que son 
 * las que realizan el acceso a la base de datos
 * 
 * @author Germán Bravo
 */
public class PersistenciaParranderos 
{
	/* ****************************************************************
	 * 			Constantes
	 *****************************************************************/
	/**
	 * Logger para escribir la traza de la ejecución
	 */
	private static Logger log = Logger.getLogger(PersistenciaParranderos.class.getName());
	
	/**
	 * Cadena para indicar el tipo de sentencias que se va a utilizar en una consulta
	 */
	public final static String SQL = "javax.jdo.query.SQL";
	
	/* ****************************************************************
	 * 			Otras Constantes
	 *****************************************************************/
	
	/**
	 * Instancia de AlohAndes necesaria para esta etapa del negocio
	 * TODO -> Cambiar cuando se añadan más universidades, o eliminar
	 */
	public final static String UNIVERSIDAD = "Universidad de los Andes";

	/* ****************************************************************
	 * 			Atributos
	 *****************************************************************/
	/**
	 * Atributo privado que es el único objeto de la clase - Patrón SINGLETON
	 */
	private static PersistenciaParranderos instance;
	
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
	 * Atributo para el acceso a las sentencias SQL propias a PersistenciaParranderos
	 */
	private SQLUtil sqlUtil;
	private SQLAlohAndes sqlAlohAndes;
	private SQLCliente sqlCliente;
	private SQLOperador sqlOperador;
	private SQLOfertaAlojamiento sqlOfertaAlojamiento;
	private SQLReserva sqlReserva;
	private SQLServicio sqlServicio;
	private SQLServicioAdquirido sqlServicioAdquirido;
	
	/* ****************************************************************
	 * 			Métodos del MANEJADOR DE PERSISTENCIA
	 *****************************************************************/

	/**
	 * Constructor privado con valores por defecto - Patrón SINGLETON
	 */
	private PersistenciaParranderos ()
	{
		pmf = JDOHelper.getPersistenceManagerFactory("Parranderos");		
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

}

	/**
	 * Constructor privado, que recibe los nombres de las tablas en un objeto Json - Patrón SINGLETON
	 * @param tableConfig - Objeto Json que contiene los nombres de las tablas y de la unidad de persistencia a manejar
	 */
	private PersistenciaParranderos (JsonObject tableConfig)
	{
		crearClasesSQL ();
		tablas = leerNombresTablas (tableConfig);
		
		String unidadPersistencia = tableConfig.get ("unidadPersistencia").getAsString ();
		log.trace ("Accediendo unidad de persistencia: " + unidadPersistencia);
		pmf = JDOHelper.getPersistenceManagerFactory (unidadPersistencia);
	}

	/**
	 * @return Retorna el único objeto PersistenciaParranderos existente - Patrón SINGLETON
	 */
	public static PersistenciaParranderos getInstance ()
	{
		if (instance == null)
		{
			instance = new PersistenciaParranderos ();
		}
		return instance;
	}
	
	/**
	 * Constructor que toma los nombres de las tablas de la base de datos del objeto tableConfig
	 * @param tableConfig - El objeto JSON con los nombres de las tablas
	 * @return Retorna el único objeto PersistenciaParranderos existente - Patrón SINGLETON
	 */
	public static PersistenciaParranderos getInstance (JsonObject tableConfig)
	{
		if (instance == null)
		{
			instance = new PersistenciaParranderos (tableConfig);
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
	}
	
	/**
	 * Transacción para el generador de secuencia de Parranderos
	 * Adiciona entradas al log de la aplicación
	 * @return El siguiente número del secuenciador de Parranderos
	 */
	private long nextval ()
	{
        long resp = sqlUtil.nextval (pmf.getPersistenceManager());
        log.trace ("Generando secuencia: " + resp);
        return resp;
    }
	
	/**
	 * Extrae el mensaje de la exception JDODataStoreException embebido en la Exception e, que da el detalle específico del problema encontrado
	 * @param e - La excepción que ocurrio
	 * @return El mensaje de la excepción JDO
	 */
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
 }
