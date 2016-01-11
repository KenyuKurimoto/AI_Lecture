package SAE

/*
注意：このコードは参考文献「関数型オブジェクト指向AIプログラミング」で用いられていたコードを、
筆者が注釈を加えつつ、手打ちで入力したものである。
*/

import java.io._
import java.nio.ByteBuffer

object Data{
  // イメージデータとラベルデータはそれぞれ別々のファイルに入っており、それを格納するための配列
  var imgData, labelData: Array[Byte] = null
  var w,h = 0
  var buf = new Array[Byte](4)

  //
  def readInt(st: BufferedInputStream) = {
    st.read(buf)
    ByteBuffer.wrap(buf).getInt
  }

  def readFile(fileName: String)(fun:(BufferedInputStream)=>Unit){
    var st:BufferedInputStream = null
    try{
      st = new BufferedInputStream(new FileInputStream(fileName))
      readInt(st)
      fun(st)
    }
    catch{
      case e: Exception => println(e)
    }
    finally{
      if(st!=null){
        st.close
      }
    }
  }
  //イメージ読み込み、mは件数、ファイル名にはMNISTデータを渡す
  def readImage(fileName: String, m: Int = 0)={
    readFile(fileName){st =>
      val n = readInt(st)
      h = readInt(st)
      w = readInt(st)
      val size = h*w*(if(m>0) m else n)
      imgData = new Array[Byte](size)
      val len = st.read(imgData, 0, size)
      printf("Image loaded : %d/%d\n", len/(h*w), n)
    }
    getData
  }
  // ラベル読み込み、mは件数、ファイル名にはMNISTデータを渡す
  def readLabel(fileName: String, m: Int = 0)={
    readFile(fileName){st=>
      val n = readInt(st)
      val size = if(m>0) m else n
      labelData = new Array[Byte](size)
      val len = st.read(labelData, 0, size)
      printf("Label loaded: %d/%d\n", len, n)
    }
    getLabel
  }

  def getData()={
    val len = w*h
    imgData.map(v => (v & 0xFF)/255.0).grouped(len).toArray
  }

  def getLabel()={
    labelData.map{v =>
      val a = new Array[Double](10)
      a(v) = 1.0
      a
    }
  }
}
