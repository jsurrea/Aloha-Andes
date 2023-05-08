package uniandes.isis2304.alohandes.persistencia;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLReservaColectiva {
	private final static String SQL = PersistenciaAlohandes.SQL;
	private PersistenciaAlohandes pp;
	public SQLReservaColectiva (PersistenciaAlohandes pp) {
		this.pp = pp;
	}
	public long crearReservaColectiva (PersistenceManager pm, long id_reserva_colectiva, long[] id_reservas) {
		long total = 0L;
		for(long id_reserva : id_reservas) {			
			Query q = pm.newQuery(SQL, "INSERT INTO ReservaColectiva (id_reserva_colectiva, id_reserva) values (?,?)");
			q.setParameters(id_reserva_colectiva, id_reserva);
			total += (long) q.executeUnique();
		}
        return total;
	}
	public long[] reservasAsociadas(PersistenceManager pm, long id_reserva_colectiva) {
        Query q = pm.newQuery(SQL, "SELECT id_reserva_colectiva, id_reserva FROM ReservaColectiva WHERE id_reserva_colectiva = ?");
        List<Object[]> results = (List<Object[]>) q.execute(id_reserva_colectiva);
        long[] reservas = new long[results.size()];
        for(int i=0; i<results.size(); i++) {
        	Object[] row = (Object[]) results.get(i);
        	reservas[i] = (long) row[1];
        }
        return reservas;
	}
}
