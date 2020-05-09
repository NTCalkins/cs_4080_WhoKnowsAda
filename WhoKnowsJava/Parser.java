public class Parser
{
    public static final int INVALID_ADDR = -1;
    public static final char INVALID_CMD = '?';
    private int addr1;
    private int addr2;
    private int addr3;
    private char command;
    private String param;

    public Parser()
    {
        addr1 = addr2 = addr3 = INVALID_ADDR;
        command = INVALID_CMD;
    }
    
    private boolean isCommand(char c)
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

    private boolean isSpecial(char c)
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

    private int atoi(String s) /* Implementation of atoi from C */
    {
        int offset = 1;
        int radix = 10;
        int result = 0;
        int i = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i)))
        {
            result *= 10;
            result += Character.getNumericValue(s.charAt(i));
        }
        return result;
    }

    public int interpretSpecial(int currAddr, int buffSize, String s, Integer offset)
    {
        int retVal;
        switch (s.charAt(0))
        {
        case '.':
            offset = 1;
            return currAddr;
        case '$':
            offset = 1;
            return buffSize;
        case '+':
            if (Character.isDigit(s.charAt(1)))
            {
                retVal = currAddr + atoi(s.substring(1));
                offset = 2;
                while (Character.isDigit(s.charAt(offset)))
                    ++offset;
                return retVal;
            }
            else
            {
                offset = 1;
                return currAddr + 1;
            }
        case '-':
            if (Character.isDigit(s.charAt(1)))
            {
                retVal = currAddr - atoi(s.substring(1));
                offset = 2;
                while (Character.isDigit(s.charAt(offset)))
                    ++offset;
                return retVal;
            }
            else
            {
                offset = 1;
                return currAddr - 1;
            }
        default:
            offset = 0;
            return INVALID_ADDR;
        }
    }

    public void parse(int currentAddress, int buffSize, String input)
    { /* Parser implementation translated from that of the C version */
        int pos = 0;
        Integer temp = 0;
        param = "";
        addr1 = addr2 = addr3 = INVALID_ADDR;
        command = INVALID_CMD;
        while (pos < input.length())
        {
            if (Character.isWhitespace(input.charAt(pos)))
                ++pos;
            else if (Character.isDigit(input.charAt(pos))) /* Argument is a number */
            {
                addr2 = INVALID_ADDR; /* Reset address 2 */
                addr1 = atoi(input.substring(pos)); /* Extract the number from input and store as address 1 */
                while (Character.isDigit(input.charAt(pos))) /* Move current position past the extracted number */
                    ++pos;
                if (input.charAt(pos) == ',' || input.charAt(pos) == ';') /* A second address is given */
                {
                    ++pos;
                    if (Character.isDigit(input.charAt(pos)))
                    {
                        addr2 = atoi(input.substring(pos)); /* Extract number and store in address 2 */
                        while (Character.isDigit(input.charAt(pos)))
                            ++pos;
                    }
                    else if (isSpecial(input.charAt(pos))) /* Special character for address 2 */
                    {
                        addr2 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                        pos += temp;
                    }
                    else /* Read input as address 2*/
                    {
                        addr2 = addr1;
                        addr1 = INVALID_ADDR;
                    }
                }
            }
            else if (isSpecial(input.charAt(pos))) /* Special character for address 1 */
            {
                addr2 = INVALID_ADDR;
                addr1 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                pos += temp;
                if (input.charAt(pos) == ',' || input.charAt(pos) == ';') /* A second address is given */
                {
                    ++pos;
                    if (Character.isDigit(input.charAt(pos)))
                    {
                        addr2 = atoi(input.substring(pos)); /* Extract number and store in address 2 */
                        while (Character.isDigit(input.charAt(pos)))
                            ++pos;
                    }
                    else if (isSpecial(input.charAt(pos))) /* Special character for address 2 */
                    {
                        addr2 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                        pos += temp;
                    }
                    else /* Read input as address 2*/
                    {
                        addr2 = addr1;
                        addr1 = INVALID_ADDR;
                    }
                }
            }
            /* Special cases for ',' and ';' */
            else if (input.charAt(pos) == ',') /* First through specified or last line of the buffer */
            {
                addr1 = 1;
                ++pos;
                if (Character.isDigit(input.charAt(pos))) /* address 2 is numerically defined */
                {
                    addr2 = atoi(input.substring(pos));
                    while (Character.isDigit(input.charAt(pos)))
                        ++pos;
                }
                else if (isSpecial(input.charAt(pos))) /* address 2 is defined by a special character */
                {
                    addr2 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                    pos += temp;
                }
                else
                    addr2 = buffSize;
            }
            else if (input.charAt(pos) == ';') /* Current through specified or last line of the buffer */
            {
                addr1 = currentAddress;
                ++pos;
                if (Character.isDigit(input.charAt(pos))) /* address 2 is numerically defined */
                {
                    addr2 = atoi(input.substring(pos));
                    while (Character.isDigit(input.charAt(pos)))
                        ++pos;
                }
                else if (isSpecial(input.charAt(pos))) /* address 2 is defined by a special character */
                {
                    addr2 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                    pos += temp;
                }
                else
                    addr2 = buffSize;
            }
            else if (isCommand(input.charAt(pos))) /* Current token is a command */
            {
                command = input.charAt(pos);
                ++pos;
                while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) /* Search for param */
                    ++pos;
                if (pos < input.length())
                    param = input.substring(pos);
                return;
            }
            else /* Invalid character encountered */
            {
                System.out.println("Warning: Unrecognized character inputted.");
                ++pos;
            }
        }
    }
    public int getLine1()
    {
        return addr1;
    }

    public int getLine2()
    {
        return addr2;
    }

    public int getLine3()
    {
        return addr3;
    }

    public char getCommand()
    {
        return command;
    }
}
