package alquilercochesapp;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Coche {
    private String modelo;
    private String marca;
    private boolean disponible;

    public Coche(String modelo, String marca) {
        this.modelo = modelo;
        this.marca = marca;
        this.disponible = true;
    }

    public String getModelo() {
        return modelo;
    }

    public String getMarca() {
        return marca;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void alquilar() {
        this.disponible = false;
    }

    public void devolver() {
        this.disponible = true;
    }
}

class Usuario {
    private String nombre;
    private String contraseña;

    public Usuario(String nombre, String contraseña) {
        this.nombre = nombre;
        this.contraseña = contraseña;
    }

    public String getNombre() {
        return nombre;
    }

    public String getContraseña() {
        return contraseña;
    }
}

public class InterfazAlquilerCoches extends JFrame {
    private JButton btnIniciarCliente;
    private JButton btnIniciarTrabajador;
    private JButton btnRegistrarse;
    private JButton btnSalir;

    private List<Coche> coches = new ArrayList<>();
    private List<Usuario> clientes = new ArrayList<>();
    private List<Usuario> trabajadores = new ArrayList<>();
    private static final String URL = "jdbc:mysql://localhost:3306/auto_denda";
    private static final String USUARIO_BD = "root";
    private static final String CONTRASEÑA_BD = "1WMG2023";

    private Usuario usuarioActual;

    public InterfazAlquilerCoches() {
        setTitle("Alquiler de Coches");
        setSize(904, 539);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        btnIniciarCliente = new JButton("Iniciar sesión como cliente");
        btnIniciarCliente.addActionListener(e -> iniciarSesionCliente());

        });
        panel.add(btnIniciarCliente);

        btnIniciarTrabajador = new JButton("Iniciar sesión como trabajador");
        btnIniciarTrabajador.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                iniciarSesionTrabajador();
            }
        });
        panel.add(btnIniciarTrabajador);

        btnRegistrarse = new JButton("Registrarse como cliente");
        btnRegistrarse.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarCliente();
            }
        });
        panel.add(btnRegistrarse);

        btnSalir = new JButton("Salir");
        btnSalir.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(btnSalir);

        getContentPane().add(panel);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void iniciarSesionCliente() {
        String nombreUsuario = JOptionPane.showInputDialog("Ingrese nombre de usuario:");
        String contraseña = JOptionPane.showInputDialog("Ingrese contraseña:");

        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            String selectSQL = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contraseña = ?";
            PreparedStatement preparedStatement = conexion.prepareStatement(selectSQL);
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, contraseña);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuarioActual = new Usuario(nombreUsuario, contraseña);
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso como cliente.");
                mostrarMenuCliente();
            } else {
                JOptionPane.showMessageDialog(this, "Nombre de usuario o contraseña incorrectos.\nPor favor, regístrese como cliente.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al iniciar sesión: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void mostrarMenuCliente() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JButton btnVerCochesDisponibles = new JButton("Ver coches disponibles");
        btnVerCochesDisponibles.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verCochesDisponibles();
            }
        });
        panel.add(btnVerCochesDisponibles);

        JButton btnVerReservas = new JButton("Ver reservas");
        btnVerReservas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                verReservasCliente();
            }
        });
        panel.add(btnVerReservas);

        JButton btnVolver = new JButton("Volver al menú principal");
        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getContentPane().removeAll();
                getContentPane().add(new InterfazAlquilerCoches().getContentPane());
                revalidate();
                repaint();
            }
        });
        panel.add(btnVolver);

        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    private void verCochesDisponibles() {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM stock");

            StringBuilder sb = new StringBuilder();
            sb.append("Modelo\tMarca\tDisponible\n");
            while (resultSet.next()) {
                String modelo = resultSet.getString("Modeloa");
                String marca = resultSet.getString("Marca");
                boolean disponible = resultSet.getBoolean("disponible");
                sb.append(modelo).append("\t").append(marca).append("\t").append(disponible ? "Sí" : "No").append("\n");
            }

            JOptionPane.showMessageDialog(this, sb.toString(), "Coches Disponibles", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener coches disponibles: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void realizarReserva(Coche coche) {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            String insertSQL = "INSERT INTO reserva (marca_coche, modelo_coche, id_reserva) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = conexion.prepareStatement(insertSQL);
            preparedStatement.setString(3, usuarioActual.getNombre());
            preparedStatement.setString(2, coche.getModelo());
            preparedStatement.setString(1, coche.getMarca());
            int filasInsertadas = preparedStatement.executeUpdate();

            if (filasInsertadas > 0) {
                JOptionPane.showMessageDialog(this, "Reserva realizada con éxito para el coche: " + coche.getModelo() + " " + coche.getMarca());
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo realizar la reserva.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al realizar la reserva: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void verReservasCliente() {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            String selectSQL = "SELECT modelo_coche, marca_coche FROM reserva WHERE cliente = ? ORDER BY id_reserva DESC";
            PreparedStatement preparedStatement = conexion.prepareStatement(selectSQL);
            preparedStatement.setString(1, usuarioActual.getNombre());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No tienes reservas realizadas.", "Reservas", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Tus reservas:\n");
                while (resultSet.next()) {
                    String modeloCoche = resultSet.getString("modelo_coche");
                    String marcaCoche = resultSet.getString("marca_coche");
                    sb.append(modeloCoche).append(" de marca ").append(marcaCoche).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Reservas", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener reservas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void iniciarSesionTrabajador() {
        String nombreUsuario = JOptionPane.showInputDialog("Ingrese nombre de usuario:");
        String contraseña = JOptionPane.showInputDialog("Ingrese contraseña:");

        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            String selectSQL = "SELECT * FROM trabajadores WHERE nombre_trabajador = ? AND contraseña = ?";
            PreparedStatement preparedStatement = conexion.prepareStatement(selectSQL);
            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, contraseña);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                usuarioActual = new Usuario(nombreUsuario, contraseña);
                JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso como trabajador.");
                mostrarMenuTrabajador();
            } else {
                JOptionPane.showMessageDialog(this, "Nombre de usuario o contraseña incorrectos.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al iniciar sesión como trabajador: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarMenuTrabajador() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        JButton btnMostrarTablas = new JButton("Mostrar contenido de tablas");
        btnMostrarTablas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarContenidoTablas();
            }
        });
        panel.add(btnMostrarTablas);

        JButton btnEditarTablas = new JButton("Editar tablas");
        btnEditarTablas.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editarTabla();
            }
        });
        panel.add(btnEditarTablas);

        JButton btnVolver = new JButton("Volver al menú principal");
        btnVolver.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                getContentPane().removeAll();
                getContentPane().add(new InterfazAlquilerCoches().getContentPane());
                revalidate();
                repaint();
            }
        });
        panel.add(btnVolver);

        getContentPane().removeAll();
        getContentPane().add(panel);
        revalidate();
        repaint();
    }

    private void mostrarContenidoTablas() {
        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            // Especificar el nombre de la base de datos en la consulta
            String query = "SHOW TABLES FROM auto_denda";
            Statement statement = conexion.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            StringBuilder sb = new StringBuilder();
            sb.append("Tablas disponibles en auto_denda:\n");
            while (resultSet.next()) {
                // La consulta "SHOW TABLES" devuelve el nombre de la tabla en la primera columna
                sb.append(resultSet.getString(1)).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Tablas", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener las tablas: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void editarTabla() {
        JOptionPane.showMessageDialog(this, "Funcionalidad de edición de tablas aún no implementada.", "Editar Tablas", JOptionPane.INFORMATION_MESSAGE);
        // Aquí se puede agregar la lógica para editar las tablas
    }

    private void registrarCliente() {
        String nombreUsuario = JOptionPane.showInputDialog("Ingrese nombre de usuario:");
        String contraseña = JOptionPane.showInputDialog("Ingrese contraseña:");

        clientes.add(new Usuario(nombreUsuario, contraseña));

        try (Connection conexion = DriverManager.getConnection(URL, USUARIO_BD, CONTRASEÑA_BD)) {
            String insertTableSQL = "INSERT INTO usuarios (nombre_usuario, contraseña) VALUES (?,?)";
            PreparedStatement preparedStatement = conexion.prepareStatement(insertTableSQL);

            preparedStatement.setString(1, nombreUsuario);
            preparedStatement.setString(2, contraseña);

            preparedStatement.executeUpdate();

            JOptionPane.showMessageDialog(this, "Registro exitoso como cliente.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar el cliente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfazAlquilerCoches();
            }
        });
    }
}


