package heaps;

import java.util.Random;
import java.util.UUID;

import javax.swing.JOptionPane;

import heaps.util.HeapToStrings;
import heaps.validate.MinHeapValidator;
import timing.Ticker;

public class MinHeap<T extends Comparable<T>> implements PriorityQueue<T> {

	// instance variables
	private Decreaser<T>[] array;
	private int size;
	private final Ticker ticker;

	/**
	 * I've implemented this for you.  We create an array
	 *   with sufficient space to accommodate maxSize elements.
	 *   Remember that we are not using element 0, so the array has
	 *   to be one larger than usual.
	 * @param maxSize
	 */
	@SuppressWarnings("unchecked")
	// constructor
	public MinHeap(int maxSize, Ticker ticker) {
		this.array = new Decreaser[maxSize+1];
		this.size = 0;
		this.ticker = ticker;
	}

	//
	// Here begin the methods described in lecture
	//
	
	/**
	 * Insert a new thing into the heap.  As discussed in lecture, it
	 *   belongs at the end of objects already in the array.  You can avoid
	 *   doing work in this method by observing, as in lecture, that
	 *   inserting into the heap is reducible to calling decrease on the
	 *   newly inserted element.
	 *   
	 *   This method returns a Decreaser instance, which for the inserted
	 *   thing, tracks the thing itself, the location where the thing lives
	 *   in the heap array, and a reference back to MinHeap so it can call
	 *   decrease(int loc) when necessary.
	 */
	public Decreaser<T> insert(T thing) { // Decreaser is the object!
		//
		// Below we create the "handle" through which the value of
		//    the contained item can be decreased.
		// VERY IMPORTANT!
		//    The Decreaser object contains the current location
		//    of the item in the heap array.  Initially it's ++size,
		//    as shown below.  The size is increased by 1, and that's
		//    were you should store ans in the heap array.
		//
		//    If and when the element there changes location in the heap
		//    array, the .loc field of the Decreaser must change to reflect
		//    that.
		//
		Decreaser<T> ans = new Decreaser<T>(thing, this, ++size); // takes in a T object, minHeap object, and location
		// thing = thing we're inserting
		// ++size = store it at one greater than the current size, bcuz hun the current one is taken
		//
		
		// Remember the array stores Decreaser objects, not T objects
		
		// You have to now put ans into the heap array, at the end now! What's the new size again? hmmmm
		//   Recall in class we reduced insert to decrease
		//
		array[size] = ans; // put THAT ans at the end of the array!
		array[size].loc = size; // don't forget to set the location of that new object!
		decrease(size); // CALL THAT decrease man, I already hate this
		// decrease takes in argument of current location of object
		ticker.tick(5); // tick tick
		//
		return ans;
	}
	// Remember, priority queue is an ADT, it implements when you use it
	// Add physical things to the array, not the queue
	
	/**
	 * This method responds to an element in the heap decreasing in
	 * value.   As described in lecture, that element might have to swap
	 * its way up the tree so that the heap property is maintained.
	 * 
	 * This method can be called from within this class, in response
	 *   to an insert.  Or it can be called from a Decreaser.
	 *   The information needed to call this method is the current location
	 *   of the heap element (index into the array) whose value has decreased.
	 *   
	 * Really important!   If this method changes the location of elements in
	 *   the array, then the loc field within those elements must be modified 
	 *   too.  For example, if a Decreaser d is currently at location 100,
	 *   then d.loc == 100.  If this method moves that element d to
	 *   location 50, then this method must set d.loc = 50.
	 *   
	 * In my solution, I made sure the above happens by writing a method
	 *    moveItem(int start, int final)
	 * which moves the Decreaser from index "start" to index "final" and, when
	 * done, sets array[to].loc = final
	 *   
	 * This method is missing the "public" keyword so that it
	 *   is only callable within this package.
	 * @param loc position in the array where the element has been
	 *     decreased in value
	 */
	void decrease(int loc) {
		if (loc <= 1) { // the root starts at 1 actually
			ticker.tick(1);
			return;
		}
		int p = loc/2;
		T child = array[loc].getValue(); // get value T of child, the current location should be the child
		T parent = array[p].getValue(); // get value T of parent, should be current location / 2
		if (parent.compareTo(child) > 0) { // if the child is bigger than the parent
			swap(p,loc); // parent = loc/2    child = loc
			//ticker.tick(2);
			decrease(loc/2); // recursive call, do it again and again until get out of this if loop
//			
//			// remember, when swapping, always use a temp variable to store a value
//			Decreaser<T> temp = array[loc/2]; // the array stores Decreaser objects!! Store that object in temp variable
//			array[loc/2] = array[loc]; // move the child object into the parent object's place
//			array[loc] = temp; // move the parent into the child object's place
//			
//			// must update the loc variable, remember?? location is attached to the value, if the value moves, 
//																		// the location value is also messed up
//			//'val'.loc is still old location, update with new location
//			array[loc].loc = loc; //array[loc] is the value, adding another .loc describes the location
//			array[loc/2].loc = loc/2; // update new location

		}
	}
	
	/**
	 * Described in lecture, this method will return a minimum element from
	 *    the heap.  The hole that is created is handled as described in
	 *    lecture.
	 *    This method should call heapify to make sure the heap property is
	 *    maintained at the root node (index 1 into the array).
	 */
	public T extractMin() {
		T ans = array[1].getValue(); // the min/root value is always at 1!
		// store the min value somewhere else, will return that value later 
		//
		// There is effectively a hole at the root, at location 1 now.
		//    Fix up the heap as described in lecture.
		//    Be sure to store null in an array slot if it is no longer
		//      part of the active heap
		//
		array[1] = array[size]; // move the last value into the root
		array[1].loc = 1; // update the loc
		array[size] = null; // the last value slot is now null
		this.size -= 1; // logic sense to decrease total size by one
		ticker.tick(5);
		//
		if (size > 1) { // at least two nodes exist
			ticker.tick(1);
			heapify(1); // argument is always root location
		}
		ticker.tick(1);
		return ans;
	}
	
	public void swap(int parent, int child) { // locations of parent and child
		Decreaser<T> temp = array[parent];		// save parent somewhere temporary
		array[parent] = array[child];			// put child where parent was
		array[child] = temp;
		array[child].loc = child;// put parent where child was
		array[parent].loc = parent;				// update the location of parent
		//array[child].loc = child;				// update the location of child
		//ticker.tick(6);
	}

	/**
	 * As described in lecture, this method looks at a parent and its two 
	 *   children, imposing the heap property on them by perhaps swapping
	 *   the parent with the lesser of the two children.  The child thus
	 *   affected must be heapified itself by a recursive call.
	 * @param where the index into the array where the parent lives
	 */
	private void heapify(int where) { // essentially, where it is always the root, where pretty much will always = 1 at start		
		if (where * 2 > size || array[where * 2] == null) { // if the left child is greater than the actual size or there's no object there
			ticker.tick(1);
			return;
		}
		if (2 * where + 1 > size || array[2 * where + 1] == null) { // if right child if bigger than size or there's no object there
			if( array[where * 2].getValue().compareTo( array[where].getValue() ) < 0 ) { // if left child is smaller than parent
				swap(where * 2, where); // swap the child and parent
				ticker.tick(3);
			}
			return;
		}
		
		Decreaser<T> left = array[where * 2];
		Decreaser<T> right = array[where * 2 + 1];
		T parentVal = array[where].getValue();
		T leftVal = array[where * 2].getValue();
		T rightVal = array[where * 2 + 1].getValue();
		ticker.tick(5);
		 
		if (left != null) {
			if (right != null) {
				// if left is smaller than parent AND left is smaller or = to right
				if (leftVal.compareTo(parentVal) < 0 &&	leftVal.compareTo(rightVal) <= 0) {
					swap(where, where * 2); //swap parent and left child
					ticker.tick(5);
					heapify(where*2); // heapify the new child
				}
				else if (rightVal.compareTo(parentVal) < 0) { // if right is smaller than parent
					swap(where, where * 2 + 1); // swap parent and right child
					ticker.tick(3);
					heapify(where * 2 + 1); // heapify the new child
				}
			}		
		}
		else {
			ticker.tick(1);
			return;
		}
	}
	
	/**
	 * Does the heap contain anything currently?
	 * I implemented this for you.  Really, no need to thank me!
	 */
	public boolean isEmpty() {
		return size == 0;
	}
	
	//
	// End of methods described in lecture
	//
	
	//
	// The methods that follow are necessary for the debugging
	//   infrastructure.
	//
	/**
	 * This method would normally not be present, but it allows
	 *   our consistency checkers to see if your heap is in good shape.
	 * @param loc the location
	 * @return the value currently stored at the location
	 */
	public T peek(int loc) {
		if (array[loc] == null)
			return null;
		else return array[loc].getValue();
	}

	/**
	 * Return the loc information from the Decreaser stored at loc.  They
	 *   should agree.  This method is used by the heap validator.
	 * @param loc
	 * @return the Decreaser's view of where it is stored
	 */
	public int getLoc(int loc) {
		return array[loc].loc;
	}

	public int size() {
		return this.size;
	}
	
	public int capacity() {
		return this.array.length-1;
	}
	

	/**
	 * The commented out code shows you the contents of the array,
	 *   but the call to HeapToStrings.toTree(this) makes a much nicer
	 *   output.
	 */
	public String toString() {
//		String ans = "";
//		for (int i=1; i <= size; ++i) {
//			ans = ans + i + " " + array[i] + "\n";
//		}
//		return ans;
		return HeapToStrings.toTree(this);
	}

	/**
	 * This is not the unit test, but you can run this as a Java Application
	 * and it will insert and extract 100 elements into the heap, printing
	 * the heap each time it inserts.
	 * @param args
	 */
	public static void main(String[] args) {
		JOptionPane.showMessageDialog(null, "You are welcome to run this, but be sure also to run the TestMinHeap JUnit test");
		MinHeap<Integer> h = new MinHeap<Integer>(500, new Ticker());
		MinHeapValidator<Integer> v = new MinHeapValidator<Integer>(h);
		Random r = new Random();
		for (int i=0; i < 100; ++i) {
			v.check();
			h.insert(r.nextInt(1000));
			v.check();
			System.out.println(HeapToStrings.toTree(h));
			//System.out.println("heap is " + h);
		}
		while (!h.isEmpty()) {
			int next = h.extractMin();
			System.out.println("Got " + next);
		}
	}


}
