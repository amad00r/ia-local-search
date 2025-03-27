package redsensores;

import aima.search.framework.Search;
import aima.search.informed.HillClimbingSearch;
import aima.search.informed.SimulatedAnnealingSearch;
import aima.search.framework.Problem;
import aima.search.framework.SearchAgent;

import java.util.Locale;
import java.util.Random;

public class ExperimentsMain {

    private static double alphaTest = 0.4;

    private static void usage() {
        System.err.println("""
            Usage:
                make run-experiment ARGS="1"
                make run-experiment ARGS="2"
                make run-experiment ARGS="3"
                make run-experiment ARGS="4"
                make run-experiment ARGS="5"
                make run-experiment ARGS="6"
                make run-experiment ARGS="7 <alpha-low> <alpha-up> <alpha-step>"
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
        case 3:
            if (args.length != 1) usage();
            try { experimento3(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 4:
            if (args.length != 1) usage();
            try { experimento4(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 5:
            if (args.length != 1) usage();
            try { experimento5(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 6:
            if (args.length != 1) usage();
            try { experimento6(); } catch (Exception e) { e.printStackTrace(); }
            break;
        case 7:
            if (args.length != 4) usage();
            double alphaLow = Double.parseDouble(args[1]);
            double alphaUp = Double.parseDouble(args[2]);
            double alphaStep = Double.parseDouble(args[3]);
            try { experimento7(alphaLow, alphaUp, alphaStep); } catch (Exception e) { e.printStackTrace(); }
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

    private static void experimento3() throws Exception {

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; steps; stiter; k; lambda; time_ms; solution_cost; solution_throughput; operators");

        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();
        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        heuristicFn.setAlpha(alphaTest);

        int[] sensoresSeeds = (new Random()).ints().limit(5).toArray();
        int[] centrosSeeds = (new Random()).ints().limit(sensoresSeeds.length).toArray();
        float[] lambda = new float[]{0.001f, 0.005f, 0.01f, 0.02f, 0.4f, 0.6f, 0.8f, 0.1f};
        int[] ks = new int[]{1, 10, 50, 100};

        for (int i = 0; i < sensoresSeeds.length; ++i) {
            for (int k = 0; k < ks.length; ++k) {
                for (int j = 0; j < lambda.length; ++j) {
                    Search simulatedAnnealing = new SimulatedAnnealingSearch(500, 1, ks[k], lambda[j]);

                    Problem problem = new Problem(new RedSensoresEstado(100, 4, sensoresSeeds[i], centrosSeeds[i], 2), successorFn, goalTest, heuristicFn);

                    long start = System.currentTimeMillis();
                    SearchAgent agent = new SearchAgent(problem, simulatedAnnealing);
                    long end = System.currentTimeMillis();

                    RedSensoresEstado.Evaluation eval = ((RedSensoresEstado) simulatedAnnealing.getGoalState()).evaluateSolution();

                    System.out.println(String.format(Locale.US,
                            "100; 4; %d; %d; %d; simulated-annealing; %f; %d; %d; %d; %f; %d; %d; %d; cambiarConexion + intercambiarConexion",
                            sensoresSeeds[i], centrosSeeds[i], 2, alphaTest, 500, 10, ks[k], lambda[j], end - start, eval.cost(), eval.throughput()));
                }
            }
        }
    }

    private static void experimento4() throws Exception {

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput; operators");

        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();
        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        heuristicFn.setAlpha(alphaTest);

        Search searchAlgorithm = new HillClimbingSearch();

        for (int i = 4; i <= 12; i += 2) {
            int[] sensoresSeeds = (new Random()).ints().limit(10).toArray();
            int[] centrosSeeds = (new Random()).ints().limit(sensoresSeeds.length).toArray();

            for (int j = 0; j < sensoresSeeds.length; ++j) {
                Problem problem = new Problem(new RedSensoresEstado(25*i, i, sensoresSeeds[j], centrosSeeds[j], 2), successorFn, goalTest, heuristicFn);

                long start = System.currentTimeMillis();
                SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
                long end = System.currentTimeMillis();

                RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

                System.out.println(String.format(Locale.US,
                        "%d; %d; %d; %d; %d; hill-climbing; %f; %d; %d; %d; cambiarConexion + intercambiarConexion",
                        25*i, i, sensoresSeeds[j], centrosSeeds[j], 2, alphaTest, end - start, eval.cost(), eval.throughput()));
            }
        }
    }

    private static void experimento5() throws Exception {

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput; centros_usados");

        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();
        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        heuristicFn.setAlpha(alphaTest);

        int[] sensoresSeeds = (new Random()).ints().limit(10).toArray();
        int[] centrosSeeds = (new Random()).ints().limit(sensoresSeeds.length).toArray();

        Search hillClimbing = new HillClimbingSearch();

        for (int i = 0; i < sensoresSeeds.length; ++i) {
            RedSensoresEstado red = new RedSensoresEstado(100, 4, sensoresSeeds[i], centrosSeeds[i], 2);
            Problem problem = new Problem(red, successorFn, goalTest, heuristicFn);

            long start = System.currentTimeMillis();
            SearchAgent agent = new SearchAgent(problem, hillClimbing);
            long end = System.currentTimeMillis();

            RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)hillClimbing.getGoalState()).evaluateSolution();

            int centros = 0;
            for (int j = 0; j < red.getNumCentros(); ++j) {
                if (red.getCentroAt(j).getConexionesRestantes() < 25) ++centros;
            }

            System.out.println(String.format(Locale.US,
                    "%d; %d; %d; %d; %d; hill-climbing; %f; %d; %d; %d; %d",
                    100, 4, sensoresSeeds[i], centrosSeeds[i], 2, alphaTest, end - start, eval.cost(), eval.throughput(), centros));
        }

        Search simulatedAnnealing = new SimulatedAnnealingSearch(500, 1, 1, 0.8);

        for (int i = 0; i < sensoresSeeds.length; ++i) {
            RedSensoresEstado red = new RedSensoresEstado(100, 4, sensoresSeeds[i], centrosSeeds[i], 2);
            Problem problem = new Problem(red, successorFn, goalTest, heuristicFn);

            long start = System.currentTimeMillis();
            SearchAgent agent = new SearchAgent(problem, simulatedAnnealing);
            long end = System.currentTimeMillis();

            RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)simulatedAnnealing.getGoalState()).evaluateSolution();

            int centros = 0;
            for (int j = 0; j < red.getNumCentros(); ++j) {
                if (red.getCentroAt(j).getConexionesRestantes() < 25) ++centros;
            }

            System.out.println(String.format(Locale.US,
                    "%d; %d; %d; %d; %d; simulated-annealing; %f; %d; %d; %d; %d",
                    100, 4, sensoresSeeds[i], centrosSeeds[i], 2, alphaTest, end - start, eval.cost(), eval.throughput(), centros));
        }
    }

    private static void experimento6() throws Exception {

        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();

        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();

        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        heuristicFn.setAlpha(alphaTest);

        int[] sensoresSeeds = (new Random()).ints().limit(10).toArray();
        int[] centrosSeeds = (new Random()).ints().limit(sensoresSeeds.length).toArray();

        Search searchAlgorithm = new HillClimbingSearch();

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput; centros usados");

        for (int i = 2; i <= 10; i += 2) {
            for (int k = 0; k < 10; ++k) {
                RedSensoresEstado estado = new RedSensoresEstado(100, i, sensoresSeeds[k], centrosSeeds[k], 2);
                Problem problem = new Problem(estado, successorFn, goalTest, heuristicFn);

                long start = System.currentTimeMillis();
                SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
                long end = System.currentTimeMillis();

                RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

                int centrosUsados = 0;
                for (int j = 0; j < estado.getNumCentros(); ++j) {
                    if (estado.getCentroAt(j).getConexionesRestantes() < 25) centrosUsados++;
                }

                System.out.println(String.format(Locale.US,
                        "100; %d; %d; %d; 2; hill-climbing; %f; %d; %d; %d; %d",
                        i, sensoresSeeds[k], centrosSeeds[k], alphaTest, end - start, eval.cost(), eval.throughput(), centrosUsados));
            }
        }

        searchAlgorithm = new SimulatedAnnealingSearch(500, 1, 1, 0.8);

        for (int i = 2; i <= 10; i += 2) {
            for (int k = 0; k < 10; ++k) {
                RedSensoresEstado estado = new RedSensoresEstado(100, i, sensoresSeeds[k], centrosSeeds[k], 2);
                Problem problem = new Problem(estado, successorFn, goalTest, heuristicFn);

                long start = System.currentTimeMillis();
                SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
                long end = System.currentTimeMillis();

                RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

                int centrosUsados = 0;
                for (int j = 0; j < estado.getNumCentros(); ++j) {
                    if (estado.getCentroAt(j).getConexionesRestantes() < 25) centrosUsados++;
                }

                System.out.println(String.format(Locale.US,
                        "100; %d; %d; %d; 2; simulatedannealing; %f; %d; %d; %d; %d",
                        i, sensoresSeeds[k], centrosSeeds[k], alphaTest, end - start, eval.cost(), eval.throughput(), centrosUsados));
            }
        }
    }

    private static void experimento7(double alphaLow, double alphaUp, double alphaStep) throws Exception {
        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();

        RedSensoresGoalTest goalTest = new RedSensoresGoalTest();
        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        Search searchAlgorithm = new HillClimbingSearch();

        int[] sensoresSeeds = (new Random()).ints().limit(10).toArray();
        int[] centrosSeeds = (new Random()).ints().limit(sensoresSeeds.length).toArray();

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput; operators");

        for (double alpha = alphaLow; alpha <= alphaUp; alpha += alphaStep) {
            heuristicFn.setAlpha(alpha);

            for (int i = 0; i < sensoresSeeds.length; ++i) {
                Problem problem = new Problem(new RedSensoresEstado(100, 2, sensoresSeeds[i], centrosSeeds[i], 2), successorFn, goalTest, heuristicFn);

                long start = System.currentTimeMillis();
                SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
                long end = System.currentTimeMillis();

                RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

                System.out.println(String.format(Locale.US,
                    "100; 2; %d; %d; 2; hill-climbing; %f; %d; %d; %d; cambiarConexion + intercambiarConexion",
                    sensoresSeeds[i], centrosSeeds[i], alpha, end - start, eval.cost(), eval.throughput()));
            }
        }
    }

    private static void experimento8() throws Exception {
        RedSensoresSuccessorFunction successorFn = new RedSensoresSuccessorFunction();
        successorFn.enableCambiarConexion();
        successorFn.enableIntercambiarConexion();

        RedSensoresHeuristicFunction heuristicFn = new RedSensoresHeuristicFunction();
        Search searchAlgorithm = new HillClimbingSearch();

        // Cabecera del CSV
        System.out.println("n_sensores; n_centros; sensores_seed; centros_seed; mala1_buena2; algorithm; alpha; time_ms; solution_cost; solution_throughput");

        heuristicFn.setAlpha(alphaTest);

        Problem problem = new Problem(new RedSensoresEstado(100, 4, 4321, 1234, 2), successorFn, new RedSensoresGoalTest(), heuristicFn);

        long start = System.currentTimeMillis();
        SearchAgent agent = new SearchAgent(problem, searchAlgorithm);
        long end = System.currentTimeMillis();

        RedSensoresEstado.Evaluation eval = ((RedSensoresEstado)searchAlgorithm.getGoalState()).evaluateSolution();

        System.out.println(String.format(Locale.US,
            "100; 4; 4321; 1234; 2; hill-climbing; %f; %d; %d; %d",
            alphaTest, end - start, eval.cost(), eval.throughput()));
    }
}
