//
//  RankSort.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include <iostream>
#include <omp.h>
#include "Header.h"

void ranksort(int arr[])
{
    clock_t t = clock();
    double st = omp_get_wtime();
    int s = getarraysize(arr);
    std::cout << "Size: " << s << std::endl;
    int *finallist = (int *)malloc(sizeof(int) * s);
    int rank = 0;
    int i, j;
    omp_set_num_threads(omp_get_num_procs());
    #pragma omp parallel for private(j, rank)
    for (i = 0; i < s; i++)
    {
        for (j = 1; j < s; j++)
        {
            if (arr[i] >= arr[j])
            {
                rank++;
            }
        }
        finallist[rank] = arr[i];
        rank = 0;
    }
    std::cout << "Sequential time: " << (float)(clock() - t)/CLOCKS_PER_SEC << std::endl;
    std::cout << "Parallel Time: " << (float)(omp_get_wtime() - st) << std::endl;
}

