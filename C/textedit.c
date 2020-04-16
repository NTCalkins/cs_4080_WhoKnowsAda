#include <stdio.h>
#include <stdbool.h>
#include <assert.h>
#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include "LinkList.h"

#define INVALID_ADDR -1 /* Value to indicate invalid address */
#define INVALID_CMD '?' /* Value to indicate invalid command */
#define BUFF_LEN 256 /* Maximum length of c-string buffer for I/O */
#define DEFAULT_FILE "out.txt" /* Default file name */

struct TextBuffer
{
    /* Data */
    struct LinkList text; /* The text content of the buffer */
    bool changesMade; /* Changes made since last write to file */
    unsigned int (*currAddr)(struct TextBuffer*); /* Address used as the default argument for some commands when none is specified */
    unsigned int (*size)(struct TextBuffer*); /* Return the number of lines in the buffer */
    /* Functions */
    /* IMPORTANT: Reference documentation for details on how currAddr is affected by each of these commands */
    void (*append)(struct TextBuffer*, unsigned int, struct LinkList*); /* Append buffer of text after input line */
    void (*delete)(struct TextBuffer*, unsigned int, unsigned int); /* Delete input range */
    void (*change)(struct TextBuffer*, unsigned int, unsigned int, struct LinkList*); /* Delete input range and replace with buffer of text */
    void (*join)(struct TextBuffer*, unsigned int, unsigned int); /* Replace input range with their concatenation */
    void (*move)(struct TextBuffer*, unsigned int, unsigned int, unsigned int); /* Move input range to specified position */
    void (*print)(struct TextBuffer*, unsigned int, unsigned int); /* Print out input range */
    void (*list)(struct TextBuffer*, unsigned int, unsigned int); /* Print input range unambiguously */
    void (*number)(struct TextBuffer*, unsigned int, unsigned int); /* Print out input range with line numbers */
    void (*transfer)(struct TextBuffer*, unsigned int, unsigned int, unsigned int); /* Copy input range and insert at specified position */
    void (*write)(struct TextBuffer*, unsigned int, unsigned int, char*); /* Write current buffer to file */
    void (*edit)(struct TextBuffer*, char*); /* Load contents of a file */
};

unsigned int TextBuffer_currAddr(struct TextBuffer *self)
{
    return self->text.pos;
}

unsigned int TextBuffer_size(struct TextBuffer *self)
{
    return self->text.count;
}

void TextBuffer_append(struct TextBuffer *self, unsigned int line, struct LinkList *buff) /* Current address is set to last line entered */
{
    assert(line >= 0 && line <= self->text.count);
    /* Move position in buffer for insertion after input line */
    if (self->text.pos != line)
	self->text.move(&(self->text), line);
    if (buff->count == 0) /* Do nothing if input is empty */
	return;
    /* Insert contents of buff in between curr and curr->next */
    if (self->text.curr->next != NULL)
	self->text.curr->next->prev = buff->tail;
    else /* self->text.curr is the tail */
	self->text.tail = buff->tail;
    self->text.curr->next = buff->head->next;
    self->text.count += buff->count; /* Update size of text buffer */
    self->text.curr = buff->tail; /* Update position of text buffer */
    self->text.pos += buff->count;
    /* Reinitialize buff */
    buff->tail = buff->curr = buff->head;
    buff->count = 0;
    buff->pos = 0;
    self->changesMade = true;
}

void TextBuffer_delete(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to after last line deleted */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count);
    struct Node *pos1, *pos2; /* Positions for deletion */
    unsigned int temp;
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    temp = self->text.pos;
    /* Get positions for deletion */
    pos1 = self->text.curr;
    self->text.move(&(self->text), line2);
    pos2 = self->text.curr->next;
    /* Delete nodes between pos1 and pos2 */
    self->text.curr = pos1;
    self->text.pos = temp;
    while (self->text.curr->next != pos2)
	self->text.remove(&(self->text));
    self->text.next(&self->text); /* Set current address to after deleted text */
    self->changesMade = true;
}

void TextBuffer_change(struct TextBuffer *self, unsigned int line1, unsigned int line2, struct LinkList *buff) /* Current address set to last line entered or, if none, line after last deleted */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count);
    if (buff->count == 0) /* Simply delete the lines if input buffer is empty */
	TextBuffer_delete(self, line1, line2);
    else
    {
	int changeSize = line2 - line1 + 1; /* Number of lines to be changed out */
	struct Node *pos1, *pos2; /* Positions to be changed out */
	struct Node *temp;
	/* Get positions */
	if (self->text.pos != (line1 - 1))
	    self->text.move(&(self->text), line1 - 1);
	pos1 = self->text.curr;
	self->text.move(&(self->text), line2);
	pos2 = self->text.curr->next;
	/* Swap nodes */
	temp = pos1->next;
	pos1->next = buff->head->next;
	buff->head->next->prev = pos1;
	buff->head->next = temp;
	if (pos2 != NULL)
	{
	    temp = pos2->prev;
	    temp->next = NULL;
	    pos2->prev = buff->tail;
	    pos2->prev->next = pos2;
	    buff->tail = temp;
	}
	else /* self->text.curr is the tail */
	{
	    self->text.tail = buff->tail;
	    buff->tail = self->text.curr;
	}
	self->text.count += (buff->count - changeSize); /* Update line count */
	self->text.move(&(self->text), line1 + buff->count - 1); /* Move current address to last line added */
	buff->clear(buff); /* Delete old nodes and reinitialize input buffer */
    }
    self->changesMade = true;
}

void TextBuffer_join(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to the joined line */
{
    assert(line2 > line1 && line1 > 0 && line2 <= self->text.count);
    struct Node *pos1, *pos2;
    unsigned int temp;
    if (self->text.pos != line1)
	self->text.move(&(self->text), line1);
    /* Get positions for range to be merged */
    temp = self->text.pos;
    pos1 = self->text.curr;
    self->text.move(&(self->text), line2);
    pos2 = self->text.curr->next;
    self->text.curr = pos1;
    self->text.pos = temp;
    while (self->text.curr->next != pos2) /* Append and remove each node in range */
    {
	self->text.curr->append(self->text.curr, self->text.curr->next->get(self->text.curr->next));
	self->text.remove(&(self->text));
    }
    self->changesMade = true;
}

void TextBuffer_move(struct TextBuffer *self, unsigned int line1, unsigned int line2, unsigned int line3) /* Current address is set to new address of last line moved */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count && line3 <= self->text.count);
    assert(line3 < line1 || line3 > line2);
    struct Node *pos1, *pos2, *temp;
    if (self->text.pos != line1)
	self->text.move(&(self->text), line1);
    pos1 = self->text.curr;
    if (line2 != line1)
	self->text.move(&(self->text), line2);
    pos2 = self->text.curr;
    self->text.move(&(self->text), line3);
    /* Detach nodes from list */
    pos1->prev->next = pos2->next;
    if (pos2->next != NULL)
	pos2->next->prev = pos1->prev;
    else
	self->text.tail = pos1->prev;
    pos1->prev = NULL;
    pos2->next = NULL;
    /* Attach nodes after line3 */
    if (self->text.curr->next == NULL) /* New tail */
    {
	self->text.tail = pos2;
	self->text.curr->next = pos1;
	pos1->prev = self->text.curr;
    }
    else
    {
	temp = self->text.curr->next;
	self->text.curr->next = pos1;
	pos1->prev = self->text.curr;
	pos2->next = temp;
	temp->prev = pos2;
    }
    /* Move position to new position of last line moved */
    temp = self->text.curr;
    self->text.moveToStart(&(self->text));
    while (self->text.curr != pos2)
	self->text.next(&(self->text));
    self->changesMade = true;
}

void TextBuffer_print(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to last line printed */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count);
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    for (unsigned int i = 0; i < (line2 - line1 + 1); ++i)
    {
	puts(self->text.curr->next->get(self->text.curr->next)); /* Print string contents of node */
	self->text.next(&(self->text)); /* Advance position of buffer */
    }
}

void TextBuffer_list(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to last line printed */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count);
    char *string;
    unsigned int i;
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    for (unsigned int j = line1; j <= line2; ++j)
    {
	string = self->text.curr->next->get(self->text.curr->next);
	i = 0;
	while (string[i] != '\0') /* Disambiguate special chracters */
	{
	    switch (string[i])
	    {
	    case '$':
		fputs("\\$", stdout);
		break;
	    case '\v':
		fputs("\\v", stdout);
		break;
	    case '\t':
		fputs("\\t", stdout);
		break;
	    case '\\':
		fputs("\\\\", stdout);
		break;
	    case '\'':
		fputs("\\'", stdout);
		break;
	    case '\"':
		fputs("\\\"", stdout);
		break;
	    case '\?':
		fputs("\\?", stdout);
		break;
	    default:
		putchar(string[i]);
	    }
	    ++i;
	}
	puts("$");
	self->text.next(&(self->text));
    }
}

void TextBuffer_number(struct TextBuffer *self, unsigned int line1, unsigned int line2) /* Current address is set to last line printed */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count);
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    for (unsigned int i = line1; i <= line2; ++i)
    {
	printf("%d\t%s\n", i, self->text.curr->next->get(self->text.curr->next));
	self->text.next(&(self->text));
    }
}

void TextBuffer_transfer(struct TextBuffer *self, unsigned int line1, unsigned int line2, unsigned int line3) /* Current address is set to last line copied */
{
    assert(line2 >= line1 && line1 > 0 && line2 <= self->text.count && line3 <= self->text.count);
    assert(line3 < line1 || line3 > line2);
    unsigned int transSize = line2 - line1 + 1;
    struct Node *pos1, *pos2;
    struct Node *ins;
    if (self->text.pos != line1)
	self->text.move(&(self->text), line1);
    pos1 = self->text.curr;
    if (line2 != line1)
	self->text.move(&(self->text), line2);
    pos2 = self->text.curr;
    self->text.move(&(self->text), line3);
    /* Insert lines in range after line3 */
    while (pos2 != pos1->prev)
    {
	ins = makeNode();
	ins->copy(ins, pos2->get(pos2));
	self->text.insert(&(self->text), ins);
	pos2 = pos2->prev;
    }
    /* Update position */
    for (unsigned int i = 0; i < transSize; ++i)
	self->text.next(&(self->text));
    self->changesMade = true;
}

void TextBuffer_write(struct TextBuffer *self, unsigned int line1, unsigned int line2, char *file) /* Current address is unchanged */
{
    assert(line1 > 0 && line2 >= line1 && line2 <= self->text.count);
    FILE *outFile = fopen(file, "w");
    if (outFile == NULL)
    {
	puts("Error: Could not open output file.");
	return;
    }
    struct Node *pos1, *pos2, *tempNode;
    unsigned int tempPos;
    tempNode = self->text.curr;
    tempPos = self->text.pos;
    if (self->text.pos != (line1 - 1))
	self->text.move(&(self->text), line1 - 1);
    pos1 = self->text.curr;
    self->text.move(&(self->text), line2);
    pos2 = self->text.curr->next;
    self->text.curr = tempNode;
    self->text.pos = tempPos;
    while (pos1->next != pos2)
    {
	fputs(pos1->next->get(pos1->next), outFile);
	fputc('\n', outFile);
	pos1 = pos1->next;
    }
    fclose(outFile);
    self->changesMade = false;
}

void TextBuffer_edit(struct TextBuffer *self, char *file) /* Current address is set to last line in buffer */
{
    FILE *inFile = fopen(file, "r");
    struct Node *temp;
    if (inFile == NULL)
	puts("Error: File not found.");
    else
    {
	char input[BUFF_LEN];
	self->text.clear(&(self->text)); /* Clear current text buffer */
	while (fgets(input, BUFF_LEN, inFile)) /* Read every line in file into buffer */
	{
	    if (input[strlen(input) - 1] == '\n') /* Get rid of new line character at end */
		input[strlen(input) - 1] = '\0';
	    temp = makeNode();
	    temp->copy(temp, input);
	    self->text.insert(&(self->text), temp);
	    self->text.next(&(self->text));
	}
	fclose(inFile);
	self->changesMade = false;
    }
}

struct TextBuffer makeBuffer() /* Construct and initialize a TextBuffer struct */
{
    struct TextBuffer result;
    result.text = makeList();
    result.changesMade = false;
    result.currAddr = TextBuffer_currAddr;
    result.size = TextBuffer_size;
    result.append = TextBuffer_append;
    result.delete = TextBuffer_delete;
    result.change = TextBuffer_change;
    result.join = TextBuffer_join;
    result.move = TextBuffer_move;
    result.print = TextBuffer_print;
    result.list = TextBuffer_list;
    result.number = TextBuffer_number;
    result.transfer = TextBuffer_transfer;
    result.write = TextBuffer_write;
    result.edit = TextBuffer_edit;
    return result;
}

void deleteBuffer(struct TextBuffer *it) /* Clean up a TextBuffer struct */
{
    deleteList(&(it->text));
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
	return true;
    default:
	return false;
    }
}

bool isSuffix(char c)
/* Return true if the character is a valid suffix command */
{
    switch(c)
    {
    case 'p':
    case 'l':
    case 'n':
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
	if (offset != NULL)
	    *offset = 1;
	return buff->currAddr(buff);
    case '$':
	if (offset != NULL)
	    *offset = 1;
	return buff->size(buff);
    case '+':
	if (isdigit(s[1]))
	{
	    retVal = buff->currAddr(buff) + atoi(s + 1);
	    if (offset != NULL)
		*offset = 2;
	    while (isdigit(s[*offset]))
		++offset;
	    return retVal;
	}
	else
	{
	    if (offset != NULL)
		*offset = 1;
	    return buff->currAddr(buff) + 1;
	}
    case '-':
	if (isdigit(s[1]))
	{
	    retVal = buff->currAddr(buff) - atoi(s + 1);
	    if (offset != NULL)
	    {
		*offset = 2;
		while (isdigit(s[*offset]))
		++offset;
	    }
	    return retVal;
	}
	else
	{
	    if (offset != NULL)
		*offset = 1;
	    return buff->currAddr(buff) - 1;
	}
    default:
	if (offset != NULL)
	    *offset = 0;
	return INVALID_ADDR;
    }
}

void parse(struct TextBuffer *buff, char *input, int *addr1, int *addr2, char *command, char *param)
/* Parse the user inputted line */
{
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
	if (isblank(input[pos]))
	    continue;
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
	    *addr1 = buff->currAddr(buff);
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

void inputMode(char *input, struct LinkList *buff) /* Enter input mode */
{
    struct Node *temp;
    while (strcmp(fgets(input, BUFF_LEN, stdin), ".\n") != 0)
    {
	/* Append inputed lines to input buffer */
	if (input[strlen(input) - 1] == '\n')
	    input[strlen(input) - 1] = '\0';
	temp = makeNode();
	temp->copy(temp, input);
	buff->append(buff, temp);
    }
}

char processSuffix(char *s) /* Get valid suffix command from string if present */
{
    char command = INVALID_CMD;
    unsigned int pos = 0;
    while (isSuffix(s[pos]))
    {
	command = s[pos];
	++pos;
    }
    return command;
}

int main(int argc, char **argv)
{
    bool done = false;
    char input[BUFF_LEN] = {}; /* Line input from user */
    char command; /* Command arguments */
    int addr1, addr2, addr3; /* Line address arguments */
    char param[BUFF_LEN] = {}; /* Prameter argument */
    char defaultFile[BUFF_LEN] = "file"; /* Default file name */
    struct TextBuffer textBuff = makeBuffer(); /* Buffer of text for the document */
    struct LinkList inputBuff = makeList(); /* Buffer of input lines for input mode */
    while (!done)
    {
	fgets(input, BUFF_LEN, stdin);
	if (input[strlen(input) - 1] == '\n') /* Trim the excess newline character */
	    input[strlen(input) - 1] = '\0';
	parse(&textBuff, input, &addr1, &addr2, &command, param);
	if (command == INVALID_CMD && addr1 != INVALID_ADDR) /* Only an address was entered */
	    textBuff.text.move(&(textBuff.text), addr1);
	else
	{
	    switch (command)
	    {
	    case 'a': /* Enter input mode and insert after addressed line */
		if (addr1 != INVALID_ADDR && addr2 != INVALID_ADDR) /* Received 2 address arguments */
		{
		    puts("Error: This command does not take an address range.");
		    break;
		}
		else if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = textBuff.text.pos; /* Use current address */
		if (addr1 >= 0 && addr1 <= (int) textBuff.text.count)
		{
		    inputMode(input, &inputBuff); /* Enter input mode and fill input buffer */
		    textBuff.append(&textBuff, addr1, &inputBuff); /* Insert input buffer after inputted address */
		    command = processSuffix(param); /* Look for a suffix command */
		    if (command != INVALID_CMD)
			switch (command)
			{
			case 'p':
			    textBuff.print(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'l':
			    textBuff.list(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'n':
			    textBuff.number(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			}
		}
		else
		    puts("Error: Invalid address.");
		break;
	    case 'i': /* Enter input mode and insert before addressed line */
		if (addr1 != INVALID_ADDR && addr2 != INVALID_ADDR) /* Received 2 address arguments */
		{
		    puts("Error: This command does not take an address range.");
		    break;
		}
		else if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = textBuff.text.pos; /* Use current address */
		if (addr1 >= 0 && addr1 <= (int) textBuff.text.count)
		{
		    inputMode(input, &inputBuff); /* Enter input mode and fill input buffer */
		    textBuff.append(&textBuff, (addr1 == 0) ? 0 : (addr1 - 1), &inputBuff); /* Insert input buffer before inputted address */
		    command = processSuffix(param); /* Look for a suffix command */
		    if (command != INVALID_CMD)
			switch (command)
			{
			case 'p':
			    textBuff.print(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'l':
			    textBuff.list(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'n':
			    textBuff.number(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			}
		}
		else
		    puts("Error: Invalid address.");
		break;
	    case 'c': /* Enter input mode and change out addressed lines with input buffer */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count)
		{
		    inputMode(input, &inputBuff); /* Enter input mode and fill input buffer */
		    textBuff.change(&textBuff, addr1, addr2, &inputBuff);
		}
		else
		    puts("Error: Invalid address.");
		break;
	    case 'd': /* Delete addressed lines from buffer */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count)
		    textBuff.delete(&textBuff, addr1, addr2);
		else
		    puts("Error: Invalid address.");
		break;
	    case 'j': /* Replace addressed lines with their concatenation */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		{
		    addr1 = textBuff.text.pos;
		    addr2 = addr1 + 1;
		}
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		{
		    puts("Error: This command requires an address range.");
		    break;
		}
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count)
		{
		    textBuff.join(&textBuff, addr1, addr2);
		    command = processSuffix(param); /* Look for a suffix command */
		    if (command != INVALID_CMD)
			switch (command)
			{
			case 'p':
			    textBuff.print(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'l':
			    textBuff.list(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'n':
			    textBuff.number(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			}
		}
		else
		    puts("Error: Invalid address.");
		break;
	    case 'p': /* Print out addressed lines */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count)
		    textBuff.print(&textBuff, addr1, addr2);
		else
		    puts("Error: Invalid address.");
		break;
	    case 'l': /* Print out addressed lines unambiguously */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count)
		    textBuff.list(&textBuff, addr1, addr2);
		else
		    puts("Error: Invalid address.");
		break;
	    case 'n': /* Print and number addressed lines */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count)
		    textBuff.number(&textBuff, addr1, addr2);
		else
		    puts("Error: Invalid address.");
		break;
	    case 'm': /* Moves addressed lines to after the destination address */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (strlen(param) == 0) /* No third address given */
		    addr3 = textBuff.text.pos; /* Use current address */
		else /* Read in addr3 from param */
		{
		    if (isSpecial(param[0]))
			addr3 = interpretSpecial(&textBuff, param, NULL);
		    else if (isdigit(param[0]))
			addr3 = atoi(param);
		}
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count && addr3 != INVALID_ADDR && (addr3 < addr1 || addr3 > addr2) && addr3 <= (int) textBuff.text.count)
		{
		    textBuff.move(&textBuff, addr1, addr2, addr3);
		    command = processSuffix(param); /* Look for a suffix command */
		    if (command != INVALID_CMD)
			switch (command)
			{
			case 'p':
			    textBuff.print(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'l':
			    textBuff.list(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'n':
			    textBuff.number(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			}
		}
		else
		    puts("Error: Invalid address.");
		break;
	    case 't': /* Copies addressed lines to after the destination address */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		    addr1 = addr2 = textBuff.text.pos; /* Use current address */
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (strlen(param) == 0) /* No third address given */
		    addr3 = textBuff.text.pos; /* Use current address */
		else /* Read in addr3 from param */
		{
		    if (isSpecial(param[0]))
			addr3 = interpretSpecial(&textBuff, param, NULL);
		    else if (isdigit(param[0]))
			addr3 = atoi(param);
		}
		if (addr2 >= addr1 && addr1 > 0 && addr2 <= (int) textBuff.text.count && addr3 != INVALID_ADDR && (addr3 < addr1 || addr3 > addr2) && addr3 <= (int) textBuff.text.count)
		{
		    textBuff.transfer(&textBuff, addr1, addr2, addr3);
		    command = processSuffix(param); /* Look for a suffix command */
		    if (command != INVALID_CMD)
			switch (command)
			{
			case 'p':
			    textBuff.print(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'l':
			    textBuff.list(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			case 'n':
			    textBuff.number(&textBuff, textBuff.text.pos, textBuff.text.pos);
			    break;
			}
		}
		else
		    puts("Error: Invalid address.");
		break;
	    case 'w': /* Write to file */
		if (addr1 == INVALID_ADDR && addr2 == INVALID_ADDR) /* Received no address arguments */
		{
		    /* Use whole buffer */
		    addr1 = 1;
		    addr2 = textBuff.text.count;
		}
		else if (addr2 == INVALID_ADDR) /* Received only one address */
		    addr2 = addr1; /* Affect only addr1 */
		if (strlen(param) == 0) /* Use default file name if none is specified */
		    strcpy(param, defaultFile);
		else
		    strcpy(defaultFile, param); /* Set default file name to given file name */
		if (addr1 > 0 && addr1 <= (int) textBuff.text.count)
		    textBuff.write(&textBuff, addr1, addr2, param);
		else
		    puts("Error: Invalid address.");
		break;
	    case 'e': /* Load contents from file */
		if (strlen(param) == 0) /* Use default file name if none is specified */
		    strcpy(param, defaultFile);
		else
		    strcpy(defaultFile, param); /* Set default file name to given file name */
		textBuff.edit(&textBuff, param);
		break;
	    case 'q': /* Quit */
		if (textBuff.changesMade)
		{
		    input[0] = '\0';
		    while (strcmp(input, "yes\n") != 0 && strcmp(input, "no\n") != 0)
		    {
			printf("Unsaved changes in buffer. Still quit? (yes/no): ");
			fgets(input, BUFF_LEN, stdin);
		    }
		    if (strcmp(input, "yes\n") == 0)
			done = true;
		}
		else
		    done = true;
		break;
	    default:
		puts("Error: Unknown command.");
	    }
	}
    }
    return 0;
}
