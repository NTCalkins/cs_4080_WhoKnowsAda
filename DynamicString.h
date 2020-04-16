#pragma once
#include "DynamicString.c"

struct DynamicString;
void DynamicString_clear(struct DynamicString*);
void DynamicString_append(struct DynamicString*, char*);
void DynamicString_copy(struct DynamicString*, char*);
struct DynamicString makeString();
inline void deleteString(struct DynamicString*);
