//import java.lang.reflect.Array;
//import java.util.Arrays;
//import java.util.LinkedList;
//
//public class HeatPlateSingleThread
//{
//
//    private double[][] matrix;
//    int width, height;
//    double totalError;
//    int iterations;
//
//    public HeatPlateSingleThread(int w, int h, double leftHeat, double topHeat, double rightHeat, double bottomHeat)
//    {
//        matrix = new double[h][w];
//        width = w;
//        height = h;
//        iterations = 0;
//
//        fillHorizontal(0, 1, width - 1, topHeat);
//        fillHorizontal(height - 1, 1, width - 1, bottomHeat);
//        fillVertical(0, 1, height - 1, leftHeat);
//        fillVertical(width - 1, 1, height - 1, rightHeat);
//        preCalculateCorners();
//    }
//
//    private void preCalculateCorners()
//    {
//        //Top Left
//        matrix[0][0] = (matrix[1][0] + matrix[0][1]) / 2;
//        //Top Right
//        matrix[0][width - 1] = (matrix[1][width - 1] + matrix[0][width - 2]) / 2;
//        //Bottom Left
//        matrix[height - 1][0] = (matrix[height - 2][0] + matrix[height - 1][1]) / 2;
//        //Bottom Right
//        matrix[height - 1][width - 1] = (matrix[height - 1][width - 2] + matrix[height - 2][width - 1]) / 2;
//    }
//
//    private void fillHorizontal(int row, int start, int end, double value)
//    {
//        for (int i = start; i < end; i++)
//        {
//            //System.out.println(Arrays.toString(matrix[i]));
//            matrix[row][i] = value;
//        }
//    }
//
//    private void fillVertical(int col, int start, int end, double value)
//    {
//        for (int i = start; i < end; i++)
//        {
//            matrix[i][col] = value;
//        }
//    }
//
//    private double calculateValueAt(int row, int col)
//    {
//        //(Left + Top + Right + Bottom)/4
//        return (matrix[row][col - 1] + matrix[row - 1][col] + matrix[row][col + 1] + matrix[row + 1][col]) / 4;
//    }
//
//    //Assumes x1 < x2 and y1 < y2
//    private void calculateRange(int x1, int y1, int x2, int y2)
//    {
//        double localErrorSum = 0;
//        for (int c = x1; c <= x2; c++)
//        {
//            for (int r = y1; r <= y2; r++)
//            {
//                double newValue = calculateValueAt(r, c);
//                double error = Math.abs(matrix[r][c] - newValue);
//                localErrorSum += error;
//                matrix[r][c] = newValue;
//            }
//        }
//        iterations++;
//        totalError = localErrorSum;
//        //System.out.println(totalError);
//    }
//
//    //Returns a linked list of arrays of size 4 containing points to partition for p number of threads
//    //each array is [x1, x2, y1, y2]
//    private LinkedList<int[]> getPartitions(int p)
//    {
//        LinkedList<int[]> partitions = new LinkedList<>();
//        int innerWidth = width - 2;
//        int innerHeight = height - 2;
//        int rows = 0;
//        int cols = 1;
//        int powerOfTwo = 1;
//        while (p % powerOfTwo == 0 && cols < Math.sqrt(p))
//        {
//            cols = powerOfTwo;
//            powerOfTwo *= 2;
//        }
//        rows = p/cols;
//
//        int widthOffset = (innerWidth/cols) - 1;
//        int widthRemainder = innerWidth % cols;
//        int x1 = 1;
//        int x2 = 0;
//
//        for(int r = 0; r < cols; r++)
//        {
//            x2 = x1+widthOffset;
//            if(widthRemainder > 0)
//            {
//                x2++;
//                widthRemainder--;
//            }
//
//            int heightOffset = (innerHeight/rows) - 1;
//            int heightRemainder = innerHeight % rows;
//            int y1 = 1;
//            int y2= 0;
//            for (int c = 0; c < rows; c++)
//            {
//                y2 = y1+heightOffset;
//                if(heightRemainder > 0)
//                {
//                    y2++;
//                    heightRemainder--;
//                }
//
//                int [] box = new int[4];
//                box[0] = x1;
//                box[1] = x2;
//                box[2] = y1;
//                box[3] = y2;
//                partitions.add(box);
//                y1 = y2+1;
//
//            }
//
//            x1 = x2+1;
//        }
//
//        System.out.println(rows + "  " + cols);
//
//        for(int [] a: partitions)
//            System.out.println(Arrays.toString(a));
//        return partitions;
//    }
//
//
//    public void printStatistics()
//    {
//        StringBuilder sb = new StringBuilder();
//        sb.append("Thread count: ");
//        //sb.append(threadCount);
//        sb.append("\t");
//        sb.append("Final Error: ");
//        sb.append(String.format("%.2f", totalError));
//        sb.append("\t");
//        sb.append("Global Average Value: ");
//        sb.append(String.format("%.2f", totalGridAverage()));
//
//        System.out.println(sb.toString());
//    }
//
//    public void calculate_until_5_error()
//    {
//        do
//        {
//            calculateRange(1, 1, width - 2, height - 2);
//        } while (totalError > 5);
//    }
//
//    public double totalGridAverage()
//    {
//        double sum = 0;
//        for (double[] row : matrix)
//        {
//            for (double col : row)
//            {
//                sum += col;
//            }
//        }
//        return sum / (width * height);
//    }
//
//    public String toString()
//    {
//        StringBuilder sb = new StringBuilder();
//        for (double[] row : matrix)
//        {
//            for (double col : row)
//            {
//                String val = String.format("%.6f", col);
//                sb.append(val);
//                sb.append("\t");
//            }
//            sb.append("\n\n");
//        }
//        return sb.toString();
//    }
//
//}
