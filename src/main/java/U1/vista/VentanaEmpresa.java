package U1.vista;

import U1.controlador.*;
import U1.vista.base.VentanaEmpresaUI;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Comparator;
import java.util.function.Consumer;

public class VentanaEmpresa extends VentanaEmpresaUI {

    private final ProductosControlador Pcontrolador;
    private final CategoriaControlador Ccontrolador;
    private final FacturaControlador Fcontrolador;
    private final ClientesControlador CLcontrolador;

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

        Pcontrolador.Plistar();
        configurarSorter(tbl_PProductos);

        Ccontrolador.Clistar();

        Fcontrolador.cargarCombos();
        Fcontrolador.Flistar();
        configurarSorter(tbl_FFacturas);

        CLcontrolador.CLlistar();
        CLcontrolador.cargarComboFacturas();
        configurarSorter(tbl_CLClientes);

        configurarEventosTablas();
    }

    /**
     * Ordena por la columna 0 como número. Solo se aplica a tablas cuya
     * columna 0 es el ID (entero). Si el valor no es numérico, se trata como 0
     * para evitar NumberFormatException durante el ordenamiento.
     */
    private void configurarSorter(JTable tabla) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) tabla.getModel());
        sorter.setComparator(0, Comparator.comparingInt(valor -> {
            try {
                return Integer.parseInt(valor.toString());
            } catch (NumberFormatException e) {
                return 0;
            }
        }));
        tabla.setRowSorter(sorter);
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
        String[] cats = {"LÁCTEOS", "CARNES", "FRUTAS", "VERDURAS", "GRANOS", "PANADERÍA", "BEBIDAS", "ENLATADOS", "SNACKS"};
        cmb_CNombre.removeAllItems();
        cmb_PCategoria.removeAllItems();
        for (String c : cats) {
            cmb_CNombre.addItem(c);
            cmb_PCategoria.addItem(c);
        }
    }

    private void cargarEstados() {
        cmb_CEstado.removeAllItems();
        cmb_CEstado.addItem("DISPONIBLE");
        cmb_CEstado.addItem("AGOTADO");
        cmb_CEstado.setSelectedIndex(0);
    }

    private void agregarBuscador(JTextField campo, Consumer<String> accion) {
        campo.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { accion.accept(campo.getText().trim()); }
            public void removeUpdate(DocumentEvent e) { accion.accept(campo.getText().trim()); }
            public void changedUpdate(DocumentEvent e) { accion.accept(campo.getText().trim()); }
        });
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
        agregarBuscador(txt_PBuscador, t -> { if (t.isEmpty()) Pcontrolador.Plistar(); else Pcontrolador.PbuscarYMostrar(t); });
        agregarBuscador(txt_FBuscador, t -> { if (t.isEmpty()) Fcontrolador.Flistar(); else Fcontrolador.FbuscarYMostrar(t); });
        agregarBuscador(txt_CLBuscador, t -> { if (t.isEmpty()) CLcontrolador.CLlistar(); else CLcontrolador.CLbuscarYMostrar(t); });
    }

    private void configurarEventosTablas() {
        tbl_PProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_PProductos.getSelectedRow();
                if (filaVista >= 0) {
                    int fila = tbl_PProductos.convertRowIndexToModel(filaVista);
                    txt_PNombre.setText(valorCelda(tbl_PProductos, fila, 1));
                    txt_PPrecio.setText(valorCelda(tbl_PProductos, fila, 2));
                    txt_PCantidad.setText(valorCelda(tbl_PProductos, fila, 3));
                }
            }
        });

        tbl_CCategorias.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_CCategorias.getSelectedRow();
                if (filaVista >= 0) {
                    int fila = tbl_CCategorias.convertRowIndexToModel(filaVista);
                    boolean estado = Boolean.parseBoolean(valorCelda(tbl_CCategorias, fila, 3));
                    cmb_CEstado.setSelectedIndex(estado ? 0 : 1);
                    cmb_CNombre.setSelectedItem(valorCelda(tbl_CCategorias, fila, 1));
                    txt_CDescripcion.setText(valorCelda(tbl_CCategorias, fila, 2));
                }
            }
        });

        tbl_CLClientes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int filaVista = tbl_CLClientes.getSelectedRow();
                if (filaVista >= 0) {
                    int fila = tbl_CLClientes.convertRowIndexToModel(filaVista);
                    txt_CLNombre.setText(valorCelda(tbl_CLClientes, fila, 1));
                    txt_CLApellido.setText(valorCelda(tbl_CLClientes, fila, 2));
                    txt_CLCedula.setText(valorCelda(tbl_CLClientes, fila, 3));
                    txt_CLTelefono.setText(valorCelda(tbl_CLClientes, fila, 4));
                    txt_CLCorreo.setText(valorCelda(tbl_CLClientes, fila, 5));
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

    private String valorCelda(JTable tabla, int fila, int col) {
        Object val = tabla.getValueAt(fila, col);
        return val != null ? val.toString() : "";
    }
}