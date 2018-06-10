import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Test;

public class MyTest {
    
    @Test
    public void testDeleteKeyRandomly(){
        ArrayList<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 200; i++){
            numbers.add(i);
        }
        System.err.println(numbers.size());
        BPlusTree<Integer, Integer> tree = new BPlusTree<>();
        for (int i = 0; i< 200; i++){
            int randomIndex = ThreadLocalRandom.current().nextInt(0, numbers.size());
            //System.err.println("rrrrr: " + numbers.get(randomIndex));
            tree.insert(numbers.get(randomIndex), numbers.get(randomIndex));
            numbers.remove(randomIndex);
        }
        Utils.printTree(tree);
        numbers.clear();
        for (int i = 0; i < 200; i++){
            numbers.add(i);
        }
        for (int i = 0; i < 200; i++){
            int randomIndex = ThreadLocalRandom.current().nextInt(0, numbers.size());
            tree.delete(numbers.get(randomIndex));
            numbers.remove(randomIndex);
            Utils.printTree(tree);
        }
        
    }
    
    @Test
    public void testTTTT(){
        Integer primeNumbers[] = new Integer[] {  16 ,  13 ,  4 ,  0 ,  11 ,  7 ,  3 ,  2 ,  5 ,  19 ,  1 ,  12 ,  10 ,  18 ,  15 ,  9 ,  17 ,  6 ,  14 ,  8 };
        // create tree
        BPlusTree<Integer, Integer> tree = new BPlusTree<>();
        for (int i = 0; i< 20; i++){
            tree.insert(primeNumbers[i], primeNumbers[i]);
        }
        Utils.printTree(tree);
        for (int i = 0; i < 20; i++){
            System.out.println(19-i);
            tree.delete(19-i);
            Utils.printTree(tree);
            
        }
        
    }
    
    //@Test
    public void testLargeTreeDelete() {
      BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>();
      int n=190;
      for (int i = n/2; i >=0; i--) {
        tree.insert(i, i);
      }
      for (int i = n/2+1; i <n; i++) {
            tree.insert(i, i);
          }
      Utils.printTree(tree);
      for (int i = 0; i <n; i++) {
          //System.out.println(i);

          tree.delete(i);
          Utils.printTree(tree);
        }
      String test = Utils.outputTree(tree);
      Utils.printTree(tree);
      String result = "[]$%%";
      assertEquals(result, test);
    }
    
    @Test
    public void testHandleLeafNodeUnderflow(){
        
        LeafNode<Integer, Integer> left = new LeafNode<Integer, Integer>(22, 22);
//        left.insertSorted(24, 24);
//        left.insertSorted(25, 25);
        LeafNode<Integer, Integer> right = new LeafNode<Integer, Integer>(27, 27);
        right.insertSorted(29, 29);
        // right.insertSorted(28, 28);
        
        LeafNode<Integer, Integer> third = new LeafNode<Integer, Integer>(33, 33);
        third.insertSorted(34, 34);
        third.insertSorted(38, 38);
        IndexNode<Integer, Integer> parent = new IndexNode<>(27, left, right);
        parent.insertSorted(new SimpleEntry(30, third), 1);
        right.previousLeaf = left;
        left.nextLeaf = right;
        left.parent = parent;
        right.parent = parent;
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>();
        int index = tree.handleLeafNodeUnderflow(left, right, parent);
        System.out.println(index);
        
    }
    
    @Test
    public void testHandleIndexNodeUnderflowed(){
        // construct tree for test
        LeafNode<Integer, Integer> leaf1 = new LeafNode<Integer, Integer>(2, 2);
        leaf1.insertSorted(3, 3);
        LeafNode<Integer, Integer> leaf2 = new LeafNode<Integer, Integer>(5, 5);
        leaf2.insertSorted(7, 7);
        leaf2.insertSorted(8, 8);
        LeafNode<Integer, Integer> leaf3 = new LeafNode<Integer, Integer>(14, 14);
        leaf3.insertSorted(16, 16);
        LeafNode<Integer, Integer> leaf4 = new LeafNode<Integer, Integer>(22,22);
        leaf4.insertSorted(27, 27);
        leaf4.insertSorted(29, 29);
        LeafNode<Integer, Integer> leaf5 = new LeafNode<Integer, Integer>(33, 33);
        leaf5.insertSorted(34, 34);
        leaf5.insertSorted(38, 38);
        leaf5.insertSorted(39, 39);
        leaf1.nextLeaf = leaf2; leaf2.previousLeaf = leaf1; leaf2.nextLeaf = leaf3;
        leaf3.previousLeaf = leaf2; leaf3.nextLeaf = leaf4; leaf4.previousLeaf = leaf3;
        leaf4.nextLeaf = leaf5; leaf5.previousLeaf = leaf4; 
        
        ArrayList<Integer> leftkeys = new ArrayList<>();
        leftkeys.add(5); leftkeys.add(13);
        ArrayList<Node<Integer, Integer>> leftNodeChilds = new ArrayList<>();
        leftNodeChilds.add(leaf1); leftNodeChilds.add(leaf2); leftNodeChilds.add(leaf3 );
        IndexNode<Integer, Integer> left = new IndexNode<>(leftkeys, leftNodeChilds);
        
        IndexNode<Integer, Integer> right = new IndexNode<>(30, leaf4, leaf5);
        
        IndexNode<Integer, Integer> parent = new IndexNode<>(17, left, right);
        left.parent = parent;
        right.parent = parent;
        // -----end construction
        
        BPlusTree<Integer, Integer> tree = new BPlusTree<>();
        int index = tree.handleIndexNodeUnderflow(left, right, parent);
        System.out.println(index);
        
    }
    
    @Test
    public void testIndexNodeRedistribute(){
        // construct tree for test
        LeafNode<Integer, Integer> leaf1 = new LeafNode<Integer, Integer>(2, 2);
        leaf1.insertSorted(3, 3);
        LeafNode<Integer, Integer> leaf2 = new LeafNode<Integer, Integer>(5, 5);
        leaf2.insertSorted(7, 7);
        leaf2.insertSorted(8, 8);
        LeafNode<Integer, Integer> leaf3 = new LeafNode<Integer, Integer>(14, 14);
        leaf3.insertSorted(16, 16);
        LeafNode<Integer, Integer> leaf3_3 = new LeafNode<Integer, Integer>(17, 17);
        leaf3_3.insertSorted(18, 18);
        leaf3_3.insertSorted(19, 19);
        
        LeafNode<Integer, Integer> leaf4 = new LeafNode<Integer, Integer>(22,22);
        leaf4.insertSorted(27, 27);
        leaf4.insertSorted(29, 29);
        LeafNode<Integer, Integer> leaf5 = new LeafNode<Integer, Integer>(33, 33);
        leaf5.insertSorted(34, 34);
        leaf5.insertSorted(38, 38);
        leaf5.insertSorted(39, 39);
        leaf1.nextLeaf = leaf2; leaf2.previousLeaf = leaf1; leaf2.nextLeaf = leaf3;
        leaf3.previousLeaf = leaf2; leaf3.nextLeaf = leaf4; leaf4.previousLeaf = leaf3;
        leaf4.nextLeaf = leaf5; leaf5.previousLeaf = leaf4; 
        
        ArrayList<Integer> leftkeys = new ArrayList<>();
        leftkeys.add(5); leftkeys.add(13); leftkeys.add(17);
        ArrayList<Node<Integer, Integer>> leftNodeChilds = new ArrayList<>();
        leftNodeChilds.add(leaf1); leftNodeChilds.add(leaf2); leftNodeChilds.add(leaf3 );
        leftNodeChilds.add(leaf3_3);
        IndexNode<Integer, Integer> left = new IndexNode<>(leftkeys, leftNodeChilds);
        
        IndexNode<Integer, Integer> right = new IndexNode<>(30, leaf4, leaf5);
        
        IndexNode<Integer, Integer> parent = new IndexNode<>(20, left, right);
        left.parent = parent;
        right.parent = parent;
        // -----end construction
        
        BPlusTree<Integer, Integer> tree = new BPlusTree<>();
        tree.root = parent;
        int index = tree.handleIndexNodeUnderflow(left, right, parent);
        System.out.println(index);
        Integer a = tree.search(17);
        System.out.println(a);
    }
    
    @Test
    public void testBuildTree() {
        System.out.println("\n testSimpleHybrid");
        Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        String alphabetStrings[] = new String[alphabet.length];
        for (int i = 0; i < alphabet.length; i++) {
            alphabetStrings[i] = (alphabet[i]).toString();
        }
        BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
        Utils.bulkInsert(tree, alphabet, alphabetStrings);

        String test = Utils.outputTree(tree);
        String correct = "@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
        if (correct.equals(test)){
            System.out.println("success");
        }
        assertEquals(correct, test);
        
        
    }
    @Test
    public void testBuildTree2(){
        Integer primeNumbers[] = new Integer[] { 2, 4, 5, 7, 8, 9, 10, 11, 12,
                13, 14, 15, 16 };
        String primeNumberStrings[] = new String[primeNumbers.length];
        for (int i = 0; i < primeNumbers.length; i++) {
            primeNumberStrings[i] = (primeNumbers[i]).toString();
        }
        BPlusTree<Integer, String> tree = new BPlusTree<Integer, String>();
        Utils.bulkInsert(tree, primeNumbers, primeNumberStrings);

        String test = Utils.outputTree(tree);
        String correct = "@10/@%%@5/8/@@12/14/@%%[(2,2);(4,4);]#[(5,5);(7,7);]#[(8,8);(9,9);]$[(10,10);(11,11);]#[(12,12);(13,13);]#[(14,14);(15,15);(16,16);]$%%";
        Object object = tree.search(8);
        assertEquals(test, correct);
        assertEquals(tree.search(8), "8");
    }
    
    @Test
    public void testSearch() {
        System.out.println("\n testSimpleHybrid");
        Character alphabet[] = new Character[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g' };
        String alphabetStrings[] = new String[alphabet.length];
        for (int i = 0; i < alphabet.length; i++) {
            alphabetStrings[i] = (alphabet[i]).toString();
        }
        BPlusTree<Character, String> tree = new BPlusTree<Character, String>();
        Utils.bulkInsert(tree, alphabet, alphabetStrings);

        String test = Utils.outputTree(tree);
        String correct = "@c/e/@%%[(a,a);(b,b);]#[(c,c);(d,d);]#[(e,e);(f,f);(g,g);]$%%";
        Object object = tree.search('a');
        System.out.println(object);
    }
    
 // Testing appropriate depth and node invariants on a big tree
    @Test
    public void testLargeTree() {
        BPlusTree<Integer, Integer> tree = new BPlusTree<Integer, Integer>();
        ArrayList<Integer> numbers = new ArrayList<Integer>(100000);
        for (int i = 0; i < 100000; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        for (int i = 0; i < 100000; i++) {
            tree.insert(numbers.get(i), numbers.get(i));
        }
        testTreeInvariants(tree);
        int depth = treeDepth(tree.root);
        assertTrue(depth < 11);
    }

    public <K extends Comparable<K>, T> void testTreeInvariants(
            BPlusTree<K, T> tree) {
        for (Node<K, T> child : ((IndexNode<K, T>) (tree.root)).children)
            testNodeInvariants(child);
    }

    public <K extends Comparable<K>, T> void testNodeInvariants(Node<K, T> node) {
        assertFalse(node.keys.size() > 2 * BPlusTree.D);
        assertFalse(node.keys.size() < BPlusTree.D);
        if (!(node.isLeafNode))
            for (Node<K, T> child : ((IndexNode<K, T>) node).children)
                testNodeInvariants(child);
    }

    public <K extends Comparable<K>, T> int treeDepth(Node<K, T> node) {
        if (node.isLeafNode)
            return 1;
        int childDepth = 0;
        int maxDepth = 0;
        for (Node<K, T> child : ((IndexNode<K, T>) node).children) {
            childDepth = treeDepth(child);
            if (childDepth > maxDepth)
                maxDepth = childDepth;
        }
        return (1 + maxDepth);
    }

}
