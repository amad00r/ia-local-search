package redsensores;


import aima.search.framework.HeuristicFunction;

import redsensores.RedSensoresEstado;

public class RedSensoresHeuristicFunction implements HeuristicFunction {

    private int choice = 1;
    private double alpha = 1;
    private double beta = 1;

    public void changeHeuristicFunction(int newChoice) {
        choice = newChoice;
    }

    public void changeAlpha (double newAlpha) {
        alpha = newAlpha;
    }

    public void changeBeta (double newBeta) {
        beta = newBeta;
    }

    public double getHeuristicValue(Object state) throws IllegalStateException {
        RedSensoresEstado redSensores = (RedSensoresEstado) state;
        RedSensoresEstado.Evaluation eval = redSensores.evaluateSolution();
        
        //Choice 1: Throughput
        //Choice 2: Cost
        //Choice 3: - Cost + Throughput (Maximize throughput and minimize cost) 
        //          - Buscamos valores cercanos a cero o positivos
        //Choice 4: - Alpha * Cost + Beta * Throughput (Maximize throughput and minimize cost) 
        //          - Buscamos valores cercanos a cero o positivos, ponderamos los valores
        switch (choice) {
            case 1:
                return eval.throughput();
            case 2:
                return eval.cost();
            case 3:
                return (- eval.cost() + eval.throughput());
            case 4:
                return (- alpha * eval.cost() + beta * eval.throughput());
            default:
                throw new IllegalStateException("Usad un numero entre 1 y 4");
        }   
    }
}
