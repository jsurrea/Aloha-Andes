package uniandes.isis2304.alohandes.persistencia;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

public class SQLCliente {
	private final static String SQL = PersistenciaAlohandes.SQL;
	private PersistenciaAlohandes pp;
	public SQLCliente (PersistenciaAlohandes pp) {
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
	
	public String usoTipoUsuario(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, "SELECT CL.tipo, COUNT(R.id_reserva) AS Reservas, SUM(costo) AS Ingresos, SUM(R.periodos) AS Periodos FROM CLIENTE CL LEFT JOIN RESERVA R ON R.cliente = CL.cedula GROUP BY CL.tipo");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |Tipo|# Reservas|Total Ingresos|Total Periodos|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String usoUsuario(PersistenceManager pm, long cedula) {
		Query q = pm.newQuery(SQL, "SELECT OP.nombre, OP.tipo_operador, OA.tipo, OA.ubicacion, OA.periodicidad, R.periodos, R.costo, R.inicio FROM RESERVA R LEFT JOIN OFERTAALOJAMIENTO OA ON R.oferta = OA.id_oferta LEFT JOIN OPERADOR OP ON OA.operador = OP.id_convenio WHERE R.cliente = ? ORDER BY R.creacion DESC");
		List<Object[]> results = (List<Object[]>) q.execute(cedula);
		StringBuilder rta = new StringBuilder();
		rta.append(" Información de las últimas reservas realizadas por el cliente con cédula " + cedula + ":\n");
		rta.append(" |Operador|Tipo Operador|Tipo Oferta|Ubicación|Periodicidad|#Periodos|Costo|Fecha Inicio|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
	
	public String consultarBuenosClientes(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, " SELECT * FROM (SELECT c.cedula, c.nombre, c.correo, ( SELECT COUNT(*) FROM reserva r WHERE r.cliente = c.cedula AND r.inicio >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) ) AS reservas_mensuales, ( SELECT COUNT(*) FROM reserva r INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta WHERE r.cliente = c.cedula AND a.costo > 150 ) AS reservas_costosas, ( SELECT COUNT(*) FROM reserva r INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta WHERE r.cliente = c.cedula AND a.tipo = 'SUITE' ) AS reservas_suites FROM cliente c WHERE ( EXISTS ( SELECT 1 FROM reserva r WHERE r.cliente = c.cedula AND r.inicio >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1) ) OR EXISTS ( SELECT 1 FROM reserva r INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta WHERE r.cliente = c.cedula AND a.costo > 150 ) OR EXISTS ( SELECT 1 FROM reserva r INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta WHERE r.cliente = c.cedula AND a.tipo = 'SUITE' ) ) ) WHERE ROWNUM <= 500");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |Cedula|Nombre|Correo|Reservas mensuale|Reservas costosas|Reservas suites|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
}
