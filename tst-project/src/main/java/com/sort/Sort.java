package com.sort;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 排序分类：5大类8小类
 * 1）插入排序（1.1直接插入排序、1.2希尔排序）
 * 2）交换排序（2.1冒泡排序、2.2快速排序）
 * 3）选择排序（3.1直接选择排序、3.2堆排序）
 * 4）归并排序
 * 5）分配排序（基数排序）
 * 
 * 所需辅助空间最多：归并排序(稳定)
 * 所需辅助空间最少：堆排序(不稳定)
 * 平均速度最快：快速排序(不稳定)
 * 
 * 稳定的排序算法：冒泡排序、插入排序、归并排序和基数排序
 * 不稳定：选择排序、快速排序、希尔排序、堆排序。
 *
 */
public class Sort {
	
	/**  
     * 1.1插入排序
     * 
     * 从第一个元素开始，该元素可以认为已经被排序
     * 取出下一个元素，在已经排序的元素序列中从后向前扫描 
     * 如果该元素（已排序）大于新元素，将该元素移到下一位置  
     * 重复步骤3，直到找到已排序的元素小于或者等于新元素的位置  
     * 将新元素插入到该位置中  
     * 重复步骤2  
     * @param numbers  待排序数组
     */  
	public static void insertSort(int[] a) {
		 int temp = 0;
		 int j = 0;
		 int size = a.length;
	     for (int i = 1; i < size; i++) {
	         temp = a[i]; //13
	         
	         for (j = i -1; j >= 0 && a[j] > temp; j--) {
	             //将大于temp的值整体后移一个单位  
	             a[j + 1] = a[j];
	         }  
	         // 如果上面执行的话，会执行j--
	         a[j + 1] = temp;
	     }  
	     System.out.println(Arrays.toString(a) + " insertSort");  
	     
	     /*{
	     int size = numbers.length;
	     int temp = 0 ;
	     int j =  0;
	     
	     for(int i = 0 ; i < size ; i++)
	     {
	         temp = numbers[i];
	         //假如temp比前面的值小，则将前面的值后移
	         for(j = i ; j > 0 && temp < numbers[j-1] ; j --)
	         {
	         numbers[j] = numbers[j-1];
	         }
	         numbers[j] = temp;
	     }*/
	 }  
	
	/**1.2  希尔排序的原理:根据需求，如果你想要结果从大到小排列，它会首先将数组进行分组，然后将较大值移到前面，较小值
	 * 移到后面，最后将整个数组进行插入排序，这样比起一开始就用插入排序减少了数据交换和移动的次数，可以说希尔排序是加强版的插入排序
	 * 
	 * 拿数组5, 2, 8, 9, 1, 3，4来说，数组长度为7，当increment为3时，数组分为两个序列
	 * 5，2，8和9，1，3，4，第一次排序，9和5比较，1和2比较，3和8比较，4和比其下标值小increment的数组值相比较
	 * 此例子是按照从大到小排列，所以大的会排在前面，第一次排序后数组为9, 2, 8, 5, 1, 3，4
	 * 第一次后increment的值变为3/2=1,此时对数组进行插入排序，
	 *实现数组从大到小排
	 */
	public static void shellSort(int[] array) {  
	    /*int i;  
	    int j;  
	    int temp;  
	    int gap = 1;  
	    int len = array.length;  
	    while (gap < len / 3) { gap = gap * 3 + 1; }  
	    for (; gap > 0; gap /= 3) {  
	        for (i = gap; i < len; i++) {  
	            temp = array[i];  
	            for (j = i - gap; j >= 0 && array[j] > temp; j -= gap) {  
	                array[j + gap] = array[j];  
	            }  
	            array[j + gap] = temp;  
	        }  
	    }  */
		int j = 0;
		int temp = 0;
		// 每次将步长缩短为原来的一半
		for (int increment = array.length / 2; increment > 0; increment /= 2) {
			for (int i = increment; i < array.length; i++) {
				temp = array[i];
				for (j = i; j >= increment && temp < array[j - increment]; j -= increment) {
					array[j] = array[j - increment];
				}
				array[j] = temp;
			}
		}
	    System.out.println(Arrays.toString(array) + " shellSort");  
	}
	
	/**
     * 3.1选择排序算法
     * 在未排序序列中找到最小元素，存放到排序序列的起始位置  
     * 再从剩余未排序元素中继续寻找最小元素，然后放到排序序列末尾。 
     * 以此类推，直到所有元素均排序完毕。 
     * @param numbers
     */
	public static void selectSort(int[] numbers) {  
	    /*int position = 0;  
	    int size = array.length;
	    int temp;
	    for (int i = 0; i < size; i++) {  
	        int j = i + 1;  
	        position = i;  
	        temp = array[i];  
	        for (; j < size; j++) {  
	            if (array[j] < temp) {  
	                temp = array[j];  
	                position = j;  
	            }  
	        }  
	        array[position] = array[i];  
	        array[i] = temp;  
	    }*/
		//57,1,68,3,2     1,57,68,3,2
		int size = numbers.length; //数组长度
	    int temp = 0 ; //中间变量
	    
		for (int i = 0; i < size; i++) {
			int k = i; // 待确定的位置
			// 选择出应该在第i个位置的数
			for (int j = size - 1; j > i; j--) {
				if (numbers[j] < numbers[k]) {
					k = j;
				}
			}
			// 交换两个数
			temp = numbers[i];
			numbers[i] = numbers[k];
			numbers[k] = temp;
		}
	    System.out.println(Arrays.toString(numbers) + " selectSort");  
	}
	
	/**
	 * 3.2堆排序
	 * @param array
	 */
	public static void heapSort(int[] array) {  
	    /* 
	     *  第一步：将数组堆化 
	     *  beginIndex = 第一个非叶子节点。 
	     *  从第一个非叶子节点开始即可。无需从最后一个叶子节点开始。 
	     *  叶子节点可以看作已符合堆要求的节点，根节点就是它自己且自己以下值为最大。 
	     */  
	    int len = array.length - 1;  
	    int beginIndex = (len - 1) >> 1;  
	    for (int i = beginIndex; i >= 0; i--) {  
	        maxHeapify(i, len, array);  
	    }  
	    /* 
	     * 第二步：对堆化数据排序 
	     * 每次都是移出最顶层的根节点A[0]，与最尾部节点位置调换，同时遍历长度 - 1。 
	     * 然后从新整理被换到根节点的末尾元素，使其符合堆的特性。 
	     * 直至未排序的堆长度为 0。 
	     */  
	    for (int i = len; i > 0; i--) {  
	        swap(0, i, array);  
	        maxHeapify(0, i - 1, array);  
	    }  
	    System.out.println(Arrays.toString(array) + " heapSort");  
	}
	
	/** 
	 * 调整索引为 index 处的数据，使其符合堆的特性。 
	 * 
	 * @param index 需要堆化处理的数据的索引 
	 * @param len   未排序的堆（数组）的长度 
	 */  
	private static void maxHeapify(int index, int len, int[] arr) {  
	    int li = (index << 1) + 1; // 左子节点索引  
	    int ri = li + 1;           // 右子节点索引  
	    int cMax = li;             // 子节点值最大索引，默认左子节点。  
	    if (li > len) {  
	        return;       // 左子节点索引超出计算范围，直接返回。  
	    }  
	    if (ri <= len && arr[ri] > arr[li]) // 先判断左右子节点，哪个较大。  
	    { cMax = ri; }  
	    if (arr[cMax] > arr[index]) {  
	        swap(cMax, index, arr);      // 如果父节点被子节点调换，  
	        maxHeapify(cMax, len, arr);  // 则需要继续判断换下后的父节点是否符合堆的特性。  
	    }  
	}  
	
	/**
	 * 将数组的值互换
	 * @param i
	 * @param j
	 * @param arr
	 */
	private static void swap(int i, int j, int[] arr) {  
	    int temp = arr[i];  
	    arr[i] = arr[j];  
	    arr[j] = temp;  
	}
	
	/**
	 * 3.2
	 * @param a
	 */
    public static void heapSort1(int[] a) {  
    	int arrayLength=a.length;  
        //循环建堆  
        for(int i=0;i<arrayLength-1;i++){  
            //建堆  
            buildMaxHeap(a,arrayLength-1-i);  
            //交换堆顶和最后一个元素  
            swap(0,arrayLength-1-i,a);  
        }  
    	System.out.println(Arrays.toString(a) + " heapSort1");  
    }

	
	 //对data数组从0到lastIndex建大顶堆
    public static void buildMaxHeap(int[] data, int lastIndex){
         //从lastIndex处节点（最后一个节点）的父节点开始 
        for(int i=(lastIndex-1)/2;i>=0;i--){
            //k保存正在判断的节点 
            int k=i;
            //如果当前k节点的子节点存在  
            while(k*2+1<=lastIndex){
                //k节点的左子节点的索引 
                int biggerIndex=2*k+1;
                //如果biggerIndex小于lastIndex，即biggerIndex+1代表的k节点的右子节点存在
                if(biggerIndex<lastIndex){  
                    //若果右子节点的值较大  
                    if(data[biggerIndex]<data[biggerIndex+1]){  
                        //biggerIndex总是记录较大子节点的索引  
                        biggerIndex++;  
                    }  
                }  
                //如果k节点的值小于其较大的子节点的值  
                if(data[k]<data[biggerIndex]){  
                    //交换他们  
                    swap(k,biggerIndex,data);  
                    //将biggerIndex赋予k，开始while循环的下一次循环，重新保证k节点的值大于其左右子节点的值  
                    k=biggerIndex;  
                }else{  
                    break;  
                }  
            }
        }
    }
	
    /**
     * 2.1冒泡排序
     * 比较相邻的元素。如果第一个比第二个大，就交换他们两个。  
     * 对每一对相邻元素作同样的工作，从开始第一对到结尾的最后一对。在这一点，最后的元素应该会是最大的数。  
     * 针对所有的元素重复以上的步骤，除了最后一个。
     * 持续每次对越来越少的元素重复上面的步骤，直到没有任何一对数字需要比较。 
     * @param numbers 需要排序的整型数组
     */
    public static void bubbleSort(int[] array) {  
        int temp = 0; 
        int size = array.length;
        for (int i = 0; i < size - 1; i++) {  
            for (int j = 0; j < size - 1 - i; j++) {  
                if (array[j] > array[j + 1]) {  
                    temp = array[j];  
                    array[j] = array[j + 1];  
                    array[j + 1] = temp;  
                }  
            }  
        }  
        System.out.println(Arrays.toString(array) + " bubbleSort");  
    }
    
    /**
     * 2.2快速排序
     * @param numbers 带排序数组
     */
	public static void quickSort(int[] numbers) {
		if (numbers.length > 0) // 查看数组是否为空
		{
			quickSort(numbers, 0, numbers.length - 1);
			System.out.println(Arrays.toString(numbers) + " quickSort"); 
		}
	}
    
    /**
     * 
     * @param numbers 带排序数组
     * @param low  开始位置
     * @param high 结束位置
     */
	public static void quickSort(int[] numbers, int low, int high) {
		if (low < high) {
			int middle = getMiddle(numbers, low, high); // 将numbers数组进行一分为二
			quickSort(numbers, low, middle - 1); // 对低字段表进行递归排序
			quickSort(numbers, middle + 1, high); // 对高字段表进行递归排序
		}
	}
    
    /**
     * 查找出中轴（默认是最低位low）的在numbers数组排序后所在位置
     * 
     * @param numbers 带查找数组
     * @param low   开始位置
     * @param high  结束位置
     * @return  中轴所在位置
     */
	public static int getMiddle(int[] numbers, int low, int high) {
		int temp = numbers[low]; // 数组的第一个作为中轴
		while (low < high) {
			while (low < high && numbers[high] >= temp) {
				high--;
			}
			numbers[low] = numbers[high];// 比中轴小的记录移到低端
			while (low < high && numbers[low] <= temp) {
				low++;
			}
			numbers[high] = numbers[low]; // 比中轴大的记录移到高端
		}
		numbers[low] = temp; // 中轴记录到尾
		return low; // 返回中轴的位置
	}
    
	/**
     * 4.归并排序
     */
	public static void mergingSort(int[] array) {  
	    sort(array, 0, array.length - 1);  
	    System.out.println(Arrays.toString(array) + " mergingSort");  
	}
	
	 /**
     * 归并排序
     * 简介:将两个（或两个以上）有序表合并成一个新的有序表 即把待排序序列分为若干个子序列，每个子序列是有序的。然后再把有序子序列合并为整体有序序列
     * 时间复杂度为O(nlogn)
     * 稳定排序方式
     * @param nums 待排序数组
     * @return 输出有序数组
     */
    public static int[] sort(int[] nums, int left, int right) {
    	//找出中间索引 
        int center = (left + right) / 2;
        if (left < right) {
        	//对左边数组进行递归 
            sort(nums, left, center);
            //对右边数组进行递归 
            sort(nums, center + 1, right);
            // 左右归并
            merge1(nums, left, center, right);
        }
        return nums;
    }

    /**
     * 将数组中low到high位置的数进行排序
     * @param nums 待排序数组
     * @param left 待排的开始位置
     * @param center 待排中间位置
     * @param right 待排结束位置
     */
    public static void merge(int[] nums, int left, int center, int right) {
        int[] temp = new int[right - left + 1];
        int i = left;// 左指针
        int j = center + 1;// 右指针
        int k = 0;

        // 把较小的数先移到新数组中
        while (i <= center && j <= right) {
            if (nums[i] < nums[j]) {
                temp[k++] = nums[i++];
            } else {
                temp[k++] = nums[j++];
            }
        }

        // 把左边剩余的数移入数组
        while (i <= center) {
            temp[k++] = nums[i++];
        }

        // 把右边边剩余的数移入数组
        while (j <= right) {
            temp[k++] = nums[j++];
        }

        // 把新数组中的数覆盖nums数组
        for (int k2 = 0; k2 < temp.length; k2++) {
            nums[k2 + left] = temp[k2];
        }
    }
	
    private static void merge1(int[] data, int left, int center, int right) {  
        int[] tmpArr = new int[data.length];  
        int mid = center + 1;  
        //third记录中间数组的索引  
        int third = left;  
        int tmp = left;  
        while (left <= center && mid <= right) {  
            //从两个数组中取出最小的放入中间数组  
            if (data[left] <= data[mid]) {  
                tmpArr[third++] = data[left++];  
            } else {  
                tmpArr[third++] = data[mid++];  
            }  
        }  
      
        //剩余部分依次放入中间数组  
        while (mid <= right) {  
            tmpArr[third++] = data[mid++];  
        }  
      
        while (left <= center) {  
            tmpArr[third++] = data[left++];  
        }  
      
        //将中间数组中的内容复制回原数组  
        while (tmp <= right) {  
            data[tmp] = tmpArr[tmp++];  
        }  
    }
    
    /**
     * 5.分配排序（基数排序）
     * @param array
     */
    public static void radixSort(int[] data, int radix, int d) {  
        // 缓存数组  
        int[] tmp = new int[data.length];  
        // buckets用于记录待排序元素的信息  
        // buckets数组定义了max-min个桶  
        int[] buckets = new int[radix];  
  
        for (int i = 0, rate = 1; i < d; i++) {  
  
            // 重置count数组，开始统计下一个关键字  
            Arrays.fill(buckets, 0);  
            // 将data中的元素完全复制到tmp数组中  
            System.arraycopy(data, 0, tmp, 0, data.length);  
  
            // 计算每个待排序数据的子关键字  
            for (int j = 0; j < data.length; j++) {  
                int subKey = (tmp[j] / rate) % radix;  
                buckets[subKey]++;  
            }  
  
            for (int j = 1; j < radix; j++) {  
                buckets[j] = buckets[j] + buckets[j - 1];  
            }  
  
            // 按子关键字对指定的数据进行排序  
            for (int m = data.length - 1; m >= 0; m--) {  
                int subKey = (tmp[m] / rate) % radix;  
                data[--buckets[subKey]] = tmp[m];  
            }  
            rate *= radix;  
        }  
        System.out.println(Arrays.toString(data) + " radixSort");
    }
    
    /**
     * 分配排序（基数排序）
     * @param array
     */
    public static void radixSort1(int[] array) {  
        //首先确定排序的趟数;  
        int max = array[0];  
        for (int i = 1; i < array.length; i++) {  
            if (array[i] > max) {  
                max = array[i];  
            }  
        }  
        int time = 0;  
        //判断位数;  
        while (max > 0) {  
            max /= 10;  
            time++;  
        }  
      
      
        //建立10个队列;  
        ArrayList<ArrayList<Integer>> queue = new ArrayList<ArrayList<Integer>>();  
        for (int i = 0; i < 10; i++) {  
            ArrayList<Integer> queue1 = new ArrayList<Integer>();  
            queue.add(queue1);  
        }  
      
      
        //进行time次分配和收集;  
        for (int i = 0; i < time; i++) {  
            //分配数组元素;  
            for (int anArray : array) {  
                //得到数字的第time+1位数;  
                int x = anArray % (int)Math.pow(10, i + 1) / (int)Math.pow(10, i);  
                ArrayList<Integer> queue2 = queue.get(x);  
                queue2.add(anArray);  
                queue.set(x, queue2);  
            }  
            int count = 0;//元素计数器;  
            //收集队列元素;  
            for (int k = 0; k < 10; k++) {  
                while (queue.get(k).size() > 0) {  
                    ArrayList<Integer> queue3 = queue.get(k);  
                    array[count] = queue3.get(0);  
                    queue3.remove(0);  
                    count++;  
                }  
            }  
        }  
        System.out.println(Arrays.toString(array) + " radixSort1");  
    }
    
  //pos=1表示个位，pos=2表示十位
    public static int getNumInPos(int num, int pos) {
        int tmp = 1;
        for (int i = 0; i < pos - 1; i++) {
            tmp *= 10;
        }
        return (num / tmp) % 10;
    }
 
    //求得最大位数d
    public static int getMaxWeishu(int[] a) {
        int max = a[0];
        for (int i = 0; i < a.length; i++) {
            if (a[i] > max)
                max = a[i];
        }
        int tmp = 1, d = 1;
        while (true) {
            tmp *= 10;
            if (max / tmp != 0) {
                d++;
            } else
                break;
        }
        System.out.println("最大位数d:"+d);
        return d;
    }
 
    public static void radixSort3(int[] a, int d) {
 
        int[][] array = new int[10][a.length + 1];
        for (int i = 0; i < 10; i++) {
            array[i][0] = 0;// array[i][0]记录第i行数据的个数
        }
        for (int pos = 1; pos <= d; pos++) {
            for (int i = 0; i < a.length; i++) {// 分配过程
                int row = getNumInPos(a[i], pos);
                int col = ++array[row][0];
                array[row][col] = a[i];
            }
            for (int row = 0, i = 0; row < 10; row++) {// 收集过程
                for (int col = 1; col <= array[row][0]; col++) {
                    a[i++] = array[row][col];
                }
                array[row][0] = 0;// 复位，下一个pos时还需使用
            }
        }
        System.out.println(Arrays.toString(a) + " radixSort3");
    }
    
	public static void main(String[] args) {
		long st = System.currentTimeMillis();
		// 排序原始数据  
		int[] numbers =  
		{49, 38, 65, 97, 76, 13, 27, 78, 34, 12, 64,0, 5, 323232,4, 62, 99, 98, 54, 56, 17, 18, 23, 34, 15,15,15,15};
		System.out.println(Arrays.toString(numbers)); 
		//insertSort(numbers);
		//shellSort(numbers);
		//selectSort(numbers);
//		heapSort(numbers);
//		heapSort1(numbers);
		//bubbleSort(numbers);
//		quickSort(numbers);
//		mergingSort(numbers);
//		radixSort(numbers,10,2);
//		radixSort1(numbers);
		radixSort3(numbers, getMaxWeishu(numbers));
		long ed = System.currentTimeMillis();
		System.out.println("执行时间："+(ed-st)+"ms");
		
	}
}
