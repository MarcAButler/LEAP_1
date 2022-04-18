# Because Lua and Python consider a expressions and/or statements to be
# distinguished by newlines. We test Python exception of a newline in
# this file

# print("Hello 
#     world")
# [NOTE]
# This prints out "SyntaxError: EOL while scanning string literal"

# print("Hello world" +
#     "hi there")
# [NOTE]
# This prints out "Hello worldhi there"