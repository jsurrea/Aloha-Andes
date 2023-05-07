package uniandes.isis2304.parranderos.negocio;

public class Servicio {
	private int id_servicio;
	private String nombre;
	private String descripcion;
	private int costo;
	private String tipo;
	private long oferta;
	
	public int getId_servicio() {
		return id_servicio;
	}
	public void setId_servicio(int id_servicio) {
		this.id_servicio = id_servicio;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getCosto() {
		return costo;
	}
	public void setCosto(int costo) {
		this.costo = costo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public long getOferta() {
		return oferta;
	}
	public void setOferta(long oferta) {
		this.oferta = oferta;
	}
	
	public Servicio(int id_servicio, String nombre, String descripcion, int costo, String tipo, long oferta) {
		this.setId_servicio(id_servicio);
		this.setNombre(nombre);
		this.setDescripcion(descripcion);
		this.setCosto(costo);
		this.setTipo(tipo);
		this.setOferta(oferta);
	}

	@Override
	public String toString() {
		return "AlohAndes ["+"id_servicio="+id_servicio+"nombre="+nombre+"descripcion="+descripcion+"costo="+costo+"tipo="+tipo+"oferta="+oferta+"]";
	}
}
