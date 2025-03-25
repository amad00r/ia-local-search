package redsensores;

import IA.Red.*;

import java.util.*;

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
                this.conectadoA.updateCapacidadRestante(-variacionThroughput);
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
            centrosInfoList[i] = new CentroInfo(auxCentros.get(i).getCoordX(), 
                                                auxCentros.get(i).getCoordY(), 
                                                150, 
                                                25);
        }

        //Generar solucion inicial
        if (mode == 1) solucionMala();
        else if (mode == 2) solucionBuena();
        else if (mode == 3) solucionMedia();
    }

    public record Evaluation(int cost, int throughput) {}

    public Evaluation evaluateSolution() {
        int cost = 0;
        for (SensorInfo sensor : sensoresInfoList)
            cost += computeCost(sensor, sensor.getConectadoA());
        

        int throughput = 0;
        
        for (CentroInfo centro : centrosInfoList)
            throughput += 150 - Math.max(centro.getCapacidadRestante(),0);

        return new Evaluation(cost, throughput);
    }

    private int computeCost(SensorInfo origen, Conectable destino) {
        int dx = origen.getCoordX() - destino.getCoordX();
        int dy = origen.getCoordY() - destino.getCoordY();
        return (dx*dx + dy*dy) * origen.getThroughput();
    }

    // TODO: Implementar solucion mala + definir nombre
    // Solucion mala -- Nombre provisional
    private void solucionMala() {
        boolean[] visited = new boolean[sensoresInfoList.length];
        Conectable last = centrosInfoList[0];

        for (int i = 0; i < sensoresInfoList.length; ++i) {
            int candidate = -1;
            int candidateCost = -1;
            for (int j = 0; j < sensoresInfoList.length; ++j) {
                if (!visited[j]) {
                    int cost = computeCost(sensoresInfoList[j], last);
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

    static class Edge {
        int from, to, cost;
        int capacity, flow;

        Edge(int from, int to, int capacity, int cost, int flow) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.cost = cost;
            this.flow = flow;
        }
    }

    public void addEdge(List<List<Edge>> adj, int u, int v, int capacity, int cost) {
        adj.get(u).add(new Edge(u, v, capacity, cost, 0));
    }

    private boolean dijkstra(int nodes, int source, int sink, List<List<Edge>> adj, int[] parent, int[] parentEdge, int[] connections, int[] connectedTo, int[] newFlow, boolean[] visited, boolean[] captured) {
        int[] dist = new int[nodes];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(newFlow, 0);
        dist[source] = 0;
        parent[source] = -1;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        boolean found1 = false;
        for (int i = 0; i < adj.get(source).size() && !found1; ++i) {
            Edge e = adj.get(source).get(i);
            if (!captured[e.to]) {
                newFlow[e.to] = 0;
                dist[e.to] = 0;
                parent[e.to] = source;
                parentEdge[e.to] = i;
                pq.add(new int[]{e.to, dist[e.to]});
                found1 = true;
            }
        }

        while (!pq.isEmpty()) {
            int[] next = pq.poll();
            int u = next[0];
            int d = dist[u];

            if (visited[u]){
                Edge e;

                boolean found2 = false;
                for (int i = 0; i < adj.get(u).size() && !found2; ++i) {
                    e = adj.get(u).get(i);
                    if (e.to == connectedTo[u]) {
                        found2 = true;
                        if ((captured[u] || u >= getNumSensores()) && Math.min(newFlow[u], e.capacity - e.flow) == newFlow[u] && dist[u] + e.cost < dist[e.to]) {
                            newFlow[e.to] = newFlow[u];
                            dist[e.to] = dist[u] + e.cost;
                            parent[e.to] = u;
                            parentEdge[e.to] = i;
                            pq.add(new int[]{e.to, dist[e.to]});
                        }
                        else if ((!captured[u] && u < getNumSensores()) && Math.min(newFlow[u], e.capacity - (sensoresInfoList[u].getCapacidad() + e.flow)) == newFlow[u] && dist[u] + e.cost < dist[e.to]) {
                            newFlow[e.to] = newFlow[u] + sensoresInfoList[u].getCapacidad();
                            dist[e.to] = dist[u] + e.cost;
                            parent[e.to] = u;
                            parentEdge[e.to] = i;
                            pq.add(new int[]{e.to, dist[e.to]});
                        }
                    }
                }
            }
            else {
                for (int i = 0; i < adj.get(u).size(); i++) {
                    Edge e = adj.get(u).get(i);
                    if (((e.to < getNumSensores() && connections[e.to] < 3) || (e.to >= getNumSensores() && e.to < (getNumSensores() + getNumCentros()) && connections[e.to] < 25) || e.to >= (getNumSensores() + getNumCentros())) &&
                            (((captured[u] || u >= getNumSensores()) && e.capacity - e.flow >= newFlow[u]) ||
                                    ((!captured[u] && u < getNumSensores()) && e.capacity - (sensoresInfoList[u].getCapacidad() + e.flow) >= newFlow[u])) &&

                            dist[u] + e.cost < dist[e.to]) {

                        if (!captured[u] && u < getNumSensores()) {
                            newFlow[e.to] = Math.min(newFlow[u], e.capacity - (sensoresInfoList[u].getCapacidad() + e.flow)) + sensoresInfoList[u].getCapacidad();
                        }
                        else {
                            newFlow[e.to] = Math.min(newFlow[u], e.capacity - e.flow);
                        }
                        dist[e.to] = dist[u] + e.cost;
                        parent[e.to] = u;
                        parentEdge[e.to] = i;
                        pq.add(new int[]{e.to, dist[e.to]});
                    }
                }
            }
        }
        return dist[sink] != Integer.MAX_VALUE;
    }

    // TODO: Implementar solucion buena + definir nombre
    //Solucion buena -- Nombre provisional
    private void solucionBuena() {

        int totalFlow = 0;
        int totalCost = 0;

        int source = getNumSensores() + getNumCentros();
        int sink = source + 1;
        int nodes = sink + 1;

        int[] parent = new int[nodes];
        int[] parentEdge = new int[nodes];
        int[] connections = new int[nodes];
        int[] connectedTo = new int[nodes];
        int[] newFlow = new int[nodes];
        boolean[] visited = new boolean[nodes];
        boolean[] captured = new boolean[nodes];

        List<List<Edge>> adj = new ArrayList<>(nodes);
        for (int i = 0; i < nodes; ++i) adj.add(new ArrayList<>());

        // Creación aristas sensores
        for (int i = 0; i < getNumSensores(); ++i) {

            // Source a sensor
            addEdge(adj, source, i, sensoresInfoList[i].getCapacidad(), 0);

            // Sensor i al resto de sensores
            for (int j = i + 1; j < getNumSensores(); ++j) {

                int dx = sensoresInfoList[i].getCoordX() - sensoresInfoList[j].getCoordX();
                int dy = sensoresInfoList[i].getCoordY() - sensoresInfoList[j].getCoordY();

                int cost = dx*dx + dy*dy;
                addEdge(adj, i, j, sensoresInfoList[i].getCapacidad()*3, cost);
                addEdge(adj, j, i, sensoresInfoList[j].getCapacidad()*3, cost);
            }

            // Sensor i a los centros
            for (int j = 0; j < getNumCentros(); ++j) {
                int centro = getNumSensores() + j;

                int dx = sensoresInfoList[i].getCoordX() - centrosInfoList[j].getCoordX();
                int dy = sensoresInfoList[i].getCoordY() - centrosInfoList[j].getCoordY();

                int cost = dx*dx + dy*dy;
                addEdge(adj, i, centro, sensoresInfoList[i].getCapacidad()*3, cost);
            }
        }

        // Centros a sink
        for (int i = 0; i < getNumCentros(); ++i) addEdge(adj, getNumSensores() + i, sink, 150, 0);

        int path = 1;
        while (dijkstra(nodes, source, sink, adj, parent, parentEdge, connections, connectedTo, newFlow, visited, captured)) {
            ++path;
            for (int u = sink; u != source; u = parent[u]) {
                int v = parent[u];
                int edgeIdx = parentEdge[u];

                if (v < getNumSensores()) {
                    if (u < getNumSensores()) sensoresInfoList[v].setConectadoA(sensoresInfoList[u]);
                    else if (u < source) sensoresInfoList[v].setConectadoA(centrosInfoList[u - getNumSensores()]);
                }
                connectedTo[v] = u;

                if (v != source) {
                    if (adj.get(v).get(edgeIdx).flow == 0) captured[v] = true;
                    if (parent[v] != source && !visited[parent[v]]) ++connections[v];
                }

                adj.get(v).get(edgeIdx).flow += newFlow[u];

                if (u != sink) visited[u] = true;

                totalCost += newFlow[u] * adj.get(v).get(edgeIdx).cost;
            }

            totalFlow += newFlow[sink];
        }
    }

    private boolean accesible(SensorInfo buscado, SensorInfo sensor) {
        Conectable next = sensor.getConectadoA();
        if (next == null || next instanceof CentroInfo) return false;
        if (next == buscado) return true;
        return accesible(buscado, (SensorInfo)next);
    }

    public void solucionMedia() {

        int sensores = getNumSensores();

        List<Edge> edges = new ArrayList<>();

        // Creación aristas sensores
        for (int i = 0; i < getNumSensores(); ++i) {
            for (int j = i + 1; j < getNumSensores(); ++j) {

                int dx = sensoresInfoList[i].getCoordX() - sensoresInfoList[j].getCoordX();
                int dy = sensoresInfoList[i].getCoordY() - sensoresInfoList[j].getCoordY();

                int cost = dx*dx + dy*dy;
                edges.add(new Edge(i, j, sensoresInfoList[i].getCapacidad()*3, cost, 0));
                edges.add(new Edge(j, i, sensoresInfoList[j].getCapacidad()*3, cost, 0));
            }

            // Sensor i a los centros
            for (int j = 0; j < getNumCentros(); ++j) {
                int centro = getNumSensores() + j;

                int dx = sensoresInfoList[i].getCoordX() - centrosInfoList[j].getCoordX();
                int dy = sensoresInfoList[i].getCoordY() - centrosInfoList[j].getCoordY();

                int cost = dx*dx + dy*dy;
                edges.add(new Edge(i, centro, sensoresInfoList[i].getCapacidad()*3, cost, 0));
            }
        }

        edges.sort(Comparator.comparingInt(e -> e.cost));

        int sensoresRestantes = sensores;
        for (Edge e : edges) {
            SensorInfo sensor = sensoresInfoList[e.from];
            if (sensor.getConectadoA() == null) {
                Conectable dest;
                if (e.to >= sensores) dest = centrosInfoList[e.to - sensores];
                else dest = sensoresInfoList[e.to];

                if (dest.conexionesRestantes > 0 && sensor.getThroughput() <= dest.getCapacidadRestante() && (dest instanceof CentroInfo || !accesible(sensor, (SensorInfo) dest))) {
                    sensor.setConectadoA(dest);
                    --sensoresRestantes;

                    if (sensoresRestantes == 0) return;
                }
            }
        }

    }

    //OPERADORES
    public void cambiarConexion(SensorInfo origen, Conectable nuevoDestino) {
        assert 
            Arrays.asList(sensoresInfoList).contains(nuevoDestino) ||
            Arrays.asList(centrosInfoList).contains(nuevoDestino);
        assert 
            Arrays.asList(sensoresInfoList).contains(origen.getConectadoA()) ||
            Arrays.asList(centrosInfoList).contains(origen.getConectadoA());

        //Eliminar la conexion antigua 
        // !!NO HACE FALTA IMPLICITO EN SET CONECTADO
        // origen.getConectadoA().recibirDesconexion(origen.getThroughput());

        //Establecer la nueva conexion
        origen.setConectadoA(nuevoDestino);

        // !!NO HACE FALTA IMPLICITO EN SET CONECTADO
        //nuevoDestino.recibirConexion(origen.getThroughput());
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
