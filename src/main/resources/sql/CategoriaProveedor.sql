-- Elimina la tabla intermedia anterior si existe para permitir su recreación.
DROP TABLE IF EXISTS categoria_proveedor;

-- Define la relación muchos a muchos entre categorías y proveedores.
CREATE TABLE categoria_proveedor (
    -- Identificador de la categoría relacionada.
        categoria_id INT NOT NULL,
    -- Identificador del proveedor relacionado.
        proveedor_id INT NOT NULL,

    -- Clave primaria compuesta para evitar registros duplicados.
        PRIMARY KEY (categoria_id, proveedor_id),

    -- Relaciona la tabla intermedia con la tabla de categorías.
        CONSTRAINT fk_categoria_proveedor_categoria
            FOREIGN KEY (categoria_id)
                REFERENCES categorias(id),

    -- Relaciona la tabla intermedia con la tabla de proveedores.
        CONSTRAINT fk_categoria_proveedor_proveedor
            FOREIGN KEY (proveedor_id)
                REFERENCES proveedores(id)
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM categoria_proveedor;