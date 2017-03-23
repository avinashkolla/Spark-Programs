package SparkPractise.myProgram

import org.apache.spark._
import java.nio.charset.CodingErrorAction
import org.apache.log4j._
import scala.io.Codec
import scala.io.Source

object PopularMoviesBetter {
  
  //loads up a Map of movieIds to MovieNames
  def linkMovieName():Map[Int,String]={
    
    //funky character in foreign language so using UTF-8 codec
   implicit val codec = Codec("UTF-8") 
   codec.onMalformedInput(CodingErrorAction.REPLACE)
   codec.onUnmappableCharacter(CodingErrorAction.REPLACE)
   var movieNames:Map[Int,String] = Map()
   
  val lines = Source.fromFile("/Users/AVINASH/Documents/Data/ml-100k/u.item").getLines()
   for(line<-lines){
      var fields = line.split('|')
      if(fields.length>1){
        movieNames += (fields(0).toInt -> fields(1))
      }
   }
   movieNames
  }
  
  def main(args:Array[String])={
    Logger.getLogger("org").setLevel(Level.ERROR)
    val conf = new SparkConf().setMaster("local").setAppName("PopularMoviesBetter")
    val sc = new SparkContext(conf)
    
    val file = sc.textFile("/Users/AVINASH/Documents/Data/ml-100k/u.data")
    var names = sc.broadcast(linkMovieName)
    val lines = file.map(x=>(x.split("\t")(1).toInt,1))
    val fields = lines.reduceByKey((x,y)=>x+y)
    val movies = fields.map(x=>(x._2,x._1)).sortByKey()
    val moviesNames = movies.map(x=>( names.value(x._2),x._1))
    moviesNames.foreach(println)
    
  }
  
}