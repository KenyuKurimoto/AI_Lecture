package SAE
/*
このコードはほとんどが筆者オリジナルのもの
*/
// mとnはそれぞれ入力層&出力層のノード数、隠れ層のノード数としてあらかじめ与える。
class SAE(val n: Int, val m: Int){
  /*
  yは隠れ層出力(要素数m)
  zは最終出力(要素数n)
  スパースオートエンコーダはmがnに比べて小さくないときに有効
  wは重みづけ(初期値はランダム)＿
  b1は入力層と隠れ層の間で用いられるバイアス(初期値0で隠れ層のノードそれぞれについて異なる値を持つのでm次ベクトル)
  b2は隠れ層と出力層の間で用いられるバイアス(初期値0で出力層のノードそれぞれについて異なる値を持つのでn次ベクトル)
  wDeltaは重み修正値(行列)
  b1Deltaはバイアス修正値(m次ベクトル)
  b2Deltaはバイアス修正値(n次ベクトル)
  betaは
  rhodat(j)はそれまでに登場した入力に対する隠れ層のj番目のノードの出力値の和
  分かりやすくするためにiは0からn、jは0からmの間で変化する変数として統一する
  */
  val y = new Array[Double](m)
  val z = new Array[Double](n)
  val w1 = Array.fill[Double](m,n)((math.random*2-1)*0.01)
  val w2 = Array.fill[Double](n,m)((math.random*2-1)*0.01)
  val b1 = Array.fill[Double](m)(0.0)
  val b2 = Array.fill[Double](n)(0.0)
  val w1Delta = Array.ofDim[Double](m,n)
  val w2Delta = Array.ofDim[Double](n,m)
  val b1Delta = new Array[Double](m)
  val b2Delta = new Array[Double](n)
  val rhohat = new Array[Double](m)
  val alpha = 0.0005
  val beta = 0.05
  val lambda = 0.0
  val rho = 0.2
  // 毎度おなじみ和を求める関数
  def sum(k: Int)(func: (Int) => Double) = {
    var i = 0
    var s = 0.0
    while(i < k){
      s += func(i)
      i += 1
    }
    s
  }
  // バッチループの初めでwDeltaとb1Deltaとb2Deltaの中身を全て0に初期化
  def initDeltaRhohat(){
    for (i <- 0 to n-1){
      b2Delta(i) = 0.0
    }
    for (j <- 0 to m-1){
      b1Delta(j) = 0.0
      rhohat(j) = 0.0
    }
    for (j <- 0 to m-1){
      for (i <- 0 to n-1){
        w1Delta(j)(i) = 0.0
        w2Delta(i)(j) = 0.0
      }
    }
  }
  // シグモイド関数
  def sigmoid(x: Double) = {
    1/(1 + math.pow(math.E, -x))
  }
  // j=0~m-1までy(j)を求める
  // i=0~n-1についてw(j)(i)*x(i)の和を求めて、バイアスを足してシグモイド関数に代入
  def encode(x: Array[Double], y: Array[Double]){
    for (j <- 0 to m-1){
      var wsum = sum(n){i => w1(j)(i)*x(i)}
      y(j) = sigmoid(wsum + b1(j))
    }
  }
  // i=0~n-1までz(i)を求める
  // j=0~m-1についてw(j)(i)*y(j)の和を求めて、バイアスを足してシグモイド関数に代入
  def decode(y: Array[Double], z: Array[Double]){
    for (i <- 0 to n-1){
      z(i) = sigmoid(sum(m){j => w2(i)(j)*y(j)} + b2(i))
    }
  }
  // 一通り計算してyとzを導出
  def propagate(x: Array[Double]){
    encode(x,y)
    decode(y,z)
  }
  // b1Delta,b2Delta,w1Delta,w2Deltaを加算する関数
  def addDelta(x: Array[Double], y: Array[Double], z: Array[Double], rhohat: Array[Double], batchK: Int){
    // b1Temp・b1Tempはb1Delta・b2Deltaに足す前のバイアス修正値
    val b1Temp = new Array[Double](m)
    val b2Temp = new Array[Double](n)
    // Back Propagationに従い、先にb2Delta求める
    for (i <-  0 to n){
      //b2Temp(i) = x(i) - z(i)
      // xを正しい値として参照している
      // シグモイド関数の微分形を利用
      b2Temp(i) = -(x(i)-z(i))*(1-z(i))*z(i)
      b2Delta(i) += -(x(i)-z(i))*(1-z(i))*z(i)
    }
    for (j <- 0 to m){
      // 上の結果を利用してb1Tempを求める
      b1Temp(j) = (sum(n){i => w1(j)(i) * b2Temp(i)})*(1-y(j))*y(j) + sparsity(rho, rhohat(j))
      b1Delta(j) += b1Temp(j)
    }
    for (i <- 0 to n){
      for (j <- 0 to m){
        // 上の2つをちょっと変えたものを足せば良い
        w1Delta(j)(i) += x(i) * b1Temp(j)
        w2Delta(i)(j) += y(j) * b2Temp(i)
      }
    }
  }
  // 散在性の項を計算
  // rhoには定数を、rhohatにはrhohat(j)を入れる
  def sparsity(rho: Double, rhohat: Double) = {
    beta*(-rho/rhohat + (1 - rho)/(1 - rhohat))
  }
  // 訓練用関数
  // dataが扱うデータ、patNは
  def train(data: Array[Array[Double]], patN: Int, trainN: Int, batchK: Int){
    // 訓練ループ開始
    for (k <- 0 to trainN * patN/batchK - 1){
      // 新たな訓練ループを始めるのでdeltaとrhohatを初期化
      initDeltaRhohat
      // 1周目のバッチループで先にrhohatを求めておく
      for (l <- 0 to batchK-1){
        // データの読み込み
        val idx = k * batchK + l
        // 各j毎に(＜このバッチループにおける＞y(j))/batchKを足してやる
        for (j <- 0 to m-1){
          rhohat(j) += (sum(n){i => w1(j)(i)*data(idx)(i)}+b1(j))/batchK//
        }
      }
      // 2周目のバッチループで修正量b1Delta、b2Delta、wDeltaを求める
      for (l <- 0 to batchK-1){
        // 再度データの読み込み
        val idx = k * batchK + l
        // プロパゲーション実行、これによりxyzが定まる
        propagate(data(idx))
        // Deltaを増やしていく
        addDelta(data(idx), y, z, rhohat, batchK)
      }
      // 訓練ループの最後にb1、b2、wを更新する
      for (j <- 0 to m-1){
        b1(j) += -alpha*b1Delta(j)
      }
      for (i <- 0 to n-1){
        b2(i) += -alpha*b2Delta(i)
      }
      for (j <- 0 to m-1){
        for (i <- 0 to n){
          w1(j)(i) += -alpha*(w1Delta(j)(i)/batchK + lambda*w1(j)(i))
          w2(i)(j) += -alpha*(w2Delta(i)(j)/batchK + lambda*w2(i)(j))
        }
      }
    }
  }
}
