import java.util.ArrayList;

public class DecisionTree {

	int totalPost;
	ArrayList<Integer> attribute;
	public DecisionTree(int totalPost){
		this.totalPost = totalPost;
		attribute = new ArrayList<Integer>();
	}
	
	public TreeNode createDT(double[][] trainData,ArrayList<Integer> dataAttributeList,ArrayList<Integer> continuous){
		TreeNode node= new TreeNode();
		
		double pureVal = InfoGain.isPure(InfoGain.getTarget(trainData));
		
		if(pureVal!=-1){
			node.setNodeName("leafNode");
			node.setTartgetValue(pureVal);
			return node;
		}
		if(dataAttributeList.size()==0){
			node.setTartgetValue(pureVal);
			return node;
		}
		else{
			double minGain = 1.0;
			int attrIndex = -1;
			InfoGain infoGain = new InfoGain(trainData,dataAttributeList,continuous);
			for(int i = 0;i < dataAttributeList.size();i++){
				double tempGain = infoGain.giniIndex(dataAttributeList.get(i));
				if(minGain > tempGain){
					minGain = tempGain;
					attrIndex = dataAttributeList.get(i);
				}
			}
			
			node.setAttributeValue(dataAttributeList.get(attrIndex));
			node.setSplitVal(infoGain.getSplit(dataAttributeList.get(attrIndex)));
			node.setContinuous(continuous.get(dataAttributeList.get(attrIndex)));
			
			infoGain.splitData(dataAttributeList.get(attrIndex));
			double[][] leftData = infoGain.getLeftData();
			double[][] rightData = infoGain.getRightData();
			
			ArrayList<Integer> leftAttributeList = (ArrayList<Integer>) dataAttributeList.clone();
			ArrayList<Integer> rightAttributeList = (ArrayList<Integer>) dataAttributeList.clone();
			leftAttributeList.remove(attrIndex);
			rightAttributeList.remove(attrIndex);
			
			TreeNode leftNode = createDT(leftData, leftAttributeList, continuous);
			TreeNode rightNode = createDT(rightData, rightAttributeList, continuous);
			node.getChildTreeNodes().add(leftNode);
			node.getChildTreeNodes().add(rightNode);
		}
		return node;
	}
	
}
