/**
 * Title : Challenge for Test
 * Author : Dong-Ik Lee
 * Date : July 25, 2016
 */
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.JSONObject;

/**
 * Challege Class for showing menu, result, error, etc and getting the found result
 * @author DONG IK LEE
 *
 */
public class Challenge {
	
	public static Map<String, Object> products  = new LinkedHashMap<String, Object>();
	public static ArrayList<String> listings = new ArrayList<String>();
	
	public static void main(String[] args){
		
		String command = "";
		String productName = "";
		String productNumber = "";
		
		Scanner sc = new Scanner(System.in);
		
		printStart();
        printMenu();
        
        while(sc.hasNextLine()){
        	
        	command = sc.nextLine().toString();
        	
        	if(command==null || command.trim().equals("")){
        		printMenu();
        	}else if(command.trim().equalsIgnoreCase("QUIT")){
        		break;
        	//if command is numeric, then find list in listing found
        	}else if(isNumeric(command)){
    			if(command.length() != 4){
    				printError();
    			}else{
    				productNumber = command;
					
    				try {
    					//To find list from listings.txt
						findListing(productNumber);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
        	}else{
    			if(command.length()<3){
    				System.out.println("\n- Please, put the word more than 2 letters");
    			}else{
    				productName = command;

    				try {
    					//To find list from products.txt
						findProduct(productName);
						if(products.size() > 0) System.out.println("\n- If you want to find the product list related to product above, just put the number on left side"); 
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}
        	}
        }
        
        printFinish();
	}
	
	/**
	 * To show the product name found 
	 * @param productName
	 * @throws IOException
	 */
	public static void findProduct(String productName)throws IOException{
		//To find the product list contain product name from user's input
		products = DataFinder.getProductList(productName);
		
		if(products.size() < 1){
			System.out.println("\n\n- There is no matching product with \""+productName+"\"");
			printMenu();
		}else{
			
			printLine();
			for (Map.Entry<String, Object> entry : products.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				System.out.println(key + " : "+ value);
			}
			printLine();
			System.out.println("\n- Found "+products.size()+" matching products with \""+productName+"\"");
		}
	}
	
	/**
	 * To show the list name found
	 * @param productNumber
	 * @throws IOException
	 */
	public static void findListing(String productNumber)throws IOException{
		//to try to matching from Hashmap 
		String productName = (String) products.get(productNumber);
		
		if(productName!=null){
			System.out.println("\n- Your selected product name : \""+productName+"\"");
			
			//To find the list contain the product name from user's input
			listings = DataFinder.getListingsList(productName);
			
			if(listings.size() < 1){
				System.out.println("\n- There is no matching product list with \""+productName+"\"");
				System.out.println("- Try again!");
			}else{
				printLine();
				for(String list : listings){
					System.out.println(list);
				}
				printLine();
				System.out.println("\n- Found "+listings.size()+" matching lists related to \""+productName+"\"");
				
				DataFinder.makeResultFile(productName, listings);
			}
		}else{
			printError();
		}
	}
	
	
	public static void printMenu(){
		System.out.println("\n- If you want to quit this program, just type in 'quit'");
        System.out.println("- If you want to find the product, please put the product name in :");
	}
	
	public static void printLine(){
		System.out.println("-----------------------------------------------------------------");
	}
	
	public static void printError(){
		System.out.println("\n- Error! put the product name or the number on the left of the product list found");
	}	
	
	public static void printStart(){
        System.out.println("===================================================================");
        System.out.println("============    THE SEARCH PROGRAM OF THE PRODUCT    ==============");
        System.out.println("===================================================================");
	}
	
	public static void printFinish(){
        System.out.println("==================================================================");
        System.out.println("=================   THANK YOU FOR TESTING   ======================");
        System.out.println("==================================================================");
	}
	
	public static boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  
	}	
	
}	

/**
 * To find the mapping data out of products.txt and listings.txt
 * @author DONG IK LEE
 *
 */
class DataFinder {
	
	static private String PRODUCT_FILE = "./products.txt";
	static private String LISTINGS_FILE = "./listings.txt";
	static private String RESULT_FILE = "./result.txt";
	
	public DataFinder(){}

	/**
	 * To find the product name out of products.txt
	 * @param productName
	 * @return
	 * @throws IOException
	 */
	public static Map<String, Object> getProductList(String productName) throws IOException {
		Map<String, Object> findProducts = new LinkedHashMap<String, Object>();
		
		try { 
	        InputStream in = new FileInputStream(new File(PRODUCT_FILE));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        
	        int pid=1000;
	        String line;
	        while ((line = reader.readLine()) != null) {
	            //out.append(line);
	        	JSONObject jsonObj = new JSONObject(line);
	            String productNameFromFile = jsonObj.getString ("product_name");
	            
	            ArrayList<String> list = stringTokenizerToArrayList(productName, " ");
	            
	            if(compareProductName(list, productNameFromFile)){
	        		pid++;
	        		findProducts.put(String.valueOf(pid), productNameFromFile);
            	}
	        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

		return findProducts;
	}
	
	/**
	 * To find the lists out of listings.txt
	 * @param productName
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> getListingsList(String productName) throws IOException {
		ArrayList<String> findListings = new ArrayList<String>();
		
		try { 
	        InputStream in = new FileInputStream(new File(LISTINGS_FILE));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        
	        String line;
	        while ((line = reader.readLine()) != null) {
	        	JSONObject jsonObj = new JSONObject(line);
	            String title = jsonObj.getString ( "title" );
	            
	            ArrayList<String> list = stringTokenizerToArrayList(productName, "_");
	            
	            if(compareTitleName(list, title)){
	            	findListings.add(title);
            	}
	        }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 

		return findListings;
	}
	
	/**
	 * compare the inserted string with product name  
	 * if product name has the inserted string, then it is picked up
	 * ex)NIKON 700 shows the result NIKON P7000, NIKON 700, NIKON D7000
	 * @param str
	 * @param target
	 * @return
	 */
	public static boolean compareProductName(ArrayList<String> list, String target){
		boolean result = true;
		
		for(String source:list){
			if(!target.toLowerCase().contains(source.toLowerCase())){
				result = false;
			}
		}
		
		return result;
	}
	
	/**
	 * compare the inserted string with list title
	 * title words have to match the inserted string exactly 
	 * ex) NIKON P7000 is differnt from NIKON 700
	 * @param list
	 * @param target
	 * @return
	 */
	public static boolean compareTitleName(ArrayList<String> list, String target){
		ArrayList<String> targetList = stringTokenizerToArrayList(target, " ");
		
		ArrayList<Boolean> matchCount = new ArrayList<Boolean>();
		for(String source:list){
			for(String tar:targetList){
				if(tar.equalsIgnoreCase(source)){
					matchCount.add(true);
					break;
				}
			}
		}
		
		if(list.size() == matchCount.size()) return true;
		else return false;
	}
	
	/**
	 * To make resut.txt from the result found
	 * @param productName
	 * @param list
	 * @throws IOException
	 */
	public static void makeResultFile(String productName, ArrayList<String> list)throws IOException{
	    
	    FileWriter fw = new FileWriter(new File(RESULT_FILE));

	    try {
	        JSONObject obj = makeResultJson(productName, list);
			fw.write(obj.toString());
			
	    }catch (IOException ioe) {
	    	 ioe.printStackTrace();
	    }finally {
	    	fw.flush();
	    	fw.close();
	    } 
	}
	
	/**
	 * To make json, it needs json.jar for JSONObject
	 * @param productName
	 * @param list
	 * @return
	 * @throws IOException
	 */
	public static JSONObject makeResultJson(String productName, ArrayList<String> list) throws IOException {
        
        JSONObject obj = new JSONObject();
        obj.put("product_name",productName);
        obj.put("listings", list);
        
		return obj;
	}	

	//To make arraylist from string token
	public static ArrayList<String> stringTokenizerToArrayList(String text, String delimiter) {
		StringTokenizer tokens = new StringTokenizer(text, delimiter);
		ArrayList<String> list = new ArrayList<String>();
		while(tokens.hasMoreTokens()) {
			list.add(tokens.nextToken());
		}
		return list;
	}		
}	



