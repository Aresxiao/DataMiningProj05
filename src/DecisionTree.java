import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DecisionTree {


	TreeNode rootNode;
	ArrayList<Integer> attributeContinuous;
	int flag;
	int minimumNumLeaf;
	DataSet dataSet;
	
	public DecisionTree(int flag,int mimimumNumLeaf,ArrayList<Integer> continuous){
		this.flag = flag;
		this.minimumNumLeaf = mimimumNumLeaf;
		attributeContinuous = (ArrayList<Integer>) continuous.clone();
	}
	
	public DecisionTree(DataSet ds,int minimumNumLeaf){
		dataSet = ds;
		attributeContinuous = (ArrayList<Integer>) ds.getContinuousArrayList().clone();
		this.flag = ds.getFlag();
		this.minimumNumLeaf = minimumNumLeaf;
	}
	
	public TreeNode createDT(double[][] trainData,ArrayList<Integer> dataAttributeList,ArrayList<Integer> continuous){
		TreeNode node= new TreeNode();
		//System.out.println("create decision tree,trainData.length="+trainData.length);
		if(flag==0){
			if(trainData.length < minimumNumLeaf){
				
				double maxKey = InfoGain.setDataSetClass(InfoGain.getTarget(trainData));
				node.setNodeName("leafNode");
				node.setNodeId(0);
				node.setTartgetValue(maxKey);
				return node;
			}
			double pureVal = InfoGain.isPure(InfoGain.getTarget(trainData));
			if(pureVal!=-1){
				//System.out.println("pureVal!=-1,get a leafNode");
				node.setNodeName("leafNode");
				node.setNodeId(0);
				node.setTartgetValue(pureVal);
				return node;
			}
			if(dataAttributeList.size()==0){
				
				double maxKey = InfoGain.setDataSetClass(InfoGain.getTarget(trainData));
				node.setNodeName("leafNode");
				node.setNodeId(0);
				node.setTartgetValue(maxKey);
				return node;
			}
			else{
				
				double minGain = 10.0;
				int attrIndex = -1;
				InfoGain infoGain = new InfoGain(trainData,dataAttributeList,continuous);
				for(int i = 0;i < dataAttributeList.size();i++){
					double tempGain = infoGain.giniIndex(dataAttributeList.get(i));
					
					if(minGain > tempGain){
						minGain = tempGain;
						attrIndex = i;
					}
				}
				if(attrIndex==-1){
					
					double maxKey = InfoGain.setDataSetClass(InfoGain.getTarget(trainData));
					node.setNodeName("leafNode");
					node.setNodeId(0);
					node.setTartgetValue(maxKey);
					return node;
				}
				node.setAttributeValue(dataAttributeList.get(attrIndex));
				node.setSplitVal(infoGain.getSplit(dataAttributeList.get(attrIndex)));
				node.setContinuous(continuous.get(dataAttributeList.get(attrIndex)));
				
				infoGain.splitData(dataAttributeList.get(attrIndex));
				double[][] leftData = infoGain.getLeftData();
				double[][] rightData = infoGain.getRightData();
				
				ArrayList<Integer> leftAttributeList = new ArrayList<>();
				
				ArrayList<Integer> rightAttributeList = new ArrayList<>();
				for(int i = 0;i < dataAttributeList.size();i++){
					if(i != attrIndex){
						leftAttributeList.add(dataAttributeList.get(i));
						rightAttributeList.add(dataAttributeList.get(i));
					}
				}
				
				//System.out.println("before recursion");
				TreeNode leftNode = createDT(leftData, leftAttributeList, continuous);
				TreeNode rightNode = createDT(rightData, rightAttributeList, continuous);
				node.getChildTreeNodes().add(leftNode);
				node.getChildTreeNodes().add(rightNode);
				node.setNodeId(1);
			}
		}
		else {
			if(trainData.length < minimumNumLeaf){
				double val = InfoGain.setDataSetMeanValue(InfoGain.getTarget(trainData));
				node.setNodeId(0);
				node.setTartgetValue(val);
				return node;
			}
			if(dataAttributeList.size() == 0){
				double val = InfoGain.setDataSetMeanValue(InfoGain.getTarget(trainData));
				node.setNodeId(0);
				node.setTartgetValue(val);
				return node;
			}
			else {
				double minVariance =100000;
				int attrIndex = -1;
				InfoGain infoGain = new InfoGain(trainData,dataAttributeList,continuous);
				for(int i = 0;i < dataAttributeList.size();i++){
					double tempVariance = infoGain.calMinVariance(dataAttributeList.get(i));
					
					if(minVariance > tempVariance){
						minVariance = tempVariance;
						attrIndex = i;
					}
				}
				if(attrIndex==-1){
					
					double val = InfoGain.setDataSetMeanValue(InfoGain.getTarget(trainData));
					node.setNodeName("leafNode");
					node.setNodeId(0);
					node.setTartgetValue(val);
					return node;
				}
				node.setAttributeValue(dataAttributeList.get(attrIndex));
				node.setSplitVal(infoGain.getSplit(dataAttributeList.get(attrIndex)));
				node.setContinuous(continuous.get(dataAttributeList.get(attrIndex)));
				
				infoGain.splitData(dataAttributeList.get(attrIndex));
				double[][] leftData = infoGain.getLeftData();
				double[][] rightData = infoGain.getRightData();
				
				ArrayList<Integer> leftAttributeList = new ArrayList<>();
				
				ArrayList<Integer> rightAttributeList = new ArrayList<>();
				for(int i = 0;i < dataAttributeList.size();i++){
					if(i != attrIndex){
						leftAttributeList.add(dataAttributeList.get(i));
						rightAttributeList.add(dataAttributeList.get(i));
					}
				}
				
				//System.out.println("before recursion");
				TreeNode leftNode = createDT(leftData, leftAttributeList, continuous);
				TreeNode rightNode = createDT(rightData, rightAttributeList, continuous);
				node.getChildTreeNodes().add(leftNode);
				node.getChildTreeNodes().add(rightNode);
				node.setNodeId(1);
			}
		}
		return node;
	}
	
	public void trainDT(double[][] trainData,ArrayList<Integer> dataAttributeList,ArrayList<Integer> continuous){
		//System.out.println("trainDT size = "+dataAttributeList.size());
		rootNode = createDT(trainData, dataAttributeList, continuous);
	}
	
	public double classifyByDT(double[] testData,TreeNode node){
		
		if(node.getId()==0){
			return node.getTargetValue();
		}
		else{
			int attrIndex = node.getAttributeValue();
			double splitVal = node.getSplitVal();
			
			if(attributeContinuous.get(attrIndex)==0){
				if(testData[attrIndex]<splitVal){
					node = node.getChildTreeNodes().get(0);
					
				}
				else {
					node = node.getChildTreeNodes().get(1);
				}
				return classifyByDT(testData, node);
			}
			else {
				if(testData[attrIndex] == splitVal){
					node = node.getChildTreeNodes().get(0);
				}
				else {
					node = node.getChildTreeNodes().get(1);
				}
				return classifyByDT(testData, node);
			}
		}
	}
	
	public double regressionByDT(double[] testData,TreeNode node){
		if(node.getId()==0){
			return node.getTargetValue();
		}
		else {
			int attrIndex = node.getAttributeValue();
			double splitVal = node.getSplitVal();
			if(attributeContinuous.get(attrIndex)==0){
				if(testData[attrIndex] < splitVal){
					node = node.getChildTreeNodes().get(0);
				}
				else {
					node = node.getChildTreeNodes().get(1);
				}
				return regressionByDT(testData, node);
			}
			else {
				if(testData[attrIndex] == splitVal){
					node = node.getChildTreeNodes().get(0);
				}
				else {
					node = node.getChildTreeNodes().get(1);
				}
				return regressionByDT(testData, node);
			}
		}
	}
	
	public TreeNode getRootNode(){
		return rootNode;
	}
	
	public void tenFoldCrossValidation(){
		double[][] dataMatrix = dataSet.getDataMatrix();
		int totalPostNum = dataSet.getTotalSampleNum();
		int dimensionNum = dataSet.getDimensionNum();
		ArrayList<Integer> continuousList = dataSet.getContinuousArrayList();
		ArrayList<Integer> dataAttributeIndex = dataSet.getAttributeList();
		ArrayList<Double> accuracyArrayList = new ArrayList<Double>();		//用来存储准确率
		
		for(int k = 0; k < 10;k++){
			ArrayList<Integer> testPostRowArrayList = new ArrayList<Integer>();
			ArrayList<Integer> trainPostRowArrayList = new ArrayList<Integer>();
			
			for(int i = 0;i < totalPostNum;i++){
				if(i%10==k){
					testPostRowArrayList.add(i);
				}
				else {
					trainPostRowArrayList.add(i);
				}
			}
			
			int testSize = testPostRowArrayList.size();
			int trainSize = trainPostRowArrayList.size();
			
			double[][] testData = new double[testSize][dimensionNum];
			double[][] trainData = new double[trainSize][dimensionNum];
			
			for(int i = 0;i < testPostRowArrayList.size();i++){
				int row = testPostRowArrayList.get(i);
				for(int j = 0;j<dimensionNum;j++){
					testData[i][j] = dataMatrix[row][j];
				}
			}
			for(int i = 0;i < trainPostRowArrayList.size();i++){
				int row = trainPostRowArrayList.get(i);
				for(int j = 0;j < dimensionNum;j++){
					trainData[i][j] = dataMatrix[row][j];
				}
			}
			
			//DecisionTree dt = new DecisionTree(totalPostNum, continuousList,dataSet.getFlag(),40);
			trainDT(trainData, dataAttributeIndex, continuousList);
			if(dataSet.getFlag()==0){
				double sum = 0;
				for(int i = 0;i < testSize;i++){
					double theoryTheme = testData[i][dimensionNum-1];
					TreeNode treeNode = getRootNode();
					double realTheme = classifyByDT(testData[i], treeNode);
					if(theoryTheme == realTheme)
						sum++;
				}
				
				sum = sum/testSize;
				accuracyArrayList.add(sum);
				System.out.println("第k = "+k+" 次正确率为: "+sum);
			}
			else {
				double sum = 0;
				for(int i = 0;i < testSize;i++){
					double theoryValue = testData[i][dimensionNum-1];
					TreeNode treeNode = getRootNode();
					double realValue = regressionByDT(testData[i], treeNode);
					sum += (theoryValue - realValue)*(theoryValue - realValue);
				}
				sum = sum/testSize;
				sum = Math.sqrt(sum);
				accuracyArrayList.add(sum);
				System.out.println("第k = "+k+" 次MSE为: "+sum);
			}
		}

		double correctSum = 0;
		double averageRatio = 0;
		double variance = 0;
		for(int i = 0;i < accuracyArrayList.size();i++){
			correctSum += accuracyArrayList.get(i);
		}
		averageRatio = correctSum/accuracyArrayList.size();
		for(int i = 0;i < accuracyArrayList.size();i++){
			double d = accuracyArrayList.get(i);
			variance+=(d-averageRatio)*(d-averageRatio);
		}
		variance = variance/accuracyArrayList.size();
		System.out.println("平均准确率为: "+averageRatio+",方差为："+variance);
		
	}
}
