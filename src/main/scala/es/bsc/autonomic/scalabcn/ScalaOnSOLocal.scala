package es.bsc.autonomic.scalabcn

import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.SparkContext._

/**
 * Jordi Aranda
 * 18/06/14
 * <jordi.aranda@bsc.es>
 */
object ScalaOnSOLocal {

  def main(args: Array[String]){

    import SOUtils._

    //Load Spark config file
    lazy val conf = ConfigFactory.load

    //Set Spark config object
    val sparkConf = new SparkConf()
      .setMaster(conf.getString("spark.dev.config.master"))
      .setAppName(conf.getString("spark.dev.config.appName"))
      .set("spark.executor.memory", conf.getString("spark.dev.config.executorMemory"))  // Amount of memory to use per executor process
      .set("spark.cores.max", conf.getString("spark.dev.config.coresMax"))              // Maximum amount of CPU cores to request for the app across the cluster (not from each machine)
      .set("spark.akka.frameSize", conf.getString("spark.dev.config.akkaFrameSize"))    // Maximum message size (serialized tasks / tasks results)

    //Simple text file with "stringified" Post case class
    val postsFile = conf.getString("spark.dev.data.posts")

    //Spark context initialization
    val sc = new SparkContext(sparkConf)

    //Count Scala-related tags in descending order
    val scalaPosts = sc.textFile(postsFile)
      .map(line => PostString.unapply(line).getOrElse(DUMMY_POST))
      .filter(post => post.id != DUMMY_POST.id)
      .map(post => (post.tags, 1))
      .reduceByKey(_ + _)
      .map{case (k,v) => (v, k)}
      .sortByKey(false)

    scalaPosts.foreach(println)

  }

}
