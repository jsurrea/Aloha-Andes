package uniandes.isis2304.parranderos.persistencia;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLCliente {
	private final static String SQL = PersistenciaParranderos.SQL;
	private PersistenciaParranderos pp;
	public SQLCliente (PersistenciaParranderos pp) {
		this.pp = pp;
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long crearCliente (PersistenceManager pm, long cedula, String nombre, String correo, String contrasena, String tipo, String alohandes) {
        Query q = pm.newQuery(SQL, "INSERT INTO Cliente" + "(cedula, nombre, correo, contrasena, tipo, alohandes) values (?, ?, ?, ?, ?, ?)");
        q.setParameters(cedula, nombre, correo, contrasena, tipo, alohandes);
        return (long) q.executeUnique();
	}
}
