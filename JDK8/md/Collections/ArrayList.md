## ArrayList

### 继承结构及完整源码解析

[Iterable](https://github.com/Augustvic/Blogs/tree/master/JDK8/src/Collections/Iterable.java) | [Collection](https://github.com/Augustvic/Blogs/tree/master/JDK8/src/Collections/Collection.java) | [List](https://github.com/Augustvic/Blogs/tree/master/JDK8/src/Collections/List.java) | [AbstractCollection](https://github.com/Augustvic/Blogs/tree/master/JDK8/src/Collections/AbstractCollection.java) | [AbstractList](https://github.com/Augustvic/Blogs/tree/master/JDK8/src/Collections/AbstractList.java) | [ArrayList](https://github.com/Augustvic/Blogs/tree/master/JDK8/src/Collections/ArrayList.java)

<img src="https://github.com/Augustvic/Blogs/tree/master/JDK8/images/ArrayList.png" width=50% />

### 类属性

```java
Object[] elementData;
```

使用 Object 数组来存储 ArrayList 的元素，ArrayList 的容量是这个数组的长度。

transient: 不希望被序列化或反序列化的字段，生命周期仅存于当前内存中。

### 成员函数

**add 方法和扩容方法**

根据参数的不同，add 方法有以下两种：

```java
    /**
     * 在列表末尾添加元素
     */
    public boolean add(E e) {
        // 确保容量充足
        ensureCapacityInternal(size + 1);  // Increments modCount!!
        elementData[size++] = e;
        return true;
    }
    
    /**
     * 在列表指定位置添加元素。把该位置及之后的所有元素向后移动（索引加一）。
     */
    public void add(int index, E element) {
        rangeCheckForAdd(index);

        ensureCapacityInternal(size + 1);  // Increments modCount!!
        System.arraycopy(elementData, index, elementData, index + 1,
                size - index);
        elementData[index] = element;
        size++;
    }
```

其中 rangeCheckForAdd 函数用于检查插入的索引位置是否超出数组边界，超出直接抛出异常。

```java
    /**
     * add 操作和 addAll 操作的 rangeCheck 版本。
     */
    private void rangeCheckForAdd(int index) {
        if (index > size || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
    }
```

ensureCapacityInternal 函数和 ensureExplicitCapacity 函数是扩容的入口方法。（如果容量充足就不用扩容。）

```java
    // 扩容的入口方法
    private void ensureCapacityInternal(int minCapacity) {
        ensureExplicitCapacity(calculateCapacity(elementData, minCapacity));
    }
    
    // 扩容的入口方法
    private void ensureExplicitCapacity(int minCapacity) {
        modCount++;

        // 需要的容量超过实际容量，调用 grow 函数扩容
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }
```

在 grow 函数中，增大容量确保可以容纳参数中指定最小容量的元素。首先计算出新的容量。新的容量首先设置为原来的 1.5 倍，如果新的容量仍然小于指定的最小容量，那么直接扩容到指定的最小容量。如果新的容量比允许的最大容量还要大，调用 hugeCapacity 函数进行判断。计算出新的容量之后，对元素数组进行扩容。

```java
    /**
     * 增大容量确保可以容纳指定最小容量的元素。
     *
     * @param minCapacity the desired minimum capacity
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        // 新的容量是原来的 1.5 倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        // 如果 newCapacity 不足以容纳 minCapacity，那么直接扩容到 minCapacity
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        // 如果 newCapacity 比最大容量还大，调用 hugeCapacity 函数进行判断
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // elementData 最终指向 newCapacity 大小的新数组空间
        elementData = Arrays.copyOf(elementData, newCapacity);
    }
```

**get 方法**

返回 ArrayList 中指定索引位置的元素。

```java
    /**
     * 返回列表中指定位置的元素
     */
    public E get(int index) {
        // 边界检查
        rangeCheck(index);
        return elementData(index);
    }
    
    @SuppressWarnings("unchecked")
    E elementData(int index) {
        return (E) elementData[index];
    }
```

**set 方法**

用指定元素替换列表中某一位置的元素。

```java
    /**
     * 用指定元素替换列表中某一位置的元素。返回值是该位置的旧值。
     */
    public E set(int index, E element) {
        // 边界检查
        rangeCheck(index);
        E oldValue = elementData(index);
        // 替换
        elementData[index] = element;
        // 返回被删除的值
        return oldValue;
    }
```

**remove 方法**

ArrayList 提供两种删除方法，一种是删除指定索引处的元素，一种是删除和指定对象相同的元素。

```java
    /**
     * 删除列表指定位置的元素。
     * 把后续元素向左移动（索引减一）。
     */
    public E remove(int index) {
        rangeCheck(index);

        modCount++;
        E oldValue = elementData(index);

        // 计算要移动的元素个数
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // clear to let GC do its work

        return oldValue;
    }

    /**
     * 删除列表中第一次出现的指定元素，如果它存在的话。如果该元素不存在，
     * 不作任何变化.
     */
    public boolean remove(Object o) {
        if (o == null) {
            for (int index = 0; index < size; index++)
                if (elementData[index] == null) {
                    fastRemove(index);
                    return true;
                }
        } else {
            for (int index = 0; index < size; index++)
                if (o.equals(elementData[index])) {
                    fastRemove(index);
                    return true;
                }
        }
        return false;
    }
    
    /**
     * 私有的删除方法，填过了边界检查且不返回删除元素的值。
     * 此方法和 remove(index) 的后半部分相同
     */
    private void fastRemove(int index) {
        modCount++;
        int numMoved = size - index - 1;
        if (numMoved > 0)
            System.arraycopy(elementData, index+1, elementData, index,
                    numMoved);
        elementData[--size] = null; // clear to let GC do its work
    }
```

**removeAll 和 retailAll**
```java
    /**
     * 移除列表中和指定集合相同的元素。
     */
    public boolean removeAll(Collection<?> c) {
        // 使用 Objects 工具类检查集合 c 是否指向 null
        Objects.requireNonNull(c);
        // 根据第二个参数判断是删除还是保留
        return batchRemove(c, false);
    }
    
    /**
     * 保留列表中和指定集合相同的元素（求交集）。换句话说，移除列表中有的而
     * 指定集合中没有的那些元素。
     */
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        return batchRemove(c, true);
    }

    // complement 为 true，保留集合中存在的元素；complement 为 false 时删除。
    private boolean batchRemove(Collection<?> c, boolean complement) {
        // 对于一个final变量，如果是基本数据类型的变量，则其数值一旦在初始化
        // 之后便不能更改；如果是引用类型的变量，则在对其初始化之后便不能
        // 再让其指向另一个对象。
        final Object[] elementData = this.elementData;
        int r = 0, w = 0;
        boolean modified = false;
        try {
            for (; r < size; r++)
                // complement 为 false：若 c 不包含 elementData[r]，保留该
                // elementData[r]。
                // complement 为 true：若 c 包含 elementData[r]，保留该
                // elementData[r]。
                if (c.contains(elementData[r]) == complement)
                    // 将右边的元素左移，相当于保留该元素，删除左边的元素。
                    elementData[w++] = elementData[r];
        } finally {
            if (r != size) {
                System.arraycopy(elementData, r,
                        elementData, w,
                        size - r);
                w += size - r;
            }
            if (w != size) {
                // 索引 w 之后的元素设为 null
                for (int i = w; i < size; i++)
                    elementData[i] = null;
                modCount += size - w;
                size = w;
                modified = true;
            }
        }
        return modified;
    }
```

### ArrayList 小结

1. 元素存储在数组里，位置访问操作就是数组的位置访问操作，所以查找效率较高，时间消耗为 O(1)，但是插入删除效率低，因为插入删除操作会移动数组指定位置的前方或后方的所有元素。

2. 插入删除等基本方法均用到 Arrays.copy() 或 System.arraycopy 函数进行批量数组元素的复制。（其实不止是 ArrayList，所有的以数组为底层存储结构的集合的批量复制都是用这两个函数。）

3. 每次增加元素的时候，都需要调用 ensureCapacity 方法确保足够的容量。一般情况下单次扩容 1.5 倍，如果需要的容量大于 1.5 倍，直接扩容到指定容量。

### 随机访问数据结构（如数组）在内存中的分布

数组在内存中是一段连续的空间，数组名指向了这个空间的起始处地址。a[1] 表示相对于起始处地址偏移量为 1 的那个元素，以此类推。

由于数组在内存中是一段连续的空间，不管访问哪个元素都是用起始位置加上偏移量获取地址，然后取数据。这就是它支持随机访问的原因，且不管数组的长度是多长，都是同样的两步就能完成，所以数组的位置访问操作在常量的时间内完成，即按索引访问数组元素的时间复杂度就是 O(1)。

ArrayList 是对数组的包装，因为数组在内存中分配时必须指定长度，且一旦分配好后便无法再增加长度。在 ArrayList 的实现中，当底层数组填满后，它会再分配一个更大的新的数组，把原数组里的元素拷贝过来，然后把原数组抛弃掉。使用新的数组作为底层数组来继续存储。

可扩容是列表和数组最重要的区别之一。

### ArrayList 每次扩容为原来的 1.5 倍？

实际上并不是每一次都准确地将容量扩展成原来的 1.5 倍，例如当 1.5 倍还不能满足最小容量的要求时，直接将容量扩展为指定的最小容量；例如计算出来的新的容量大于最大容量时，将新的容量设置为 Integer.MAX_VALUE 或者 Integer.MAX_VALUE - 8。

### fail-fast

当多个线程对集合进行结构上（删除、添加）的改变时，可能触发 fail-fast 机制，即立刻抛出 ConcurrentModificationException 异常。

fail-fast 在操作迭代器时产生，主要依赖 expectedModCount 和 modCount 两个变量。在 ArrayList 中包括 add、remove、clear 等一系列方法只要涉及元素个数的变化，都会导致 modCount 值的改变。

如果在调用 checkForComodification 验证时这两个变量的值相等，说明没有发生并发修改，反之则改变了，马上抛出  ConcurrentModificationException 异常。

ArrayList 是非线程安全的集合类，如果想要在多线程环境下保持线程安全，有两种方法，一个是为方法加上 synchronized（或使用 Collections.synchronizedList），另一种方法是改用线程安全集合，例如 CopyOnWriteArrayList。