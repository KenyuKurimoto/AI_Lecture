package Connect4

import javafx.application._

object Connect4MinMaxMain{
  def main(args: Array[String]){
    Application.launch(classOf[Connect4MinMaxApp], args:_*)
  }
}

class Connect4MinMaxApp extends Connect4GraphicsApp{
  override val game = new Connect4MinMax(this)
}

class Connect4MinMax(app : Connect4GraphicsApp) extends Connect4Graphics(app){
  override def computer(p:Char){
    //形式に合わせて最良の一手を採択
    val((r,c,v),cnt) = minmaxsearch(p, p, 4)
    bd(c)(r) = p
    println("row=>"+r+",search"+cnt+"times")
  }
}
