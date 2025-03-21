package redsensores;

import aima.search.framework.Search;
import aima.search.informed.HillClimbingSearch;
import aima.search.framework.Problem;
import aima.search.framework.SearchAgent;


public class ExperimentsMain {
    private static void usage() {
        System.err.println("Usage:make run-experiment ARGS=<num-experimento>");
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length != 1) usage();

        int numExperimento = Integer.parseInt(args[0]);
        switch (numExperimento) {
        case 1:
            try { experimento1(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 8:
            try { experimento8(); } catch (Exception e) { e.printStackTrace(); }
            break;
        default:
            throw new UnsupportedOperationException(String.format("el experimento `%d` no está implementado", numExperimento));
        }
    }


    private static void experimento1_rec(int solucionInicial, String[] operators, int idx) throws Exception {
        if (idx < operators.length) {
            experimento1_rec(solucionInicial, operators, idx + 1);
            String op = operators[idx];
            operators[idx] = null;
            experimento1_rec(solucionInicial, operators, idx + 1);
            operators[idx] = op;
            return;
        }

        String operatorsNames = "";
        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        for (String op : operators) {
            if (op != null) {
                if (operatorsNames.equals(""))
                    operatorsNames = op;
                else
                    operatorsNames += " + " + op;
 
                if (op.equals("cambiarConexion")) successorFn.enableCambiarConexion();
                else if (op.equals("intercambiarConexion")) successorFn.enableIntercambiarConexion();
            }
        }

        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        heuristicFn.setAlpha(0.1);

        Search searchAlgorithm = new HillClimbingSearch();

        for (int i = 0; i < 10; ++i) {
            Problem problem = new Problem(new RedSensoresEstado(100, 4, 4321, 1234, solucionInicial), successorFn, goalTest, heuristicFn);

            long start = System.currentTimeMillis();
            SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
            long end = System.currentTimeMillis();

            RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

            System.out.println(String.format(
                "100, 4, 4321, 1234, %d, hill-climbing, %f, %d, %f, %d, %s",
                solucionInicial, 0.1, end - start, eval.cost(), eval.throughput(), operatorsNames));
        }
    }

    private static void experimento1() throws Exception {
        String[] operators = { "cambiarConexion", "intercambiarConexion" };

        // Cabecera del CSV
        System.out.println("n_sensores, n_centros, sensores_seed, centros_seed, mala1_buena2, algorithm, alpha, time_ms, solution_cost, solution_throughput, operators");

        experimento1_rec(1, operators, 0);
        experimento1_rec(2, operators, 0);
    }


    private static void experimento8() throws Exception {
        // TODO: el parámetro de la solución inicial lo tenemos que sacar experimentalmente con los experimentos 1 y 2
        // Temporalmente será la solución inicial mala
        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        // TODO: confirmar que usamos estos operadores
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();

        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        Search searchAlgorithm = new HillClimbingSearch();

        // Cabecera del CSV
        System.out.println("n_sensores, n_centros, sensores_seed, centros_seed, mala1_buena2, algorithm, alpha, time_ms, solution_cost, solution_throughput");

        for (double alpha = 0.0; alpha <= 1.0; alpha += 0.1) {
            heuristicFn.setAlpha(alpha);

            for (int i = 0; i < 10; ++i) {
                Problem problem = new Problem(new RedSensoresEstado(100, 4, 4321, 1234, 1), successorFn, goalTest, heuristicFn);

                long start = System.currentTimeMillis();
                SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
                long end = System.currentTimeMillis();

                RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

                System.out.println(String.format(
                    "100, 4, 4321, 1234, 1, hill-climbing, %f, %d, %f, %d",
                    alpha, end - start, eval.cost(), eval.throughput()));
            }
        }
    }
}