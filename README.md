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

## Dataflow Programming
Referential transparency, which allows us to change the in which functions are called without affecting the behaviour of the program facilitates dataflow programming in which code executes when the data it depends on becomes available.

## Structure Sharing
All of Clojure’s collections are persistent. Persistence in this case doesn’t have anything to do with persistence on disk or within a database. Instead it refers to a data structure that always preserves its previous version when it’s modified, which allows code to have a consistent view of the data in the face of modifications. Persistent data structures behave as though a complete copy is made each time they’re modified. Their implementation is done using structure sharing, to avoid the penalties of actually copying data. For example, when two lists have a common tail, the tails are shared. Lists handle only common tails well — if we want to have two lists with different tails, we have no choice
but to copy.

## ClojureScript
Browser-based JavaScript engines are single threaded, so what relevance can core.async possibly have? Don’t you need multiple threads for concurrent programming to be useful?

The go macro’s inversion of control magic means that ClojureScript can bring the appearance of multiple threads to client-side programming even in the absence of true multithreading. This is a form of cooperative multitasking—one task won’t preemptively interrupt another.

## CSP vs Actors
Most of the differences between actors and CSP result from the differing focus of the communities that have developed around them. The actor community has concentrated on fault tolerance and distribution, and the CSP community on efficiency and expressiveness. Choosing between them, therefore, is largely a question of deciding which of these aspects is most important to you.


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

### Shared Mutable State
An **atom** allows you to make synchronous changes to a *single* value — synchronous because when swap! returns, the update has taken place. Updates to one atom are *not coordinated* with other updates.

An **agent** allows you to make *asynchronous* changes to a *single* value — asynchronous because the update takes place after send returns. Updates to one agent are *not coordinated* with other updates.

**Refs** allow you to make *synchronous*, *coordinated* changes to *multiple* values.

### Atoms or STM?
Whenever we need to coordinate modifications of multiple values we can either use multiple refs and coordinate access to them with transactions or collect those values together into a compound data structure stored in a single atom. Both approaches work so pick the simpliest and most efficient approach for the problem you are solving.

### loop/recur
Unlike many functional languages, Clojure does not provide tail-call elimination, so idiomatic Clojure makes very little use of recursion. Instead, Clojure provides loop/recur.

The loop macro defines a target that recur can jump to (reminiscent of setjmp() and longjmp() in C/C++).

### Go Blocks
Threads have both an overhead and a startup cost, which is why most modern programs avoid creating threads directly and use a thread pool instead.

Thread pools are a great way to handle CPU-intensive tasks—those that tie a thread up for a brief period and then return it to the pool to be reused. But what if we want to do something that involves communication? Blocking a thread ties it up indefinitely, eliminating much of the value of using a thread pool.

There are ways around this, but they typically involve restructuring code to make it event-driven, a style of programming that will be familiar to anyone who’s done UI programming or worked with any of the recent breed of evented servers.

Although this works, it breaks up the natural flow of control and can make code difficult to read and reason about. Worse, it can lead to an excess of global state, with event handlers saving data for use by later handlers. And as we’ve seen, state and concurrency really don’t mix.

Go blocks provide an alternative that gives us the best of both worlds—the efficiency of event-driven code without having to compromise its structure or readability. They achieve this by transparently rewriting sequential code into event-driven code under the hood.

Code within a go block is transformed into a state machine. Instead of blocking when it reads from or writes to a channel, the state machine parks, relinquishing control of the thread it’s executing on. When it’s next able to run, it performs a state transition and continues execution, potentially on another thread.

This represents an inversion of control, allowing the core.async runtime to effi- ciently multiplex many go blocks over a limited thread pool. Just how efficiently we’ll soon see, but first let’s see an example.

## Elixir

### The error-kernel pattern

A software system’s error kernel is the part that must be correct if the system is to function correctly. Well-written programs make this error kernel as small and as simple as possible—so small and simple that there are obviously no deficiencies.

An actor program’s error kernel is its top-level supervisors. These supervise their children—starting, stopping, and restarting them as necessary.

This leads to a hierarchy of error kernels in which risky operations are pushed down toward the lower-level actors.

### Defensive Programming vs let-it-crash

Defensive programming is an approach to achieving fault tolerance by trying to anticipate possible bugs. Imagine, for example, that we’re writing a method that takes a string and returns true if it’s all uppercase and false otherwise. Here’s one possible implementation:

```
def all_upper?(s) do
  String.upcase(s) == s
end
```

This is a perfectly reasonable method, but if for some reason we pass nil to it, it will crash. With that in mind, some developers would change it to read like this:

```
def all_upper?(s) do
  cond do
    nil?(s) -> false
    true -> String.upcase(s) == s
  end
end
```

So now the code won’t crash if it’s given nil, but what if we pass something else that doesn’t make sense (a keyword, for example)? And in any case, what does it mean to call this function with nil? There’s an excellent chance that any code that does so contains a bug—a bug that we’ve now masked, meaning that we’re likely to remain unaware of it until it bites us at some time in the future.

Actor programs tend to avoid defensive programming and subscribe to the 'let it crash' philosophy, allowing an actor’s supervisor to address the problem instead.

### Linking processes
Elixir provides fault detection by allowing processes to be linked, which can be used to create supervisors:

* Links are bidirectional—if process *a* is linked to process *b*, then *b* is also linked to *a*
* Links propagate errors—if two processes are linked and one of them ter- minates abnormally, so will the other.
* If a process is marked as a system process, instead of exiting when a linked process terminates abnormally, it’s notified with an :EXIT message.

## OpenCL
OpenCL (Open Computing Language) is a framework for writing programs that execute across heterogeneous platforms consisting of central processing units (CPUs), graphics processing units (GPUs), digital signal processors (DSPs), field-programmable gate arrays (FPGAs) and other processors or hardware accelerators. OpenCL specifies programming languages (based on C99 and C++11) for programming these devices and application programming interfaces (APIs) to control the platform and execute programs on the compute devices. OpenCL provides a standard interface for parallel computing using task- and data-based parallelism.

### Compute kernel
In computing, a compute kernel is a routine compiled for high throughput accelerators (such as GPUs, DSPs or FPGAs), separate from (but used by) a main program. They are sometimes called compute shaders, sharing execution units with vertex shaders and pixel shaders on GPUs, but are not limited to execution on one class of device, or graphics APIs.

## C
### Basic Notes on Memory Management
```
int main() {
    int i;    // i is an int
    int *p;   // this is a * in a type-name. It means p is a pointer-to-int
    p = &i;   // use & operator to get a pointer to i, assign that to p.
    *p = 3;   // use * operator to "dereference" p, meaning 3 is assigned to i.
}
```





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

## Leiningen
* lein new project-name
* lein compile
* lein repl
* lein run (make sure project.clj has the main attribute that point to the file that needs to run)
* lein cljsbuild once (building a clojurescript project by transpiling clojurescript to javascript). Use *lein run* to run the server after the building is completed.

## Make
* Navigate to the directory where the Makefile is located
* Run command: *make*
* To run: *./program_name*

# Installing
## Clojure
* brew install clojure
* brew install leiningen


# Book Source Code
https://github.com/islomar/seven-concurrency-models-in-seven-weeks

# Upto
Page 234
