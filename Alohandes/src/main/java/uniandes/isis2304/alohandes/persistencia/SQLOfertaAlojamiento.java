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
		Query q = pm.newQuery(SQL, "SELECT * FROM (SELECT OA.id_oferta, SQ.reservas / OA.capacidad AS INDICE FROM OFERTAALOJAMIENTO OA LEFT JOIN (SELECT R.oferta, COUNT(R.id_reserva) AS RESERVAS FROM RESERVA R GROUP BY R.oferta) SQ ON OA.id_oferta = SQ.oferta ORDER BY INDICE DESC) WHERE ROWNUM <= 500 AND INDICE IS NOT NULL");
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
		query.append("SELECT * FROM (SELECT DISTINCT OA.id_oferta FROM OFERTAALOJAMIENTO OA LEFT JOIN SERVICIO S ON OA.id_oferta = S.oferta WHERE OA.retiro IS NULL ");
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
		query.append(" ) WHERE ROWNUM <= 500");
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
		Query q = pm.newQuery(SQL, "SELECT * FROM ( SELECT ofe.id_oferta, ofe.tipo FROM ofertaalojamiento ofe LEFT JOIN (SELECT ofe.id_oferta, COUNT(*) AS cantidad FROM ofertaalojamiento ofe INNER JOIN reserva r ON r.oferta = ofe.id_oferta WHERE r.inicio >= (SYSDATE - INTERVAL '1' MONTH) GROUP BY ofe.id_oferta) SQ ON SQ.id_oferta = ofe.id_oferta WHERE SQ.id_oferta IS NULL ) WHERE ROWNUM <= 500");
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
	
	public String analizarOperacion(PersistenceManager pm, String tipo) {
		Query q = pm.newQuery(SQL, "SELECT * FROM(SELECT TO_CHAR(R.inicio, 'DD-MM-YY') AS fecha, COUNT(*) AS Reservas, SUM(R.costo) AS Ingresos, SUM(OA.capacidad) AS Capacidad FROM RESERVA R  LEFT JOIN OFERTAALOJAMIENTO OA ON OA.id_oferta = R.oferta WHERE OA.tipo = ? GROUP BY TO_CHAR(R.inicio, 'DD-MM-YY') ORDER BY fecha DESC) WHERE ROWNUM <= 500");
		List<Object[]> results = (List<Object[]>) q.execute(tipo);
		StringBuilder rta = new StringBuilder();
		rta.append(" Información diaria del tipo de oferta de alojamiento " + tipo + ":\n");
		rta.append(" |Fecha|Reservas|Ingresos|Capacidad|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}

	public String consultarConsumo1(PersistenceManager pm, Long id_oferta, String date1, String date2) {
		Query q = pm.newQuery(SQL, "SELECT r.cliente, a.id_oferta, a.tipo, COUNT(*) AS total_reservas FROM reserva r inner JOIN ofertaalojamiento a ON r.oferta = a.id_oferta WHERE a.id_oferta = ? and r.inicio BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') GROUP BY r.cliente, a.id_oferta, a.tipo ORDER BY r.cliente");
		List<Object[]> results = (List<Object[]>) q.execute(id_oferta, date1, date2);
		StringBuilder rta = new StringBuilder();
		rta.append("|Cliente|Oferta|Tipo|Total Reservas|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}

	public String consultarConsumo2(PersistenceManager pm, Long id_oferta, String date1, String date2) {
		Query q = pm.newQuery(SQL, "SELECT c.cedula, c.nombre FROM CLIENTE c left join ( SELECT r.cliente FROM reserva r inner JOIN ofertaalojamiento a ON r.oferta = a.id_oferta WHERE a.id_oferta = ? and r.inicio BETWEEN TO_DATE(?, 'YYYY-MM-DD') AND TO_DATE(?, 'YYYY-MM-DD') GROUP BY r.cliente, a.id_oferta, a.tipo ORDER BY r.cliente ) SQ on c.cedula = SQ.cliente Where SQ.cliente is NULL and rownum <= 500");
		List<Object[]> results = (List<Object[]>) q.execute(id_oferta, date1, date2);
		StringBuilder rta = new StringBuilder();
		rta.append("|Cliente|Oferta|Tipo|Total Reservas|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String consultarFuncionamiento(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, "WITH ocupacion_semanal AS ( SELECT TO_CHAR(reserva.inicio, 'WW') AS semana, reserva.oferta, COUNT(*) AS cantidad_reservas FROM reserva GROUP BY TO_CHAR(reserva.inicio, 'WW'), reserva.oferta ), operadores_solicitados AS ( SELECT TO_CHAR(reserva.inicio, 'WW') AS semana, ofertaalojamiento.operador, COUNT(*) AS cantidad_reservas FROM reserva INNER JOIN ofertaalojamiento ON reserva.oferta = ofertaalojamiento.id_oferta GROUP BY TO_CHAR(reserva.inicio, 'WW'), ofertaalojamiento.operador )SELECT oc.semana, MAX(a.id_oferta) AS oferta_mas_ocupada, MIN(a.id_oferta) AS oferta_menos_ocupada, MAX(op.operador) AS operador_mas_solicitado, MIN(op.operador) AS operador_menos_solicitado FROM ocupacion_semanal oc INNER JOIN ofertaalojamiento a ON oc.oferta = a.id_oferta INNER JOIN operadores_solicitados op ON oc.semana = op.semana AND a.operador = op.operador GROUP BY oc.semana");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |Semana|Oferta más ocupada|Oferta menos ocupada|Operador más solicitado|Operador menos solicitado|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
}
