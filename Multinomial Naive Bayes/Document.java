package mnb;

import java.util.HashMap;
import java.util.Map;


public class Document {
    /**
     * Document constructor
     */
	public Document() {
        tokens = new HashMap<>();
    }
    /**
     * List of token counts
     */
    public Map<String, Integer> tokens;
    
    /**
     * The class of the document
     */
    public String category;
    
    
    
}
