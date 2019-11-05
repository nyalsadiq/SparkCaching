# SparkCaching

### Project Goal:

* This project aims to simplify and improve caching in Spark for the Spark programmer.
* Currently the Spark programmer must manually cache RDD's, and Spark restricts the cache replacement
algorithm to LRU.
* We want to add active caching, where we will try to cache every RDD, and implement various cache replacement/selection algorithms.

### Design:
I have implemented LIFO, FIFO, and LFU algorithms using the following approach:

* I have Implement cache classes that can take RDDs and invoke their existing cache() functions. After invoking, the class then
stores the RDD object in a structure appropriate for the cache replacement algorithm to be used.

* The cache classes expose a cache(RDD) method, currently this has to be manually invoked and passed an RDD by the programmer.
I hope to make this invocation automatic and invisible to the programmer.

* The cache classes can store a set number of RDD's (eg 100 RDDs.). The idea is that we can set the limit
to something close to the actual machines max cache size, so that when our limit is reached we can invoke our own custom cache replacement algorithms, and not allow Spark's algorithm to be invoked.

* When our cache is full, whenever we attempt to cache an additional RDD our cache replacement policy is used.

### LFU Cache:
I have implemented an LFU cache by storing each cached RDD along with it's frequency of usage in the program in a MinHeap data structure.

Whenever an rdd is used in the program, we attempt to cache it with the following policy:
* If it doesn't exist in the cache and the cache isn't full, we cache it.
* If it does exist in the cache, we simply increase the frequency counter for that RDD.
* If it doesn't exist in the cache and the cache is full, we remove the RDD with the minimum frequency and insert our new RDD.  

### Problems:
* I need to investigate how caching works across multiple machines, this may change my implementation.