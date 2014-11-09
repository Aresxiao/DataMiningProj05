import java.lang.reflect.Array;
import java.security.interfaces.DSAKey;
import java.util.ArrayList;
import java.util.List;

import javax.swing.text.StyledEditorKit.ForegroundAction;


public class StackingDecisionTree {

	int N;
	int K;
	int dimensionNum;
	DataSet dataSet;
	ArrayList<DecisionTree> decisionTrees;
	DecisionTree metaTree;
	DecisionTree fullDataTree;
	public StackingDecisionTree(DataSet dataSet){
		N = 1;
		K = 10;
		this.dataSet = dataSet;
		dimensionNum = dataSet.getDimensionNum();
		decisionTrees = new ArrayList<DecisionTree>();
		for(int i = 0;i < N;i++){
			DecisionTree dt = new DecisionTree(dataSet, i+40);
			decisionTrees.add(dt);
		}
		
	}
	
	public void trainStackingDT(double[][] trainData) {
		int len = trainData.length;
		
		ArrayList<ArrayList<Integer>> kGroupsArrayList = new ArrayList<ArrayList<Integer>>();
		for(int i = 0;i < K;i++){
			ArrayList<Integer> list = new ArrayList<Integer>();
			kGroupsArrayList.add(list);
		}
		for(int i = 0;i < len;i++){
			int y = i%K;
			kGroupsArrayList.get(y).add(i);
		}
		
		double[][] metaData = new double[len][N+1];
		int metaDataRowFlag = 0;
		for(int i = 0;i < K;i++){
			int diSizeFlag=0;
			
			int diSize = kGroupsArrayList.get(i).size();		//这一部分把数据分成Di和X-Di,X-Di用来训练决策树，Di用来预测这部分。
			double[][] diData = new double[diSize][dimensionNum];
			for(int k = 0;k < diSize;k++){
				int row = kGroupsArrayList.get(i).get(k);
				for(int x = 0;x < dimensionNum;x++){
					diData[k][x] = trainData[row][x];
				}
			}
			int remainSize = len - diSize;
			double[][] remainData = new double[remainSize][dimensionNum];
			int rowFlag = 0;
			for(int y = 0;y < K;y++){
				if(y != i){
					ArrayList<Integer> list = kGroupsArrayList.get(y);
					for(int k = 0;k < list.size();k++){
						int row = list.get(k);
						for(int x = 0;x < dimensionNum;x++){
							remainData[rowFlag][x] = trainData[row][x];
						}
						rowFlag++;
					}
				}
			}
			
			for(int j = 0;j < N;j++){
				
				
				DecisionTree dt = decisionTrees.get(j);
				System.out.println("i="+i+",开始训练第j="+j+"个树");
				dt.trainDT(remainData, dataSet.getAttributeList(), dataSet.getContinuousArrayList());
				
				for(int y = 0;y < diSize;i++){
					TreeNode treeNode = dt.getRootNode();
					double calTheme = dt.classifyByDT(diData[y], treeNode);
					metaData[rowFlag+y][j] = calTheme;
				}
				diSizeFlag = diSize;
			}
			metaDataRowFlag = metaDataRowFlag + diSizeFlag;
		}
		System.out.println("得到meta data");
		for(int i = 0;i < len;i++){
			metaData[i][N] = trainData[i][dimensionNum-1];
		}
		
		ArrayList<Integer> metaContinuousList = new ArrayList<Integer>();
		ArrayList<Integer> metaDataAttributeIndexList = new ArrayList<Integer>();
		for(int i = 0;i < (N+1);i++){
			metaContinuousList.add(1);
		}
		for(int i = 0;i< N;i++){
			metaDataAttributeIndexList.add(i);
		}
		
		metaTree = new DecisionTree(0,40,metaContinuousList);
		metaTree.trainDT(metaData, metaDataAttributeIndexList, metaContinuousList);
		System.out.println("训练完成meta tree");
		//decisionTrees.clear();
		for(int i = 0;i < N;i++){
			DecisionTree dt = decisionTrees.get(i);
			dt.trainDT(trainData, dataSet.getAttributeList(), dataSet.getContinuousArrayList());
		}
		System.out.println("训练完full data tree");
	}
	
	public double predict(double[] test){
		double[] metaLevelFeature = new double[N+1];
		metaLevelFeature[N] = 0;
		for(int i = 0;i < N;i++){
			DecisionTree dt = decisionTrees.get(i);
			TreeNode treeNode = dt.getRootNode();
			double predict = dt.classifyByDT(test, treeNode);
			metaLevelFeature[i] = predict;
		}
		
		TreeNode treeNode = metaTree.getRootNode();
		
		double realVal = metaTree.classifyByDT(metaLevelFeature, treeNode);
		return realVal;
		
	}
	
	public void tenFoldCrossValidation(){
		double[][] dataMatrix = dataSet.getDataMatrix();
		int totalSmapleNum = dataSet.getTotalSampleNum();
		int dimensionNum = dataSet.getDimensionNum();
		
		ArrayList<Double> accuracyArrayList = new ArrayList<Double>();
		for(int k = 0;k < 10;k++){
			ArrayList<Integer> testPostRowArrayList = new ArrayList<Integer>();
			ArrayList<Integer> trainPostRowArrayList = new ArrayList<Integer>();
			
			for(int i = 0;i < totalSmapleNum;i++){
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
			for(int i = 0;i < trainPostRowArrayList.size();i++){		//到这里之前都是用来划分数据。
				int row = trainPostRowArrayList.get(i);
				for(int j = 0;j < dimensionNum;j++){
					trainData[i][j] = dataMatrix[row][j];
				}
			}
			System.out.println("开始训练k= "+k);
			trainStackingDT(trainData);
			double sum = 0;
			for(int i = 0; i < testSize;i++){
				double theoryTheme = testData[i][dimensionNum-1];
				double predictTheme = predict(testData[i]);
				if(theoryTheme==predictTheme){
					sum++;
				}
			}
			sum = sum/testSize;
			System.out.println("第k = "+k+" 次正确率为: "+sum);
			accuracyArrayList.add(sum);
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
