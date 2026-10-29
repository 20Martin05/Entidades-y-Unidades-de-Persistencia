-- ==========================================
-- TABLA: PRODUCTOS
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS productos CASCADE;
-- Define la estructura utilizada por la entidad JPA Productos.
CREATE TABLE productos (
    -- Identificador autoincremental y clave primaria.
 id SERIAL PRIMARY KEY,

    -- Nombre comercial del producto.
    nombre  VARCHAR(100) NOT NULL,

    -- Precio unitario de venta.
    precio  NUMERIC(10,2) NOT NULL,

    -- Stock disponible del producto.
    cantidad    INT NOT NULL,

    -- Categoría a la que pertenece el producto.
    categoria_id    INT,

    -- Relaciona cada producto con una categoría.
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
            REFERENCES categorias(id)
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM productos;