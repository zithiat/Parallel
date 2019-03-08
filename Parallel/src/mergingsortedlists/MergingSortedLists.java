/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mergingsortedlists;

import java.util.Arrays;

/**
 *
 * @author quandoanh
 */
public class MergingSortedLists {
    private final static int n = 40000000; // 40000000
    private final static int[] arr1 = new int[n];
    private final static int[] arr2 = new int[n];
    private static int[] arr3 = new int[n * 2];
    
    
    public static void main(String[] args) {
        System.out.println(String.format("Generating arrays with %,3d items", n));
        generator();
        System.out.println("Sorting arrays ...");
        Arrays.sort(arr1);
        Arrays.sort(arr2);
//        System.out.println("Array 1");
//        printArray(arr1);
//        System.out.println("Array 2");
//        printArray(arr2);
        System.out.println("Merging arrays ...");
        
        long start, seqTimeBinary, seqTimeWhile, mergeTime;
        // With BinarySearch algorithm      
        start = System.currentTimeMillis();
        SequentialMerge.mergeWithBinarySearch(arr1, arr2, arr3);
        seqTimeBinary = System.currentTimeMillis() - start;
        System.out.println("Sequential merging with Binary: " + seqTimeBinary + " ms");
        // Reset array 3
        reset();
        
        // With While loop algorithm
        start = System.currentTimeMillis();
        SequentialMerge.mergeWithWhile(arr1, arr2, arr3);
        seqTimeWhile = System.currentTimeMillis() - start;
        System.out.println("Sequential merging with While: " + seqTimeWhile + " ms");
        // Reset array 3
        reset();
        
        // Parallel with BinarySearch algo
        start = System.currentTimeMillis();
        ParallelMerge.merge(arr1, arr2, arr3);
        mergeTime = System.currentTimeMillis() - start;
        System.out.println("Parallel merging with Binary Search: " + mergeTime + " ms");
        
        System.out.println(String.format("Speedup between Sequential (While algorithm) and Parallel: %.6f", ((double)seqTimeWhile / mergeTime)));
        System.out.println(String.format("Processor Utilization: %.6f", ((double)seqTimeWhile / mergeTime) / Runtime.getRuntime().availableProcessors()));
        System.out.println(String.format("Speedup between Sequential (BinarySearch algorithm) and Parallel: %.6f", ((double)seqTimeBinary / mergeTime)));
        System.out.println(String.format("Processor Utilization: %.6f", ((double)seqTimeBinary / mergeTime) / Runtime.getRuntime().availableProcessors()));
    }
    
    public static void generator() {
        for (int i = 0; i < n; i++) {
            arr1[i] = i * 2;
            arr2[i] = 1 + i * 2;
        }
    }
    
    public static void printArray(int[] arr) {
        for (int a : arr) {
            System.out.println(a + " ");
        }
    }
    
    public static void reset() {
        arr3 = new int[n * 2];
    }
}
