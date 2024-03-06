#include "nuclei.h"

#ifdef INTERP
#include "lisp.h"
lisp** lsps = NULL;
char* vars = NULL;
int nlisp = 0;
lisp* tmp = NULL;
int print = 1;
#endif


void check_token(Token* token, char* name)
{
	strcpy(token->name, name);
	if (!strcmp(name, "("))
	{
		token->type = LBRACK;
	}
	else if (!strcmp(name, ")"))
	{
		token->type = RBRACK;
	}
	else if (!strcmp(name, "CAR"))
	{
		token->type = CAR;
	}
	else if (!strcmp(name, "CDR"))
	{
		token->type = CDR;
	}
	else if (!strcmp(name, "CONS"))
	{
		token->type = CONS;
	}
	else if (!strcmp(name, "SET"))
	{
		token->type = SET;
	}
	else if (!strcmp(name, "IF"))
	{
		token->type = IF;
	}
	else if (!strcmp(name, "WHILE"))
	{
		token->type = WHILE;
	}
	else if (!strcmp(name, "NIL"))
	{
		token->type = NIL;
	}
	else if (!strcmp(name, "PLUS"))
	{
		token->type = PLUS;
	}
	else if (!strcmp(name, "LENGTH"))
	{
		token->type = LENGTH;
	}
	else if (!strcmp(name, "LESS"))
	{
		token->type = LESS;
	}
	else if (!strcmp(name, "GREATER"))
	{
		token->type = GREATER;
	}
	else if (!strcmp(name, "EQUAL"))
	{
		token->type = EQUAL;
	}
	else if (!strcmp(name, "PRINT"))
	{
		token->type = PRINT;
	}
	else if (!strcmp(name, "SWITCH"))
	{
		token->type = SWITCH;
	}
	else if (!strcmp(name, "CASE"))
	{
		token->type = CASE;
	}
	else
	{
		token->type = VAR;
	}
}

int check_literal(char* name)
{
	int brack = 0, hb = 0;;
	int i = 0;
	int len = strlen(name);
	for (i = 0; i < len; i++)
	{
		if (name[i] == ' ' || (name[i] >= '0' && name[i] <= '9') || name[i] == '-')
		{
			continue;
		}
		if (name[i] == '(')
		{
			hb = 1;
			brack++;
		}
		if (name[i] == ')')
		{
			hb = 1;
			if (brack <= 0)
			{
				return 0;
			}
			brack--;
		}
	}
	if (brack != 0)
	{
		return 0;
	}
	if (hb == 1)
	{
		if (name[0] != '(' || name[strlen(name) - 1] != ')')
		{
			return 0;
		}
	}
	return 1;
}

int parse_token(FILE* fp, Token* tokens)
{
	int idx = 0;
	char c;
	char name[100];
	int num = 0;
	while (1)
	{
		c = fgetc(fp);
		if (feof(fp))
		{
			break;
		}
		if (c == '#')
		{
			if (idx > 0)
			{
				name[idx] = 0;
				check_token(&tokens[num], name);
				num++;
				idx = 0;
			}
			while (c != '\n')
			{
				c = fgetc(fp);
			}
			continue;
		}
		else if (c == '"')
		{
			if (idx > 0)
			{
				name[idx] = 0;
				check_token(&tokens[num], name);
				num++;
				idx = 0;
			}
			c = fgetc(fp);
			while (c != '"')
			{
				name[idx++] = c;
				c = fgetc(fp);
			}
			name[idx] = 0;
			tokens[num].type = STRING;
			strcpy(tokens[num].name, name);
			num++;
			idx = 0;
		}
		else if (c == '\'')
		{
			if (idx > 0)
			{
				name[idx] = 0;
				strcpy(tokens[num].name, name);
				tokens[num].type = LITERAL;
				num++;
				idx = 0;
			}
			c = fgetc(fp);
			while (c != '\'')
			{
				name[idx++] = c;
				c = fgetc(fp);
			}
			name[idx] = 0;
			if (check_literal(name) == 0)
			{
				on_error("check_literal");
			}
			tokens[num].type = LITERAL;
			strcpy(tokens[num].name, name);
			num++;
			idx = 0;
		}
		else if (c == '(' || c == ')')
		{
			if (idx > 0)
			{
				name[idx] = 0;
				check_token(&tokens[num], name);
				num++;
				idx = 0;
			}
			name[idx] = c;
			idx++;
			name[idx] = 0;
			check_token(&tokens[num], name);
			num++;
			idx = 0;
		}
		else if (c >= 'A' && c <= 'Z')
		{
			name[idx] = c;
			idx++;
		}
		else if (c == ' ' || c == '\r' || c == '\n')
		{
			if (idx > 0)
			{
				name[idx] = 0;
				check_token(&tokens[num], name);
				num++;
				idx = 0;
			}
		}
	}
	if (idx > 0)
	{
		name[idx] = 0;
		check_token(&tokens[num], name);
		num++;
		idx = 0;
	}
	return num;
}

void parse_prog(Token* tokens, int num)
{
	int idx = 0;
	if (idx >= num || tokens[idx].type != LBRACK)
	{
		on_error("parse_prog");
	}
	idx++;
	parse_instrcts(tokens, num, &idx);
	if (idx != num)
	{
		on_error("parse_prog");
	}
}

void parse_instrcts(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_instrcts");
	}
	if (tokens[*cur].type == RBRACK)
	{
		(*cur)++;
		return;
	}
	parse_instrct(tokens, num, cur);
	parse_instrcts(tokens, num, cur);
}

void parse_instrct(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_instrct");
	}
	if (tokens[*cur].type != LBRACK)
	{
		on_error("parse_instrct");
	}
	(*cur)++;
	parse_func(tokens, num, cur);
	if (tokens[*cur].type != RBRACK)
	{
		on_error("parse_instrct");
	}
	(*cur)++;
}

void parse_func(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_func");
	}
	if (parse_retfunc(tokens, num, cur))
	{
		return;
	}
	if (parse_iofunc(tokens, num, cur))
	{
		return;
	}
	if (parse_if(tokens, num, cur))
	{
		return;
	}
	if (parse_loop(tokens, num, cur))
	{
		return;
	}
	if (parse_switch(tokens, num, cur))
	{
		return;
	}
	on_error("parse_func");
}

int parse_retfunc(Token* tokens, int num, int* cur)
{
	if (parse_listfunc(tokens, num, cur))
	{
		return 1;
	}
	if (parse_intfunc(tokens, num, cur))
	{
		return 1;
	}
	if (parse_boolfunc(tokens, num, cur))
	{
		return 1;
	}
	return 0;
}

int parse_listfunc(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_listfunc");
	}
#ifdef INTERP
	lisp* t1, * t2;
#endif
	switch (tokens[*cur].type)
	{
	case CAR:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		tmp = lisp_car(tmp);
#endif
		return 1;
	case CDR:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		tmp = lisp_cdr(tmp);
#endif
		return 1;
	case CONS:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		t1 = tmp;
#endif
		parse_list(tokens, num, cur);
#ifdef INTERP
		t2 = tmp;
		tmp = lisp_cons(t1, t2);
#endif
		return 1;
	default:
		return 0;
	}
}

int parse_iofunc(Token* tokens, int num, int* cur)
{
	if (parse_set(tokens, num, cur))
	{
		return 1;
	}
	if (parse_print(tokens, num, cur))
	{
		return 1;
	}
	return 0;
}

int parse_if(Token* tokens, int num, int* cur)
{
#ifdef INTERP
	int flag = 1;
#endif
	if (*cur >= num)
	{
		on_error("parse_if");
	}
	if (tokens[*cur].type != IF)
	{
		return 0;
	}
	(*cur)++;
	if (*cur >= num || tokens[*cur].type != LBRACK)
	{
		on_error("parse_if");
	}
	(*cur)++;
	if (parse_boolfunc(tokens, num, cur) == 0)
	{
		on_error("parse_if");
	}
#ifdef INTERP
	flag = lisp_getval(tmp);
	print = flag;
#endif
	if (*cur >= num || tokens[*cur].type != RBRACK)
	{
		on_error("parse_if");
	}
	(*cur)++;
	if (*cur >= num || tokens[*cur].type != LBRACK)
	{
		on_error("parse_if");
	}
	(*cur)++;
	parse_instrcts(tokens, num, cur);
	if (*cur >= num || tokens[*cur].type != LBRACK)
	{
		on_error("parse_if");
	}
	(*cur)++;
#ifdef INTERP
	print = !flag;
#endif
	parse_instrcts(tokens, num, cur);
#ifdef INTERP
	print = 1;
#endif
	return 1;
}

int parse_switch(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_switch");
	}
	if (tokens[*cur].type != SWITCH)
	{
		return 0;
	}
	(*cur)++;
	if (*cur >= num || tokens[*cur].type != LBRACK)
	{
		on_error("parse_switch");
	}
	(*cur)++;
	parse_list(tokens, num, cur);
#ifdef INTERP
	char buf[100];
	memset(buf, 0, sizeof(buf));
	lisp_tostring(tmp, buf);
	int sel = atoi(buf);
#endif
	if (*cur >= num || tokens[*cur].type != RBRACK)
	{
		on_error("parse_switch");
	}
	(*cur)++;
	int flag = 0;
	while (1)
	{
		if (*cur >= num || tokens[*cur].type != LBRACK)
		{
			if (flag == 0)
			{
				on_error("parse_switch");
			}
			else
			{
				return 1;
			}
		}
		(*cur)++;
		if (*cur >= num || tokens[*cur].type != CASE)
		{
			if (flag == 1)
			{
				(*cur) -= 2;
				return 1;
			}
			else
			{
				on_error("parse_switch");
			}
		}
		flag = 1;
		(*cur)++;
		if (*cur >= num || tokens[*cur].type != LITERAL)
		{
			on_error("parse_switch");
		}
#ifdef INTERP
		int tsel = atoi(tokens[*cur].name);
		if (sel == tsel)
		{
			print = 1;
		}
		else
		{
			print = 0;
		}
#endif
		(*cur)++;
		parse_instrcts(tokens, num, cur);
#ifdef INTERP
		print = 1;
#endif
	}
}

int parse_loop(Token* tokens, int num, int* cur)
{
#ifdef INTERP
	int tcur = *cur;
#endif
	while (1)
	{
		if (*cur >= num)
		{
			on_error("parse_loop");
		}
		if (tokens[*cur].type != WHILE)
		{
			return 0;
		}
		(*cur)++;
		if (*cur >= num || tokens[*cur].type != LBRACK)
		{
			on_error("parse_loop");
		}
		(*cur)++;
		if (parse_boolfunc(tokens, num, cur) == 0)
		{
			on_error("parse_loop");
		}
#ifdef INTERP
		print = lisp_getval(tmp);
#endif
		if (*cur >= num || tokens[*cur].type != RBRACK)
		{
			on_error("parse_loop");
		}
		(*cur)++;
		if (*cur >= num || tokens[*cur].type != LBRACK)
		{
			on_error("parse_loop");
		}
		(*cur)++;
		parse_instrcts(tokens, num, cur);
#ifdef INTERP
		if (print == 1)
		{
			*cur = tcur;
			continue;
		}
		else
		{
			print = 1;
			break;
		}
#endif
		break;
	}
	return 1;
}

int parse_intfunc(Token* tokens, int num, int* cur)
{
#ifdef INTERP
	lisp* t1, * t2;
	int n1, n2;
#endif
	if (*cur >= num)
	{
		on_error("parse_intfunc");
	}
	switch (tokens[*cur].type)
	{
	case PLUS:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		t1 = tmp;
#endif
		parse_list(tokens, num, cur);
#ifdef INTERP
		t2 = tmp;
		n1 = lisp_getval(t1);
		n2 = lisp_getval(t2);
		tmp = lisp_atom(n1 + n2);
#endif
		return 1;
	case LENGTH:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		n1 = lisp_length(tmp);
		tmp = lisp_atom(n1);
#endif
		return 1;
	default:
		return 0;
	}
}

int parse_boolfunc(Token* tokens, int num, int* cur)
{
#ifdef INTERP
	lisp* t1, * t2;
	int n1, n2;
#endif
	if (*cur >= num)
	{
		on_error("parse_boolfunc");
	}
	switch (tokens[*cur].type)
	{
	case LESS:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		t1 = tmp;
#endif
		parse_list(tokens, num, cur);
#ifdef INTERP
		t2 = tmp;
		n1 = lisp_getval(t1);
		n2 = lisp_getval(t2);
		if (n1 < n2)
		{
			tmp = lisp_atom(1);
		}
		else
		{
			tmp = lisp_atom(0);
		}
#endif
		return 1;
	case GREATER:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		t1 = tmp;
#endif
		parse_list(tokens, num, cur);
#ifdef INTERP
		t2 = tmp;
		n1 = lisp_getval(t1);
		n2 = lisp_getval(t2);
		if (n1 > n2)
		{
			tmp = lisp_atom(1);
		}
		else
		{
			tmp = lisp_atom(0);
		}
#endif
		return 1;
	case EQUAL:
		(*cur)++;
		parse_list(tokens, num, cur);
#ifdef INTERP
		t1 = tmp;
#endif
		parse_list(tokens, num, cur);
#ifdef INTERP
		t2 = tmp;
		n1 = lisp_getval(t1);
		n2 = lisp_getval(t2);
		if (n1 == n2)
		{
			tmp = lisp_atom(1);
		}
		else
		{
			tmp = lisp_atom(0);
		}
#endif
		return 1;
	default:
		return 0;
	}
}

#ifdef INTERP
lisp* parse_num(char* name, int* idx)
{
	int len = strlen(name);
	int i = 0;
	int num = 0;
	int less = 0;
	for (i = *idx; i < len; i++)
	{
		if (name[i] >= '0' && name[i] <= '9')
		{
			num = num * 10 + name[i] - '0';
		}
		else if (name[i] == '-')
		{
			less = 1;
		}
		else
		{
			break;
		}
	}
	*idx = i;
	if (less)
	{
		num = 0 - num;
	}
	return lisp_atom(num);
}

lisp* parse_literal(char* name, int* idx)
{
	int i = 0, j = 0;
	int len = strlen(name);
	lisp* tlsp[50];
	lisp* t1 = NULL, * t2 = NULL;
	int ntlsp = 0;
	for (i = *idx; i < len; i++)
	{
		if (name[i] == '(')
		{
			i++;
			(*idx) = i;
			tlsp[ntlsp] = parse_literal(name, idx);
			ntlsp++;
			i = *idx;
		}
		else if (name[i] == ')')
		{
			if (ntlsp == 1)
			{
				break;
			}
			for (j = ntlsp - 1; j >= 0; j--)
			{
				t1 = lisp_cons(tlsp[j], t2);
				t2 = t1;
			}
			return t1;
		}
		else if ((name[i] >= '0' && name[i] <= '9') || name[i] == '-')
		{
			*idx = i;
			tlsp[ntlsp] = parse_num(name, idx);
			ntlsp++;
			i = *idx - 1;
		}
	}
	return tlsp[0];
}
#endif

void parse_list(Token* tokens, int num, int* cur)
{
#ifdef INTERP
	int i = 0;
#endif
	if (*cur >= num)
	{
		on_error("parse_list");
	}
	switch (tokens[*cur].type)
	{
	case VAR:
	{
#ifdef INTERP
		tmp = NULL;
		for (i = 0; i < nlisp; i++)
		{
			if (tokens[*cur].name[0] == vars[i])
			{
				tmp = lsps[i];
				break;
			}
		}
#endif
		(*cur)++;
	}
	break;
	case LITERAL:
	{
#ifdef INTERP
		i = 0;
		tmp = parse_literal(tokens[*cur].name, &i);
#endif
		(*cur)++;
	}
	break;
	case NIL:
	{
#ifdef INTERP
		tmp = NULL;
#endif
		(*cur)++;
	}
	break;
	case LBRACK:
	{
		(*cur)++;
		if (parse_retfunc(tokens, num, cur) == 0)
		{
			on_error("parse_list");
		}
		if (*cur >= num || tokens[*cur].type != RBRACK)
		{
			on_error("parse_list");
		}
		(*cur)++;
	}
	break;
	default:
		on_error("parse_list");
		break;
	}
}

int parse_set(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_set");
	}
	if (tokens[*cur].type != SET)
	{
		return 0;
	}
	(*cur)++;
	if (*cur >= num || tokens[*cur].type != VAR)
	{
		on_error("parse_set");
	}
#ifdef INTERP
	int i = 0;
	for (i = 0; i < nlisp; i++)
	{
		if (vars[i] == tokens[*cur].name[0])
		{
			break;
		}
	}
	vars[i] = tokens[*cur].name[0];
#endif
	(*cur)++;
	parse_list(tokens, num, cur);
#ifdef INTERP
	if (i == nlisp)
	{
		lsps[i] = tmp;
		nlisp++;
	}
	else
	{
		lsps[i] = tmp;
	}
#endif
	return 1;
}

int parse_print(Token* tokens, int num, int* cur)
{
	if (*cur >= num)
	{
		on_error("parse_print");
	}
	if (tokens[*cur].type != PRINT)
	{
		return 0;
	}
	(*cur)++;
	if (*cur >= num)
	{
		on_error("parse_set");
	}
	if (tokens[*cur].type == STRING)
	{
#ifdef INTERP
		if (print)
		{
			printf("%s\n", tokens[*cur].name);
		}
#endif
		(*cur)++;
		return 1;
	}
	parse_list(tokens, num, cur);
#ifdef INTERP
	char buf[100];
	memset(buf, 0, sizeof(buf));
	lisp_tostring(tmp, buf);
	if (print)
	{
		printf("%s\n", buf);
	}
#endif
	return 1;
}

int main(int argc, char** argv)
{
	if (argc != 2)
	{
		on_error("argc error\n");
	}
	FILE* fp = fopen(argv[1], "r");
	Token* tokens = (Token*)malloc(sizeof(Token) * 1000);
	int ntoken = parse_token(fp, tokens);
	fclose(fp);
#ifdef INTERP
	int i = 0;
	lsps = (lisp**)malloc(sizeof(lisp*) * 1000);
	vars = (char*)malloc(sizeof(char) * 1000);
#endif
	parse_prog(tokens, ntoken);
#ifdef INTERP
	for (i = 0; i < nlisp; i++)
	{
		lisp_free(&lsps[i]);
	}
	free(lsps);
	free(vars);
#else
	printf("Parsed OK\n");
#endif
	free(tokens);
	return 0;
}
