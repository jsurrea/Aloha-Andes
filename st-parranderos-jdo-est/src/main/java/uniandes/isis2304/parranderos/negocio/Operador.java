package uniandes.isis2304.parranderos.negocio;

public class Operador {
    private long id_convenio;
    private String nombre;
    private String correo;
    private String contrasena;
    private String tipo_operador;
    private String alohandes;
    
    public Operador(long id_convenio, String nombre, String correo, String contrasena, String tipo_operador, String alohandes) {
    	this.setId_convenio(id_convenio);
        this.setNombre(nombre);
        this.setCorreo(correo);
        this.setContrasena(contrasena);
        this.setTipo_operador(tipo_operador);
        this.setAlohandes(alohandes);
    }

	public long getId_convenio() {
		return id_convenio;
	}

	public void setId_convenio(long id_convenio) {
		this.id_convenio = id_convenio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getTipo_operador() {
		return tipo_operador;
	}

	public void setTipo_operador(String tipo_operador) {
		this.tipo_operador = tipo_operador;
	}

	public String getContrasena() {
		return contrasena;
	}

	public void setContrasena(String contrasena) {
		this.contrasena = contrasena;
	}

	public String getAlohandes() {
		return alohandes;
	}

	public void setAlohandes(String alohandes) {
		this.alohandes = alohandes;
	}
	@Override
	public String toString() {
		return "AlohAndes ["+"id_convenio="+id_convenio+"nombre="+nombre+"correo="+correo+"contrasena="+contrasena+"tipo_operador="+tipo_operador+"alohandes="+alohandes+"]";
	}
}
