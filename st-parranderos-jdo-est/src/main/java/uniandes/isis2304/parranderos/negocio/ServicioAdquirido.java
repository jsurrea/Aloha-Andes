package uniandes.isis2304.parranderos.negocio;

public class ServicioAdquirido {
	private long servicio;
	private long reserva;
	public ServicioAdquirido(long servicio, long reserva) {
		this.setReserva(reserva);
		this.setServicio(servicio);
	}
	
	public long getServicio() {
		return servicio;
	}
	public void setServicio(long servicio) {
		this.servicio = servicio;
	}
	public long getReserva() {
		return reserva;
	}
	public void setReserva(long reserva) {
		this.reserva = reserva;
	}
	@Override
	public String toString() {
		return "AlohAndes ["+"servicio="+servicio+"reserva="+reserva+"]";
	}
}
