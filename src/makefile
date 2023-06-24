INC= -I ../
DEF=
LIB=

gcc: ../formatter.java
	g++ -x c++ ../formatter.java -o ../antiformatter.exe -Wall -Wextra -Werror $(INC) $(DEF) $(LIB) -O2

clang: ../formatter.java 
	clang++ -x c++ ../formatter.java -o ../antiformatter.exe -Wall -Wextra -Werror $(INC) $(DEF) $(LIB) -O2

vcc: 
	@echo Cannot be used, use g++ or clang++ instead

run: ../antiformatter.exe 
	../antiformatter ../test.cpp
	../antiformatter ../test2.cpp

stdgcc: ../stdformatter.java
	g++ -x c++ ../stdformatter.java -o ../antiformatter.exe -Wall -Wextra -Werror $(INC) $(DEF) $(LIB) -O2

clear: ../test.cpp.out ../test2.cpp.out 
	rm -f ../test.cpp.out ../test2.cpp.out 
