-- Indexes
CREATE INDEX FK_OfertaAlojamiento_Operador on OfertaAlojamiento(Operador);
CREATE INDEX FK_OfertaAlojamiento_Tipo on OfertaAlojamiento(Tipo);
CREATE INDEX FK_Reserva_Oferta on Reserva(Oferta);
CREATE INDEX FK_Reserva_Cliente on Reserva(Cliente);
CREATE INDEX FK_Reserva_Inicio on Reserva(Inicio);
CREATE INDEX FK_Servicio_Oferta on Servicio(Oferta);

DROP INDEX INDEX FK_OfertaAlojamiento_Operador;
DROP INDEX INDEX FK_OfertaAlojamiento_Tipo;
DROP INDEX INDEX FK_Reserva_Oferta;
DROP INDEX INDEX FK_Reserva_Cliente;
DROP INDEX INDEX FK_Reserva_Inicio;
DROP INDEX INDEX FK_Servicio_Oferta;