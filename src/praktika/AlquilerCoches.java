package praktika;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AlquilerCoches {
	public class AlquilerCochesApp {
	    private List<coche> coches = new ArrayList<>();
	    private List<Usuario> clientes = new ArrayList<>();
	    private List<Usuario> trabajadores = new ArrayList<>();
	    private static final String URL = "jdbc:mysql://localhost:3306/auto_denda";
	    private static final String USUARIO = "root";
	    private static final String CONTRASEÑA = "1WMG2023";

	    public void iniciar() {
	        cargarDatosDesdeBaseDeDatos();

	        Scanner scanner = new Scanner(System.in);

	        while (true) {
	            System.out.println("Bienvenido a la aplicación de alquiler de coches.");
	            System.out.println("1. Iniciar sesión como cliente");
	            System.out.println("2. Iniciar sesión como trabajador");
	            System.out.println("3. Registrarse como cliente");
	            System.out.println("4. Salir");
	            System.out.print("Seleccione una opción: ");
	            int opcion = scanner.nextInt();
	            scanner.nextLine();

	            switch (opcion) {
	                case 1:
	                    iniciarSesionCliente(scanner);
	                    break;
	                case 2:
	                    iniciarSesionTrabajador(scanner);
	                    break;
	                case 3:
	                    registrarCliente(scanner);
	                    break;
	                case 4:
	                    System.out.println("Gracias por usar nuestra aplicación. ¡Hasta luego!");
	                    return;
	                default:
	                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
	            }
	        }
	    }

	    private void cargarDatosDesdeBaseDeDatos() {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            cargarCochesDesdeBaseDeDatos(conexion);
	            cargarClientesDesdeBaseDeDatos(conexion);
	            cargarTrabajadoresDesdeBaseDeDatos(conexion);
	        } catch (SQLException e) {
	            System.out.println("Error al cargar los datos desde la base de datos");
	            e.printStackTrace();
	        }
	    }

	    private void cargarCochesDesdeBaseDeDatos(Connection conexion) throws SQLException {
	        Statement statement = conexion.createStatement();
	        ResultSet resultSet = statement.executeQuery("SELECT Modeloa, Marca FROM stock");

	        while (resultSet.next()) {
	            String modelo = resultSet.getString("Modeloa");
	            String marca = resultSet.getString("Marca");
	            coches.add(new coche(modelo, marca));
	        }
	    }

	    private void cargarClientesDesdeBaseDeDatos(Connection conexion) throws SQLException {
	        Statement statement = conexion.createStatement();
	        ResultSet resultSet = statement.executeQuery("SELECT nombre_usuario, contraseña FROM usuarios");

	        while (resultSet.next()) {
	            String nombreUsuario = resultSet.getString("nombre_usuario");
	            String contraseña = resultSet.getString("contraseña");
	            clientes.add(new Usuario(nombreUsuario, contraseña));
	        }
	    }

	    private void cargarTrabajadoresDesdeBaseDeDatos(Connection conexion) throws SQLException {
	        Statement statement = conexion.createStatement();
	        ResultSet resultSet = statement.executeQuery("SELECT nombre_trabajador, contraseña FROM trabajadores");

	        while (resultSet.next()) {
	            String nombreUsuario = resultSet.getString("nombre_trabajador");
	            String contraseña = resultSet.getString("contraseña");
	            trabajadores.add(new Usuario(nombreUsuario, contraseña));
	        }
	    }

	    private void iniciarSesionCliente(Scanner scanner) {
	        System.out.println("Iniciar sesión como cliente:");
	        System.out.print("Nombre de usuario: ");
	        String nombreUsuario = scanner.nextLine();
	        System.out.print("Contraseña: ");
	        String contraseña = scanner.nextLine();

	        for (Usuario cliente : clientes) {
	            if (cliente.getNombre().equals(nombreUsuario) && cliente.getContraseña().equals(contraseña)) {
	                System.out.println("Inicio de sesión exitoso como cliente.");

	                // Mostrar opciones para el cliente
	                while (true) {
	                    System.out.println("¿Qué desea hacer?");
	                    System.out.println("1. Ver reservas");
	                    System.out.println("2. Ver listado de coches disponibles");
	                    System.out.println("3. Volver al menú principal");
	                    System.out.print("Seleccione una opción: ");
	                    int opcion = scanner.nextInt();
	                    int aux = -1;
	                    scanner.nextLine();

	                    switch (opcion) {
	                        case 1:
	                        	//System.out.println(nombreUsuario);
	                            verReservasCliente(nombreUsuario);
	                            break;
	                        case 2:
	                            aux = mostrarCochesDisponibles(nombreUsuario);
	                            break;
	                        case 3:
	                            return; // Volver al menú principal
	                        default:
	                            System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
	                    }
	                }
	            }
	        }
	        System.out.println("Nombre de usuario o contraseña incorrectos. Por favor, inténtelo nuevamente.");
	    }

	    private int mostrarCochesDisponibles(String nombreUsuario) {
	    	int opcion = 0;
	        System.out.println("Listado de coches disponibles para alquilar:");

	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            Statement statement = conexion.createStatement();
	            ResultSet resultSet = statement.executeQuery("SELECT Modeloa, Marca FROM stock");

	            List<coche> cochesDisponibles = new ArrayList<>();

	            while (resultSet.next()) {
	                String modelo = resultSet.getString("Modeloa");
	                String marca = resultSet.getString("Marca");
	                coche coche = new coche(modelo, marca);
	                cochesDisponibles.add(coche);
	                System.out.println(cochesDisponibles.size() + ". Modeloa: " + modelo + ", Marca: " + marca);
	            }

	            if (cochesDisponibles.isEmpty()) {
	                System.out.println("No hay coches disponibles en este momento.");
	            }

	            Scanner scanner = new Scanner(System.in);
	            System.out.print("Seleccione el número de coche que desea reservar (0 para cancelar): ");
	            opcion = scanner.nextInt();
	            scanner.nextLine();

	            if (opcion >= 1 && opcion <= cochesDisponibles.size()) {
	                coche cocheSeleccionado = cochesDisponibles.get(opcion - 1);
	                realizarReserva(cocheSeleccionado, nombreUsuario);
	            } else if (opcion != 0) {
	                System.out.println("Opción no válida. Por favor, seleccione un número de coche válido.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al cargar los coches disponibles desde la base de datos");
	            e.printStackTrace();
	        }
	        return opcion;
	    }

	    private void realizarReserva(coche coche, String nombreUsuario) {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            String insertSQL = "INSERT INTO reserva (marca_coche, modelo_coche, cliente) VALUES (?, ?, ?)";
	            PreparedStatement preparedStatement = conexion.prepareStatement(insertSQL);
	            preparedStatement.setString(3, nombreUsuario);
	            preparedStatement.setString(2, coche.getModelo());
	            preparedStatement.setString(1, coche.getMarca());
	            int filasInsertadas = preparedStatement.executeUpdate();
	            
	            if (filasInsertadas > 0) {
	                System.out.println("Reserva realizada con éxito para el coche: " + coche.getModelo() + " " + coche.getMarca());
	                // Actualizar la disponibilidad del coche en la tabla de la base de datos
	                String updateTableSQL = "UPDATE stock SET disponible = false WHERE Modeloa = ? AND Marca = ?";
	                preparedStatement = conexion.prepareStatement(updateTableSQL);
	                preparedStatement.setString(1, coche.getModelo());
	                preparedStatement.setString(2, coche.getMarca());
	                preparedStatement.executeUpdate();
	            } else {
	                System.out.println("No se pudo realizar la reserva.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al realizar la reserva en la base de datos");
	            e.printStackTrace();
	        }
	    }

	   


	    private void mostarReserva(coche coche, String nombreUsuario) {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            String insertTableSQL = "INSERT INTO auto_denda.reserva ( modelo_coche, marca_coche, id_reserva) VALUES (?, ?, ?)";
	            PreparedStatement preparedStatement = conexion.prepareStatement(insertTableSQL);

	            preparedStatement.setString(1, coche.getModelo());
	            preparedStatement.setString(2, coche.getMarca());
	            preparedStatement.setString(3, nombreUsuario);

	            int filasInsertadas = preparedStatement.executeUpdate();

	            if (filasInsertadas > 0) {
	                System.out.println("Reserva realizada con éxito.");
	                // Actualizar la disponibilidad del coche en la tabla de la base de datos
	                String updateTableSQL = "UPDATE auto_denda.stock SET disponible = false WHERE Modeloa = ? AND Marca = ?";
	                preparedStatement = conexion.prepareStatement(updateTableSQL);
	                preparedStatement.setString(1, coche.getModelo());
	                preparedStatement.setString(2, coche.getMarca());
	                preparedStatement.executeUpdate();
	            } else {
	                System.out.println("No se pudo realizar la reserva.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al realizar la reserva");
	            e.printStackTrace();
	        }
	    }


	    private void verReservasCliente(String nombreUsuario) {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            String selectSQL = "SELECT modelo_coche, marca_coche FROM reserva WHERE cliente = ? ORDER BY id_reserva DESC";
	            PreparedStatement preparedStatement = conexion.prepareStatement(selectSQL);
	            preparedStatement.setString(1, nombreUsuario);

	            ResultSet resultSet = preparedStatement.executeQuery();

	            if (!resultSet.isBeforeFirst()) {
	                System.out.println("No tienes reservas realizadas.");
	            } else {
	                while (resultSet.next()) {
	                    String modeloCoche = resultSet.getString("modelo_coche");
	                    String marcaCoche = resultSet.getString("marca_coche");
	                    System.out.println("El coche " + modeloCoche + " de marca " + marcaCoche + " está reservado por " + nombreUsuario);
	                }
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al obtener las reservas del cliente");
	            e.printStackTrace();
	        }
	    }





	    private void iniciarSesionTrabajador(Scanner scanner) {
	        System.out.println("Iniciar sesión como trabajador:");
	        System.out.print("Nombre de usuario: ");
	        String nombreUsuario = scanner.nextLine();
	        System.out.print("Contraseña: ");
	        String contraseña = scanner.nextLine();

	        for (Usuario trabajador : trabajadores) {
	            if (trabajador.getNombre().equals(nombreUsuario) && trabajador.getContraseña().equals(contraseña)) {
	                System.out.println("Inicio de sesión exitoso como trabajador.");

	                // Mostrar opciones para el trabajador
	                while (true) {
	                    System.out.println("¿Qué desea hacer?");
	                    System.out.println("1. Ver todas las tablas");
	                    System.out.println("2. Modificar tablas");
	                    System.out.println("3. Volver al menú principal");
	                    System.out.print("Seleccione una opción: ");
	                    int opcion = scanner.nextInt();
	                    scanner.nextLine();

	                    switch (opcion) {
	                        case 1:
	                            mostrarContenidoTablas();
	                            break;
	                        case 2:
	                            // Lógica para modificar tablas
	                            break;
	                        case 3:
	                            return; // Volver al menú principal
	                        default:
	                            System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
	                    }
	                }
	            }
	        }
	        System.out.println("Nombre de usuario o contraseña incorrectos. Por favor, inténtelo nuevamente.");
	    }

	    
	    
	    private void mostrarContenidoTablas() {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            // Lista de nombres de las tablas
	            String[] nombresTablas = {"stock", "usuarios", "trabajadores", "reserva", "langileak"};

	            System.out.println("Contenido de las tablas en la base de datos 'auto_denda':");

	            // Recorrer todas las tablas
	            for (String nombreTabla : nombresTablas) {
	                System.out.println("Tabla: " + nombreTabla);

	                // Consultar el contenido de la tabla
	                Statement statement = conexion.createStatement();
	                ResultSet tablaResultSet = statement.executeQuery("SELECT * FROM " + nombreTabla);

	                // Imprimir el contenido de la tabla
	                while (tablaResultSet.next()) {
	                    // Obtener metadatos de la tabla
	                    ResultSetMetaData tablaMetaData = tablaResultSet.getMetaData();
	                    int columnCount = tablaMetaData.getColumnCount();

	                    // Imprimir cada fila de la tabla
	                    for (int i = 1; i <= columnCount; i++) {
	                        String columnName = tablaMetaData.getColumnName(i);
	                        String columnValue = tablaResultSet.getString(i);
	                        System.out.println(columnName + ": " + columnValue);
	                    }
	                    System.out.println();
	                }
	                tablaResultSet.close();
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al obtener el contenido de las tablas de la base de datos");
	            e.printStackTrace();
	        }
	    }
	    
	    private void modificarTabla(Scanner scanner) {
	        System.out.println("Seleccione la tabla que desea modificar:");
	        mostrarTodasLasTablas();
	        System.out.print("Ingrese el nombre de la tabla: ");
	        String nombreTabla = scanner.nextLine();

	        switch (nombreTabla) {
	            case "stock":
	                editarTablaStock();
	                break;
	            case "reserva":
	                editarTablaReserva();
	                break;
	            case "usuarios":
	                editarTablaUsuarios();
	                break;
	            case "trabajadores":
	                editarTablaTrabajadores();
	                break;
	            case "langileak":
	                editarTablaLangileak();
	                break;
	            
	            default:
	                System.out.println("Tabla no válida. Por favor, seleccione una tabla existente.");
	        }
	    }


		private void mostrarTodasLasTablas() {
			// TODO Auto-generated method stub
			
		}

		private void editarTablaStock() {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            Scanner scanner = new Scanner(System.in);
	            System.out.println("Ingrese el ID del coche que desea editar:");
	            int id = scanner.nextInt();
	            scanner.nextLine(); // Consumir la nueva línea

	            System.out.println("Ingrese el nuevo modelo:");
	            String nuevoModelo = scanner.nextLine();

	            System.out.println("Ingrese la nueva marca:");
	            String nuevaMarca = scanner.nextLine();

	            String updateSQL = "UPDATE stock SET Modeloa = ?, Marca = ? WHERE id = ?";
	            PreparedStatement preparedStatement = conexion.prepareStatement(updateSQL);
	            preparedStatement.setString(1, nuevoModelo);
	            preparedStatement.setString(2, nuevaMarca);
	            preparedStatement.setInt(3, id);

	            int filasActualizadas = preparedStatement.executeUpdate();

	            if (filasActualizadas > 0) {
	                System.out.println("Tabla \"stock\" actualizada correctamente.");
	            } else {
	                System.out.println("No se pudo actualizar la tabla \"stock\".");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al editar la tabla \"stock\".");
	            e.printStackTrace();
	        }
	    }

	    private void editarTablaUsuarios() {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            Scanner scanner = new Scanner(System.in);
	            System.out.println("Ingrese el nombre de usuario que desea editar:");
	            String nombreUsuario = scanner.nextLine();

	            System.out.println("Ingrese la nueva contraseña:");
	            String nuevaContraseña = scanner.nextLine();

	            String updateSQL = "UPDATE usuarios SET contraseña = ? WHERE nombre_usuario = ?";
	            PreparedStatement preparedStatement = conexion.prepareStatement(updateSQL);
	            preparedStatement.setString(1, nuevaContraseña);
	            preparedStatement.setString(2, nombreUsuario);

	            int filasActualizadas = preparedStatement.executeUpdate();

	            if (filasActualizadas > 0) {
	                System.out.println("Tabla \"usuarios\" actualizada correctamente.");
	            } else {
	                System.out.println("No se pudo actualizar la tabla \"usuarios\".");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al editar la tabla \"usuarios\".");
	            e.printStackTrace();
	        }
	    }

	    private void editarTablaTrabajadores() {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            Scanner scanner = new Scanner(System.in);
	            System.out.println("Ingrese el nombre del trabajador que desea editar:");
	            String nombreTrabajador = scanner.nextLine();

	            System.out.println("Ingrese la nueva contraseña:");
	            String nuevaContraseña = scanner.nextLine();

	            String updateSQL = "UPDATE trabajadores SET contraseña = ? WHERE nombre_trabajador = ?";
	            PreparedStatement preparedStatement = conexion.prepareStatement(updateSQL);
	            preparedStatement.setString(1, nuevaContraseña);
	            preparedStatement.setString(2, nombreTrabajador);

	            int filasActualizadas = preparedStatement.executeUpdate();

	            if (filasActualizadas > 0) {
	                System.out.println("Tabla \"trabajadores\" actualizada correctamente.");
	            } else {
	                System.out.println("No se pudo actualizar la tabla \"trabajadores\".");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al editar la tabla \"trabajadores\".");
	            e.printStackTrace();
	        }
	    }
	    
	    private void editarTablaReserva() {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            Scanner scanner = new Scanner(System.in);
	            System.out.println("Ingrese el ID de la reserva que desea cancelar:");
	            int idReserva = scanner.nextInt();

	            String deleteSQL = "DELETE FROM reserva WHERE id_reserva = ?";
	            PreparedStatement preparedStatement = conexion.prepareStatement(deleteSQL);
	            preparedStatement.setInt(1, idReserva);

	            int filasEliminadas = preparedStatement.executeUpdate();

	            if (filasEliminadas > 0) {
	                System.out.println("Reserva cancelada correctamente.");
	            } else {
	                System.out.println("No se pudo cancelar la reserva.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al cancelar la reserva.");
	            e.printStackTrace();
	        }
	    }
	    
	    private void editarTablaLangileak() {
	        Scanner scanner = new Scanner(System.in);
	        
	        System.out.println("Ingrese el ID del registro que desea editar:");
	        int id = scanner.nextInt();
	        scanner.nextLine();  // Limpiar el buffer
	        
	        System.out.println("Ingrese el nuevo valor para el campo que desea editar:");
	        String nuevoValor = scanner.nextLine();
	        
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            // Construir la consulta SQL para actualizar el registro en la tabla langileak
	            String updateSQL = "UPDATE langileak SET campo = ? WHERE id = ?";
	            PreparedStatement preparedStatement = conexion.prepareStatement(updateSQL);
	            preparedStatement.setString(1, nuevoValor);
	            preparedStatement.setInt(2, id);
	            
	            // Ejecutar la consulta
	            int filasActualizadas = preparedStatement.executeUpdate();
	            
	            if (filasActualizadas > 0) {
	                System.out.println("Registro actualizado con éxito.");
	            } else {
	                System.out.println("No se pudo actualizar el registro. Verifique el ID proporcionado.");
	            }
	        } catch (SQLException e) {
	            System.out.println("Error al editar la tabla langileak");
	            e.printStackTrace();
	        }
	    }


	    private void registrarCliente(Scanner scanner) {
	        System.out.println("Registrarse como cliente:");
	        System.out.print("Nombre de usuario: ");
	        String nombreUsuario = scanner.nextLine();
	        System.out.print("Contraseña: ");
	        String contraseña = scanner.nextLine();

	        clientes.add(new Usuario(nombreUsuario, contraseña));
	        guardarClienteEnBaseDeDatos(nombreUsuario, contraseña);
	        System.out.println("Cliente registrado con éxito.");
	    }

	    private void guardarClienteEnBaseDeDatos(String nombreUsuario, String contraseña) {
	        try (Connection conexion = DriverManager.getConnection(URL, USUARIO, CONTRASEÑA)) {
	            String insertTableSQL = "INSERT INTO usuarios (nombre_usuario, contraseña) VALUES (?,?)";
	            PreparedStatement preparedStatement = conexion.prepareStatement(insertTableSQL);

	            preparedStatement.setString(1, nombreUsuario);
	            preparedStatement.setString(2, contraseña);

	            preparedStatement.executeUpdate();

	            System.out.println("Registro exitoso!");

	        } catch (SQLException e) {
	            System.out.println("Error al registrar el cliente en la base de datos");
	            e.printStackTrace();
	        }
	    }

	    public static void main(String[] args) {
	        AlquilerCochesApp app = new AlquilerCochesApp();
	        app.iniciar();
	    }
	}













}
