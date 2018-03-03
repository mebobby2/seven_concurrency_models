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

## Three perils of threads and locks
* Race conditions
* Deadlocks
* Memory visibility

### Rules to help avoid them
* Synchronize all access to shared variables.
* Both the writing and the reading threads need to use synchronization.
* Acquire multiple locks in a fixed, global order.
* Don’t call alien methods while holding a lock.
* Hold locks for the shortest possible amount of time.

## Livelock
Although the tryLock() solution avoids infinite deadlock, that doesn’t mean it’s a good solution. Firstly, it doesn’t avoid deadlock—it simply provides a way to recover when it happens. Secondly, it’s susceptible to a phenomenon known as livelock—if all the threads time out at the same time, it’s quite possible for them to immediately deadlock again. Although the deadlock doesn’t last forever, no progress is made either.

This situation can be mitigated by having each thread use a different timeout value, for example, to minimize the chances that they will all time out simultaneously. But the bottom line is that timeouts are rarely a good solution—it’s far better to avoid deadlock in the first place.

## How Large Should My Thread Pool Be?
The optimum number of threads will vary according to the hardware you’re running on, whether your threads are IO or CPU bound, what else the machine is doing at the same time, and a host of other factors.

Having said that, a good rule of thumb is that for computation-intensive tasks, you probably want to have approximately the same number of threads as available cores. Larger numbers are appropriate for IO-intensive tasks.

Beyond this rule of thumb, your best bet is to create a realistic load test and break out the stopwatch.

## Why a Blocking Queue?
As well as blocking queues, java.util.concurrent provides ConcurrentLinkedQueue, an unbounded, wait-free, and nonblocking queue. That sounds like a very desirable set of attributes, so why isn’t it a good choice for this problem?

The issue is that the producer and consumer may not (almost certainly will not) run at the same speed. In particular, if the producer runs faster than the consumer, the queue will get larger and larger. Given that the Wikipedia dump we’re parsing is around 40 GiB, that could easily result in the queue becoming too large to fit in memory.

Using a blocking queue, by contrast, will allow the producer to get ahead of the con- sumer, but not too far.

## Performance Curve
You’ll see this curve again and again when working with parallel programs. The performance initially increases linearly and is then followed by a period where performance continues to increase, but more slowly. Eventually performance will peak, and adding more threads will only make things slower.

## Strengths and Weaknesses of Threads and Locks
The primary strength of threads and locks is the model’s broad applicability. They are 'close to the metal' — little more than a formal- ization of what the underlying hardware does anyway—they can be very efficient when used correctly.

Outside of a few experimental distributed shared-memory research systems, threads and locks support only shared-memory architectures. If you need to support distributed memory (and, by extension, either geographical distribu- tion or resilience), you will need to look elsewhere. This also means that threads and locks cannot be used to solve problems that are too large to fit on a single system.

The greatest weakness of the approach, however, is that threads-and-locks programming is hard.

The rules about using synchronization to access shared variables; acquiring locks in a fixed, global order; and avoiding alien method calls while holding a lock are applicable to any language with threads and locks.

## Functional Programming
In contrast to an imperative program, which consists of a series of statements that change global state when executed, a functional program models compu- tation as the evaluation of expressions. Those expressions are built from pure mathematical functions that are both first-class (can be manipulated like any other value) and side effect–free. It’s particularly useful when dealing with concurrency because the lack of side effects makes reasoning about thread safety much easier.

Data that doesn’t change (is immutable) can be accessed by multiple threads without any kind of locking. This is what makes functional programming so compelling when it comes to concurrency and parallelism—functional programs have no mutable state, so they cannot suffer from any of the problems associated with shared mutable state.

## The Perils of Mutable State
* Hidden mutable state
* Escapologist (escaped) mutable state

## Evaluation Order
In an imperative language like Java, the order in which things happen is tightly bound to the order in which statements appear in the source code. The compiler and runtime can move things around somewhat, but broadly speaking things happen in the same order as we write them down.

Functional languages have a much more declarative feel. Instead of writing a set of instructions for how to perform an operation, a functional program is more a statement of what the result should be. How the various calculations are ordered to achieve that result is much more fluid—this freedom to reorder calculations is what allows functional code to be parallelized so easily.

## Referential Transparency
Pure functions are referentially transparent — anywhere an invocation of the function appears, we can replace it with its result without changing the behavior of the program.

Indeed, one way to think about what executing functional code means is to think of it as repeatedly replacing function invocations with their results until you reach the final result.

And because every function is referentially transparent, it enables us to safely make the radical changes to evaluation order.




# Language Notes
## Java
### Anonymous Inner Class in Java
Anonymous Inner class that extends a class. E.g. You can create a thread by extending the Thread class. You can use an anonymous inner class to declare and instantiate it in one go.
```
Thread t = new Thread()
{
    public void run()
    {
        System.out.println("Child Thread");
    }
};
```

You can also create an anonymous Inner class that implements a interface. E.g. we also know that by implementing Runnable interface we can create a Thread:
```
Thread t = new Thread(new Runnable()
{
    public void run()
    {
        System.out.println("Child Thread");
    }
});
```

### ++Var and Var++
Although both var++ and ++var increment the variable they are applied to, the result returned by  var++ is the value of the variable before incrementing, whereas the result returned by ++var is the value of the variable after the increment is applied.

### Just-in-time Optimizer
As is often the case with code running on the JVM, we have to run more than once to give the just-in-time optimizer a chance to kick in and get a representative time

## Clojure
* *lein repl* - to open the REPL
* *(load-file "FunctionalProgramming/sum.clj")* - to load a clojure file from inside the REPL. The path is relative from the directory where the REPL is started
* *lein new project_name* - generate a new clojure project
* *lein run* - run the project. Will automatically pull down the dependencies inside project.clj and run the main file specified in project.clj as well

### Creating Standalone Java Applications with Leiningen
1. Specify the *aot* and *main* attributes in project.clj
2. Ensure *:gen-class* in the namespace of the program and define a *-main* function
3. lein compile
4. lein uberjar
5. java -jar target/*file.jar

### Impure
Clojure is an impure functional language — it is possible to write functions with side effects in Clojure, and any such functions will not be referentially transparent.

This turns out to make little difference in practice because side effects are both very rare in idiomatic Clojure code and obvious when they do exist. There are a few simple rules about where side effects can safely appear, and as long as you follow those rules you’re unlikely to hit problems with evaluation order.

# Building
## Java
* Compile: *javac file.java*. This will generate a *.class* file.
* Run: *java file* from the directory where the *.class* file is located
* Compile with classpath: *javac -classpath /path/to/other/classes file.java*. Once compiled with the classpath, you can use the run command above as usual, i.e. without the classpath parameter. Classpath parameter is only needed when compiling. If using the current directory as the classpath, can use this shortcut *javac -classpath . file.java*

## Maven
* Must have the src/main/java/com directory set up
* Copy the pom.xml
* Package: *mvn package*
* Run: *java -cp file.jar ClassToRun* E.g. java -cp target/my-app-1.0-SNAPSHOT.jar WordCount

# Installing
## Clojure
* brew install clojure
* brew install leiningen


# Book Source Code
https://github.com/islomar/seven-concurrency-models-in-seven-weeks

# Upto
Page 94

Day 3 Wrap-Up

Before that: Debug why the transcript handler is not working
