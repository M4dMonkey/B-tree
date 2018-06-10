# BPlusTree
## Author: Wenyan Si

# Main change of Skeleton Code
# Add Parent Point in class Node
  Thus, children can know it's parent node, it's convenient for
  recursive tracking from bottom to up root.
  The way to indicate the root node is that, the parent node of
  root node is `null`.

# New function `treeSearch`
  This function is important for `search`, `insert` and `delete`.
  Because all these actions need find the right leaf node to do
  operation.    
  `treeSearch` function accept a key for searching and a start
  point node. Using recursive way to search entire tree, until
  reach the leaf node.

  But it will not check whether the key is truely contained in
  the leaf node it returned. Thus the `search` function need to
  check the whether the leaf node contains the key.

# New function `insertIndexKey`
  This function is a helper function for `insert` process.
  While inserting elements, the leaf node should split, when
  it's overflowed. It have to add new split key in it's parent
  node, index node. And it's possible that the index node overflowed
  after insert that split key. So the insert split key process need
  repeat until a parent node doesn't need split.

# New function `deleteIndexKey`
  This function is helper function for `delete`. It use recursive way
  bottom-up to delete the split key in IndexNode. Until reach the
  root node, which allowed contains less than `D` elements.
  But if root node contains `0` key, which means root only has one child.
  Then make right IndexNode as new root node(always merge to right node).
  After change right IndexNode as root, we need to change its parent as null.

# Key Ideas of Implementation
## `splitLeafNode`
After constructing the right node, need to maintaining the
`previousLeaf` and the `nextLeaf` point of origin leaf node,
new leaf node(right leaf node), and the nextLeaf of origin leaf
node,`leaf.nextLeaf.previousLeaf = rightNode`.

## `splitIndexNode`
In this function we should maintaining the parent node point
of new IndexNode


## `insert` function logic
1) find the leaf node, insert the value and key in that leaf node    
2) check `isOverflowed`    
   a) if not then done    
   b) if yes then split leaf node and insert split key to parent node    
   c) if parent node is overflowed after insertion split it    
      insert the split key to it's parent. until reach node don't need split    
3) if root splits, then create new index node for root, and make the    
     splitted nodes as its children
 
## `delete` function logic    
1) find the leaf node contains that key    
2) if cannot find then return; else delete that key and value    
  a) check the node isUnderflowed    
    i) if underflowed but its a root node, then return    
    ii) underflowed && not root    
        do redistribute or merge; if redistribute, then update the split
        key in parent node.    
    iii) if merge happened    
          delete split key in parent node. This process repeat    
          until reach the parent node don't need redistribute or merge.       
          iii-1) if root become empty after delete, then make it's    
          only child (previous rightNode, before merge happened) as root    
          node, and change it's parent as null.    


