
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<K,T> root;
	public static final int D = 2;

	/**
	 * TODO Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public T search(K key) {
	    LeafNode<K, T> nodeMightContainsKey = (LeafNode<K, T>) treeSearch(key, root); 
	    int position = nodeMightContainsKey.keys.indexOf(key);
	    if ( position == -1){
	        return null;
	    }
	    else {
            return nodeMightContainsKey.values.get(position);
        }
	}
	
	/**
	 * recursive search start from one node
	 * if the node is a leaf node, return that node
	 * which means that node should contain the key
	 * @param key: which you want to search
	 * @param node: start from it
	 */
	private Node treeSearch(K key, Node<K, T> node){
	    if (node.isLeafNode)
	        return node;
	    // if node is not leaf node then convert it to index node
	    IndexNode<K, T> indexNode = (IndexNode<K, T>)node;
	    if (key.compareTo(indexNode.keys.get(0)) < 0 ) {
            return treeSearch(key, indexNode.children.get(0));
        }
	    else if (key.compareTo(indexNode.keys.get(indexNode.keys.size() - 1)) >= 0) {
	        return treeSearch(key, indexNode.children.get(indexNode.children.size()-1));
        }
	    else {
            // find the position" node.k_i <= key < node.k_{i+1}
	        int sizeOfKeys = indexNode.keys.size();
	        for (int i = 2; i <= sizeOfKeys; i++){
	            K tempKey = indexNode.keys.get(sizeOfKeys - i);
	            if (key.compareTo(tempKey) >= 0){
	                return treeSearch(key, indexNode.children.get(sizeOfKeys - i + 1));
	            }
	        }
        }
	    // should not get here
	    return null;
	}

	/**
	 * Insert a key/value pair into the BPlusTree
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(K key, T value) {
	    if (root == null){
	        root = new LeafNode<K, T>(key, value);
	        root.parent = null;
	    }
	    else {
	        // find the place to insert data
            LeafNode<K, T> leafNode = (LeafNode<K, T>) treeSearch(key, root);
            leafNode.insertSorted(key, value);
            // check node is full or not
            if (leafNode.isOverflowed()){
                Entry<K, Node<K,T>> entry = splitLeafNode(leafNode);
                K splitingKey = entry.getKey();
                LeafNode<K, T> rightNode = (LeafNode<K, T>) entry.getValue();
                rightNode.parent = leafNode.parent; // right side node share parent with left(origin) first
                // insert entry to parent node
                if (leafNode.parent == null) { // node is root
                    root = new IndexNode<>(splitingKey, leafNode, rightNode);
                    leafNode.parent = root;
                    rightNode.parent = root;
                }
                else{
                    // find the right place to insert splitting key
                    // then check the indexNode's isOverFlowed
                    insertIndexKey((IndexNode<K, T>) leafNode.parent, entry);
                }
            }
        }

	}
	/**
	 * After split leaf node, the split key should be insert into the parent node of 
	 * the leaf node. After insert the split key, still need to check whether the index node
	 * is over flowed. If so need to split index node and then do the same thing, update 
	 * the split key in the it's parent node. recursively doing this, until reach the root node
	 * and if root is full, then split create new root node.
	 * @param indexNode: the index node which need insert entry into it
	 * @param entry: the split key and child node
	 */
	private void insertIndexKey(IndexNode<K, T> indexNode, Entry<K, Node<K, T>> entry){
	    K splittingKey = entry.getKey();
	    int index = 0;
	    int sizeOfKeys = indexNode.keys.size();
        for(int i=0; i < sizeOfKeys; i++){
            if(splittingKey.compareTo(indexNode.keys.get(sizeOfKeys - 1 - i)) > 0){
                index = sizeOfKeys -i;
                break;
            }
        }
        indexNode.insertSorted(entry, index);
        
        if (indexNode.isOverflowed()) {
            
            Entry<K, Node<K,T>> newEntry = splitIndexNode(indexNode);
            K newSplittingKey = newEntry.getKey();
            IndexNode<K, T> rightNode = (IndexNode<K, T>) newEntry.getValue();
            if (indexNode.parent == null) { // which means indexNode is root node
                // create new index node for root
                root = new IndexNode<>(newSplittingKey, indexNode, rightNode);
                // setting the previous node point to root
                // and right node point to root
                indexNode.parent = root;
                rightNode.parent = root;
            }
            else {
                //
                insertIndexKey((IndexNode<K, T>) indexNode.parent, newEntry);
            }
        }
	}

	/**
	 * Split a leaf node and return the new right node and the splitting
	 * key as an Entry<slitingKey, RightNode>
	 * 
	 * @param leaf, any other relevant data
	 * @return the key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitLeafNode(LeafNode<K,T> leaf) {
	    
	    ArrayList<K> rightNodeKeys = new ArrayList<>();
	    ArrayList<T> rightNodeValues = new ArrayList<>();
	    LeafNode<K, T> rightNode;
	    // get keys and values from origin leaf Node
	    int keysSize = leaf.keys.size();
	    for(int i=D; i < keysSize; i++){
	        rightNodeKeys.add(leaf.keys.get(i));
	        rightNodeValues.add(leaf.values.get(i));
	    }
	    // remove keys and values from D to end of leafNode
	    int size = leaf.keys.size();
	    for(int i=D; i < size; i++){
	        leaf.keys.remove(D);
	        leaf.values.remove(D);
	    }
	    // consider maintaining the right order of previous point and next point 
	    rightNode = new LeafNode<K, T>(rightNodeKeys, rightNodeValues);
	    rightNode.nextLeaf = leaf.nextLeaf;
	    rightNode.previousLeaf = leaf;
	    if (leaf.nextLeaf != null){
	        leaf.nextLeaf.previousLeaf = rightNode;
	    }
	    leaf.nextLeaf = rightNode;
	    rightNode.parent = leaf.parent;
	    Entry<K, Node<K, T>> entry = new SimpleEntry(rightNode.keys.get(0), rightNode);
		return entry;
	}

	/**
	 * split an indexNode and return the new right node and the splitting
	 * key as an Entry<splitting, RightNode>
	 * 
	 * @param index, any other relevant data
	 * @return new key/node pair as an Entry
	 */
	public Entry<K, Node<K,T>> splitIndexNode(IndexNode<K,T> index) {
	    // 
	    ArrayList<K> rightNodeKeys = new ArrayList<>();
	    ArrayList<Node<K, T>> rightNodeChildren = new ArrayList<>();
	    IndexNode<K, T> rightNode;
	    K splittingKey = index.keys.get(D);
	    // splitting key should not be included in right node, so start from D+1
	    for (int i = D+1; i < index.keys.size(); i++){
	        rightNodeKeys.add(index.keys.get(i));
	    }
	    for (int i = D+1; i < index.children.size(); i++){
	        rightNodeChildren.add(index.children.get(i));
	    }
	    // remove elements in original indexNode
	    // have to remove splitting key, so start from D
	    int size = index.keys.size();
	    for (int i = D; i < size; i++){
	        index.keys.remove(D);
	    }
	    // if use index.children.size() in for() size will be evaluated dynamically 
	    size = index.children.size(); 
	    for(int i = D+1; i < size; i++){
	        index.children.remove(D+1);
	    }
	    rightNode = new IndexNode<>(rightNodeKeys, rightNodeChildren);
	    rightNode.parent = index.parent;
	    // change its children to point to it
	    int sizeOfChildren = rightNode.children.size();
	    for (int i = 0; i < sizeOfChildren; i++){
	        Node<K, T> child = rightNode.children.get(i);
	        child.parent = rightNode;
	    }
	    
	    Entry<K, Node<K, T>> entry = new SimpleEntry(splittingKey, rightNode);
		return entry;
	}

	/**
	 * Delete a key/value pair from this B+Tree
	 * 
	 * @param key
	 */
	public void delete(K key) {
	    LeafNode<K, T> nodeMightContainsKey = (LeafNode<K, T>) treeSearch(key, root); 
        int position = nodeMightContainsKey.keys.indexOf(key);
        if ( position == -1){
            System.err.println("Cannot find the key in the tree");
        }
        else {
            nodeMightContainsKey.keys.remove(position);
            nodeMightContainsKey.values.remove(position);
            // whether is under flowed
            if (nodeMightContainsKey.isUnderflowed()){
                if(nodeMightContainsKey.parent == null){ 
                    //which means its a root node
                    //because leaf node might be the root node
                    return;
                }
                LeafNode<K, T> underFlowedNode = nodeMightContainsKey;
                // try get left sibling first
                LeafNode<K, T> targetSibling;
                targetSibling = underFlowedNode.previousLeaf;
                IndexNode<K, T> parent = (IndexNode<K, T>)underFlowedNode.parent;
                int index;
                LeafNode<K, T> left;
                LeafNode<K, T> right;
                
                if (targetSibling == null || targetSibling.parent != underFlowedNode.parent){
                    targetSibling = underFlowedNode.nextLeaf;
                    left = underFlowedNode;
                    right = targetSibling;
                }
                else{
                    left = targetSibling;
                    right = underFlowedNode;
                }

                index = handleLeafNodeUnderflow(left, right, parent);
                // which means merge happened
                if (index != -1){
                    // delete the splitting key in their parent
                    deleteIndexKey(parent, index, right);
                }
                
            }
        }

	}
	/**
	 * delete split key from index node.
	 * check after delete. if the index node underflowed, than
	 * invoke function, handleIndexNodeUnderflow, if after handler, still
	 * need to delete split key in parent node (while merge, happened), invoke 
	 * this function recursively. Until some node will not underflowed or, reach
	 * the root.
	 * @param node: delete split key in this node
	 * @param position: indicate the position of split key
	 * @param rightNode: if delete the last element in root node, right node become the new root
	 */
	public void deleteIndexKey(IndexNode<K, T> node, int position, Node<K, T> rightNode){
	    node.keys.remove(position);
	    node.children.remove(position);
	    
	    if (node.isUnderflowed()){
	        if (node.parent == null){ // which means node is root
	            if(node.keys.isEmpty()){
	                root = rightNode;
	                rightNode.parent = null; // after right node become root, should change point of parent.
	            }
	        }
	        else{
	            IndexNode<K, T> parent = (IndexNode<K, T>) node.parent;
	            int nodePosition = parent.children.indexOf(node);
	            IndexNode<K, T> leftIndex;
	            IndexNode<K, T> rightIndex;
	            if (nodePosition == 0){//which means node is most left one
	                leftIndex = node;
	                rightIndex = (IndexNode<K, T>) parent.children.get(1);
	            }
	            else{
	                leftIndex = (IndexNode<K, T>) parent.children.get(nodePosition - 1);
	                rightIndex = node;
	            }
	            int index = handleIndexNodeUnderflow(leftIndex, rightIndex, parent);
	            if (index != -1){
	                deleteIndexKey(parent, index, rightIndex);
	            }
	        }
	    }
	}

	/**
	 * Handle LeafNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleLeafNodeUnderflow(LeafNode<K,T> left, LeafNode<K,T> right,
			IndexNode<K,T> parent) {
	    // redistribute
	    if(left.keys.size() + right.keys.size() >= 2*D){
	        if(left.keys.size() > right.keys.size()){
	            // borrow from the tail of left node
	            // only leave D elements in left node
	            int sizeofLeft = left.keys.size();
	            for (int i=0; i < sizeofLeft - D; i++){
	                right.insertSorted(left.keys.get(D), left.values.get(D));
	                left.keys.remove(D);
	                left.values.remove(D);
	            }
	        }
	        else{
	            // right size equal left size is impossible
	            // borrow from the head of right node
	            left.insertSorted(right.keys.get(0), right.values.get(0));
	            right.keys.remove(0);
	            right.values.remove(0);
	        }
	        // update the splitting key in parent
	        int keyPosition = parent.children.indexOf(right) - 1;
	        parent.keys.remove(keyPosition);
	        parent.keys.add(keyPosition, right.keys.get(0));
	        return -1;
	    }
	    // merge
	    // merge have to change next and previous point
	    else{
	        // merge to the right node
	        int sizeofLeft = left.keys.size();
	        for (int i = 0; i < sizeofLeft; i++){
	            right.insertSorted(left.keys.get(i), left.values.get(i));
	        }
	        // change right previous point
	        right.previousLeaf = left.previousLeaf;
	        // 
	        if (left.previousLeaf != null){
	            left.previousLeaf.nextLeaf = right;
	        }
	        
	        int keyPosition = parent.children.indexOf(right) - 1;
	        
	        return keyPosition;
	        //return position
	    }
	}

	/**
	 * Handle IndexNode Underflow (merge or redistribution)
	 * 
	 * @param left
	 *            : the smaller node
	 * @param right
	 *            : the bigger node
	 * @param parent
	 *            : their parent index node
	 * @return the splitkey position in parent if merged so that parent can
	 *         delete the splitkey later on. -1 otherwise
	 */
	public int handleIndexNodeUnderflow(IndexNode<K,T> leftIndex,
			IndexNode<K,T> rightIndex, IndexNode<K,T> parent) {
	    // redistribute
	    if (leftIndex.keys.size() + rightIndex.keys.size() >= 2*D){
	        if(leftIndex.keys.size() > rightIndex.keys.size()){
	            // move children of left to right
	            int originChildrenSize = leftIndex.children.size();
	            for (int i = D + 1; i < originChildrenSize; i++){
	                Node<K, T> child = leftIndex.children.get(leftIndex.children.size() - 1);
	                rightIndex.children.add(0, child);
	                // update parent node of child
	                child.parent = rightIndex; 
	                leftIndex.children.remove(leftIndex.children.size() - 1);
	            }
	            int splittingKeyPosition = parent.children.indexOf(leftIndex);
	            K oldSplittingKey = parent.keys.get(splittingKeyPosition);
	            rightIndex.keys.add(0, oldSplittingKey);
	            K newSplittingKey = leftIndex.keys.get(D);
	            parent.keys.remove(splittingKeyPosition);
	            parent.keys.add(splittingKeyPosition, newSplittingKey);
	            // move keys to right node
	            for (int i = leftIndex.keys.size() - 1; i >= D+1; i--){
	                K key = leftIndex.keys.get(i);
	                rightIndex.keys.add(0, key);
	            }
	            // remove keys in left node
	            for (int i = leftIndex.keys.size() - 1; i>=D; i--){
	                leftIndex.keys.remove(D);
	            }
	        }
	        else{
	            // left key size  < right key size
	            // borrow one key and child from right
	            // add old splitting key to left, and replace it with new one from right node
	            int splittingKeyPosition = parent.children.indexOf(leftIndex);
	            K oldSplittingKey = parent.keys.get(splittingKeyPosition);
	            leftIndex.keys.add(oldSplittingKey);
	            K newSplittingKey = rightIndex.keys.get(0);
	            rightIndex.keys.remove(0);
	            parent.keys.remove(splittingKeyPosition);
	            parent.keys.add(splittingKeyPosition, newSplittingKey);
	            // move one child from right node
	            Node<K, T> child = rightIndex.children.get(0);
	            rightIndex.children.remove(0);
	            child.parent = leftIndex;
	            leftIndex.children.add(child);
	        }
	        return -1;
	    }
	    // merge
	    else{
	        // merge to right side 
	        int sizeofLeftChildren = leftIndex.children.size();
	        for (int i = sizeofLeftChildren - 1; i >=0; i--){
	            Node<K, T> child = leftIndex.children.get(i);
	            rightIndex.children.add(0, child);
	            child.parent = rightIndex;
	        }
	        int splittingKeyPosition = parent.children.indexOf(leftIndex);
	        K splittingKey = parent.keys.get(splittingKeyPosition);
	        rightIndex.keys.add(0, splittingKey);
	        // move rest keys in left node
	        for (int i = leftIndex.keys.size() - 1; i >=0; i--){
	            K key = leftIndex.keys.get(i);
	            rightIndex.keys.add(0, key);
	        }
	        return splittingKeyPosition;
	    }
	}

}
