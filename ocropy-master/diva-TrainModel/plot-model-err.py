# plot Model error rates
import numpy
import matplotlib
import matplotlib.pyplot as plt
import os, sys,ocrolib,argparse
from pylab import plot,title,show

x = [2,5,10,15,20]
y = [15.324, 11.582, 8.266, 7.508, 7.461]

fig = plt.figure()
plt.plot(x, y)
#plt.title("%.2f,%s,%d"%(min(err),m_name[err.index(min(err))],len(files)))
plt.title("Recognition error rate")
plt.xlim([0,25])
plt.ylim([0,20])
plt.xlabel('Size of training set')
plt.ylabel('Error rate (100%)')
fig.savefig('Recognition error rate.png')