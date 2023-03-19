ALTER SESSION SET CURRENT_SCHEMA = PARRANDEROS;

WITH BARES_POR_CIUDAD AS (

    SELECT 
        BA.ciudad,
        SUM(SQ.bares) AS bares
        
    FROM 
    (
        SELECT
            BA.id, COUNT(BB.id) AS bares
        
        FROM 
            SIRVEN SR 
            INNER JOIN 
            BEBIDAS BB ON SR.id_bebida = BB.id
            INNER JOIN 
            BARES BA ON SR.id_bar = BA.id
            
        WHERE
            BB.grado_alcohol = 25 AND
            BA.presupuesto = 'Medio'
        
        GROUP BY
            BA.id
        
        HAVING 
            COUNT(BB.id) >= 4
    ) SQ INNER JOIN
        BARES BA ON SQ.id = BA.id
        
    GROUP BY
        BA.ciudad
)

SELECT 
    ciudad, bares
    
FROM 
    BARES_POR_CIUDAD
    
WHERE 
    bares = (SELECT MAX(bares) FROM BARES_POR_CIUDAD)
    
FETCH FIRST 1 ROWS ONLY
;