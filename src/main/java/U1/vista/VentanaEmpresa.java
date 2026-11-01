package U1.vista;

import U1.controlador.*;
import U1.vista.base.VentanaEmpresaUI;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.util.Comparator;

@SuppressWarnings("unchecked")
public class VentanaEmpresa extends VentanaEmpresaUI {

    private final ProductosControlador Pcontrolador;
    private final CategoriaControlador Ccontrolador;
    private final FacturaControlador Fcontrolador;
    private final ClientesControlador CLcontrolador;

    private TableRowSorter<DefaultTableModel> Psorter;
    private TableRowSorter<DefaultTableModel> Fsorter;
    private TableRowSorter<DefaultTableModel> CLsorter;

    public VentanaEmpresa() {
        this.Pcontrolador = new ProductosControlador(this);
        this.Ccontrolador = new CategoriaControlador(this);
        this.Fcontrolador = new FacturaControlador(this);
        this.CLcontrolador = new ClientesControlador(this);

        setTitle("Gestión de Productos - Corporación Favorita");
        setContentPane(PanelPrincipal);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        iniciar();
    }

    public void iniciar() {
        cargarCategorias();
        cargarEstados();
        configurarEventos();

        // Productos
        Pcontrolador.Plistar();
        Psorter = new TableRowSorter<>((DefaultTableModel) tbl_PProductos.getModel());
        Psorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_PProductos.setRowSorter(Psorter);

        // Categorías
        Ccontrolador.Clistar();

        // Facturas
        Fcontrolador.cargarCombos();
        Fcontrolador.Flistar();
        Fsorter = new TableRowSorter<>((DefaultTableModel) tbl_FFacturas.getModel());
        Fsorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_FFacturas.setRowSorter(Fsorter);

        // Clientes
        CLcontrolador.CLlistar();
        CLcontrolador.cargarComboFacturas();
        CLsorter = new TableRowSorter<>((DefaultTableModel) tbl_CLClientes.getModel());
        CLsorter.setComparator(0, Comparator.comparingInt(valor -> Integer.parseInt(valor.toString())));
        tbl_CLClientes.setRowSorter(CLsorter);

        configurarEventosTablas();
    }

    private void recargarTodo() {
        Fcontrolador.cargarCombos();
        CLcontrolador.cargarComboFacturas();

        Pcontrolador.Plistar();
        Ccontrolador.Clistar();
        Fcontrolador.Flistar();
        CLcontrolador.CLlistar();
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
        // PRODUCTOS
        btn_PGuardar.addActionListener(e -> { Pcontrolador.Pguardar(); recargarTodo(); });
        btn_PActualizar.addActionListener(e -> { Pcontrolador.Pactualizar(); recargarTodo(); });
        btn_PEliminar.addActionListener(e -> { Pcontrolador.Peliminar(); recargarTodo(); });
        btn_PLimpiar.addActionListener(e -> Pcontrolador.Plimpiar());

        // CATEGORIA
        btn_CGuardar.addActionListener(e -> { Ccontrolador.Cguardar(); recargarTodo(); });
        btn_CActualizar.addActionListener(e -> { Ccontrolador.Cactualizar(); recargarTodo(); });
        btn_CEliminar.addActionListener(e -> { Ccontrolador.Celiminar(); recargarTodo(); });
        btn_CLimpiar.addActionListener(e -> Ccontrolador.Climpiar());

        // FACTURA
        btn_FGuardar.addActionListener(e -> { Fcontrolador.Fguardar(); recargarTodo(); });
        btn_FActualizar.addActionListener(e -> { Fcontrolador.Factualizar(); recargarTodo(); });
        btn_FEliminar.addActionListener(e -> { Fcontrolador.Feliminar(); recargarTodo(); });
        btn_FLimpiar.addActionListener(e -> Fcontrolador.Flimpiar());

        // CLIENTE
        btn_CLGuardar.addActionListener(e -> { CLcontrolador.CLguardar(); recargarTodo(); });
        btn_CLActualizar.addActionListener(e -> { CLcontrolador.CLactualizar(); recargarTodo(); });
        btn_CLEliminar.addActionListener(e -> { CLcontrolador.CLeliminar(); recargarTodo(); });
        btn_CLLimpiar.addActionListener(e -> CLcontrolador.CLlimpiar());

        // BUSCADORES
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

        txt_CLBuscador.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarClientes(); }
            public void removeUpdate(DocumentEvent e) { filtrarClientes(); }
            public void changedUpdate(DocumentEvent e) { filtrarClientes(); }
        });
    }

    private void filtrarProductos() {
        String texto = txt_PBuscador.getText().trim();
        if (texto.isEmpty()) {
            Pcontrolador.Plistar();
            return;
        }
        Pcontrolador.PbuscarYMostrar(texto);
    }

    private void filtrarFacturas() {
        String texto = txt_FBuscador.getText().trim();
        if (texto.isEmpty()) {
            Fcontrolador.Flistar();
            return;
        }
        Fcontrolador.FbuscarYMostrar(texto);
    }

    private void filtrarClientes() {
        String texto = txt_CLBuscador.getText().trim();
        if (texto.isEmpty()) {
            CLcontrolador.CLlistar();
            return;
        }
        CLcontrolador.CLbuscarYMostrar(texto);
    }

    private void configurarEventosTablas() {
        tbl_PProductos.addMouseListener(new MouseAdapter() {
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

        tbl_CLClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_CLClientes.getSelectedRow();
                int fila = filaVista >= 0 ? tbl_CLClientes.convertRowIndexToModel(filaVista) : -1;
                if (fila != -1) {
                    txt_CLNombre.setText(tbl_CLClientes.getValueAt(fila, 1) != null ? tbl_CLClientes.getValueAt(fila, 1).toString() : "");
                    txt_CLApellido.setText(tbl_CLClientes.getValueAt(fila, 2) != null ? tbl_CLClientes.getValueAt(fila, 2).toString() : "");
                    txt_CLCedula.setText(tbl_CLClientes.getValueAt(fila, 3) != null ? tbl_CLClientes.getValueAt(fila, 3).toString() : "");
                    txt_CLTelefono.setText(tbl_CLClientes.getValueAt(fila, 4) != null ? tbl_CLClientes.getValueAt(fila, 4).toString() : "");
                    txt_CLCorreo.setText(tbl_CLClientes.getValueAt(fila, 5) != null ? tbl_CLClientes.getValueAt(fila, 5).toString() : "");
                }
            }
        });

        tbl_FFacturas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Fcontrolador.Fcargar();
            }
        });
    }
}