package csv;

/**
 *
 * @author Mathias Seuret
 */
public class Data {
    public String imName;
    public String gtName;
    public String wdName;
    
    public Data(String imName, String gtName, String wdName) {
        this.imName = imName;
        this.gtName = gtName;
        this.wdName = wdName;
        System.out.println("Found: ["+imName+"|"+gtName+"|"+wdName+"]");
    }
}
