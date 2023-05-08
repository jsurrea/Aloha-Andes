package uniandes.isis2304.alohandes.persistencia;

import java.sql.Timestamp;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLOfertaAlojamiento {
	private final static String SQL = PersistenciaAlohandes.SQL;
	private PersistenciaAlohandes pp;
	public SQLOfertaAlojamiento (PersistenciaAlohandes pp) {
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
	
	public String ofertasPopulares(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, "SELECT * FROM ( SELECT OA.id_oferta, COUNT(R.id_reserva) AS RESERVAS FROM OFERTAALOJAMIENTO OA LEFT JOIN RESERVA R ON OA.id_oferta = R.oferta GROUP BY OA.id_oferta ORDER BY RESERVAS DESC) WHERE ROWNUM <= 20");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |Oferta|# Reservas|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String indiceOcupacion(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, "SELECT OA.id_oferta, NULLIF(SQ.reservas, 0) / OA.capacidad AS INDICE FROM OFERTAALOJAMIENTO OA LEFT JOIN (SELECT R.oferta, COUNT(R.id_reserva) AS RESERVAS FROM RESERVA R GROUP BY R.oferta) SQ ON OA.id_oferta = SQ.oferta");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |Oferta|Índice|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String alojamientosDisponibles(PersistenceManager pm, String[] servicios) {
		StringBuilder query = new StringBuilder();
		query.append("SELECT DISTINCT OA.id_oferta FROM OFERTAALOJAMIENTO OA LEFT JOIN SERVICIO S ON OA.id_oferta = S.oferta WHERE OA.retiro IS NULL");
		if(servicios.length > 0 && !servicios[0].equals("")) {
			query.append(" AND ( ");
			query.append(" S.TIPO = '");
			query.append(servicios[0].toUpperCase().trim());
			query.append("' ");
			for(int i = 1; i < servicios.length; i++) {
				query.append(" OR S.tipo = '");
				query.append(servicios[i]);
				query.append("' ");
			}
			query.append(" ) ");
		}
		Query q = pm.newQuery(SQL, query.toString());
		List<Object> results = (List<Object>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |ID Oferta|\n");
		for (Object row : results) {
		    rta.append(" | " + row);
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String clientesFrecuentes(PersistenceManager pm, long id_oferta) {
		Query q = pm.newQuery(SQL, "SELECT c.nombre, c.cedula, COUNT(*) as utilizado, SUM(r.periodos) as noches FROM cliente c INNER JOIN reserva r ON c.cedula = r.cliente WHERE r.oferta = ? GROUP BY c.nombre, c.cedula HAVING COUNT(*) >= 3 OR SUM(r.periodos) >= 15");
		List<Object[]> results = (List<Object[]>) q.execute(id_oferta);
		StringBuilder rta = new StringBuilder();
		rta.append(" |Nombre|Cédula|# Reservas|# Noches|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String ofertasBajaDemanda(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, "SELECT ofe.id_oferta, ofe.tipo FROM ofertaalojamiento ofe WHERE NOT EXISTS ( SELECT 1 FROM reserva r WHERE r.oferta = ofe.id_oferta AND r.inicio >= (SYSDATE - INTERVAL '1' MONTH))");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |ID Oferta|Tipo|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public long deshabilitarOferta (PersistenceManager pm, long id_oferta) {
        Query q = pm.newQuery(SQL, "UPDATE OfertaAlojamiento SET retiro = ? WHERE id_oferta = ?");
        q.setParameters(new Timestamp(System.currentTimeMillis()), id_oferta);
        // TODO
        return (long) q.executeUnique();
	}
	
	public int cantidadReservas(PersistenceManager pm, long id_oferta) {
		String query = "SELECT id_reserva, cliente FROM RESERVA WHERE OFERTA = ?";
        Query q = pm.newQuery(SQL, query);
        List<Object[]> results = (List<Object[]>) q.execute(id_oferta);
        long[] reserva = new long[results.size()];
        long[] cliente = new long[results.size()];
        for(int i=0; i<results.size(); i++) {
        	Object[] row = (Object[]) results.get(i);
        	reserva[i] = ((java.math.BigDecimal) row[0]).longValue();
        	cliente[i] = ((java.math.BigDecimal) row[1]).longValue();
        }
        return reserva.length;
	}
	
	/**
	 * @return El número de tuplas insertadas
	 */
	public long habilitarOferta (PersistenceManager pm, long id_oferta) {
        Query q = pm.newQuery(SQL, "UPDATE OfertaAlojamiento SET retiro = NULL WHERE id_oferta = ?");
        q.setParameters(id_oferta);
        return (long) q.executeUnique();
	}
}
