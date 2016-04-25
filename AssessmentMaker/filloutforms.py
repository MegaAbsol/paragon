import glob
from random import choice
import os
os.system('mkdir handin')

a = glob.glob('tests/*/*.txt')
print(a)
for i in a:
    x = open(i,'r').read().split('\n')
    x[2] += i
    x[3] += choice(['A','B','X','Y'])
    for j in range(9,len(x)-1):
        x[j] += choice(['A','B','C','D'])
    y = open('handin/'+i.split('\\')[-1],'w')
    for item in x:
        y.write(item + '\n')
