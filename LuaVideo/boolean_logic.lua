-- Lets start off with two variables that we will call true and false
a = true
b = false

-- variables of type boolean can be evaluated in conditional logic
if a then
    print("a is true")
end

-- Note that we can also do comparison
-- Note that Lua does not use the bang symbol ! and it instead ops for tilde ~
if a ~= b then
    print("a is equivalent to b")
end

-- We can do or
if a or b then
    print("either a or b is true")
end

-- And we can do, well, and
if a and b then
    print("this should definately not show up")
end

