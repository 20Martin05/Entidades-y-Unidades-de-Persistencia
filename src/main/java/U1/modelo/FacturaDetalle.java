package U1.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "factura_detalle",
        uniqueConstraints = @UniqueConstraint(columnNames = {"factura_id", "producto_id"}))
@Getter
@Setter
@NoArgsConstructor
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

    @Override
    public String toString() {
        return "FacturaDetalle{id=" + id + ", cantidad=" + cantidad + "}";
    }
}
