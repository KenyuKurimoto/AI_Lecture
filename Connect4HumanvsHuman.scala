package Connect4

import javafx.application._

object Connect4HumanvsHumanMain{
  def main(args: Array[String]){
    Application.launch(classOf[Connect4HumanvsHumanApp], args:_*)
  }
}

class Connect4HumanvsHumanApp extends Connect4GraphicsApp{
  override val game = new Connect4HumanvsHuman(this)
}

class Connect4HumanvsHuman(app : Connect4GraphicsApp) extends Connect4Graphics(app){
  override def computer(p:Char){
    selR = -1
    selC = -1
    while (selR == -1 && selC == -1){
      Thread.sleep(100)
      if(!playing) return
    }
    selC = whichcol(selR)
    if(bd(selC)(selR) != ' ') computer(p)
    else {
      bd(selC)(selR) = p
      println(s"row=>$selR")
    }
  }
}
