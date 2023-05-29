-- Drop
DROP TABLE alohandes cascade constraints;
DROP TABLE cliente cascade constraints;
DROP TABLE reserva cascade constraints;
DROP TABLE ofertaalojamiento cascade constraints;
DROP TABLE servicio cascade constraints;
DROP TABLE servicioadquirido cascade constraints;
DROP TABLE operador cascade constraints;
DROP TABLE ReservaColectiva cascade constraints;
DROP SEQUENCE index_sequence;

-- Create


CREATE SEQUENCE index_sequence;

CREATE TABLE alohandes (
    universidad VARCHAR(30) PRIMARY KEY
);

CREATE TABLE cliente (
    cedula INT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    correo VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR (20) NOT NULL, 
        CONSTRAINT contrasena_len CHECK (LENGTH(contrasena) >= 8),
    tipo VARCHAR(20) NOT NULL, 
        CONSTRAINT tipo_vecino CHECK (tipo != 'VECINO'),
    alohandes VARCHAR(30) NOT NULL, 
        CONSTRAINT fk_alohandes_cliente FOREIGN KEY (alohandes) REFERENCES alohandes(universidad)
);

CREATE TABLE reserva (
    id_reserva INT PRIMARY KEY,
    creacion DATE NOT NULL, 
    inicio DATE NOT NULL,
    periodos INT NOT NULL,
        CONSTRAINT periodos_min CHECK (periodos > 0),
    costo INT NOT NULL,
        CONSTRAINT costo_min CHECK (costo > 0),
    cliente INT NOT NULL,
        CONSTRAINT fk_cliente FOREIGN KEY (cliente) REFERENCES cliente(cedula),
    oferta INT NOT NULL
);

CREATE TABLE OfertaAlojamiento (
    id_oferta INT PRIMARY KEY,
    tipo VARCHAR(25) NOT NULL, 
    costo INT NOT NULL,
        CONSTRAINT costo_min1 CHECK (costo >0),
    capacidad INT NOT NULL,
        CONSTRAINT capacidad_min CHECK (capacidad > 0),
    periodicidad VARCHAR(25) NOT NULL,
    tiempo_minimo INT NOT NULL,
        CONSTRAINT tiempo_minimo_min CHECK (tiempo_minimo >= 0), 
    ubicacion VARCHAR(50) NOT NULL,
    tamano INT NOT NULL,
        CONSTRAINT  tamano_min CHECK (tamano > 0),
    retiro DATE, 
    operador INT NOT NULL
);


CREATE TABLE  servicio (
    id_servicio INT PRIMARY KEY,
    nombre VARCHAR(25) NOT NULL,
    descripcion VARCHAR(25) NOT NULL,
    costo INT NOT NULL,
        CONSTRAINT costo_min2 CHECK (costo >= 0),
    tipo VARCHAR(20) NOT NULL,
    oferta INT NOT NULL,
        CONSTRAINT fk_oferta FOREIGN KEY (oferta) REFERENCES OfertaAlojamiento(id_oferta));


CREATE TABLE ServicioAdquirido (
    servicio INT,
        CONSTRAINT fk_servicio FOREIGN KEY (servicio) REFERENCES servicio(id_servicio),
    reserva INT,
        CONSTRAINT fk_reserva FOREIGN KEY (reserva) REFERENCES reserva(id_reserva),
        CONSTRAINT pk_servicioadquirido PRIMARY KEY (servicio,reserva)
);

CREATE TABLE operador (
    id_convenio INT PRIMARY KEY,
    nombre VARCHAR(25) NOT NULL,
    correo VARCHAR(50) NOT NULL UNIQUE,
    contrasena VARCHAR (20) NOT NULL,
        CONSTRAINT contrasena_len1 CHECK (LENGTH(contrasena) >= 8),
    tipo_operador VARCHAR(25) NOT NULL,
    alohandes VARCHAR(30) NOT NULL, 
        CONSTRAINT fk_alohandes_operador FOREIGN KEY (alohandes) REFERENCES alohandes(universidad)
);

CREATE TABLE ReservaColectiva (
    id_reserva_colectiva INT,
    id_reserva INT,
        CONSTRAINT pk_ReservaColectiva PRIMARY KEY (id_reserva_colectiva,id_reserva)
);

ALTER TABLE reserva ADD CONSTRAINT fk_oferta1 FOREIGN KEY (oferta) REFERENCES OfertaAlojamiento (id_oferta);

ALTER TABLE OfertaAlojamiento ADD CONSTRAINT fk_operador FOREIGN KEY (operador) REFERENCES operador(id_convenio);


-- INIT values

INSERT INTO AlohAndes VALUES ('Universidad de los Andes');

