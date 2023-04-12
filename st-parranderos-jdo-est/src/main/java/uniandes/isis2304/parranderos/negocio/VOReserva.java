package uniandes.isis2304.parranderos.negocio;

import java.sql.Timestamp;

public interface VOReserva {
    public long getId_reserva();
    public Timestamp getCreacion();
    public Timestamp getInicio();
    public int getPeriodos();
    public int getCosto();
}
