import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/***************************************************
 * Program Title: PA1 *
 * Author: Anna Rue *
 * Class: CSCI3320, Spring 2022 *

 * Programming Assignment #1 *
 * Objective: Modify subsequence sum algorithms,
 * and measure their execution times depending on input size
 *****************************************************/

public class PAOneDanielZapata {
    public static void main(String[] args) {
        Scanner intInput = new Scanner(System.in);
        Scanner stringInput = new Scanner(System.in);

    int[] numbers = {};
//        Sample data is below.
//        int[] numbers = {673, -869, -153, 214, -139, 40, 65, -925, -639, -696, 956, 823, -714, 500, 967};

        System.out.println("Choose to manually enter values (1) or to randomly generate values (2):");
        int choice = intInput.nextInt();
        if (choice == 1) {
                System.out.println("Enter up to 50 integers ranging from -9999 to 9999. \nThe numbers should be in a line separated by a comma (with no spaces).");
                String userInput = stringInput.nextLine();
            try {
                String[] stringNums = userInput.split(",");
                numbers = new int[stringNums.length];
                for (int i = 0; i < numbers.length; i++) { //Convert user input to int array
                    numbers[i] = Integer.parseInt(stringNums[i]);
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input.");
            }
        } else if (choice == 2) {
            System.out.print("Please enter the size of the problem (N):");
            int N = intInput.nextInt();
            numbers = generateNumbers(N);
        }

        if (numbers.length < 50) {
            String string = Arrays.toString(numbers);
            System.out.println(string);
        }
        long begin = System.nanoTime();
        int[] alg2 = maxSubSumTwo(numbers);
        long end = System.nanoTime();
        long alg2Time = end - begin;
        System.out.println("Algorithm 2:");
        System.out.println("MaxSum: " + alg2[0] + ", S_index: " + alg2[1] + ", E_index: " + alg2[2]);
        System.out.println("Execution Time: " + alg2Time + " nanoseconds");

        begin = System.nanoTime();
        int[] alg3 = maxSumRec(numbers, 0, numbers.length-1);
        end = System.nanoTime();
        long alg3Time = end - begin;
        System.out.println("Algorithm 3:");
        System.out.println("MaxSum: " + alg3[0] + ", S_index: " + alg3[1] + ", E_index: " + alg3[2]);
        System.out.println("Execution Time: " + alg3Time + " nanoseconds");

        begin = System.nanoTime();
        int[] alg4 = maxSubSumFour(numbers);
        end = System.nanoTime();
        long alg4Time = end - begin;
        System.out.println("Algorithm 4:");
        System.out.println("MaxSum: " + alg4[0] + ", S_index: " + alg4[1] + ", E_index: " + alg4[2]);
        System.out.println("Execution Time: " + alg4Time + " nanoseconds");
    }

    public static int[] generateNumbers(int N) {
        int[] numbers = new int[N];
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            int randomNum = r.nextInt((9999 - -9999)+1) + -9999;
            numbers[i] = randomNum;
        }
        return numbers;
    }
    /**
     * Function maxSubSumTwo
     * Finds the maximum sum of a sequence of integers.
     * Parameters: int[] a - integer array.
     * Returns: int[] maxSum, firstIndex, and lastIndex of the subsequence.
     */
    public static int[] maxSubSumTwo( int[] a) {
        int maxSum = 0;
        int firstIndex = 0;
        int lastIndex = 0;

        for( int i =0; i < a.length; i++) {
            int thisSum = 0;
            for (int j = i; j < a.length; j++) {
                thisSum += a[j];

                if (thisSum > maxSum) {
                    maxSum = thisSum;
                    if ( j > 0 ) {
                        firstIndex = i;
                        lastIndex = j;
                    }
                }
            }
        }
        return new int[]{maxSum, firstIndex, lastIndex};
    }
    /**
     * Function maxSumRec
     * A recursive function that finds the maximum sum of a sequence of integers.
     * Parameters: int[] a - integer array.
     * Returns: int maxSum - maximum sum.
     */
    public static int[] maxSumRec (int[] a, int left, int right) {
        if(left == right) { //Base case
            if (a[left] > 0)
                return new int[] {a[left], left, left};
            else
                return new int[] {0,left,left};
        }

        int center = (left + right)/2;
        int[] leftReturnValues = maxSumRec(a, left, center);
        int[] rightReturnValues = maxSumRec(a, center+1, right);
        int maxLeftSum = leftReturnValues[0];
        int maxRightSum = rightReturnValues[0];
        int maxLeftBorderSum = 0, leftBorderSum = 0;
        int leftBorderIndex = left;
        for(int i = center; i >= left; i--) {
            leftBorderSum += a[i];
            if (leftBorderSum > maxLeftBorderSum) {
                maxLeftBorderSum = leftBorderSum;
                leftBorderIndex = i;
            }
        }

        int maxRightBorderSum = 0, rightBorderSum = 0;
        int rightBorderIndex = right;
        for(int i = center+1; i <= right; i++) {
            rightBorderSum += a[i];
            if (rightBorderSum > maxRightBorderSum) {
                maxRightBorderSum = rightBorderSum;
                rightBorderIndex = i;
            }
        }
        int maxBorderSum = maxLeftBorderSum + maxRightBorderSum;
        int[] maxThreeResult = max3(maxLeftSum, maxRightSum, maxBorderSum);
        int max3Sum = maxThreeResult[0];
        int max3Winner = maxThreeResult[1];
        int[] result = {};
        switch(max3Winner) {
            case 0: // left
                result = new int[] {max3Sum, leftReturnValues[1], leftReturnValues[2]};
                break;
            case 1: // right
                result = new int[] {max3Sum, rightReturnValues[1], rightReturnValues[2]};
                break;
            case 2: // border
                result = new int[] {max3Sum, leftBorderIndex, rightBorderIndex};
                break;
        }

        return result;
    }

    /**
     * Function maxSubSumFour
     * Finds the maximum sum of a sequence of integers.
     * Parameters: int[] a - integer array.
     * Returns: int[] {maxSum, firstIndex, lastIndex}.
     */
    public static int[] maxSubSumFour(int[] a) {
        int maxSum = 0;
        int thisSum = 0;
        int primaryStartIndex = 0;
        int secondaryStartIndex = 0;
        int lastIndex = 0;


        for (int j = 0; j < a.length; j++) {
            thisSum += a[j];

            if (thisSum > maxSum) {
                maxSum = thisSum;
                lastIndex = j;
                primaryStartIndex = secondaryStartIndex;
            } else if (thisSum < 0) {
                thisSum = 0;
                secondaryStartIndex = j + 1;
            }
        }
        return new int[]{maxSum, primaryStartIndex, lastIndex};
    }

    /**
     * Function max3
     * Finds the maximum of three integer values.
     * Parameters: int maxLeftSum, int maxRightSum, maxBorderSum.
     * Returns: int[] {maxSum, int index of max element (left right or border)} .
     */
    private static int[] max3 (int maxLeftSum, int maxRightSum, int maxBorderSum) {
        if(maxLeftSum > maxRightSum && maxLeftSum >= maxBorderSum) {
            return new int[] {maxLeftSum, 0};
        } else if(maxRightSum > maxLeftSum && maxRightSum >= maxBorderSum) {
            return new int[] {maxRightSum, 1};
        } else {
            return new int[] {maxBorderSum, 2};
        }
    }
}