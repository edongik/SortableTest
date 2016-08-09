/**
 * Title : Challenge for Test
 * Author : Dong-Ik Lee
 * Create Date : July 25, 2016
 * Update Date : August 8, 2016
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
import java.util.Scanner;
import java.util.StringTokenizer;

import org.json.JSONObject;

/**
 * Challenge Class for showing menu, result, error, etc and getting the found result
 * @author DONG IK LEE
 *
 */
public class Challenge {
	
	public static void main(String[] args){
		
		String command = "";
		String productName = "";
		
		Scanner sc = new Scanner(System.in);
		
		printStart();
        printMenu();
        
        while(sc.hasNextLine()){
        	
        	command = sc.nextLine().toString();
        	
        	if(command==null || command.trim().equals("")){
        		printMenu();
        	}else if(command.trim().equalsIgnoreCase("QUIT")){
        		break;
        	}else{
    			if(command.length()<2){
    				System.out.println("\n- Please, put a word(s) more than 1 letter");
    			}else{
    				productName = command;

    				try {
    					//To find list from products.txt
						findProduct(productName);
						
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
	 * To show the product name found and find the list
	 * @param productName
	 * @throws IOException
	 */
	public static void findProduct(String productName)throws IOException{
		ArrayList<JSONObject> resultList = new ArrayList<JSONObject>();
		ArrayList<String> products  = new ArrayList<String>();
		
		products = DataFinder.getProductList(productName);
		
		if(products.size() < 1){
			System.out.println("\n\n- There is no matching product with the name of \""+productName+"\"");
		}else{
			System.out.println("\n- Found "+products.size()+" matching products with the name of \""+productName+"\"");
			
			for (String product : products) {
				//To find list from product list
				JSONObject obj = findListing(product);
				resultList.add(obj);
			}
			
		}
		//To make result file
		DataFinder.makeResultFile(resultList);
		
		printMenu();
	}
	
	/**
	 * To show the list name found
	 * @param productNumber
	 * @throws IOException
	 */
	public static JSONObject findListing(String productName)throws IOException{
		ArrayList<Listing> listings = new ArrayList<Listing>();
		JSONObject resultObj = new JSONObject();
		
		if(productName!=null){
			System.out.println("\n- product name : \""+productName+"\"");
			
			//To find the list contain the product name from user's input
			listings = DataFinder.getListingsList(productName);
			//To get json type result
			resultObj = DataFinder.makeResultJson(productName, listings);			
		}else{
			printError();
		}
		return resultObj;
	}
	
	public static void printMenu(){
		System.out.println("\n- If you want to quit this program, please enter 'quit'");
        System.out.println("- If you want to find the product, please put the product(s)' name in :");
	}
	
	public static void printLine(){
		System.out.println("-----------------------------------------------------------------");
	}
	
	public static void printError(){
		System.out.println("\n- Error! Put the product name");
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
	static private String RESULT_FILE = "./results.txt";
	
	public DataFinder(){}

	/**
	 * To find the product name out of products.txt
	 * @param productName
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<String> getProductList(String productName) throws IOException {
		ArrayList<String> findProducts = new ArrayList<String>();
		
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
	        		findProducts.add(productNameFromFile);
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
	public static ArrayList<Listing> getListingsList(String productName) throws IOException {
		ArrayList<Listing> findListings = new ArrayList<Listing>();
		
		try { 
	        InputStream in = new FileInputStream(new File(LISTINGS_FILE));
	        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
	        
	        String line;
	        Listing l;
	        while ((line = reader.readLine()) != null) {
	        	l = new Listing();
	        	JSONObject jsonObj = new JSONObject(line);
	            String title = jsonObj.getString ( "title" );
	            String manufacturer = jsonObj.getString ( "manufacturer" );
	            String currency = jsonObj.getString ( "currency" );
	            String price = jsonObj.getString ( "price" );
	            
	            ArrayList<String> arrayProductName = stringTokenizerToArrayList(productName, "_");
	            
	            if(compareTitleName(arrayProductName, title)){
	            	l.setTitle(title);
	            	l.setManufacturer(manufacturer);
	            	l.setCurrency(currency);
	            	l.setPrice(price);
	            	
	            	findListings.add(l);
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
	 * To make results.txt from the result found
	 * @param productName
	 * @param list
	 * @throws IOException
	 */
	public static void makeResultFile(ArrayList<JSONObject> resultList)throws IOException{
	    
	    FileWriter fw = new FileWriter(new File(RESULT_FILE));

	    try {
	    	for(JSONObject jo:resultList){
	    		fw.write(jo.toString());
	    		fw.write(System.getProperty( "line.separator" ));
	    	}
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
	public static JSONObject makeResultJson(String productName, ArrayList<Listing> list) throws IOException {
        
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


