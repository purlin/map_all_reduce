package reducible;

public class IntScalarReducible implements AllReducible {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public int val;
	
	public IntScalarReducible(int instance) {
		val = instance;
	}
	
	public String toString(){
		return ""+val;
	}
	
	@Override
	public void sum(AllReducible other) {
		val += ((IntScalarReducible) other).val;		
	}

}
