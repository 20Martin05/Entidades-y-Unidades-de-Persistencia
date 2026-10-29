# Entidades y Unidades de Persistencia: Gestión de Productos

Proyecto de Persistencia de Datos (U2) que implementa un sistema CRUD para gestionar categorías, productos, clientes y facturas de una tienda, con Java, Maven, interfaz gráfica Swing y persistencia relacional en **PostgreSQL** mediante **Jakarta Persistence (JPA)** y el framework **Hibernate ORM**.

El desarrollo aplica el Mapeo Objeto-Relacional (ORM) con anotaciones estándar de JPA y gestiona sesiones y transacciones mediante `EntityManager`, para garantizar la integridad de los datos en cada operación de lectura y escritura.

## ⚙️ Características Técnicas

* **CRUD completo** para categorías, productos, clientes y facturas (`persist`, `createQuery`, `find`, `remove`).
* **Mapeo Objeto-Relacional (ORM):** anotaciones JPA (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany`, `@JoinColumn`).
* **Líneas de factura:** la entidad `FacturaDetalle` guarda producto, cantidad y precio unitario por línea.
* **Control de stock:** al guardar una factura se descuenta el stock; al actualizar se ajusta solo la diferencia; al eliminar, el stock vuelve a su valor anterior. Todo ocurre en una sola transacción.
* **Pool de conexiones (HikariCP)** hacia PostgreSQL.
* **Transacciones controladas** con `EntityManager` (`begin`, `commit`, `rollback`).
* **Normalización de datos:** nombres de clientes y productos en formato título; correos de clientes en minúsculas.
* **Recarga automática:** combos y tablas se actualizan tras cada operación.

## 🛠️ Requisitos Previos

* **Java Development Kit (JDK)** 21 o superior.
* **Apache Maven**.
* **PostgreSQL** activo en `localhost:5432`.
* **IntelliJ IDEA** (recomendado, por el diseñador de formularios Swing).

## 📦 Dependencias principales

| Librería | Versión | Uso |
|---|---|---|
| Jakarta Persistence API | 3.2.0 | Anotaciones y `EntityManager` |
| Hibernate ORM | 7.4.13.Final | Implementación de JPA |
| PostgreSQL JDBC | 42.7.14 | Conexión a la base de datos |
| HikariCP | 7.1.0 | Pool de conexiones |
| Lombok | 1.18.48 | Getters y setters |
| forms_rt | 7.0.3 | Runtime de formularios Swing |

## 📂 Estructura del Proyecto

```text
src/main/
 ├── java/U1/
 │   ├── Main.java                       # Punto de entrada
 │   ├── controlador/
 │   │   ├── CategoriaControlador.java
 │   │   ├── ClientesControlador.java
 │   │   ├── FacturaControlador.java
 │   │   └── ProductosControlador.java
 │   ├── modelo/
 │   │   ├── Categoria.java
 │   │   ├── Cliente.java
 │   │   ├── Factura.java
 │   │   ├── FacturaDetalle.java
 │   │   └── Productos.java
 │   ├── util/
 │   │   └── ConexionJPA.java            # Fábrica de EntityManager
 │   └── vista/
 │       ├── base/
 │       │   ├── VentanaEmpresaUI.java   # Componentes generados por IntelliJ
 │       │   └── VentanaEmpresa.form     # Diseño visual (IntelliJ GUI Designer)
 │       └── VentanaEmpresa.java         # Eventos y lógica de la ventana
 └── resources/
     ├── META-INF/
     │   └── persistence.xml             # Unidad de persistencia "postgres"
     ├── sql/                            # Scripts SQL de cada tabla
     │   ├── Categoria.sql
     │   ├── Cliente.sql
     │   ├── Factura.sql
     │   ├── FacturaDetalle.sql
     │   └── Productos.sql
     ├── images/
     │   ├── pestañas/                   # Capturas de cada pestaña
     │   └── Diagrama de Clases.png
     └── uml/                            # Diagrama UML (Visual Paradigm, .vpp)
```

## 📊 Modelo de Datos

| Tabla             | Descripción                                             |
|-------------------|---------------------------------------------------------|
| `categorias`      | Categorías de productos                                 |
| `productos`       | Productos con precio, stock y categoría                 |
| `clientes`        | Clientes con cédula única                               |
| `facturas`        | Cabecera de factura (número único, fecha, totales)      |
| `factura_detalle` | Líneas de factura: producto, cantidad y precio unitario |

Relaciones principales:

* Una categoría tiene muchos productos (`Productos.categoriaRelacion`).
* Un cliente tiene muchas facturas (`Factura.cliente`).
* Una factura tiene muchas líneas; cada línea apunta a un producto (`FacturaDetalle.producto`).
* Un producto no puede borrarse si aparece en una factura (`ON DELETE RESTRICT`).

## 🖼️ Evidencia de ejecución

**Pestaña Clientes**

![Pestaña Clientes](src/main/resources/images/pestañas/Pesta%C3%B1a%20Clientes.png)

**Pestaña Categorías**

![Pestaña Categorías](src/main/resources/images/pestañas/Pesta%C3%B1a%20Categor%C3%ADas%20.png)

**Pestaña Productos**

![Pestaña Productos](src/main/resources/images/pestañas/Pesta%C3%B1a%20Productos.png)

**Pestaña Factura**

![Pestaña Factura](src/main/resources/images/pestañas/Pesta%C3%B1a%20Factura.png)

## 🚀 Instrucciones de Uso

### 1. Clonar el proyecto

```bash
git clone https://github.com/20Martin05/Entidades-y-Unidades-de-Persistencia.git
cd Entidades-y-Unidades-de-Persistencia
```

### 2. Preparar PostgreSQL

1. Inicia PostgreSQL en el puerto `5432`.
2. Ejecuta los scripts de `src/main/resources/sql/` en este orden: `Categoria.sql`, `Cliente.sql`, `Productos.sql`, `Factura.sql`, `FacturaDetalle.sql`.
3. Revisa la URL y el usuario en `src/main/resources/META-INF/persistence.xml`. La contraseña debe estar disponible para la aplicación.

### 3. Configurar y ejecutar el programa

1. Abre la carpeta clonada en IntelliJ IDEA, Eclipse, NetBeans o VS Code como proyecto Maven.
2. Dirígete a `src/main/resources/META-INF/persistence.xml` y reemplaza `PON_AQUI_TU_CONTRASEÑA` por la contraseña local de tu usuario `postgres`.
3. Recarga las dependencias Maven.
4. Ejecuta la clase principal `U1.Main`.

```bash
mvn clean package
```

### Uso de la interfaz

* **Categoría:** elige el nombre y el estado, escribe la descripción y pulsa **Guardar**. Selecciona una fila para editarla o eliminarla.
* **Productos:** asigna una categoría y escribe nombre, precio y cantidad. Los nombres se capitalizan al guardar.
* **Factura:** elige cliente y producto, escribe número, fecha (`AAAA-MM-DD`), cantidad, subtotal e impuesto. El total se calcula como subtotal + impuesto. Al guardar, el stock del producto baja por la cantidad.
* **Cliente:** completa los datos; los nombres se capitalizan y el correo se guarda en minúsculas.
* **Buscadores:** filtran productos, facturas (por cliente) y clientes por nombre.

## 👤 Autor

* **Desarrollado por:** Martin Rody Rosero Játiva
* **Materia:** Persistencia de Datos
* **Universidad:** Universidad Politécnica Salesiana
* **Bloque:** Bloque I - Unidad 2 (Entidades y Unidades de Persistencia)
   ```
