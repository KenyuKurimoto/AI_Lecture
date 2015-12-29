ファイル説明
各ファイルはコンパイルが必要です
$ scalac (ファイル名).scala
実行は
$ scala Connect4.(ファイル名)Main
で

ファイル名:説明
Connect4.scala:
クラス"Connect4"+オブジェクト"Connect4App"
探索に必要な関数等はすべてここに収めた。
コマンドラインでランダムに手を選ぶコンピュータと対戦できる。
0~6までの数字を選択して対戦。
終了後もう一度対戦する場合は1を、しない場合は0を選択。

Connect4Graphics.scala:
クラス"Connect4Graphics","Connect4GraphicsApp"+オブジェクト"Connect4GraphicsMain"
グラフィック用。
マウスで行を選択して駒をどこに落とすか決める。

Connect4MinMaxinmax.scala:
クラス"Connect4MinMax","Connect4MinMaxApp"+オブジェクト"Connect4MinMaxMain"
ミニマックス法で挑んでくるコンピュータと対戦できる。

Connect4AlphaBeta.scala:
クラス"Connect4AlphaBeta","Connect4AlphaBetaApp"+オブジェクト"Connect4AlphaBetaMain"
アルファベータカットを追加したコンピュータと対戦できる。

Connect4CPvsCP.scala:
クラス"Connect4CPvsCP"+オブジェクト"Connect4CPvsCPMain"
コンピュータ同士を1000回対戦させ、その勝敗数を返す。
片方はランダムに手を選び、もう片方は探索を用いて戦う。

Connect4HumanvsHuman.scala:
おまけファイル。
人と人との対戦用。
2分で作った。
