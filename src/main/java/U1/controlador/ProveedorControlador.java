package U1.controlador;

import U1.modelo.Proveedor;
import U1.modelo.Categoria;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ProveedorControlador {
    private final VentanaEmpresa vista;

    public ProveedorControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    public void PRlistar() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Empresa", "Teléfono", "Correo", "Dirección", "Categoría ID"}, 0);
        vista.tbl_PRProveedores.setRowSorter(null);
        vista.tbl_PRProveedores.setModel(modelo);

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Proveedor> lista = em.createQuery("FROM Proveedor", Proveedor.class).getResultList();
            for (Proveedor p : lista) {
                Integer categoriaId = null;
                if (p.getCategorias() != null && !p.getCategorias().isEmpty()) {
                    categoriaId = p.getCategorias().iterator().next().getId();
                }

                modelo.addRow(new Object[]{
                        p.getId(), p.getNombre(), p.getEmpresa(), p.getTelefono(), p.getCorreo(), p.getDireccion(), categoriaId
                });
            }
        }
    }

    public void cargarComboCategorias() {
        if (vista.cmb_PRCategoria != null) {
            vista.cmb_PRCategoria.removeAllItems();
            try (EntityManager em = ConexionJPA.crearEntityManager()) {
                List<Categoria> lista = em.createQuery("FROM Categoria", Categoria.class).getResultList();
                for (Categoria c : lista) {
                    vista.cmb_PRCategoria.addItem(c.getNombre());
                }
            }
        }
    }

    public void PRguardar() {
        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            String nombre = vista.txt_PRNombre.getText().trim();
            String empresa = vista.txt_PREmpresa.getText().trim();
            String telefono = vista.txt_PRTelefono.getText().trim();
            String correo = vista.txt_PRCorreo.getText().trim();
            String direccion = vista.txt_PRDireccion.getText().trim();

            Proveedor nuevo = new Proveedor();
            nuevo.setNombre(nombre);
            nuevo.setEmpresa(empresa);
            nuevo.setTelefono(telefono);
            nuevo.setCorreo(correo);
            nuevo.setDireccion(direccion);

            Object seleccionCat = vista.cmb_PRCategoria.getSelectedItem();
            if (seleccionCat != null) {
                Categoria cat = em.createQuery("FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                        .setParameter("nombre", seleccionCat.toString())
                        .getResultStream().findFirst().orElse(null);
                if (cat != null) {
                    nuevo.getCategorias().add(cat);
                    cat.getProveedores().add(nuevo);
                }
            }

            tx.begin();
            em.persist(nuevo);
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Proveedor guardado con éxito");
            PRlistar();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar el proveedor: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void PRactualizar() {
        int filaVista = vista.tbl_PRProveedores.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_PRProveedores.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un proveedor de la tabla primero");
            return;
        }

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            int id = ((Number) vista.tbl_PRProveedores.getValueAt(fila, 0)).intValue();
            String nuevoNombre = vista.txt_PRNombre.getText().trim();
            String nuevaEmpresa = vista.txt_PREmpresa.getText().trim();
            String nuevoTelefono = vista.txt_PRTelefono.getText().trim();
            String nuevoCorreo = vista.txt_PRCorreo.getText().trim();
            String nuevaDireccion = vista.txt_PRDireccion.getText().trim();

            tx.begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) {
                proveedor.setNombre(nuevoNombre);
                proveedor.setEmpresa(nuevaEmpresa);
                proveedor.setTelefono(nuevoTelefono);
                proveedor.setCorreo(nuevoCorreo);
                proveedor.setDireccion(nuevaDireccion);

                Object seleccionCat = vista.cmb_PRCategoria.getSelectedItem();
                if (seleccionCat != null) {
                    Categoria cat = em.createQuery("FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                            .setParameter("nombre", seleccionCat.toString())
                            .getResultStream().findFirst().orElse(null);
                    if (cat != null) {
                        proveedor.getCategorias().clear();
                        proveedor.getCategorias().add(cat);
                    }
                }
            }
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Proveedor actualizado con éxito");
            PRlistar();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar el proveedor: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void PReliminar() {
        int filaVista = vista.tbl_PRProveedores.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_PRProveedores.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un proveedor de la tabla primero");
            return;
        }

        int id = ((Number) vista.tbl_PRProveedores.getValueAt(fila, 0)).intValue();

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar este proveedor?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Proveedor proveedor = em.find(Proveedor.class, id);
            if (proveedor != null) em.remove(proveedor);
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Proveedor eliminado con éxito");
            PRlistar();
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar el proveedor: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void PRlimpiar() {
        vista.txt_PRNombre.setText("");
        vista.txt_PREmpresa.setText("");
        vista.txt_PRTelefono.setText("");
        vista.txt_PRCorreo.setText("");
        vista.txt_PRDireccion.setText("");
    }

    public List<Proveedor> PRbuscarPorNombre(String nombre) {
        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            return em.createQuery(
                            "SELECT p FROM Proveedor p WHERE LOWER(p.nombre) LIKE LOWER(:nombre) ORDER BY p.nombre",
                            Proveedor.class)
                    .setParameter("nombre", "%" + nombre + "%")
                    .getResultList();
        }
    }

    public void PRbuscarYMostrar(String texto) {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Empresa", "Teléfono", "Correo", "Dirección", "Categoría ID"}, 0);
        List<Proveedor> lista = PRbuscarPorNombre(texto);
        for (Proveedor p : lista) {
            Integer categoriaId = null;
            if (p.getCategorias() != null && !p.getCategorias().isEmpty()) {
                categoriaId = p.getCategorias().iterator().next().getId();
            }
            modelo.addRow(new Object[]{
                    p.getId(), p.getNombre(), p.getEmpresa(), p.getTelefono(), p.getCorreo(), p.getDireccion(), categoriaId
            });
        }
        vista.tbl_PRProveedores.setModel(modelo);
    }
}
