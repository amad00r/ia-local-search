package redsensores;

import IA.Red.*;
    
public class RedSensoresEstado {

    // TODO: Como nos sabemos si los sensores estan conectados a algo o no? conectadoA != null?
    // TODO: Añadir cantidad de informacion a enviar?

    private abstract class Conectable {
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

    private class SensorInfo extends Conectable {
        private Sensor sensor;
        private Conectable conectadoA; // Puede ser un Sensor o un Centro de Datos

        public SensorInfo(int capacidad, int cx, int cy, int capacidadRestante, int conexionesRestantes, Conectable conectadoA) {
            super(capacidadRestante, conexionesRestantes);
            sensor = new Sensor(capacidad, cx, cy);
            this.conectadoA = conectadoA;
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
            this.conectadoA = conectadoA;
        }

        @Override
        public void updateCapacidadRestante(int incremento) {
            this.capacidadRestante += incremento;
            if (this.conectadoA != null)
                this.conectadoA.updateCapacidadRestante(incremento);
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

    private class CentroInfo extends Conectable {
        private Centro centro;

        public CentroInfo(int cx, int cy, int capacidadRestante, int conexionesRestantes) {
            super(capacidadRestante, conexionesRestantes);
            centro = new Centro(cx, cy);
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

    public RedSensoresEstado(int nsens, int ncent, int seed, int mode) {
        sensoresInfoList = new SensorInfo[nsens];
        centrosInfoList = new CentroInfo[ncent];
        
        //Generar sensores con la info extra
        Sensores auxSensores = new Sensores(nsens, seed);
        for (int i = 0; i < nsens; i++) {
            sensoresInfoList[i] = new SensorInfo((int) auxSensores.get(i).getCapacidad(), 
                                                 auxSensores.get(i).getCoordX(), 
                                                 auxSensores.get(i).getCoordY(), 
                                                 (int) auxSensores.get(i).getCapacidad() * 2, 
                                                 3, 
                                                 null);
        }

        //Generar centros de datos con la info extra
        CentrosDatos auxCentros = new CentrosDatos(ncent, seed);
        for (int i = 0; i < ncent; i++) {
            centrosInfoList[i] = new CentroInfo(auxSensores.get(i).getCoordX(), 
                                                auxSensores.get(i).getCoordY(), 
                                                150, 
                                                25);
        }

        //Generar solucion inicial
        if (mode == 0) solucionMala();
        else if (mode == 1) solucionBuena();

        Evaluation eval = evaluateSolution();
        System.out.println(eval);
    }

    private record Evaluation(double cost, int throughput) {}

    private Evaluation evaluateSolution() {
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

    // Getters y setters de los sensores
    public int sensorGetCoordX(int i) {
        return sensoresInfoList[i].getCoordX();
    }

    public int sensorGetCoordY(int i) {
        return sensoresInfoList[i].getCoordY();
    }

    public int sensorGetCapacidad(int i) {
        return (int) sensoresInfoList[i].getCapacidad();
    }

    public int sensorGetCapacidadRestante(int i) {
        return sensoresInfoList[i].getCapacidadRestante();
    }

    public int sensorGetConexionesRestantes(int i) {
        return sensoresInfoList[i].getConexionesRestantes();
    }

    public Conectable sensorGetConectadoA(int i) {
        return sensoresInfoList[i].getConectadoA();
    }

    public void sensorRecibirConexion(int i, int capacidad) {
        sensoresInfoList[i].recibirConexion(capacidad);
    }

    public void sensorSetConectadoA(int i, Conectable conectadoA) {
        sensoresInfoList[i].setConectadoA(conectadoA);
    }

    // Getters y setters de los centros de datos
    public int centroGetCoordX(int i) {
        return centrosInfoList[i].getCoordX();
    }

    public int centroGetCoordY(int i) {
        return centrosInfoList[i].getCoordY();
    }

    public int centroGetCapacidadRestante(int i) {
        return centrosInfoList[i].getCapacidadRestante();
    }

    public int centroGetConexionesRestantes(int i) {
        return centrosInfoList[i].getConexionesRestantes();
    }

    public void centroRecibirConexion(int i, int capacidad) {
        centrosInfoList[i].recibirConexion(capacidad);
    }

    //OPERADORES
    public void cambiarConexion(SensorInfo origen, Conectable destino) {
        //Eliminar la conexion antigua
        origen.getConectadoA().recibirDesconexion(origen.getThroughput());

        //Establecer la nueva conexion
        origen.setConectadoA(destino);
        destino.recibirConexion(origen.getThroughput());
    }
}
