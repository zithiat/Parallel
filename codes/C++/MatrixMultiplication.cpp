//
//  MatrixMultiplication.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include <iostream>
#include <omp.h>
#include "Header.h"
using namespace std;
double matrixseqtimetypical = 0.0;
double matrixseqtimeinterchange = 0.0;

int matrix3[row1][col2];

void seqmatrixmultiplicationtypical()
{
    clock_t t = clock();
    for (int i = 0; i < row1; i++)
    {
        for (int j = 0; j < col2; j++)
        {
            for (int k = 0; k < col1; k++)
            {
                matrix3[i][j] += matrix1[i][k] * matrix2[k][j];
            }
        }
    }
    matrixseqtimetypical = (double)(clock() - t)/CLOCKS_PER_SEC;
    cout << "Sequential time for typical: " << matrixseqtimetypical << endl;
}

void parmatrixmultiplicationtypical()
{
    double st = omp_get_wtime();
    omp_set_num_threads(omp_get_num_procs());
#pragma omp parallel for
    for (int i = 0; i < row1; i++)
    {
        for (int j = 0; j < col2; j++)
        {
            for (int k = 0; k < col1; k++)
            {
                matrix3[i][j] += matrix1[i][k] * matrix2[k][j];
            }
        }
    }
    cout << endl;
    double partime = omp_get_wtime() - st;
    cout << "Parallel time for typical: " << partime << endl;
    cout << "Speedup for typical: " << matrixseqtimetypical / partime << endl;
    cout << "Processor Utilization for typical: " << (matrixseqtimetypical / partime) / omp_get_num_procs() << endl;
}

void seqmatrixmultiplicationinterchangeloop()
{
    clock_t t = clock();
    for (int i = 0; i < row1; i++)
    {
        for (int k = 0; k < col1; k++)
        {
            for (int j = 0; j < col2; j++)
            {
                matrix3[i][j] += matrix1[i][k] * matrix2[k][j];
            }
        }
    }
    cout << endl;
    matrixseqtimeinterchange = (double)(clock() - t)/CLOCKS_PER_SEC;
    cout << "Sequential time for interchange: " << matrixseqtimeinterchange << endl;
}

void parmatrixmultiplicationinterchangeloop()
{
    double st = omp_get_wtime();
    omp_set_num_threads(omp_get_num_procs());
#pragma omp parallel for
    for (int i = 0; i < row1; i++)
    {
        for (int k = 0; k < col1; k++)
        {
            for (int j = 0; j < col2; j++)
            {
                matrix3[i][j] += matrix1[i][k] * matrix2[k][j];
            }
        }
    }
    cout << endl;
    double partime = omp_get_wtime() - st;
    cout << "Parallel time for interchange: " << partime << endl;
    cout << "Speedup for interchange: " << matrixseqtimeinterchange / partime << endl;
    cout << "Processor Utilization for interchange: " << (matrixseqtimeinterchange / partime) / omp_get_num_procs() << endl;
    cout << endl;
    cout << "Speedup overall: " << matrixseqtimetypical / partime << endl;
    cout << "Processor Utilization overall: " << (matrixseqtimetypical / partime) / omp_get_num_procs() << endl;
}
