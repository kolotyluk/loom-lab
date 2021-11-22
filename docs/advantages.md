# Project Loom Advantages

## Thread Pools with Blocking Tasks

While it is possible to use Tread Pool Executors with Tasks that block on I/O calls and other and
other APIs, it can be problematic.

Virtual Threads are extremely well suited to solve this problem, in fact, it is one of the main design goals.
