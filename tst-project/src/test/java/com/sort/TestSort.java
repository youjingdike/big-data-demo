package com.sort;

import org.junit.Test;

import java.util.Arrays;

public class TestSort {
    
    @Test
    public void testSearch() {
        int[] arr = {234,245,77,3,543,67,78,95,378,678,205,753,457,2903,340} ;   
        int searchWord = 6780; 
        
        System.out.printf("普通循环查找%d的次数是%d",searchWord,genetalLoop(arr,searchWord));
        System.out.printf("二分法查找%d的次数是%d",searchWord,binarySearch(arr,searchWord));
    }
    
    /**
     * 普通循环查找
     * @param arr
     * @param searchWord
     * @return
     */
    //普通的循环法，最少需要比较一次，比如查找1，最多需要比较15次，比如8721
    static int genetalLoop(int[] arr,int searchWord){
        int searchCount = 0;
        for(int i=0;i<arr.length;i++){ 
            searchCount++; 
            if (searchWord==arr[i]) 
            break; 
        } 
        return searchCount;         
    }
    
    /**
     * 二分法查找
     * @param arr
     * @param searchWord
     * @return
     */
    static int binarySearch(int[] arr,int searchWord){
        Arrays.sort(arr);   //先对传进来的数组进行排序
        System.out.println("\n"+Arrays.toString(arr));
        //二分法查找 
        int iIndex=0;  
        int iStart=0;  
        int iEnd=arr.length-1; 
        int searchCount = 0;
        for(int i=0;i<arr.length/2;i++) { 
            searchCount++; 
            iIndex = (iStart+iEnd)/2; 
            if(arr[iIndex]<searchWord){ 
                System.out.println("aa");
                iStart = iIndex; 
            }else if(arr[iIndex]>searchWord){
                System.out.println("bb");
                iEnd = iIndex; 
            }else{ 
                break; 
            } 
        } 
        return searchCount; 
    }
    
    @Test
    public void testSort() {
        int[] values = { 3, 1, 6, 2, 9, 0, 7, 4, 5,8 };
        sort(values);
        System.out.println(Arrays.toString(values)); 
    }
    
    /**
     * 冒泡排序
     * @param values
     */
    static void sort(int[] values) {
        int temp;
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values.length - 1- i ; j++) {
                if (values[j] > values[j + 1]) {
                    temp = values[j];
                    values[j] = values[j + 1];
                    values[j + 1] = temp;
                }
            }
        }
    }
    
}
