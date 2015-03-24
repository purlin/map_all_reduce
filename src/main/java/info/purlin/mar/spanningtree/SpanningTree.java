package info.purlin.mar.spanningtree;

import java.util.HashMap;
import java.util.Map;

/**
 * Spanning tree
 *
 * @author Erheng Zhong (purlin.zhong@gmail.com)
 */
public class SpanningTree {
    /**
     * Data Structure
     */
    public Map<Integer, TreeNode> Tree = new HashMap<Integer, TreeNode>();

    /**
     * Build Tree
     *
     * @param root:   current node id
     * @param parent: the parent id of current node
     */
    public void buildTree(int root, int parent) {
        int left_id = root * 2 + 1;
        int right_id = root * 2 + 2;
        TreeNode currentNode = Tree.get(root);
        currentNode.parent_id = parent;
        if (Tree.containsKey(left_id)) {
            currentNode.left_id = left_id;
            buildTree(left_id, root);
        }
        if (Tree.containsKey(right_id)) {
            currentNode.right_id = right_id;
            buildTree(right_id, root);
        }
        Tree.put(root, currentNode);
    }

}
