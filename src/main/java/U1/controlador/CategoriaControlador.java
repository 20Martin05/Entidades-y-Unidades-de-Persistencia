package U1.controlador;

import U1.modelo.Categoria;
import U1.vista.VentanaEmpresa;
import U1.util.ConexionJPA;
import jakarta.persistence.EntityManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class CategoriaControlador {
    private final VentanaEmpresa vista;
    private final EntityManager em = ConexionJPA.crearEntityManager();

    public CategoriaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    public void Clistar() {
        DefaultTableModel modelo = new DefaultTableModel(
                new String[]{"ID", "Nombre", "Descripción", "Estado"}, 0);
        vista.tbl_CCategorias.setModel(modelo);
        List<Categoria> lista = em.createQuery("SELECT c FROM Categoria c ORDER BY c.id", Categoria.class).getResultList();
        for (Categoria c : lista) {
            modelo.addRow(new Object[]{c.getId(), c.getNombre(), c.getDescripcion(), c.isEstado()});
        }
    }

    public void Cguardar() {
        try {
            String nombre = String.valueOf(vista.cmb_CNombre.getSelectedItem());
            String  descripcion = vista.txt_CDescripcion.getText().trim();
            Boolean estado = vista.cmb_CEstado.getSelectedIndex() == 0;
            Categoria nuevo = new Categoria(0, nombre, descripcion, estado);
            em.getTransaction().begin();
            em.persist(nuevo);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Categoría guardada con éxito");
            Clistar();
        } catch (NumberFormatException ex) {
            CcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "La categoría no tiene datos válidos.");
        } catch (RuntimeException ex) {
            CcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "No se pudo guardar la categoría: " + ex.getMessage());
        }
    }

    public void Cactualizar() {
        int filaVista = vista.tbl_CCategorias.getSelectedRow();
        int fila = filaVista >= 0 ? vista.tbl_CCategorias.convertRowIndexToModel(filaVista) : -1;
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona una categoría de la tabla primero");
            return;
        }
        try {
            int id = ((Number) vista.tbl_CCategorias.getValueAt(fila, 0)).intValue();
            String nuevoNombre = String.valueOf(vista.cmb_CNombre.getSelectedItem());
            String nuevoDescripcion = vista.txt_CDescripcion.getText().trim();
            Boolean nuevoEstado = vista.cmb_CEstado.getSelectedIndex() == 0;
            em.getTransaction().begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria != null) {
                categoria.setNombre(nuevoNombre);
                categoria.setDescripcion(nuevoDescripcion);
                categoria.setEstado(nuevoEstado);
            }
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Categoría actualizada con éxito");
            Clistar();
        } catch (NumberFormatException ex) {
            CcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "La categoría no tiene datos válidos.");
        } catch (RuntimeException ex) {
            CcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "No se pudo actualizar la categoría: " + ex.getMessage());
        }
    }

    private void CcancelarTransaccionSiNecesario() {
        if (em.getTransaction().isActive()) em.getTransaction().rollback();
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
        try {
            em.getTransaction().begin();
            Categoria categoria = em.find(Categoria.class, id);
            if (categoria != null) em.remove(categoria);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Categoría eliminada con éxito");
            Clistar();
        } catch (RuntimeException ex) {
            CcancelarTransaccionSiNecesario();
            JOptionPane.showMessageDialog(vista, "No se pudo eliminar la categoría: " + ex.getMessage());
        }
    }

    public void Climpiar() {
        vista.cmb_CNombre.setSelectedIndex(0);
        vista.txt_CDescripcion.setText("");
        vista.cmb_CEstado.setSelectedIndex(0);
    }
}
