using System;
using System.Collections.Generic;
using System.Text;

namespace CS4080project.Parser
{
	class Parser
	{
		internal uint line1;
		internal uint line2;
		internal uint line3;
		internal uint savedIndex;
		internal uint numberOfLine;
		internal string para;
		internal char command;
		internal bool accept;

		public unsafe Parser(String input, uint texbufferIndex, uint numberOfLine, uint currentAddress)
		{
			this.numberOfLine = numberOfLine;
			this.savedIndex = currentAddress;
			decode(input, texbufferIndex);
		}

		/* Parse the user inputted line */
		public unsafe void decode(String input, uint texbufferIndex)
		{

			int index = 0;
			
			//uint temp2 = 0;
			String number;
			String empty = string.Empty;
				
			int temp = 0; // new location	
			int* tempPointer = &temp;
			

			while (index < input.Length)
			{
				if (char.IsDigit(input[index])) /* Argument is a number */ //The string space is removed
				{
					line2 = 0;
					line1 = (uint)Char.GetNumericValue(input[index]);

					while (Char.IsDigit(input[index]))
					{
						if ((input.Length - 1) == index)
						{
							return;
						}
						empty += input[index];
						++index;
						//Console.Write("Input length is " + input.Length +" index is " + index);
					}


					if (empty.Length > 0)
					{
						line1 = uint.Parse(empty);
					}

					if (input[index] == ',' || input[index] == ';') /* A second address is given */
					{
						++index;

						if ((input.Length - 1) <= index)
						{
							return;
						}

						if (Char.IsDigit(input[index]))
						{
							empty = string.Empty;

							while (Char.IsDigit(input[index]))
							{
								if ((input.Length - 1) == index)
								{
									return;
								}
								empty += input[index];
								++index;
							}

							if (empty.Length > 0)
							{
								line2 = uint.Parse(empty);
							}
						}
						else if (isSpecial(input[index])) /* Special character for addr2 */
						{
							line2 = (uint) interpretSpecial(input.Substring(index, (input.Length - index) ), &temp);
							index += temp;
						}

						else /* Read input as addr2 ??? */
						{
							line2 = line1;
							line1 = '\0';
						}
					}
				}
				else if (isSpecial(input[index])) /* Special character for addr1 */
				{
					line2 = 0;
					line1 = (uint)interpretSpecial(input.Substring(index, (input.Length - index)), &temp);
					index += temp;

					if (input[index] == ',' || input[index] == ';') /* A second address is given */
					{
						++index;
						if (Char.IsDigit(input[index]))
						{
							empty = string.Empty;

							while (Char.IsDigit(input[index]))
							{
								empty += input[index];
								++index;
							}

							if (empty.Length > 0)
							{
								line2 = uint.Parse(empty);
							}
						}
						else if (isSpecial(input[index])) /* Special character for addr2 */
						{
							line2 = (uint)interpretSpecial(input.Substring(index, (input.Length - index)), &temp);
							index += temp;
						}
						else /* Read input as addr2 */
						{
							line2 = line1;
							line1 = '\0';
						}
					}
				}

				else if (input[index] == ',') /* First through specified or last 5of the buffer */
				{
					line1 = 1;
					++index;

					if (Char.IsDigit(input[index])) /* addr2 is numerically defined */
					{
						empty = string.Empty;

						while (Char.IsDigit(input[index]))
						{
							if ((input.Length - 1) == index)
							{
								return;							
							}

							empty += input[index];
							++index;
						}

						if (empty.Length > 0)
						{
							line2 = uint.Parse(empty);
						}
					}
					else if (isSpecial(input[index])) /* addr2 is defined by a special character */
					{
						line2 = (uint)interpretSpecial(input.Substring(index, (input.Length - index)), &temp);
						index += temp;
					}

					else
						line2 = texbufferIndex;

				}
				else if (input[index] == ';') /* Current through specified or last line of the buffer */
				{
					line1 = texbufferIndex;
					++index;
					if (Char.IsDigit(input[index]))
					{
						empty = string.Empty;

						while (Char.IsDigit(input[index]))
						{
							if ((input.Length - 1) == index)
							{
								return;
							}

							empty += input[index];
							++index;
						}

						if (empty.Length > 0)
						{
							line2 = uint.Parse(empty);
						}
					}
					else if (isSpecial(input[index]))
					{
						line2 = (uint)interpretSpecial(input.Substring(index, (input.Length - index)), &temp);
						index += temp;
					}

					else
						line2 = texbufferIndex;
				}
				else if (isCommand(input[index])) /* Current token is a command */
				{
					command = input[index];
					++index;
					accept = true;

					int startIndex = index;

					if (index < input.Length)
					{
						para = input.Substring(startIndex);

						empty = string.Empty;

						for (int i = 0; i < para.Length; i++)
						{
							if (Char.IsDigit(para[i]))
								empty += para[i];
						}

						if (empty.Length > 0)
						{
							line3 = uint.Parse(empty);
						}
					}


					return;
				}
				else /* Invalid character encountered */
				{
					Console.WriteLine("Warning: Unrecognized character inputted.");
					accept = false;
					++index;
					break;
				}
			}
		}

		/* Return true if the character has a special meaning when passed as an argument */
		public bool isSpecial(char c)
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

		/* Return true if the character is a valid command */
		public bool isCommand(char c)
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

		/* Interpret the value of the special substring relative to the buffer.
        Does not include the special characters ',' and ';'. Returns how many
        characters were read as offset. */
		public unsafe uint interpretSpecial(string s, int* offset)
		{
			uint retVal;
			String empty = string.Empty;


			switch (s[0])
			{
				case '.':
					if (offset != null)    // if offset is not null than offset is 1
						*offset = 1;
					return savedIndex;
				case '$':
					if (offset != null)
					{
						*offset = 1; 
					}

					return numberOfLine;
				case '+':				
					if (Char.IsDigit(s[1])) //if next character is number 
					{

						empty += s[1];
						retVal = savedIndex - uint.Parse(empty);

						if (offset != null)
						{
							*offset = 2;
						}

						while (Char.IsDigit(s[*offset]))
							++offset;
						return retVal;
					}
					else
					{
						if (offset != null) // if offset is not null than offset is 1
							*offset = 1;
						return savedIndex + 1;
					}
				case '-':
					if (Char.IsDigit(s[1])) //if next character is number
					{
						empty = string.Empty;						
						empty += s[1];

						retVal = savedIndex - uint.Parse(empty);

						if (offset != null)
						{
							*offset = 2;
							while (Char.IsDigit(s[*offset]))
							++offset;
						}
						return retVal;
					}
					else
					{
						if (offset != null) // if offset is not null than offset is 1
							*offset = 1;
						return savedIndex - 1;
					}
				default:
					if (offset != null) // if offset is not null than offset is 0
						*offset = 0;
					return 0; ;
			}
		}
	}
}
