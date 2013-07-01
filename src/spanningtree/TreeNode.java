package spanningtree;

import java.net.Socket;

/**
 * Node of spanning tree
 * 
 * @author Erheng Zhong (purlin.zhong@gmail.com)
 * 
 */
public class TreeNode {
	/**
	 * Constructor
	 * 
	 * @param s : Socket
	 */
	public TreeNode(Socket s) {
		this.socket = s;
		this.ip = s.getInetAddress().getHostAddress();
	}

	/**
	 * Socket for current node
	 */
	public Socket socket;
	/**
	 * The IP address of current node
	 */
	public String ip;
	/**
	 * The port for the current client
	 */
	public int port;
	/**
	 * Index for the left child
	 */
	public int left_id = -1;
	/**
	 * Index for the right child
	 */
	public int right_id = -1;
	/**
	 * Index for the parent node
	 */
	public int parent_id = -1;

}
