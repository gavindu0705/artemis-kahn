package com.artemis.kahn.component;

/**
 * 异步运行线程
 *
 * @author xiaoyu
 */
public class AsyncThread {
    public interface Call {
        void invoke();
    }

    private String name;
    private int interval;
    private Call call;

    public AsyncThread(int interval, Call call) {
        this.interval = interval;
        this.call = call;
        this.run();
    }

    public AsyncThread(String name, int interval, Call call) {
        this.name = name;
        this.interval = interval;
        this.call = call;
        this.run();
    }

    private void run() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        call.invoke();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(interval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        if (name != null) {
            thread.setName(name);
        }
        thread.setDaemon(true);
        thread.start();
    }
}
