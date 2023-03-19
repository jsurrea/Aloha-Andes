ALTER SESSION SET CURRENT_SCHEMA = PARRANDEROS;

SELECT
    COALESCE(BA.ciudad, BE.ciudad) AS ciudad,
    NVL(bares, 0) AS bares,
    NVL(bebedores, 0) AS bebedores
FROM
(
    SELECT ciudad, COUNT(id) AS bares
    FROM BARES
    WHERE presupuesto = 'Alto'
    GROUP BY ciudad
) BA FULL OUTER JOIN
(
    SELECT ciudad, COUNT(id) AS bebedores
    FROM BEBEDORES
    WHERE presupuesto = 'Alto'
    GROUP BY ciudad
) BE ON BA.ciudad = BE.ciudad
;