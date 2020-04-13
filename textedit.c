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
{
    /* Data */
    /* NOTE: Line numbers count from 1 */
    struct LinkList text; /* The text content of the buffer */
    unsigned int currAddr; /* Address used as the default argument for some commands when none is specified */
    unsigned int (*size)(struct TextBuffer*); /* Return the number of lines in the buffer */
    /* Functions */
    /* IMPORTANT: Reference documentation for details on how currAddr is affected by each of these commands */
    void (*append)(struct TextBuffer*, unsigned int, struct LinkList*); /* Append buffer of text after input line */
    void (*change)(struct TextBuffer*, unsigned int, unsigned int, struct LinkList*); /* Delete input range and replace with buffer of text */
    void (*delete)(struct TextBuffer*, unsigned int, unsigned int); /* Delete input range */
    void (*join)(struct TextBuffer*, unsigned int, unsigned int); /* Replace input range with their concatenation */
    void (*move)(struct TextBuffer*, unsigned int, unsigned int, unsigned int); /* Move input range to specified position */
    void (*list)(struct TextBuffer*, unsigned int, unsigned int); /* Print out input range */
    void (*number)(struct TextBuffer*, unsigned int, unsigned int); /* Print out input range with line numbers */
    void (*transfer)(struct TextBuffer*, unsigned int, unsigned int, unsigned int); /* Copy input range and insert at specified position */
    void (*write)(struct TextBuffer*, char*); /* Write current buffer to file */
    void (*edit)(struct TextBuffer*, char*); /* Load contents of a file */
};
/* TODO: Implement member functions*/
void TextBuffer_append(struct TextBuffer *self, unsigned int line, struct LinkList *buff) /* Current address is set to last line entered */
{
    
}

void TextBuffer_change(struct TextBuffer *self, unsigned int line1, unsigned int line2, struct LinkList *buff) /* Current address set to last line entered or, if none, line after last deleted */
{
    
}

void TextBuffer_delete(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to after last line deleted */
{
    
}

void TextBuffer_join(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to the joined line */
{
    
}

void TextBuffer_move(struct TextBuffer *self, unsigned int line1, unsigned int line2, unsigned int line3) /* Current address is set to new address of last line moved */
{

}

void TextBuffer_list(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to last line printed */
{
    assert(line2 >= line1 && line1 > 0 && line1 < self->text.count);
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    self->currAddr = line1 - 1;
    for (unsigned int i = line1; i <= line2; ++i)
    {
	++(self->currAddr); /* Set current address to current line */
	struct Node *temp = self->text.getCurr(&(self->text)); /* Get the relevant node */
	puts(temp->get(temp)); /* Print string contents of node */
	self->text.next(&(self->text)); /* Advance position of buffer */
    }
    self->text.prev(&(self->text)); /* Shift current position in link list back to last node printed */    
}

void TextBuffer_number(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to last line printed */
{
    assert(line2 >= line1 && line1 > 0 && line1 < self->text.count);
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    self->currAddr = line1 - 1;
    for (unsigned int i = line1; i <= line2; ++i)
    {
	++(self->currAddr); /* Set current address to current line */
	struct Node *temp = self->text.getCurr(&(self->text)); /* Get the relevant node */
	printf("%d\t%s", i, temp->get(temp)); /* Print line number and string contents of node */
	self->text.next(&(self->text)); /* Advance position of buffer */
    }
    self->text.prev(&(self->text)); /* Shift current position in link list back to last node printed */
}

void TextBuffer_transfer(struct TextBuffer *self, unsigned int line1, unsigned int line2, unsigned int line3) /* Current address is set to last line copied */
{
    
}

void TextBuffer_write(struct TextBuffer *self, char *file) /* Current address is unchanged */
{
    
}

void TextBuffer_edit(struct TextBuffer *self, char *file) /* Current address is set to last line in buffer */
{
    
}

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
{ /* TODO: Needs testing */
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
