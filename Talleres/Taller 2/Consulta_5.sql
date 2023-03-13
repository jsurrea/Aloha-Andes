ALTER SESSION SET CURRENT_SCHEMA = PARRANDEROS;

WITH NO_ALEGRES AS
(        
    SELECT 
        BE.ID
        
    FROM 
        BEBEDORES BE
        INNER JOIN 
        FRECUENTAN FR ON BE.id = FR.id_bebedor
        
    GROUP BY 
        BE.id
    
    HAVING
        (
            CASE
                WHEN MAX(fecha_ultima_visita) - MIN(fecha_ultima_visita) < 365 THEN COUNT(*) / 365
                ELSE COUNT(*) / (MAX(fecha_ultima_visita) - MIN(fecha_ultima_visita)) 
            END
        ) >= 10 / 365
        
    UNION
    
    SELECT 
        id_bebedor 
        
    FROM 
        GUSTAN GT INNER JOIN 
        BEBIDAS BB ON BB.ID = GT.ID_BEBIDA
        
    WHERE 
        BB.grado_alcohol > 20
)

SELECT
    BB.id,
    BB.nombre,
    BB.tipo

FROM
(
    SELECT 
        id_bebida
        
    FROM 
        GUSTAN GT 
        INNER JOIN
        BEBEDORES BE ON GT.id_bebedor = BE.id
        LEFT JOIN 
        NO_ALEGRES NA ON NA.id = BE.id
        
    WHERE
        NA.id IS NULL
        
    GROUP BY 
        id_bebida
        
    ORDER BY 
        COUNT(GT.id_bebedor) DESC
        
    FETCH FIRST 10 ROWS ONLY
    
) SQ INNER JOIN
    BEBIDAS BB ON SQ.id_bebida = BB.id
;