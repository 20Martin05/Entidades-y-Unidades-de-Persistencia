package U1.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "productos")
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(nullable = false)
    private int cantidad;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoriaRelacion;

    @OneToMany(mappedBy = "producto")
    private List<FacturaDetalle> detalles = new ArrayList<>();

    public Productos() {}

    public Productos(int id, String nombre, BigDecimal precio, int cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public Categoria getCategoriaRelacion() { return categoriaRelacion; }
    public void setCategoriaRelacion(Categoria categoriaRelacion) { this.categoriaRelacion = categoriaRelacion; }

    public List<FacturaDetalle> getDetalles() { return detalles; }
    public void setDetalles(List<FacturaDetalle> detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Productos{id=" + id + ", nombre='" + nombre + "'}";
    }
}
