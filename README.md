# CodeSmellDetectorML

How to use: 
-----------

So far the tool allows to learn a machine learning model, serialize the model learn, 
and predict the class of the instances contained in a new dataset.

For learning and serialization are avaiable these flags:
  * -ser `configuration file` for serialize the model learn
  * -print `configuration file` for print at command line the human-readable result of the model learn
  * -save `configuration file` for save the human-readable result of the model learn in a file (one for each classifier chosen)
 
For prediction are avaiable these flags:
  * -pred `configuration file` 
  * -pred `path of the serialized model` `path of the dataset`
  * -pred `path of the dataset` `path of the serialized model`
  
 
 ### Configuration file
 
 ___There are two type of .properties configuration file:___
 
  **Configuration file for serializaton**
  
	dataset = `path of the dataset` (required!)
	path = `path where the serialized model will be saved` (optional)
  	(if "path" will be not specified or it is incorrect the models will be saved in the folder "result" in the repository)
  
  	`name of the classifier or ensemble method` `_` `everything you want` = [`options of the ensemble method`  `name of the classifier`] `options of the classifier`  
  
  **Configuration file for prediction**
  
  	dataset = `path dataset that contain the instances that have to be predict` (required!)
  
  	serialized = `path of the serialized file` (required!)
       
      
 ___There are two type of .yml configuration file:___

  **Configuration file for serializaton**
  
  	dataset: `path of the dataset` (required!)
	path: `path where the serialized model will be saved` (optional)
	(if "path" will be not specified or it is incorrect the models will be saved in the folder "result" in the repository)
  	classifiers:
		-	name: `name of the classifier` (REQUIRED!) 
			options: `options of the classifier` (optional)
			ensemble: `name of the ensemble method` (optional)
			ens_options: `options of the ensemble method` (optional)
			comment: `everything you want` (optional, the program will ingnore this parameters)
			
  **Configuration file for prediction**
  
	dataset: `path dataset that contain the instances that have to be predict` (required!)
	serialized: `path of the serialized file` (required!) 
  
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
   
	serialized = C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model
	
  **Exemple of a .yml configuration file for serialization:**
  
	dataset: C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv 
	classifiers:
    -   name: weka.classifiers.trees.J48
        options: [-U]
        ensemble: weka.classifiers.meta.AdaBoostM1
        
    -   name: weka.classifiers.trees.J48
        ensemble: weka.classifiers.meta.Bagging
        
    -   name: weka.classifiers.trees.J48
        options: [-R]
    
    -   name: weka.classifiers.rules.JRip 
        
    -   name: weka.classifiers.rules.JRip
        ensemble: weka.classifiers.meta.AdaBoostM1
        
    -   name: weka.classifiers.functions.LibSVM
        options: [-P, 100, -S, 1, -K, 2, -D, 3, -G, 0.0, -R, 0.0, -N, 0.2, -M, 40.0, -C, 1.0, -E, 0.001, -seed, 1]  

  
  **Exemple of a .yml configuration file for prediction:**
  
	dataset: C:/Users/uazad/Documents/Progetto/dataset/feature-envy.csv
   
	serialized: C:/Users/uazad/Documents/Progetto/result/5_is_feature_envy_JRip.model
  
  **Exemple of execution**
  
	java -jar CSDML_v1.0.jar -ser -save -print .\configuration\serialization_valid.properties
  
	java -jar CSDML_v1.0.jar -pred .\configuration\try_classification.properties
  
	java -jar CSDML_v1.0.jar -pred ./result/5_is_feature_envy_J48.model ./dataset/feature-envy.csv
  
	java -jar CSDML_v1.0.jar -pred ./dataset/feature-envy.csv ./result/5_is_feature_envy_J48.model
