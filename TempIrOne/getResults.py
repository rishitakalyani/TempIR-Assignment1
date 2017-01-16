
import gensim
import logging
import argparse

parser = argparse.ArgumentParser(description='Script for finding similar items')
parser.add_argument('-w', '--words', type=str, help='Query terms')
args = parser.parse_args()
model = gensim.models.Word2Vec.load('/Users/Rishita/ITIS/semester_02/myModelBonus')
wordlist = args.words.split(',')
discard_wordlist=[]
for wo in wordlist:
    if wo in model.vocab:
        discard_wordlist.append(wo)
ms= model.most_similar(positive=discard_wordlist, topn=5)
y=[]
i=0
for x in ms:
    y.append(x[0])
print y
