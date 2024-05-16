package alquilercochesapp;

import java.util.Scanner;

public class Main {
	
    public static void main(String[] args) {
        // Crear una instancia de la clase que contiene el punto de entrada de la aplicación
        AlquilerCochesApp app = new AlquilerCochesApp();
        // Iniciar la aplicación
        app.iniciar();
    }

    // Método para iniciar la aplicación
    public void iniciar() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Bienvenido a la aplicación de alquiler de coches.");
            System.out.println("1. Iniciar sesión como trabajador");
            System.out.println("2. Iniciar sesión como cliente");
            System.out.println("3. Registrarse");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar el buffer

            switch (opcion) {
                case 1:
                    // Aquí llamas al método para iniciar sesión como trabajador
                    iniciarSesionTrabajador(scanner);
                    break;
                case 2:
                    // Aquí llamas al método para iniciar sesión como cliente
                    iniciarSesionCliente(scanner);
                    break;
                case 3:
                    // Aquí llamas al método para registrar un nuevo usuario
                    registrarUsuario(scanner);
                    break;
                case 4:
                    System.out.println("Gracias por usar nuestra aplicación. ¡Hasta luego!");
                    return; // Salir del bucle y terminar la aplicación
                default:
                    System.out.println("Opción no válida. Por favor, seleccione una opción válida.");
            }
        }
    }

    // Método para iniciar sesión como trabajador
    private void iniciarSesionTrabajador(Scanner scanner) {
        // Aquí iría la lógica para iniciar sesión como trabajador
        System.out.println("Iniciar sesión como trabajador (En construcción)");
    }

    // Método para iniciar sesión como cliente
    private void iniciarSesionCliente(Scanner scanner) {
        // Aquí iría la lógica para iniciar sesión como cliente
        System.out.println("Iniciar sesión como cliente (En construcción)");
    }

    // Método para registrar un nuevo usuario
    private void registrarUsuario(Scanner scanner) {
        // Aquí iría la lógica para registrar un nuevo usuario
        System.out.println("Registrarse como nuevo usuario (En construcción)");
    }
}



