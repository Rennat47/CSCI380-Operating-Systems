public class Main
{

    public static void main(String[] args)
    {
        HeatPlateFloatVersion plate = new HeatPlateFloatVersion(750, 750, 10, 80, 30, 40, 16);
//        HeatPlate plate = new HeatPlate(750, 750, 10, 80, 30, 40, 8);

        plate.setBarrier(false);

        long start = System.currentTimeMillis();
        plate.beginSimulation();
        long stop = System.currentTimeMillis();

        double runtime = stop - start;

        System.out.println("Total Run Time: " + runtime / 1000 + "s");
//        System.out.println(plate);

    }
}
