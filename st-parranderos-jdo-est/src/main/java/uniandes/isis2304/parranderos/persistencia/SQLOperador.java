package uniandes.isis2304.parranderos.persistencia;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLOperador {
	private final static String SQL = PersistenciaParranderos.SQL;
	private PersistenciaParranderos pp;
	public SQLOperador (PersistenciaParranderos pp) {
		this.pp = pp;
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long crearOperador (PersistenceManager pm, long id_convenio, String nombre, String correo, String contrasena, String tipo_operador, String alohandes) {
        Query q = pm.newQuery(SQL, "INSERT INTO Operador" + "(id_convenio, nombre, correo, contrasena, tipo_operador, alohandes) values (?,?,?,?,?,?)");
        q.setParameters(id_convenio, nombre, correo, contrasena, tipo_operador, alohandes);
        return (long) q.executeUnique();
	}
}
