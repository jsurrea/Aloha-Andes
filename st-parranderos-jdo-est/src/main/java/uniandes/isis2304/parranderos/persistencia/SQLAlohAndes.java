package uniandes.isis2304.parranderos.persistencia;

public class SQLAlohAndes {
	/* Esta clase se usará en el futuro cuando sea necesario extender el negocio a más universidades */
	private final static String SQL = PersistenciaParranderos.SQL;
	private PersistenciaParranderos pp;
	public SQLAlohAndes (PersistenciaParranderos pp) {
		this.pp = pp;
	}
}
