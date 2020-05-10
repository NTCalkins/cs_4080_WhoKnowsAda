#pragma once
#include "LinkList.c"

struct LinkList;

void LinkList_clear(struct LinkList*); /* Clear contents of list */
void LinkList_insert(struct LinkList*, struct Node*); /* Insert node after current position */
void LinkList_append(struct LinkList*, struct Node*); /* Insert node at end of list */
void LinkList_remove(struct LinkList*); /* Remove curr->next */
void LinkList_moveToStart(struct LinkList*); /* Move current position to head */
void LinkList_moveToEnd(struct LinkList*); /* Move current position to tail */
void LinkList_prev(struct LinkList*); /* Shift current position one left */
void LinkList_next(struct LinkList*); /* Shift current position one right */
unsigned int LinkList_currPos(struct LinkList*); /* Return position of current element */
void LinkList_move(struct LinkList*, unsigned int); /* Move to specified position */
unsigned int LinkList_size(struct LinkList*); /* Return current size of list */

struct LinkList makeList(void); /* Initialize a LinkList */
void deleteList(struct LinkList*); /* Clean up a LinkList */
