# CodeSmellDetectorML

How to use: 
-----------

So far the tool allows to learn a machine learning model, serialize the model learn, 
predict the class of the instances contained in a new dataset, apply a cross validation 
and allow to execute a weka experiment.

For learning and serialization are avaiable these flags:
  * -ser `configuration file` for serialize the model learn
  * -print `configuration file` for print at command line the human-readable result of the model learn
  * -save `configuration file` for save the human-readable result of the model learn in a file (one for each classifier chosen)
 
For prediction are avaiable these flags:
  * -pred `configuration file` 
  * -pred `path of the serialized model` `path of the dataset`
  * -pred `path of the dataset` `path of the serialized model`
  
  N.B. the dataset for the prediction __must _not_ contain the class that is supposed to be predected__
  
For cross validation are avaiable these flags:
  * -cross `configuration file`
  * -cross -fold `number` `configuration file`
  * -cross -seed `number` `configuration file`
  * -cross -fold `number` -seed `number` `configuration file` 

  N.B. if the fold or the seed are not specified the default numebers will be used, which are respectively 10 and 1
  
For weka experiment are avaiable these flags:
  * -wekaExp -exptype `classification` -splittype `crossvalidation` -runs `# of runs` -folds `# of cross-validation folds`  `configuration file` 
  * -wekaExp -exptype `regression` -splittype `crossvalidation` -runs `# of runs` -folds `# of cross-validation folds` (-randomized)  `configuration file`      
  * -wekaExp -exptype `classification` -splittype `randomsplit` -runs `# of runs` -percentage `percentage for randomsplit`  `configuration file` 
  * -wekaExp -exptype `regression` -splittype `randomsplit` -runs `# of runs` -percentage `percentage for randomsplit` (-randomized)  `configuration file` 
  
  N.B. if one of the flag are not specified or are incorrect the default values will be used, which are:
       exptype: classification, splittype: crossvalidation, runs: 10, fold: 10, percentage: 66.0, not randomized
   
 ### Configuration file
 
 ___There are two type of .properties configuration file:___
 
  **Configuration file for serializaton, cross validation and weka experiment**
  
	dataset = `path of the dataset` (required!)
	path = `path where all the files will be saved` (optional)
  	(if "path" will be not specified or it is incorrect the models will be saved in the folder "result" in the repository)
  
  	key = [`name of the ensemble method` `options of the ensemble method`]  `name of the classifier` `options of the classifier`(required at least one classifier)
	
N.B. the names are inteded as the name __complete with the path__ of the weka class that corrispond with the classifier 
     you want to use (see [weka documetation](http://weka.sourceforge.net/doc.stable/)). You can also copy and paste the
     configuration of the classifier generate by the weka GUI.
  
  **Configuration file for prediction**
  
  	dataset = `path dataset that contain the instances that have to be predict` (required!)
  
  	key = `path of the serialized file` (required at least one)
       
      
			
  
  ### Exemples
  
  **Exemple of a .properties configuration file for serialization:**
  
	dataset = C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
 
	J48_unpruned = weka.classifiers.meta.AdaBoostM1 -I 2 -W weka.classifiers.trees.J48 -- -U
	weka.classifiers.meta.Bagging -W weka.classifiers.trees.J48
	weka.classifiers.trees.J48 -R
	weka.classifiers.rules.JRip 
	weka.classifiers.meta.AdaBoostM1 -W weka.classifiers.rules.JRip
	weka.classifiers.functions.LibSVM -P 100 -S 1 -K 2 -D 3 -G 0.0 -R 0.0 -N 0.2 -M 40.0 -C 1.0 -E 0.001 -seed 1
  
  **Exemple of a .properties configuration file for prediction:**
  
	dataset: C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
   
	key1 = C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model
	key2 = C:/Users/uazad/Documents/Progetto/result/1_is_feature_envy_AdaBoostM1_J48.model

  
  **Exemple of execution**
  
	java -jar CSDML_v1.0.jar -ser -save -print .\configuration\serialization_valid.properties
  
	java -jar CSDML_v1.0.jar -pred .\configuration\try_classification.properties
  
	java -jar CSDML_v1.0.jar -pred ./result/5_is_feature_envy_J48.model ./dataset/feature-envy.csv
  
	java -jar CSDML_v1.0.jar -pred ./dataset/feature-envy.csv ./result/5_is_feature_envy_J48.model
   
	java -jar CSDML_v1.0.jar -cross -seed 2 -fold 8 .\configuration\serialization_valid.properties
   
	java -jar CSDML_v1.0.jar -wekaExp -exptype classification -splittype crossvalidation -runs 8 -folds 8  .\configuration\serialization_valid.properties
   
	java -jar CSDML_v1.0.jar -wekaExp -exptype regression -splittype randomsplit -runs 6 -percentage 80.0 -randomized .\configuration\try_regression.properties