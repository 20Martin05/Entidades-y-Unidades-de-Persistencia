# Entidades y Unidades de Persistencia: Gestión de Productos

Proyecto de Persistencia de Datos (U2) que implementa un sistema CRUD para gestionar categorías, productos, clientes y facturas de una tienda, con Java, Maven, interfaz gráfica Swing y persistencia relacional en **PostgreSQL** mediante **Jakarta Persistence (JPA)** y el framework **Hibernate ORM**.

El desarrollo aplica el Mapeo Objeto-Relacional (ORM) con anotaciones estándar de JPA y gestiona sesiones y transacciones mediante `EntityManager`.

## ⚙️ Características Técnicas

* **CRUD** para categorías, productos, clientes y facturas (`persist`, `createQuery`, `find`, `remove`).
* **Mapeo Objeto-Relacional (ORM):** anotaciones JPA (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@OneToMany`, `@JoinColumn`).
* **Líneas de factura:** la entidad `FacturaDetalle` guarda producto, cantidad y precio unitario por línea.
* **Control de stock:** al guardar una factura se descuenta el stock del producto; al actualizar la factura se devuelve el stock de la línea anterior y se descuenta el de la nueva; al eliminar, el stock vuelve a su valor anterior. Cada operación ocurre en una sola transacción.
* **Pool de conexiones** configurado en `persistence.xml` (ver nota en la sección de configuración).
* **Transacciones controladas** con `EntityManager` (`begin`, `commit`, `rollback`).
* **Protección de datos al borrar:** una categoría no se elimina si tiene productos, y un cliente no se elimina si tiene facturas.
* **Normalización de datos:** nombres de clientes y de productos en formato título; correos de clientes en minúsculas.
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
| Lombok | 1.18.48 | Dependencia declarada en el proyecto |
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
     ├── images/
     │   ├── pestañas/                   # Capturas de cada pestaña
     │   └── Diagrama de Clases.png
     ├── META-INF/
     │   └── persistence.xml             # Unidad de persistencia "postgres"
     ├── sql/                            # Scripts SQL de cada tabla
     │   ├── Categoria.sql
     │   ├── Cliente.sql
     │   ├── Productos.sql
     │   ├── Factura.sql
     │   └── FacturaDetalle.sql
     └── uml/                            # Diagrama UML (Visual Paradigm, .vpp)
```

## 📊 Modelo de Datos

| Tabla             | Descripción                                             |
|-------------------|---------------------------------------------------------|
| `categorias`      | Categorías de productos                                 |
| `productos`       | Productos con precio, stock y categoría                 |
| `clientes`        | Clientes con cédula única                               |
| `facturas`        | Cabecera de factura (número único, fecha, subtotal, impuesto y total) |
| `factura_detalle` | Líneas de factura: producto, cantidad y precio unitario |

Relaciones principales:

* Una categoría tiene muchos productos (`Productos.categoriaRelacion`).
* Un cliente tiene muchas facturas (`Factura.cliente`).
* Una factura tiene muchas líneas; cada línea apunta a un producto (`FacturaDetalle.producto`).

Reglas de integridad:

* **Categoría:** no se puede eliminar mientras tenga productos. La aplicación lo verifica antes de borrar.
* **Cliente:** no se puede eliminar mientras tenga facturas. La aplicación lo verifica antes de borrar.
* **Producto:** no se puede eliminar si aparece en una factura (`ON DELETE RESTRICT` en la base de datos). En ese caso la aplicación muestra el error devuelto por PostgreSQL.

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
3. Si ya tenías la base creada antes de este cambio, ejecuta también:

   ```sql
   ALTER TABLE productos DROP CONSTRAINT fk_producto_categoria;
   ALTER TABLE productos ADD CONSTRAINT fk_producto_categoria
       FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE RESTRICT;
   ```

### 3. Configurar y ejecutar el programa

1. Abre la carpeta clonada en IntelliJ IDEA, Eclipse, NetBeans o VS Code como proyecto Maven.
2. Dirígete a `src/main/resources/META-INF/persistence.xml` y reemplaza `PON_AQUI_TU_CONTRASEÑA` por la contraseña local de tu usuario `postgres`.
3. Recarga las dependencias Maven.
4. Ejecuta la clase principal `U1.Main`.

Para compilar y empaquetar desde la terminal:

```bash
mvn clean package
```

### Uso de la interfaz

* **Categoría:** elige el nombre y el estado, escribe la descripción y pulsa **Guardar**. Selecciona una fila para editarla o eliminarla.
* **Productos:** asigna una categoría y escribe nombre, precio y cantidad. Los nombres se capitalizan al guardar. Todos los campos son obligatorios.
* **Factura:** elige cliente y producto, escribe número, fecha (`AAAA-MM-DD`), cantidad, subtotal, impuesto y total. El total se introduce manualmente en el formulario. Al guardar, el stock del producto baja por la cantidad.
* **Cliente:** completa nombre, apellido y cédula (obligatorios), y opcionalmente teléfono y correo. Los nombres se capitalizan y el correo se guarda en minúsculas.
* **Buscadores:** filtran productos, facturas (por cliente) y clientes por nombre.

## 👤 Autor

* **Desarrollado por:** Martin Rody Rosero Játiva
* **Materia:** Persistencia de Datos
* **Universidad:** Universidad Politécnica Salesiana
* **Bloque:** Bloque I - Unidad 2 (Entidades y Unidades de Persistencia)