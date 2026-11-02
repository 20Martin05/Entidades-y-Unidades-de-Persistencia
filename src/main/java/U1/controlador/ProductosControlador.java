package U1.controlador;

import U1.modelo.Productos;
import U1.modelo.Categoria;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.util.List;

public class ProductosControlador {
    private final VentanaEmpresa vista;

    public ProductosControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isBlank()) return "";
        String[] palabras = texto.trim().toLowerCase().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String p : palabras) {
            if (sb.length() > 0) sb.append(' ');
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
        }
        return sb.toString();
    }

    private DefaultTableModel crearModelo() {
        return new DefaultTableModel(new String[]{"ID", "Nombre", "Precio", "Cantidad", "categoria_id"}, 0);
    }

    private void llenar(DefaultTableModel modelo, List<Productos> lista) {
        for (Productos p : lista) {
            Integer catId = p.getCategoriaRelacion() == null ? null : p.getCategoriaRelacion().getId();
            modelo.addRow(new Object[]{p.getId(), p.getNombre(), p.getPrecio(), p.getCantidad(), catId});
        }
    }

    public void Plistar() {
        DefaultTableModel modelo = crearModelo();
        vista.tbl_PProductos.setRowSorter(null);
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            llenar(modelo, em.createQuery("FROM Productos", Productos.class).getResultList());
        }
        vista.tbl_PProductos.setModel(modelo);
    }

    private boolean camposValidos(String nombre, String precio, String cantidad) {
        if (nombre.isBlank() || precio.isBlank() || cantidad.isBlank()) {
            JOptionPane.showMessageDialog(vista, "Nombre, precio y cantidad son obligatorios.");
            return false;
        }
        return true;
    }

    public void Pguardar() {
        String nombre = vista.txt_PNombre.getText().trim();
        String precio = vista.txt_PPrecio.getText().trim();
        String cantidad = vista.txt_PCantidad.getText().trim();
        if (!camposValidos(nombre, precio, cantidad)) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            Productos nuevo = new Productos();
            nuevo.setNombre(capitalizar(nombre));
            nuevo.setPrecio(new BigDecimal(precio));
            nuevo.setCantidad(Integer.parseInt(cantidad));
            nuevo.setCategoriaRelacion(categoriaSeleccionada(em));

            tx.begin();
            em.persist(nuevo);
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Guardado con éxito");
            Plistar();
        } catch (NumberFormatException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "Precio y cantidad deben ser valores numéricos válidos.");
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar el producto: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Pactualizar() {
        int id = idSeleccionado();
        if (id == -1) return;

        String nombre = vista.txt_PNombre.getText().trim();
        String precio = vista.txt_PPrecio.getText().trim();
        String cantidad = vista.txt_PCantidad.getText().trim();
        if (!camposValidos(nombre, precio, cantidad)) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Productos producto = em.find(Productos.class, id);
            if (producto == null) throw new IllegalStateException("El producto ya no existe");
            producto.setNombre(capitalizar(nombre));
            producto.setPrecio(new BigDecimal(precio));
            producto.setCantidad(Integer.parseInt(cantidad));
            producto.setCategoriaRelacion(categoriaSeleccionada(em));
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Actualizado con éxito");
            Plistar();
        } catch (NumberFormatException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "Precio y cantidad deben ser valores numéricos válidos.");
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el producto: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    private Categoria categoriaSeleccionada(EntityManager em) {
        Object seleccion = vista.cmb_PCategoria.getSelectedItem();
        if (seleccion == null || seleccion.toString().isBlank()) return null;
        return em.createQuery("FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                .setParameter("nombre", seleccion.toString())
                .getResultStream().findFirst().orElse(null);
    }

    public void Peliminar() {
        int id = idSeleccionado();
        if (id == -1) return;

        if (JOptionPane.showConfirmDialog(vista, "¿Estás seguro de eliminar este producto?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Productos producto = em.find(Productos.class, id);
            if (producto != null) em.remove(producto);
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Eliminado con éxito");
            Plistar();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el producto: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Plimpiar() {
        vista.txt_PNombre.setText("");
        vista.txt_PPrecio.setText("");
        vista.txt_PCantidad.setText("");
    }

    public List<Productos> PbuscarPorNombre(String nombre) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery("SELECT p FROM Productos p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.nombre", Productos.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void PbuscarYMostrar(String texto) {
        DefaultTableModel modelo = crearModelo();
        llenar(modelo, PbuscarPorNombre(texto));
        vista.tbl_PProductos.setModel(modelo);
    }

    private int idSeleccionado() {
        int filaVista = vista.tbl_PProductos.getSelectedRow();
        if (filaVista < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona un producto de la tabla primero");
            return -1;
        }
        return ((Number) vista.tbl_PProductos.getValueAt(vista.tbl_PProductos.convertRowIndexToModel(filaVista), 0)).intValue();
    }
}