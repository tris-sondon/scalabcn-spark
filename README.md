**Scala Developers Barcelona Lightning Talks** (18/06/14): *First Steps on Apache Spark* (demo)

This project includes different Spark programs following different setups.

* ```HelloWorld.scala```: it shows Spark transformations lazyness.
* ```ScalaOnSOLocal.scala```: it reads locally a previously parsed text file containing Scala-related questions in StackOverflow community and computes the tag lists occurences.
* ```ScalaOnSOCluster.scala```: simple program that parses the original XML file provided by StackOverflow community about its posts and counts them (executed within a cluster, standalone mode). See [this](http://blog.stackexchange.com/category/cc-wiki-dump/) for more details.

Check ```dev```/```prod``` environment variables within ```application.conf``` configuration file.

Feel free to play around!
