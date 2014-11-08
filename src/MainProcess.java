import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


public class MainProcess {
public static void main(String[] args) throws IOException{
		
		DataSet dataSet = new DataSet();
		dataSet.readSegmentData();
		
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
			System.out.println("testSize="+testSize+",trainSize="+trainSize+",totalSampleNum="+totalPostNum+",dimensionNum="+dimensionNum);
			System.out.println("continuous.size="+continuousList.size()+"attributeIndex.size="+dataAttributeIndex.size());
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
			
			DecisionTree dt = new DecisionTree(totalPostNum, continuousList);
			//System.out.println("开始训练");
			dt.trainDT(trainData, dataAttributeIndex, continuousList);
			
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
