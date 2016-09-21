package com.ttv.util;

import java.util.Comparator;

import com.ttv.dao.Product;


public class ProductComparator implements Comparator<Product> {
    @Override
    public int compare(Product o1, Product o2) {
    	int kq = 0;
    	if(o1.distance > o2.distance) kq = 1;
    	else if(o1.distance <  o2.distance) kq = -1;
    	
    
    	
    	//System.out.println(kq);
    	return kq;
    }


}
