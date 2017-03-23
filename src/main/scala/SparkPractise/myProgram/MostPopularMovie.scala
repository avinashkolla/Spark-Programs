package SparkPractise.myProgram

import org.apache.spark._
import org.apache.log4j._

object MostPopularMovie {
  
  def parsedLines(lines:String)={
    val line = lines.split("\\s+")
      (line(0).toInt,line.length-1)
  }
  
  def occurances(lines:String):Option[(Int,String)]={
    val line = lines.split("\"")
    if(line.length>1)
      return Some(line(0).trim().toInt,line(1))
     else
       None
  }
  
  def main(args:Array[String])={
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val conf = new SparkConf().setMaster("local[1]").setAppName("MostPopularMovie")
    val sc = new SparkContext(conf)
    
    val file = sc.textFile("/Users/AVINASH/Documents/Software/Data/SparkScala/Marvel-graph.txt")
    val lines = file.map(parsedLines)
    
    
    val names = sc.textFile("/Users/AVINASH/Documents/Software/Data/SparkScala/Marvel-names.txt")
    val namesR = names.flatMap(occurances)
    
    val mapped = lines.reduceByKey((x,y)=>x+y)
    val RDD = mapped.map(x=>(x._2,x._1))
    val maxRDD = RDD.max()
    
    val popularhero = namesR.lookup(maxRDD._2)(0)
    popularhero.foreach(print)
    
        
  }
}