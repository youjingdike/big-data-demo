package com.thread.aqs;

import org.junit.runner.notification.RunListener;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

@RunListener.ThreadSafe
public class OneShotLaunch {
    private final Sync sync = new Sync();

    public void signal() {
        sync.releaseShared(0);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(0);
    }

    private class Sync extends AbstractQueuedSynchronizer{
        @Override
        protected int tryAcquireShared(int arg) {
            //如果锁是开的（state==1），那么这个操作将成功，否则将失败
            return getState()==1?1:-1;
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            setState(1);//现在打开锁
            return true;//现在其他的线程可以获取该闭锁
        }
    }
}
