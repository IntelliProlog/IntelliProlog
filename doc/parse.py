import re
import codecs
import argparse
import operator
from os import walk
from os.path import join


p = re.compile('\[([^\[\]]+)\]\(([^)]+)\)')
search = p.search

with open('links.txt', 'w+') as l:
  for root, dirs, files in walk('.'):
          for name in files:
              with codecs.open(join(root, name), 'r', 'utf-8') as f:
                  try:
                      data = f.read()
                  except (UnicodeError, ValueError):
                      continue
              match = re.findall('\[([^\[\]]+)\]\(([^)]+)\)', data)
              if match is not None:
                  for i in match:
                    #print(f'@online{{FILL, title = {{ {i[0]} }}, url = {{ {i[1]} }} }} ')
                    #l.write(f'@online{{FILL, title = {{ {i[0]} }}, url = {{ {i[1]} }} }} ')
                    print('@online{FILL, title = {' + i[0] + '}, url = { ' + i[1] + '} }')
