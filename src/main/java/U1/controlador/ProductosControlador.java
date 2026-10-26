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

    public void Plistar() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Precio", "Cantidad", "categoria_id"}, 0);
        vista.tbl_PProductos.setRowSorter(null);
        vista.tbl_PProductos.setModel(modelo);

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Productos> lista = em.createQuery("FROM Productos", Productos.class).getResultList();
            for (Productos p : lista) {
                Integer categoriaId = p.getCategoriaRelacion() == null
                        ? null
                        : p.getCategoriaRelacion().getId();
                modelo.addRow(new Object[]{
                        p.getId(), p.getNombre(), p.getPrecio(), p.getCantidad(), categoriaId
                });
            }
        }
    }

    public void Pguardar() {
        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            String nombre = vista.txt_PNombre.getText().trim();
            BigDecimal precio = new BigDecimal(vista.txt_PPrecio.getText().trim());
            int cantidad = Integer.parseInt(vista.txt_PCantidad.getText().trim());

            Productos nuevo = new Productos(0, nombre, precio, cantidad);
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
        int filaVista = vista.tbl_PProductos.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_PProductos.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un producto de la tabla primero");
            return;
        }

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            int id = ((Number) vista.tbl_PProductos.getValueAt(fila, 0)).intValue();
            String nuevoNombre = vista.txt_PNombre.getText().trim();
            BigDecimal nuevoPrecio = new BigDecimal(vista.txt_PPrecio.getText().trim());
            int nuevaCantidad = Integer.parseInt(vista.txt_PCantidad.getText().trim());

            tx.begin();
            Productos producto = em.find(Productos.class, id);
            if (producto != null) {
                producto.setNombre(nuevoNombre);
                producto.setPrecio(nuevoPrecio);
                producto.setCantidad(nuevaCantidad);
                producto.setCategoriaRelacion(categoriaSeleccionada(em));
            }
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
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    public void Peliminar() {
        int filaVista = vista.tbl_PProductos.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_PProductos.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un producto de la tabla primero");
            return;
        }

        int id = ((Number) vista.tbl_PProductos.getValueAt(fila, 0)).intValue();
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar este producto?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

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
            return em.createQuery(
                            "SELECT p FROM Productos p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.nombre",
                            Productos.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void PbuscarYMostrar(String texto) {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Precio", "Cantidad", "categoria_id"}, 0
        );

        List<Productos> lista = PbuscarPorNombre(texto);

        for (Productos p : lista) {
            Integer categoriaId = p.getCategoriaRelacion() == null
                    ? null
                    : p.getCategoriaRelacion().getId();

            modelo.addRow(new Object[]{
                    p.getId(),
                    p.getNombre(),
                    p.getPrecio(),
                    p.getCantidad(),
                    categoriaId
            });
        }

        vista.tbl_PProductos.setModel(modelo);
    }
}
