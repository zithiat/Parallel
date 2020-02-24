//
//  Header.h
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//

#ifndef Header_h
#define Header_h
#include <iostream>
#include <omp.h>
#include <time.h>
#include <math.h>

const int smalllistnum = 10000; // for RankSort
const int listnum = 10000000; // number of items in the list
extern int smalllist[smalllistnum];
extern int list1[listnum];
extern int list2[listnum];

const int row1 = 1024;
const int col1 = 1024;
const int row2 = 1024;
const int col2 = 1024;
extern int matrix1[row1][col1];
extern int matrix2[row2][col2];
extern int matrix3[row1][col2];

// Utils
int getarraysize(int *ptr);

// Generate.cpp
void gensmalllist();
void genonelist();
void gentwolist();
void genonematrix();
void gentwomatrix();

// RankSort
void ranksort(int arr[]);

// MatrixMultiplication
void seqmatrixmultiplicationtypical();
void parmatrixmultiplicationtypical();
void seqmatrixmultiplicationinterchangeloop();
void parmatrixmultiplicationinterchangeloop();

// BucketSort
void bucketsort();

// LogarithmArray
void seqlogarithm();
void parlogarithm();

// Numerical Integration
void numericalintegration();

#endif /* Header_h */
