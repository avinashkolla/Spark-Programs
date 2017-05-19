package SparkPractise.myProgram

import org.apache.spark._

object wordCount {
  
  def main(args: Array[String])={
    val conf=new SparkConf().setMaster("local").setAppName("WordCount")
    val sc=new SparkContext(conf)
    
    val file=sc.textFile("/Users/AVINASH/spark-1.6.2-bin-hadoop2.6/book.txt")
    
    //val lines=file.map(x=>x.split(" "))
    val flines=file.flatMap(x=>x.split(" "))
    
   // val countl=lines.countByValue()
    val countf=flines.countByValue()
    
    //countl.foreach(println)
    println("Words are")
    countf.foreach(println)
    
  }
  
}