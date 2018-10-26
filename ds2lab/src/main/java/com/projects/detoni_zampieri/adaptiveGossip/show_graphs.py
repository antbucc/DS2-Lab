import matplotlib.pyplot as plt
import os
import pandas as pd
import numpy as np

dir_path = './../../../../../../../'

nodes = {}

i = 1
os.chdir(dir_path)
for filename in os.listdir('.'):
	if filename.endswith('log.csv'):
		df = pd.read_csv(filename,header=None,names=['timestamp','var_name','value'])
		plt.subplot(4,5,i)
		
		for var in df.var_name.unique():
			tmp_df = df[df['var_name']==var]
			plt.plot(tmp_df['timestamp']-np.min(tmp_df['timestamp']),tmp_df['value'],label=var)

	i+=1

plt.legend()
plt.show()