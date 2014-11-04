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
		
		
		Map<String,Double> idfMap = new HashMap<String, Double>();
		Map<String, Integer> wordMap = new HashMap<String, Integer>();	//词和序号的map
		ArrayList<String> wordArrayList = new ArrayList<String>();
		ArrayList<Integer> numPostPerTheme = new ArrayList<Integer>();
		Map<Integer, Integer> postToThemeMap = new HashMap<Integer, Integer>();	//tfidf矩阵的行对应的主题
		ArrayList<String> postArrayList = new ArrayList<String>();
		ArrayList<HashSet<Integer>> realClusterSets = new ArrayList<HashSet<Integer>>();
		
		for(int i=0;i<10;i++){
			HashSet<Integer> set = new HashSet<Integer>();
			realClusterSets.add(set);
		}
		
		int wordMapIndex=0;		//词的索引结构，最后得到的是词数
		int countPost=0;
		int countTheme=10;
		//int postIndex = 0;
		String str = "啊测试分词工具一些停止词";
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
        
		
		for(int i=0;i<post.length;i++){			//得到一个词-序号的map
			File file = new File(post[i]);
			Scanner input = new Scanner(file);
			int postPerTheme=0;
	        while(input.hasNext()){
	        	postPerTheme++;
	        	postToThemeMap.put(countPost, i);
	        	realClusterSets.get(i).add(countPost);
	        	
	        	countPost++;
	        	str = input.nextLine();
	        	postArrayList.add(str);
	        	
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
	        numPostPerTheme.add(postPerTheme);
	        input.close();
		}
		
		double[][] tfidfMatrix = new double[countPost][wordMapIndex];
		for(int i = 0;i<countPost;i++)
			for(int j = 0;j<wordMapIndex;j++)
				tfidfMatrix[i][j] = 0;
		
		for(int i = 0;i<postArrayList.size();i++){		//得到一个词频数的矩阵。
			String string = postArrayList.get(i);
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
			for(int i = 0;i < countPost;i++){
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
			for(int i=0;i<countPost;i++){
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
			d=Math.log((countPost)/(1+d));
			
			idfMap.put(word, d);
		}
		
		for(int i=0;i<countPost;i++){
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
		
		int remainWordCount = wordMapIndex - deleteWord.size();		//这是删除掉一部分值
		double[][] trainPost = new double[countPost][remainWordCount];
		for(int i = 0;i<countPost;i++){
			int k = 0;
			for(int j = 0;j<wordMapIndex;j++){
				if(tfList[j]>=10){
					trainPost[i][k] = tfidfMatrix[i][j];
					k++;
				}
			}
		}
		
	}
	
	
}
