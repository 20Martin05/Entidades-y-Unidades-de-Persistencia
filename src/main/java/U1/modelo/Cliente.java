package U1.modelo;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "clientes")
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

    public Cliente() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public Set<Factura> getFacturas() { return facturas; }
    public void setFacturas(Set<Factura> facturas) { this.facturas = facturas; }

    @Override
    public String toString() {
        return "Cliente{id=" + id + ", nombre='" + nombre + " " + apellido + "'}";
    }
}
