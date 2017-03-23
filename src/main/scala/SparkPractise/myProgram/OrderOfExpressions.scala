package SparkPractise.myProgram

import org.apache.spark._
import org.apache.log4j._

object OrderOfExpressions {
   def main(args:Array[String])={
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    val sc = new SparkContext("local","MovieSimilarities")
    
    val file=sc.textFile("").map( x=>x.split(""))
    
   }
  
}