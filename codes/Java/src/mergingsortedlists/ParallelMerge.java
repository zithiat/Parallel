/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mergingsortedlists;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author quandoanh
 */
public class ParallelMerge {
    
    public static void merge(int[] arr1, int[] arr2, int[] arr3) {
        List<Thread> threads = new ArrayList<>();
        
        int numThreads = Runtime.getRuntime().availableProcessors();
        int startIndex, endIndex, step;
        step = arr1.length / numThreads;
        startIndex = 0;
        endIndex = step;
        
        for (int i = 0; i < numThreads; i++) {
            ParallelMergeTask task = new ParallelMergeTask(arr1, arr2, arr3, i, startIndex, endIndex);
            Thread thread = new Thread(task);
            thread.start();
            threads.add(thread);
            startIndex = endIndex;
            endIndex = i == numThreads - 2 ? arr1.length : endIndex + step;
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
