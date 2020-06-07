

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import static java.util.stream.Collectors.toMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;

import sorting.Sort;
import textprocessing.In;
import textprocessing.TST;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/SearchServlet")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    // This method removes stop words such as I, me, myself etc from the input string.
    
    
    public static String[] getKeywords(String inputStr) throws IOException 
	{
    	
    	BufferedReader br = new BufferedReader(new FileReader("C:\\\\Users\\\\mani\\\\Downloads\\\\stop-texts.txt"));   	
		int i = 0;	
		inputStr = inputStr.toLowerCase();
		String str="";
		while( (str = br.readLine()) != null )
		{
			str = str.toLowerCase();
			str = "\\b"+str+"\\b";
			inputStr = inputStr.replaceAll(str,"");;
		}
		
		System.out.println(inputStr);
		
		StringTokenizer st  = new StringTokenizer(inputStr, " ");
		String[] keyWords = new String[st.countTokens()];
		
		while (st.hasMoreTokens()) 
		{ 
			keyWords[i]=st.nextToken();
			i++;
        }
		return keyWords;
	}
    
    
    /* INDEXING IN HASHMAP. This method indexed the urls from the file websites.txt into 
       a hashmap and returns the hashmap with indexed urls. 	*/
     
    
    public static HashMap<Integer,String> indexURLS() throws IOException 
	{	
		int i = 0;
		HashMap<Integer,String> UrlIndex = new HashMap<Integer,String>();
		
		BufferedReader br = new BufferedReader(new FileReader("C:\\\\Users\\\\mani\\\\Downloads\\\\websites.txt"));
		String str;
		while( (str = br.readLine()) != null )
		{
			UrlIndex.put(i,str);
        	i++;
		}
		System.out.println(UrlIndex);
		return UrlIndex;
	}
    
    
    // METHOD FOR GENERATING TST
    // This method generates the TST for each file in the folder 'urls'.
    
    public static TST<Integer> getTST(String finalPath) 
	{	
		int j = 0;
		TST<Integer> tst = new TST<Integer>();	
		In in = new In(finalPath);
		
        while (!in.isEmpty()) 
        {
        	String text = in.readLine();
	        if (j == 0) {
	            	 
	        	j = 1;
	            continue;
	            	 
	        } else if (j == 1) {  
	        	j = 0; 
	        	
	        	StringTokenizer st  = new StringTokenizer(text, " ");
	        	while (st.hasMoreTokens()) { 
	    			
	    			String word = st.nextToken();
	    			word = word.toLowerCase();
	    			//System.out.println(word);
	    			
	        		if(tst.contains(word)) {
	        			
	        			tst.put(word, tst.get(word)+1);
	        			//System.out.println("true");
	        			
	        		} else {
	        			
	        			tst.put(word, 1);
	        		}
	            }
	        }	
        }
        
		return tst;
	}
    
    
    
    /*
	   This method is responsible to find the the occurrence of the keywords in each text file
	   and get the count
	 */
	public static HashMap<Integer, Integer> getFreqList(String[] keyWords){
		
		
		//Map each text file to its corresponding number into an arraylist
		ArrayList<String> textList = new ArrayList<>();
		HashMap<Integer,Integer > freqList = new HashMap<Integer, Integer>();
	       
		File folder = new File("C:\\Users\\mani\\Desktop\\urls"); 
        File[] files = folder.listFiles();
 
        for (File file : files)
        {
        	
            String myURL = file.getName();
            //myURL = myURL.substring(0, (myURL.length()-4));
            textList.add(myURL);
        	
        }
        
        for (int i = 0 ; i < textList.size() ; i++) {
        	
	        String filePath = "C:\\Users\\mani\\Desktop\\urls\\";
	        String fileName = textList.get(i);
	        String finalPath = filePath+fileName;
	        
	        //System.out.println(finalPath);
	        
	        String tempFileIndex = fileName.substring(0, (fileName.length()-4));
        	int fileIndex = Integer.parseInt(tempFileIndex);
			//System.out.println(fileIndex); 
			
	        TST<Integer> tst = new TST<Integer>();
	        tst = getTST(finalPath);
	        
	        int counter = 0;
	        
	        for (String str: keyWords) {	        	
	        	if (tst.contains(str)){
	
	        		int count = tst.get(str);
	        		//System.out.println(str+" "+count);
	        		counter = counter + count;        		
	        	}
	        }
	       
	        freqList.put(fileIndex, counter);
        }  
        
        //System.out.println(freqList);
		return freqList;
	}

	
	/**
	 * This method is responsible to sort hashmap in descending order based on the values
	 * @param freqList
	 * @return
	 */
	public static HashMap<Integer, Integer> sortHashMap(HashMap<Integer,Integer> freqList)
	{	
		  HashMap<Integer, Integer> sortedFreqList = freqList.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue())).collect(toMap(e -> e.getKey(), e -> e.getValue(), (e1, e2) -> e2,LinkedHashMap::new));
		  return sortedFreqList;		
	}
	
	
	/*
	  This method is used to store the frequency list hashmap used for Page Ranking
	 */
	public static void storeHashMap(HashMap<Integer, Integer> freqList, String[] keyWords) {

		Sort.mergeSort(keyWords);
		String fileName = "";

		for (String str : keyWords) {

			fileName = fileName + str + "_";
		}

		fileName = fileName + ".dat";

		// System.out.println(fileName);

		String filePath = "C:\\Users\\mani\\Desktop\\hashmap_data\\";
		String finalPath = filePath + fileName;

		try {

			FileOutputStream fileOut = new FileOutputStream(finalPath);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(freqList);
			out.close();
			fileOut.close();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}
	
	
	/*
	 This method is used to retrieve the frequency list hashmap used for Page Ranking
	 */
	public static HashMap<Integer,Integer> retreiveHashMap(String[] keyWords) {
		
		Sort.mergeSort(keyWords);

		String fileName = "";

		for (String str : keyWords) {

			fileName = fileName + str + "_";
		}

		fileName = fileName + ".dat";
		String filePath = "C:\\Users\\mani\\Desktop\\hashmap_data\\";
		String finalPath = filePath + fileName;
		
		  HashMap<Integer,Integer> freqList = new HashMap<Integer,Integer>();
		  freqList = null;
		  
		  try{
			  
			  FileInputStream fileIn = new FileInputStream(finalPath);
			  ObjectInputStream in = new ObjectInputStream(fileIn);
			  freqList = ((HashMap<Integer, Integer>)in.readObject());
			  in.close();
			  fileIn.close();
			  
		  } catch (Exception e){
			  
		  e.printStackTrace();
		  }
	
		  return freqList;
		
	}
    

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
		
		ArrayList<String> urlList = new ArrayList<>();
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		String name = request.getParameter("filename");
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\mani\\Downloads\\websites.txt"));
		String str;int j=0;
		while( (str = br.readLine()) != null )
		{
		 urlList.add(str);
		 j++;
		}
		
		
		// CODE FOR GENERATING URLS FOLDER ON DESKTOP. JSOUP HAS BEEN USED HERE IN THIS CODE.
		
		
for(int i = 0; i < urlList.size(); i++) {
        	
        	try {
        		
        		org.jsoup.nodes.Document doc = Jsoup.connect(urlList.get(i)).get();
        		String text = doc.text();
            	String FilePath = "C:\\Users\\mani\\Desktop\\urls\\" + (i)+".txt" ;
            	PrintWriter out = new PrintWriter(FilePath);
        		out.println(urlList.get(i));
            	out.println(text);
        		pw.println(urlList.get(i));
        		out.close();
        		
        		
        	}catch(Exception e){
        		
        		pw.println("Exception, Cannot be converted to text: "+ urlList.get(i));
        	}
        	
        
        }
		
		

		String input = request.getParameter("search_text");
		pw.println(input);
		
		// STEP 1
		String search_keywords[] = getKeywords(input);
		for(String a : search_keywords)
		{
			pw.println(a);
		}
		
		
		String fileName = "";
		for (String a : search_keywords) {

			fileName = fileName + a + "_";
		}

		fileName = fileName + ".dat";
		
		boolean fileExist = false;
		
		File folder = new File("C:\\Users\\mani\\Desktop\\hashmap_data"); 
        File[] files = folder.listFiles();
        
        for (File file : files) {

			String myFileName = file.getName();

			if (myFileName.compareTo(fileName) == 0) {

				fileExist = true;
				break;

			}

		}
        
if (fileExist == true){
			
			HashMap<Integer,String> urlIndex = new HashMap<Integer, String>();
			urlIndex = indexURLS();
			
			HashMap<Integer,Integer> freqList = new HashMap<Integer,Integer>();
			freqList = retreiveHashMap(search_keywords);
			
			System.out.println("Top 10 Search Results for \""+ input +"\" are:\n");
			String desc = "Top 10 Search Results for \""+ input +"\" are:\n";
			request.setAttribute("description", desc);
			int x = 0;
			for (HashMap.Entry<Integer, Integer> entry : freqList.entrySet()) {
				
				if (x < 10) {
					
					//System.out.println(entry.getKey() + " = " + entry.getValue());
					int urlKey = entry.getKey();
					System.out.println(urlIndex.get(urlKey)+"\n");
					request.setAttribute("website"+(x+1), urlIndex.get(urlKey));
					x++;
					
				} else {
					
					break;
				}
			}
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
				
		} else if (fileExist == false) {
			
			HashMap<Integer,String> urlIndex = new HashMap<Integer, String>();
			urlIndex = indexURLS();
			
			HashMap<Integer,Integer> freqList = new HashMap<Integer,Integer>();
			freqList = getFreqList(search_keywords);
			
			freqList = sortHashMap(freqList);
			
			storeHashMap(freqList, search_keywords);
					
			System.out.println("Top 10 Search Results for \""+ input +"\" are:\n");
			int x = 0;
			
			String desc = "Top 10 Search Results for \""+ input +"\" are:\n";
			request.setAttribute("description", desc);
			
			for (HashMap.Entry<Integer, Integer> entry : freqList.entrySet()) {
				
				if (x < 10) {
					
					//System.out.println(entry.getKey() + " = " + entry.getValue());
					int urlKey = entry.getKey();
					System.out.println(urlIndex.get(urlKey)+"\n");
					request.setAttribute("website"+(x+1), urlIndex.get(urlKey));
					x++;
					
				} else {
					
					break;
				}
			}	
			RequestDispatcher rd = request.getRequestDispatcher("index.jsp");
			rd.forward(request, response);
			
		}	      
               
        br.close();
		pw.close();
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
