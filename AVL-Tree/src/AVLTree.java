import java.util.ArrayList;

public class AVLTree<T extends Comparable<T>> implements AVLTreeInterface<T> {

	public Node<T> root;

	/**
	 * Basic storage units in a tree. Each Node object has a left and right
	 * children fields.
	 * 
	 * If a node does not have a left and/or right child, its right and/or left
	 * child is null.
	 * 
	 */
	private class Node<E> {
		private E data;
		private Node<E> left, right; // left and right subtrees

		public Node(E data) {
			this.data = data;
		}
	}

	// CHANGES START BELOW THIS LINE
	/**
	 * size of this tree
	 */
	private int size;
	@Override
	public boolean isEmpty() {
		return root == null;
	}
	@Override
	public int size() {
		return this.size;
	}
	@Override
	public boolean contains(T element) {
		return contains(element,root);
	}
	/**
	 * Checks whether this tree contains first parameter.
	 * 
	 * This method is private because user doesn't use this method directly. 
	 * Instead, calls other contains method with only, element parameter.
	 * 
	 * @param element element to be checked for existence in the tree
	 * @param node root of the tree
	 * @return returns true if this tree contains this element
	 */
	private boolean contains(T element, Node<T> node){
		Node<T> temp = node;
		if(temp == null) return false;
		if(temp.data.compareTo(element) == 0) return true;
		return contains(element,temp.left) || contains(element,temp.right);
		
	}
	@Override
	public int height() {
		return height(root);
	}
	/**
	 * Finds the height of this tree
	 * 
	 * This method is private because user doesn't use this method directly. 
	 * Instead, calls other height method without any parameter.
	 * 
	 * @param node takes the root of this tree
	 * @return height of this tree
	 */
	private int height(Node<T> node){
		if(node == null) return -1;
		return Math.max(height(node.left), height(node.right))+1;
	}
	@Override
	public ArrayList<T> inOrderTraversal() {
		ArrayList<T> list = new ArrayList<T>();
		inOrderTraversal(root,list);
		return list;
	}
	/**
	 * Traverses the tree "in order".
	 * This means, the left subtree of the node is traversed first.
	 * Then, the node itself is visited.
	 * Then, the the right subtree is traversed.
	 * 
	 * The data in each node is added to an ArrayList as the 
	 * node is processed. In other words, this ArrayList stores the order of visiting
	 * of the nodes in the tree
	 * 
	 * This method is private because user doesn't use this method directly. 
	 * Instead, calls other inOrderTraverse method without any parameter.
	 * 
	 * @param node root of this tree
	 * @param list list that elements to be added
	 */
	private void inOrderTraversal(Node<T> node, ArrayList<T> list){
		if(node == null) return;
		if(node.left != null) inOrderTraversal(node.left,list);
		list.add(node.data);
		if(node.right != null) inOrderTraversal(node.right,list);
	}
	@Override
	public ArrayList<T> bfTraverse() {
		ArrayList<T> list = new ArrayList<T>();
		ArrayList<Node<T>> queue = new ArrayList<Node<T>>();
		if(root == null) return list;
		queue.add(root);
		bfTraverse(queue,list);
		return list;
	}
	/**
	 * Visits all the nodes in a breadth first manner. 
	 * As nodes are visited, "data" fields are added to an ArrayList.
	 * 
	 * This method is private because user doesn't use this method directly. 
	 * Instead, calls other bfTraverse method without any parameter.
	 * 
	 * @param queue arraylist to be used as a queue
	 * @param list that elements to be added
	 */
	private void bfTraverse(ArrayList<Node<T>> queue, ArrayList<T> list) {
		if(queue.size() == 0) return;
		if(queue.get(0).left != null) queue.add(queue.get(0).left);
		if(queue.get(0).right != null) queue.add(queue.get(0).right);
		list.add(queue.get(0).data);
		queue.remove(0);
		bfTraverse(queue,list);
	}
	@Override
	public boolean areCousins(T element1, T element2) {
	    if(depth(element1,root) == depth(element2,root)) 
			if(!getParent(element1).data.equals(getParent(element2).data))
				return true;
		return false;
	}
	/**
	 * Finds the depth of the tree.
	 * 
	 * Depth is the reverse of height. For example root has a 0 depth, and if a node is the child of the root,
	 * then its depth is 1, and its increasing like that.
	 * 
	 * If tree is empty, its depth is -1;
	 * 
	 * This method is private because user does not need to use it directly 
	 * 
	 * @param data element that will be searched in the tree
	 * @param node root of this tree
	 * @return depth of this tree
	 */
	private int depth(T data, Node<T> node){
		if(node == null) return -1;
		if(node.data.compareTo(data) == 0) return 0;
		int a = depth(data,node.left);
		int b = depth(data,node.right);
		if(a != b) return Math.max(a, b) + 1;
		return a;
	}
	/**
	 * Gets the parent of the node that contains this element as "data"
	 * 
	 * Also calls another getParent method that is actually finds the parent
	 * in a recursive manner
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param element child of the parent that is wanted
	 * @return parent of this element
	 */
	private Node<T> getParent(T element){
		if(root == null) return null;
		if(element.compareTo(root.data) == 0) return null;
		if(element.compareTo(root.data) < 0) return getParent(element, root.left, root);
		else return getParent(element, root.right, root);
	}
	/**
	 * Gets the parent of the node that contains this data as "data" field
	 * 
	 * This method is called by other methods to get the parent of a child node
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param data child of the parent that is wanted
	 * @param node that is searched for its parent
	 * @param parent root of the tree
	 * @return parent of this data
	 */
	private Node<T> getParent(T data, Node<T> node, Node<T> parent){
		if(node == null) return null;
		if(data.compareTo(node.data) == 0) return parent;
		if(data.compareTo(node.data) < 0) return getParent(data, node.left, node);
		else return getParent(data, node.right, node);
	}
	@Override
	public int numElementsInRange(T lower, T upper) {
		ArrayList<T> list = bfTraverse();
		int counter = 0;
		for(T element : list)
			if(element.compareTo(upper) < 0 && element.compareTo(lower) > 0 ) counter++;
		return counter;
	}
	@Override
	public int balanceFactor(T data) {
		return balanceFactor(data, root);
	}
	/**
	 * Returns the balance factor of the node that stores the data
	 * given as parameter
	 * BalanceFactor: height(leftSubtree) - height(rightSubtree)
	 * 
	 * This method is called by other methods to find balance factor
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param data
	 * @param root
	 * @return
	 */
	private int balanceFactor(T data, Node<T> root) {
		if(root == null) return -1;
		return height(getNode(data,root).left) - height(getNode(data,root).right);
	}
	/**
	 * Gets the node carries the given "data" as a field
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param data is the "data" field in the node
	 * @param node root of the tree
	 * @return node that carries the given "data" as a field
	 */
	private Node<T> getNode(T data, Node<T> node){
		if(node == null) return null;
		if(node.data.compareTo(data) == 0) return node;
		if(node.data.compareTo(data) < 0) return getNode(data, node.right);
		else return getNode(data, node.left);
	}
	@Override
	public void insert(T element) {
		if(!contains(element)) size++;
		else return;
		root = insert(element, root);
		root = balance(root);
	}
	/**
	 * Balances the tree so that the tree has a balance factor between -1 and 1.
	 * 
	 * Uses 2 rotation methods to provide desired balance state
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param node root of the tree
	 * @return root of the tree after balancing
	 */
	private Node<T> balance(Node<T> node){
		if(node == null) return null;
		if(node.left == null && node.right == null) return node;
		node.left = balance(node.left);
		node.right = balance(node.right);
		if(balanceFactor(node.data) > 1){
			if(balanceFactor(node.left.data) < 0)
				node.left = leftRotate(node.left);
			node = rightRotate(node);
		} else if(balanceFactor(node.data) < -1){
			if(balanceFactor(node.right.data) > 0)
				node.right = rightRotate(node.right);
			node = leftRotate(node);
		}
		
		return node;
	}
	/**
	 * Makes a rotation in the counter-clockwise to make the balancing operation 
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param node that will be rotated to the left
	 * @return child node after rotation
	 */
	private Node<T> leftRotate(Node<T> node){
		Node<T> temp = node;
		node = node.right;
		temp.right = node.left;
		node.left = temp;
		return node;			
	}
	/**
	 * Makes a rotation in the clockwise to make the balancing operation 
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param node that will be rotated to the right
	 * @return child node after rotation
	 */
	private Node<T> rightRotate(Node<T> node){
		
		Node<T> temp = node;
		node = node.left;
		temp.left = node.right;
		node.right = temp;
		return node;
		 
	}
	/**
	 * Inserts the element in the parameter to the tree
	 * 
	 * If tree already contains the parameter,
	 * no update is done on the tree
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param element Element to be added
	 */
	private Node<T> insert(T element, Node<T> node){
		if(node == null) return node = new Node<T>(element); 
		else{
			if(element.compareTo(node.data) < 0)  node.left = insert(element,node.left);
			else node.right = insert(element,node.right);
			return node;
		}
	}
	@Override
	public void delete(T element){
		if(contains(element)) size--;
		else return;
		Node<T> remove = getNode(element,root);
		Node<T> parent = getParent(element);
		remove = deleteNode(remove);
		// there is a problem in here
		if(remove == null){
			if(parent.right == null){
				parent.left = null;
			} else {
				if(parent.right.data.compareTo(element) == 0){
					parent.right = null;
				} else {
					parent.left = null;
				}
			}
		}
		root = balance(root);
	}
	/**
	 * Deletes the element in the parameter 
	 * 
	 * If tree does not contain the element in the parameter,
	 * no update is done on the tree
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param remove element to be removed
	 * @return node lacking deleted element
	 */
	private Node<T> deleteNode(Node<T> remove){
		if(remove.left == null && remove.right == null) return null;
		int k = balanceFactor(remove.data);
		Node<T> temp = remove;
		if(k > 0){
			temp = temp.left;
			while(temp.right != null) temp = temp.right;
			T x = temp.data;
			remove.data = x;
			remove.left = adjustLeft(remove.left);
			return remove;
		} else {
			temp = temp.right;
			while(temp.left != null) temp = temp.left;
			T x = temp.data;
			remove.data = x;
			remove.right = adjustRight(remove.right);
			return remove;
		}
	}
	/**
	 * After deletion in this tree, makes an adjustment to the right child of the deleted node
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param n	node to be adjusted
	 * @return node after adjustment
	 */
	private Node<T> adjustRight(Node<T> n) {
		if(n.left != null) n.left = adjustRight(n.left);
		else return n.right;
		return n;
	}
	/**
	 * After deletion in this tree, makes an adjustment to the left child of the deleted node
	 * 
	 * This method is private because user does not need to use it directly
	 * 
	 * @param n	node to be adjusted
	 * @return node after adjustment
	 */
	private Node<T> adjustLeft(Node<T> n) {
		if(n.right != null)	n.right = adjustLeft(n.right);
		else return n.left;
		return n;
	}	
	// CHANGES END ABOVE THIS LINE	
}