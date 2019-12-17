
import java.util.Arrays;

/**
     *By students: tslilchen 313170987 and lihinahir 205814957 
	 * FibonacciHeap
	 *
	 * An implementation of fibonacci heap over non-negative integers.
	 */
	public class FibonacciHeap
	{
		public HeapNode min;
		public int size;
		public int marked;
		public int trees;
		public static int totalLinks;      		
		public static int totalCuts;   			
		

	   /**
	    * public boolean empty()
	    *
	    * precondition: none
	    * 
	    * The method returns true if and only if the heap
	    * is empty.
	    *   
	    */
	    public boolean empty()
	    {
	    	return min==null;
	    }
			
	   /**
	    * public HeapNode insert(int key)
	    *
	    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap. 
	    */
	    public HeapNode insert(int key)
	    {
	    	size++;
	    	trees++;
	    	HeapNode newNode=new HeapNode(key);
	    	if(empty()) {//insert first node
	    		min=newNode;
	    		min.left=min;
	    		min.right=min;
	    	}
	    	else { 		//insert as min.right
	        	min.insertRight(newNode);
	        	if(key<min.getKey()) {
	        		min=newNode;
	        	}
	    	}
	    	return newNode; 
	    }
	    

	   
	    
	   /**
	    * public void deleteMin()
	    *
	    * Delete the node containing the minimum key.
	    *
	    */
	    public void deleteMin()
	    {
	    	trees=trees+min.rank-1;
	    	if(size==1) {
	    		deleteLast();
	    	}
	    	else {
	    		size--;
	    		findNewMinAndDelete(); 
	    	}
	    }
	    
	    
	    
	    public void deleteLast() {
	    	size--;
	    	min=null;
	    }
	    
	    /**
		    * public void findNewMinAndDelete()
		    *assumes min has at least on sibling or at least one child
		    * Delete the node containing the minimum key and find the new minimum
		    *
		    */
	  
	    public void findNewMinAndDelete() {
	    	
	    	if(!isOnlyInLevel(min)) {// min has siblings
	    		HeapNode lastMin=min;
	    		if(min.child!=null) {//min has a child
	    			HeapNode childLeft=min.child.left;
	    			min=min.child;
	    			lastMin.left.setRight(min);
	    			childLeft.setRight(lastMin.right);
	    		}
	    		else {				// min does not have a child
	    			min=min.right;
	    		}
	    		lastMin.skip();
	    		consolidate(min);
	    	}
	    	else {// min does not have siblings (min has a child)
	    		min=min.child;
	    		consolidate(min);
	    	}
	    		
	    }
	    
	    /**
		    * public boolean isOnlyInLevel(HeapNode node) 
		    *return true iff node has no siblings
		    * 
		    *
		    */	    
	    public boolean isOnlyInLevel(HeapNode node) {
	    	return node.right==node;
	    		
	    }
	    
	    
	    
	    /**
		    * public HeapNode isolateRoot(HeapNode root)
		    *disconnect node from his parent and siblings and retrun it
		    * 
		    *
		    */	 
	    
	    public HeapNode isolateRoot(HeapNode root) {
	    	root.parent=null;
	    	root.skip();
	    	root.left=root;
	    	root.right=root;
	    	return root;
	    	
	    }
	    
	    
	    /**
		    * public void consolidate(HeapNode startNode)
		    *create an array containing at most one tree of each possible rank
		    * 
		    *
		    */	
	    
	    public void consolidate(HeapNode startNode){
	    	HeapNode curr=startNode;
	    	HeapNode isolated;
	    	HeapNode next;
	    	HeapNode[] array=new HeapNode[(int)(Math.log(size()) / Math.log(2))+1];
	    	while(curr.right!=curr) {
	    		if(curr.mark) {
	    			curr.mark=false;
	    			marked--;
	    		}
	    		next=curr.right;
	    		isolated=isolateRoot(curr);
	    		consolidateRec(array,isolated);
	    		curr=next;
	    	}
			consolidateRec(array,curr);
			buildRootList(array);
		}
	    
	    /**
		    * public void consolidateRec(HeapNode[] array, HeapNode curr)
		    *link tree of the same rank into one tree recursively
		    * 
		    *
		    */
	    public void consolidateRec(HeapNode[] array, HeapNode curr) {
	    	if(curr.key<min.key) {
	    		min=curr;
	    	}
	    	
	    	if(array[curr.rank]!=null) {
	    		HeapNode newTree;
	    		HeapNode existingTree=array[curr.rank];
	    		array[curr.rank]=null;
				newTree=link(curr,existingTree);
				consolidateRec(array,newTree);
			}
	    	else {
	    		array[curr.rank]=curr;
	    	}
	    }
	    
	    
	    /**
		    * public HeapNode link(HeapNode root1, HeapNode root2)
		    *link tree of the same rank into one tree
		    * 
		    *
		    */
	    public HeapNode link(HeapNode root1, HeapNode root2) {
	    	totalLinks++;  
	    	if (root1.key < root2.key) {
	    		return linkToRoot(root1,root2);
	    	}
	    	else {
	    		return linkToRoot(root2,root1);
	    	}
	    }
		
	    /**
		    * public HeapNode linkToRoot(HeapNode root, HeapNode newChild)
		    *add newChild to the children list of root
		    * 
		    *
		    */	    
	    
	    public HeapNode linkToRoot(HeapNode root, HeapNode newChild) {
	   		if(root.rank==0) {	//the new root has no children
				root.setChild(newChild);
				root.rank++;
				if(newChild.mark) {
					marked--;
				}
				newChild.mark=false;
				return root;
			}
			else {			
				HeapNode oldChild=root.child;
				root.setChild(newChild);
				newChild.setLeft(oldChild.left);
				newChild.setRight(oldChild);
				root.rank++;
				return root;
			}
	    }
	    
	    
	    /**
		    * public void buildRootList(HeapNode[] array)
		    *rebuild the heap from the tree in array, creating a heap containing at most one tree of each rank 
		    * 
		    *
		    */
	    
	    public void buildRootList(HeapNode[] array) {
	    	HeapNode last=min;
	    	trees=1;
	    	for (HeapNode root : array) {
	    		
				if (root!=null&&root!=min) {
					last.setRight(root);
					root.setLeft(last);
					last=root;
					trees++;
				}
	    	}
	    	
	    	last.setRight(min);
	    	min.setLeft(last);
	    }
	    
	    
	    	
	  

	   /**
	    * public HeapNode findMin()
	    *
	    * Return the node of the heap whose key is minimal. 
	    *
	    */
	    public HeapNode findMin()
	    {
	    	if(empty()) {
	    		return null;
	    	}
	    	return min;
	    } 
	    
	   /**
	    * public void meld (FibonacciHeap heap2)
	    *
	    * Meld the heap with heap2
	    *
	    */
	    public void meld (FibonacciHeap heap2)
	    {
	    	trees+=heap2.trees;
	    	this.size+=heap2.size();
	    	if(this.empty()&&!heap2.empty()) {
	    		min=heap2.min;
	    	}
	    	else if(!this.empty()&&!heap2.empty()) {
	    	HeapNode last2=heap2.min.left;
    		HeapNode rightOfMin1=this.min.right;
    		this.min.setRight(heap2.min);
    		rightOfMin1.setLeft(last2);
	    	if(heap2.min.key<this.min.key) {
	    		this.min=heap2.min;
	    	  }
	    	}
	    }

	   /**
	    * public int size()
	    *
	    * Return the number of elements in the heap
	    *   
	    */
	    public int size()
	    {
	    	return size; 
	    }
	    	
	    /**
	    * public int[] countersRep()
	    *
	    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
	    * 
	    */
	    public int[] countersRep() {
	    int[] array=new int[(int)(Math.log(size()) / Math.log(2))+1];
	    int indexOfMaxRank=0;
	    array[min.rank]++;
	    if (isOnlyInLevel(min)) {
	    	indexOfMaxRank=min.rank;
	    }
	    HeapNode curr = min.right;
	    while (curr!=min) {
	    	array[curr.rank]++;
	    	if(curr.rank>indexOfMaxRank) {
	    		indexOfMaxRank=curr.rank;
	    	}
	    	curr=curr.right;
	     }
	     array = Arrays.copyOfRange(array, 0, indexOfMaxRank+1);
	     return array;
	    }
		
	   /**
	    * public void delete(HeapNode x)
	    *
	    * Deletes the node x from the heap. 
	    *
	    */
	    public void delete(HeapNode x) 
	    {    
	    	decreaseKey(x, x.key-min.key+1);
	    	deleteMin();
	    }

	   /**
	    * public void decreaseKey(HeapNode x, int delta)
	    *
	    * The function decreases the key of the node x by delta. The structure of the heap should be updated
	    * to reflect this chage (for example, the cascading cuts procedure should be applied if needed).
	    */
	    public void decreaseKey(HeapNode x, int delta)
	    {    
	    	if(x.parent==null) {
	    		x.key-=delta;
	    	}
	    	else{
	    		x.key-=delta;
	    		if(x.key<x.parent.key) {
	    			HeapNode parent=x.parent;
	    			cut(x);
	    			cascadingCut(parent);
	    		}
	    	
	    	}
	    	if(x.key<min.key) {
	    		min=x;
	    	}
	    }
	    
	    
	    /**
		    * public void cut(HeapNode node)
		    *
		    * cut node from his parent and add it to the root list of the heap
		    */
	    public void cut(HeapNode node) {
	    	totalCuts++;       
	    	trees++;
	    	node.parent.rank--;
	    	if(node.mark) {
		    	marked--;
	    	}
	    	node.mark=false;
	    	if(node.parent.child==node) {
	    		if(isOnlyInLevel(node)) {
	    			node.parent.child=null;
	    			node.parent=null;
	    			min.insertRight(node);
	    		}
	    		else {
	    			node.parent.child=node.right;
	    			isolateRoot(node);
	    			min.insertRight(node);
	    		}
	    	}
	    	else {
	    		isolateRoot(node);
	    		min.insertRight(node);
	    	}
	    }
	    
	    
	    /**
		    * public void cascadingCut(HeapNode parent)
		    *
		    * if parent is marked, cut it recursively from it's parent
		    */
	    
	    public void cascadingCut(HeapNode parent) {
	    	HeapNode grandpa=parent.parent;
	    	if(parent.mark) {
	    		cut(parent);
	    		cascadingCut(grandpa);
	    	}
	    	else{
	    		if(parent.parent!=null) {
	    			parent.mark=true;
		    		marked++;
	    		}
	    		
	    	}
	    }
	    	
	    	

	   /**
	    * public int potential() 
	    *
	    * This function returns the current potential of the heap, which is:
	    * Potential = #trees + 2*#marked
	    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
	    */
	    public int potential() 
	    {    
	    	return 2*marked+trees;
	    }

	   /**
	    * public static int totalLinks() 
	    *
	    * This static function returns the total number of link operations made during the run-time of the program.
	    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
	    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
	    * in its root.
	    */
	    public static int totalLinks() {     
	    	return totalLinks; 
	    }

	   /**
	    * public static int totalCuts() 
	    *
	    * This static function returns the total number of cut operations made during the run-time of the program.
	    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
	    */
	    public static int totalCuts() {    
	    	return totalCuts;
	    }
	    
	   /**
	    * public class HeapNode
	    * 
	    * If you wish to implement classes other than FibonacciHeap
	    * (for example HeapNode), do it in this file, not in 
	    * another file 
	    *  
	    */
	    public class HeapNode{

		public int key;
		public int rank;
		public boolean mark;
		public HeapNode child;
		public HeapNode right;
		public HeapNode left;
		public HeapNode parent;
		public int size;
		
		
		

	  	public HeapNode(int key) {
		    this.key = key;
		    this.mark=false;
		    this.child=null;
		    this.parent=null;
		    this.left=this;
		    this.right=this;
		    this.size=1;
	      }
	  	
	  	
	  	/**
		    * public int getKey()
		    * 
		    * return the key of the node 
		    *  
		    */

	  	public int getKey() {
		    return this.key;
	      }

	  	
	  	
		/**
		    * public void skip()
		    * 
		    * remove the node from the list by skipping it
		    *  
		    */
	  	 public void skip() {
	  		 this.left.setRight(this.right);
	  		 this.right.setLeft(this.left);
	  	 }
	  	 
	  	 
	  	 
	 	/**
		    * public void insertLeft(HeapNode newNode)
		    * 
		    * insert newNode into an existing list as this.left
		    *  
		    */
	  	
	  	 public void insertLeft(HeapNode newNode) {
	  		 if (isOnlyInLevel(this)) {
	  			 newNode.right=this;
	  	  		 newNode.left=this;
	  	  		 this.right=newNode;
	  	  		 this.left=newNode;
	  		 }
	  		 else {
	  			newNode.left=this.left;
	  	  		newNode.right=this;
	  	  		newNode.left.right=newNode;
	  	  		this.left=newNode;
	  		 }
	  		
	  		
	  	 }
	  	 
	  	/**
		    * public void insertRight(HeapNode newNode)
		    * 
		    * insert newNode into an existing list as this.right
		    *  
		    */
	  	 
	  	 public void insertRight(HeapNode newNode) {
	  		 if (isOnlyInLevel(this)) {
	  			 newNode.right=this;
	  	  		 newNode.left=this;
	  	  		 this.right=newNode;
	  	  		 this.left=newNode;
	  		 }
	  		 else {
	  			newNode.right=this.right;
	  	  		 newNode.left=this;
	  	  		 newNode.right.left=newNode;
	  	  		 this.right=newNode;
	  		 }
	  		 
	  		 
	  	 }
	  	 
	  	 
	  	 
	  	/**
		    *  public void setChild(HeapNode newChild)
		    * 
		    * set a new child to the current node
		    *  
		    */
	  	 
	  	 public void setChild(HeapNode newChild) {
	  		 this.child=newChild;
	  		 newChild.parent=this;
	  	 }
	  	 
	  	 
	 	/**
		    * public void setRight(HeapNode newRight)
		    * 
		    * set a new right to the current node
		    *  
		    */
	  	
	  	 public void setRight(HeapNode newRight) {
	  		 this.right=newRight;
	  		 newRight.left=this;
	  	 }
	  	 
	  	 
	  	 
	  	/**
		    * public void setLeft(HeapNode newLeft)
		    * 
		    * set a new left to the current node
		    *  
		    */
	  	 public void setLeft(HeapNode newLeft) {
	  		 this.left=newLeft;
	  		 newLeft.right=this;
	  	 }
	  	 
	  	 
	  	 
	    }
	}
