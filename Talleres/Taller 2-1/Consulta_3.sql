ALTER SESSION SET CURRENT_SCHEMA = PARRANDEROS;

SELECT 
    BE.id, 
    BE.nombre, 
    BE.presupuesto,
    COUNT(*) AS apariciones
    
FROM
(   -- Todas las apariciones en la base de datos
    SELECT id FROM BEBEDORES
    UNION ALL
    SELECT id_bebedor FROM FRECUENTAN
    UNION ALL
    SELECT id_bebedor FROM GUSTAN
)   APR INNER JOIN 
    BEBEDORES BE ON APR.id = BE.id

GROUP BY
    BE.id, 
    BE.nombre, 
    BE.presupuesto
    
ORDER BY
    apariciones DESC,
    BE.nombre ASC
    
FETCH FIRST 10 ROWS ONLY;