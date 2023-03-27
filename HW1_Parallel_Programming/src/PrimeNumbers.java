import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class PrimeNumbers implements Runnable
{


    private int start_;
    private int end_;
    private List<Integer> resultList;

    public PrimeNumbers(int s, int e)
    {
        start_ = s;
        end_ = e;
        resultList = new LinkedList<>();
    }

    //Bug (misses 2 and 3)
    private void calculate(int start, int end)
    {
        double totalSum = 0.0;
        for (int n = start; n < end; n++) //Loop through range
        {
            for(int i = 2; i<=(n/2); i++) //Check up to half of current value
            {
                if(n % i == 0)
                {
                    break; //Not a prime
                }
                if(i>=(n/2))
                {
                    //Found prime
                    //System.out.println("Prime found: " + n);
                    resultList.add(n);
                }
            }
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
