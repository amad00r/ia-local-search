package redsensores;

import redsensores.RedSensoresEstado;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el número de sensores: ");
        int numSensores = scanner.nextInt();

        System.out.print("Ingrese el número de centros de datos: ");
        int numCentros = scanner.nextInt();

        System.out.print("Ingrese la semilla para los sensores: ");
        int semillaSensor = scanner.nextInt();

        System.out.print("Ingrese la semilla para los centros: ");
        int semillaCentros = scanner.nextInt();

        System.out.print("Ingrese el modo de generación: ");
        int mode = scanner.nextInt();

        scanner.close();

        RedSensoresEstado redSensores = new RedSensoresEstado(numSensores, numCentros, semillaSensor, semillaCentros, mode);
        
        System.out.println("Coordenadas de los sensores:");
        for (int i = 0; i < numSensores; i++) {
            System.out.println("Sensor " + i + ": (" + redSensores.sensorGetCoordX(i) + ", " + redSensores.sensorGetCoordY(i) + ") -> Capacidad: " + redSensores.sensorGetCapacidad(i) + " --> Conexiones restantes: " + redSensores.sensorGetConexionesRestantes(i) + " --> Capacidad restante: " + redSensores.sensorGetCapacidadRestante(i));     
        }
        
        System.out.println("Coordenadas de los centros de datos:");
        for (int i = 0; i < numCentros; i++) {
            System.out.println("Centro de datos " + i + ": (" + redSensores.centroGetCoordX(i) + ", " + redSensores.centroGetCoordY(i) + ")" + " --> Capacidad restante: " + redSensores.centroGetCapacidadRestante(i) + " --> Conexiones restantes: " + redSensores.centroGetConexionesRestantes(i));
        }
    }
}
