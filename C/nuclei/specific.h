#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>

typedef int atomtype;
typedef struct lisp lisp;
typedef enum { ATOM, LIST } ElmeTag;

struct lisp {
    atomtype tag;
    union {
        atomtype atom;
        struct {
            lisp* tp;
            lisp* hp;
        }prt;
    }uregion;
};
