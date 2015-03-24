package reducible;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleMatrix1D;

public class VectorReducible implements AllReducible {

	private static final long serialVersionUID = 1L;

	public DoubleMatrix1D vectors;
	
	public VectorReducible(int numDimensions, String type){
		if("sparse".equalsIgnoreCase(type)) vectors = DoubleFactory1D.sparse.make(numDimensions);
		else vectors = DoubleFactory1D.dense.make(numDimensions);
	}
	
	public VectorReducible(DoubleMatrix1D instance){
		vectors = instance.copy();
	}
	
	public String toString(){
		return vectors.toString();
	}
	
	@Override
	public void sum(AllReducible other) {
		for(int i=0;i<vectors.size();i++) vectors.set(i, vectors.get(i) + ((VectorReducible) other).vectors.get(i));
	}
	
}
