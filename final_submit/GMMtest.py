
import numpy as np
import pandas as pd
from sklearn.externals.six.moves import xrange
from sklearn.mixture import GMM
from sklearn.externals import joblib


train=pd.read_csv('data_final.csv',header=0)
X=train
y=train

X=X.drop(['y'],axis=1) #remove the output label column

y=y.drop(['lr','rl','Lr','Rl','ll','rr','l','r','L','R','Space','Enter','cpm'],axis=1) #remove all the columns except the output label

X=X.values
y=y.values


X_train=X
y_train=y


#GMM 
n_classes=len(np.unique(y_train))
classifiers = dict((covar_type, GMM(n_components=n_classes,
                    covariance_type=covar_type, init_params='wc', n_iter=40))
                   for covar_type in ['tied'])
                       
n_classifiers = len(classifiers)



for index, (name, classifier) in enumerate(classifiers.items()):
    

    y_train = y_train.flatten()


#    z = y_train == (0+1)
#    print("z.shape", z.shape)
#    print("X_train[y_train == (0+1)].shape:", X_train[y_train == (0+1)].shape)
    classifier.means_ = np.array([X_train[y_train == (i)].mean(axis=0)
                                  for i in xrange(n_classes)])
#    print("classifier.means_:", classifier.means_)                                  
    #Fitting the training data
    classifier.fit(X_train)
#    #Predictions
#   y_train_pred = classifier.predict(X_train)
#  train_accuracy = np.mean(y_train_pred.ravel() == y_train.ravel()) * 100
  #print "The accuracy in training is ",train_accuracy,"\n"





X_test=pd.read_csv('test_data.csv',header=0)  #test file


X_test=X_test[['lr','rl','Lr','Rl','ll','rr','l','r','L','R','Space','Enter','cpm']]
X_test=X_test.values
GMM_classifier=classifier
y_test=GMM_classifier.predict(X_test)

Person={0:'User1',1:'User2',2:'User3',3:'User4',4:'User5'}

y_test_length=len(y_test)
y_=y_test.tolist()
mode=max(set(y_), key=y_.count)
frequency=y_.count(mode)
accuracy=float(frequency)/len(y_)*100

if  accuracy > 60:
 print "The Person identified typing is ", Person[mode],"with",round(accuracy,2),'% accuracy'
else:
 print "Impostor!"
