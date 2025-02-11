package segundapruebapracticaut3server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SegundaPruebaPracticaUT3Server {
    
    public static void main(String[] args) {
        // Puerto del servidor
        int serverPort = 5000;
        int numeroCliente = 1;
        
        try {
            // Iniciar el servidor
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Servidor de autenticación iniciado en el puerto " + serverPort + ".");
            
            // Esperar a que se conecten los jugadores.
            while (true) {
                // Aceptar una conexion
                Socket clientSocket = serverSocket.accept();
                System.out.printf("Cliente conectado desde la IP %s y el puerto %s. \n",
                        clientSocket.getInetAddress(), clientSocket.getPort());
                
                // Crear y lanzar hilo para antender la peticion
                ServerThread serverThread = new ServerThread(clientSocket, numeroCliente);
                new Thread(serverThread).start();
                numeroCliente += 1;
            }
            
        } catch (IOException e) {
            
        }
    }
    
    // Clase hilo servidor
    static class ServerThread implements Runnable {
        // Propiedades
        private Socket socket;
        private BufferedReader input;
        private PrintWriter output;
        private int numeroCliente;
        
        int intentos = 1;
        String mensaje = "";
        
        // Constructor
        ServerThread(Socket socket, int numeroCliente) {
            this.socket = socket;
            this.numeroCliente = numeroCliente;
        }
        // Metodo run()
        @Override
        public void run() {
            try {
                // Capturar los streams de entrada salida
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                // Mensajes de bienvenida
                output.println("Está conectado al servidor de autenticacion como cliente nº " + this.numeroCliente);
                output.println("Se permiten 3 errores como máximo");

                // Manejo de los mensajes del cliente.
                String username, password;
                while (true) {
                    output.println("Intento " + Integer.toString(intentos) + " de 3");
                    mensaje += "Intento " + Integer.toString(intentos) + " de 3 del cliente " + this.numeroCliente + ":\n";
                    // Solicitar usuario y contraseña al cliente
                    output.println("Ingrese usuario:");
                    username = input.readLine();
                    output.println("Ingrese contraseña:");
                    password = input.readLine();
                    
                    mensaje += "(" + username + ", " + password + ") --> ";
                    
                    
                    // Verificar el usuario y contraseña
                    if (intentos <= 3) {
                        if ("user1".equals(username) && "pas1".equals(password) || "user2".equals(username) && "pas2".equals(password) || "user3".equals(username) && "pas3".equals(password)) {
                            output.println("Acceso permitido");
                            output.println("Conexion finalizada. Adiós");
                            mensaje += "Acceso permitido";
                            socket.close();
                        } else {
                            output.println("Acceso denegado");
                            mensaje += "Acceso denegado";
                            intentos += 1;
                        }
                    } else {
                        output.println("Debe esperar 30 segundos para volver a intentarlo.");
                        mensaje += "Acceso denegado. Debe esperar 30 segundos para volver a intentarlo.";
                        intentos = 0;
                        try {
                            Thread.sleep(5*1000);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(SegundaPruebaPracticaUT3Server.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    System.out.println(mensaje);
                    mensaje = "";
                }
            } catch (IOException e) {
                
            } finally {
                try {
                    socket.close();
                } catch(IOException e) {
                    
                }
            }
        }
    }
    
}
