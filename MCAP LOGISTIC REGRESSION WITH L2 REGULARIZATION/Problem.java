package mcap;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


public class Problem {

    /** the number of training data */
    public int         l;

    /** the number of features (including the bias feature if bias &gt;= 0) */
    public int         n;

    /** an array containing the target values */
    public double[]    y;

    /** array of sparse feature nodes */
    public Feature[][] x;

    /**
     * If bias &gt;= 0, we assume that one additional feature is added
     * to the end of each data instance
     */
    public double      bias;

    /**
     * see {@link Train#readProblem(File, double)}
     */
    public static Problem readFromFile(File file, double bias) throws IOException, InvalidInputDataException {
        return Train.readProblem(file, bias);
    }

    /**
     * see {@link Train#readProblem(File, Charset, double)}
     */
    public static Problem readFromFile(File file, Charset charset, double bias) throws IOException, InvalidInputDataException {
        return Train.readProblem(file, charset, bias);
    }

    /**
     * see {@link Train#readProblem(InputStream, double)}
     */
    public static Problem readFromStream(InputStream inputStream, double bias) throws IOException, InvalidInputDataException {
        return Train.readProblem(inputStream, bias);
    }

    /**
     * see {@link Train#readProblem(InputStream, Charset, double)}
     */
    public static Problem readFromStream(InputStream inputStream, Charset charset, double bias) throws IOException, InvalidInputDataException {
        return Train.readProblem(inputStream, charset, bias);
    }
}

