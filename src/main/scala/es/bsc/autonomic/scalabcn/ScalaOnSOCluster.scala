package es.bsc.autonomic.scalabcn

import com.typesafe.config.ConfigFactory
import org.apache.spark.{SparkContext, SparkConf}

import scala.xml.XML

/**
 * Jordi Aranda
 * 18/06/14
 * <jordi.aranda@bsc.es>
 */
object ScalaOnSOCluster {

  def main(args: Array[String]){

    import SOUtils._

    //Load Spark config file
    lazy val conf = ConfigFactory.load

    //Set Spark config object
    val sparkConf = new SparkConf()
      .setMaster(conf.getString("spark.prod.config.master"))
      .setAppName(conf.getString("spark.prod.config.appName"))
      .set("spark.executor.memory", conf.getString("spark.prod.config.executorMemory"))   // Amount of memory to use per executor process
      .set("spark.cores.max", conf.getString("spark.prod.config.coresMax"))               // Maximum amount of CPU cores to request for the app across the cluster (not from each machine)
      .set("spark.akka.frameSize", conf.getString("spark.prod.config.akkaFrameSize"))     // Maximum message size (serialized tasks / tasks results)
      .set("spark.local.dir", conf.getString("spark.prod.config.localDir"))               // Tmp folder user for Spark splits/shuffle data

    //Original SO Post xml file,
    val postsFile = conf.getString("spark.prod.data.posts")

    //Spark context initialization
    val sc = new SparkContext(sparkConf)
    //Spark way to share required jar's across cluster nodes
    sc.addJar("target/scala-2.10/scalabcn-spark_2.10-0.0.1-SNAPSHOT.jar")

    //Posts RDD
    val postsXml = sc.textFile(postsFile)

    //Parse posts
    val posts = postsXml.filter(line => line.contains("<row"))
      .map(line => XML.loadString(line))
      .map(xmlElement => parseXmlPostElement(xmlElement)).cache

    //Do whatever you want with RDD[Post]...
    println(posts.count)

  }
  
}
