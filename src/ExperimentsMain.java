package redsensores;

import aima.search.framework.Search;
import aima.search.informed.HillClimbingSearch;
import aima.search.framework.Problem;
import aima.search.framework.SearchAgent;

import java.util.Locale;
import java.util.Random;

public class ExperimentsMain {

    private static double alphaTest = 0.005;

    private static void usage() {
        System.err.println("""
            Usage:
                make run-experiment ARGS="1"
                make run-experiment ARGS="2"
                make run-experiment ARGS="8"
            """
        );
        System.exit(1);
    }

    public static void main(String[] args) {
        if (args.length == 0) usage();

        int numExperimento = Integer.parseInt(args[0]);
        switch (numExperimento) {
        case 1:
            if (args.length != 1) usage();
            try { experimento1(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 2:
            if (args.length != 1) usage();
            try { experimento2(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 8:
            if (args.length != 1) usage();
            try { experimento8(); } catch (Exception e) { e.printStackTrace(); }
            break;
        default:
            throw new UnsupportedOperationException(String.format("el experimento `%d` no está implementado", numExperimento));
        }
    }


    private static void experimento1_rec(int[] sensoresSeeds, int[] centrosSeeds, String[] operators, int idx) throws Exception {
        if (idx < operators.length) {
            experimento1_rec(sensoresSeeds, centrosSeeds, operators, idx + 1);
            String op = operators[idx];
            operators[idx] = null;
            experimento1_rec(sensoresSeeds, centrosSeeds, operators, idx + 1);
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
        heuristicFn.setAlpha(alphaTest);

        Search searchAlgorithm = new HillClimbingSearch();

        for (int i = 0; i < sensoresSeeds.length; ++i) {
            Problem problem = new Problem(new RedSensoresEstado(100, 4, sensoresSeeds[i], centrosSeeds[i], 1), successorFn, goalTest, heuristicFn);

            long start = System.currentTimeMillis();
            SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
            long end = System.currentTimeMillis();

            RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

            System.out.println(String.format(Locale.US,
                "100; 4; %d; %d; 1; hill-climbing; %f; %d; %d; %d; %s",
                sensoresSeeds[i], centrosSeeds[i], alphaTest, end - start, eval.cost(), eval.throughput(), operatorsNames));
        }
    }

    private static void experimento1() throws Exception {
        String[] operators = { "cambiarConexion", "intercambiarConexion" };

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput; operators");
        experimento1_rec((new Random()).ints().limit(10).toArray(), (new Random()).ints().limit(10).toArray(), operators, 0);
    }

    private static void experimento2() throws Exception {
        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput; operators");

        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();
        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        heuristicFn.setAlpha(alphaTest);

        Search searchAlgorithm = new HillClimbingSearch();

        int[] sensoresSeeds = (new Random()).ints().limit(10).toArray();
        int[] centrosSeeds = (new Random()).ints().limit(sensoresSeeds.length).toArray();

        for (int initialSolution = 1; initialSolution <= 2; ++initialSolution) {
            for (int i = 0; i < sensoresSeeds.length; ++i) {
                Problem problem = new Problem(new RedSensoresEstado(100, 4, sensoresSeeds[i], centrosSeeds[i], initialSolution), successorFn, goalTest, heuristicFn);

                long start = System.currentTimeMillis();
                SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
                long end = System.currentTimeMillis();

                RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

                System.out.println(String.format(Locale.US,
                    "100; 4; %d; %d; %d; hill-climbing; %f; %d; %d; %d; cambiarConexion + intercambiarConexion",
                    sensoresSeeds[i], centrosSeeds[i], initialSolution, alphaTest, end - start, eval.cost(), eval.throughput()));
            }
        }
    }


    private static void experimento8() throws Exception {
        // TODO: el parámetro de la solución inicial lo tenemos que sacar experimentalmente con los experimentos 1 y 2
        // Temporalmente será la solución inicial mala
        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        // TODO: confirmar que usamos estos operadores
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();

        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        Search searchAlgorithm = new HillClimbingSearch();

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput");

        heuristicFn.setAlpha(alphaTest);

        Problem problem = new Problem(new RedSensoresEstado(100, 4, 4321, 1234, 1), successorFn, new RedSensoresGoalTest(), heuristicFn);

        long start = System.currentTimeMillis();
        SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
        long end = System.currentTimeMillis();

        RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

        System.out.println(String.format(Locale.US,
            "100; 4; 4321; 1234; 1; hill-climbing; %f; %d; %d; %d",
            alphaTest, end - start, eval.cost(), eval.throughput()));
    }
}
