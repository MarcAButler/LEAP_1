-- a = 1

-- -- [?] Can we declare local variables outside of a block? -- Evidently this is allowed
-- local c = 4

-- if (true) then
--     a = 3

--     print(a)
-- end


-- a = 1
-- c = 10

-- while a < c do
--     print(a)
--     a = a + 1
--     print("after increment: ") 
--     print(a)
-- end


-- Legal
-- a = 0
-- do
--     b = 2
--     local a = 1 + b
    
-- end
-- print(a)
-- [OUTPUT]: 0

-- Legal
a = 0
do
    local b = 2
    a = 1 + b
    
end
print(a)
-- [OUTPUT]: 3