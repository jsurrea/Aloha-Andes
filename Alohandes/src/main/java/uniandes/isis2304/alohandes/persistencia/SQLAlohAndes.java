package uniandes.isis2304.alohandes.persistencia;

public class SQLAlohAndes {
	/* Esta clase se usará en el futuro cuando sea necesario extender el negocio a más universidades */
	private final static String SQL = PersistenciaAlohandes.SQL;
	private PersistenciaAlohandes pp;
	public SQLAlohAndes (PersistenciaAlohandes pp) {
		this.pp = pp;
	}
}
