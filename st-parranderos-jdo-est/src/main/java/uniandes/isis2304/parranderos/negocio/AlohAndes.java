package uniandes.isis2304.parranderos.negocio;

public class AlohAndes {
	private String universidad;

	public AlohAndes(String universidad) {
		this.setUniversidad(universidad);
	}

	public String getUniversidad() {
		return universidad;
	}

	public void setUniversidad(String universidad) {
		this.universidad = universidad;
	}
	
	@Override
	public String toString() {
		return "AlohAndes [universidad="+ universidad + "]";
	}
}
