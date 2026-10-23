package U1.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

// Entidad JPA que representa los registros de la tabla productos.
@Entity
@Table(name = "productos")
@Getter
@Setter
public class Productos {

    // Identificador generado automáticamente por PostgreSQL.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Nombre comercial del producto.
    @Column
    private String nombre;

    // Precio unitario de venta.
    @Column
    private double precio;

    // Cantidad disponible en inventario.
    @Column
    private int cantidad;

    // Categoría a la que pertenece el producto.
    @Column
    private String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoriaRelacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id")
    private Proveedor proveedor;

    @ManyToMany(mappedBy = "productos")
    private Set<Factura> facturas = new HashSet<>();

    // Constructor requerido por JPA.
    public Productos() {
    }

    // Constructor utilizado para crear productos desde la interfaz.
    public Productos(int id, String nombre, double precio, int cantidad, String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
        this.categoria = categoria;
    }

    @Override
    public String toString() {
        return "Productos{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                ", cantidad=" + cantidad +
                ", categoria='" + categoria + '\'' +
                '}';
    }
}
