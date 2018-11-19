import matplotlib.pyplot as plt
import os
import pandas as pd
import numpy as np

dir_path = './../../../../../../../../'

nodes = {}

i = 1
os.chdir(dir_path)
for filename in os.listdir('.'):
    if filename.endswith('log.csv'):
		df = pd.read_csv(filename,header=None,names=['timestamp','var_name','value'])
		plt.subplot(4,5,i)

		plt.title(filename)

		for var in df.var_name.unique():
			tmp_df = df[df['var_name']==var]
			plt.plot(tmp_df["timestamp"],(tmp_df['value']-tmp_df["value"].mean())/(tmp_df['value'].max()- tmp_df['value'].min()),label=var)
			#plt.plot(range(0, len(tmp_df['value'])),(tmp_df['value']-tmp_df["value"].mean())/(tmp_df['value'].max()- tmp_df['value'].min()),label=var)

		i+=1
		#break

plt.legend()
plt.show()