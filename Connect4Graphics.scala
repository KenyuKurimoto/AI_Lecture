package Connect4

import javafx.application._
import javafx.scene.Scene
import javafx.stage._
import javafx.scene.layout.StackPane
import javafx.scene.canvas._
import javafx.scene.shape._
import javafx.scene.text._
import javafx.scene.effect._
import javafx.scene.control._
import javafx.scene.paint.Color
import javafx.scene.input.MouseEvent
import javafx.event.EventHandler
import javafx.geometry._


object Connect4GraphicsMain{
  def main(args: Array[String]){
    Application.launch(classOf[Connect4GraphicsApp], args: _*)
  }
}

class Connect4GraphicsApp extends Application{
  val w,h = 350
  val canvas = new Canvas(w,h)
  val g = canvas.getGraphicsContext2D
  val game = new Connect4Graphics(this)

  override def start(stage: Stage){
    val pane = new StackPane
    pane.getChildren.add(canvas)
    stage.setScene(new Scene(pane))
    stage.show

    new Thread(){
      override def run(){
        game.play
      }
    }.start

    pane.setOnMouseClicked(new EventHandler[MouseEvent]{
      def handle(e: MouseEvent){
        if(game.selR == -1){
          //ここでselRを決定
          game.selR = (e.getX / w * 7).toInt//
        }
      }
    })
  }

  override def stop(){
    game.playing = false
  }

  def draw(bd: Array[Array[Char]], winner: Char){
    val (dw,dh) = (w/7, h/7)
    if(winner == ' '){
      g.setFill(Color.YELLOW)
    }
    else if (winner == 'B'){
      g.setFill(Color.RED)
    }
    else {
      g.setFill(Color.BLACK)
    }
    g.fillRect(0,0,w,h)
    for(i <- 0 to 7){
      val (x,y) = (i*dw, i*dh)
      g.strokeLine(x,if(i==0 || i==7) 0 else dh,x,h)
      g.strokeLine(0,y,w,y)
    }
    //g.fillText(text, x, y [, maxWidth ] )
    //円の直径に当たる長さ
    val (mw,mh)=(dw*0.6, dh*0.6)
    for(r <- 0 to 6; c <- 0 to 5 if bd(c)(r) != ' '){
      //円の中心に当たる座標
      val(x1,y1)=(r*dw + (dw-mw)*0.5, (c+1)*dh + (dh-mh)*0.5)
      bd(c)(r) match{
        case 'B' => g.setFill(Color.BLACK); g.fillOval(x1,y1,mw,mh)
        case 'R' => g.setFill(Color.RED); g.fillOval(x1,y1,mw,mh)
      }
    }
  }
}

class Connect4Graphics(app: Connect4GraphicsApp) extends Connect4{
  var selR, selC = -1
  override def human(p: Char){
    selR = -1
    selC = -1
    while (selR == -1 && selC == -1){
      Thread.sleep(100)
      if(!playing) return
    }
    selC = whichcol(selR)
    if(bd(selC)(selR) != ' ')human(p)
    else {
      bd(selC)(selR) = p
      println(s"row=>$selR")
    }
  }
  override def disp(){
    var bdCopy = bd.map(_.clone)
    Platform.runLater(new Runnable{
      def run(){app.draw(bdCopy, winner)}
    })
  }
}
