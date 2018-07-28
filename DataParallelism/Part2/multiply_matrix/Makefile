ifeq ($(shell uname -s),Darwin)
	LIBS=-framework OpenCL
else
	LIBS=-lOpenCL
endif

target/multiply_matrix: multiply_matrix.c
	mkdir -p target
	gcc -std=c99 multiply_matrix.c $(LIBS) -o target/multiply_matrix
