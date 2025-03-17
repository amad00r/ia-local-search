package redsensores;

import redsensores.RedSensoresEstado;
import redsensores.RedSensoresEstado.SensorInfo;
import redsensores.RedSensoresEstado.CentroInfo;
import redsensores.RedSensoresSuccessorFunction;
import java.util.Scanner;

import aima.search.framework.Successor;
import aima.search.framework.Problem;
import aima.search.framework.Search;
import aima.search.framework.SearchAgent;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;


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

        System.out.println("Ingrese el modo de generación: ");
        System.out.println("    1. Solucion mala");
        System.out.println("    2. Solucion buena");
        int mode = scanner.nextInt();

        System.out.println("Ingrese el tipo de algortimo: ");
        System.out.println("    1. Hill Climbing");
        System.out.println("    2. Simulated Annealing");
        int alg = scanner.nextInt();

        scanner.close();

        RedSensoresEstado redSensores = new RedSensoresEstado(numSensores, numCentros, semillaSensor, semillaCentros, mode);
        System.out.println("Estado inicial:");
        System.out.println(redSensores);

        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        for (Successor successor : successorFn.getSuccessors(redSensores)) {
            System.out.println("Sucessor `" + successor.getAction() + "`");
            System.out.println((RedSensoresEstado)successor.getState());
        }

        Search hillClimbing = new HillClimbingSearch();

        //steps: Cuánto tiempo se ejecuta el algoritmo.
        //stiter: Cuántas iteraciones se hacen por nivel de temperatura.
        //k: Controla la probabilidad de aceptar soluciones peores.
        //lamb: Controla la velocidad de enfriamiento.
        //TODO: Ver como afectan los parametros
        Search simulatedAnnealing = new SimulatedAnnealingSearch(2000, 100, 5, 0.001); 
        Problem p = new Problem (redSensores, new RedSensoresSuccessorFunction(), new RedSensoresGoalTest(), new RedSensoresHeuristicFunction());

        SearchAgent agent = null;
        try {
            if (alg == 1) agent = new SearchAgent(p, hillClimbing);
            else if (alg == 2)  agent = new SearchAgent(p, simulatedAnnealing);
        } catch (Exception e) {
            e.printStackTrace();
        }

        RedSensoresEstado finalState = null;

        if (alg == 1) finalState = (RedSensoresEstado) hillClimbing.getGoalState();
        else if (alg == 2) finalState = (RedSensoresEstado) simulatedAnnealing.getGoalState();

        // TODO: Print the final state <- Implementar
        //finalState.printSolution();

    }
}
