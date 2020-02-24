//
//  Generate.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//
#include <cstdlib>

#include "Header.h"

int list1[listnum], list2[listnum];
int matrix1[row1][col1], matrix2[col1][col2];
int smalllist[smalllistnum];

void gensmalllist()
{
    for (int i = 0; i < smalllistnum; i++)
    {
        smalllist[i] = rand();
    }
}

void genonelist()
{
    for (int i = 0; i < listnum; i++)
    {
        list1[i] = rand();
    }
}

void gentwolist()
{
    genonelist();
    for (int i = 0; i < listnum; i++)
    {
        list2[i] = rand();
    }
}

void genonematrix()
{
    for (int i = 0; i < row1; i++)
    {
        for (int j = 0; j < col1; j++)
        {
            matrix1[i][j] = i + j + 1;
        }
    }
}


void gentwomatrix()
{
    genonematrix();
    for (int i = 0; i < row2; i++)
    {
        for (int j = 0; j < col2; j++)
        {
            matrix2[i][j] = i + j + 2;
        }
    }
}
