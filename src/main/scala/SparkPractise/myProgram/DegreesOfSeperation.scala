package SparkPractise.myProgram

import org.apache.spark._
import org.apache.log4j._
import org.apache.spark.rdd._
import org.apache.spark.Accumulator
import scala.collection.mutable.ArrayBuffer

object DegreesOfSeperation {
  
  type BFSdata=(Array[Int],Int,String)
  type BFSNode = (Int, BFSdata)
  
  val startCharacterId = 5306
  val targetCharacterId = 14
  
  var hitCounter:Option[Accumulator[Int]] = None
  
  def convertToBFS(lines:String):BFSNode={
    val fields = lines.split("\\s+")
    val heroId = fields(0).toInt
    var connections:ArrayBuffer[Int] = ArrayBuffer()
    for(connection<- 1 to (fields.length-1)){
      connections+=fields(connection).toInt
    }
    
    var color = "WHITE"
    var distance = 9999
    
    if(heroId==startCharacterId){
      color = "GREY"
      distance = 0
    }
    (heroId,(connections.toArray,distance,color))
  }
  
  def bfsMap(l:BFSNode):Array[BFSNode]={
    val characterId:Int = l._1
    val data:BFSdata = l._2
    val connections:Array[Int]=data._1
    val distance:Int=data._2
    var color=data._3
    var results:ArrayBuffer[BFSNode]=ArrayBuffer()
    
    if(color=="GREY"){
      for(connection<-connections){
        val newCharacterId=connection
        val newDistance=distance+1
        val newColor="GREY"
        
        if(targetCharacterId==connection){
          if(hitCounter.isDefined){
            hitCounter.get.add(1)
          }
        }
        val newentry:BFSNode=(newCharacterId,(Array(),newDistance,newColor))
        results+=newentry
      }
      color="BLACK"
    }
    val thisentry:BFSNode=(characterId,(connections,distance,color))
    results+=thisentry
   
   results.toArray
  }
  

  def createRDD(sc:SparkContext):RDD[BFSNode]={
    val file = sc.textFile("/Users/AVINASH/Documents/Notes PDF/Spark by Udemy/SparkScala/Marvel-graph.txt")
    return file.map(convertToBFS)
  }
  
  def bfsReduce(data1:BFSdata,data2:BFSdata):BFSdata={
    val edge1:Array[Int]=data1._1
    val edge2:Array[Int]=data2._1
    val dist1=data1._2
    val dist2=data2._2
    val color1:String=data1._3
    val color2=data2._3
    
    var distance:Int=9999
    var color:String="WHITE"
    var edges:ArrayBuffer[Int]=ArrayBuffer()
    
    if(edge1.length>1)
      edges++=edge1
    
    if(edge2.length>1)
      edges++=edge2
      
    if(dist1<distance)
      distance=dist1
    
    if(dist2<distance)
      distance=dist2
    
    if(color1=="WHITE" && (color2=="GREY" || color2=="BLACK"))
      color=color2
    if(color1=="GREY" && color2=="BLACK")
      color=color2
    if(color2=="WHITE"&&(color1=="GREY"||color1=="BLACK"))
      color=color1
    if(color2=="GREY" && color1=="BLACK")
      color=color1
      
    (edges.toArray,distance,color)
  }
  
  def main(args:Array[String])={
    Logger.getLogger("org").setLevel(Level.ERROR)
    
    val conf = new SparkConf().setMaster("local").setAppName("DegreesOfSeperation")
    val sc = new SparkContext(conf)
    var lines = createRDD(sc)
    hitCounter = Some(sc.accumulator(0))
    
    var iteration:Int=0
    for(iteration <- 1 to 10){
      
    }
    val linesmapped = lines.flatMap(bfsMap)
    
    if(hitCounter.isDefined){
      val hitCount = hitCounter.get.value
      if(hitCount>0){
        println("Hit the target character! From " + hitCount + 
              " different direction(s).")
      }
    }
    lines = linesmapped.reduceByKey(bfsReduce)
    lines.foreach(println)
  }
}