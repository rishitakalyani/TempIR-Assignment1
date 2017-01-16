#!/usr/bin/env python
# -*- coding: utf-8 -*-

import logging
import gensim
import os
import argparse

parser = argparse.ArgumentParser(description='Script for creating model')
parser.add_argument('-c', '--corpus', type=str, help='Path to corpus')
args = parser.parse_args()


logging.basicConfig(format='%(asctime)s : %(levelname)s : %(message)s', level=logging.INFO)
from gensim.models import Word2Vec
min_count=1
size=500
window=5

class MySentences(object):
    def __init__(self, dirname):
        self.dirname = dirname
    
    def __iter__(self):
        for fname in os.listdir(self.dirname):
            for line in open(os.path.join(self.dirname, fname)):
                yield line.split()

sentences = MySentences(args.corpus) # a memory-friendly iterator
model = gensim.models.Word2Vec(sentences, size=200, window=5, min_count=3, workers=4)
#stoplist = set('for a an of the and to in'.split())
#discard_stopwords = lambda: ((word for word in sentence.lower() if word not in stopword_set) for sentence in sentences)
#model.build_vocab(discard_stopwords()))
#model.train(discard_stopwords())
model.save('/Users/Rishita/ITIS/semester_02/myModelBonus')


