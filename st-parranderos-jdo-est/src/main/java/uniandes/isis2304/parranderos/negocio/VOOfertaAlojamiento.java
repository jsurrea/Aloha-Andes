package uniandes.isis2304.parranderos.negocio;

import java.sql.Timestamp;

public interface VOOfertaAlojamiento {
    public long getId_oferta();
    public String getTipo();
    public int getCosto();
    public int getCapacidad();
    public String getPeriodicidad();
    public int getTiempo_minimo();
    public String getUbicacion();
    public int getTamano();
    public Timestamp getRetiro();
    public long getOperador();
}
