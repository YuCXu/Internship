# ScheduledThreadPoolExecutor

##### 简介

一、JDK1.5开始，JDK提供了ScheduledThreadPoolExecutor类来支持**周期性任务的调度**。之前的实现需要依靠**Timer**和**TimerTask**或者第三方工具来完成。但是Timer有不少缺点：

①Timer是单线程模式；

②如果执行任务期间某个TimerTask耗时较久，那么会影响其他任务的调度；

③Timer的任务调度是基于绝对时间的，对系统时间敏感；

④Timer不会捕获执行TimerTask所抛出的异常，由于Timer是单线程，所以一旦出现异常，则线程会终止，其他任务也得不到执行。

二、ScheduledThreadPoolExcutor继承ThreadPoolExecutor来重用线程池的功能，它的实现方式如下：

①将任务封装成ScheduledFutureTask对象，ScheduledFutureTask基于**相对时间**，不受系统时间的改变所影响。

②ScheduledFutureTask实现了`java.lang.Comparable`接口和`java.util.concurrent.Delayed`接口，所以有两个重要的方法：compareTo和getDelay。compareTo方法用于比较任务之间的优先级关系，如果距离下次执行的时间间隔较短，则优先级高；getDelay方法用于返回距离下次任务执行时间的时间间隔；

③ScheduledThreadPoolExecutor定义了一个DelayedWorkQueue，它是一个有序队列，会通过每个任务按照距离下次执行时间间隔的大小来排序；

④ScheduledFutureTask继承自FutureTask，可以通过返回Future对象来获取执行的结果。

三、比较

|                      Timer                       |      ScheduledThreadPoolExecutor       |
| :----------------------------------------------: | :------------------------------------: |
|                      单线程                      |                 多线程                 |
|         单个任务执行时间影响其他任务调度         |            多线程，不会影响            |
|                   基于绝对时间                   |              基于相对时间              |
| 一旦执行任务出现异常不会捕获，其他任务得不到执行 | 多线程，单个任务的执行不会影响其他线程 |

##### ScheduledThreadPoolExecutor的使用

```java
public class ScheduledThreadPoolTest {

    public static void main(String[] args) throws InterruptedException {
        // 创建大小为5的线程池
        ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);

        for (int i = 0; i < 3; i++) {
            Task worker = new Task("task-" + i);
            // 只执行一次
            //scheduledThreadPool.schedule(worker, 5, TimeUnit.SECONDS);
            // 周期性执行，每5秒执行一次
            scheduledThreadPool.scheduleAtFixedRate(worker, 0,5, TimeUnit.SECONDS);
        }

        Thread.sleep(10000);

        System.out.println("Shutting down executor...");
        // 关闭线程池
        scheduledThreadPool.shutdown();
        boolean isDone;
        // 等待线程池终止
        do {
            isDone = scheduledThreadPool.awaitTermination(1, TimeUnit.DAYS);
            System.out.println("awaitTermination...");
        } while(!isDone);

        System.out.println("Finished all threads");
    }


}


class Task implements Runnable {

    private String name;

    public Task(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        System.out.println("name = " + name + ", startTime = " + new Date());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("name = " + name + ", endTime = " + new Date());
    }

}
```

