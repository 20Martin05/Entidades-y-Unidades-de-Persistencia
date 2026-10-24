# Entidades y Unidades de Persistencia

Aplicación de escritorio desarrollada en Java para gestionar productos, categorías y facturas usando JPA, Hibernate y PostgreSQL.

## Tecnologías

- Java 21
- Jakarta Persistence (JPA)
- Hibernate ORM
- PostgreSQL
- Swing
- Maven

## Funcionalidades

- Gestión de productos.
- Gestión de categorías.
- Gestión de facturas.
- Relaciones entre productos, categorías, proveedores y clientes.
- Consultas JPQL.

## Ejecución

1. Configurar PostgreSQL y la unidad de persistencia en `persistence.xml`.
2. Abrir el proyecto como proyecto Maven.
3. Ejecutar la clase `U1.Main`.

## Estructura principal

```text
src/main/java/U1/modelo        Entidades JPA
src/main/java/U1/controlador   Controladores CRUD
src/main/java/U1/vista         Interfaz Swing
src/main/resources/sql         Scripts SQL
```
