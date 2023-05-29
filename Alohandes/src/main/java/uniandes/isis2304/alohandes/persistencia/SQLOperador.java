package uniandes.isis2304.alohandes.persistencia;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import java.util.List;

public class SQLOperador {
	private final static String SQL = PersistenciaAlohandes.SQL;
	private PersistenciaAlohandes pp;
	public SQLOperador (PersistenciaAlohandes pp) {
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
	
	public String ingresosRecibidos(PersistenceManager pm) {
		Query q = pm.newQuery(SQL, "SELECT OP.nombre, SUM(SQ.suma) AS INGRESOS FROM OPERADOR OP LEFT JOIN OFERTAALOJAMIENTO OA ON OP.id_convenio = OA.operador LEFT JOIN ( SELECT R.oferta, SUM(R.costo) AS SUMA FROM RESERVA R WHERE R.inicio BETWEEN TRUNC(SYSDATE, 'YEAR') AND ADD_MONTHS(TRUNC(SYSDATE, 'YEAR'), 12) GROUP BY R.oferta ) SQ ON OA.id_oferta = SQ.oferta GROUP BY OP.nombre HAVING SUM(SQ.suma) IS NOT NULL");
		List<Object[]> results = (List<Object[]>) q.execute();
		StringBuilder rta = new StringBuilder();
		rta.append(" |Operador|Ingresos|\n");
		for (Object[] row : results) {
		    for (Object col : row) {
		        rta.append(" | " + col);
		    }
		    rta.append(" |\n");
		}
		return rta.toString();
	}
}
