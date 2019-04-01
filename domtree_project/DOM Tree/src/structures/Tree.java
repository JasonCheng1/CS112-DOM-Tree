package structures;

import java.util.*;

/**
 * This class implements an HTML DOM Tree. Each node of the tree is a TagNode, with fields for
 * tag/text, first child and sibling.
 * 
 */
public class Tree {
	
	/**
	 * Root node
	 */
	TagNode root=null;
	
	/**
	 * Scanner used to read input HTML file when building the tree
	 */
	Scanner sc;
	
	/**
	 * Initializes this tree object with scanner for input HTML file
	 * 
	 * @param sc Scanner for input HTML file
	 */
	public Tree(Scanner sc) {
		this.sc = sc;
		root = null;
	}
	private static boolean isText(String tag){
		if(tag.equals("html") || tag.equals("body") || tag.equals("p") || tag.equals("em") || tag.equals("b") || tag.equals( "table") || tag.equals("tr") || tag.equals("td") || tag.equals("ol") || tag.equals("ul") || tag.equals("li")) {
			return false;
		}
		return true;
	}
	/**
	 * Builds the DOM tree from input HTML file, through scanner passed
	 * in to the constructor and stored in the sc field of this object. 
	 * 
	 * The root of the tree that is built is referenced by the root field of this object.
	 */
	public void build() {
		/** COMPLETE THIS METHOD **/
		TagNode temp;
		Stack<TagNode> bin = new Stack();
		Stack<Integer> level = new Stack();
		int count = 0;
		boolean isChild = true;
		while(sc.hasNextLine()){
			String curr = sc.nextLine();
			if(curr.charAt(0) == '<') {// remove the '<....>' at the ends
				curr = curr.substring(1, curr.length()-1);
			}
			if(root == null){//initializes the root
				root = new TagNode(curr, null, null);
				bin.push(root);
				count++;
				level.push(count);
				continue;
			}
			if(!bin.isEmpty() && isText(bin.peek().tag) && (!(curr.charAt(0) == '/'))){ //&& (curr.equals("em") || curr.equals("b"))){//special case
				temp = new TagNode(curr, null, null);
				bin.peek().sibling = temp;
				bin.push(temp);
				level.push(count);
				continue;
			}
			if(!bin.isEmpty() && curr.charAt(0) == '/'){//next one will be a sibling except for em/b cases
					if(!level.isEmpty() && curr.equals("/li") && !isChild){
						bin.pop();
						bin.pop();
						level.pop();
						level.pop();
						isChild = false;
						count--;
						continue;
					}
					
					bin.pop();
					isChild = false;
					if(!level.isEmpty()) {
					level.pop();
					}
					count--;
					continue;
			}
			if(isChild){//adds a child
				temp = new TagNode(curr, null, null);
				bin.peek().firstChild = temp;
				bin.push(temp);
				count++;
				level.push(count);
				continue;
			}
			if(!bin.isEmpty() && !isChild ){//adds a sibling but for em/b cases you can have siblings and children
				isChild = true;
				temp = new TagNode(curr, null, null);
				while(!level.isEmpty() && count != level.peek()) {
					level.pop();
					bin.pop();
				}
				if(!bin.isEmpty()) {
				bin.peek().sibling = temp;
				}
				bin.push(temp);
				level.push(count);
				continue;
			}
		}
	}
	
	/**
	 * Replaces all occurrences of an old tag in the DOM tree with a new tag
	 * 
	 * @param oldTag Old tag
	 * @param newTag Replacement tag
	 */
	public void replaceTag(String oldTag, String newTag) {
		/** COMPLETE THIS METHOD **/
		replaceTag(oldTag, newTag, this.root);
	}
	private static void replaceTag(String oldTag, String newTag, TagNode root) {
		for(TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild != null){
				replaceTag(oldTag, newTag, ptr.firstChild);
			}	
			if(ptr.tag.equals(oldTag)) {ptr.tag = newTag;}
		}
	}
	

	private static TagNode find(String name, TagNode root) {
		if(root != null) {
			if(root.tag.equals(name)) {
				return root;
			}else {
				TagNode ptr = find(name, root.firstChild);
				if(ptr == null) {
					ptr = find(name, root.sibling);
				}
				return ptr;
			}
		} else {
			return null;
		}
		
	}
	/**
	 * Boldfaces every column of the given row of the table in the DOM tree. The boldface (b)
	 * tag appears directly under the td tag of every column of this row.
	 * 
	 * @param row Row to bold, first row is numbered 1 (not 0).
	 */
	public void boldRow(int row) {
		/** COMPLETE THIS METHOD **/
		TagNode ptr = find("table", this.root);//finds table (table)
		ptr = ptr.firstChild;
		for(int count = 1; count != row; count++) {//find correct row (tr)
			ptr = ptr.sibling; 
		}
		ptr = ptr.firstChild;
		while(ptr != null) {//iterates down the columns of this row (td)
			if(ptr.firstChild.tag.equals("em")) {
				ptr.firstChild.tag = "b";
			}else{
				ptr.firstChild = new TagNode("b", ptr.firstChild, null);
				/*if(prev != null) {
					prev.firstChild.sibling = ptr.firstChild; FOR SIBLING NODES THAT NEED TO BE CONNECTED IN THE COLUMN
				}
				*/	
			}
			//prev = ptr;
			ptr = ptr.sibling;
		}
		
	}
	private static TagNode findPrev(TagNode target, TagNode root) {
		if(root != null) {
			if(root.sibling != null && root.sibling == target) {
				return root;
			}
			if(root.firstChild != null && root.firstChild == target) {
				return root;
			}else{
				TagNode ptr = findPrev(target, root.firstChild);
				if(ptr == null){
					ptr = findPrev(target, root.sibling);
				}
				return ptr;
			}
		} else {
			return null;
		}
	}
	/**
	 * Remove all occurrences of a tag from the DOM tree. If the tag is p, em, or b, all occurrences of the tag
	 * are removed. If the tag is ol or ul, then All occurrences of such a tag are removed from the tree, and, 
	 * in addition, all the li tags immediately under the removed tag are converted to p tags. 
	 * 
	 * @param tag Tag to be removed, can be p, em, b, ol, or ul
	 */
	public void removeTag(String tag) {
		/** COMPLETE THIS METHOD **/
		if(tag.equals("em") || tag.equals("b") || tag.equals("p")){
		while(find(tag, this.root) != null){
			TagNode ptr = find(tag, this.root);
			TagNode prev = findPrev(ptr, this.root);
			if(prev.firstChild != null && prev.firstChild.tag.equals(tag)){//child to child
				prev.firstChild = ptr.firstChild;
			}
			else if(prev.sibling != null && prev.sibling.tag.equals(tag)) {//sibling to child
				prev.sibling = ptr.firstChild;
			}
			if( ptr.firstChild.sibling == null) {//sibling to sibling (only 1)
				ptr.firstChild.sibling = ptr.sibling;
			}
			else if( ptr.firstChild.sibling != null){//sibling to sibling (more than 1)
				TagNode temp = ptr.firstChild;
				while(temp.sibling != null) {
					temp = temp.sibling;
				}
				temp.sibling = ptr.sibling;		
				}
			}
		}else if(tag.equals("ul") || tag.contentEquals("ol")){
			while(find(tag, this.root) != null){
				TagNode ptr = find(tag, this.root);
				TagNode prev = findPrev(ptr, this.root);
				if(prev.firstChild != null && prev.firstChild.tag.equals(tag)){//child to child
					prev.firstChild = ptr.firstChild;
				}
				else if(prev.sibling != null && prev.sibling.tag.equals(tag)) {//sibling to child
					prev.sibling = ptr.firstChild;
				}
				if( ptr.firstChild.sibling == null) {//sibling to sibling (only 1)
					if(ptr.firstChild.tag.equals("li")){
					ptr.firstChild.tag = "p";
					}
					ptr.firstChild.sibling = ptr.sibling;
				}
				else if( ptr.firstChild.sibling != null){//sibling to sibling (more than 1)
					TagNode temp = ptr.firstChild;
					while(temp.sibling != null) {
						if(temp.tag.equals("li")){
							temp.tag = "p";
							}
						temp = temp.sibling;
					}
					temp.sibling = ptr.sibling;	
					if(temp.tag.equals("li")){
						temp.tag = "p";
						}
					}
				}
			}	
		}
	
	private static void recAddTag(String word, String tag, TagNode root, TagNode realRoot){
			if(root == null) {
				return;
			}else if(isText(root.tag) && root.tag.toLowerCase().contains(word)){
				TagNode prev = findPrev(root, realRoot);
				if(prev.tag.equals(tag)) {//doesn't work for successive cases
					return;
				}
				else if(prev.tag.equals("em") && tag.equals("b")) {
					prev.tag = "b";
					return;
				}
				else if(prev.tag.equals("b") && tag.equals("em")) {
					prev.tag = "em";
					return;
				}
				TagNode ptr = root;
				StringTokenizer tok = new StringTokenizer(root.tag, " \t", true);
				String str = "";
		    	while(tok.hasMoreTokens()){
		    		String curr = tok.nextToken();
		    		//System.out.println(curr);
		    		String currL = curr.toLowerCase();
		    		if(currL.equals(word) || currL.equals(word + ".") || currL.equals(word + ",") || currL.equals(word + "?") || currL.equals(word + "!") || currL.equals(word + ":") || currL.equals(word + ";")){
		    			if(str.equals("")){
		    				if(prev.firstChild != null && !prev.tag.equals(tag)){
			    				prev.firstChild = new TagNode(tag, new TagNode(curr, null, null), ptr.sibling);// adding em to text(if tag is firstChild of prev)
			    				prev = prev.firstChild;
			    			}else { //if(prev.sibling != null){
			    				prev.sibling = new TagNode(tag, new TagNode(curr, null, null), ptr.sibling);//adding tag to text (if tag is sibling of prev)
			    				prev = prev.sibling;
			    			}
		    			}else{//there is str
		    				if(prev.firstChild != null && !prev.tag.equals(tag)){
			    				prev.firstChild = new TagNode(str, null, new TagNode(tag, new TagNode(curr, null, null), ptr.sibling));
			    				prev = prev.firstChild.sibling;
			    				str = "";
			    			}else { //if(prev.sibling != null){
			    				prev.sibling = new TagNode(str, null, new TagNode(tag, new TagNode(curr, null, null), ptr.sibling));
			    				prev = prev.sibling.sibling;
			    				str = "";
			    			}
		    			}
		    		}else {
		    			str += curr;
		    		}
		    	}
		    	if(!str.equals("") && prev.sibling==null && !prev.firstChild.tag.equals(str)) {
		    	prev.sibling = new TagNode(str, null, null);
		    	}
			}else{
					recAddTag(word, tag, root.firstChild, realRoot);
					recAddTag(word, tag, root.sibling, realRoot);
			}
		}
	/**
	 * Adds a tag around all occurrences of a word in the DOM tree.
	 * 
	 * @param word Word around which tag is to be added
	 * @param tag Tag to be added
	 */
	public void addTag(String word, String tag) {
		/** COMPLETE THIS METHOD **/
		word = word.toLowerCase();
		recAddTag(word, tag, this.root, this.root);
	}
	
	
	/**
	 * Gets the HTML represented by this DOM tree. The returned string includes
	 * new lines, so that when it is printed, it will be identical to the
	 * input file from which the DOM tree was built.
	 * 
	 * @return HTML string, including new lines. 
	 */
	public String getHTML() {
		StringBuilder sb = new StringBuilder();
		getHTML(root, sb);
		return sb.toString();
	}
	
	private void getHTML(TagNode root, StringBuilder sb) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			if (ptr.firstChild == null) {
				sb.append(ptr.tag);
				sb.append("\n");
			} else {
				sb.append("<");
				sb.append(ptr.tag);
				sb.append(">\n");
				getHTML(ptr.firstChild, sb);
				sb.append("</");
				sb.append(ptr.tag);
				sb.append(">\n");	
			}
		}
	}
	
	/**
	 * Prints the DOM tree. 
	 *
	 */
	public void print() {
		print(root, 1);
	}
	
	private void print(TagNode root, int level) {
		for (TagNode ptr=root; ptr != null;ptr=ptr.sibling) {
			for (int i=0; i < level-1; i++) {
				System.out.print("      ");
			};
			if (root != this.root) {
				System.out.print("|----");
			} else {
				System.out.print("     ");
			}
			System.out.println(ptr.tag);
			if (ptr.firstChild != null) {
				print(ptr.firstChild, level+1);
			}
		}
	}
}
