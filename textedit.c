#include <stdio.h>
#include <stdbool.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "LinkList.c"

#define INVALID_ADDR -1 /* Value to indicate invalid address */
#define INVALID_CMD '?' /* Value to indicate invalid command */

struct TextBuffer
{ /* TODO: Finish implementing this */
    struct LinkList text; /* The text content of the buffer */
    unsigned int currAddr; /* Address used as the default argument for some commands when none is specified */
    unsigned int (*size)(struct TextBuffer*); /* Return the number of lines in the buffer */
};

int main(int argc, char **argv)
{
    puts("Success!\n");
    return 0;
}

bool isCommand(char c)
/* Return true if the character is a valid command */
{
    switch (c)
    {
    case 'a':
    case 'c':
    case 'd':
    case 'j':
    case 'l':
    case 'm':
    case 'n':
    case 'p':
    case 't':
    case 'w':
    case 'e':
    case 'q':
	return true;
    default:
	return false;
    }
}

bool isSpecial(char c)
/* Return true if the character has a special meaning when passed as an argument */
{
    switch (c)
    {
    case '.':
    case '$':
    case '+':
    case '-':
    case ',':
    case ';':
	return true;
    default:
	return false;
    }
}

unsigned int interpretSpecial(struct TextBuffer *buff, char *s, unsigned int *offset)
/* Interpret the value of the special substring relative to the buffer.
   Does not include the special characters ',' and ';'. Returns how many
   characters were read as offset. */
{
    int retVal;
    switch (s[0])
    {
    case '.':
	*offset = 1;
	return buff->currAddr;
    case '$':
	*offset = 1;
	return buff->size(buff);
    case '+':
	if (isdigit(s[1]))
	{
	    retVal = buff->currAddr + atoi(s + 1);
	    *offset = 2;
	    while (isdigit(s[*offset]))
		++offset;
	    return retVal;
	}
	else
	{
	    *offset = 1;
	    return buff->currAddr + 1;
	}
    case '-':
	if (isdigit(s[1]))
	{
	    retVal = buff->currAddr - atoi(s + 1);
	    *offset = 2;
	    while (isdigit(s[*offset]))
		++offset;
	    return retVal;
	}
	else
	{
	    *offset = 1;
	    return buff->currAddr - 1;
	}
    default:
	*offset = 0;
	return INVALID_ADDR;
    }
}

void parse(struct TextBuffer *buff, char *input, int *addr1, int *addr2, char *command, char *param)
/* Parse the user inputted line */
{ /* TODO: Test this */
    unsigned int pos = 0;
    unsigned int temp = 0;
    const unsigned int inputLength = strlen(input);
    *addr1 = *addr2 = INVALID_ADDR;
    param[0] = '\0';
    *command = INVALID_CMD;
    while (isblank(input[pos]))
	++pos;
    while (pos < inputLength)
    {
	if (isdigit(input[pos])) /* Argument is a number */
	{
	    *addr2 = INVALID_ADDR; /* Reset addr2 */
	    *addr1 = atoi(input + pos); /* Extract the number from the input string and store as addr1 */
	    while (isdigit(input[pos])) /* Move current position past the extracted number */
		++pos;
	    if (input[pos] == ',' || input[pos] == ';') /* A second address is given */
	    {
		++pos;
		if (isdigit(input[pos]))
		{
		    *addr2 = atoi(input + pos); /* Extract number and store in addr2 */
		    while (isdigit(input[pos]))
			++pos;
		}
		else if (isSpecial(input[pos])) /* Special character for addr2 */
		{
		    *addr2 = interpretSpecial(buff, input + pos, &temp);
		    pos += temp;
		}
		else /* Read input as addr2 */
		{
		    *addr2 = *addr1;
		    *addr1 = INVALID_ADDR;
		}
	    }
	}
	else if (isSpecial(input[pos])) /* Special character for addr1 */
	{
	    *addr2 = INVALID_ADDR;
	    *addr1 = interpretSpecial(buff, input + pos, &temp);
	    pos += temp;
	    if (input[pos] == ',' || input[pos] == ';') /* A second address is given */
	    {
		++pos;
		if (isdigit(input[pos]))
		{
		    *addr2 = atoi(input + pos); /* Extract number and store in addr2 */
		    while (isdigit(input[pos]))
			++pos;
		}
		else if (isSpecial(input[pos])) /* Special character for addr2 */
		{
		    *addr2 = interpretSpecial(buff, input + pos, &temp);
		    pos += temp;
		}
		else /* Read input as addr2 */
		{
		    *addr2 = *addr1;
		    *addr1 = INVALID_ADDR;
		}
	    }
	}
	/* Special cases for ',' and ';' */
	else if (input[pos] == ',') /* First through specified or last line of the buffer */
	{
	    *addr1 = 1;
	    ++pos;
	    if (isdigit(input[pos])) /* addr2 is numerically defined */
	    {
		*addr2 = atoi(input + pos);
		while (isdigit(input[pos]))
		    ++pos;
	    }
	    else if (isSpecial(input[pos])) /* addr2 is defined by a special character */
	    {
		*addr2 = interpretSpecial(buff, input + pos, &temp);
		pos += temp;
	    }
	    else
		*addr2 = buff->size(buff);
	}
	else if (input[pos] == ';') /* Current through specified or last line of the buffer */
	{
	    *addr1 = buff->currAddr;
	    ++pos;
	    if (isdigit(input[pos]))
	    {
		*addr2 = atoi(input + pos);
		while (isdigit(input[pos]))
		    ++pos;
	    }
	    else if (isSpecial(input[pos]))
	    {
		*addr2 = interpretSpecial(buff, input + pos, &temp);
		pos += temp;
	    }
	    else
		*addr2 = buff->size(buff);
	}
	else if (isCommand(input[pos])) /* Current token is a command */
	{
	    *command = input[pos];
	    ++pos;
	    while (isblank(input[pos])) /* Search for param */
		++pos;
	    if (input[pos] != '\0') /* Extract the param string */
		strcpy(param, input + pos);
	    return;
	}
	else /* Invalid character encountered */
	{
	    puts("Warning: Unrecognized character inputted.");
	    ++pos;
	}
    }
}
