package U1.modelo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categorias")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(nullable = false)
    private boolean estado = true;

    @OneToMany(mappedBy = "categoriaRelacion")
    private Set<Productos> productos = new HashSet<>();

    @Override
    public String toString() {
        return "Categoria{id=" + id + ", nombre='" + nombre + "'}";
    }
}
