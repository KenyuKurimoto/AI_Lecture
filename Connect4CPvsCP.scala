package Connect4

import scala.util._

class Connect4CPvsCP extends Connect4{
  override def f(p:Char, t:List[(Int, Int)]) = {
    //評価値計算
    val mine = t.count(x => bd(x._1)(x._2)==p)
    val space = t.count(y => bd(y._1)(y._2)==' ')
    val yours = 4 - mine - space
    if (yours != 0) 0
    //この辺りの数字を変更するとminmaxの結果が変わってくる
    else{
      if(mine == 4 && p=='R') 40
      else if(mine == 3 && p=='R') 15
      else if(mine == 2 && p=='R') 7
      else if(mine == 1 && p=='R') 3
      else if(mine == 4 && p=='B') 15
      else if(mine == 3 && p=='B') 5
      else if(mine == 2 && p=='B') 3
      else if(mine == 1 && p=='B') 1
      else 0
    }
  }

  override def human(p:Char){
    val((r,c,v),cnt) = alphabetasearch(p, p, 3, 100)
    bd(c)(r) = p
  }

  override def computer(p: Char){
    val r = Random.nextInt(7)
    val c = whichcol(r)
    bd(0)(r) match{
      case ' ' => bd(c)(r) = p
      case _ => computer(p)
    }
    bd(c)(r) = p
  }
  //Scalaはインクリメントが大変。最大の欠点かもしれない。
  val winnum = Stream.from(0).iterator
  val losenum = Stream.from(0).iterator
  val drawnum = Stream.from(0).iterator
  val gametime = Stream.from(0).iterator
  var win = winnum.next
  var lose = losenum.next
  var draw = drawnum.next
  var games = gametime.next
  override def reset(){
    for(i<-0 to 5; j<-0 to 6){
      bd(i)(j)=' '
    }
    winner = ' '
    playing = true
    play()
  }
  override def play(){
    var p = 'B'
    var temp = Random.nextInt(2)
    val games = gametime.next
    if (temp==0) {
      p = 'B'
    }
    else{
      p = 'R'
    }
    do{
      if (p=='B'){
        human(p)
      }
      else {
        computer(p)
      }
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
      win = winnum.next
    }
    else if(winner == 'R'){
      lose = losenum.next
    }
    else{
      draw = drawnum.next
    }
    if(games != 1000) reset
    else println(s"WIN:$win, LOSE:$lose, DRAW:$draw")
  }

}

object Connect4CPvsCPMain extends App {
  new Connect4CPvsCP().play
}
