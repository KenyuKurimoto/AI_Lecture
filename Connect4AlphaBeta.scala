package Connect4

import javafx.application._

object Connect4AlphaBetaMain{
  def main(args: Array[String]) {
    Application.launch(classOf[Connect4AlphaBetaApp], args: _*)
  }
}

class Connect4AlphaBetaApp extends Connect4GraphicsApp{
  override val game = new Connect4AlphaBeta(this)
}

class Connect4AlphaBeta(app: Connect4GraphicsApp) extends Connect4MinMax(app){
  override def computer(p:Char){
    //形式に合わせて最良の一手を採択
    val((r,c,v),cnt) = alphabetasearch(p, p, 3, 1000)
    bd(c)(r) = p
    println("row=>"+r+",search"+cnt+"times")
  }
}
