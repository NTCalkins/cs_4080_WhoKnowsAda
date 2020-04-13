/* Double linked list implementation */
#pragma once
#include "StaticLinkNode.c" /* Use static length strings in node */
// #include "DynamicLinkNode.c" /* Use dynamic length strings in node */

struct LinkList
{
    struct Node *head; /* The start of the list. Note that this is an empty node and the first non-empty element of the list is head->next */
    struct Node *tail; /* The last node in the list. Only points to an empty node if the */
    struct Node *curr; /* Current position in the list. Current element is actually curr->next. */
    unsigned int count; /* Number of nodes in the list */
    unsigned int pos; /* Current numerical position in list */
    void (*clear)(struct LinkList*); /* Clear the contents of the list */
    void (*insert)(struct LinkList*, struct Node*); /* Insert node before node at current position */
    void (*append)(struct LinkList*, struct Node*); /* Insert node at end of list */
    void (*remove)(struct LinkList*); /* Remove the current node */
    void (*moveToStart)(struct LinkList*); /* Move current position to head */
    void (*moveToEnd)(struct LinkList*); /* Move current position to tail */
    void (*prev)(struct LinkList*); /* Shift current position one left */
    void (*next)(struct LinkList*); /* Shift current position one right */
    unsigned int (*currPos)(struct LinkList*); /* Return position of current element */
    void (*move)(struct LinkList*, unsigned int); /* Move to specified position */
    unsigned int (*size)(struct LinkList*); /* Return current size of the list */
    struct Node* (*getCurr)(struct LinkList*); /* Return pointer to current node (curr->next) */
};

void LinkList_clear(struct LinkList *self)
{
    while (self->head != NULL)
    {
	self->curr = self->head;
	self->head = self->head->next;
	deleteNode(self->curr);
	free(self->curr);
    }
    self->head = self->tail = self->curr = makeNode();
    self->count = self->pos = 0;
}

void LinkList_insert(struct LinkList *self, struct Node *it)
{
    it->next = self->curr->next;
    it->prev = self->curr;
    self->curr->next = it;
    if (self->tail == self->curr) /* New tail */
	self->tail = self->curr->next;
    else
	it->next->prev = it;
    ++(self->count);
}

void LinkList_append(struct LinkList *self, struct Node *it)
{
    self->tail = self->tail->next = it;
    ++(self->count);
}

void LinkList_remove(struct LinkList *self)
{
    struct Node *temp = self->curr->next;
    if (self->tail == self->curr->next)
	self->tail = self->curr;
    else
    {
	self->curr->next = self->curr->next->next;
	self->curr->next->prev = self->curr;
    }
    deleteNode(temp); /* Deconstruct the node's data */
    free(temp);
    --(self->count);
}

void LinkList_moveToStart(struct LinkList *self)
{
    self->curr = self->head;
    self->pos = 0;
}

void LinkList_moveToEnd(struct LinkList *self)
{
    self->curr = self->tail;
    self->pos = self->count;
}

void LinkList_prev(struct LinkList *self)
{
    if (self->curr != self->head)
	self->curr = self->curr->prev;
    --(self->pos);
}

void LinkList_next(struct LinkList *self)
{
    if (self->curr != self->tail)
	self->curr = self->curr->next;
    ++(self->pos);
}

unsigned int LinkList_currPos(struct LinkList *self)
{
    return self->pos;
}

void LinkList_move(struct LinkList *self, unsigned int pos)
{
    if (pos < 0 || pos >= self->count)
	return;
    self->curr = self->head;
    for (unsigned int i = 0; i < pos; ++i)
	self->curr = self->curr->next;
}

inline unsigned int LinkList_size(struct LinkList *self)
{
    return self->count;
}

inline struct Node* LinkList_getCurr(struct LinkList *self)
{
    return self->curr->next;
}

struct LinkList makeList()
{
    struct LinkList result;
    result.head = result.tail = result.curr = makeNode();
    result.count = 0;
    result.pos = 0;
    result.clear = LinkList_clear;
    result.insert = LinkList_insert;
    result.append = LinkList_append;
    result.remove = LinkList_remove;
    result.moveToStart = LinkList_moveToStart;
    result.moveToEnd = LinkList_moveToEnd;
    result.prev = LinkList_prev;
    result.next = LinkList_next;
    result.currPos = LinkList_currPos;
    result.move = LinkList_move;
    result.size = LinkList_size;
    result.getCurr = LinkList_getCurr;
    return result;
}

inline void deleteList(struct LinkList *it)
{
    while (it->head != NULL)
    {
	it->curr = it->head;
	it->head = it->head->next;
	free(it->curr);
    }
}
