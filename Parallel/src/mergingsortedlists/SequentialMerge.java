/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mergingsortedlists;

/**
 *
 * @author quandoanh
 */
public class SequentialMerge {    
    public static void merge(int[] arr1, int[] arr2, int[] arr3) {
        long start, end;
        start = System.currentTimeMillis();
        mergeWithWhile(arr1, arr2, arr3);
        end = System.currentTimeMillis();
        System.out.println("Sequential merge with While loop: " + (end - start) + " ms");
        start = System.currentTimeMillis();
        mergeWithBinarySearch(arr1, arr2, arr3);
        end = System.currentTimeMillis();
        System.out.println("Sequential merge with Binary Search: " + (end - start) + " ms");
    }
    
    /**
     * Using findPosition of the value in the array with divide-conquer (BinarySearch)
     * @param arr1
     * @param arr2
     * @param arr3 
     */
    public static void mergeWithBinarySearch(int[] arr1, int[] arr2, int[] arr3) {
        int pos = 0;
        for (int i = 0; i < arr1.length; i++) {
            pos = findPos(arr1[i], arr2);
            arr3[i + pos] = arr1[i];
            //System.out.println("idx of " + arr1[i] + " in arr2: " + pos + "--> idx in arr3:" + (pos + i));
        }
        
        for (int i = 0; i < arr2.length; i++) {
            pos = findPos(arr2[i], arr1);
            arr3[i + pos] = arr2[i];
            //System.out.println("idx of " + arr2[i] + " in arr1: " + pos + "--> idx in arr3:" + (pos + i));
        }
    }
    
    private static int findPos(int key, int arr[]) {
        int start = 0;
        int end = arr.length - 1;
        int index = (int) Math.floor((end - start) / 2) + start;
        if (key > arr[arr.length - 1]) { // The target is beyond the end of this array.
            index = arr.length;
        } else {
            // Start in middle, divide and conquer.
            while (start < end) {
                // Get value at current index.
                int value = arr[index];

                if (value == key) {
                    // Found our target.
                    break;
                } else if (key < value) {
                    // Target is lower in array, move the index halfway down.
                    end = index;
                } else {
                    // Target is higher in array, move the index halfway up.
                    start = index + 1;
                }
                // Get next mid-point.
                index = (int) Math.floor((end - start) / 2) + start;
            }
        }
        return index;
    }
    
    /**
     * Using While loop to merge
     * @param arr1
     * @param arr2
     * @param arr3 
     */
    public static void mergeWithWhile(int[] arr1, int[] arr2, int[] arr3) {
        int cur1 = 0;
        int cur2 = 0;
        int cur3 = 0;
        while(cur1 < arr1.length && cur2 < arr2.length) {
            if(arr1[cur1] <= arr2[cur2]) {
                arr3[cur3++] = arr1[cur1++];
            } else {
                arr3[cur3++] = arr2[cur2++];
            }
        }
        
        while (cur1 < arr1.length) {
            arr3[cur3++] = arr1[cur1++];
        }
 
        while (cur2 < arr2.length) {
            arr3[cur3++] = arr2[cur2++];
        }
    }
}
