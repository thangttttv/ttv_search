package com.ttv.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class VietnameseUtil {
  
  final static public boolean containVietnameseCharacter(String src){
    if (src == null || src.isEmpty()) return false ;
    int length = src.length() ;
    for(int i = 0; i < length; i++){
      char c = src.charAt(i) ;
      if(c <= 'z') continue ;
      if(isVietnameseCharacter(c)) return true;
    }
    return false ;
  }

  final static public boolean isVietnameseCharacter(char c) {
    return removeVietnameseAccent(c) != c ;
  }
  
  final static public String removeVietnameseAccent(String src) {
    if(src == null || src.isEmpty()) return null ;
    StringBuilder b = new StringBuilder() ;
    int length = src.length() ;
    for(int i = 0; i < length; i++) {
      char c = src.charAt(i) ;
      b.append(removeVietnameseAccent(c)) ;
    }
    return b.toString() ;
  }
  
  final static public char toLowcaseVietnamese(char c) {
    if(c <= 'z') return c ;
    switch (c) {
      case 'À': return 'à';
      case 'Á': return 'á';
      case 'Ả': return 'ả';
      case 'Ã': return 'ã';
      case 'Ạ': return 'ạ';
      case 'Ă': return 'ă';
      case 'Ằ': return 'ằ';
      case 'Ắ': return 'ắ';
      case 'Ẳ': return 'ẳ';
      case 'Ẵ': return 'ẵ';
      case 'Ặ': return 'ặ';
      case 'Â': return 'â';
      case 'Ầ': return 'ầ';
      case 'Ấ': return 'ấ';
      case 'Ẩ': return 'ẩ';
      case 'Ẫ': return 'ẫ';
      case 'Ậ': return 'ậ';
      case 'Đ': return 'đ';
      case 'È': return 'è';
      case 'É': return 'é';
      case 'Ẻ': return 'ẻ';
      case 'Ẽ': return 'ẽ';
      case 'Ẹ': return 'ẹ';
      case 'Ê': return 'ê';
      case 'Ề': return 'ề';
      case 'Ế': return 'ế';
      case 'Ể': return 'ể';
      case 'Ễ': return 'ễ';
      case 'Ệ': return 'ệ';
      case 'Ì': return 'ì';
      case 'Í': return 'í';
      case 'Ỉ': return 'ỉ';
      case 'Ĩ': return 'ĩ';
      case 'Ị': return 'ị';
      case 'Ò': return 'ò';
      case 'Ó': return 'ó';
      case 'Ỏ': return 'ỏ';
      case 'Õ': return 'õ';
      case 'Ọ': return 'ọ';
      case 'Ô': return 'ô';
      case 'Ồ': return 'ồ';
      case 'Ố': return 'ố';
      case 'Ổ': return 'ổ';
      case 'Ỗ': return 'ỗ';
      case 'Ộ': return 'ộ';
      case 'Ơ': return 'ơ';
      case 'Ờ': return 'ờ';
      case 'Ớ': return 'ớ';
      case 'Ở': return 'ở';
      case 'Ỡ': return 'ỡ';
      case 'Ợ': return 'ợ';
      case 'Ù': return 'ù';
      case 'Ú': return 'ú';
      case 'Ủ': return 'ủ';
      case 'Ũ': return 'ũ';
      case 'Ụ': return 'ụ';
      case 'Ư': return 'ư';
      case 'Ừ': return 'ừ';
      case 'Ứ': return 'ứ';
      case 'Ử': return 'ử';
      case 'Ữ': return 'ữ';
      case 'Ự': return 'ự';
      case 'Ỳ': return 'ỳ';
      case 'Ý': return 'ý';
      case 'Ỷ': return 'ỷ';
      case 'Ỹ': return 'ỹ';
      case 'Ỵ': return 'ỵ';
      default: return c;
    }
  }

  final static public char removeVietnameseAccent(char c){
    if(c <= 'z') return c ;
    switch (c) {
      case 'À': return 'A';
      case 'à': return 'a';
      case 'Á': return 'A';
      case 'á': return 'a';
      case 'Ả': return 'A';
      case 'ả': return 'a';
      case 'Ã': return 'A';
      case 'ã': return 'a';
      case 'Ạ': return 'A';
      case 'ạ': return 'a';
      case 'Ă': return 'A';
      case 'ă': return 'a';
      case 'Ằ': return 'A';
      case 'ằ': return 'a';
      case 'Ắ': return 'A';
      case 'ắ': return 'a';
      case 'Ẳ': return 'A';
      case 'ẳ': return 'a';
      case 'Ẵ': return 'A';
      case 'ẵ': return 'a';
      case 'Ặ': return 'A';
      case 'ặ': return 'a';
      case 'Â': return 'A';
      case 'â': return 'a';
      case 'Ầ': return 'A';
      case 'ầ': return 'a';
      case 'Ấ': return 'A';
      case 'ấ': return 'a';
      case 'Ẩ': return 'A';
      case 'ẩ': return 'a';
      case 'Ẫ': return 'A';
      case 'ẫ': return 'a';
      case 'Ậ': return 'A';
      case 'ậ': return 'a';
      case 'Đ': return 'D';
      case 'đ': return 'd';
      case 'È': return 'E';
      case 'è': return 'e';
      case 'É': return 'E';
      case 'é': return 'e';
      case 'Ẻ': return 'E';
      case 'ẻ': return 'e';
      case 'Ẽ': return 'E';
      case 'ẽ': return 'e';
      case 'Ẹ': return 'E';
      case 'ẹ': return 'e';
      case 'Ê': return 'E';
      case 'ê': return 'e';
      case 'Ề': return 'E';
      case 'ề': return 'e';
      case 'Ế': return 'E';
      case 'ế': return 'e';
      case 'Ể': return 'E';
      case 'ể': return 'e';
      case 'Ễ': return 'E';
      case 'ễ': return 'e';
      case 'Ệ': return 'E';
      case 'ệ': return 'e';
      case 'Ì': return 'I';
      case 'ì': return 'i';
      case 'Í': return 'I';
      case 'í': return 'i';
      case 'Ỉ': return 'I';
      case 'ỉ': return 'i';
      case 'Ĩ': return 'I';
      case 'ĩ': return 'i';
      case 'Ị': return 'I';
      case 'ị': return 'i';
      case 'Ò': return 'O';
      case 'ò': return 'o';
      case 'Ó': return 'O';
      case 'ó': return 'o';
      case 'Ỏ': return 'O';
      case 'ỏ': return 'o';
      case 'Õ': return 'O';
      case 'õ': return 'o';
      case 'Ọ': return 'O';
      case 'ọ': return 'o';
      case 'Ô': return 'O';
      case 'ô': return 'o';
      case 'Ồ': return 'O';
      case 'ồ': return 'o';
      case 'Ố': return 'O';
      case 'ố': return 'o';
      case 'Ổ': return 'O';
      case 'ổ': return 'o';
      case 'Ỗ': return 'O';
      case 'ỗ': return 'o';
      case 'Ộ': return 'O';
      case 'ộ': return 'o';
      case 'Ơ': return 'O';
      case 'ơ': return 'o';
      case 'Ờ': return 'O';
      case 'ờ': return 'o';
      case 'Ớ': return 'O';
      case 'ớ': return 'o';
      case 'Ở': return 'O';
      case 'ở': return 'o';
      case 'Ỡ': return 'O';
      case 'ỡ': return 'o';
      case 'Ợ': return 'O';
      case 'ợ': return 'o';
      case 'Ù': return 'U';
      case 'ù': return 'u';
      case 'Ú': return 'U';
      case 'ú': return 'u';
      case 'Ủ': return 'U';
      case 'ủ': return 'u';
      case 'Ũ': return 'U';
      case 'ũ': return 'u';
      case 'Ụ': return 'U';
      case 'ụ': return 'u';
      case 'Ư': return 'U';
      case 'ư': return 'u';
      case 'Ừ': return 'U';
      case 'ừ': return 'u';
      case 'Ứ': return 'U';
      case 'ứ': return 'u';
      case 'Ử': return 'U';
      case 'ử': return 'u';
      case 'Ữ': return 'U';
      case 'ữ': return 'u';
      case 'Ự': return 'U';
      case 'ự': return 'u';
      case '.': return ' ';
      case 'Ỳ': return 'Y';
      case 'ỳ': return 'y';
      case 'Ý': return 'Y';
      case 'ý': return 'y';
      case 'Ỷ': return 'Y';
      case 'ỷ': return 'y';
      case 'Ỹ': return 'Y';
      case 'ỹ': return 'y';
      case 'Ỵ': return 'Y';
      case 'ỵ': return 'y';
      default: return c;
    }
  }
  
  public static String convert(String org){
	//convert to VNese no sign. @haidh 2008
	char arrChar[] = org.toCharArray();
	char result[] = new char[arrChar.length];
	for (int i = 0; i < arrChar.length; i++) {
	switch(arrChar[i]){
	case '\u00E1':
	case '\u00E0':
	case '\u1EA3':
	case '\u00E3':
	case '\u1EA1':
	case '\u0103':
	case '\u1EAF':
	case '\u1EB1':
	case '\u1EB3':
	case '\u1EB5':
	case '\u1EB7':
	case '\u00E2':
	case '\u1EA5':
	case '\u1EA7':
	case '\u1EA9':
	case '\u1EAB':
	case '\u1EAD':
	case '\u0203':
	case '\u01CE':
	{
	result[i] = 'a';
	break;
	}
	case '\u00E9':
	case '\u00E8':
	case '\u1EBB':
	case '\u1EBD':
	case '\u1EB9':
	case '\u00EA':
	case '\u1EBF':
	case '\u1EC1':
	case '\u1EC3':
	case '\u1EC5':
	case '\u1EC7':
	case '\u0207':
	{
	result[i] = 'e';
	break;
	}
	case '\u00ED':
	case '\u00EC':
	case '\u1EC9':
	case '\u0129':
	case '\u1ECB':
	{
	result[i] = 'i';
	break;
	}
	case '\u00F3':
	case '\u00F2':
	case '\u1ECF':
	case '\u00F5':
	case '\u1ECD':
	case '\u00F4':
	case '\u1ED1':
	case '\u1ED3':
	case '\u1ED5':
	case '\u1ED7':
	case '\u1ED9':
	case '\u01A1':
	case '\u1EDB':
	case '\u1EDD':
	case '\u1EDF':
	case '\u1EE1':
	case '\u1EE3':
	case '\u020F':
	{
	result[i] = 'o';
	break;
	}
	case '\u00FA':
	case '\u00F9':
	case '\u1EE7':
	case '\u0169':
	case '\u1EE5':
	case '\u01B0':
	case '\u1EE9':
	case '\u1EEB':
	case '\u1EED':
	case '\u1EEF':
	case '\u1EF1':
	{
	result[i] = 'u';
	break;
	}
	case '\u00FD':
	case '\u1EF3':
	case '\u1EF7':
	case '\u1EF9':
	case '\u1EF5':
	{
	result[i] = 'y';
	break;
	}
	case '\u0111':
	{
	result[i] = 'd';
	break;
	}
	case '\u00C1':
	case '\u00C0':
	case '\u1EA2':
	case '\u00C3':
	case '\u1EA0':
	case '\u0102':
	case '\u1EAE':
	case '\u1EB0':
	case '\u1EB2':
	case '\u1EB4':
	case '\u1EB6':
	case '\u00C2':
	case '\u1EA4':
	case '\u1EA6':
	case '\u1EA8':
	case '\u1EAA':
	case '\u1EAC':
	case '\u0202':
	case '\u01CD':
	{
	result[i] = 'A';
	break;
	}
	case '\u00C9':
	case '\u00C8':
	case '\u1EBA':
	case '\u1EBC':
	case '\u1EB8':
	case '\u00CA':
	case '\u1EBE':
	case '\u1EC0':
	case '\u1EC2':
	case '\u1EC4':
	case '\u1EC6':
	case '\u0206':
	{
	result[i] = 'E';
	break;
	}
	case '\u00CD':
	case '\u00CC':
	case '\u1EC8':
	case '\u0128':
	case '\u1ECA':
	{
	result[i] = 'I';
	break;
	}
	case '\u00D3':
	case '\u00D2':
	case '\u1ECE':
	case '\u00D5':
	case '\u1ECC':
	case '\u00D4':
	case '\u1ED0':
	case '\u1ED2':
	case '\u1ED4':
	case '\u1ED6':
	case '\u1ED8':
	case '\u01A0':
	case '\u1EDA':
	case '\u1EDC':
	case '\u1EDE':
	case '\u1EE0':
	case '\u1EE2':
	case '\u020E':
	{
	result[i] = 'O';
	break;
	}
	case '\u00DA':
	case '\u00D9':
	case '\u1EE6':
	case '\u0168':
	case '\u1EE4':
	case '\u01AF':
	case '\u1EE8':
	case '\u1EEA':
	case '\u1EEC':
	case '\u1EEE':
	case '\u1EF0':
	{
	result[i] = 'U';
	break;
	}

	case '\u00DD':
	case '\u1EF2':
	case '\u1EF6':
	case '\u1EF8':
	case '\u1EF4':
	{
	result[i] = 'Y';
	break;
	}
	case '\u0110':
	case '\u00D0':
	case '\u0089':
	{
	result[i] = 'D';
	break;
	}
	default:
	result[i] = arrChar[i];
	}
	}
	return new String(result);
	}


  public static String change(String text)
  {
	String chars[] = {"a","A","e","E","o","O","u","U","i","I","d","D","y","Y"};

	String uni0[] = {"á","à","ạ","ả","ã","â","ấ","ầ","ậ","ẩ","ẫ","ă","ắ","ằ","ặ","ẳ","ẵ","� �"};
	String uni1[] = {"Á","À","Ạ","Ả","Ã","Â","Ấ","Ầ","Ậ","Ẩ","Ẫ","Ă","Ắ","Ằ","Ặ","Ẳ","Ẵ","� �"};
	String uni2[] = {"é","è","ẹ","ẻ","ẽ","ê","ế","ề","ệ","ể","ễ"};
	String uni3[] = {"É","È","Ẹ","Ẻ","Ẽ","Ê","Ế","Ề","Ệ","Ể","Ễ"};
	String uni4[] = {"ó","ò","ọ","ỏ","õ","ô","ố","ồ","ộ","ổ","ỗ","ơ","ớ","ờ","ợ","ở","ỡ","� �","ổ","ỏ"};
	String uni5[] = {"Ó","Ò","Ọ","Ỏ","Õ","Ô","Ố","Ồ","Ộ","Ổ","Ỗ","Ơ","Ớ","Ờ","Ợ","Ở","Ỡ","� �"};
	String uni6[] = {"ú","ù","ụ","ủ","ũ","ư","ứ","ừ","ự","ử","ữ","ụ"};
	String uni7[] = {"Ú","Ù","Ụ","Ủ","Ũ","Ư","Ứ","Ừ","Ự","Ử","Ữ"};
	String uni8[] = {"í","ì","ị","ỉ","ĩ","ị"};
	String uni9[] = {"Í","Ì","Ị","Ỉ","Ĩ"};
	String uni10[] = {"đ"};
	String uni11[] = {"Đ"};
	String uni12[] = {"ý","ỳ","ỵ","ỷ","ỹ"};
	String uni13[] = {"Ý","Ỳ","Ỵ","Ỷ","Ỹ"};
	
	int i = 0;  Pattern p = null;	Matcher m = null;
	
	for(i=0; i<uni0.length; i++) {
		p = Pattern.compile(uni0[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[0]); }
  		
  	}
	
	for(i=0; i<uni1.length; i++) {
		p = Pattern.compile(uni1[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[1]); }
  		
  	}
	
	for(i=0; i<uni2.length; i++) {
		p = Pattern.compile(uni2[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[2]); }
  		
  	}
	
	for(i=0; i<uni3.length; i++) {
		p = Pattern.compile(uni3[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[3]); }
  		
  	}
	
	for(i=0; i<uni4.length; i++) {
		p = Pattern.compile("ó");
		m = p.matcher(text);
		if (m.find()) {System.out.println("ó"); text = m.replaceAll(chars[4]); }
  		
  	}
	
	for(i=0; i<uni5.length; i++) {
		p = Pattern.compile(uni5[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[5]); }
  		
  	}
	
	for(i=0; i<uni6.length; i++) {
		p = Pattern.compile(uni6[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[6]); }
  		
  	}
	
	for(i=0; i<uni7.length; i++) {
		p = Pattern.compile(uni7[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[7]); }
  		
  	}
	
	for(i=0; i<uni8.length; i++) {
		p = Pattern.compile(uni8[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[8]); }
  		
  	}
	
	for(i=0; i<uni9.length; i++) {
		p = Pattern.compile(uni9[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[9]); }
  		
  	}
	
	for(i=0; i<uni10.length; i++) {
		p = Pattern.compile(uni10[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[10]); }
  		
  	}
	
	for(i=0; i<uni11.length; i++) {
		p = Pattern.compile(uni11[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[11]); }
  		
  	}
	
	for(i=0; i<uni12.length; i++) {
		p = Pattern.compile(uni12[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[12]); }
  		
  	}
	
	for(i=0; i<uni13.length; i++) {
		p = Pattern.compile(uni13[i]);
		m = p.matcher(text);
		if (m.find()) { text = m.replaceAll(chars[13]); }
  		
  	}
	
  	return text;
  }
  
  public static void main(String[] args) {
	  try {
		String str = new String("Chàng ngóc bem gái".getBytes(),"ISO8859_1");
		String txt = VietnameseUtil.change(str);
			System.out.println(str);
			System.out.println(txt);
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	 
		
	}
}
