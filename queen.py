# -*- coding: utf-8 -*-
# nはn×nの盤面を表す
# queen_arrはクイーンの置いてある位置の配列
# 1ならクイーン、2なナイト
import time

def cell_check_empty(n, queen_arr):
    arr = [[0 for i in range(n)] for j in range(n)]
    empty=0
    # i:row, j:col
    for i in range(n):
        for j in range(n):
            if queen_arr[i][j]==1:
                # 同じ列のマスを埋める
                # この処理は改良した後分岐不要
                for k in range(n):
                    arr[k][j]=1
                # 同じ行のマスを埋める
                # この処理は改良した後分岐不要
                for l in range(n):
                    arr[i][l]=1
                # 斜めの処理
                k=i
                l=j
                while k>=0 and l>=0 :
                    arr[k][l]=1
                    k-=1
                    l-=1
                # 斜めの処理
                k=i
                l=j
                while k<n and l>=0 :
                    arr[k][l]=1
                    k+=1
                    l-=1
                # 斜めの処理
                k=i
                l=j
                while k>=0 and l<n:
                    arr[k][l]=1
                    k-=1
                    l+=1
                # 斜めの処理
                k=i
                l=j
                while k<n and l<n:
                    arr[k][l]=1
                    k+=1
                    l+=1

    for i in range(n):
        for j in range(n):
            if not arr[i][j]==1:
                empty+=1
    # ここの数字を5にすると一気にスピードが上がる。3だとダメ。
    if empty>7:
        return 0
    else :
        for i in range(n):
            for j in range(n):
                if not queen_arr[i][j]==1:
                    knight_check(i,j,arr,queen_arr,n)
        return 0

def knight_check(i,j,arr,queen_arr,n):
    new_arr = [[0 for s in range(n)] for t in range(n)]
    for s in range (n):
        for t in range (n):
            new_arr[s][t] = arr[s][t]
    new_arr[i][j]=1
    if i-1>=0 and j-2>=0 :
        new_arr[i-1][j-2]=1

    if i-2>=0 and j-1>=0 :
        new_arr[i-2][j-1]=1

    if i-1>=0 and j+2<n :
        new_arr[i-1][j+2]=1

    if i-2>=0 and j+1<n :
        new_arr[i-2][j+1]=1

    if i+1<n and j-2>=0 :
        new_arr[i+1][j-2]=1

    if i+2<n and j-1>=0 :
        new_arr[i+2][j-1]=1

    if i+1<n and j+2<n :
        new_arr[i+1][j+2]=1

    if i+2<n and j+1<n :
        new_arr[i+2][j+1]=1

    for s in range(n):
        for t in range(n):
            if not new_arr[s][t]==1:
                return 0
    queen_arr[i][j]=2

    for u in range(n):
        print (queen_arr[u])
    print()

    queen_arr[i][j]=0
    return 0

def choose_4queen(n):
    check_time=0
    for a in range(n-3):
        for b in range(n):
            for c in range(a+1, n):
                for d in range(n):
                    if not d==b:
                        for e in range(c+1, n):
                            for f in range(n):
                                if not (f==b or f==d):
                                    for g in range(e+1, n):
                                        for h in range(n):
                                            if (not h==b) and (not h==d) and (not h==f):
                                                queen_arr = [[0 for i in range(n)] for j in range(n)]
                                                queen_arr[a][b]=1
                                                queen_arr[c][d]=1
                                                queen_arr[e][f]=1
                                                queen_arr[g][h]=1
                                                cell_check_empty(n, queen_arr)
start = time.time()
choose_4queen(8)
elapsed_time = time.time() - start
print("elapsed_time:{0}".format(elapsed_time))
