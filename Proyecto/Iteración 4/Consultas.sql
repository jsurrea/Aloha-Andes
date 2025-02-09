-- RFC1
SELECT OP.nombre, SUM(SQ.suma) AS INGRESOS
FROM OPERADOR OP
LEFT JOIN OFERTAALOJAMIENTO OA
ON OP.id_convenio = OA.operador
LEFT JOIN (
    SELECT R.oferta, SUM(R.costo) AS SUMA FROM RESERVA R
    WHERE R.inicio BETWEEN TRUNC(SYSDATE, 'YEAR') AND ADD_MONTHS(TRUNC(SYSDATE, 'YEAR'), 12)
    GROUP BY R.oferta
) SQ ON OA.id_oferta = SQ.oferta
GROUP BY OP.nombre;

-- RFC2
SELECT * FROM (
    SELECT OA.id_oferta, COUNT(R.id_reserva) AS RESERVAS FROM OFERTAALOJAMIENTO OA
    LEFT JOIN RESERVA R ON OA.id_oferta = R.oferta
    GROUP BY OA.id_oferta
    ORDER BY RESERVAS DESC
) WHERE ROWNUM <= 20;

-- RFC3
SELECT OA.id_oferta, NULLIF(SQ.reservas, 0) / OA.capacidad AS INDICE
FROM OFERTAALOJAMIENTO OA
LEFT JOIN (
    SELECT R.oferta, COUNT(R.id_reserva) AS RESERVAS
    FROM RESERVA R
    GROUP BY R.oferta
) SQ ON OA.id_oferta = SQ.oferta;

-- RFC4
SELECT DISTINCT id_oferta FROM OFERTAALOJAMIENTO OA
LEFT JOIN SERVICIO S
ON OA.id_oferta = S.oferta
WHERE retiro IS NULL
AND (
    S.TIPO = 'RECEPCION'
);

-- RFC5
SELECT CL.tipo, COUNT(R.id_reserva) AS Reservas, SUM(costo) AS Ingresos, SUM(R.periodos) AS Periodos
FROM CLIENTE CL
LEFT JOIN RESERVA R 
ON R.cliente = CL.cedula
GROUP BY CL.tipo;

-- RFC6
SELECT OP.nombre, OP.tipo_operador, OA.tipo, OA.ubicacion, OA.periodicidad, R.periodos, R.costo, R.inicio
FROM RESERVA R
LEFT JOIN OFERTAALOJAMIENTO OA
ON R.oferta = OA.id_oferta
LEFT JOIN OPERADOR OP
ON OA.operador = OP.id_convenio
WHERE R.cliente = 1001
ORDER BY R.creacion DESC;

-- RFC7
SELECT TO_CHAR(R.inicio, 'DD-MM-YY') AS fecha, COUNT(*) AS Reservas, SUM(R.costo) AS Ingresos, SUM(OA.capacidad) AS Capacidad
FROM RESERVA R 
LEFT JOIN OFERTAALOJAMIENTO OA
ON OA.id_oferta = R.oferta
WHERE OA.tipo = 'HABITACION_INDIVIDUAL'
GROUP BY TO_CHAR(R.inicio, 'DD-MM-YY');

-- RFC8
SELECT c.nombre, c.cedula, COUNT(*) as utilizado, SUM(r.periodos) as noches
FROM cliente c
INNER JOIN reserva r ON c.cedula = r.cliente
WHERE r.oferta = 1
GROUP BY c.nombre, c.cedula
HAVING COUNT(*) >= 3 OR SUM(r.periodos) >= 15;

-- RFC9
SELECT ofe.id_oferta, ofe.tipo
FROM ofertaalojamiento ofe
WHERE NOT EXISTS (
  SELECT 1
  FROM reserva r
  WHERE r.oferta = ofe.id_oferta
    AND r.inicio >= (SYSDATE - INTERVAL '1' MONTH));
    
-- RFC12

WITH ocupacion_semanal AS (
    SELECT
        TO_CHAR(reserva.inicio, 'WW') AS semana,
        reserva.oferta,
        COUNT(*) AS cantidad_reservas
    FROM
        reserva
    GROUP BY
        TO_CHAR(reserva.inicio, 'WW'),
        reserva.oferta
),
operadores_solicitados AS (
    SELECT
        TO_CHAR(reserva.inicio, 'WW') AS semana,
        ofertaalojamiento.operador,
        COUNT(*) AS cantidad_reservas
    FROM
        reserva
        INNER JOIN ofertaalojamiento ON reserva.oferta = ofertaalojamiento.id_oferta
    GROUP BY
        TO_CHAR(reserva.inicio, 'WW'),
        ofertaalojamiento.operador
)
SELECT
    oc.semana,
    MAX(a.id_oferta) AS oferta_mas_ocupada,
    MIN(a.id_oferta) AS oferta_menos_ocupada,
    MAX(op.operador) AS operador_mas_solicitado,
    MIN(op.operador) AS operador_menos_solicitado
FROM
    ocupacion_semanal oc
    INNER JOIN ofertaalojamiento a ON oc.oferta = a.id_oferta
    INNER JOIN operadores_solicitados op ON oc.semana = op.semana AND a.operador = op.operador
GROUP BY
    oc.semana;
    
-- RFC13

SELECT
    c.cedula,
    c.nombre,
    c.correo,
    (
        SELECT COUNT(*)
        FROM reserva r
        WHERE r.cliente = c.cedula
        AND r.inicio >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)
    ) AS reservas_mensuales,
    (
        SELECT COUNT(*)
        FROM reserva r
        INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta
        WHERE r.cliente = c.cedula
        AND a.costo > 150
    ) AS reservas_costosas,
    (
        SELECT COUNT(*)
        FROM reserva r
        INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta
        WHERE r.cliente = c.cedula
        AND a.tipo = 'SUITE'
    ) AS reservas_suites
FROM
    cliente c
WHERE
    (
        EXISTS (
            SELECT 1
            FROM reserva r
            WHERE r.cliente = c.cedula
            AND r.inicio >= ADD_MONTHS(TRUNC(SYSDATE, 'MM'), -1)
        )
        OR EXISTS (
            SELECT 1
            FROM reserva r
            INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta
            WHERE r.cliente = c.cedula
            AND a.costo > 150
        )
        OR EXISTS (
            SELECT 1
            FROM reserva r
            INNER JOIN ofertaalojamiento a ON r.oferta = a.id_oferta
            WHERE r.cliente = c.cedula
            AND a.tipo = 'SUITE'
        )
    );