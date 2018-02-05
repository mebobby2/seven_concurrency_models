# Seven Concurrency Models

# Notes
## Concurrency
A concurrent program has multiple logical threads of control. These threads may or may not run in parallel. An alternative way of thinking about this is that concurrency is an aspect of the problem domain—your program needs to handle multiple simultaneous (or near-simultaneous) events. Concurrent programs are often nondeterministic —they will give different results depending on the precise timing of events.

## Parallelism
A parallel program potentially runs more quickly than a sequential program by executing different parts of the computation simultaneously (in parallel). It may or may not have more than one logical thread of control. Parallelism is an aspect of the solution domain—you want to make your program faster by processing differ- ent portions of the problem in parallel. Parallelism doesn’t necessarily imply nonde- terminism—doubling every number in an array doesn’t (or at least, shouldn’t) become nondeterministic just because you double half the numbers on one core and half on another.

## Levels of Parallelism
### Bit-Level Parallelism
Why is a 32-bit computer faster than an 8-bit one? Parallelism. If an 8-bit computer wants to add two 32-bit numbers, it has to do it as a sequence of 8-bit operations. By contrast, a 32-bit computer can do it in one step, handling each of the 4 bytes within the 32-bit numbers in parallel.

### Instruction-Level Parallelism
Modern CPUs are highly parallel, using techniques like pipelining, out-of-order execution, and speculative execution.

### Data Parallelism
Data-parallel (sometimes called SIMD, for “single instruction, multiple data”) architectures are capable of performing the same operations on a large quantity of data in parallel.

One of the applications that’s most amenable to data parallelism is image processing. To increase the brightness of an image, for example, we increase the brightness of each pixel. For this reason, modern GPUs (graphics process- ing units) have evolved into extremely powerful data-parallel processors.

### Task-Level Parallelism
Multiple processors. From a programmer's point of view, the most important distinguishing feature of a multiprocessor architecture is the memory model, specifically whether it’s shared or distributed.

# Building
## Java
* Build: *javac file.java*. This will generate a *.class* file.
* Run: *java file* from the directory where the *.class* file is located

# Upto
Page 15

Chapter 1
