package es.bsc.autonomic.scalabcn

import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.SparkContext._

/**
 * Jordi Aranda
 * 18/06/14
 * <jordi.aranda@bsc.es>
 */
object HelloWorld {

  def main(args: Array[String]){

    //Load Spark config file
    lazy val conf = ConfigFactory.load

    //Set Spark config object
    val sparkConf = new SparkConf()
      .setMaster(conf.getString("spark.dev.config.master"))
      .setAppName(conf.getString("spark.dev.config.appName"))
      .set("spark.executor.memory", conf.getString("spark.dev.config.executorMemory"))  // Amount of memory to use per executor process
      .set("spark.cores.max", conf.getString("spark.dev.config.coresMax"))              // Maximum amount of CPU cores to request for the app across the cluster (not from each machine)
      .set("spark.akka.frameSize", conf.getString("spark.dev.config.akkaFrameSize"))    // Maximum message size (serialized tasks / tasks results)

    //Spark context initialization
    val sc = new SparkContext(sparkConf)

    //Transformations are lazy! No computation until result in required from driver program
    val sum1 = sc.parallelize(1 to 10000).map(x => (x%2 -> x)).groupByKey.reduceByKey(_ ++ _).mapValues(numbers => numbers.sum)

    //Much more efficient
    val sum2 = sc.parallelize(1 to 10000).map(x => (x%2 -> x)).reduceByKey(_ + _)

    //sum1.collect.foreach(println)
    //sum2.collect.foreach(println)

  }

}