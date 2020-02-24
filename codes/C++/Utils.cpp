//
//  Utils.cpp
//  Parallel
//
//  Created by Quan Doan on 2/28/19.
//  Copyright © 2019 Quan Doan. All rights reserved.
//
#include "Header.h"

int getarraysize(int *ptr)
{
    unsigned int len=0;
    while(*ptr!=0)
    {
        len++;
        ptr++;
    }
    return len;
}
