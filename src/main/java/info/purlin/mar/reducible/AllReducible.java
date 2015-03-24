package info.purlin.mar.reducible;

import java.io.Serializable;

/**
 * Object for all-reduce
 * @author Erheng Zhong (purlin.zhong@gmail.com)
 *
 */
public interface AllReducible extends Serializable {
	/**
	 * Sum operator
	 * @param other
	 */
	public void sum(AllReducible other);
}
