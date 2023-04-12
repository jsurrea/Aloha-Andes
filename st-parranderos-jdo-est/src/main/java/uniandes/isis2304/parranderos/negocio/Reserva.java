package uniandes.isis2304.parranderos.negocio;

import java.sql.Timestamp;

public class Reserva {
	private long id_reserva;
	private Timestamp creacion;
	private Timestamp inicio;
	private int periodos;
	private int costo;
	private long cliente;
	private long oferta;
	
	public Reserva(long id_reserva, Timestamp creacion, Timestamp inicio, int periodos, int costo, long cliente, long oferta) {
		this.setId_reserva(id_reserva);
		this.setCreacion(creacion);
		this.setInicio(inicio);
		this.setPeriodos(periodos);
		this.setCosto(costo);
		this.setCliente(cliente);
		this.setOferta(oferta);
	}

	public long getId_reserva() {
		return id_reserva;
	}

	public void setId_reserva(long id_reserva) {
		this.id_reserva = id_reserva;
	}

	public Timestamp getCreacion() {
		return creacion;
	}

	public void setCreacion(Timestamp creacion) {
		this.creacion = creacion;
	}

	public Timestamp getInicio() {
		return inicio;
	}

	public void setInicio(Timestamp inicio) {
		this.inicio = inicio;
	}

	public int getPeriodos() {
		return periodos;
	}

	public void setPeriodos(int periodos) {
		this.periodos = periodos;
	}

	public int getCosto() {
		return costo;
	}

	public void setCosto(int costo) {
		this.costo = costo;
	}
	@Override
	public String toString() {
		return "AlohAndes ["+"id_reserva="+id_reserva+"creacion="+creacion+"inicio="+inicio+"periodos="+periodos+"costo="+costo+"]";
	}

	public long getCliente() {
		return cliente;
	}

	public void setCliente(long cliente) {
		this.cliente = cliente;
	}

	public long getOferta() {
		return oferta;
	}

	public void setOferta(long oferta) {
		this.oferta = oferta;
	}
}
