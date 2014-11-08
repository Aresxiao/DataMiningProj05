import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class DecisionTree {

	int totalPost;
	ArrayList<Integer> attribute;
	TreeNode rootNode;
	ArrayList<Integer> attributeContinuous;
	public DecisionTree(int totalPost,ArrayList<Integer> continuous){
		this.totalPost = totalPost;
		attribute = new ArrayList<Integer>();
		attributeContinuous = (ArrayList<Integer>) continuous.clone();
		
	}
	
	public TreeNode createDT(double[][] trainData,ArrayList<Integer> dataAttributeList,ArrayList<Integer> continuous){
		TreeNode node= new TreeNode();
		//System.out.println("create decision tree,trainData.length="+trainData.length);
		
		if(trainData.length < 40){
			
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
			//System.out.println("attributelis size is not empty");
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
	public TreeNode getRootNode(){
		return rootNode;
	}
	
}
