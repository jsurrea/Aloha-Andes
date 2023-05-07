package uniandes.isis2304.alohandes.negocio;

public class Cliente {
	private long cedula;
	private String nombre;
	private String correo;
	private String contrasena;
	private String tipo;
	private String alohandes;

	public Cliente(long cedula, String nombre, String correo, String contrasena, String tipo, String alohandes) {
		this.setCedula(cedula);
		this.setNombre(nombre);
		this.setCorreo(correo);
		this.setContrasena(contrasena);
		this.setTipo(tipo);
		this.setAlohandes(alohandes);
	}
	
	public long getCedula() {
		return cedula;
	}
	public void setCedula(long cedula) {
		this.cedula = cedula;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getContrasena() {
		return contrasena;
	}
	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}
	public String getCorreo() {
		return correo;
	}
	public void setCorreo(String correo) {
		this.correo = correo;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getAlohandes() {
		return alohandes;
	}
	public void setAlohandes(String alohandes) {
		this.alohandes = alohandes;
	}
	@Override
	public String toString() {
		return "AlohAndes ["+"cedula="+cedula+"nombre="+nombre+"correo="+correo+"contrasena="+contrasena+"tipo="+tipo+"alohandes="+alohandes+"]";
	}
}
