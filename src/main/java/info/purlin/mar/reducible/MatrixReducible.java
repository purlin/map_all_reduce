package info.purlin.mar.reducible;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;

public class MatrixReducible implements AllReducible {
	
	private static final long serialVersionUID = 1L;

	public DoubleMatrix2D matrix;
	
	public MatrixReducible(int numRows, int numCols, String type){
		if("sparse".equalsIgnoreCase(type)) matrix = DoubleFactory2D.sparse.make(numRows, numCols);
		else matrix = DoubleFactory2D.dense.make(numRows, numCols);
	}
	
	public MatrixReducible(DoubleMatrix2D instance){
		matrix = instance.copy();
	}
	
	public String toString(){
		return matrix.toString();
	}
	
	@Override
	public void sum(AllReducible other) {
		DoubleMatrix2D otherMatrix = ((MatrixReducible) other).matrix;
		for(int i=0;i<matrix.rows();i++)
			for(int j=0;j<matrix.columns();j++)
				this.matrix.set(i, j, otherMatrix.get(i, j) + this.matrix.get(i, j));
	}

}
