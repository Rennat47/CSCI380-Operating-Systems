import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SquareRoots implements Runnable
{


    //    private int numberOfThreads;
//    private int total;
//    private int range;
    private int start_;
    private int end_;
    private List<Double> resultList;

    public SquareRoots(int s, int e)
    {
        start_ = s;
        end_ = e;
        resultList = new LinkedList<>();
    }

    private void calculate(int start, int end)
    {
        double totalSum = 0.0;
        for (int n = start; n < end; n++)
        {
            double root = Math.sqrt(n);
            totalSum += root;
            //System Print Statement
            //System.out.println("N = " + n + ", sqrRoot = " + root);
            resultList.add(root);
        }
        //System.out.println(Arrays.toString(resultList.toArray()));
    }


    @Override
    public void run()
    {
        long startTime = System.currentTimeMillis();
        calculate(start_, end_);
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("DONE! " + Thread.currentThread().getName() + " TOTAL TIME: " + totalTime + " milliseconds");
    }
}
