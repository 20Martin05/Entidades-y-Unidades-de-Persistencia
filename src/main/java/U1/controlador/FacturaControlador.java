package U1.controlador;

import U1.vista.VentanaEmpresa;
import U1.modelo.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaControlador {
    private final VentanaEmpresa vista;
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("postgres");
    private final EntityManager em = emf.createEntityManager();

    public FacturaControlador(VentanaEmpresa vista) {
        this.vista = vista;
    }

    public void Flistar() {
        DefaultTableModel m = new DefaultTableModel(
                new String[]{"ID","Número","Fecha","Subtotal","Impuesto","Total","Cliente"}, 0);
        vista.tbl_FFacturas.setModel(m);
        for (Factura f : em.createQuery("FROM Factura", Factura.class).getResultList()) {
            m.addRow(new Object[]{f.getId(), f.getNumero(), f.getFecha(), f.getSubtotal(), f.getImpuesto(), f.getTotal(), f.getCliente() == null ? "" : f.getCliente().getNombre()});
        }
    }

    public void cargarCombos() {
        vista.cmb_FCliente.removeAllItems();
        for (Cliente c : em.createQuery("FROM Cliente", Cliente.class).getResultList()) {
            vista.cmb_FCliente.addItem(c.getNombre());
        }
        vista.cmb_FProducto.removeAllItems();
        for (Productos p : em.createQuery("FROM Productos", Productos.class).getResultList()) {
            vista.cmb_FProducto.addItem(p.getNombre());
        }
    }

    public void Fguardar() {
        try {
            Factura f = leerFactura(new Factura());
            em.getTransaction().begin();
            em.persist(f);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Factura guardada con éxito");
            Flistar();
        } catch (Exception e) {
            rollback(); JOptionPane.showMessageDialog(vista, "No se pudo guardar la factura: " + e.getMessage());
            }
    }

    public void Factualizar() {
        int fila = vista.tbl_FFacturas.getSelectedRow(); if (fila < 0) { JOptionPane.showMessageDialog(vista, "Selecciona una factura");
            return;
        } try { int id = ((Number) vista.tbl_FFacturas.getValueAt(fila, 0)).intValue();
            em.getTransaction().begin(); Factura f = em.find(Factura.class, id); leerFactura(f);
            em.getTransaction().commit(); JOptionPane.showMessageDialog(vista, "Factura actualizada con éxito");
            Flistar();
            } catch (Exception e) {
                rollback(); JOptionPane.showMessageDialog(vista, "No se pudo actualizar la factura: " + e.getMessage());
                }
    }
    public void Feliminar() {
        int fila = vista.tbl_FFacturas.getSelectedRow(); if (fila < 0) {
            JOptionPane.showMessageDialog(vista, "Selecciona una factura"); return; }
        try {
            int id = ((Number) vista.tbl_FFacturas.getValueAt(fila, 0)).intValue();
            em.getTransaction().begin();
            Factura f = em.find(Factura.class, id);
            if (f != null) em.remove(f);
            em.getTransaction().commit();
            JOptionPane.showMessageDialog(vista, "Factura eliminada con éxito");
            Flistar();
        } catch (Exception e) {
            rollback(); JOptionPane.showMessageDialog(vista, "No se pudo eliminar la factura: " + e.getMessage());
        }
    }

    public void Flimpiar() {
        vista.txt_FNumero.setText("");
        vista.txt_FFecha.setText("");
        vista.txt_FSubTotal.setText("");
        vista.txt_FImpuesto.setText("");
        vista.txt_FTotal.setText("");
    }

    private Factura leerFactura(Factura f) {
        f.setNumero(vista.txt_FNumero.getText().trim());
        f.setFecha(LocalDate.parse(vista.txt_FFecha.getText().trim()));
        f.setSubtotal(new BigDecimal(vista.txt_FSubTotal.getText().trim()));
        f.setImpuesto(new BigDecimal(vista.txt_FImpuesto.getText().trim()));
        f.setTotal(new BigDecimal(vista.txt_FTotal.getText().trim()));
        String cliente = String.valueOf(vista.cmb_FCliente.getSelectedItem());
        f.setCliente(em.createQuery("FROM Cliente c WHERE c.nombre=:n", Cliente.class).setParameter("n", cliente).getResultStream().findFirst().orElse(null));
        String producto = String.valueOf(vista.cmb_FProducto.getSelectedItem());
        Productos productoSeleccionado = em.createQuery("FROM Productos p WHERE p.nombre=:n", Productos.class).setParameter("n", producto).getResultStream().findFirst().orElse(null);
        f.getProductos().clear();
        if (productoSeleccionado != null) f.getProductos().add(productoSeleccionado);
        return f;
    }
    private void rollback() {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
    }
}
