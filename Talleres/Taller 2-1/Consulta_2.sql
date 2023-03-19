ALTER SESSION SET CURRENT_SCHEMA = PARRANDEROS;

SELECT ID, NOMBRE, CIUDADBEBEDOR, BA.horario, MIN(PERSONAS) AS personas
FROM (
SELECT be.id, be.nombre, ba.ciudad, be.ciudad ciudadbebedor
    FROM frecuentan fe INNER JOIN bebedores be on FE.id_bebedor = be.id INNER JOIN bares ba on fe.id_bar = ba.id
    WHERE ba.ciudad != be.ciudad
    GROUP BY be.id, be.nombre, ba.ciudad, be.ciudad
    ORDER BY BE.id
) BE FULL OUTER JOIN 
(
SELECT p1.ciudad, p.horario, p1.personas
FROM (SELECT ciudad,  horario, SUM(personas) personas
FROM
(
    SELECT be.id, fe.horario, COUNT(fe.horario) personas, be.ciudad
    FROM frecuentan fe INNER JOIN bebedores be on FE.id_bebedor = be.id INNER JOIN bares ba on fe.id_bar = ba.id
    WHERE ba.ciudad = be.ciudad
    GROUP BY be.id, fe.horario, be.ciudad
    ORDER BY BE.id
)
    GROUP BY Ciudad, horario
    ORDER BY Ciudad
) p INNER JOIN 
(SELECT ciudad, MIN(DISTINCT personas) personas
FROM 
(SELECT ciudad,  horario, SUM(personas) personas
FROM
(
    SELECT be.id, fe.horario, COUNT(fe.horario) personas, be.ciudad
    FROM frecuentan fe INNER JOIN bebedores be on FE.id_bebedor = be.id INNER JOIN bares ba on fe.id_bar = ba.id
    WHERE ba.ciudad = be.ciudad
    GROUP BY be.id, fe.horario, be.ciudad
    ORDER BY BE.id
)
    GROUP BY Ciudad, horario
    ORDER BY Ciudad
)
GROUP BY ciudad)
p1 on p.ciudad = p1.ciudad
WHERE p.personas = p1.personas
GROUP BY p1.ciudad, p.horario, p1.personas
) BA ON BE.ciudad = BA.ciudad group by ID, NOMBRE, CIUDADBEBEDOR, BA.horario
GROUP BY ID, NOMBRE, CIUDADBEBEDOR, BA.horario
ORDER BY BE.ID
