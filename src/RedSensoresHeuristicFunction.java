package redsensores;


import aima.search.framework.HeuristicFunction;

import redsensores.RedSensoresEstado;

public class RedSensoresHeuristicFunction implements HeuristicFunction {    
    private double alpha;

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }

    public void setMaximizeThroughputHeuristic() {
        this.alpha = 0.0;
    }

    public void setMinimizeCostHeuristic() {
        this.alpha = 1.0;
    }

    public void setEqualWeightsHeuristic() {
        this.alpha = 0.5;
    }

    // public double getHeuristicValue(Object state) throws IllegalStateException {
    //     RedSensoresEstado redSensores = (RedSensoresEstado) state;
    //     RedSensoresEstado.Evaluation eval = redSensores.evaluateSolution();
        
    //     //Choice 1: Throughput
    //     //Choice 2: Cost
    //     //Choice 3: - Cost + Throughput (Maximize throughput and minimize cost) 
    //     //          - Buscamos valores cercanos a cero o positivos
    //     //Choice 4: - Alpha * Cost + Beta * Throughput (Maximize throughput and minimize cost) 
    //     //          - Buscamos valores cercanos a cero o positivos, ponderamos los valores

    //     //assumption greater heuristic value => HIGHER on hill; 0 == goal state;
    //     //HillClimbing invierte el signo de la heurística, necesitamos generar el caso contrario.
    //     //Mismo para SimulatedAnnealing.
    //     switch (choice) {
    //         case 1:
    //             return - eval.throughput(); //Mejor solucion valor pequeño.
    //         case 2:
    //             return eval.cost(); //Mejor solucion valor grande.
    //         case 3:
    //             return eval.cost() - eval.throughput(); //Mejor solucion valor cercano a cero.
    //         case 4:
    //             return alpha * eval.cost() - beta * eval.throughput(); //Mejor solucion valor cercano a cero.
    //         case 5:
    //             return alpha2 * eval.cost() - beta2 * eval.throughput();
    //         default:
    //             throw new IllegalStateException("Usad un numero entre 1 y 4");
    //     }   
    // }
    public double getHeuristicValue(Object state) throws IllegalStateException {
        RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)state).evaluateSolution();
        return alpha * eval.cost() - (1 - alpha) * eval.throughput() * 1000;
    }
}
