public class Parser {
    private int line1;
    private int line2;
    private int line3;
    private int currentAddress;
    private String parsed;
    private char command;
    private boolean accept;

    public Parser(String input, int currentAddress){
        this.currentAddress = currentAddress;
        decode(input, currentAddress);
    }

    public void decode(String input, int textBufferIndex){
        int index = 0;
        String s = "";
        Integer temp = 0;
        while(index<input.length()){
            if(Character.isDigit(input.charAt(index))){
                line2 = 0;
                line1 = (int)Character.getNumericValue(input.charAt(index));
                while(Character.isDigit(input.charAt(index))) {
                    s += input.charAt(index);
                    ++index;
                    if (input.length() == index) {
                        line1 = Integer.parseInt(s);
                        return;
                    }
                }
                if(s.length()>0)
                    line1 = Integer.parseInt(s);
                if(input.charAt(index)==',' || input.charAt(index)==';'){
                    ++index;
                    if(input.length()-1<=index)
                        return;
                    if(Character.isDigit(input.charAt(index))){
                        s = "";
                        while(Character.isDigit(input.charAt(index))){
                            if(input.length()-1==index)
                                return;
                            s+=input.charAt(index);
                            ++index;
                        }
                        if(s.length()>0)
                            line2 = Integer.parseInt(s);
                    }
                    else if(isSpecial(input.charAt(index))){
                        line2 = (int) interpretSpecial(input.substring(index,(input.length()-index)), temp);
                        index += temp;
                    }
                    else{
                        line2 = line1;
                        line1 = '\0';
                    }
                }
            }
            else if(isSpecial(input.charAt(index))){
                line2 = 0;
                line1 = (int)interpretSpecial(input.substring(index,(input.length()-index)),temp);
                index+=temp;
                if(input.length()==index)
                    return;
                if(input.charAt(index)==',' || input.charAt(index)==';'){
                    ++index;
                    if(Character.isDigit(input.charAt(index))){
                        s = "";
                        while(Character.isDigit(input.charAt(index))){
                            s+=input.charAt(index);
                            ++index;
                        }
                        if(s.length()>0)
                            line2 = Integer.parseInt(s);
                    }
                    else if(isSpecial(input.charAt(index))){
                        line2 = (int) interpretSpecial(input.substring(index,(input.length()-index)), temp);
                        index += temp;
                    }
                    else{
                        line2 = line1;
                        line1 = '\0';
                    }
                }
            }
            else if(input.charAt(index)==','){
                line1 = 1;
                ++index;
                if(input.length()==index)
                    return;
                if(Character.isDigit(input.charAt(index))){
                    s = "";
                    while(Character.isDigit(input.charAt(index))){
                        if(input.length()-1==index)
                            return;
                        s+=input.charAt(index);
                        ++index;
                    }
                    if(s.length()>0)
                        line2 = Integer.parseInt(s);
                }
                else if(isSpecial(input.charAt(index))){
                    line2 = (int) interpretSpecial(input.substring(index,(input.length()-index)), temp);
                    index += temp;
                }
                else
                    line2 = textBufferIndex;
            }
            else if(input.charAt(index)==';'){
                line1 = textBufferIndex;
                ++index;
                if(input.length()==index)
                    return;
                if(Character.isDigit(input.charAt(index))){
                    while(Character.isDigit(input.charAt(index))){
                        if(input.length()-1==index)
                            return;
                        s+=input.charAt(index);
                        ++index;
                    }
                    if(s.length()>0)
                        line2 = Integer.parseInt(s);
                }
                else if(isSpecial(input.charAt(index))){
                    line2 = (int) interpretSpecial(input.substring(index,(input.length()-index)), temp);
                    index += temp;
                }
                else
                    line2 = textBufferIndex;
            }
            else if(isCommand(input.charAt(index))){
                command = input.charAt(index);
                ++index;
                accept = true;
                int startIndex = index;
                if(index<input.length()){
                    parsed = input.substring(startIndex);
                    s = "";
                    for(int i=0; i<parsed.length();i++){
                        if(Character.isDigit(parsed.charAt(i)))
                            s+=parsed.charAt(i);

                    }
                    if(s.length()>0)
                        line3 = Integer.parseInt(s);
                }
                return;
            }
            else{
                System.out.println("Cannot recognize character.");
                accept = false;
                ++index;
            }
        }
    }

    public boolean isSpecial(char c){
        switch (c){
            case '.': case '$': case '+': case '-':
                return true;
            default:
                return false;
        }
    }

    public boolean isCommand(char c){
        switch (c){
            case 'a': case 'c': case 'd': case 'j':
            case 'l': case 'm': case 'n': case 'p':
            case 't': case 'w': case 'e': case 'q':
                return true;
            default:
                return false;
        }
    }

    public int interpretSpecial(String str, Integer offset){
        int value = 0;
        String s = "";

        switch(str.charAt(0)){
            case '.':
                if(offset!=null)
                    offset = 1;
                return currentAddress;
            case '$':
                if(offset!=null)
                    offset = 1;
                return currentAddress;
            case '+':
                if(Character.isDigit(str.charAt(1))){
                    s+=str.charAt(1);
                    value = currentAddress + Integer.parseInt(s);
                    if(offset!=null)
                        offset = 2;
                    while(Character.isDigit(str.charAt(offset)))
                        ++offset;
                    return value;
                }
                else{
                    if(offset!=null)
                        offset = 1;
                    return currentAddress + 1;
                }
            case '-':
                if(Character.isDigit(str.charAt(1))){
                    s = "";
                    s+=str.charAt(1);
                    value = currentAddress - Integer.parseInt(s);
                    if(offset!=null) {
                        offset = 2;
                        if(str.length()<=offset)
                            return value;
                        while(Character.isDigit(str.charAt(offset)))
                            ++offset;
                    }
                    return value;
                }
                else{
                    if(offset!=null)
                        offset = 1;
                    return currentAddress - 1;
                }
            default:
                if(offset!=null)
                    offset = 0;
                return 0;
        }
    }

    public boolean isAccept() {
        return accept;
    }

    public int getLine1() {
        return line1;
    }

    public int getLine2() {
        return line2;
    }

    public int getLine3() {
        return line3;
    }

    public char getCommand() {
        return command;
    }

    public String getParsed(){
        return parsed;
    }
}