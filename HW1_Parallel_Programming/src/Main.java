import java.util.InputMismatchException;
import java.util.Scanner;

public class Main
{


    public static void main(String[] args)
    {
        try
        {
            //Get input of which program to run
            Scanner input = new Scanner(System.in);
            System.out.println("Enter S for square roots and P for prime numbers");
            String choice = input.nextLine();

            System.out.println("How many numbers?");
            int n = input.nextInt();

            System.out.println("How many threads? Enter 0 to use max hardware threads (" + Runtime.getRuntime().availableProcessors() + ") on this PC");
            int threadAmt = input.nextInt();
            if (threadAmt < 1)
            {
                threadAmt = Runtime.getRuntime().availableProcessors();
            }

            int range = n / threadAmt;
            int begin = 0;
            Thread[] threads = new Thread[threadAmt];
            long startTime = System.currentTimeMillis();

            //Create and run # of threads
            for (int i = 0; i < threadAmt; i++)
            {
//                System.out.println(begin + "  " + (begin + range));
                Thread t;
                if (choice.equalsIgnoreCase("S"))
                {
                    SquareRoots rootCalculator = new SquareRoots(begin, begin + range);
                    t = new Thread(rootCalculator);
                } else if (choice.equalsIgnoreCase("P"))
                {
                    PrimeNumbers primeCalculator = new PrimeNumbers(begin, begin + range);
                    t = new Thread(primeCalculator);
                } else
                {
                    return;
                }
                t.setName("Thread " + i + " From: " + begin + ", To: " + (begin + range));
                threads[i] = t;
                t.start();
                begin += range;
            }

            //Wait for all threads to finish
            for (Thread thread : threads)
            {
                thread.join();
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Total time was: " + totalTime + " milliseconds");

        } catch (InputMismatchException | InterruptedException e)
        {
            System.err.println("INVALID INPUT. EXITING");
            System.exit(1);
        } //End Try Catch

    }//End void Main

} //End Class Main
