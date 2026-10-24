package U1.vista;

import U1.controlador.CategoriaControlador;
import U1.controlador.ProductosControlador;
import U1.controlador.FacturaControlador;
import U1.vista.base.VentanaEmpresaUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.regex.Pattern;
import java.util.Comparator;

// Ventana Swing (Vista) que delega la lógica de negocio y persistencia a ProductosControlador.
public class VentanaEmpresa extends VentanaEmpresaUI {

    private final ProductosControlador Pcontrolador;
    private final CategoriaControlador Ccontrolador;
    private final FacturaControlador Fcontrolador;

    private TableRowSorter<DefaultTableModel> Psorter;
    private TableRowSorter<DefaultTableModel> Fsorter;

    public VentanaEmpresa() {
        // Inicializa el controlador asociándolo a esta vista
        this.Pcontrolador = new ProductosControlador(this);
        this.Ccontrolador = new CategoriaControlador(this);
        this.Fcontrolador = new FacturaControlador(this);

        // Configuración visual básica de la ventana
        setTitle("Gestión de Productos - Corporación Favorita");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Método general que orquesta todas las inicializaciones
        iniciar();
    }

    // Método general que centraliza y llama a todas las configuraciones iniciales
    public void iniciar() {
        cargarCategorias();
        cargarEstados();
        configurarEventos();
        Pcontrolador.Plistar();
        Pcontrolador.PbuscarPorNombre(txt_PBuscador.getText());
        Psorter = new TableRowSorter<>((DefaultTableModel) tbl_PProductos.getModel());
        Psorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_PProductos.setRowSorter(Psorter);
        Ccontrolador.Clistar();
        Fcontrolador.cargarCombos();
        Fcontrolador.Flistar();
        Fsorter = new TableRowSorter<>((DefaultTableModel) tbl_FFacturas.getModel());
        Fsorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_FFacturas.setRowSorter(Fsorter);
        Empresa(tbl_PProductos, tbl_CCategorias);
    }

    private void cargarCategorias() {
        cmb_CNombre.removeAllItems();
        cmb_CNombre.addItem("LÁCTEOS");
        cmb_CNombre.addItem("CARNES");
        cmb_CNombre.addItem("FRUTAS");
        cmb_CNombre.addItem("VERDURAS");
        cmb_CNombre.addItem("GRANOS");
        cmb_CNombre.addItem("PANADERÍA");
        cmb_CNombre.addItem("BEBIDAS");
        cmb_CNombre.addItem("ENLATADOS");
        cmb_CNombre.addItem("SNACKS");
        cmb_PCategoria.removeAllItems();
        for (int i = 0; i < cmb_CNombre.getItemCount(); i++) {
            cmb_PCategoria.addItem(cmb_CNombre.getItemAt(i));
        }
    }

    private void cargarEstados() {
        cmb_CEstado.removeAllItems();
        cmb_CEstado.addItem("DISPONIBLE");
        cmb_CEstado.addItem("AGOTADO");
        cmb_CEstado.setSelectedIndex(0);
    }

    private void configurarEventos() {
        // Conecta cada botón con la operación correspondiente en el controlador
        btn_PGuardar.addActionListener(e -> Pcontrolador.Pguardar());
        btn_PActualizar.addActionListener(e -> Pcontrolador.Pactualizar());
        btn_PEliminar.addActionListener(e -> Pcontrolador.Peliminar());
        btn_PLimpiar.addActionListener(e -> Pcontrolador.Plimpiar());

        //CATEGORIA
        btn_CGuardar.addActionListener(e -> Ccontrolador.Cguardar());
        btn_CActualizar.addActionListener(e -> Ccontrolador.Cactualizar());
        btn_CEliminar.addActionListener(e -> Ccontrolador.Celiminar());
        btn_CLimpiar.addActionListener(e -> Ccontrolador.Climpiar());
        btn_FGuardar.addActionListener(e -> Fcontrolador.Fguardar());
        btn_FActualizar.addActionListener(e -> Fcontrolador.Factualizar());
        btn_FEliminar.addActionListener(e -> Fcontrolador.Feliminar());
        btn_FLimpiar.addActionListener(e -> Fcontrolador.Flimpiar());

        //BUSCADORES
        txt_PBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarProductos(); }
            public void removeUpdate(DocumentEvent e) { filtrarProductos(); }
            public void changedUpdate(DocumentEvent e) { filtrarProductos(); }
        });

        txt_FBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarFacturas(); }
            public void removeUpdate(DocumentEvent e) { filtrarFacturas(); }
            public void changedUpdate(DocumentEvent e) { filtrarFacturas(); }
        });
    }

    private void filtrarProductos() {
        String PTexto = txt_PBuscador.getText().trim();

        if (PTexto.isEmpty()) {
            Pcontrolador.Plistar();
            return;
        }

        Pcontrolador.PbuscarYMostrar(PTexto);

    }
    private void filtrarFacturas() {
        String FTexto = txt_FBuscador.getText().trim();
        if (FTexto.isEmpty()) {
            Fcontrolador.Flistar();
            return;
        }
        Fcontrolador.FbuscarYMostrar(FTexto);
    }
    public void Empresa(JTable tblPProductos, JTable tbl_CCategorias) {
        tblPProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_PProductos.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_PProductos.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    txt_PNombre.setText(tbl_PProductos.getValueAt(fila, 1).toString());
                    txt_PPrecio.setText(tbl_PProductos.getValueAt(fila, 2).toString());
                    txt_PCantidad.setText(tbl_PProductos.getValueAt(fila, 3).toString());
                }
            }
        });

        tbl_CCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_CCategorias.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_CCategorias.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    boolean estado = Boolean.parseBoolean(tbl_CCategorias.getValueAt(fila, 3).toString());
                    cmb_CEstado.setSelectedIndex(estado ? 0 : 1);
                    cmb_CNombre.setSelectedItem(tbl_CCategorias.getValueAt(fila, 1).toString());
                    txt_CDescripcion.setText(tbl_CCategorias.getValueAt(fila, 2).toString());

                }
            }
        });

    }

}
