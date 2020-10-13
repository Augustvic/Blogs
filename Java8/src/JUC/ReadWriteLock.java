/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package JUC;

/**
 * 一个 ReadWriteLock 维护一对有关联的 Lock，一个只用来进行读操作，一个
 * 用来进行写操作。读锁可以同时被多个线程持有，只要没有写入者即可。写锁是
 * 独占锁。
 *
 * 所有 ReadWriteLock 的实现必须保证 writeLock 操作的内存同步效果对关联
 * 的 readLock 也有效。也就是说，一个成功获取读锁的线程将看到在已释放的
 * 写锁上进行的所有更新。
 *
 * 与互斥所相比，读写锁在访问共享数据时允许更高级别的并发性。它利用了：
 * 虽然一次只能有一个线程（写线程）修改共享数据，但在许多情况下，任何
 * 数量的线程（读线程）都可以并发读取数据。
 * 理论上，与使用互斥锁相比，读写锁中允许的并发性增加将提高性能。在实践中，
 * 并发性的增加只能在多处理器上完全实现，然后使用共享数据的访问模式才合适。
 *
 * 读写锁相比于互斥锁是否能提高性能取决于数据的读取相对写入的频率，读和
 * 写操作的持续时间，和数据的竞争 —— 即同一时间试图读取或写入的线程数量。
 * 例如，一个集合最初用数据填充，然后很少进行修改，而经常被搜索（例如某种
 * 目录）是使用读写锁的理想选择。但是，如果频繁更新，那么数据大部分时间
 * 被锁定，并发性基本没有增加。此外如果读操作太短，读写锁实现的开销（其
 * 本质上比互斥锁更复杂）会决定执行成本，特别是许多读写锁的实现会通过
 * 一小段代码序列化所有线程。最终，只有分析和度量才能确定读写锁是否适合
 * 某个应用程序。
 *
 * 尽管读写锁的基本操作很简单，但是其实现必须使用很多策略，这可能影响给定
 * 应用程序中读写锁的有效性。
 * 这些策略的示例包括：
 * 当读和写线程都在等待，此时一个写锁正好释放，需要确定是将锁授予读锁
 * 还是写锁。通常偏好写锁，因为写入操作被认为是短暂且很少发生的。偏向读
 * 锁的策略是不常见的，如果读操作是频繁发生且长久进行的，将会导致写操作
 * 的延迟。公平（按顺序）策略也可行。
 * 在读操作仍然活跃而写操作正在等待时，确定是否授予请求读锁的读线程读锁。
 * 偏向读的策略将会无线延迟写操作，偏向写的策略则会降低并发性。
 * 确定锁是否是可重入的：一个持有写锁的线程是否可以重新获取写锁？它能否
 * 在持有写锁同时获取读锁？读锁自身是可重入的吗？
 * 不允许插入写程序时是否可以将写锁降级为读锁？是否可以优先于其他正在等待
 * 读取或写入的线程将读锁升级为写锁。
 *
 * 在评估给定实现的适用性时应该考虑这些所有的因素。
 *
 * @see ReentrantReadWriteLock
 * @see Lock
 * @see ReentrantLock
 *
 * @since 1.5
 * @author Doug Lea
 */
public interface ReadWriteLock {
    /**
     * 返回读锁。
     *
     * @return the lock used for reading
     */
    Lock readLock();

    /**
     * 返回写锁。
     *
     * @return the lock used for writing
     */
    Lock writeLock();
}
