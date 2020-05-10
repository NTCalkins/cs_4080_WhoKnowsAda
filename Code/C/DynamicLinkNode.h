#pragma once
#include "DynamicLinkNode.c"

struct Node;
void Node_copy(struct Node*, char*); /* Copy contents of string into node */
void Node_append(struct Node*, char*); /* Append contents of string into node */
char* Node_get(struct Node*); /* Get pointer to string contents of node */
struct Node* makeNode(void); /* Construct a Node */
void deleteNode(struct Node*); /* Clean up a Node */
