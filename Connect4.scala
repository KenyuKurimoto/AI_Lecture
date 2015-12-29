package Connect4

import scala.util._

class Connect4{
  val bd = Array.fill(6)(Array.fill(7)(' '))
  val pat = {val a = List(0,1,2,3); val b = List(0,1,2);
    //垂直
    b.map(r=>a.map(c=>(c+r,0))):::
    b.map(r=>a.map(c=>(c+r,1))):::
    b.map(r=>a.map(c=>(c+r,2))):::
    b.map(r=>a.map(c=>(c+r,3))):::
    b.map(r=>a.map(c=>(c+r,4))):::
    b.map(r=>a.map(c=>(c+r,5))):::
    b.map(r=>a.map(c=>(c+r,6))):::
    //水平
    a.map(r=>a.map(c=>(0,r+c))):::
    a.map(r=>a.map(c=>(1,r+c))):::
    a.map(r=>a.map(c=>(2,r+c))):::
    a.map(r=>a.map(c=>(3,r+c))):::
    a.map(r=>a.map(c=>(4,r+c))):::
    a.map(r=>a.map(c=>(5,r+c))):::
    //斜めは効率の良い選び方が見当たらなかったのですべて書き出す
    //右下へ
    List(List((1,0),(2,1),(3,2),(4,3))):::
    List(List((2,1),(3,2),(4,3),(5,4))):::
    List(List((2,0),(3,1),(4,2),(5,3))):::
    List(List((0,0),(1,1),(2,2),(3,3))):::
    List(List((1,1),(2,2),(3,3),(4,4))):::
    List(List((2,2),(3,3),(4,4),(5,5))):::
    List(List((0,1),(1,2),(2,3),(3,4))):::
    List(List((1,2),(2,3),(3,4),(4,5))):::
    List(List((2,3),(3,4),(4,5),(5,6))):::
    List(List((0,2),(1,3),(2,4),(3,5))):::
    List(List((1,3),(2,4),(3,5),(4,6))):::
    List(List((0,3),(1,4),(2,5),(3,6))):::
    //左下へ
    List(List((0,6),(1,5),(2,4),(3,3))):::
    List(List((1,5),(2,4),(3,3),(4,2))):::
    List(List((2,4),(3,3),(4,2),(5,1))):::
    List(List((1,6),(2,5),(3,4),(4,3))):::
    List(List((2,5),(3,4),(4,3),(5,2))):::
    List(List((2,6),(3,5),(4,4),(5,3))):::
    List(List((0,5),(1,4),(2,3),(3,2))):::
    List(List((1,4),(2,3),(3,2),(4,1))):::
    List(List((2,3),(3,2),(4,1),(5,0))):::
    List(List((0,4),(1,3),(2,2),(3,1))):::
    List(List((1,3),(2,2),(3,1),(4,0))):::
    List(List((0,3),(1,2),(2,1),(3,0)))
  }
  var playing = true
  var winner = ' '
  def goal(p: Char)={
    //ある勝利パターンに含まれるすべてのマスについて同じ文字"p"が入っているならば勝利
    pat.exists(t => t.forall(x => bd(x._1)(x._2)==p))
  }

  def fin(): Boolean = {
    for (r <- 0 to 6; c <- 0 to 5 if bd(c)(r) == ' ')return false
    return true
  }

  def whichcol(r: Int): Int = {
    for(c<-0 to 5 if bd(5-c)(r)==' ') return 5-c
    return 0
  }

  def computer(p: Char){
    val r = Random.nextInt(7)
    val c = whichcol(r)
    bd(0)(r) match{
      case ' ' => bd(c)(r) = p
      case _ => computer(p)
    }
    bd(c)(r) = p
    println(s"row=>$r")
  }

  def human(p: Char){
    print("row => ")
    val s = new java.util.Scanner(System.in)
    val r = s.nextInt
    if(!(r>=0 && r<=6)){
      human(p)
    }
    else{
      val c = whichcol(r)
      bd(0)(r) match{
        case ' ' => bd(c)(r) = p
        case _ => human(p)
      }
    }
  }
  //pはどちら側の評価値を求めているか、tはある勝利パターン(４つのスペースの組み合わせ)
  def f(p:Char, t:List[(Int, Int)]) = {
    //評価値計算
    val mine = t.count(x => bd(x._1)(x._2)==p)
    val space = t.count(y => bd(y._1)(y._2)==' ')
    val yours = 4 - mine - space
    if (yours != 0) 0
    //この辺りの数字を変更するとminmaxの結果が変わってくる
    else{
      if(mine == 4 && p=='B') 40
      else if(mine == 3 && p=='B') 15
      else if(mine == 2 && p=='B') 7
      else if(mine == 1 && p=='B') 3
      else if(mine == 4 && p=='R') 15
      else if(mine == 3 && p=='R') 5
      else if(mine == 2 && p=='R') 3
      else if(mine == 1 && p=='R') 1
      else 0
    }
  }

  def eval(p:Char)={
    //勝利パターンになりうる4箇所の組み合わせすべてについて評価値を求めその合計を求める
    pat.map(t => f(p,t)).sum
  }
  //各列に駒を落としてみて、評価関数が最大になる
  //pは誰の手番か(コンピューター)、pswは誰の手を評価しているか、levelは階層
  //返すのは((評価値の最小化あるいは最大化によって得られた位置,,評価値), 探索回数)
  def minmaxsearch(p: Char, psw: Char, level: Int): ((Int,Int,Int),Int)={
    val myTurn = psw == p
    var minmax = (0,0,if (myTurn) Int.MinValue else Int.MaxValue)
    var searchtime = 0
    for(r<-0 to 6 if bd(0)(r)==' '){
      var c = whichcol(r)
      bd(c)(r) = psw
      val v = if(level == 1 || goal(psw) || fin){
                searchtime += 1
                eval(p)
              }
              else{
                //階層が1でないなら一つ上の階層に上がる
                val vrec = minmaxsearch(p, turn(psw), level-1)
                searchtime += vrec._2
                //MaxあるいはMinの評価値
                vrec._1._3
              }
      bd(c)(r) = ' '
      //自分の番ならMaxを採用し、相手の番ならMinを採用する
      if((myTurn && v > minmax._3) || (!myTurn && v < minmax._3)){
        minmax = (r,c,v)
      }
    }
    (minmax, searchtime)
  }
  //引数が一つ増えているがminmaxと一緒
  def alphabetasearch(p: Char, psw: Char, level: Int, alphaBeta: Int):((Int,Int,Int),Int)={
    val myTurn = psw == p
    var minmax = (0,0,if (myTurn) Int.MinValue else Int.MaxValue)
    var searchtime = 0
    for(r<-0 to 6 if bd(0)(r)==' '){
      var c = whichcol(r)
      bd(c)(r) = psw
      val v = if(level == 1 || goal(psw) || fin){
                searchtime += 1
                eval(p)
              }
              else{
                val vrec = alphabetasearch(p, turn(psw), level-1, minmax._3)
                searchtime += vrec._2
                //MaxあるいはMinの評価値
                vrec._1._3
              }
      bd(c)(r) = ' '
      //変わったのはこの打ち切り操作だけ
      //自分の番であり、評価値が現在の最大値より小さくなったら探索終了
      //相手の番であり、評価値が現在の最小値より大きくなったら探索終了

      if((myTurn && v >= alphaBeta) || (!myTurn && v <= alphaBeta)){
        return ((r, c, v), searchtime)
      }

      if((myTurn && v > minmax._3) || (!myTurn && v < minmax._3)){
        minmax = (r,c,v)
      }
    }
    (minmax, searchtime)
  }
  def turn(p:Char) = {if(p == 'B'){'R'} else 'B'}//OK

  def disp(){
    println(bd.map(_.mkString("|")).mkString("\n"))
  }//OK

  def reset(){
    var retry = new java.util.Scanner(System.in)
    var answer = retry.nextInt
    if(answer==1){
      for(i<-0 to 5; j<-0 to 6){
        bd(i)(j)=' '
      }
      winner = ' '
      playing = true
      play()
    }
    else{
      println("GOOD BYE!")
    }
  }

  def play(){
    var p = 'B'
    var temp = Random.nextInt(2)
    if (temp==0) {
      p = 'B'
      println("YOU MOVE FIRST.")
    }
    else{
      p = 'R'
      println("COMPUTER MOVES FIRST.")
    }
    disp
    do{
      if (p=='B'){
        println("YOUR TURN")
        human(p)
      }
      else {
        println("COMPUTER'S TURN")
        computer(p)
      }
      disp
      if(goal(p)){
        winner = p
        playing = false
      }
      else if (fin){
        playing = false
      }
      else{
        p=turn(p)
      }
    }while(playing)
    if(winner == 'B'){
      println("YOU WIN!")
    }
    else if(winner == 'R'){
      println("YOU LOSE")
    }
    else{
      println("DRAWN")
    }
    println("PLAY AGAIN? (YES->1 : NO->0)")
    reset
  }
}

object Connect4Main extends App {
  new Connect4().play
}
