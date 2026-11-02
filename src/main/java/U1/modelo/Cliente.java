package U1.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @OneToMany(mappedBy = "cliente")
    private Set<Factura> facturas = new HashSet<>();

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + " " + apellido + "'}";
    }
}
