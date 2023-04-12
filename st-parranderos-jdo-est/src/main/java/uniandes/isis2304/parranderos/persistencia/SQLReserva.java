package uniandes.isis2304.parranderos.persistencia;

import java.sql.Timestamp;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLReserva {
	private final static String SQL = PersistenciaParranderos.SQL;
	private PersistenciaParranderos pp;
	public SQLReserva (PersistenciaParranderos pp) {
		this.pp = pp;
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long crearReserva (PersistenceManager pm, long id_reserva, Timestamp creacion, Timestamp inicio, int periodos, int costo, long cliente, long oferta) {
        Query q = pm.newQuery(SQL, "INSERT INTO Reserva" + "(id_reserva, creacion, inicio, periodos, costo, cliente, oferta) values (?,?,?,?,?,?,?)");
        q.setParameters(id_reserva, creacion, inicio, periodos, costo, cliente, oferta);
        return (long) q.executeUnique();
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long borrarReserva (PersistenceManager pm, long id_reserva) {
        Query q = pm.newQuery(SQL, "DELETE FROM Reserva WHERE id_reserva = ?");
        q.setParameters(id_reserva);
        return (long) q.executeUnique();
	}
}
