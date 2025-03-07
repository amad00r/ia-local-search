package redsensores;

import java.util.ArrayList;
import IA.Red.*;
    
public class RedSensoresEstado {

    // TODO: Como nos sabemos si los sensores estan conectados a algo o no? conectadoA != null?
    // TODO: Añadir cantidad de informacion a enviar?

    private class SensorInfo extends Sensor {
        private int capacidadRestante;
        private int conexionesRestantes;
        private Object conectadoA; // Puede ser un Sensor o un Centro de Datos

        public SensorInfo(int capacidad, int cx, int cy, int capacidadRestante, int conexionesRestantes, Object conectadoA) {
            super(capacidad, cx, cy);
            this.capacidadRestante = capacidadRestante;
            this.conexionesRestantes = conexionesRestantes;
            this.conectadoA = conectadoA;
        }

        public int getCapacidadRestante() {
            return capacidadRestante;
        }

        public void updateCapacidadRestante(int capacidadConexion) {
            if (capacidadConexion < capacidadRestante) this.capacidadRestante -= capacidadConexion;
            else this.capacidadRestante = 0;
        }

        public int getConexionesRestantes() {
            return conexionesRestantes;
        }

        public void recibirConexion() {
            this.conexionesRestantes -= 1;
        }

        public Object getConectadoA() {
            return conectadoA;
        }

        public void setConectadoA(Object conectadoA) {
            this.conectadoA = conectadoA;
        }
    }

    private class CentroInfo extends Centro {
        private int capacidadRestante;
        private int conexionesRestantes;

        public CentroInfo(int cx, int cy, int capacidadRestante, int conexionesRestantes) {
            super(cx, cy);
            this.capacidadRestante = capacidadRestante;
            this.conexionesRestantes = conexionesRestantes;
        }

        public int getCapacidadRestante() {
            return capacidadRestante;
        }

        public void updateCapacidadRestante(int capacidadConexion) {
            if (capacidadConexion < capacidadRestante) this.capacidadRestante -= capacidadConexion;
            else this.capacidadRestante = 0;
        }

        public int getConexionesRestantes() {
            return conexionesRestantes;
        }

        public void recibirConexion() {
            this.conexionesRestantes -= 1;
        }
    }

    private ArrayList<SensorInfo> sensoresInfoList = new ArrayList<>();
    private ArrayList<CentroInfo> centrosInfoList = new ArrayList<>();

    public RedSensoresEstado(int nsens, int ncent, int seed, int mode) {

        //Generar sensores con la info extra
        Sensores auxSensores = new Sensores(nsens, seed);
        for (int i = 0; i < nsens; i++) {
            sensoresInfoList.add(new SensorInfo((int) auxSensores.get(i).getCapacidad(), 
                                                auxSensores.get(i).getCoordX(), 
                                                auxSensores.get(i).getCoordY(), 
                                                (int) auxSensores.get(i).getCapacidad() * 2, 
                                                3, 
                                                null));
        }

        //Generar centros de datos con la info extra
        CentrosDatos auxCentros = new CentrosDatos(ncent, seed);
        for (int i = 0; i < ncent; i++) {
            centrosInfoList.add(new CentroInfo(auxSensores.get(i).getCoordX(), 
                                                auxSensores.get(i).getCoordY(), 
                                                150, 
                                                25));
        }

        //Generar solucion inicial
        if (mode == 0) solucionMala();
        else if (mode == 1) solucionBuena();
    }

    // TODO: Implementar solucion mala + definir nombre
    // Solucion mala -- Nombre provisional
    private void solucionMala() {return;}

    // TODO: Implementar solucion buena + definir nombre
    //Solucion buena -- Nombre provisional
    private void solucionBuena() {return;}

    // Getters y setters de los sensores
    public int sensorGetCoordX(int i) {
        return sensoresInfoList.get(i).getCoordX();
    }

    public int sensorGetCoordY(int i) {
        return sensoresInfoList.get(i).getCoordY();
    }

    public int sensorGetCapacidad(int i) {
        return (int) sensoresInfoList.get(i).getCapacidad();
    }

    public int sensorGetCapacidadRestante(int i) {
        return sensoresInfoList.get(i).getCapacidadRestante();
    }

    public int sensorGetConexionesRestantes(int i) {
        return sensoresInfoList.get(i).getConexionesRestantes();
    }

    public Object sensorGetConectadoA(int i) {
        return sensoresInfoList.get(i).getConectadoA();
    }

    public void sensorUpdateCapacidadRestante(int i, int capacidadConexion) {
        sensoresInfoList.get(i).updateCapacidadRestante(capacidadConexion);
    }

    public void sensorRecibirConexion(int i) {
        sensoresInfoList.get(i).recibirConexion();
    }

    public void sensorSetConectadoA(int i, Object conectadoA) {
        sensoresInfoList.get(i).setConectadoA(conectadoA);
    }

    // Getters y setters de los centros de datos
    public int centroGetCoordX(int i) {
        return centrosInfoList.get(i).getCoordX();
    }

    public int centroGetCoordY(int i) {
        return centrosInfoList.get(i).getCoordY();
    }

    public int centroGetCapacidadRestante(int i) {
        return centrosInfoList.get(i).getCapacidadRestante();
    }

    public int centroGetConexionesRestantes(int i) {
        return centrosInfoList.get(i).getConexionesRestantes();
    }

    public void centroUpdateCapacidadRestante(int i, int capacidadConexion) {
        centrosInfoList.get(i).updateCapacidadRestante(capacidadConexion);
    }

    public void centroRecibirConexion(int i) {
        centrosInfoList.get(i).recibirConexion();
    }

    // TODO: Implementar operadores + definirlos  
    
}
