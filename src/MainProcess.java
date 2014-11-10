import java.io.IOException;
import java.util.ArrayList;


public class MainProcess {
	
	public static void main(String[] args) throws IOException{
		/*
		DataSet dataSet = new DataSet(0);
		dataSet.readBreastCancerData();
		System.out.println("读完数据");
		
		*/
		
		
		
		
		DataSet dataSet = new DataSet();
		dataSet.readMetaData();
		System.out.println("读完数据");
		DecisionTree dt = new DecisionTree(dataSet, 40);
		dt.tenFoldCrossValidation();
		//StackingDecisionTree sdt = new StackingDecisionTree(dataSet);
		//sdt.tenFoldCrossValidation();
		
		/*
		double[][] dataMatrix = dataSet.getDataMatrix();
		int totalPostNum = dataSet.getTotalSampleNum();
		int dimensionNum = dataSet.getDimensionNum();
		ArrayList<Integer> continuousList = dataSet.getContinuousArrayList();
		ArrayList<Integer> dataAttributeIndex = dataSet.getAttributeList();
		ArrayList<Double> accuracyArrayList = new ArrayList<Double>();		//用来存储准确率
		System.out.println("开始交叉验证");
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
			
			DecisionTree dt = new DecisionTree(dataSet, 40);
			dt.trainDT(trainData, dataAttributeIndex, continuousList);
			if(dataSet.getFlag()==0){
				double sum = 0;
				for(int i = 0;i < testSize;i++){
					double theoryTheme = testData[i][dimensionNum-1];
					TreeNode treeNode = dt.getRootNode();
					double realTheme = dt.classifyByDT(testData[i], treeNode);
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
					TreeNode treeNode = dt.getRootNode();
					double realValue = dt.regressionByDT(testData[i], treeNode);
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
		*/
	}
	
}
