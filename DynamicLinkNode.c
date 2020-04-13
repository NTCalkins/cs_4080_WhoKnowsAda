/* Node that uses a dynamic length string implementation */
#pragma once
#include "DynamicString.c"

struct Node
{
    struct DynamicString string;
    struct Node *prev;
    struct Node *next;
    void (*copy)(struct Node*, char*);
    void (*append)(struct Node*, char*);
    char* (*get)(struct Node*); /* Return pointer to c-string */
};

void Node_copy(struct Node *self, char *s)
{
    self->string.copy(&(self->string), s);
}

void Node_append(struct Node *self, char *s)
{
    self->string.append(&(self->string), s);
}

char* Node_get(struct Node *self)
{
    return self->string.string;
}

struct Node* makeNode()
{
    struct Node *result = malloc(sizeof(struct Node));
    result->string = makeString();
    result->prev = NULL;
    result->next = NULL;
    result->copy = Node_copy;
    result->append = Node_append;
    result->get = Node_get;
    return result;
}

inline void deleteNode(struct Node *it)
{
    deleteString(&(it->string));
}
