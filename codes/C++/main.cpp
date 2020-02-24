//
//  main.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include <iostream>
#include <omp.h>
#include "Header.h"
using namespace std;

void testranksort()
{
    std::cout << "===Ranksort test==="<< std::endl;
    gensmalllist();
    ranksort(smalllist);
}

void testmatrixmultiplication()
{
    std::cout << "===Matrix Multiplication test===" << std::endl;
    gentwomatrix();
    std::cout <<"Generated: matrix1["<<row1<<"]["<<col1<<"] and matrix2["<<row2<<"]["<<col2<<"]"<< std::endl;
    seqmatrixmultiplicationtypical();
    parmatrixmultiplicationtypical();
    seqmatrixmultiplicationinterchangeloop();
    parmatrixmultiplicationinterchangeloop();
}

void testlogarithmarray()
{
    std::cout << "===Logarithm test==="<< std::endl;
    seqlogarithm();
    parlogarithm();
}

void testbucketsort()
{
    std::cout << "===Bucketsort test==="<< std::endl;
    bucketsort();
}

void exercise_omp7() // Exercise from the exercises of Feb 28
{
    std::cout << "===Exercise 7==="<< std::endl;
#define n 20
    int tid;
    int i;
    omp_set_num_threads(3);
#pragma omp parallel for private(tid) schedule(static,1)
    for (i = 0; i < n; i++) {
        tid = omp_get_thread_num();
        cout << "Schedule(static,1): Thread "<< tid <<" executing iteration "<< i <<endl;
    }
    
    omp_set_num_threads(3);
#pragma omp parallel for private(tid)
    for (i = 0; i < n; i++) {
        tid = omp_get_thread_num();
        cout << "Default: Thread "<< tid <<" executing iteration "<< i <<endl;
    }
}

void exercise_omp9_1()
{
    int x[100];
    int num = 100000000;
    int i;
    double start = omp_get_wtime();
    omp_set_num_threads(2);
    #pragma omp parallel private(i)
    {
        #pragma omp sections nowait
        {
            #pragma omp section
                for (i = 0; i < num; i++)
                    x[0]++;
            #pragma omp section
                for(i = 0; i < num; i++)
                    x[1]++;
        }
    }
    cout << "Time: " << (float)(omp_get_wtime() - start) << endl;
}

void exercise_omp9_2()
{
    int y[100];
    int num = 100000000;
    int i;
    double start = omp_get_wtime();
    omp_set_num_threads(2);
    #pragma omp parallel private(i)
    {
        #pragma omp sections nowait
        {
            #pragma omp section
                for (i = 0; i < num; i++)
                    y[0]++;
            #pragma omp section
                for(i = 0; i < num; i++)
                    y[99]++;
        }
    }
    cout << "Time: " << (float)(omp_get_wtime() - start) << endl;
}

void exercise_omp9()
{
    // Should run individually, because if both methods are executing
    // in the same call, they will encounter the cache ping pong,
    // even the threads are closed, but the cache for each processor/core
    // is still there.
    //cout << "First thread writing on x[0] - Second thread writing on x[1]" << endl;
    //for (int i = 0; i < 10; i++)
    //    exercise_omp9_1();
    cout << "First thread writing on x[0] - Second thread writing on x[99]" << endl;
    for (int i = 0; i < 10; i++)
        exercise_omp9_2();
}

void testnumericalintegration()
{
    std::cout << "===Numerical Integration test==="<< std::endl;
    numericalintegration();
}

int main(int argc, const char * argv[]) {
    //testranksort();
    //cout << endl;
    //testlogarithmarray();
    //cout << endl;
    //testbucketsort();
    //cout << endl;
    //exercise_omp7();
    //cout << endl;
    //exercise_omp9();
    //cout << endl;
    //testmatrixmultiplication();
    //cout << endl;
    testnumericalintegration();
    return 0;
}
