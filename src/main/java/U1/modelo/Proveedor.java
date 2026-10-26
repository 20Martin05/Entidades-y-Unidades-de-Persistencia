package U1.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "proveedores")
@Getter
@Setter
@ToString(exclude = {"productos", "categorias"})
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 100)
    private String empresa;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String correo;

    @Column(length = 255)
    private String direccion;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Productos> productos = new HashSet<>();

    @ManyToMany(mappedBy = "proveedores")
    private Set<Categoria> categorias = new HashSet<>();

    public Proveedor() {
    }
}