//
//  NumericalIntegration.cpp
//  Parallel
//
//  Created by Quan Doan on 3/1/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include "Header.h"
using namespace std;

// Trapezoid Rule: w * [ f(a)/2 + f(a+w) + f(a+2w) + ... + f(a+nw) + f(b)/2 ]
// f(x) = sqrt(4.0 – x*x)
// range a = 0 to b = 2
// 100,000,000 sample points
// Expected result: pi: 3.1415926535
int samplepoints = 100000000 ; /* 100,000,000 sample points*/
double a = 0.0;
double b = 2.0;
double w = (b - a) / samplepoints;

double seqtime = 0.0;

double f(int i)
{
    return sqrt(4.0 - (a + i * w) * (a + i * w));
}

void seqnumericalintegration()
{
    clock_t st = clock();
    double sum = 0.0;
    for (int i = 1; i <= samplepoints; i++)
    {
        sum = sum + f(i);
    }
    cout << "Result: " << w * (sum + (sqrt(a) + sqrt(b)) / 2) << endl;
    seqtime = (double)(clock() - st);
    cout << "Sequential time: " << seqtime/CLOCKS_PER_SEC << endl;
}

void parnumericalintegrationsection4blocks()
{
    double sum = 0.0;
    double w = (b - a) / samplepoints;
    double st = omp_get_wtime();
    int tnum = omp_get_num_procs();
    omp_set_num_threads(tnum);
    int range = samplepoints / tnum;
    cout << "Number of processors: " << tnum << endl;
#pragma omp sections nowait
    {
#pragma omp section
        {
            for (int i = 1; i <= range; i++)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
#pragma omp section
        {
            for (int i = range + 1; i <= (2 * range); i++)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
#pragma omp section
        {
            for (int i = range * 2 + 1; i <= (3 * range); i++)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
#pragma omp section
        {
            for (int i = range * 3 + 1; i <= (4 * range); i++)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
    }
    
    cout << "Result: " << w * (sum + (sqrt(a) + sqrt(b)) / 2) << endl;
    double partime = omp_get_wtime() - st;
    cout << "Parallel using 4 blocks time: " << partime << endl;
    cout << "Speedup: " << seqtime / partime << endl;
    cout << "Processor Utilization: " << (seqtime / partime) / omp_get_num_procs() << endl;
}

void parnumericalintegrationsectionjumping()
{
    double sum = 0.0;
    double st = omp_get_wtime();
    int tnum = omp_get_num_procs();
    omp_set_num_threads(tnum);
    int s1, s2, s3, s4;
    cout << "Number of processors: " << tnum << endl;
#pragma omp sections nowait
    {
#pragma omp section
        {
            for (int i = 1; i <= samplepoints; i = i + 4)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
#pragma omp section
        {
            for (int i = 2; i <= samplepoints; i = i + 4)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
#pragma omp section
        {
            for (int i = 3; i <= samplepoints; i = i + 4)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
#pragma omp section
        {
            for (int i = 4; i <= samplepoints; i = i + 4)
            {
                double t = f(i);
#pragma omp critical
                {
                    sum = sum + t;
                }
            }
        }
    }
//#pragma omp critical
//    sum = s1 + s2 + s3 + s4;
    
    cout << "Result: " << w * (sum + (sqrt(a) + sqrt(b)) / 2) << endl;
    double partime = omp_get_wtime() - st;
    cout << "Parallel using Jumping pointer time: " << partime << endl;
    cout << "Speedup: " << seqtime / partime << endl;
    cout << "Processor Utilization: " << (seqtime / partime) / omp_get_num_procs() << endl;
}

void parnumericalintegrationparallelcritical()
{
    double sum = 0.0;
    double st = omp_get_wtime();
    int tnum = omp_get_num_procs();
    omp_set_num_threads(tnum);
    int range = samplepoints / tnum;
    int i;
    int start, end;
    int tid;
    cout << "Number of processors: " << tnum << endl;
#pragma omp parallel private (i, tid, start, end)
    {
        tid = omp_get_thread_num();
        start = tid * range + 1;
        end = tid * range + range;
        
        for (i = start; i <= end; i++)
        {
#pragma omp critical
            {
                sum = sum + f(i);
            }
        }
    }
    cout << "Result: " << w * (sum + (sqrt(a) + sqrt(b)) / 2) << endl;
    double partime = omp_get_wtime() - st;
    cout << "Parallel using Critical time: " << partime << endl;
    cout << "Speedup: " << seqtime / partime << endl;
    cout << "Processor Utilization: " << (seqtime / partime) / omp_get_num_procs() << endl;
}

void parnumericalintegrationparallelsummaryarray()
{
    double sum = 0.0;
    double st = omp_get_wtime();
    int tnum = omp_get_num_procs();
    omp_set_num_threads(tnum);
    int range = samplepoints / tnum;
    int i;
    int start, end;
    int tid;
    double sumarr[tnum];
    cout << "Number of processors: " << tnum << endl;
#pragma omp parallel private (i, tid, start, end)
    {
        tid = omp_get_thread_num();
        start = tid * range + 1;
        end = tid * range + range;
        sumarr[tid] = 0.0;
        for (i = start; i <= end; i++)
        {
            sumarr[tid] = sumarr[tid] + f(i);
        }
    }
    
    // Add up the individual sum to the last sum
    for (int j = 0; j < tnum; j++)
    {
        sum = sum + sumarr[j];
    }
    
    cout << "Result: " << w * (sum + (sqrt(a) + sqrt(b)) / 2) << endl;
    double partime = omp_get_wtime() - st;
    cout << "Parallel using Summary Array time: " << partime << endl;
    cout << "Speedup: " << seqtime / partime << endl;
    cout << "Processor Utilization: " << (seqtime / partime) / omp_get_num_procs() << endl;
}

void numericalintegration()
{
    cout << "Sequential Numerical Integration" << endl;
    seqnumericalintegration();
    cout << endl;
    //cout << "Parallel Numerical Integration Using Section with 4 blocks" << endl;
    //parnumericalintegrationsection4blocks();
    //cout << endl;
    cout << "Parallel Numerical Integration Using Section with Jumping pointer" << endl;
    parnumericalintegrationsectionjumping();
    cout << endl;
    //cout << "Parallel Numerical Integration Using Parallel with Critical" << endl;
    //parnumericalintegrationparallelcritical();
    cout << endl;
    cout << "Parallel Numerical Integration Using Parallel with Summary Array" << endl;
    parnumericalintegrationparallelsummaryarray();
}
