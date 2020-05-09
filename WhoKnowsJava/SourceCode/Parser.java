public class Parser
{
    private class MutableInt /* Wrapper to allow passing integer by reference */
    {
        public Integer value;
        public MutableInt()
        {
            value = 0;
        }
    }
    /* All members are public for efficiency */
    public static final int INVALID_ADDR = -1;
    public static final char INVALID_CMD = '?';
    public int addr1;
    public int addr2;
    public int addr3;
    public char command;
    public String param;

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
        int radix = 10;
        int result = 0;
        int i = 0;
        while (i < s.length() && Character.isDigit(s.charAt(i)))
        {
            result *= radix;
            result += Character.getNumericValue(s.charAt(i));
            ++i;
        }
        return result;
    }

    public int interpretSpecial(int currAddr, int buffSize, String s, MutableInt offset)
    {
        int retVal;
        switch (s.charAt(0))
        {
        case '.':
            offset.value = 1;
            return currAddr;
        case '$':
            offset.value = 1;
            return buffSize;
        case '+':
            if (s.length() > 1 && Character.isDigit(s.charAt(1)))
            {
                retVal = currAddr + atoi(s.substring(1));
                offset.value = 2;
                while (offset.value < s.length() && Character.isDigit(s.charAt(offset.value)))
                    ++offset.value;
                return retVal;
            }
            else
            {
                offset.value = 1;
                return currAddr + 1;
            }
        case '-':
            if (s.length() > 1 && Character.isDigit(s.charAt(1)))
            {
                retVal = currAddr - atoi(s.substring(1));
                offset.value = 2;
                while (offset.value < s.length() && Character.isDigit(s.charAt(offset.value)))
                    ++offset.value;
                return retVal;
            }
            else
            {
                offset.value = 1;
                return currAddr - 1;
            }
        default:
            offset.value = 0;
            return INVALID_ADDR;
        }
    }

    public void parse(int currentAddress, int buffSize, String input)
    { /* Parser implementation translated from that of the C version */
        int pos = 0;
        MutableInt temp = new MutableInt();
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
                while (pos < input.length() && Character.isDigit(input.charAt(pos))) /* Move current position past the extracted number */
                    ++pos;
                if (pos < input.length())
                {
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
                            pos += temp.value;
                        }
                        else /* Read input as address 2*/
                        {
                            addr2 = addr1;
                            addr1 = INVALID_ADDR;
                        }
                    }
                }
            }
            else if (isSpecial(input.charAt(pos))) /* Special character for address 1 */
            {
                addr2 = INVALID_ADDR;
                addr1 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                pos += temp.value;
                if (pos < input.length())
                {
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
                            pos += temp.value;
                        }
                        else /* Read input as address 2*/
                        {
                            addr2 = addr1;
                            addr1 = INVALID_ADDR;
                        }
                    }    
                }
            }
            /* Special cases for ',' and ';' */
            else if (input.charAt(pos) == ',') /* First through specified or last line of the buffer */
            {
                addr1 = 1;
                ++pos;
                if (pos < input.length())
                {
                    if (Character.isDigit(input.charAt(pos))) /* address 2 is numerically defined */
                    {
                        addr2 = atoi(input.substring(pos));
                        while (Character.isDigit(input.charAt(pos)))
                            ++pos;
                    }
                    else if (isSpecial(input.charAt(pos))) /* address 2 is defined by a special character */
                    {
                        addr2 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                        pos += temp.value;
                    }
                    else
                        addr2 = buffSize;
                }
                else
                    addr2 = buffSize;
            }
            else if (input.charAt(pos) == ';') /* Current through specified or last line of the buffer */
            {
                addr1 = currentAddress;
                ++pos;
                if (pos < input.length())
                {
                    if (Character.isDigit(input.charAt(pos))) /* address 2 is numerically defined */
                    {
                        addr2 = atoi(input.substring(pos));
                        while (Character.isDigit(input.charAt(pos)))
                            ++pos;
                    }
                    else if (isSpecial(input.charAt(pos))) /* address 2 is defined by a special character */
                    {
                        addr2 = interpretSpecial(currentAddress, buffSize, input.substring(pos), temp);
                        pos += temp.value;
                    }
                    else
                        addr2 = buffSize;
                }
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
                if (param.length() == 0)
                    addr3 = currentAddress;
                else if (isSpecial(param.charAt(0)))
                    addr3 = interpretSpecial(currentAddress, buffSize, param, temp);
                else if (Character.isDigit(param.charAt(0)))
                    addr3 = atoi(param);
                else
                    addr3 = INVALID_ADDR;
                return;
            }
            else /* Invalid character encountered */
            {
                System.out.println("Warning: Unrecognized character inputted.");
                ++pos;
            }
        }
    }
}
