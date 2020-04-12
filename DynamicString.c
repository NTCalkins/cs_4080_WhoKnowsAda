#pragma once
#include <string.h>
#include <stdlib.h>

#define INIT_SIZE 10 /* The initial length for new dynamic strings */

struct DynamicString /* Simple Dynamic Length String ADT */
{
    unsigned int maxSize; /* Current max length of the string */
    char *string;
    void (*clear)(struct DynamicString*); /* Empty the contents of the string */
    void (*append)(struct DynamicString*, char*); /* Append contents of a c-string to the DynamicString */
    void (*copy)(struct DynamicString*, char*); /* Copy contents of a c-string into the DynamicString */
};

void DynamicString_clear (struct DynamicString *self)
{
    self->string[0] = '\0';
}

void DynamicString_append(struct DynamicString *self, char *s)
{
    unsigned int resultLength = strlen(s) + strlen(self->string) + 1;
    if (resultLength > self->maxSize)
    {
	unsigned int newSize = self->maxSize * 2;
	while (newSize < resultLength)
	    newSize *= 2;
	self->string = realloc(self->string, newSize);
	self->maxSize = newSize;
    }
    strcat(self->string, s);
}

void DynamicString_copy(struct DynamicString *self, char *s)
{
    unsigned int resultLength = strlen(s) + 1;
    if (resultLength > self->maxSize)
    {
	unsigned int newSize = self->maxSize * 2;
	while (newSize < resultLength)
	    newSize *= 2;
	self->string = realloc(self->string, newSize);
	self->maxSize = newSize;
    }
    strcpy(self->string, s);
}

struct DynamicString makeString() /* Constructor for DynamicString */
{
    struct DynamicString result;
    result.maxSize = INIT_SIZE;
    result.string = malloc(sizeof(char) * INIT_SIZE);
    result.string[0] = '\0';
    result.clear = DynamicString_clear;
    result.append = DynamicString_append;
    result.copy = DynamicString_copy;
    return result;
}

inline void deleteString(struct DynamicString *it) /* Destructor for DynamicString */
{
    free(it->string);
}
