SELECT 
    TA.NombreTabla, 
    TA.NombreColumna, 
    TA.TipoDeDato, 
    NVL(RE.NombreRestriccion, 'NOTIENE') AS NombreRestriccion, 
    TA.PermiteNulos
FROM
    (
    SELECT 
        TABLE_NAME AS NombreTabla, 
        COLUMN_NAME as NombreColumna, 
        DATA_TYPE AS TipoDeDato, 
        NULLABLE AS PermiteNulos
        
    FROM ALL_TAB_COLS
    
    WHERE owner = 'PARRANDEROS'
    
    GROUP BY 
        TABLE_NAME, 
        COLUMN_NAME, 
        DATA_TYPE, 
        NULLABLE 
        
    ORDER BY 
        TABLE_NAME, 
        COLUMN_NAME, 
        DATA_TYPE, 
        NULLABLE
    ) TA
    LEFT JOIN 
    (
    SELECT 
        CONSTRAINT_NAME AS NombreRestriccion, 
        TABLE_NAME AS NombreTabla, 
        COLUMN_NAME AS NombreColumna
        
    FROM ALL_CONS_COLUMNS
    
    WHERE owner = 'PARRANDEROS'
    ) RE 
    ON TA.NombreTabla = RE.NombreTabla 
    AND TA.NombreColumna = RE.NombreColumna
ORDER BY 
    TA.NombreTabla ASC, 
    TA.NombreColumna ASC, 
    NombreRestriccion ASC;
