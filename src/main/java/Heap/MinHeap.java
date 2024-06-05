package Heap;

import java.util.Arrays;
public class MinHeap {
    private MinHeapNode[] Heap;
    private int index;
    private int size;

    public MinHeap(int size) {
        this.size = size;
        this.index = 0;
        Heap = new MinHeapNode[size];
    }

    public MinHeapNode[] getHeap() {
        return Heap;
    }

    public int getIndex() {
        return index;
    }

    private int parent(int i) {
        return (i - 1) / 2;
    }

    private int leftChild(int i) {
        return (i * 2) + 1;
    }

    private int rightChild(int i) {
        return (i * 2) + 2;
    }

    private boolean isLeaf(int i) {
        if (rightChild(i) >= size || leftChild(i) >= size) {
            return true;
        }
        return false;
    }

    public void insert(MinHeapNode element ) {
        if (index >= size) {
            return;
        }
        Heap[index] = element;
        int current = index;

        //if Heap[current] is lexicographically less than Heap[parent(current)]
        while (Heap[current].getValue().compareTo(Heap[parent(current)].getValue()) < 0)   {
            swap(current, parent(current));
            current = parent(current);
        }
        index++;
    }

    // removes and returns the minimum element from the heap
    public MinHeapNode remove() {
        // since its a min heap, so root = minimum
        MinHeapNode popped = Heap[0];
        if(Heap.length <= 2){

        }
        Heap[0] = Heap[--index];
        minHeapify(0);
        return popped;
    }

    // heapify the node at i
    private void minHeapify(int i) {
        // If the node is a non-leaf node and any of its child is smaller
        if (!isLeaf(i)) {

            //if Heap[i] is lexicographically greater than Heap[leftChild(i)]
            // or Heap[i] is lexicographically greater than Heap[rightChild(i)]
            if (Heap[i].getValue().compareTo(Heap[leftChild(i)].getValue()) > 0 ||
                    Heap[i].getValue().compareTo(Heap[rightChild(i)].getValue())> 0) {
                //if Heap[leftChild(i)] is lexicographically less than Heap[rightChild(i)]
                if (Heap[leftChild(i)].getValue().compareTo(Heap[rightChild(i)].getValue()) < 0) {
                    swap(i, leftChild(i));
                    minHeapify(leftChild(i));
                } else {
                    swap(i, rightChild(i));
                    minHeapify(rightChild(i));
                }
            }
        }
    }

    // builds the min-heap using the minHeapify
    public void minHeap() {
        for (int i = (index - 1 / 2); i >= 1; i--) {
            minHeapify(i);
        }
    }

    // Function to print the contents of the heap
    public void printHeap() {
        for (int i = 0; i < (index / 2); i++) {
            System.out.print("Parent : " + Heap[i]);
            if (leftChild(i) < index)
                System.out.print(" Left : " + Heap[leftChild(i)]);
            if (rightChild(i) < index)
                System.out.print(" Right :" + Heap[rightChild(i)]);
            System.out.println();
        }
    }

    // swaps two nodes of the heap
    private void swap(int x, int y) {
        MinHeapNode tmp;
        tmp = Heap[x];
        Heap[x] = Heap[y];
        Heap[y] = tmp;
    }
}
