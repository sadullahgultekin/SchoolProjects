import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		
		
		AVLTree<Integer> tree = new AVLTree<Integer>();
		tree.insert(50);
		tree.insert(10);
		if(!tree.isEmpty())
			tree.insert(100);
		tree.insert(90);
		tree.insert(5); 
		if(!tree.contains(15)) 
			tree.insert(0);
		tree.insert(-5);
		tree.insert(-70); 
		if(tree.size() > 5)
			tree.insert(8);
		tree.insert(6);
		tree.delete(0);
		if(tree.numElementsInRange(5, 50) > 3) 
			tree.delete(-5);
		tree.insert(-80);
		tree.insert(100);
		if(tree.balanceFactor(50) < 1) 
		tree.insert(110);
		tree.insert(120);
		tree.delete(50);
		tree.insert(20);
		tree.insert(25);
		if(tree.areCousins(20, 100))
			tree.delete(5);
		tree.delete(-5); 
		tree.insert(9);
		tree.delete(100);
		tree.delete(20);
		tree.insert(7);
		tree.insert(60);
		tree.insert(70);
		if(tree.height() < 5)
			tree.delete(8);
		System.out.println("BFTraverse:\n" + tree.bfTraverse());
		System.out.println("True one:\n[9, 5, 60, -70, 6, 25, 90, -80, 7, 10, 70, 120]\n");
		System.out.println("Your InorderTraversal:\n" + tree.inOrderTraversal());
		System.out.println("True one:\n" + "[-80, -70, 5, 6, 7, 9, 10, 25, 60, 70, 90, 120]");
		
		
		
		
				
		
		
	}
}