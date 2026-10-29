-- ==========================================
-- TABLA: CATEGORÍAS
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS categorias CASCADE;

-- Define la estructura utilizada por la entidad JPA Categoria.
CREATE TABLE categorias (
    -- Identificador autoincremental y clave primaria.
    id SERIAL PRIMARY KEY,
    -- Nombre de la categoría.
    nombre VARCHAR(100) NOT NULL,
    -- Descripción de la categoría.
    descripcion VARCHAR(255),
    -- Indica si la categoría se encuentra activa.
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM categorias;