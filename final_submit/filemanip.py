
import numpy as np
import pandas as pd
import csv
from collections import defaultdict
import re




def monthvalue(date):
 return{
  'Jan' : 1,'Feb' : 2,'Mar' : 3,'Apr' : 4,'May' : 5,'Jun' : 6,'Jul' : 7,'Aug' : 8,'Sep' : 9,'Oct' : 10,'Nov' : 11,'Dec' : 12}[date]

def compute_time_diff(month,time):
 #takes into consideration base month as April
 value=(month-4)*30*24*60*60+(float(time[0])-1)*24*60*60+float(time[1])*60*60+float(time[2])*60+float(time[3])
 return value

def timevalue(dates):
 pattern='\d{1,2}\:\d{2}\:\d{2}\:\d{2}\.\d{6}'
 for index,date in enumerate(dates):
   month=monthvalue(re.search('^([^\s]+)',date).group(0))
   time=re.search(pattern,date).group(0).split(':')
   #takes into consideration base date as April 1:00:00:00:000000
   value=compute_time_diff(month,time)
   dates[index]=value
 return dates







def categorize(chars):
   hand_a=lr(chars[0])
   hand_b=lr(chars[2])
   category={('l',False,'r',False):1,('l',True,'r',True):1,
             ('r',False,'l',False):2,('r',True,'l',True):2,
             ('l',False,'r',True):3,
             ('l',True,'r',False):4,
             ('r',False,'l',True):5,
             ('r',True,'l',False):6,
             ('l',False,'l',True):7,('l',True,'l',False):7,
             ('r',False,'r',True):8,('r',True,'r',False):8,
             ('l',False,'l',False):9,('l',True,'l',True):9,
             ('r',False,'r',False):10,('r',True,'r',True):10}
   
   category=defaultdict(lambda: 0,category)
  
   return category[hand_a,chars[1],hand_b,chars[3]]



def lr(x):
 
  dict={82:'l',88:'l',70:'l',83:'l',85:'l',66:'l',84:'l',69:'l',71:'l',72:'l',91:'l',89:'l',68:'l',87:'l',67:'l',50:'l',51:'l',52:'l',53:'l',54:'l',193:'l', 90:'r',86:'r',74:'r',80:'r',81:'r',220:'r',222:'r',73:'r',75:'r',76:'r',77:'r', 187:'r',223:'r',79:'r',78:'r',189:'r',191:'r',192:'r',55:'r',56:'r',57:'r',58:'r',49:'r',190:'r',188:'r',10:'b', 14:'E', 33:'S'
      }
  return dict.get(x,'n')


def lr_holdtime(x):
 
 category={('l',False):1,('r',False):2,('l',True):3,('r',True):4,('E',False):5,('E',True):5,
  ('S',False):6,('S',False):6
  }
   
 category=defaultdict(lambda: 0,category)
    
 return category[lr(x[0]),x[1]]


for m in range(0,5):
  fr=open(str(m)+'.txt','rb')
  info=fr.readlines()
  fw=open(str(m)+'.csv','wb')

  in_txt=csv.reader(info,delimiter='\t')
  out_csv=csv.writer(fw)
  out_csv.writerows(in_txt)

  fr.close()
  fw.close()
  columns=['Event_Type','Key_Code','Shift','Alt','Control','Time']
  df=pd.read_csv(str(m)+'.csv')
  if len(df.columns)>6:
    for i in range(len(df.columns)-1,5,-1):
        df=df.drop(df.columns[[i]],axis=1)
  df.columns=columns;

  del_rows=[10,38,39,40,41,165,161,163,162,164,166]
  for i in del_rows:
    df=df[df.Key_Code!=i]
  df=df[df.Alt==False]
  df=df[df.Control==False]
  df=df.drop('Alt',axis=1)
  df=df.drop('Control',axis=1)

  dates=df.Time.tolist()
  dates=pd.Series(timevalue(dates))
  df=df.reset_index()
  df.Time=dates
  df=df.drop('index',axis=1)




  final_features=pd.DataFrame(columns=('lr','rl','lR','Lr','rL','Rl','lL','rR','ll','rr','l','r','L','R','Space','Enter',
                                     'Backspace','cpm'))

  label=m  

  #initial value
  index=-1
  count=0

  while index<len(df)-1:
    
    tmp_df=pd.DataFrame(columns=('Event_Type','Key_Code','Shift','Time'))
    feature_list=np.zeros((2,11))
    mean_latencies=np.zeros(11)
    i=0    
    index=index+1
    
    tmp=[df.Event_Type[index],df.Key_Code[index],df.Shift[index],df.Time[index]]
    tmp_df.loc[i]=tmp

    i=i+1

    while 1:
        
        if index>len(df)-1:
            break
        
        tmp=[df.Event_Type[index],df.Key_Code[index],df.Shift[index],df.Time[index]]
        if tmp[3]-tmp_df.Time[i-1]>10:
            break
        
        if tmp[3]-tmp_df.Time[0]>60:
            break
        
        tmp_df.loc[i]=tmp
        i=i+1
        index=index+1
            
    
    
    if len(tmp_df.Time)<100:
        continue
    
   
    #Calculating latencies

    
    
    for j in range(0,len(tmp_df.Time)-1):
        
        flag=0
        
        if tmp_df.Event_Type[j]=='KeyDown':
            for k in range(j+1,len(tmp_df.Time)):
                if tmp_df.Event_Type[k]=='KeyDown':
                    flag=1
                    chars=[tmp_df.Key_Code[j],tmp_df.Shift[j],tmp_df.Key_Code[k],tmp_df.Shift[k]]
                    latency=tmp_df.Time[k]-tmp_df.Time[j]
                    break
                
        if flag==1 and latency<1.5:           
            feature=categorize(chars)
            feature_list[0][feature]=feature_list[0][feature]+latency
            feature_list[1][feature]=feature_list[1][feature]+1
    
    mean_latencies=feature_list[0]/feature_list[1]

    
    #Calculating hold times and number of backspaces and cpm
    feature_list_holdtime=np.zeros((2,7))
    backspaces=0
    characters=0
    cpm=float('NaN')
    backspace_per_character=float('NaN')
    
    for j in range(0,len(tmp_df.Time)-1):
        
        flag=0
        
        if tmp_df.Event_Type[j]=='KeyDown':
            key=tmp_df.Key_Code[j]    
            for k in range(j+1,len(tmp_df.Time)):
                if tmp_df.Key_Code[k]==key:
                    flag=1
                    chars=[key,tmp_df.Shift[j]]
                    holdtime=tmp_df.Time[k]-tmp_df.Time[j]
                    break
        
            characters=characters+1
            
            if key==9:
                backspaces=backspaces+1
                
        if flag==1 and holdtime<1.5:
            feature=lr_holdtime(chars)
            feature_list_holdtime[0][feature]=feature_list_holdtime[0][feature]+holdtime
            feature_list_holdtime[1][feature]=feature_list_holdtime[1][feature]+1
        
    mean_holdtime=feature_list_holdtime[0]/feature_list_holdtime[1]
        
    if characters>100:
        cpm=characters/(tmp_df.Time[len(tmp_df.Time)-1]-tmp_df.Time[0])*60
        backspace_per_character=float(backspaces)/characters
    

    
    mean_latencies=np.delete(mean_latencies,0,0)
    mean_holdtime=np.delete(mean_holdtime,0,0)    
    total_features=np.append(mean_latencies,mean_holdtime)
    total_features=np.append(total_features,backspace_per_character)
    total_features=np.append(total_features,cpm)
    
        
    
    final_features.loc[count]=total_features
    count=count+1    
    
    # print count,'-',index, '-',len(tmp_df.Time)
    

           

  new_final_features=final_features.dropna(how='all')
  new_final_features=new_final_features.reset_index()
  tmp=np.empty(len(new_final_features))
  tmp.fill(label)

  new_final_features['y'] = pd.Series(tmp)
  new_final_features_median=new_final_features.fillna(final_features.median())
  new_final_features_median.to_csv('final_median'+str(m)+'.csv',index=False)
  new_final_features_mean=new_final_features.fillna(final_features.mean())
  new_final_features_mean.to_csv('final_mean'+str(m)+'.csv',index=False)


file1=pd.read_csv('final_mean0.csv')
file1=file1.drop('index',axis=1)
file2=pd.read_csv('final_mean1.csv')
file2=file2.drop('index',axis=1)
file3=pd.read_csv('final_mean2.csv')
file3=file3.drop('index',axis=1)
file4=pd.read_csv('final_mean3.csv')
file4=file4.drop('index',axis=1)
file5=pd.read_csv('final_mean4.csv')
file5=file5.drop('index',axis=1)

filetot=file1.append(file2,ignore_index=True)
filetot=filetot.append(file3,ignore_index=True)
filetot=filetot.append(file4,ignore_index=True)
filetot=filetot.append(file5,ignore_index=True)
file1=filetot[['lr','rl','Lr','Rl','ll','rr','l','r','L','R','Space','Enter','cpm','y']]

file1.to_csv('data_final.csv',index=False)
