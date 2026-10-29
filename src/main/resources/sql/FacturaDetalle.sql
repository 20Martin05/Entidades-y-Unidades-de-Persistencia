-- ==========================================
-- TABLA: FACTURA_DETALLE
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS factura_detalle;

-- Crea la tabla de líneas de factura: cada fila es un producto dentro de una factura.
CREATE TABLE factura_detalle (
    -- Identificador único de la línea.
    id               SERIAL PRIMARY KEY,

    -- Identificador de la factura a la que pertenece la línea.
    factura_id       INT NOT NULL,

    -- Identificador del producto vendido.
    producto_id      INT NOT NULL,

    -- Cantidad vendida del producto en esta factura. Debe ser mayor que cero.
    cantidad         INT NOT NULL CHECK (cantidad > 0),

    -- Precio del producto en el momento de la venta. Se guarda para que
    -- cambios futuros no alteren facturas antiguas.
    precio_unitario  NUMERIC(10,2) NOT NULL CHECK (precio_unitario >= 0),

    -- Relaciona la línea con la factura. Si se borra la factura, se borran sus líneas.
    CONSTRAINT fk_detalle_factura
        FOREIGN KEY (factura_id)
        REFERENCES facturas(id)
        ON DELETE CASCADE,

    -- Relaciona la línea con el producto. No permite borrar un producto que ya fue vendido.
    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id)
        REFERENCES productos(id)
        ON DELETE RESTRICT,

    -- Evita que un mismo producto aparezca dos veces en la misma factura.
    CONSTRAINT uq_factura_producto
        UNIQUE (factura_id, producto_id)
);

-- Índice para acelerar la búsqueda de líneas por factura.
CREATE INDEX idx_detalle_factura ON factura_detalle (factura_id);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM factura_detalle;
