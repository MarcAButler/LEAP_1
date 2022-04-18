-- [DATA AND TYPES]
-- Useless comment

myVar = "It is cool"

-- Another way to print
-- # gets the length
io.write("Size of string ", #myVar, "\n")

anotherVar = 32
yetAnotherVar = 12

-- Get type
print(type(myVar))

-- Long String Demo
longString = [[I can go between
multiple
             lines ]]

print(longString)

-- Append / concat myVar to longString with ..
print(longString .. myVar)

-- Everything in Lua by default is nil if it does not get assigned a value
-- or is never declared
io.write(type(varThatDoesNotExist) .. "\n")

-- Error
-- print("myLocalVar: " .. myLocalVar)

-- [ARITHMETIC]
print(2^4)

-- [Control Flow and Boolean Operators]
age = 14

if age > 13 then
    print("You are older than 13!")
    local myLocalVar = "This will be nil outside of this block"
    print("myLocalVar: " .. myLocalVar)
elseif age < 13 then
    print("You are younger than 13!")
else
    print("You must be 13")
end

time = -2

if time > 3 or time < 3 then
    print("The time is not 3")
else
    print("The time must be 3")
end

myBool = false

print(not myBool)


i = 0
while i <= 10 do
    print(i)
    i = i + 1

    if i == 8 then break end
end
-- No continue statement in lua

-- Essentially a do while
repeat
    io.write("Testing")
    i = i + 1
until i >= 18

for i = 0, 10, 1 do
    print(i)
end

-- Note that this is not zero indexed
fruits = {"apple", "banana", "coconuts", "durian", "eggplant"}

print("SOME FRUIT FROM TABLE: " .. fruits[3])

-- in keyword
-- pairs() docs: https://www.lua.org/pil/7.3.html
for i, elem in pairs(fruits) do
    print(elem)
end

-- [TABLES]
myTable = {}

-- Put values in a table
for i = 0, 10 do
    myTable[i] = i
end

io.write("My Table: " .. myTable[2])

-- Insert into a table at an index
table.insert(myTable, 5, 9999)

io.write("My Table: " .. myTable[5])

table.remove(myTable, 5)

-- Convert a table to a string
print(table.concat(myTable))
print(table.concat(myTable, ", "))

-- You can have multiple dimensional tables and can populate via for loops

print "hi there" print "test"
print "another hi there"print "another test"
-- [NOTE] does not need parentheses
-- [NOTE] statements can be placed on one line
