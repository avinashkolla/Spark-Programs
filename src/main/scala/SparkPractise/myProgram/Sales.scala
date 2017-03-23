package SparkPractise.myProgram

import org.apache.spark._

object Sales {
  
  def parsedLines(lines:String)={
    val fields=lines.split(",")
    (fields(0).toInt,fields(2).toFloat)
  }

  def main(args:Array[String])={
    val conf=new SparkConf().setMaster("local").setAppName("Sales")
    val sc=new SparkContext(conf)
    
    val file=sc.textFile("/Users/AVINASH/Documents/Notes PDF/Spark by Udemy/SparkScala/customer-orders.csv")
    
    val lines=file.map(parsedLines)
    val total=lines.reduceByKey((x,y)=>x+y)
    total.sortByKey().foreach(println)
    total.map(x=>(x._2,x._1)).sortByKey(false).foreach(println)
    
   
    
  }
}