package redsensores;

import IA.Red.*;
import java.util.Arrays;
import java.util.HashMap;

public class RedSensoresEstado {

    // TODO: Como nos sabemos si los sensores estan conectados a algo o no? conectadoA != null?
    // TODO: Añadir cantidad de informacion a enviar?

    public abstract class Conectable {
        protected int capacidadRestante;
        protected int conexionesRestantes;

        public Conectable(int capacidadRestante, int conexionesRestantes) {
            this.capacidadRestante = capacidadRestante;
            this.conexionesRestantes = conexionesRestantes;
        }

        public int getCapacidadRestante() {
            return capacidadRestante;
        }

        public int getConexionesRestantes() {
            return conexionesRestantes;
        }

        public abstract void updateCapacidadRestante(int incremento);

        public void recibirConexion(int capacidad) {
            this.conexionesRestantes -= 1;
            updateCapacidadRestante(-capacidad);
        }

        public void recibirDesconexion(int capacidad) {
            this.conexionesRestantes += 1;
            updateCapacidadRestante(capacidad);
        }

        public abstract int getCoordX();
        public abstract int getCoordY();
    }

    public class SensorInfo extends Conectable {
        private Sensor sensor;
        private Conectable conectadoA; // Puede ser un Sensor o un Centro de Datos

        public SensorInfo(int capacidad, int cx, int cy, int capacidadRestante, int conexionesRestantes, Conectable conectadoA) {
            super(capacidadRestante, conexionesRestantes);
            sensor = new Sensor(capacidad, cx, cy);
            this.conectadoA = conectadoA;
        }

        public SensorInfo(SensorInfo sensor) {
            this(
                sensor.getCapacidad(), sensor.getCoordX(), sensor.getCoordY(),
                sensor.getCapacidadRestante(), sensor.getConexionesRestantes(),
                sensor.getConectadoA());
        }

        public int getCapacidad() {
            return (int)sensor.getCapacidad();
        }

        public int getThroughput() {
            return getCapacidad()*3 - Math.max(0, this.capacidadRestante);
        }

        public Conectable getConectadoA() {
            return conectadoA;
        }

        public void setConectadoA(Conectable conectadoA) {
            if (this.conectadoA != null)
                this.conectadoA.recibirDesconexion(getThroughput());
            conectadoA.recibirConexion(getThroughput());
            this.conectadoA = conectadoA;
        }

        @Override
        public void updateCapacidadRestante(int incremento) {
            int throughputAnterior = getThroughput();    //Antes
            this.capacidadRestante += incremento;        //Cambio
            int throughputActual = getThroughput();      //Despues
            int variacionThroughput = throughputActual - throughputAnterior;

            if (this.conectadoA != null) {
                if (this.conectadoA instanceof CentroInfo) {
                    //Si es un centro, solo envía la variación del throughput.
                    if (variacionThroughput != 0) {
                        this.conectadoA.updateCapacidadRestante(-variacionThroughput);
                    }
                }
                else {
                    //Si no es un centro, envía el incremento directamente.
                    this.conectadoA.updateCapacidadRestante(incremento);
                }
            }
        }

        @Override
        public int getCoordX() {
            return sensor.getCoordX();
        }

        @Override
        public int getCoordY() {
            return sensor.getCoordY();
        }
    }

    public class CentroInfo extends Conectable {
        private Centro centro;

        public CentroInfo(int cx, int cy, int capacidadRestante, int conexionesRestantes) {
            super(capacidadRestante, conexionesRestantes);
            centro = new Centro(cx, cy);
        }

        public CentroInfo(CentroInfo centro) {
            this(
                centro.getCoordX(), centro.getCoordY(),
                centro.getCapacidadRestante(), centro.getConexionesRestantes());
        }

        @Override
        public void updateCapacidadRestante(int incremento) {
            this.capacidadRestante += incremento;
        }

        @Override
        public int getCoordX() {
            return centro.getCoordX();
        }

        @Override
        public int getCoordY() {
            return centro.getCoordY();
        }
    }
    
    private SensorInfo[] sensoresInfoList;
    private CentroInfo[] centrosInfoList;

    public int getNumSensores() {
        return sensoresInfoList.length;
    }

    public int getNumCentros() {
        return centrosInfoList.length;
    }

    public SensorInfo getSensorAt(int i) {
        return sensoresInfoList[i];
    }

    public CentroInfo getCentroAt(int i) {
        return centrosInfoList[i];
    }

    public RedSensoresEstado(RedSensoresEstado estado) {
        int nSensores = estado.getNumSensores();
        int nCentros = estado.getNumCentros();
        sensoresInfoList = new SensorInfo[nSensores];
        centrosInfoList = new CentroInfo[nCentros];

        HashMap<Conectable, Conectable> newPtrs = new HashMap<>();

        for (int i = 0; i < nSensores; ++i) {
            SensorInfo newSensor = new SensorInfo(estado.getSensorAt(i));
            newPtrs.put(estado.sensoresInfoList[i], newSensor);
            sensoresInfoList[i] = newSensor;
        }

        for (int i = 0; i < nCentros; ++i) {
            CentroInfo newCentro = new CentroInfo(estado.getCentroAt(i)); 
            newPtrs.put(estado.centrosInfoList[i], newCentro);
            centrosInfoList[i] = newCentro;
        }

        for (int i = 0; i < nSensores; ++i) {
            Conectable conectadoA = sensoresInfoList[i].conectadoA;
            if (conectadoA != null)
                sensoresInfoList[i].conectadoA = newPtrs.get(conectadoA);
        }
    }


    public RedSensoresEstado(int nsens, int ncent, int seedSensor, int seedCentro, int mode) {
        sensoresInfoList = new SensorInfo[nsens];
        centrosInfoList = new CentroInfo[ncent];
        
        //Generar sensores con la info extra
        Sensores auxSensores = new Sensores(nsens, seedSensor);
        for (int i = 0; i < nsens; i++) {
            sensoresInfoList[i] = new SensorInfo((int) auxSensores.get(i).getCapacidad(), 
                                                 auxSensores.get(i).getCoordX(), 
                                                 auxSensores.get(i).getCoordY(), 
                                                 (int) auxSensores.get(i).getCapacidad() * 2, 
                                                 3, 
                                                 null);
        }

        //Generar centros de datos con la info extra
        CentrosDatos auxCentros = new CentrosDatos(ncent, seedCentro);
        for (int i = 0; i < ncent; i++) {
            centrosInfoList[i] = new CentroInfo(auxSensores.get(i).getCoordX(), 
                                                auxSensores.get(i).getCoordY(), 
                                                150, 
                                                25);
        }

        //Generar solucion inicial
        if (mode == 1) solucionMala();
        else if (mode == 2) solucionBuena();
    }

    public record Evaluation(double cost, int throughput) {}

    public Evaluation evaluateSolution() {
        double cost = 0.0;
        for (SensorInfo sensor : sensoresInfoList)
            cost += computeCost(sensor, sensor.getConectadoA());
        
        int throughput = 0;
        for (SensorInfo sensor : sensoresInfoList)
            if (sensor.getConectadoA() instanceof CentroInfo)
                throughput += sensor.getThroughput();

        return new Evaluation(cost, throughput);
    }

    private double computeCost(SensorInfo origen, Conectable destino) {
        int dx = origen.getCoordX() - destino.getCoordX();
        int dy = origen.getCoordY() - destino.getCoordY();
        double d = Math.sqrt(dx*dx + dy*dy);
        return d*d + origen.getThroughput();
    }

    // TODO: Implementar solucion mala + definir nombre
    // Solucion mala -- Nombre provisional
    private void solucionMala() {
        boolean[] visited = new boolean[sensoresInfoList.length];
        Conectable last = centrosInfoList[0];

        for (int i = 0; i < sensoresInfoList.length; ++i) {
            int candidate = -1;
            double candidateCost = -1;
            for (int j = 0; j < sensoresInfoList.length; ++j) {
                if (!visited[j]) {
                    double cost = computeCost(sensoresInfoList[j], last);
                    if (cost > candidateCost) {
                        candidateCost = cost;
                        candidate = j;
                    }
                }
            }

            visited[candidate] = true;
            sensoresInfoList[candidate].setConectadoA(last);
            last = sensoresInfoList[candidate];
        }
    }

    // TODO: Implementar solucion buena + definir nombre
    //Solucion buena -- Nombre provisional
    private void solucionBuena() {return;}

    //OPERADORES
    public void cambiarConexion(SensorInfo origen, Conectable nuevoDestino) {
        assert 
            Arrays.asList(sensoresInfoList).contains(nuevoDestino) ||
            Arrays.asList(centrosInfoList).contains(nuevoDestino);
        assert 
            Arrays.asList(sensoresInfoList).contains(origen.getConectadoA()) ||
            Arrays.asList(centrosInfoList).contains(origen.getConectadoA());

        //Eliminar la conexion antigua
        origen.getConectadoA().recibirDesconexion(origen.getThroughput());

        //Establecer la nueva conexion
        origen.setConectadoA(nuevoDestino);
        nuevoDestino.recibirConexion(origen.getThroughput());
    }

    public void intercambiarConexion(SensorInfo sensorA, SensorInfo sensorB) {        
        Conectable aux = sensorA.getConectadoA();
        cambiarConexion(sensorA, sensorB.getConectadoA());
        cambiarConexion(sensorB, aux);
    }

    public String toString() {
        String ret = "";

        for (int i = 0; i < sensoresInfoList.length; i++) {
            SensorInfo sensor = sensoresInfoList[i];
            ret +=
                "(Sensor[" + i + "], Coords = (" + sensor.getCoordX() + ", " + sensor.getCoordY() +
                "), Capacidad = " + sensor.getCapacidad() +
                ", Capacidad restante = " + sensor.getCapacidadRestante() +
                ", Conexiones restantes = " + sensor.getConexionesRestantes() +
                ", Throughput = " + sensor.getThroughput() +
                ") -> ";
            
            if (sensor.getConectadoA() instanceof SensorInfo)
                ret += "Sensor[" + Arrays.asList(sensoresInfoList).indexOf((SensorInfo)sensor.getConectadoA()) + "]\n";
            else
                ret += "Centro[" + Arrays.asList(centrosInfoList).indexOf((CentroInfo)sensor.getConectadoA()) + "]\n";
        }

        for (int i = 0; i < centrosInfoList.length; i++) {
            CentroInfo centro = centrosInfoList[i];
            ret +=
                "(Centro[" + i + "], Coords = (" + centro.getCoordX() + ", " + centro.getCoordY() +
                "), Capacidad restante = " + centro.getCapacidadRestante() +
                ", Conexiones restantes = " + centro.getConexionesRestantes() + ")\n";
        }

        ret += evaluateSolution().toString();

        return ret;
    }
}
