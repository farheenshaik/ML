package mcap;





import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;



public final class Model implements Serializable {

    private static final long serialVersionUID = -6456047576741854834L;

    double                    bias;

    int[]                     label;

    int                       nr_class;

    int                       nr_feature;

    SolverType                solverType;

    double[]                  w;

    public int getNrClass() {
        return nr_class;
    }

    public int getNrFeature() {
        return nr_feature;
    }

    public int[] getLabels() {
        return copyOf(label, nr_class);
    }

    public double[] getFeatureWeights() {
        return Linear.copyOf(w, w.length);
    }

    public boolean isProbabilityModel() {
        return solverType.isLogisticRegressionSolver();
    }

    public double getBias() {
        return bias;
    }

    private double get_w_value(int idx, int label_idx) {
        if (idx < 0 || idx > nr_feature) {
            return 0;
        }
        if (solverType.isSupportVectorRegression()) {
            return w[idx];
        } else {
            if (label_idx < 0 || label_idx >= nr_class) {
                return 0;
            }
            if (nr_class == 2 && solverType != SolverType.MCSVM_CS) {
                if (label_idx == 0) {
                    return w[idx];
                } else {
                    return -w[idx];
                }
            } else {
                return w[idx * nr_class + label_idx];
            }
        }
    }

    public double getDecfunCoef(int featIdx, int labelIdx) {
        if (featIdx > nr_feature) {
            return 0;
        }
        return get_w_value(featIdx - 1, labelIdx);
    }

    public double getDecfunBias(int labelIdx) {
        int biasIdx = nr_feature;
        if (bias <= 0) {
            return 0;
        } else {
            return bias * get_w_value(biasIdx, labelIdx);
        }
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Model");
        sb.append(" bias=").append(bias);
        sb.append(" nr_class=").append(nr_class);
        sb.append(" nr_feature=").append(nr_feature);
        sb.append(" solverType=").append(solverType);
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(bias);
        result = prime * result + (int)(temp ^ (temp >>> 32));
        result = prime * result + Arrays.hashCode(label);
        result = prime * result + nr_class;
        result = prime * result + nr_feature;
        result = prime * result + ((solverType == null) ? 0 : solverType.hashCode());
        result = prime * result + Arrays.hashCode(w);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Model other = (Model)obj;
        if (Double.doubleToLongBits(bias) != Double.doubleToLongBits(other.bias)) return false;
        if (!Arrays.equals(label, other.label)) return false;
        if (nr_class != other.nr_class) return false;
        if (nr_feature != other.nr_feature) return false;
        if (solverType == null) {
            if (other.solverType != null) return false;
        } else if (!solverType.equals(other.solverType)) return false;
        if (!equals(w, other.w)) return false;
        return true;
    }

    protected static boolean equals(double[] a, double[] a2) {
        if (a == a2) return true;
        if (a == null || a2 == null) return false;

        int length = a.length;
        if (a2.length != length) return false;

        for (int i = 0; i < length; i++)
            if (a[i] != a2[i]) return false;

        return true;
    }

    public void save(File file) throws IOException {
        Linear.saveModel(file, this);
    }

    public void save(Writer writer) throws IOException {
        Linear.saveModel(writer, this);
    }

    public static Model load(File file) throws IOException {
        return Linear.loadModel(file);
    }

    public static Model load(Reader inputReader) throws IOException {
        return Linear.loadModel(inputReader);
    }
}

