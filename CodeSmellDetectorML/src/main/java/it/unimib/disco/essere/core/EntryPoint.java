package it.unimib.disco.essere.core;


import weka.classifiers.Classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import it.unimib.disco.essere.load.*;

public class EntryPoint {

	private ArrayList<Classifier> classifiers;
	private Loader configuration;
	private Serializer serializer = new Serializer();
	private DataClassifier  classifier;
	private Predictor predictor;

	public EntryPoint(){}
	
	public static void main(String[] args){ 
		EntryPoint workflow = new EntryPoint();
		try {
			workflow.start(args);
		} catch (Exception e) {
			// do nothing, the error message are already print out
		}
		
	}
	
	public void start(String[] args) throws Exception{
		List<String> input = Arrays.asList(args);
		
		if(input.contains("-pred")){
			if(args.length == 2)
				this.predict(args[args.length - 1]);
			else
				this.predict(args[args.length - 1], args[args.length - 2]);

		}else{
			if(input.contains("-print") || input.contains("-save") || input.contains("-ser")){
				this.load(args[args.length - 1]);
				this.classify(); 

				if(input.contains("-print"))
					this.printClassifier();

				if(input.contains("-save"))
					this.saveClassifier();

				if(input.contains("-ser"))
					this.serialize();
			}else{
				System.out.println("No valid operation selected, please use:");
				System.out.println("-ser for serialize the classifier specified in the configuaration file");
				System.out.println("-print for print the human-readable result of classification");
				System.out.println("-save for save the human-readable result of classification");
				System.out.println("-pred for predict the class of a new dataset");
				System.out.println("\n For more information on how to use it please read the README.MD");
			}
		}
	}
	
	public void load(String path) throws Exception{
		try {
			configuration = new LoaderProperties();
			classifiers = configuration.load(path);
		} catch (Exception e1) {
			System.out.println("ERROR : Invalid or not found property file, please check the path");
			//System.exit(0);
			throw new Exception();
		}
	}

	public void classify(){
		classifier = new DataClassifier(configuration.getDataset(), classifiers);
	}

	public void saveClassifier() throws Exception{
		try{	
			classifier.getSummary(configuration.getPath_for_result());
		}catch(Exception e){
			try {
				System.out.println("Path not specified or incorrect");
				String path = new java.io.File("").getAbsolutePath();

				//                  ||||||||||||||||||||||||||||||
				// COMMENTS FOR JAR VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
				classifier.getSummary(path.substring(0, path.lastIndexOf("\\"))+"\\result");

				//                    ||||||||||||||||||||||||||||||
				// DECOMMENTS FOR JAR VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
				//classifier.getSummary(path.substring(0, path.lastIndexOf("\\"))+"\\CodeSmellDetectorML"+"\\result");

			} catch (Exception e1) {
				throw new Exception();
			}
		}
	}

	public void printClassifier(){
		System.out.println(classifier.getSummary());
	}

	public void serialize() throws Exception{
		serializer = new Serializer();
		int i = 1;
		String pathToPrint = "";
		for(Classifier c: classifiers){
			String name = classifier.generateNameForFile(c, i);
			try{	
				serializer.serialize(configuration.getPath_for_result() + "\\" + name + ".model", c);
				pathToPrint = configuration.getPath_for_result();
			}catch(Exception e){
				try {
					String path = new java.io.File("").getAbsolutePath();

					//	                ||||||||||||||||||||||||||||||
					// COMMENTS FOR JAR VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
					serializer.serialize(path.substring(0, path.lastIndexOf("\\"))+"\\result" + "\\" + name + ".model", c);
					pathToPrint = path.substring(0, path.lastIndexOf("\\"))+"\\result";

					//		             ||||||||||||||||||||||||||||||
					// DECOMMENTS FOR JAR VVVVVVVVVVVVVVVVVVVVVVVVVVVVVV
					//serializer.serialize(path.substring(0, path.lastIndexOf("\\"))+"\\CodeSmellDetectorML"+"\\result" + "\\" + name + ".model", c);
					//pathToPrint = path.substring(0, path.lastIndexOf("\\"))+"\\CodeSmellDetectorML"+"\\result";

				} catch (Exception e1) {
					System.out.println("ERROR : "+e1.getMessage());
					throw new Exception();
				}
			}
			i++;
		}
		System.out.println("The serialized files were saved in: "+ pathToPrint);
	}

	public void predict(String path) throws Exception{
		configuration = new LoaderProperties();
		ArrayList<String> paths =  configuration.loadForPred(path);
		String	path_dataset =  paths.get(0);
		paths.remove(0);

		for(String s: paths){
			predWithOneClassifier(path_dataset, s);
		}
	}
	
	private void predWithOneClassifier(String path_dataset, String path_serialized) throws Exception{
		Classifier c = null;
		try {
			c = serializer.read(path_serialized);
		}catch(Exception e){
			System.out.println("------------------------------------------------------------------");
			System.out.println("ERROR : the serialized file is incorrect, please check the path ");
			System.out.println("	and make sure that the file is a .model"); 
			System.out.println("------------------------------------------------------------------");
			//System.exit(0);
			throw new Exception();
		}

		DatasetHandler dataset = new DatasetHandler(path_dataset);
		predictor = new Predictor(dataset.getDataset());
		DatasetHandler datasetPredicted = predictor.makePredicitions(c, false);

		String directory = path_dataset.substring(0, path_dataset.lastIndexOf("/")+1);
		String name = path_dataset.substring(path_dataset.lastIndexOf("/") + 1);
		String nameClassifier = c.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf(".")).replace(".", "");
		
		String path = directory + "Predicted_" + nameClassifier + "_" + name;
		
		datasetPredicted.toCSV(path);
	}

	public void predict(String path_1, String path_2) throws Exception{
		String path_dataset = path_1;
		String path_serialized = path_2;
		Classifier c = null;
		try{
			c = serializer.read(path_serialized);
		}catch(Exception e1){
			String temp = path_dataset;
			path_dataset = path_serialized;
			path_serialized = temp;
			try {
				c = serializer.read(path_serialized);
			} catch(Exception e){
				System.out.println("------------------------------------------------------------------");
				System.out.println("ERROR : the serialized file is incorrect, please check the path ");
				System.out.println("	and make sure that the file is a .model"); 
				System.out.println("------------------------------------------------------------------");
				//System.exit(0);
				throw new Exception();
			}
		}
		DatasetHandler dataset = new DatasetHandler(path_dataset);
		predictor = new Predictor(dataset.getDataset());
		DatasetHandler datasetPredicted = predictor.makePredicitions(c, false);

		String directory = path_dataset.substring(0, path_dataset.lastIndexOf("/")+1);
		String name = path_dataset.substring(path_dataset.lastIndexOf("/") + 1);
		String nameClassifier = c.getClass().getName();
		nameClassifier = nameClassifier.substring(nameClassifier.lastIndexOf(".")).replace(".", "");
		
		String path = directory + "Predicted_" + nameClassifier + "_" + name;
		
		datasetPredicted.toCSV(path);
	}
	
	/*public String getDatasetForPred(String path) throws Exception{
		String	path_dataset = "";
		try {
			configuration = new LoaderYaml();
			ArrayList<String> paths =  configuration.loadForPred(path);
			path_dataset = paths.get(0);
		} catch (Exception e) {
			try {
				configuration = new LoaderProperties();
				String[] paths =  configuration.loadForPred(path);
				path_dataset = paths[0];
			} catch (Exception e1) {
				System.out.println("ERROR : Invalid or not found property file, please check the path");
				//System.exit(0);
				throw new Exception();
			}
		}
		return path_dataset;
	}
	
	public String getSerializeForPred(String path) throws Exception{
		String path_serialized = "";
		try {
			configuration = new LoaderYaml();
			ArrayList<String> paths =  configuration.loadForPred(path);
			path_serialized = paths.get(0);			
		} catch (Exception e) {
			try {
				configuration = new LoaderProperties();
				String[] paths =  configuration.loadForPred(path);
				path_serialized = paths[1];
			} catch (Exception e1) {
				System.out.println("ERROR : Invalid or not found property file, please check the path");
				//System.exit(0);
				throw new Exception();
			}
		}
		return path_serialized;
	}*/

	/*
	public void predOneInst(String path){
		// LoadProp
		Properties properties = new Properties();
		properties = configuration.readProperties(path);

		String instance = properties.getProperty("instance");
		String path_serialized = properties.getProperty("serialized");
		String path_dataset = properties.getProperty("dataset");

		Classifier c = null;
		try {
			c = serializer.read(path_serialized);
		}catch(Exception e){
			System.out.println("------------------------------------------------------------------");
			System.out.println("ERROR : the serialized file is incorrect, please check the path ");
			System.out.println("	and make sure that the file is a .model"); 
			System.out.println("------------------------------------------------------------------");
			System.exit(0);
		}

		DatasetHandler dataset = new DatasetHandler(path_dataset);
		predictor = new Predictor(dataset.getDataset());
		String result = predictor.predictOneInstance(instance, c, false);

		System.out.println(result);
	}*/
}
