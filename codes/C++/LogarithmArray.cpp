//
//  LogarithmArray.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include "Header.h"

void seqlogarithm()
{
    clock_t t = clock();
    for (int i = 0; i < listnum; i++)
    {
        list1[i] = (int)log(i);
    }
    std::cout << "Sequential time: " << (float)(clock() - t)/CLOCKS_PER_SEC << std::endl;
}

void parlogarithm()
{
    double st = omp_get_wtime();
    omp_set_num_threads(omp_get_num_procs());
#pragma omp parallel for
    for (int i = 0; i < listnum; i++)
    {
        list1[i] = (int)log(i);
    }
    std::cout << "Parallel time: " << (float)(omp_get_wtime() - st) << std::endl;
}
