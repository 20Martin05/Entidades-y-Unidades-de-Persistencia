package U1.controlador;

import U1.modelo.Categoria;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CategoriaControlador {
    private final VentanaEmpresa vista;

    public CategoriaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    public void Clistar() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción", "Estado"}, 0);
        vista.tbl_CCategorias.setModel(modelo);

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            List<Categoria> lista = em.createQuery("SELECT c FROM Categoria c ORDER BY c.id", Categoria.class).getResultList();
            for (Categoria c : lista) {
                modelo.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion(), c.isEstado()});
            }
        }
    }

    public void Cguardar() {
        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            String nombre = String.valueOf(vista.cmb_CNombre.getSelectedItem());
            String descripcion = vista.txt_CDescripcion.getText().trim();
            Boolean estado = vista.cmb_CEstado.getSelectedIndex() == 0;
            Categoria nuevo = new Categoria(0, nombre, descripcion, estado);

            tx.begin();
            em.persist(nuevo);
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Categoría guardada con éxito");
            Clistar();
        } catch (NumberFormatException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "La categoría no tiene datos válidos.");
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar la categoría: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Cactualizar() {
        int filaVista = vista.tbl_CCategorias.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_CCategorias.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona una categoría de la tabla primero");
            return;
        }

        EntityManager em = ConexionJPA.crearEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            int id = ((Number) vista.tbl_CCategorias.getValueAt(fila, 0)).intValue();
            String nuevoNombre = String.valueOf(vista.cmb_CNombre.getSelectedItem());
            String nuevoDescripcion = vista.txt_CDescripcion.getText().trim();
            Boolean nuevoEstado = vista.cmb_CEstado.getSelectedIndex() == 0;

            tx.begin();
            em.createQuery("UPDATE Categoria c SET c.nombre = :nombre, c.descripcion = :descripcion, c.estado = :estado WHERE c.id = :id")
                    .setParameter("nombre", nuevoNombre)
                    .setParameter("descripcion", nuevoDescripcion)
                    .setParameter("estado", nuevoEstado)
                    .setParameter("id", id)
                    .executeUpdate();
            tx.commit();

            JOptionPane.showMessageDialog(vista, "Categoría actualizada con éxito");
            Clistar();
        } catch (NumberFormatException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "La categoría no tiene datos válidos.");
        } catch (RuntimeException ex) {
            if (tx.isActive()) tx.rollback();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar la categoría: " + ex.getMessage());
        } finally {
            if (em.isOpen()) em.close();
        }
    }

    public void Celiminar() {
        int filaVista = vista.tbl_CCategorias.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_CCategorias.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona una categoría de la tabla primero");
            return;
        }

        int id = ((Number) vista.tbl_CCategorias.getValueAt(fila, 0)).intValue();

        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Estás seguro de eliminar esta categoría?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) return;

        try (EntityManager em = ConexionJPA.crearEntityManager()) {
            long productos = em.createQuery(
                            "SELECT COUNT(p) FROM Productos p WHERE p.categoriaRelacion.id = :id", Long.class)
                    .setParameter("id", id)
                    .getSingleResult();
            if (productos > 0) {
                JOptionPane.showMessageDialog(vista,
                        "No se puede eliminar: la categoría tiene " + productos + " producto(s).");
                return;
            }

            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.createQuery("DELETE FROM Categoria c WHERE c.id = :id")
                        .setParameter("id", id)
                        .executeUpdate();
                tx.commit();
                JOptionPane.showMessageDialog(vista, "Categoría eliminada con éxito");
                Clistar();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                JOptionPane.showMessageDialog(vista, "No se pudo eliminar la categoría: " + ex.getMessage());
            }
        }
    }

    public void Climpiar() {
        vista.cmb_CNombre.setSelectedIndex(0);
        vista.txt_CDescripcion.setText("");
        vista.cmb_CEstado.setSelectedIndex(0);
    }
}
