//
//  BucketSort.cpp
//  Parallel
//
//  Created by Quan Doan on 2/26/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#include <iostream>
#include <omp.h>
#include <time.h>
#include <limits.h>
#include <stdlib.h>
#include <stdint.h>
#include "Header.h"

using namespace std;
#define n 1000000 /*length of list 10000000*/
#define bsize 1000000 /*size of buckets 1000000*/
#define m 100 /*number of buckets 100*/
int list[n]; /*unsorted list of integers*/
int finallist[n]; /*sorted list of integers*/
int bucket[m][bsize]; /*buckets*/
int countbucket[m]; /*number of items stored in bucket*/
int minval;
int maxval;
omp_lock_t L;
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

/*
 Sequential
 */
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
}

void sequential()
{
    double sequentialTime;
    clock_t startTime;
    clock_t disTime;
    clock_t sortTime;
    clock_t mergeTime;
    clock_t endTime;
    minval = INT_MAX;
    maxval = INT_MIN;
    initialize();
    startTime = clock();
    seqDistribute();
    disTime = clock();
    cout<<"Sequential Distribution time : "<<(float)(disTime - startTime)/CLOCKS_PER_SEC<<endl;
    
    seqSort();
    sortTime = clock();
    cout<<"Sequential Sorting time : "<<(float)(sortTime - disTime)/CLOCKS_PER_SEC<<endl;
    
    seqMerge();
    mergeTime = clock();
    cout<<"Sequential Merging time : "<<(float)(mergeTime - sortTime)/CLOCKS_PER_SEC<<endl;
    
    endTime = clock();
    sequentialTime = endTime - startTime;
    cout<<"Total Sequential time : "<<(float)(sequentialTime)/CLOCKS_PER_SEC<<endl;
}

/*
 Parallel with biglock
 */
void parbldistribute()
{
    int const range = maxval - minval + 1;
    omp_init_lock(&L);
#pragma omp parallel for
    for(int i = 0; i < n ; i++)
    {
        int bnum = (int) ((float) m * ((float)list[i] - minval) / range);
        --bnum;
        omp_set_lock(&L);
        bucket[bnum][countbucket[bnum]++] = list[i];
        omp_unset_lock(&L);
    }
}

void parblsort()
{
#pragma omp parallel for
    for(int i = 0; i < m; i++)
    {
        qsort(bucket[i], countbucket[i], sizeof(int), lt);
    }
}

void parblmerge()
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
}

/*
 Parallel with fine-grained lock
 */
void parfgldistribute()
{
    int const range = maxval - minval + 1;
    for (int i = 0; i < m; i++)
        omp_init_lock(&Lock[i]);
#pragma omp parallel for
    for(int i = 0; i < n ; i++)
    {
        int bnum = (int) ((float) m * ((float)(list[i] - minval) / range));
        if (bnum > 0)
            --bnum;
        omp_set_lock(&Lock[bnum]);
        bucket[bnum][countbucket[bnum]++] = list[i];
        omp_unset_lock(&Lock[bnum]);
    }
}

void parfglsort()
{
#pragma omp parallel for
    for(int i = 0; i < m; i++)
    {
        qsort(bucket[i], countbucket[i], sizeof(int), lt);
    }
}

void parfglmerge()
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
}

/*
 Parallel with free lock
 */
void parfldistribute()
{
    int const range = maxval - minval + 1;
    int const split = m / omp_get_num_procs();
#pragma omp parallel
    {
        for(int i = 0; i < n; i++)
        {
            int id   = omp_get_thread_num();
            int bnum = (int) ((float) m * ((float)list[i] - minval) / range);
            --bnum;
            if(bnum >= (split * id) && bnum < (split * id)  + split)
            {
                bucket[bnum][countbucket[bnum]++] = list[i];
            }
        }
    }
}

void parflsort()
{
#pragma omp parallel for
    for(int i = 0; i < m; i++)
    {
        qsort(bucket[i], countbucket[i], sizeof(int), lt);
    }
}

void parflmerge()
{
    int startIndex;
#pragma omp parallel for
    for (int i = 0; i < m; i++){
        //int startIndex = i * bsize;
        //if(startIndex != 0)
        //    --startIndex;
        startIndex = i;
        for (int j = 0; j < bsize; j++)
        {
            finallist[startIndex++] = bucket[i][j];
        }
    }
}

void reset()
{
    initialize();
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

void parallelfinegrainedlock()
{
    int processnum = omp_get_num_procs();
    double startTime;
    double disTime;
    double sortTime;
    double mergeTime;
    minval = INT_MAX;
    maxval = INT_MIN;
    cout << endl;
    cout << "Fine-grained" << endl;
    omp_set_num_threads(processnum);
    cout << "Number of cores: " << processnum << endl;
    initialize();
    startTime = omp_get_wtime();
    parfgldistribute();
    disTime = omp_get_wtime();
    cout<<"Parallel Distribution time : "<<disTime - startTime<<endl;
    
    parfglsort();
    sortTime = omp_get_wtime();
    cout<<"Parallel Sorting time : "<<sortTime - disTime<<endl;
    
    parfglmerge();
    mergeTime = omp_get_wtime();
    cout<<"Parallel Merging time : "<<mergeTime - sortTime<<endl;
    
    cout<<"Total parallel time parallelfinegrainedlock: "<<omp_get_wtime() - startTime<<endl;
}

void parallelbiglock()
{
    int processnum = omp_get_num_procs();
    double startTime;
    double disTime;
    double sortTime;
    double mergeTime;
    minval = INT_MAX;
    maxval = INT_MIN;
    cout << endl;
    cout << "Biglock" << endl;
    omp_set_num_threads(processnum);
    cout << "Number of cores: " << processnum << endl;
    initialize();
    startTime = clock();
    parbldistribute();
    disTime = omp_get_wtime();
    cout<<"Parallel Distribution time : "<<disTime - startTime<<endl;
    
    parblsort();
    sortTime = omp_get_wtime();
    cout<<"Parallel Sorting time : "<<sortTime - disTime<<endl;
    
    parblmerge();
    mergeTime = omp_get_wtime();
    cout<<"Parallel Merging time : "<<mergeTime - sortTime<<endl;
    
    cout<<"Total parallel time parallelbiglock: "<<omp_get_wtime() - startTime<<endl;
}

void parallelfreelock()
{
    int processnum = omp_get_num_procs();
    double startTime;
    double disTime;
    double sortTime;
    double mergeTime;
    minval = INT_MAX;
    maxval = INT_MIN;
    cout << endl;
    cout << "Freelock" << endl;
    omp_set_num_threads(processnum);
    cout << "Number of cores: " << processnum << endl;
    initialize();
    startTime = omp_get_wtime();
    parfldistribute();
    disTime = omp_get_wtime();
    cout<<"Parallel Distribution time : "<<disTime - startTime<<endl;
    
    parflsort();
    sortTime = omp_get_wtime();
    cout<<"Parallel Sorting time : "<<sortTime - disTime<<endl;
    
    parflmerge();
    mergeTime = omp_get_wtime();
    cout<<"Parallel Merging time : "<<mergeTime - sortTime<<endl;
    
    cout<<"Total parallel time freelock: "<<omp_get_wtime() - startTime<<endl;
}

void fastestsolution()
{
    int processnum = omp_get_num_procs();
    double startTime;
    double disTime;
    double sortTime;
    double mergeTime;
    minval = INT_MAX;
    maxval = INT_MIN;
    omp_set_num_threads(processnum);
    cout << "Number of cores: " << processnum << endl;
    initialize();
    startTime = omp_get_wtime();
    seqDistribute();
    disTime = omp_get_wtime();
    cout<<"Sequential Distribution time : "<<disTime - startTime<<endl;
    
    parflsort();
    sortTime = omp_get_wtime();
    cout<<"Parallel Sorting time : "<<sortTime - disTime<<endl;
    
    parflmerge();
    mergeTime = omp_get_wtime();
    cout<<"Parallel Merging time : "<<mergeTime - sortTime<<endl;
    
    cout<<"Total time: "<<omp_get_wtime() - startTime<<endl;
}

void bucketsort()
{
    /*
    sequential();
    reset();
    
    parallelfreelock();
    reset();
    
    parallelbiglock();
    reset();
    
    parallelfinegrainedlock();
    reset();
     */
    fastestsolution();
}
