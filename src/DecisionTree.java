import java.util.ArrayList;
import java.util.HashMap;


public class DecisionTree {

	int totalPost;
	ArrayList<Integer> attribute;
	public DecisionTree(int totalPost){
		this.totalPost = totalPost;
		attribute = new ArrayList<Integer>();
	}
	
	public TreeNode createDT(double[][] trainPost,ArrayList<Integer> attributeList,
			HashMap<Integer, Integer> postToThemeMap,ArrayList<Integer> continuous){
		TreeNode node  = new TreeNode();
		InfoGain infoObject = new InfoGain();
		
		int pureRet = infoObject.isPure(postToThemeMap);
		if(pureRet!=-1){
			node.setNodeName("leafNode");
			node.setAttributeValue(pureRet);
			return node;
		}
		if(attributeList.size()==0){
			node.setAttributeValue(pureRet);
			return node;
		}
		else {
			double minGain = 0.0;
			int attrIndex = -1;
			
		}
		
		return node;
	}
}
