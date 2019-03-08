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
public class ParallelMergeTask implements Runnable {
    
    private final int[] arr1;
    private final int[] arr2;
    private final int[] arr3;
    @SuppressWarnings("unused")
	private final int tid;
    
    private final int startIndex;
    private final int endIndex;
    
    @SuppressWarnings("unused")
	private final int arr1Len;
    @SuppressWarnings("unused")
	private final int arr2Len;
    
    public ParallelMergeTask(int[] arr1, int[] arr2, int[] arr3, int tid, int startIndex, int endIndex) {
        this.arr1 = arr1;
        this.arr2 = arr2;
        this.arr3 = arr3;
        this.tid = tid;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.arr1Len = this.arr2Len = endIndex - startIndex;
    } 

    @Override
    public void run() {
        //System.out.println("threadId:" + tid + "---startIndex:" + startIndex + "---endIndex:" + endIndex);
        int idx = 0;
        for (int i = startIndex; i < endIndex; i++) {
            idx = findPos(arr1[i], arr2);
            //System.out.println("idx of " + arr1[i] + " in arr2: " + idx + "--> idx in arr3:" + (idx + i));
            arr3[idx + i] = arr1[i];
        }
        
        for (int i = startIndex; i < endIndex; i++) {
            idx = findPos(arr2[i], arr1);
            //System.out.println("idx of " + arr2[i] + " in arr1: " + idx + "--> idx in arr3:" + (idx + i));
            arr3[idx + i] = arr2[i];
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
}
