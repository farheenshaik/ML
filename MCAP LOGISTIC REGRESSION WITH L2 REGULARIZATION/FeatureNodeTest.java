package mcap;







public class FeatureNodeTest {

   
    public void testConstructorIndexZero() {
        // since 1.5 there's no more exception here
        new FeatureNode(0, 0);
    }

    
    public void testConstructorIndexNegative() {
        new FeatureNode(-1, 0);
    }

   
    public void testConstructorHappy() {
        Feature fn = new FeatureNode(25, 27.39);
        
        fn = new FeatureNode(1, -0.22222);
        
    }
}
