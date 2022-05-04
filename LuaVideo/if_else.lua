-- First let us declare a variable:
age = 19

-- Next let us check if the age is greater than 18:
if age > 18 then
    print("You are in the real world now")
else if age == 19 then
    print("age is 19")
else
   print("You have a long ways to go")
end

-- Note the "end" keyword at the end of the entire control sequence
-- This keyword tells the parser that a series of blocks has ended
-- This is synonymous with C-like languages that employ braces to denote blocks

-- Now you may wonder how do we know if a block has ended or not