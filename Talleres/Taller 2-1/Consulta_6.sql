ALTER SESSION SET CURRENT_SCHEMA = PARRANDEROS;

WITH BEB_BUSCADOS AS 
(
    SELECT 
        BE.id
         
    FROM 
        BEBEDORES BE
        INNER JOIN 
        GUSTAN GT ON GT.id_bebedor = BE.id
        INNER JOIN 
        BEBIDAS BB ON BB.ID = GT.id_bebida
    
    WHERE 
        BE.presupuesto = 'Bajo' AND
        BB.grado_alcohol BETWEEN 5 AND 15
    
    GROUP BY
        BE.id
        
    HAVING
        COUNT(DISTINCT BB.id) > 2
)

SELECT
    BA.ciudad,
    BA.nombre,
    COUNT(BE.id) AS bebedores
    
FROM
    BARES BA
    INNER JOIN 
    FRECUENTAN FR ON BA.id = FR.id_bar
    INNER JOIN 
    BEB_BUSCADOS BE ON BE.id = FR.id_bebedor
    
GROUP BY
    BA.ciudad,
    BA.nombre
    
ORDER BY
    BA.ciudad ASC,
    BA.nombre ASC,
    bebedores DESC
;