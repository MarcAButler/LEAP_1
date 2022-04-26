a = 0
c = 10

while a < c do
    --print "before increment: "
    local b = a + 1
    -- print("b:")
    -- print(b)
    -- print("a:")
    -- print(a)
    a = a + 1
    --print "after increment: " 
    --print a
end

print("AFTER LOOP")
print(b)

-- [?] Why is our interpreter printing out the local b whereas the lua interpreter prints out nil?