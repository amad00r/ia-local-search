package redsensores;


import aima.search.framework.HeuristicFunction;

import redsensores.RedSensoresEstado;

public class RedSensoresHeuristicFunction implements HeuristicFunction {    
    private double alpha;
    private int choice = 5;

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

    public void setChoice(int choice) {
        this.choice = choice;
    }

    public double getHeuristicValue(Object state) throws IllegalStateException {
        RedSensoresEstado redSensores = (RedSensoresEstado) state;
        RedSensoresEstado.Evaluation eval = redSensores.evaluateSolution();
        
        switch (choice) {
            case 1:
                return - eval.throughput(); //Mejor solucion valor pequeño.
            case 2:
                return eval.cost(); //Mejor solucion valor grande.
            case 3:
                return eval.cost() - eval.throughput(); //Mejor solucion valor cercano a cero.
            case 4:
                return alpha * eval.cost() - (1 - alpha) * eval.throughput(); //Mejor solucion valor cercano a cero.
            case 5:
                return alpha * eval.cost() - (1 - alpha) * eval.throughput() * 1000;
            default:
                throw new IllegalStateException("Usad un numero entre 1 y 4");
        }   
    }

    // public double getHeuristicValue(Object state) throws IllegalStateException {
    //     RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)state).evaluateSolution();
    //     return alpha * eval.cost() - (1 - alpha) * eval.throughput() * 1000;
    // }
}
