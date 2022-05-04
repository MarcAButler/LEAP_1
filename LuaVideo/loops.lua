a = 0
c = 10

while a < c do
    local b = a + 1
    a = a + 1
    print(a)
end

print("")
print("AFTER WHILE LOOP")
print("a: ")
print(a)
print("b: ")
print(b)
print("")

print("REPEAT-UNTIL LOOP")

-- One linguistically interesting piece that is unique to Lua is the repeat-until loop
-- The repeat until loop is essentially a method that begins a block regardless of whether the
-- until condition is met. After the first iteration however, the condition is checked and will
-- only break the loop until the condition is true.

-- Now you may think that this is just another do-while loop like any other language. But
-- this is not necessarily correct.

-- The repeat until is essentially an inverted do-while loop
-- I literally had to do a not ! on a do while loop for the interpreter
-- The while loop is different because it occurs while a condition is true
a = 0
repeat
    print(a)
    a = a + 1
until a > 10


