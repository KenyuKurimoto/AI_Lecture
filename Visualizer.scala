package SAE

/*
注意：このコードは参考文献「関数型オブジェクト指向AIプログラミング」で用いられていたコードを、
筆者が注釈を加えつつ、手打ちで入力したものである。
*/

import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import javafx.scene.layout.StackPane
import javafx.scene.canvas._
import javafx.scene.image.Image
import javafx.scene.transform.Affine
import java.io._
import javax.imageio.ImageIO
import java.awt.image.BufferedImage
import javafx.animation._
//w:幅、h:高さ、r:列、c:行
class Visualizer(val w: Int, val h: Int, val r: Int, val c:Int, val scale: Double = 1.0){
  var imgData: Array[Byte] = null
  var g: GraphicsContext = null
  //javaFXアプリ以外でjavaFXを使う場合には初期化が必要
  new javafx.embed.swing.JFXPanel
  javafx.application.Platform.runLater(new Runnable{
    override def run(){
      while(imgData == null){
        Thread.sleep(1)
      }
      val stage = new Stage
      val canvas = new Canvas(scale*((w+1)*c+1), scale*((h+1)*r+1))
      val pane = new StackPane
      pane.getChildren.add(canvas)
      stage.setScene(new Scene(pane))
      stage.show
      g = canvas.getGraphicsContext2D
      g.setTransform(new Affine(scale, 0, 0, 0, scale, 0))
      new AnimationTimer{
        override def handle(now: Long){
          draw
        }
      }.start
    }
  })

  def draw(){
    for(i <- 0 until r; j <- 0 until c){
      g.drawImage(getImage(i*c+j), j*(w+1)+1, i*(h+1)+1)
    }
  }
  // データを可視化 
  def dispDataImage(data: Array[Double]){
    val rg = data.max
    imgData = data.map{x => (x/rg*255).toByte}
  }
  // 重み行列を表示
  def dispWeightImage(weight: Array[Array[Double]]){
    imgData = weight.map{v =>
      val mi = v.min
      val rg = v.max - mi
      v.map(x => ((x-mi)/rg*255).toByte)
    }.flatten
  }

  def grayScale(b: Byte) = {
    val v = b & 0xFF
    (v<<16) | (v<<8) | v
  }

  def getImage(idx: Int): Image = {
    val b = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
    val len = w*h
    val off = len*idx
    b.setRGB(0,0,w,h,imgData.slice(off, off+len).map(grayScale(_)),0,w)
    val out = new ByteArrayOutputStream
    //例外処理
    try{
      ImageIO.write(b,"bmp",out)
      out.flush
      val img = new Image(new ByteArrayInputStream(out.toByteArray))
      out.close
      img
    }
    catch{
      case e: IOException => println(e)
      null
    }
  }
}
