
-- We just have a simple example here but it proves the power of the language
-- and sparks linguistic interest
-- Here we just modify our global variable by adding by a local variable
-- Notice here that b, a local variable, is automatically defined as NIL
-- as it is not defined globally
a = 0
do
    local b = 2
    a = 1 + b
    
end
print(a)
print(b)