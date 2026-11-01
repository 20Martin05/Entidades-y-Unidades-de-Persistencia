package U1.modelo;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "factura_detalle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"factura_id", "producto_id"}))
public class FacturaDetalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "factura_id", nullable = false)
    private Factura factura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "producto_id", nullable = false)
    private Productos producto;

    @Column(nullable = false)
    private int cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    public FacturaDetalle() {}

    public FacturaDetalle(Factura factura, Productos producto, int cantidad, BigDecimal precioUnitario) {
        this.factura = factura;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Factura getFactura() { return factura; }
    public void setFactura(Factura factura) { this.factura = factura; }

    public Productos getProducto() { return producto; }
    public void setProducto(Productos producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) { this.precioUnitario = precioUnitario; }

    public BigDecimal getSubtotalLinea() {
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    @Override
    public String toString() {
        return "FacturaDetalle{id=" + id + ", cantidad=" + cantidad + "}";
    }
}
