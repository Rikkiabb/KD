package s4;
/*************************************************************************
 *************************************************************************/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.introcs.In;
import edu.princeton.cs.introcs.Out;
import edu.princeton.cs.introcs.StdDraw;
import edu.princeton.cs.introcs.StdOut;


public class KdTree {
	// The root of the KdTree
	private Node root;
	
	private static class Node{
		private Point2D p;	// The point of the node
		private RectHV rect;	// The rectangle the node is in
		private Node left; 	// Left subtree of the node
		private Node right;	// Right subtree of the node
		private boolean redBlack; 	// Two know if the node is horizontal or vertical
		//false stands for horizontal and true stands for vertical
        
		public Node(Point2D p, RectHV rect, Node left, Node right, boolean redBlack) {
            this.p = p;
            this.rect = rect;
            this.left = left;
            this.right = right;
            this.redBlack = redBlack;
        }
	}
    // construct an empty set of points
    public KdTree() {
    	root = null;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size() == 0;
    }

    // number of points in the set
    // Calls a recursive function that returns size
    public int size() {
    	return size(root);
    }
    
    // If node is null then we have search all the tree otherwise we keep counting
    private int size(Node node){
    	if(node == null)	return 0;
    	return 1 + size(node.left) + size(node.right);
    }

    // add the point p to the set (if it is not already in the set)
    // If its the first point then we insert it otherwise we call a recursive function
    public void insert(Point2D p) {
    	if(root == null){
    		RectHV newRect = new RectHV(0, 0, 1, 1);
    		Node newNode = new Node(p, newRect, null, null, false);
    		root = newNode;
    		return;
    	}
    	else insert(p, root);
    };
    
    private void insert(Point2D p, Node node){
    	// If the tree has the point then we return
    	if(node.p.equals(p)) return;    
    	if(node.redBlack == false){	// False = vertical
			boolean newRedBlack = true;
    		if(p.x() < node.p.x()){	// go left
    			if(node.left == null){	// Insert into left child
    					RectHV newRect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.p.x(), node.rect.ymax());
    					Node newNode = new Node(p, newRect, null, null, newRedBlack);
    					node.left = newNode;
    					return;
    			}
    			else{	// Continue searching
    				insert(p, node.left);
    			}
    		}
    		else{	// go right
    			if(node.right == null){	// Insert into right child
    				RectHV newRect = new RectHV(node.p.x(), node.rect.ymin(), node.rect.xmax(), node.rect.ymax());
    				Node newNode = new Node(p, newRect, null, null, newRedBlack);
    				node.right = newNode;
    				return;
   				}
    			else{	// Continue searching
    				insert(p, node.right);
    			}
    		}
    	}
    	else{	// Current node is on horizontal axis
			boolean newRedBlack = false;
    		if(p.y() < node.p.y()){	// go left
    			if(node.left == null){	// Insert into left child
       				RectHV newRect = new RectHV(node.rect.xmin(), node.rect.ymin(), node.rect.xmax(), node.p.y());
    				Node newNode = new Node(p, newRect, null, null, newRedBlack);
    				node.left = newNode;
    				return;
    			}
    			else{	// Continue searching
    				insert(p, node.left);
    			}
    		}
    		else{	// go right
    			if(node.right == null){	// Insert into right child
    				RectHV newRect = new RectHV(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.rect.ymax());
    				Node newNode = new Node(p, newRect, null, null, newRedBlack);
    				node.right = newNode;
    				return;
    			}
    			else{	// Go right
    				insert(p, node.right);
    			}
    		}
    	}
    }

    // does the set contain the point p?
    // Search recursively for the point if the tree isn't empty
    public boolean contains(Point2D p) {
        if(root == null){
        	return false;
        }
        else{
        	return contains(p, root);
        }
    }
    
    private boolean contains(Point2D p, Node node){
    	// We have searched through the tree and not found the point
    	if(node == null){
    		return false;
    	}
    	// We have found the point
    	if(p.equals(node.p)){
    		return true;
    	}
    	else{
    		if(node.redBlack == false){	// Current node is on vertical axis
    			if(p.x() < node.p.x()){	// Search left
    				return contains(p, node.left);
    			}
    			else{	// Search right
    				return contains(p, node.right);
    			}
    		}
    		else{	// Current node is on horizontal axis
    			if(p.y() < node.p.y()){	// Search left
    				return contains(p, node.left);
    			}
    			else{	// Search right
    				return contains(p, node.right);
    			}
    		}
    	}
    }

    // draw all of the points to standard draw
    // The rectangle is always the same, we start with drawing it then we draw recursively
    public void draw() {
    	StdDraw.rectangle(0.5, 0.5, 0.5, 0.5);
    	draw(root);
    }
    
    private void draw(Node node){
    	// We have drawn all the points and lines
    	if(node == null)	return;
    	if(node.redBlack == false){	// Point is on vertical axis and then the line is red
    		StdDraw.setPenColor(255, 0, 0);
    		StdDraw.filledCircle(node.p.x(), node.p.y(), 0.005);
    		StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
    	}
    	else{	// Else the color is blue
    		StdDraw.setPenColor(0, 0, 255);
    		StdDraw.filledCircle(node.p.x(), node.p.y(), 0.005);
    		StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
    	}
    	// Draw recursively
    	draw(node.left);
    	draw(node.right);
    	
    }

    // all points in the set that are inside the rectangle
    // Create a set and call a recursive function
    public Iterable<Point2D> range(RectHV rect) {
    	SET<Point2D> insideSet = new SET<Point2D>();
    	return range(rect, root, insideSet);
    }
    
    private Iterable<Point2D> range(RectHV rect, Node node, SET<Point2D>insideSet){
    	// Have looked at all points we have to look at
    	if(node == null){
    		return insideSet;
    	}
    	// If it intersect then it is possible the point is in range
    	if(rect.intersects(node.rect)){
    		// The range contains the point and we add it
    		if(rect.contains(node.p)){
    			insideSet.add(node.p);
    		}
    		// Search recursively for other possible points
    		range(rect, node.left, insideSet);
    		range(rect, node.right, insideSet);
    	}
    	return insideSet;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
    	// Current nearest point is the root, then we call a recursive function
    	Point2D nearestPoint = root.p;
        return nearest(p, root, nearestPoint);
    }
    
    private Point2D nearest(Point2D p, Node node, Point2D nearestPoint){
    	// We have search all the tree
    	if(node == null){
    		return nearestPoint;
    	}
    	// First we check the distance from the current nearest point to the rectangle we are checking
    	if((node.rect.distanceSquaredTo(p)) < (nearestPoint.distanceSquaredTo(p))){
    		// Then we check the distance from the points
    		if(p.distanceSquaredTo(node.p) < (nearestPoint.distanceSquaredTo(p))){
    			// If it is shorter then we remember the node
    			nearestPoint = node.p;
    		}
    		// Return the nearest point up, so we remember it
			nearestPoint = nearest(p, node.left, nearestPoint);
			nearestPoint = nearest(p, node.right, nearestPoint);
    	}
    	 	
    	return nearestPoint;
    }

    /*******************************************************************************
     * Test client
     ******************************************************************************/
    public static void main(String[] args) {
        In in = new In();
        Out out = new Out();
        int N = in.readInt(), C = in.readInt(), T = 20;
        KdTree tree = new KdTree();
        Point2D [] points = new Point2D[C];
        out.printf("Inserting %d points into tree\n", N);
        for (int i = 0; i < N; i++) {
            tree.insert(new Point2D(in.readDouble(), in.readDouble()));
        }
        out.printf("tree.size(): %d\n", tree.size());
        out.printf("Testing contains method, querying %d points\n", C);
        for (int i = 0; i < C; i++) {
            points[i] = new Point2D(in.readDouble(), in.readDouble());
            out.printf("%s: %s\n", points[i], tree.contains(points[i]));
        }
        for (int i = 0; i < T; i++) {
            for (int j = 0; j < C; j++) {
                tree.contains(points[j]);
            }
        }
        tree.draw();
    }
}

