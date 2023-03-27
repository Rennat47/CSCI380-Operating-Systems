/**
 * @Author Tanner Smith
 * @Date 11/13/2022
 */

import java.util.LinkedList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class HeatPlateFloatVersion implements Runnable
{

    private float[][] matrix; //Shared Heat Plate
    private int width, height; //Width and Height
    private float totalError; //Total Error Sum

    private Thread[] threads; //Tread array
    private int[] iterations; //Iteration count array for threads
    private float[] errors; //Individual error sum per thread

    private LinkedList<int[]> ranges; //List of [x1, x2, y1, y2] ranges for threads

    private boolean usingBarrier; //Barrier toggle
    private boolean done; //Done flag

    private ThreadGroup group;//Thread group
    private CyclicBarrier barrier; //Barrier

    /**
     *
     * @param w -width
     * @param h -height
     * @param leftHeat - static heat of the left side
     * @param topHeat - static heat of the top side
     * @param rightHeat - static heat of the right side
     * @param bottomHeat - static heat of the bottom side
     * @param num_threads - number of threads to use
     */
    public HeatPlateFloatVersion(int w, int h, float leftHeat, float topHeat, float rightHeat, float bottomHeat, int num_threads)
    {
        //Link globals
        matrix = new float[h][w];
        width = w;
        height = h;

        //Array creation
        iterations = new int[num_threads];
        threads = new Thread[num_threads];
        errors = new float[num_threads];

        group = new ThreadGroup("Heat Plate Threads");
        barrier = new CyclicBarrier(num_threads);
        done = false;
        usingBarrier = false; //False by default

        ranges = getPartitions(num_threads);

        //Fill the array with the starting values
        fillHorizontal(0, 1, width - 1, topHeat);
        fillHorizontal(height - 1, 1, width - 1, bottomHeat);
        fillVertical(0, 1, height - 1, leftHeat);
        fillVertical(width - 1, 1, height - 1, rightHeat);

        //Corner values can be pre-calculated
        preCalculateCorners();


    }

    /**
     * Call this method on the object to begin the simulation
     */
    public void beginSimulation()
    {
        //Initialize the threads and start them
        for (int i = 0; i < threads.length; i++)
        {
            Thread t = new Thread(group, this);
            t.setName(i + "");
            threads[i] = t;
            t.start();
        }

        //Wait on all threads to finish
        for (Thread t : threads)
        {
            try
            {
                t.join();
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        //Calculate final error
        for (float err : errors)
            totalError += err;

        //Print stats
        printStatistics();

    }

    /**
     * Calculates corner values
     */
    private void preCalculateCorners()
    {
        //Top Left
        matrix[0][0] = (matrix[1][0] + matrix[0][1]) / 2;
        //Top Right
        matrix[0][width - 1] = (matrix[1][width - 1] + matrix[0][width - 2]) / 2;
        //Bottom Left
        matrix[height - 1][0] = (matrix[height - 2][0] + matrix[height - 1][1]) / 2;
        //Bottom Right
        matrix[height - 1][width - 1] = (matrix[height - 1][width - 2] + matrix[height - 2][width - 1]) / 2;
    }

    /**
     *
     * @param row - row to fill
     * @param start - start of the row
     * @param end - end of the row
     * @param value - value to be filled in
     */
    private void fillHorizontal(int row, int start, int end, float value)
    {
        for (int i = start; i < end; i++)
        {
            //System.out.println(Arrays.toString(matrix[i]));
            matrix[row][i] = value;
        }
    }
    /**
     *
     * @param col - column to fill
     * @param start - start of the column
     * @param end - end of the column
     * @param value - value to be filled in
     */
    private void fillVertical(int col, int start, int end, float value)
    {
        for (int i = start; i < end; i++)
        {
            matrix[i][col] = value;
        }
    }

    /**
     * Calculates the new value at a given index
     * @param row - Row index
     * @param col - Column index
     * @return
     */
    private float calculateValueAt(int row, int col)
    {
        //(Left + Top + Right + Bottom)/4
        return (matrix[row][col - 1] + matrix[row - 1][col] + matrix[row][col + 1] + matrix[row + 1][col]) / 4;
    }

    /**
     * Assumes x1 < x2 and y1 < y2
     * Inclusive
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return the error of the new values applied compared to their previous values
     */
    private float calculateRange(int x1, int y1, int x2, int y2)
    {
        float localErrorSum = 0;
        for (int c = x1; c <= x2; c++)
        {
            for (int r = y1; r <= y2; r++)
            {
                float newValue = calculateValueAt(r, c);
                float error = Math.abs(matrix[r][c] - newValue);
                localErrorSum += error;
                matrix[r][c] = newValue;
            }
        }
        return localErrorSum;
//        totalError = localErrorSum;
        //System.out.println(totalError);
    }

    /**
     * Figures out how to partition the grid among the desired thread count
     * I hardly understand how this works anymore, but it just works :)
     * each array is [x1, x2, y1, y2]
     * @param p - # of partitions needed
     * @return a linked list of arrays of size 4 containing points to partition for p number of threads
     */
    private LinkedList<int[]> getPartitions(int p)
    {
        //Return list
        LinkedList<int[]> partitions = new LinkedList<>();

        //Since the side values don't get calculated we need the grid one value inside
        int innerWidth = width - 2;
        int innerHeight = height - 2;

        //Figure out how to split the grid
        int rows = 0;
        int cols = 1;
        int powerOfTwo = 1;
        while (p % powerOfTwo == 0 && cols < Math.sqrt(p))
        {
            cols = powerOfTwo;
            powerOfTwo *= 2;
        }
        rows = p / cols;

        //Start creating partitions
        int widthOffset = (innerWidth / cols) - 1;
        int widthRemainder = innerWidth % cols;
        int x1 = 1;
        int x2 = 0;

        for (int r = 0; r < cols; r++)
        {
            x2 = x1 + widthOffset;
            if (widthRemainder > 0)
            {
                x2++;
                widthRemainder--;
            }

            int heightOffset = (innerHeight / rows) - 1;
            int heightRemainder = innerHeight % rows;
            int y1 = 1;
            int y2 = 0;
            for (int c = 0; c < rows; c++)
            {
                y2 = y1 + heightOffset;
                if (heightRemainder > 0)
                {
                    y2++;
                    heightRemainder--;
                }

                int[] box = new int[4];
                box[0] = x1;
                box[1] = x2;
                box[2] = y1;
                box[3] = y2;
                partitions.add(box);
                y1 = y2 + 1;
            }
            x1 = x2 + 1;
        }
        return partitions;
    }


    /**
     * Print the statistics of the simulation
     */
    public void printStatistics()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("Thread count: ");
        sb.append(threads.length);
        sb.append(",\t");
        sb.append("Final Error: ");
        sb.append(String.format("%.2f", totalError));
        sb.append(",\t");
        sb.append("Global Average Value: ");
        sb.append(String.format("%.2f", totalGridAverage()));
        sb.append("\n");

        for (int i = 0; i < iterations.length; i++)
        {
            sb.append("Thread ");
            sb.append(i);
            sb.append(" ran ");
            sb.append(iterations[i]);
            sb.append(" times");
            sb.append("\n");
        }

        System.out.println(sb.toString());
    }

    /**
     * Set wheather the barrier is enabled or not
     * @param b
     */
    public void setBarrier(boolean b)
    {
        usingBarrier = b;
    }

    /**
     *
     * @return sum of grid values/total values
     */
    public float totalGridAverage()
    {
        float sum = 0;
        for (float[] row : matrix)
        {
            for (float col : row)
            {
                sum += col;
            }
        }
        return sum / (width * height);
    }

    /**
     * Prints the matrix and its values
     * @return string representation of the array
     */
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        for (float[] row : matrix)
        {
            for (float col : row)
            {
                String val = String.format("%.6f", col);
                sb.append(val);
                sb.append("\t");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }


    /**
     * Individual Threads Run
     */
    @Override
    public void run()
    {
        int threadNumber = Integer.parseInt(Thread.currentThread().getName());
        int[] range = ranges.get(threadNumber);
        float localTotalError = 0;

        do
        {
            float localError = calculateRange(range[0], range[2], range[1], range[3]);
            errors[threadNumber] = localError;
            iterations[threadNumber]++;
            localTotalError = 0;
            if (usingBarrier)
            {
                try
                {
                    barrier.await();
                } catch (InterruptedException e)
                {
                    //System.out.println("Interrupted");
                } catch (BrokenBarrierException e)
                {
                    //System.out.println("Broken Barrier");
                }
            }
            for (float err : errors)
            {
                localTotalError += err;
            }
        } while (localTotalError > 5 && !done);
        done = true;
        Thread.currentThread().getThreadGroup().interrupt();
//        System.out.println("Thread " + threadNumber + " finished");
    }
}
