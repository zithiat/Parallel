//
//  BucketSort.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include <iostream>
#include <omp.h>
#include <time.h>
#include <limits.h>
#include <stdlib.h>
#include <stdint.h>
#include <assert.h>
#include "Header.h"

using namespace std;
#define n 100            /*length of list 10000000*/
#define bsize 100         /*size of buckets 1000000*/
#define m 10                 /*number of buckets 100*/
int list[n];                  /*unsorted list of integers*/
int finallist[n];             /*sorted list of integers*/
int bucket[m][bsize];         /*buckets*/
int countbucket[m];           /*number of items stored in bucket*/
int minval;
int maxval;
omp_lock_t Lock[m];

void initialize()
{
    for(int i =0; i < n; i++)
    {
        list[i] = rand();
        if(list[i]  > maxval)
        {
            maxval = list[i];
        }
        if(list[i] < minval)
        {
            minval = list[i];
        }
    }
}

int lt(const void *p, const void *q)
{
    return (*(int *)p - *(int *)q);
}

void printBuckets()
{
    cout << "Elements in corresponding bucket" << endl;
    for (int i = 0; i < m; i++)
    {
        cout << countbucket[i] << "\t";
    }
    cout << endl << endl;
    
    for (int j = 0; j < bsize; j++)
    {
        for (int i = 0; i < m; i++)
        {
            cout << bucket[i][j] << "\t ";
        }
        cout << endl;
    }
}

void seqDistribute()
{
    int const range = maxval - minval + 1;
    for(int i = 0; i < n ; i++)
    {
        int bnum = (int) ((float) m * ((float)list[i] - minval) / range);
        --bnum;
        if(bnum >= m || countbucket[bnum] >= bsize)
        {
            cout<<"error\n"<<endl;
            cout<<"bnum -"<<bnum<<"\n";
            cout<<"m -"<<m<<"\n";
            cout<<"list[i] -"<<list[i]<<"\n";
            cout<<"minval -"<<minval<<"\n";
            cout<<"range -"<<range<<"\n";
            exit(0);
        }
        bucket[bnum][countbucket[bnum]++] = list[i];
        printBuckets();
    }
}


void seqSort()
{
    for(int i = 0; i < m; i++)
    {
        qsort(bucket[i], countbucket[i], sizeof(int), lt);
    }
}

void seqMerge()
{
    for (int i = 0; i < m; i++){
        int startIndex = i * bsize;
        if(startIndex != 0)
            --startIndex;
        for (int j = 0; j < bsize; j++)
        {
            finallist[startIndex++] = bucket[i][j];
        }
    }
    
    for (int i = 0; i < n; i++)
    {
        cout << finallist[i] << "\t";
        if ((i + 1) % 8 == 0)
        {
            cout << endl;
        }
    }
}

void sequential()
{
    double sequentialTime;
    clock_t startTime;
    clock_t endTime;
    minval  = INT_MAX;
    maxval    = INT_MIN;
    initialize();
    startTime = clock();
    seqDistribute();
    seqSort();
    seqMerge();
    endTime = clock();
    sequentialTime = endTime - startTime;
    cout<<"Sequential time : "<<sequentialTime<<endl;
}

void parDistribute()
{
    int const range = maxval - minval + 1;
#pragma omp parallel for
    for(int i = 0; i < n ; i++)
    {
        int bnum = (int) ((float) m * ((float)list[i] - minval) / range);
        --bnum;
        if(bnum >= m || countbucket[bnum] >= bsize)
        {
            cout<<"error\n"<<endl;
            cout<<"bnum -"<<bnum<<"\n";
            cout<<"m -"<<m<<"\n";
            cout<<"list[i] -"<<list[i]<<"\n";
            cout<<"minval -"<<minval<<"\n";
            cout<<"range -"<<range<<"\n";
            exit(0);
        }
        omp_set_lock(&Lock[bnum]);
        bucket[bnum][countbucket[bnum]++] = list[i];
        omp_unset_lock(&Lock[bnum]);
        
        printBuckets();
    }
}


void parSort()
{
#pragma omp parallel for
    for(int i = 0; i < m; i++)
    {
        qsort(bucket[i], countbucket[i], sizeof(int), lt);
    }
}

void parMerge()
{
#pragma omp parallel for
    for (int i = 0; i < m; i++){
        int startIndex = i * bsize;
        if(startIndex != 0)
            --startIndex;
        for (int j = 0; j < bsize; j++)
        {
            finallist[startIndex++] = bucket[i][j];
        }
    }
    
    for (int i = 0; i < n; i++)
    {
        cout << finallist[i] << "\t";
        if ((i + 1) % 8 == 0)
        {
            cout << endl;
        }
    }

}

void reset()
{
    initialize();
    //final bucket count1
    for (int i = 0; i < n; i++){
        finallist[i] = 0;
    }
    
    for (int i = 0; i < m; i++){
        for (int j = 0; j < countbucket[i]; j++){
            bucket[i][j] = 0;
        }
        countbucket[i] = 0;
    }
}

void parallelfinegrainlock()
{
    int processnum = omp_get_num_procs();
    double speedup;
    float parallelTime;
    clock_t startTime;
    clock_t endTime;
    minval  = INT_MAX;
    maxval    = INT_MIN;
    omp_set_num_threads(processnum);
    cout << "Number of cores: " << processnum << endl;
    initialize();
    startTime = clock();
    parDistribute();
    parSort();
    parMerge();
    endTime = clock();
    parallelTime = endTime - startTime;
    cout<<"Total parallel time : "<<parallelTime<<endl;
    speedup = (double) 2320000 / parallelTime;
    cout<<"Speedup : "<<speedup<<endl;
}

void bucketsort()
{
    sequential();
    parallelfinegrainlock();
    reset();
    cin.get();
}
