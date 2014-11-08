import java.io.File;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;


public class DataSet {
	
	double[][] dataMatrix;
	int totalSampleNum;
	int dimensionNum;
	ArrayList<Integer> continuousArrayList;
	ArrayList<Integer> dataAttributeList;
	int flag;		//0用来做分类，1用来做回归。
	
	public DataSet(int flag){
		totalSampleNum = 0;
		continuousArrayList = new ArrayList<>();
		dataAttributeList = new ArrayList<>();
		this.flag = flag;
	}
	
	public void readPost() throws IOException{
		String directory = "data\\";
		String basketball=directory+"Basketball.txt";
		String computer=directory+"D_Computer.txt";
		String fleaMarket = directory+"FleaMarket.txt";
		String girls = directory + "Girls.txt";
		String jobExpress = directory+"JobExpress.txt";
		String mobile = directory + "Mobile.txt";
		String stock = directory + "Stock.txt";
		String suggestion = directory+"V_Suggestions.txt";
		String warAndPeace = directory+"WarAndPeace.txt";
		String WorldFootball = directory + "WorldFootball.txt";
		
		String[] post = {basketball,computer,fleaMarket,girls,jobExpress,mobile,stock,suggestion,
				warAndPeace,WorldFootball};
		
		ArrayList<String> postList = new ArrayList<String>();		//用来存储第一次读文件读到的Post
		Map<Integer, Integer> postToThemeMap = new HashMap<Integer, Integer>();		//	用来存储每一个Post对应到哪分类
		Map<String, Integer> wordMap = new HashMap<String, Integer>();		//	用来统计出现的词
		ArrayList<String> wordArrayList = new ArrayList<String>();			//
		Map<String,Double> idfMap = new HashMap<String, Double>();
		
		int wordMapIndex=0;
		String str;
		for(int i = 0;i < post.length;i++){
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
	        while(input.hasNext()){
	        	postToThemeMap.put(totalSampleNum, i);
	        	
	        	totalSampleNum++;
	        	str = input.nextLine();
	        	postList.add(str);
	        	StringReader reader = new StringReader(str);
	        	IKSegmenter ik = new IKSegmenter(reader,true);
	        	
	        	Lexeme lexeme = null;
	        	while((lexeme = ik.next())!=null){
	        		String word = lexeme.getLexemeText();
	        		
	        		if(!wordMap.containsKey(word)){
	        			wordMap.put(word, wordMapIndex);
	        			
	        			wordArrayList.add(word);
	        			wordMapIndex++;
	        		}
	        	}
	        }
	        input.close();
		}
		
		double[][] tfidfMatrix = new double[totalSampleNum][wordMapIndex];
		for(int i = 0;i<totalSampleNum;i++)
			for(int j = 0;j<wordMapIndex;j++)
				tfidfMatrix[i][j] = 0;
		for(int i = 0;i<postList.size();i++){		//得到一个词频数的矩阵。
			String string = postList.get(i);
			StringReader reader = new StringReader(string);
			IKSegmenter ik = new IKSegmenter(reader, true);
			Lexeme lx = null;
			while((lx = ik.next())!=null){
				String word = lx.getLexemeText();
				int column = wordMap.get(word).intValue();
				tfidfMatrix[i][column] = tfidfMatrix[i][column]+1;
			}
		}
		
		double[] tfList = new double[wordMapIndex];
		for(int j = 0;j < wordMapIndex; j++){
			double sum = 0;
			for(int i = 0;i < totalSampleNum;i++){
				sum += tfidfMatrix[i][j];
			}
			tfList[j] = sum;
		}
		
		ArrayList<Integer> deleteWord = new ArrayList<Integer>();
		for(int j = 0;j<wordMapIndex;j++){
			if(tfList[j]<10)
				deleteWord.add(j);
		}
		
		System.out.println("需要删除的词有 "+deleteWord.size()+" 个");
		
		for(int j=0;j<wordMapIndex;j++){			//得到每个词在多少个帖子中出现过，以用来计算idf的值。
			String word = wordArrayList.get(j);
			if(!idfMap.containsKey(word)){
				idfMap.put(word, 0.0);
			}
			double sum = 0;
			for(int i=0;i<totalSampleNum;i++){
				if(tfidfMatrix[i][j]>0)
					sum = sum+1;
			}
			idfMap.put(word, sum);
		}
		
		Set<String> set = idfMap.keySet();
		
		Iterator<String> iterator = set.iterator();
		while(iterator.hasNext()){		//计算每个词的idf值
			String word = iterator.next();
			
			double d = idfMap.get(word).doubleValue();
			d=Math.log((totalSampleNum)/(1+d));
			
			idfMap.put(word, d);
		}
		
		for(int i=0;i<totalSampleNum;i++){
			double sum = 0;
			for(int j = 0;j<wordMapIndex;j++){
				sum+=tfidfMatrix[i][j];
			}
			for(int j = 0;j<wordMapIndex;j++){
				if(sum != 0)
					tfidfMatrix[i][j] = tfidfMatrix[i][j]/sum;
			}
		}
		for(int i=0;i<tfidfMatrix.length;i++){	//计算出tf*idf
			for(int j=0;j<wordMapIndex;j++){
				double idf = idfMap.get(wordArrayList.get(j));
				tfidfMatrix[i][j] = tfidfMatrix[i][j]*idf;
			}
		}
		
		int remainWordCount = wordMapIndex - deleteWord.size();
		
		dataMatrix = new double[totalSampleNum][remainWordCount+1];		//得到一个降维矩阵
		for(int i = 0;i<totalSampleNum;i++){
			int k = 0;
			for(int j = 0;j<wordMapIndex;j++){
				if(tfList[j]>=10){
					dataMatrix[i][k] = tfidfMatrix[i][j];
				}
			}
		}
		
		for(int j = 0;j < remainWordCount;j++){
			continuousArrayList.add(0);
			dataAttributeList.add(j);
		}
		
		for(int i = 0;i < totalSampleNum;i++){
			int theme = postToThemeMap.get(i);
			dataMatrix[i][remainWordCount] = theme;
		}
		dimensionNum = remainWordCount + 1;
	}
	
	public void readSegmentData() throws IOException{
		String segmentDataString = "data\\segment.data";
		File file = new File(segmentDataString);
		Scanner scanner = new Scanner(file);
		ArrayList<String> segmentDataArrayList = new ArrayList<String>();
		
		while(scanner.hasNext()){
			String str = scanner.nextLine();
			segmentDataArrayList.add(str);
		}
		String str = segmentDataArrayList.get(0);
		String[] attributeString = str.split(",");
		
		for(int i = 0;i < attributeString.length;i++){
			int val = Integer.parseInt(attributeString[i]);
			continuousArrayList.add(val);
			dataAttributeList.add(i);
		}
		dataAttributeList.remove(attributeString.length-1);
		
		totalSampleNum = segmentDataArrayList.size()-1;
		dimensionNum = attributeString.length;
		dataMatrix = new double[totalSampleNum][attributeString.length];
		
		for(int i = 0;i<totalSampleNum;i++){
			str = segmentDataArrayList.get(i+1);
			attributeString = str.split(",");
			for(int j = 0;j < attributeString.length;j++){
				dataMatrix[i][j] = Double.parseDouble(attributeString[j]);
			}
		}
		
	}
	
	public void readBreastCancerData() throws IOException{
		String cancerDataString = "data\\breast-cancer.data";
		File file = new File(cancerDataString);
		Scanner scanner = new Scanner(file);
		ArrayList<String> cancerDataArrayList = new ArrayList<String>();
		while(scanner.hasNext()){
			String string = scanner.nextLine();
			cancerDataArrayList.add(string);
		}
		String string = cancerDataArrayList.get(0);
		String[] attributeStrings = string.split(",");
		for(int i = 0;i < attributeStrings.length;i++){
			int val = Integer.parseInt(attributeStrings[i]);
			continuousArrayList.add(val);
			dataAttributeList.add(i);
		}
		dataAttributeList.remove(attributeStrings.length-1);
		totalSampleNum = cancerDataArrayList.size()-1;
		dimensionNum = attributeStrings.length;
		
		dataMatrix = new double[totalSampleNum][dimensionNum];
		for(int i = 0;i<totalSampleNum;i++){
			string = cancerDataArrayList.get(i+1);
			attributeStrings = string.split(",");
			for(int j = 0;j < dimensionNum;j++){
				dataMatrix[i][j] = Double.parseDouble(attributeStrings[j]);
			}
		}
	}
	
	public void readHousingData()throws IOException{
		String housingDataString = "data\\housing.data";
		File file = new File(housingDataString);
		Scanner scanner = new Scanner(file);
		ArrayList<String> cancerDataArrayList = new ArrayList<String>();
		while(scanner.hasNext()){
			String string = scanner.nextLine();
			cancerDataArrayList.add(string);
		}
		String string = cancerDataArrayList.get(0);
		String[] attributeStrings = string.split(",");
		for(int i = 0;i < attributeStrings.length;i++){
			int val = Integer.parseInt(attributeStrings[i]);
			continuousArrayList.add(val);
			dataAttributeList.add(i);
		}
		dataAttributeList.remove(attributeStrings.length-1);
		totalSampleNum = cancerDataArrayList.size()-1;
		dimensionNum = attributeStrings.length;
		
		dataMatrix = new double[totalSampleNum][dimensionNum];
		for(int i = 0;i<totalSampleNum;i++){
			string = cancerDataArrayList.get(i+1);
			attributeStrings = string.split(",");
			for(int j = 0;j < dimensionNum;j++){
				dataMatrix[i][j] = Double.parseDouble(attributeStrings[j]);
			}
		}
	}
	
	public void readMetaData() throws IOException{
		String housingDataString = "data\\meta.data";
		File file = new File(housingDataString);
		Scanner scanner = new Scanner(file);
		ArrayList<String> cancerDataArrayList = new ArrayList<String>();
		while(scanner.hasNext()){
			String string = scanner.nextLine();
			cancerDataArrayList.add(string);
		}
		String string = cancerDataArrayList.get(0);
		String[] attributeStrings = string.split(",");
		for(int i = 0;i < attributeStrings.length;i++){
			int val = Integer.parseInt(attributeStrings[i]);
			continuousArrayList.add(val);
			dataAttributeList.add(i);
		}
		dataAttributeList.remove(attributeStrings.length-1);
		totalSampleNum = cancerDataArrayList.size()-1;
		dimensionNum = attributeStrings.length;
		
		dataMatrix = new double[totalSampleNum][dimensionNum];
		for(int i = 0;i<totalSampleNum;i++){
			string = cancerDataArrayList.get(i+1);
			attributeStrings = string.split(",");
			for(int j = 0;j < dimensionNum;j++){
				dataMatrix[i][j] = Double.parseDouble(attributeStrings[j]);
			}
		}
	}
	
	public double[][] getDataMatrix(){
		return dataMatrix;
	}
	
	public int getTotalSampleNum(){
		return totalSampleNum;
	}
	
	public ArrayList<Integer> getContinuousArrayList(){
		return continuousArrayList;
	}
	
	public ArrayList<Integer> getAttributeList(){
		return dataAttributeList;
	}
	
	public int getDimensionNum(){
		return dimensionNum;
	}
	
	public int getFlag() {
		return flag;
	}
	
	
}



