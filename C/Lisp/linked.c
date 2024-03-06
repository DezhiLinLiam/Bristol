
#include "../testlisp.c"
#define num 10
// Returns a deep copy of the list 'l'
lisp* lisp_atom(const atomtype a)
{
    lisp *t ;
    t = (lisp*)malloc(sizeof(lisp));
    
    t->tag = ATOM;
    t->uregion.atom = a;
    return t;
}

lisp* lisp_cons(const lisp* l1,  const lisp* l2)
{
    lisp *t;
    t=(lisp*)malloc(sizeof(lisp));
    
    t->tag = LIST;
    t->uregion.prt.hp = lisp_copy(l1);
    t->uregion.prt.tp = lisp_copy(l2);
    
    return t;
}

// Returns the car (1st) component of the list 'l'.
// Does not copy any data.
lisp* lisp_car(const lisp* l)
{
    lisp *t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);
    
    return t->uregion.prt.hp;
}

lisp* lisp_cdr(const lisp* l)
{
    lisp *t;
    t = (lisp*)malloc(sizeof(lisp));
    t=lisp_copy(l);
    
    return t->uregion.prt.tp;
}

lisp* lisp_copy(const lisp* l)
{
    lisp *q,*t;
    q = (lisp*)malloc(sizeof(lisp));
    if(l == NULL){
        q=NULL;
    }else{
        if(l->tag == ATOM){
            q->tag = ATOM;
            q->uregion.atom =(atomtype) l->uregion.atom;

        }else{
            q->tag = LIST;
            q->uregion.prt.hp = l->uregion.prt.hp;
            q->uregion.prt.tp = l->uregion.prt.tp;
            t=q->uregion.prt.hp;
        }
    }

    return q;
}

atomtype lisp_getval(const lisp* l)
{
    lisp *t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);
    
    if(t->tag == ATOM){
        return t->uregion.atom;
    }
    else{
       return 0;
    }
}

// Returns a boolean depending up whether l points to an atom (not a list)
bool lisp_isatomic(const lisp* l)
{
    if(l == NULL){
        return false;
    }else if(l->tag == ATOM){
        return true;
    }else{
        return false;
    }
}

int lisp_length(const lisp* l)
{
    atomtype cnt = 0;
    lisp *t;
    t = (lisp*)malloc(sizeof(lisp));
    t = lisp_copy(l);
    if(t == NULL || l->tag == ATOM){
        cnt = 0;
        return cnt;
    }else{
        do{
            t = t->uregion.prt.tp;
            cnt++;
        }while(t!=NULL);
    }
    return cnt;
}

// Returns stringified version of list
void lisp_tostring(const lisp* l, char* str)
{
    atomtype cnt = 0;
    char c;
    lisp *t , *q ;
    if(l == NULL){
        str[cnt++]='(';
        str[cnt++]=')';
        return;
    }
    for(int i = 0;i<LISTSTRLEN;i++){
        str[i]='\0';
    }
    t =(lisp*) (lisp*)malloc(sizeof(lisp));
    q =(lisp*) (lisp*)malloc(sizeof(lisp));
    
    t = lisp_copy(l);
    str[cnt++] = '(';
    do{
        q=t->uregion.prt.hp;
        if(q == NULL){
            str[0] = (char)(l->uregion.atom+'0');
            return;
        }
        if(q->tag == ATOM){
            if(str[cnt-1] == ')' && cnt !=0 ){
                str[cnt]=' ';
                cnt++;
            }
            c =(char)(q->uregion.atom+'0');
            str[cnt] = c;
            cnt++;
            str[cnt] = ' ';
            cnt++;
            t = t->uregion.prt.tp;
        }else{
            q = lisp_copy(t);
            q = q->uregion.prt.hp;
            lisp_tostring(q, &str[cnt]);
            for(int i=0 ; i<LISTSTRLEN ;i++){
                if(str[i] == '\0') {
                    cnt = i;
                    break;
                }
            }
            t= t->uregion.prt.tp;
        }
    }while(t != NULL);
    str[--cnt] = ')';
}

void lisp_free(lisp** l)
{
    lisp *Node;
    Node = *l;
    if((*l)->tag==ATOM){
        *l=NULL;
        free(Node);
        return;
    }
    Node->uregion.prt.hp = NULL;
    Node->uregion.prt.tp = NULL;
    Node = NULL;
    free(Node);
    *l = Node;

}

void test(void)
{
    lisp *a,*b,*l;
    char str[LISTSTRLEN];
    a=lisp_atom(3);
    b=NULL;
    l=lisp_cons(a, b);
    assert(lisp_length(l)==1);
    lisp_tostring(l, str);
    printf("%s",str);
    assert(strcmp(str, "(3)")==0);
    assert(lisp_getval(car(l))==3);
    assert(lisp_isatomic(l)==false);
    assert(lisp_isatomic(lisp_car(l))==true);
    lisp_free(&l);
    assert(l==NULL);
}
lisp* lisp_fromstring(const char* str)
{
  char str1[num];

  for(int i = 0 ; i < num ;i++){
      str1[i] = str[i];
}
  return NULL;
}

lisp* lisp_list(const int n, ...)
{
  int h;
  h = n;
  return NULL;
}

void lisp_reduce(void (*func)(lisp* l, atomtype* n), lisp* l, atomtype* acc)
{
  lisp *x;
  int i,n=0;
  i = acc[0];
  x=l;
  (*func)( l , &n);
  return;
}
