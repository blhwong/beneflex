import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;


public class ListCreate {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ArrayList<String[]> masterTokensList = new ArrayList<String[]>();
		ArrayList<String[]> traceTokensList = new ArrayList<String[]>();
		ArrayList<String> unactivate = new ArrayList<String>();
		ArrayList<String> reactivate = new ArrayList<String>();
		ArrayList<String> newEntries = new ArrayList<String>();
		
		final String beneflexDir = "C:\\Users\\Brandon\\Documents\\Beneflex\\ref\\";
		//final String refChaseDir = "C:\\Users\\Brandon\\Documents\\NMI\\ref Chase\\";
		final String mainDir = "C:\\Users\\Brandon\\Documents\\Beneflex\\";
		final String comma = ",";
		final String quote = "\"";
		
		System.out.println("Beneflex List Creator");
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Date: (MM/DD/YYYY) ");
		String date = scanner.next();
		String dateDir = date.replace("/", ""); 
		
		
		
		InputStream master = new BufferedInputStream(new FileInputStream(beneflexDir + dateDir + "\\" + "master.csv"));
		InputStream trace = new BufferedInputStream(new FileInputStream(beneflexDir + dateDir + "\\" + "Standard Trace.csv"));
		
		Reader masterReader = new InputStreamReader(master);
		Reader traceReader = new InputStreamReader(trace);
		
		BufferedReader masterBuffer = new BufferedReader(masterReader);
		BufferedReader traceBuffer = new BufferedReader(traceReader);
		
		
		masterBuffer.readLine();
		String line;
		String[] masterTokens;
		int active = 0;
		while((line = masterBuffer.readLine()) != null)
		{
			masterTokens = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			if(masterTokens[23].contains("True"))
			{
				active++;
			}
			masterTokensList.add(masterTokens);
		}
		System.out.print(masterTokensList.size());
		System.out.print(" entries in master! ");
		System.out.print(active);
		System.out.println(" active clients.");
		
		String lineTrace;
		String[] traceTokens;
		traceBuffer.readLine();
		int beneflex = 0;
		while((lineTrace = traceBuffer.readLine()) != null)
		{
			traceTokens = lineTrace.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
			if(traceTokens[11].equals("\"Beneflex\""))
			{
				beneflex++;
			}
			traceTokensList.add(traceTokens);
		}
		System.out.print(traceTokensList.size());
		System.out.print(" entries in LeadTrac! ");
		System.out.print(beneflex);
		System.out.println(" Beneflex clients in LeadTrac.");
		
		
		
		
		int matches = 0;
		int cvvnomatch = 0;
		
		final String outputDir = mainDir + "outputBeneflex\\" + dateDir + "\\";
		new File(mainDir + "outputBeneflex").mkdir();
		new File(mainDir + "outputBeneflex\\" + dateDir).mkdir();
		
		File fileUpload = new File(outputDir + dateDir + " output.csv");
		if (!fileUpload.exists()) 
		{
			fileUpload.createNewFile();
		}
		
		FileWriter fwUpload = new FileWriter(fileUpload.getAbsoluteFile());
		BufferedWriter bwUpload = new BufferedWriter(fwUpload);
		bwUpload.write("GROUP NUMBER,RECORD TYPE,PACKAGE NAME,UNIQUE IDENTIFIER,FIRST NAME,MIDDLE INITIAL,LAST NAME,ADDRESS 1,ADDRESS 2,CITY,STATE,ZIP CODE,HOME PHONE NUMBER,EMAIL ADDRESS,BIRTHDATE,EFFECTIVE DATE,TERMINATION DATE,LANGUAGE,AFFILIATE CODE,MISC 1,MISC 2,SOCIAL SECURITY NUMBER");
		//bwUpload.write("\"cc_number\",\"cc_exp\",\"amount\",\"cvv\",\"first_name\",\"last_name\",\"address_1\",\"city\",\"state\",\"postal_code\",\"country\",\"order_id\",\"order_description\"");
		bwUpload.newLine();
		
		int unactivateCount = 0;
		int reactivateCount = 0;
		int newEntriesCount = 0;
		int ineligibleState = 0;
		for(String[] sTrace : traceTokensList)
		{
			boolean match = false;
			for(String[] sMaster : masterTokensList)
			{
				if(sTrace[0].contains(sMaster[16]))		//match found within trace and master
				{
					match = true;
					String[] name;
					name = sTrace[4].replace("\"", "").split(" ");
					final String groupNumber = "SLA75001,";
					final String establishedPackageName = "Beneflex,";
					final String uniqueIdentifier = sTrace[0].replace("\"", "") + ",";
					//final String firstName = sTrace[4].replace("\"", "");
					final String firstName = name[0] + ",";
					final String middleInitial= ",";
					String lastName = "";
					for(int n = 1; n < name.length; n++)
					{
						lastName = lastName + name[n] + " ";
					}
					lastName = lastName + ",";
					//final String lastName = ",";
					String address1 = sTrace[7].replace("\"", "");
					if(address1.contains(","))
					{
						System.out.print("comma found in address for ");
						System.out.println(firstName);
						address1 = address1.replace(",", "");
					}
					address1 = address1 + ",";
					final String address2 = ",";
					final String city = sTrace[8].replace("\"", "") + ",";
					final String state = sTrace[9].replace("\"", "") + ",";
					final String zipCode = sTrace[10].replace("\"", "") + ",";
					final String homePhone = sTrace[5].replace("\"", "") + ",";
					final String emailAddress = sTrace[6].replace("\"", "") + ",";
					final String birthDate = ",";
					final String effectiveDate = date;
					final String terminationDate = ",";
					final String language = ",";
					final String affiliationCode = ",";
					final String misc1 = ",";
					final String misc2 = ",";
					final String socialSecurity = "";
					
					if(!sTrace[11].equals("\"Beneflex\"") && sMaster[23].contains("True"))		//not beneflex && active
					{
						//need to unactivate
						unactivateCount++;
						final String recordType = "T,";
						unactivate.add(groupNumber +
								recordType +
								establishedPackageName +
								uniqueIdentifier +
								firstName +
								middleInitial +
								lastName +
								address1 +
								address2 +
								city +
								state +
								zipCode +
								homePhone +
								emailAddress +
								birthDate +
								effectiveDate +
								terminationDate +
								language +
								affiliationCode +
								misc1 +
								misc2 +
								socialSecurity
								
								);
						
					}
					else if(sTrace[11].equals("\"Beneflex\"") && sMaster[23].contains("False"))		//beneflex && false
					{
						//need to reactivate
						reactivateCount++;
						final String recordType = "N,";
						reactivate.add(groupNumber +
								recordType +
								establishedPackageName +
								uniqueIdentifier +
								firstName +
								middleInitial +
								lastName +
								address1 +
								address2 +
								city +
								state +
								zipCode +
								homePhone +
								emailAddress +
								birthDate +
								effectiveDate +
								terminationDate +
								language +
								affiliationCode +
								misc1 +
								misc2 +
								socialSecurity
								
								);
						
					}
					continue;
				}
			}
			if(!match && sTrace[11].equals("\"Beneflex\""))	//no match
			{
				//need to activate new entry
				
				if(sTrace[9].contains("FL") ||
					sTrace[9].contains("WA") ||
					sTrace[9].contains("KS") ||
					sTrace[9].contains("UT") ||
					sTrace[9].contains("VT")
						)
				{
					ineligibleState++;
					continue;
				}
					
				String[] name;
				name = sTrace[4].replace("\"", "").split(" ");
				final String groupNumber = "SLA75001,";
				final String recordType = "A,";
				final String establishedPackageName = "Beneflex,";
				final String uniqueIdentifier = sTrace[0].replace("\"", "") + ",";
				//final String firstName = sTrace[4].replace("\"", "") + ",";
				final String firstName = name[0] + ",";
				final String middleInitial= ",";
				String lastName = "";
				for(int n = 1; n < name.length; n++)
				{
					lastName = lastName + name[n] + " ";
				}
				lastName = lastName + ",";
				//final String lastName = ",";
				String address1 = sTrace[7].replace("\"", "");
				if(address1.contains(","))
				{
					System.out.print("comma found in address for ");
					System.out.println(firstName);
					address1 = address1.replace(",", "");
				}
				address1 = address1 + ",";
				final String address2 = ",";
				final String city = sTrace[8].replace("\"", "") + ",";
				final String state = sTrace[9].replace("\"", "") + ",";
				final String zipCode = sTrace[10].replace("\"", "") + ",";
				final String homePhone = sTrace[5].replace("\"", "") + ",";
				final String emailAddress = sTrace[6].replace("\"", "") + ",";
				final String birthDate = ",";
				final String effectiveDate = date;
				final String terminationDate = ",";
				final String language = ",";
				final String affiliationCode = ",";
				final String misc1 = ",";
				final String misc2 = ",";
				final String socialSecurity = "";
				newEntriesCount++;
				newEntries.add(groupNumber +
						recordType +
						establishedPackageName +
						uniqueIdentifier +
						firstName +
						middleInitial +
						lastName +
						address1 +
						address2 +
						city +
						state +
						zipCode +
						homePhone +
						emailAddress +
						birthDate +
						effectiveDate +
						terminationDate +
						language +
						affiliationCode +
						misc1 +
						misc2 +
						socialSecurity
						
						);
					
					
			}
		}
		
		
		System.out.print(unactivateCount);
		System.out.println(" entries to unactivate");
		System.out.print(reactivateCount);
		System.out.println(" entries to reactivate");
		System.out.print(newEntriesCount);
		System.out.println(" entries to add");
		System.out.print("Final Beneflex count is ");
		System.out.println(active - unactivateCount + reactivateCount + newEntriesCount);
		System.out.print(ineligibleState);
		System.out.println(" ineligible states found. Please update LeadTrac Trace.");
		
		
		for(String u : unactivate)
		{
			bwUpload.write(u);
			bwUpload.newLine();
		}
		for(String r : reactivate)
		{
			bwUpload.write(r);
			bwUpload.newLine();
		}
		for(String a : newEntries)
		{
			bwUpload.write(a);
			bwUpload.newLine();
		}
		
		System.out.print(unactivate.size() + reactivate.size() + newEntries.size());
		System.out.println(" entries in upload list");
		
		
		System.out.println("Done!");
		
		
		
		bwUpload.close();
		traceBuffer.close();
		masterBuffer.close();
		

	}

}
