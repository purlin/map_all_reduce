package reducible;

public class FloatScalarReducible implements AllReducible  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public float val;
	
	public FloatScalarReducible(float instance) {
		val = instance;
	}
	
	public String toString(){
		return ""+val;
	}
	
	@Override
	public void sum(AllReducible other) {
		val += ((FloatScalarReducible) other).val;		
	}
}
