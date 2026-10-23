-- ==========================================
-- TABLA INTERMEDIA: FACTURA_PRODUCTOS
-- ==========================================
-- Elimina la tabla intermedia anterior para permitir recrear la relación.
DROP TABLE IF EXISTS factura_productos;

-- Define la relación muchos a muchos entre facturas y productos.
CREATE TABLE factura_productos (
    -- Identificador de la factura relacionada.
    factura_id INT NOT NULL,
    -- Identificador del producto relacionado.
    producto_id INT NOT NULL,

    -- Evita repetir el mismo producto dentro de una factura.
    PRIMARY KEY (factura_id, producto_id),

    -- Relaciona la tabla intermedia con facturas.
    CONSTRAINT fk_factura_productos_factura
        FOREIGN KEY (factura_id)
        REFERENCES facturas(id),

    -- Relaciona la tabla intermedia con productos.
    CONSTRAINT fk_factura_productos_producto
        FOREIGN KEY (producto_id)
        REFERENCES productos(id)
);

-- Consulta de comprobación del contenido de la tabla intermedia.
SELECT * FROM factura_productos;
