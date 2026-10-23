-- ==========================================
-- TABLA: PROVEEDORES
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS proveedores;

-- Define la estructura utilizada por la entidad JPA Proveedor.
CREATE TABLE proveedores (
    -- Identificador autoincremental y clave primaria.
    id SERIAL PRIMARY KEY,
    -- Nombre del contacto del proveedor.
    nombre VARCHAR(100) NOT NULL,
    -- Nombre de la empresa proveedora.
    empresa VARCHAR(100),
    -- Número telefónico del proveedor.
    telefono VARCHAR(20),
    -- Correo electrónico del proveedor.
    correo VARCHAR(100),
    -- Dirección del proveedor.
    direccion VARCHAR(255)
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM proveedores;
