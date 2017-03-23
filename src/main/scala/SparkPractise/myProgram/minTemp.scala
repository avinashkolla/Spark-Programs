package SparkPractise.myProgram

import org.apache.spark.{SparkConf,SparkContext}
import org.apache.spark._
import scala.math._

object minTemp {
  
  def parsedLines(lines:String)={
    val line=lines.split(",")
    val stationId=line(0)
    val tempType=line(2)
    val temp=line(3).toFloat*0.1f*(9f/5f)+32f
    (stationId,tempType,temp)
  }
  
  def main(args: Array[String])={
  
    
    val conf=new SparkConf().setMaster("local").setAppName("Min Temp")
    val sc=new SparkContext(conf)
    
    val file=sc.textFile("/Users/AVINASH/Documents/Data/SparkScala/1800.csv")
    
    val lines=file.map(parsedLines)
    val filtered=lines.filter(x=>x._2=="TMAX")
    val conv=filtered.map(x=>(x._1,x._3.toFloat))
 
    val redd=conv.reduceByKey((x,y)=> max(x,y))
    
   
    
    for(red<-redd.sortByKey()){
        val station=red._1
        val t=red._2
        println(f"$station has min temp with $t%.02f")
    }
    
    /*val text=sc.textFile("/Users/AVINASH/Desktop/text.txt")
    val lines=text.map(x=>x)
    val flatlines=text.flatMap(x=>x)
    
    lines.foreach(println)
    flatlines.foreach(println)
    */
     
    //val red=redd.take(2).drop(0)
    //red.foreach(println)
   
  }
  
}