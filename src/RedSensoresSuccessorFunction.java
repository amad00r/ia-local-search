package redsensores;

import redsensores.RedSensoresEstado.SensorInfo;
import redsensores.RedSensoresEstado.CentroInfo;
import redsensores.RedSensoresEstado.Conectable;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;
import java.util.ArrayList;
import java.util.List;

public class RedSensoresSuccessorFunction implements SuccessorFunction {

    private boolean accesible(SensorInfo buscado, SensorInfo sensor) {
        Conectable next = sensor.getConectadoA();
        if (next instanceof CentroInfo) return false;
        if (next == buscado) return true;
        return accesible(buscado, (SensorInfo)next);
    }

    public List<Successor> getSuccessors(Object state) {
        RedSensoresEstado red_state = (RedSensoresEstado)state;
        int nSensores = red_state.getNumSensores();
        int nCentros = red_state.getNumCentros();

        ArrayList<Successor> successors = new ArrayList<>();

        // Sucesores del operador "intercambiar conexion"
        // si tenemos una matriz bidimensional con todas las combinaciones de sensores,
        // lo que estamos haciendo es visitar la diagonal superior
        for (int i = 0; i < nSensores; ++i) {
            for (int j = i + 1; j < nSensores; ++j) {
                SensorInfo s1 = red_state.getSensorAt(i);
                SensorInfo s2 = red_state.getSensorAt(j);

                if (!accesible(s1, s2) && !accesible(s2, s1)) {
                    RedSensoresEstado new_state = new RedSensoresEstado(red_state);
                    new_state.intercambiarConexion(new_state.getSensorAt(i), new_state.getSensorAt(j));
                    successors.add(new Successor("intercambio de conexion", new_state));
                }
            }
        }

        // Sucesores del operador "cambiar conexion"
        for (int i = 0; i < nSensores; ++i) {
            SensorInfo sensor = red_state.getSensorAt(i);

            for (int j = 0; j < nSensores; ++j) {
                SensorInfo sensorCandidato = red_state.getSensorAt(j);
                if (
                    i != j &&
                    sensor.getConectadoA() != sensorCandidato &&
                    sensorCandidato.getConexionesRestantes() > 0 &&
                    !accesible(sensor, sensorCandidato)
                ) {
                    RedSensoresEstado new_state = new RedSensoresEstado(red_state);
                    new_state.cambiarConexion(new_state.getSensorAt(i), new_state.getSensorAt(j));
                    successors.add(new Successor("cambio de conexion a sensor", new_state));
                }
            }

            for (int j = 0; j < nCentros; ++j) {
                CentroInfo centroCandidato = red_state.getCentroAt(j);
                if (
                    sensor.getConectadoA() != centroCandidato &&
                    centroCandidato.getConexionesRestantes() > 0
                ) {
                    RedSensoresEstado new_state = new RedSensoresEstado(red_state);
                    new_state.cambiarConexion(new_state.getSensorAt(i), new_state.getCentroAt(j));
                    successors.add(new Successor("cambio de conexion a centro", new_state));
                }
            }
        }

        return successors;
    }
}
