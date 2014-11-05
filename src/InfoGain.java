import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


public class InfoGain {
	
	double[][] data;
	ArrayList<Integer> attributeList;
	HashMap<Integer, Integer> postToThemeMap;
	ArrayList<Integer> continuousArrayList;
	HashMap<Integer, Double> splitValMap;
	public InfoGain(){
		
	}
	
	public InfoGain(double[][] data,ArrayList<Integer> attributeList,
			HashMap<Integer, Integer> postToThemeMap,ArrayList<Integer> continuous){
		this.data = new double[data.length][data[0].length];
		for(int i = 0;i<this.data.length;i++){
			for(int j = 0;j<this.data[0].length;j++)
				this.data[i][j] = data[i][j];
		}
		this.attributeList = (ArrayList<Integer>) attributeList.clone();
		this.postToThemeMap = (HashMap<Integer, Integer>) postToThemeMap.clone();
		this.continuousArrayList = (ArrayList<Integer>) continuous.clone();
		splitValMap = new HashMap<Integer, Double>();
	}
	
	public int isPure(HashMap<Integer, Integer> map){
		Set<Integer> set = new HashSet<Integer>();
		Iterator<Integer> iterator = map.values().iterator();
		while(iterator.hasNext()){
			set.add(iterator.next());
		}
		if(set.size()>1) return -1;
		
		iterator = set.iterator();
		return iterator.next();
	}
	
	public double giniIndex(int i){
		double gini = 0;
		if(continuousArrayList.get(i)==0){			//对于连续属性，连续属性对属性的值空间排序，然后找出最小GINI值的分裂点
			double[] tempAttriValue = new double[data.length];
			for(int x = 0;x<tempAttriValue.length;x++){
				tempAttriValue[x] = data[x][i];
			}
			for(int x = 0;x<tempAttriValue.length;x++){
				int min = x;
				for(int j = x;j<tempAttriValue.length;j++){
					if(tempAttriValue[min]>tempAttriValue[j])
						min = j;
				}
				if(min!=x){
					double t = tempAttriValue[min];
					tempAttriValue[min] = tempAttriValue[x];
					tempAttriValue[x] = t;
				}
			}
			
			double minGini=-1;
			double splitVal=0;
			for(int x = 0;x<(tempAttriValue.length-1);x++){
				double mid = (tempAttriValue[x]+tempAttriValue[x+1])/2.0;
				HashMap<Integer, Integer> lessSet = new HashMap<Integer,Integer>();
				HashMap<Integer, Integer> biggerSet = new HashMap<Integer,Integer>();
				for(int y = 0;y<data.length;y++){
					int theme = postToThemeMap.get(y);
					if(data[y][i]>mid){
						biggerSet.put(y, theme);
					}
					else{
						lessSet.put(y, theme);
					}
				}
				double biggerSetSize = biggerSet.size();
				double lessSetSize = lessSet.size();
				
				double giniGain = (biggerSetSize/(biggerSetSize+lessSetSize))*getGini(biggerSet)+
						(lessSetSize/(lessSetSize+biggerSetSize))*getGini(lessSet);
				if(minGini == -1){
					minGini=giniGain;
					splitVal = mid;
				}
				else {
					if(minGini>giniGain){
						minGini = giniGain;
						splitVal = mid;
					}
				}
			}
			gini = minGini;
			splitValMap.put(i, splitVal);
		}
		else{			//对于离散属性，把所有属性值都作为一次分裂尝试，找到最佳的分裂方式
			
			HashSet<Double> tempAttriValue = new HashSet<Double>();
			for(int x = 0;x<data.length;x++){
				tempAttriValue.add(data[x][i]);
			}
			Iterator<Double> iterator = tempAttriValue.iterator();
			double miniGini = -1;
			double splitVal=0;
			while(iterator.hasNext()){
				double attriVal = iterator.next();
				
				HashMap<Integer, Integer> isThisAttributeValHashMap = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> notThisAttributeValHashMap = new HashMap<Integer, Integer>();
				
				for(int x = 0;x<data.length;x++){
					int theme = postToThemeMap.get(x);
					
					if(data[x][i]==attriVal){
						isThisAttributeValHashMap.put(x, theme);
					}
					else {
						notThisAttributeValHashMap.put(x, theme);
					}
				}
				
				double isSize = isThisAttributeValHashMap.size();
				double notSize = notThisAttributeValHashMap.size();
				
				double giniGain = (isSize/(isSize+notSize))*getGini(isThisAttributeValHashMap)+
						(notSize/(isSize+notSize))*getGini(notThisAttributeValHashMap);
				
				if(miniGini ==-1){
					miniGini = giniGain;
					splitVal = attriVal;
				}
				else {
					if(miniGini>giniGain){
						miniGini = giniGain;
						splitVal = attriVal;
					}
				}
			}
			gini = miniGini;
			splitValMap.put(i, splitVal);
		}
		return gini;
	}
	
	
	public double getGini(HashMap<Integer, Integer> map){
		double size = map.size();
		HashMap<Integer, Double> themeNumMap = new HashMap<Integer, Double>();
		Iterator iterator = map.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<Integer, Integer> entry = (Entry<Integer, Integer>) iterator.next();
			int post = entry.getKey();
			int val = entry.getValue();
			if(themeNumMap.containsKey(val)){
				double d = themeNumMap.get(val);
				themeNumMap.put(val, d+1.0);
			}
			else{
				themeNumMap.put(val, 1.0);
			}
		}
		double sum = 0;
		iterator = themeNumMap.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<Integer, Double> entry = (Entry<Integer, Double>) iterator.next();
			double d = entry.getValue();
			sum+=(d/size)*(d/size);
		}
		sum = 1-sum;
		
		return sum;
	}
	
	public double getSplit(int i){
		return splitValMap.get(i);
	}
	
	public double[][] getLeftData(double[][] data){
		double[][] ret = new double[data.length][data[0].length];
		return null;
	}
	
	public double[][] getRightData(double[][] data){
		
	}
}
