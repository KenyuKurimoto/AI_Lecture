package SAE

object SAEApp extends App {
  /*
  inputNは入力層・出力層のノード数
  middleNは中間層のノード数
  trainNは訓練ループの回数
  batchKはバッチループの回数
  */
  val patN  = 60000
  val inputN = 784
  val middleN = 600
  val trainN = 10
  val batchK = 20
  //Data.scala中の関数を用いて、訓練データ読み込み
  val trainData = Data.readImage("train-images.idx3-ubyte", patN)
  //新規クラスオブジェクト
  val sae = new SAE(inputN, middleN)
  //上記のように定義したエンコーダで
  sae.train(trainData, patN, trainN, batchK)
  // 0~99番目のデータを読み込んで
  val in = trainData.slice(0,100).flatten
  new Visualizer(28,28,10,10,2).dispDataImage(in)

  val out = trainData.slice(0,100).map{data =>
    sae.propagate(data)
    sae.z.clone
  }.flatten

  new Visualizer(28,28,20,20,2).dispWeightImage(sae.w1)
  new Visualizer(28,28,20,20,2).dispWeightImage(sae.w2)

}
