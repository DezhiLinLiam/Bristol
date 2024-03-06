#include "../lisp.h"
#include <stdbool.h>
#include <string.h>
#define LISPIMPL "Linked"
typedef enum{ATOM,LIST} ElmeTag;



struct lisp{
    atomtype tag;
    union{
        atomtype atom;
        struct{
            lisp *tp;
            lisp *hp;
        }prt;
    }uregion;
};
