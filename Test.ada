with Ada.Containers.Doubly_Linked_Lists;
with Ada.Text_Io; use Ada.Text_Io;
with Ada.Strings.Unbounded; use Ada.Strings.Unbounded;
with Ada.Integer_Text_IO; use Ada.Integer_Text_IO;
with Ada.IO_Exceptions;

procedure main is
   type Command is (A, C, P, E, M);
   package Command_IO is new Ada.Text_IO.Enumeration_IO (Command);
   package String_List is new Ada.Containers.Doubly_Linked_Lists(Unbounded_String);
   use String_List;
   Userinput : String(1 .. 5) := (others => ' '); --This string length is 5
   InsertLocation : String(1 .. 5) := (others => ' '); --This string length is 5
   text : List; -- List is from Doubly_Linked_Lists
   F : File_Type;
   
begin   
   loop     
      declare
         Cmd : Command;
         
         
   procedure Print(Position : Cursor) is -- this subprogram print all string from list 
         begin     
            Put_Line(To_String(Element(Position)));      
            --Put_Line("K");
   end Print;
         
      begin
         Put_Line("*****************************");
         Put_Line("*Enter A to add a text      *");
         Put_Line("*Enter C to insert a text   *");
         Put_Line("*Enter P the print text list*");
         Put_Line("*Enter E the exit program   *");
         Put_Line("*Enter M to save the list   *");
         Put_Line("*****************************");
         Put_Line(" ");
         Put(">> ");
         
         Command_IO.Get (Cmd);
         
         Ada.Text_IO.Skip_Line;
         
         Put_Line ("read " & Cmd'Image);                -- ' to sort out the not-fully-Ada-aware syntax highlighting
         Put_Line ("  " );
         case Cmd is
         
         when a =>
            Put_Line("Enter a text " );
            Put(">> " );
            Get(Userinput);  
            text.Append(To_Unbounded_String(Userinput)); -- if letter is a add it to the doubly link list  
            Put_line("  " );
        
         when c => 
            Put_Line("Enter a text location you want to insert " );
            Put(">> " );
            Get(Userinput);
            Put_Line("Enter a text " );
            Put(">> " );
            Get(InsertLocation);
            
            
            text.Insert(Before => text.Find(To_Unbounded_String(Userinput)), New_Item => To_Unbounded_String( InsertLocation ));

         when p =>      
            text.Iterate(Print'access);
            Put_line("  " );
         
            
         when m =>
            Put_Line("Save to file"); 
            Create (F, Out_File, "file.txt");
            Put_Line (F, "This string will be written to the file file.txt");
            Close (F);
            
         when e =>      
            Put_Line("Program Exit");
            
            exit;   
            
         end case;   
         
      exception
         when Ada.IO_Exceptions.Data_Error =>
            Put_Line ("unrecognised command");
            Put_Line (" ");
               
      end;
   end loop;
end main;
