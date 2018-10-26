import matplotlib.pyplot as plt
import os
import pandas as pd

dir_path = './'

nodes = {}

i = 1
os.chdir(dir_path)
for filename in os.listdir('.'):
	if filename.endswith('log.csv');
		tmp = filename.split('-')
		node = tmp[0]
		df = pd.read_csv(filename,header=None,names=['timestamp','var_name','value'])
		plt.subplot(4,3,i)
		for var in df.var_name.unique():
			tmp_df = df['var_name'==var]
			plt.plot(tmp_df['timestamp'],tmp_df['value'])
	i+=1