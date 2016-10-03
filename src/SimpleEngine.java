
import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

/**
A very simple search engine. Uses an inverted index over a folder of TXT files.
 */
public class SimpleEngine {
	
	static HashMap<List<String>,NaiveInvertedIndex> fileIndex=new HashMap<List<String>,NaiveInvertedIndex>();
	
	
	
	

	//public static void main(String[] args) throws IOException {

	public static HashMap getIndex(String corpusName)throws IOException {
		
		
		final Path currentWorkingPath = Paths.get(corpusName).toAbsolutePath();

		// the inverted index
		final NaiveInvertedIndex index = new NaiveInvertedIndex();

		// the list of file names that were processed
		final List<String> fileNames = new ArrayList<String>();


		// This is our standard "walk through all .txt files" code.
		Files.walkFileTree(currentWorkingPath, new SimpleFileVisitor<Path>() {
			int mDocumentID  = 0;

			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) {
				// make sure we only process the current working directory
				if (currentWorkingPath.equals(dir)) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws FileNotFoundException {
				// only process .txt files
				if (file.toString().endsWith(".txt") ) {
					// we have found a .txt file; add its name to the fileName list,
					// then index the file and increase the document ID counter.
					//System.out.println("Indexing file " + file.getFileName());


					fileNames.add(file.getFileName().toString());

					indexFile(file.toFile(), index, mDocumentID);
					mDocumentID++;
				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other errors occur
			public FileVisitResult visitFileFailed(Path file,
					IOException e) {

				return FileVisitResult.CONTINUE;
			}

		});

		printResults(index, fileNames);
		//searchWord(index,fileNames);

		// Implement the same program as in Homework 1: ask the user for a term,
		// retrieve the postings list for that term, and print the names of the 
		// documents which contain the term.
		fileIndex.put(fileNames,index);

		return fileIndex; 
	}

	/**
   Indexes a file by reading a series of tokens from the file, treating each 
   token as a term, and then adding the given document's ID to the inverted
   index for the term.
   @param file a File object for the document to index.
   @param index the current state of the index for the files that have already
   been processed.
   @param docID the integer ID of the current document, needed when indexing
   each term from the document.
	 */
	private static void indexFile(File file, NaiveInvertedIndex index, 
			int docID) throws FileNotFoundException{
		// TO-DO: finish this method for indexing a particular file.
		// Construct a SimpleTokenStream for the given File.
		// Read each token from the stream and add it to the index.

		SimpleTokenStream simpleTokenObj= new SimpleTokenStream(file);
		int pos=0;
		while(simpleTokenObj.hasNextToken()){
			// String fn= file.getName();
			//Write code for PorterStemmer
			String temp[]= callPC(simpleTokenObj.nextToken());
			for(String temp1: temp){
				index.addTerm(temp1, docID,pos);   
			}
			pos++;

		}





	}

	private static void printResults(NaiveInvertedIndex index, 
			List<String> fileNames) {

		// TO-DO: print the inverted index.
		// Retrieve the dictionary from the index. (It will already be sorted.)
		// For each term in the dictionary, retrieve the postings list for the
		// term. Use the postings list to print the list of document names that
		// contain the term. (The document ID in a postings list corresponds to 
		// an index in the fileNames list.)

		// Print the postings list so they are all left-aligned starting at the
		// same column, one space after the longest of the term lengths. Example:
		// 
		// as:      document0 document3 document4 document5
		// engines: document1
		// search:  document2 document4  


		String token[]=index.getDictionary();
		for(String temp:token){
			HashMap<Integer,List<Integer>> finalList=(index.getPostings(temp));
			System.out.println(temp+" : "+finalList);
		}

	}
	public static void searchWord(NaiveInvertedIndex index, List<String> fileNames,String searchWord) { //Write code for PorterStemmer
		String token[]=index.getDictionary();
		/*System.out.println("Enter a term to search for: ");
		Scanner s = new Scanner(System.in);
		String word1 = s.next();*/
		String temp[]= callPC(searchWord);
		for(String word: temp){

//			if (word.equalsIgnoreCase("quit")) {
//				System.out.println("Bye!");
//				System.exit(0);}
                // int y = Arrays.binarySearch(token, word);

			try{
				if(("null").equals(index.getPostings(word).toString())){
					System.out.println("Word does not present ");
					//searchWord(index,fileNames);

				}
				else{
					HashMap<Integer,List<Integer>> mIndex=index.getPostings(word);
					Iterator itr=mIndex.entrySet().iterator();
					while(itr.hasNext()){
						Map.Entry entry=(Map.Entry)itr.next();
						System.out.println(fileNames.get(Integer.parseInt(entry.getKey().toString()))+" ");
						//System.out.println(entry.getValue());
                                        }
					//searchWord(index,fileNames);
                                    }
                            }

			catch(NullPointerException e){
				System.out.println("Word does not present ");
				//searchWord(index,fileNames);

			}

		}}
	public static String[] callPC(String token){
		String[] processToken;

		ArrayList<String> normalizeToken=new ArrayList<>();
		normalizeToken = NormalizeToken.normalizeToken(token);
		//System.out.println("Normalized Token: " + normalizeToken);
		int i=0;
		processToken=new String[normalizeToken.size()];
		for(String tkn:normalizeToken){
			//System.out.println("we are on "+tkn);
			processToken[i] = PorterStemmer.processToken(tkn);
			//System.out.println("stem: " + processToken);
			i++;
		}

		return processToken;

	}
        public static String callPoterStem(String token){
		String processToken=null;

		ArrayList<String> normalizeToken=new ArrayList<>();
		normalizeToken = NormalizeToken.normalizeToken(token);
		for (String tkn : normalizeToken) {
                    processToken = PorterStemmer.processToken(tkn);
                }
		return processToken;

	
}}
