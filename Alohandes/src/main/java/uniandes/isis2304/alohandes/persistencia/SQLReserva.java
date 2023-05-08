package uniandes.isis2304.alohandes.persistencia;

import java.sql.Timestamp;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLReserva {
	private final static String SQL = PersistenciaAlohandes.SQL;
	private PersistenciaAlohandes pp;
	public SQLReserva (PersistenciaAlohandes pp) {
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
	
	public boolean existeCapacidad(PersistenceManager pm, String tipo, int capacidad) {
		String query = "SELECT R.oferta, OA.capacidad FROM RESERVA R LEFT JOIN OFERTAALOJAMIENTO OA ON R.oferta = OA.id_oferta";
		if(tipo != null) query += " WHERE OA.tipo = ?";
        Query q = pm.newQuery(SQL, query);
        List<Object[]> results;
        if(tipo != null) {
        	results = (List<Object[]>) q.execute(tipo);
        }
        else {
        	results = (List<Object[]>) q.execute();
        }
        long capacidadTotal = 0;
		for (Object[] row : results) {
			capacidadTotal += ((java.math.BigDecimal) row[1]).longValue();   
		}
        return capacidadTotal >= capacidad;
	}
	
	public long[] ofertasDisponibles(PersistenceManager pm, String tipo) {
		String query = "SELECT R.oferta, OA.capacidad FROM RESERVA R LEFT JOIN OFERTAALOJAMIENTO OA ON R.oferta = OA.id_oferta";
		if(tipo != null) query += " WHERE OA.tipo = ?";
        Query q = pm.newQuery(SQL, query);
        List<Object[]> results;
        if(tipo != null) {
        	results = (List<Object[]>) q.execute(tipo);
        }
        else {
        	results = (List<Object[]>) q.execute();
        }
        long[] ofertas = new long[results.size()];
        for(int i=0; i<results.size(); i++) {
        	Object[] row = (Object[]) results.get(i);
        	ofertas[i] = ((java.math.BigDecimal) row[0]).longValue();
        }
        return ofertas;
	}
}
