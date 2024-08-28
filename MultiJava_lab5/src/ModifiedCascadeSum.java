import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

class Sum extends RecursiveTask<Double> {
    final int seqThreshold = 1000;
    double[] data;
    int start, end;
    Sum(double[] values, int start, int еnd) {
        data = values;
        this.start = start;
        this.end = еnd;
    }
    @Override
    protected Double compute() {
        double sum = 0;
        if ((end - start) < seqThreshold) { // Если размер секции меньше Threshold
            for (int i = start; i < end; i++) {
                sum += data[i];
            }
        } else if (end == data.length && start == 0) {
            int groups = getBaseLog(2, data.length);
            int size = data.length/groups;
            Sum[] subTasks = new Sum[groups];
            for(int i = 0; i < groups; i++){
                if(groups - 1 == i){
                    subTasks[i] = new Sum(data, i * size, end);
                    subTasks[i].fork();
                    sum += subTasks[i].join();
                }
                else{
                    subTasks[i] = new Sum(data, i * size, (i+1) * size);

                    subTasks[i].fork();
                    sum += subTasks[i].join();}
            }
        } else {
            int middle = (start + end) / 2;
            Sum subTaskA = new Sum(data, start, middle);
            Sum subTaskB = new Sum(data, middle, end);
            subTaskA.fork();
            subTaskB.fork();
            sum = subTaskA.join() + subTaskB.join();
        }
        return sum;
    }
    static int getBaseLog(int x, int y) {
        return (int) (Math.log(y) / Math.log(x));
    }
}
class ForkJoinApp {
    public static void main(String args[]) {
        ForkJoinPool fjp = new ForkJoinPool();
        double[] nums = new double[100_000];
        for (int i = 0; i < nums.length; i++) {
            nums[i] = (double) (((i % 2) == 0) ? -i : i);
        }
        Sum task = new Sum(nums, 0, nums.length);
        double summation = fjp.invoke(task);
        System.out.println("Сумма: " + summation);
    }
}