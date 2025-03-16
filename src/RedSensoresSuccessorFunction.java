package redsensores;

import java.util.ArrayList;
import java.util.List;

import aima.search.framework.SuccessorFunction;
import aima.search.framework.Successor;

public class RedSensoresSuccessorFunction implements SuccessorFunction {
    @Override
    public List getSuccessors(Object state) {
        ArrayList<Successor> succ = new ArrayList<Successor>();
        return succ;
    }
}
