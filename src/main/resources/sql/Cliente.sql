-- ==========================================
-- TABLA: CLIENTES
-- ==========================================
-- Elimina la tabla anterior para permitir recrear el esquema desde cero.
DROP TABLE IF EXISTS clientes CASCADE ;

-- Define la estructura utilizada por la entidad JPA Cliente.
CREATE TABLE clientes (
    -- Identificador autoincremental y clave primaria.
    id SERIAL PRIMARY KEY,
    -- Nombres del cliente.
    nombre VARCHAR(100) NOT NULL,
    -- Apellidos del cliente.
    apellido VARCHAR(100) NOT NULL,
    -- Número de identificación único del cliente.
    cedula VARCHAR(20) UNIQUE NOT NULL,
    -- Número telefónico del cliente.
    telefono VARCHAR(20),
    -- Correo electrónico del cliente.
    correo VARCHAR(100)
);

-- Consulta de comprobación del contenido de la tabla.
SELECT * FROM clientes;
