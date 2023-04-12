package uniandes.isis2304.parranderos.persistencia;

import java.sql.Timestamp;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLOfertaAlojamiento {
	private final static String SQL = PersistenciaParranderos.SQL;
	private PersistenciaParranderos pp;
	public SQLOfertaAlojamiento (PersistenciaParranderos pp) {
		this.pp = pp;
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long crearOfertaAlojamiento (PersistenceManager pm, long id_oferta, String tipo, int costo, int capacidad, String periodicidad, int tiempo_minimo, String ubicacion, int tamano, Timestamp retiro, long operador) {
        Query q = pm.newQuery(SQL, "INSERT INTO OfertaAlojamiento" + "(id_oferta, tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador) values (?,?,?,?,?,?,?,?,?,?)");
        q.setParameters(id_oferta, tipo, costo, capacidad, periodicidad, tiempo_minimo, ubicacion, tamano, retiro, operador);
        return (long) q.executeUnique();
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long borrarOfertaAlojamiento (PersistenceManager pm, long id_oferta) {
        Query q = pm.newQuery(SQL, "DELETE FROM OfertaAlojamiento WHERE id_oferta = ?");
        q.setParameters(id_oferta);
        return (long) q.executeUnique();
	}
}
