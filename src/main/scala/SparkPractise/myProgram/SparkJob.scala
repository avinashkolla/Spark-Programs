package SparkPractise.myProgram

import org.apache.log4j._
import org.apache.spark._
import com.datastax.spark.connector._
import org.apache.spark.sql._

object SparkJob {
  def main(args:Array[String])={
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val conf = new SparkConf(true)
    .set("spark.cassandra.connection.host", "localhost")
    .set("spark.cassandra.connection.port","9042")
        .set("spark.cassandra.auth.username", "cassandra")            
        .set("spark.cassandra.auth.password", "cassandra")
    
    val sc = new SparkContext(conf)
    val sqlContext = new org.apache.spark.sql.SQLContext(sc)
    val table = sc.cassandraTable("test", "test.kv").select("*").foreach(println)
    val df = sqlContext.read
    .format("org.apache.spark.sql.cassandra")
    .options(Map( "table" -> "test,kv", "keyspace" -> "test" ))
    .load()
    
    sc.stop()  
  }
    
}