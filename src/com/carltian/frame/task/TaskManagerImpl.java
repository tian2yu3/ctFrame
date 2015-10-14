package com.carltian.frame.task;

import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.carltian.frame.container.ContainerImpl;
import com.carltian.frame.container.annotation.Resource;
import com.carltian.frame.container.reg.Registration;
import com.carltian.frame.container.reg.RegistrationType;
import com.carltian.frame.util.FrameLogger;

/**
 * 任务管理器实现类。<br/>
 * 任务以指定间隔运行，如果任务运行时间长于间隔时间，则任务运行结束后立刻重新运行，因此任务为单线程运行。<br/>
 * 值得注意的是，每个任务为单线程运行，不同任务之间是不同的线程。因此如果任务抛出异常，则会终止这个任务，而不会影响其他任务。
 * 
 * @version 1.0
 * @author Carl Tian
 */
public class TaskManagerImpl implements TaskManager {

	@Resource
	private ContainerImpl container;

	private Timer timer;
	private final ReadWriteLock timerLock = new ReentrantReadWriteLock();
	private final ConcurrentLinkedQueue<TaskConfig> taskConfigQueue = new ConcurrentLinkedQueue<TaskConfig>();

	@Override
	public void register(TaskConfig config) {
		// 加入容器
		Registration reg = config.getRegistration();
		if (reg.getName() == null || reg.getName().isEmpty()) {
			// 匿名Task
			reg.setName(UUID.randomUUID().toString());
		}
		container.register(reg);
		// 参数转换与有效性校验
		if (config.getDelay() < 0 || config.getPeriod() < 0) {
			throw new IllegalArgumentException("Task的参数delay或period不可为负");
		}
		taskConfigQueue.add(config);
		// 初始化task
		timerLock.readLock().lock();
		try {
			if (timer != null) {
				// timer在运行
				initTask(config);
			}
		} finally {
			timerLock.readLock().unlock();
		}
	}

	@Override
	public void stop() {
		timerLock.writeLock().lock();
		try {
			if (timer != null) {
				timer.cancel();
				timer = null;
			}
		} finally {
			timerLock.writeLock().unlock();
		}
	}

	@Override
	public void start() {
		timerLock.writeLock().lock();
		try {
			if (timer == null) {
				timer = new Timer(true);
				Iterator<TaskConfig> iterator = taskConfigQueue.iterator();
				while (iterator.hasNext()) {
					initTask(iterator.next());
				}
			}
		} finally {
			timerLock.writeLock().unlock();
		}
	}

	private void initTask(TaskConfig config) {
		TimerTask task = container.lookup(RegistrationType.Task, config.getRegistration().getName());
		if (task == null) {
			FrameLogger.error("无法从当前容器中获取Task");
			throw new RuntimeException("无法从当前容器中获取Task");
		}
		if (config.getPeriod() > 0) {
			timer.scheduleAtFixedRate(task, config.getDelay(), config.getPeriod());
		} else {
			timer.schedule(task, config.getDelay());
		}
	}

}
