package uniandes.isis2304.alohandes.negocio;

import java.sql.Timestamp;

public class OfertaAlojamiento {
	private long id_oferta;
	private String tipo;
	private int costo;
	private int capacidad;
	private String periodicidad;
	private int tiempo_minimo;
	private String ubicacion;
	private int tamano;
	private Timestamp retiro;
	private long operador;
	
	public OfertaAlojamiento(long id_oferta, String tipo, int costo, int capacidad, String periodicidad, int tiempo_minimo, String ubicacion, int tamano, Timestamp retiro, long operador) {
		this.setId_oferta(id_oferta);
	    this.setTipo(tipo);
	    this.setCosto(costo);
	    this.setCapacidad(capacidad);
	    this.setPeriodicidad(periodicidad);
	    this.setTiempo_minimo(tiempo_minimo);
	    this.setUbicacion(ubicacion);
	    this.setTamano(tamano);
	    this.setRetiro(retiro);
	    this.setOperador(operador);
	}
	
	public long getId_oferta() {
		return id_oferta;
	}
	public void setId_oferta(long id_oferta) {
		this.id_oferta = id_oferta;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public int getCosto() {
		return costo;
	}
	public void setCosto(int costo) {
		this.costo = costo;
	}
	public int getCapacidad() {
		return capacidad;
	}
	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}
	public String getPeriodicidad() {
		return periodicidad;
	}
	public void setPeriodicidad(String periodicidad) {
		this.periodicidad = periodicidad;
	}
	public int getTiempo_minimo() {
		return tiempo_minimo;
	}
	public void setTiempo_minimo(int tiempo_minimo) {
		this.tiempo_minimo = tiempo_minimo;
	}
	public String getUbicacion() {
		return ubicacion;
	}
	public void setUbicacion(String ubicacion) {
		this.ubicacion = ubicacion;
	}
	public int getTamano() {
		return tamano;
	}
	public void setTamano(int tamano) {
		this.tamano = tamano;
	}
	public Timestamp getRetiro() {
		return retiro;
	}
	public void setRetiro(Timestamp retiro) {
		this.retiro = retiro;
	}
	public long getOperador() {
		return operador;
	}
	public void setOperador(long operador) {
		this.operador = operador;
	}
	@Override
	public String toString() {
		return "AlohAndes ["+"id_oferta="+id_oferta+"tipo="+tipo+"costo="+costo+"capacidad="+capacidad+"periodicidad="+periodicidad+"tiempo_minimo="+tiempo_minimo+"ubicacion="+ubicacion+"tamano="+tamano+"retiro="+retiro+"operador="+operador+"]";
	}
}
