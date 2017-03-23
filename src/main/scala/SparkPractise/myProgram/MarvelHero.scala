package SparkPractise.myProgram

import org.apache.spark._
import org.apache.log4j._
import scala.io.Source
import scala.io.Codec
import java.nio.charset.CodingErrorAction

object MarvelHero {
  
  def linkHeroNames():Map[Int,String]={
    implicit val codec = Codec("UTF-8") 
   codec.onMalformedInput(CodingErrorAction.REPLACE)
   codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
    var linkHero:Map[Int,String]=Map()
    val file = Source.fromFile("/Users/AVINASH/Documents/Notes PDF/Spark by Udemy/SparkScala/Marvel-names.txt").getLines()
    for(f<-file){
      var fields=f.split(" ")
      if(fields.length>1){
        linkHero += (fields(0).toInt->fields(1))
      }
    } 
    linkHero
  }
  
  def main(args:Array[String])={
    
    Logger.getLogger("org").setLevel(Level.ERROR)
    val conf = new SparkConf().setMaster("local").setAppName("Marvel")
    val sc = new SparkContext(conf)
    var names = sc.broadcast(linkHeroNames)
    val file = sc.textFile("/Users/AVINASH/Documents/Notes PDF/Spark by Udemy/SparkScala/Marvel-graph.txt")
    val lines = file.map(x=>x.split(" ")(0).toInt) 
    val fields = lines.map(x=>(x,1))
    val line = fields.reduceByKey((x,y)=>x+y)
    val sorted = line.sortByKey()
    val result = sorted.map(x=>(names.value(x._2),x._2))
  }
  
}