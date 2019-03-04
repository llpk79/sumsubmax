import java.util.*;

public class SumSubMaximums {

    private static int sumSubMaximums(int [] array) {
        Stack<Integer> stack = new Stack<>();
        int total = 0;

        for(int i = 0; i < array.length; i++) {
            if(stack.empty())
            {
                stack.push(i);
            }
            else if(array[i] <= array[stack.peek()])
            {
                stack.push(i);
            }
            else
                {
                while(!stack.empty() && array[i] > array[stack.peek()])
                {
                    int oldIndex = stack.pop();
                            if(!stack.empty())
                            {
                                total += array[oldIndex] * (oldIndex - stack.peek()) * (i - oldIndex);
                            }
                            else
                                {
                                    total += array[oldIndex] * (oldIndex + 1) * (i - oldIndex);
                                }
                }
                    stack.push(i);
            }
        }
        while (!stack.empty()) {
            int oldIndex = stack.pop();
            if (!stack.empty())
            {
                total += array[oldIndex] * (oldIndex - stack.peek()) * (array.length - oldIndex);
            }
            else
                {
                    total += array[oldIndex] * (oldIndex + 1) * (array.length - oldIndex);
                }
        }
        return total;
    }
    public static void main(String [] args) {
        int[] array = {2, 1, 3, 1, 3};
        System.out.println(sumSubMaximums(array));
    }
}

