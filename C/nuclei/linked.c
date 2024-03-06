#include "specific.h"
#include <assert.h>

lisp* lisp_copy(const lisp* l)
{
    lisp* q;
    q = (lisp*)malloc(sizeof(lisp));
    if (l == NULL) {
        q = NULL;
    }
    else {
        if (l->tag == ATOM) {
            q->tag = ATOM;
            q->uregion.atom = (atomtype)l->uregion.atom;

        }
        else {
            q->tag = LIST;
            q->uregion.prt.hp = lisp_copy(l->uregion.prt.hp);
            q->uregion.prt.tp = lisp_copy(l->uregion.prt.tp);
        }
    }

    return q;
}


lisp* lisp_atom(const atomtype a)
{
    lisp* t;
    t = (lisp*)malloc(sizeof(lisp));

    t->tag = ATOM;
    t->uregion.atom = a;
    return t;
}

lisp* lisp_cons(const lisp* l1, const lisp* l2)
{
    lisp* t;
    t = (lisp*)malloc(sizeof(lisp));

    t->tag = LIST;
    t->uregion.prt.hp = lisp_copy(l1);
    t->uregion.prt.tp = lisp_copy(l2);

    return t;
}



// Returns the car (1st) component of the list 'l'.
// Does not copy any data.
lisp* lisp_car(const lisp* l)
{
    lisp* t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);

    return t->uregion.prt.hp;
}

lisp* lisp_cdr(const lisp* l)
{
    lisp* t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);

    return t->uregion.prt.tp;
}


atomtype lisp_getval(const lisp* l)
{
    lisp* t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);

    if (t->tag == ATOM) {
        return t->uregion.atom;
    }
    else if (l->tag == LIST) {
        return lisp_getval(l->uregion.prt.hp);
    }
    return 0;
}

// Returns a boolean depending up whether l points to an atom (not a list)
bool lisp_isatomic(const lisp* l)
{
    if (l == NULL) {
        return false;
    }
    else if (l->tag == ATOM) {
        return true;
    }
    else {
        return false;
    }
}

int lisp_length(const lisp* l)
{
    atomtype cnt = 0;
    lisp* t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);
    if (t == NULL || l->tag == ATOM) {
        cnt = 0;
        return cnt;
    }
    else {
        do {
            t = t->uregion.prt.tp;
            cnt++;
        } while (t != NULL);
    }
    return cnt;
}

// Returns stringified version of list
void lisp_tostring(const lisp* l, char* str)
{
    lisp* cl;
    cl = (lisp*)malloc(sizeof(lisp));
    cl = lisp_copy(l);
    int j = 0;
    str[0] = 0;
    if (!l)
    {
        str[j++] = '(';
        str[j++] = ')';
        str[j] = '\0';
    }
    else
    {
        if (l->tag == ATOM)
        {
            sprintf(str, "%d", l->uregion.atom);
            //str[strlen(str)] = (char)('0' + l->uregion.atom);
            //str[strlen(str)] = '\0';
        }
        else
        {
            str[strlen(str)] = '(';
            while (cl) {
                lisp_tostring(cl->uregion.prt.hp, &str[strlen(str)]);
                cl = cl->uregion.prt.tp;
                if (cl) {
                    str[strlen(str)] = ' ';
                    str[strlen(str)] = '\0';
                }
            }
            str[strlen(str)] = ')';
            str[strlen(str)] = '\0';
        } str[strlen(str)] = '\0';
    }
}

void lisp_free(lisp** l)
{
    lisp* Node;
    Node = *l;
    if (Node->uregion.prt.hp != NULL)
    {

        if (Node->tag == ATOM)
        {
            free(*l);
            *l = NULL;
            assert(*l == NULL);
        }
        else if (Node->tag == LIST)
        {
            while (Node) {
                if ((Node->uregion.prt.hp->tag) == ATOM)
                {
                    free(Node->uregion.prt.hp);
                    Node->uregion.prt.hp = NULL;
                }
                else if ((Node->uregion.prt.hp->tag) == LIST)
                {
                    lisp_free(&Node->uregion.prt.hp);
                }
                Node = Node->uregion.prt.tp;
                free(*l);
                *l = NULL;
            }
        }
    }
    else {
        *l = NULL;
    }
}
