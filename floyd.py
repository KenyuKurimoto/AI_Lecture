# -*- coding: utf-8 -*-
#Python3
import random
import math
#ランダムなリストを作る
def create_random_list(n):
    number_list = []
    for i in range(n):
        number_list.append(random.randrange(1,11))
    print(number_list)
    return number_list
#フロイドのリストを作る
def create_root_num_list(n):
    number_list = []
    for i in range(1, n+1):
        number_list.append(math.sqrt(i))
    number_list.reverse()
    return number_list
#欲張り法
def greedy_algo(number_list):
    new_list = sorted(number_list)
    new_list.reverse()
    list1 = []
    list2 = []
    gene = []
    sum1 = 0
    sum2 = 0
    l = len(new_list)
    for i in range(l):
        if sum1 <= sum2:
            sum1 += new_list[i]
            list1.append(new_list[i])
            gene.append(0)
        else:
            sum2 += new_list[i]
            list2.append(new_list[i])
            gene.append(1)
    '''
    print ("GREEDY\n", "group1 : ", list1, "\ngroup2 : ", list2,
            "\ndiffrence : |", sum1, "-", sum2, "|=", math.fabs(sum1-sum2))
    '''
    return gene
#差分法
def diff_algo(number_list):
    new_list = sorted(number_list)
    new_list.reverse()
    l = len(number_list)
    #maingroupはリストのリスト中身のリストはnew_listから取り出した２つの数字とその差の3つの要素を持つ
    maingroup = []
    group1 = []
    group2 = []
    sum1 = 0
    sum2 = 0
    k = 0
    while len(new_list)>1:
        k+=1
        diff = math.fabs(new_list[0]-new_list[1])
        maingroup.append([new_list[0], new_list[1], diff])
        del new_list[0]
        del new_list[0]
        new_list.append(diff)
        new_list = sorted(new_list)
        new_list.reverse()
    #最後に余った要素をgroup1に追加
    group1.append(new_list[0])
    sum1 += new_list[0]
    while k>0:
        if maingroup[k-1][2] in group1:
            sum1 -= maingroup[k-1][2]
            group1.remove(maingroup[k-1][2])
        else:
            sum2 -= maingroup[k-1][2]
            group2.remove(maingroup[k-1][2])
        if sum1 <= sum2:
            group1.append(maingroup[k-1][0])
            sum1+=maingroup[k-1][0]
            group2.append(maingroup[k-1][1])
            sum2 += maingroup[k-1][1]
        else:
            group2.append(maingroup[k-1][0])
            sum2 += maingroup[k-1][0]
            group1.append(maingroup[k-1][1])
            sum1 += maingroup[k-1][1]
        k-=1
    gene = []
    for i in range(len(number_list)):
        if number_list[i] in group1:
            gene.append(0)
        else :
            gene.append(1)
    '''
    print ("DIFFERENCING\n", "group1 : ", group1, "\ngroup2 : ", group2,
            "\ndiffrence : |", sum1, "-", sum2, "|=", math.fabs(sum1-sum2))
    '''
    return gene
#遺伝子の評価値(小さければ小さいほど良い)(number_listは大きい順にソートされているものとする)
def gene_value_check(gene, number_list):
    l = len(gene)
    sum1 = 0
    sum2 = 0
    for i in range(l):
        if gene[i] == 0:
            sum1 += number_list[i]
        else:
            sum2 += number_list[i]
    return math.fabs(sum1-sum2)


#突然変異を回数を決めてランダムに起こす(geneが元の遺伝子、mut_timeが突然変異回数)
def mutation(gene, mut_time):
    l = len(gene)
    for i in range(mut_time):
        a = random.randrange(l)
        if gene[a] == 1:
            gene[a] = 0
        else:
            gene[a] = 1
    return gene
#突然変異を確率的に起こす(各遺伝子が変異を起こす確率は独立、mut_rateは0~100の数字)
def mutation_rate(gene, mut_rate):
    l = len(gene)
    for i in range(l):
        r = random.randrange(100)
        if r < mut_rate:
            if gene[i] == 1:
                gene[i] = 0
            else:
                gene[i] = 1
    return gene
#二つの遺伝子リストについて交叉したものの一方を返す。もう一方も返してしまえ。
def crossing1(gene1,gene2):
    gene_len = len(gene1)
    new_gene1 = []
    new_gene2 = []
    start_cross = random.randrange(gene_len)
    for i in range(gene_len):
        if i < start_cross:
            new_gene1.append(gene1[i])
            new_gene2.append(gene2[i])
        else :
            new_gene1.append(gene2[i])
            new_gene2.append(gene1[i])
    return new_gene1
def crossing2(gene1,gene2):
    gene_len = len(gene1)
    new_gene1 = []
    new_gene2 = []
    start_cross = random.randrange(gene_len)
    for i in range(gene_len):
        if i < start_cross:
            new_gene1.append(gene1[i])
            new_gene2.append(gene2[i])
        else :
            new_gene1.append(gene2[i])
            new_gene2.append(gene1[i])
    return new_gene2

'''
遺伝的アルゴリズム発動(number_listは評価値を出すための元の数字のリスト(ソート済み)、main_gene
は欲張り法などで出した遺伝子配列、gene_numは集団の中の遺伝子リストの数(不変)、generationは世代数)
'''
def genetic_algo(number_list, main_gene, gene_num, generation):
    gene_list = [main_gene]
    gene_len = len(main_gene)
    #ランダムに突然変異を起こしてメインとなる遺伝子配列の派生系をgene_num-1個作る
    #main_geneと合わせてgene_num個の遺伝子リストを生み出す
    for i in range(gene_num-1):
        gene_list.append(mutation(main_gene, 10))
    #評価値の小さい順に並び替え
    gene_list = sorted(gene_list, key = lambda x:gene_value_check(x,number_list))
    #規定の世代の数だけ以下を実行
    for  i in range(generation):
        #評価値の近い者同士で交叉、交叉の度合いはランダム
        for m in range(len(gene_list)-1):
            gene_list.append(crossing1(gene_list[m],gene_list[m+1]))
            gene_list.append(crossing2(gene_list[m],gene_list[m+1]))
        #突然変異
        for j in range(1,len(gene_list)):
            gene_list[j] = mutation_rate(gene_list[j],5)
        #評価値の小さい順に並び替え
        gene_list = sorted(gene_list, key = lambda x:gene_value_check(x,number_list))
        #規定の遺伝子リストの数(gene_num)になるように評価値の大きいものを淘汰
        for k in range(gene_num,len(gene_list)):
            del gene_list[gene_num]
    return gene_list[0]

def Floyd():
    number_list = create_root_num_list(50)
    #基準となる遺伝子は欲張り法によって生まれたもの
    main_gene = diff_algo(number_list)
    final_gene = genetic_algo(number_list, main_gene, 20, 100)
    group1 = []
    group2 = []
    sum1 = 0
    sum2 = 0
    for i in range(len(final_gene)):
        if final_gene[i] == 0:
            group1.append(number_list[i])
            sum1 += number_list[i]
        else :
            group2.append(number_list[i])
            sum2 += number_list[i]
    answer = math.fabs(sum1-sum2)
    print("Group1:",group1,"\nGroup2:",group2,"\nDifference", answer)
    return [answer, group1, group2]

def test_Floyd(n):
    MIN = 100.0
    group1 = []
    group2 = []
    for i in range(n):
        [a, group1_, group2_] = Floyd()
        if a < MIN:
            MIN = a
            group1 = group1_
            group2 = group2_
    print ("\nGroup1:",group1,"\nGroup2:",group2,"\nDifference", MIN)

test_Floyd(1000)
