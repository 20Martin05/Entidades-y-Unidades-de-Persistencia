-- ==========================================
-- TABLA: PRODUCTOS
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS productos;

-- Define la estructura utilizada por la entidad JPA Productos.
CREATE TABLE productos (
    -- Identificador autoincremental y clave primaria.
    id SERIAL PRIMARY KEY,
    -- Datos básicos del producto.
    nombre VARCHAR,
    precio NUMERIC(10,2),
    cantidad INT,
    categoria VARCHAR,
    categoria_id INT,
    proveedor_id INT,
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id),
    CONSTRAINT fk_producto_proveedor
        FOREIGN KEY (proveedor_id)
        REFERENCES proveedores(id)
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM productos;
