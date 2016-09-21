package com.ttv.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTool {
	static final char NINE = (char) 0x39;
	static final char ZERO = (char) 0x30;
	static final char CH_a = (char) 'a';
	static final char CH_z = (char) 'z';
	static final char CH_A = (char) 'A';
	static final char CH_Z = (char) 'Z';

	public static String appendString(String oldS, int pos, String s) {
		return (oldS.substring(0, pos) + s + oldS.substring(pos));
	}

	// To replace a character at a specified position
	public static String replaceCharAt(String s, int pos, char c) {
		// return s.substring(0, pos) + c + s.substring(pos + 1);
		StringBuffer buf = new StringBuffer(s);
		buf.setCharAt(pos, c);
		return buf.toString();
	}

	// replace char with string
	public static String replaceChar(String s, char a, String b) {
		if (s == null)
			return null;

		StringBuffer newString = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur == a) {
				newString.append(b);
			} else {
				newString.append(cur);
			}
		}
		return newString.toString();
	}

	// To remove a character
	public static String removeChar(String s, char c) {
		if (s == null)
			return null;

		StringBuffer newString = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char cur = s.charAt(i);
			if (cur != c)
				newString.append(cur);
		}
		return newString.toString();
	}

	// To remove a character at a specified position
	public static String removeCharAt(String s, int pos) {
		// return s.substring(0, pos) + s.substring(pos + 1);
		StringBuffer buf = new StringBuffer(s.length() - 1);
		buf.append(s.substring(0, pos)).append(s.substring(pos + 1));
		return buf.toString();
	}

	// .,*/abc --> abc
	public static String removeSpecialCharsInFront(String s) {
		if (s == null)
			return null;
		String result = "";
		char currChar;
		for (int i = 0; i < s.length(); i++) {
			currChar = s.charAt(i);
			if ((currChar >= ZERO && currChar <= NINE)
					|| (currChar >= CH_a && currChar <= CH_z)
					|| (currChar >= CH_A && currChar <= CH_Z)) {
				result = s.substring(i);
				break;
			}
		}
		return result;
	}

	// "a.b-c" --> "abc"
	public static String removeSpecialCharsInString(String s) {
		if (s == null)
			return null;
		StringBuffer buffer = new StringBuffer();
		char ch;
		for (int i = 0; i < s.length(); i++) {
			ch = s.charAt(i);
			if ((ch >= ZERO && ch <= NINE) || (ch >= CH_a && ch <= CH_z)
					|| (ch >= CH_A && ch <= CH_Z)) {
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}

	public static String onlyOneSpaceBetween2Words(String text) {
		if (text == null)
			return null;

		StringBuffer buffer = new StringBuffer();
		boolean lastCharIsSpace = false;
		for (int i = 0; i < text.length(); i++) {
			char ch = text.charAt(i);
			if (ch == 0x20) {
				if (lastCharIsSpace) {
					continue;
				} else {
					lastCharIsSpace = true;
				}
			} else if (lastCharIsSpace) {
				lastCharIsSpace = false;
			}
			buffer.append(ch);
		}
		return buffer.toString();
	}

	/*
	 * In text: a string having some seperator(s) Out a collection of elements
	 * without (between) seperator
	 */
	public static Collection parseString(String text, String seperator) {
		Vector vResult = new Vector();
		if (text == null || "".equals(text))
			return vResult;

		String tempStr = text.trim();
		String currentLabel = null;

		int index = tempStr.indexOf(seperator);
		while (index != -1) {
			currentLabel = tempStr.substring(0, index).trim();
			// Only accept not null element
			if (!"".equals(currentLabel))
				vResult.addElement(currentLabel);
			tempStr = tempStr.substring(index + 1);
			index = tempStr.indexOf(seperator);
		}
		// Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel))
			vResult.addElement(currentLabel);
		return vResult;
	}

	final static String[] seperators = { " ", ".", ",", "-", "_", "=" };

	public static Collection parseString(String text) {
		Vector vResult = new Vector();
		if (text == null || "".equals(text))
			return vResult;

		String tempStr = text.trim();
		String currentLabel = null;

		int index = getNextIndex(tempStr);
		while (index != -1) {
			currentLabel = tempStr.substring(0, index).trim();
			// Only accept not null element
			if (!"".equals(currentLabel))
				vResult.addElement(currentLabel);
			tempStr = tempStr.substring(index + 1);
			index = getNextIndex(tempStr);
		}
		// Last label
		currentLabel = tempStr.trim();
		if (!"".equals(currentLabel))
			vResult.addElement(currentLabel);
		return vResult;
	}

	private static int getNextIndex(String text) {
		int index = 0;
		int newIdx = 0;
		boolean hasOne = false;
		for (int i = 0; i < seperators.length; i++) {
			newIdx = text.indexOf(seperators[i]);
			if (!hasOne) {
				if (newIdx != -1) {
					index = newIdx;
					hasOne = true;
				}
			} else if (newIdx != -1) {
				if (newIdx < index) {
					index = newIdx;
				}
			}
		}
		if (!hasOne)
			index = -1;
		return index;
	}

	/* Seperator is any charactor not in rage of (0-9), (a-z), (A-Z) */
	public static Collection parseStringEx(String text) {
		Vector vResult = new Vector();
		if (text == null || "".equals(text))
			return vResult;

		String tempStr = text.trim();
		String currLabel = "";
		char currChar = 0;
		for (int i = 0; i < tempStr.length(); i++) {
			currChar = tempStr.charAt(i);
			if ((currChar >= ZERO && currChar <= NINE)
					|| (currChar >= CH_a && currChar <= CH_z)
					|| (currChar >= CH_A && currChar <= CH_Z)) {
				currLabel = currLabel + currChar;
			} else if (currLabel.length() > 0) {
				vResult.add(currLabel);
				currLabel = new String("");
			}
		}
		// last label
		if (currLabel.length() > 0) {
			vResult.add(currLabel);
		}
		return vResult;
	}

/*	public static boolean isNumberic(String sNumber) {
		if (sNumber == null || "".equals(sNumber)) {
			return false;
		}
		char ch_max = (char) 0x39;
		char ch_min = (char) 0x30;
		char dc = '.';
		char dp = ',';
		

		for (int i = 0; i < sNumber.length(); i++) {
			char ch = sNumber.charAt(i);
			if (((ch < ch_min) || (ch > ch_max))||(ch!=dc)||(ch!=dp)) {
				return false;
			}
		}
		return true;
	}*/
	
	public static boolean isNumeric(String string) {
	      return string.matches("^[-+]?\\d+(\\.\\d+)?$");
	  }
	
	public static boolean isDPEND(String string) {
	      return string.matches(",$");
	  }
	
	public static boolean isPhoneValid(String phone) {
		    boolean retval = false;
		    String phoneNumberPattern = "(\\d-)?(\\d{3}-)?\\d{3}-\\d{4}";
		    retval = phone.matches(phoneNumberPattern);
		    return retval;
		  }

	/** *********************************************************************** */
	/* GENERATE RANDOM STRING OF CHARACTERS */
	/** *********************************************************************** */
	private static char[] charArray = null; // Holds an array of character (used
	// to get the random character for
	// the random string)
	private static Random random = null; // random object
	// Create an arrays of characters (A--Z, 0--9)
	static {
		int numOfChars = 'Z' - 'A' + 1;
		int numOfDigits = '9' - '0' + 1;

		random = new Random(); // create a random object

		charArray = new char[numOfChars + numOfDigits];
		for (int i = 0; i < numOfChars; i++) {
			charArray[i] = (char) ('A' + i);
		}
		for (int i = 0; i < numOfDigits; i++) {
			charArray[numOfChars + i] = (char) ('0' + i);
		}
		// System.out.println(charArray);
	}

	// returns a random string of chars: A--Z, 0--9
	public String generateRandomString(int length) {
		char[] ch = new char[length];
		for (int i = 0; i < length; i++)
			ch[i] = charArray[random.nextInt(charArray.length)];
		return new String(ch);
	}

	/***************************************************************************
	 * Method replaceString
	 * 
	 * @author Le Anh Tuan
	 * 
	 * Replace substring oldStr in string sStr by newStr
	 * 
	 * @param sStr
	 * @param oldStr
	 * @param newStr
	 * @return String
	 **************************************************************************/
	public static String replaceString(String sStr, String oldStr, String newStr) {
		sStr = (sStr == null ? "" : sStr);
		String strVar = sStr;
		String tmpStr = "";
		String finalStr = "";
		int stpos = 0, endpos = 0, strLen = 0;
		while (true) {
			strLen = strVar.length();
			stpos = 0;
			endpos = strVar.indexOf(oldStr, stpos);
			if (endpos == -1)
				break;
			tmpStr = strVar.substring(stpos, endpos);
			tmpStr = tmpStr.concat(newStr);
			strVar = strVar.substring(
					endpos + oldStr.length() > sStr.length() ? endpos : endpos
							+ oldStr.length(), strLen);
			finalStr = finalStr.concat(tmpStr);
			stpos = endpos;
		}
		finalStr = finalStr.concat(strVar);
		return finalStr;
	}

	/***************************************************************************
	 * Method isEmptyOrNul
	 * 
	 * @author Le Anh Tuan
	 * @param input
	 * @return boolean
	 **************************************************************************/
	public static boolean isEmptyOrNul(String input) {
		if (input == null)
			return true;
		if ("".equals(input.trim()))
			return true;
		return false;
	}

	public static String collectionBigDecimal2String(Collection c) {
		StringBuffer stringBuff = new StringBuffer();
		if (c == null)
			return null;
		if (c.isEmpty())
			return null;
		BigDecimal big = null;
		for (Iterator it = c.iterator(); it.hasNext();) {
			try {
				big = (BigDecimal) it.next();
				if (big == null)
					continue;
				stringBuff.append(big.toString());
				stringBuff.append(",");
			} catch (Exception e) {
			}
		}
		String string = (stringBuff.toString()).trim();
		if (string.endsWith(","))
			string = string.substring(0, string.length() - 1);
		return string;
	}

	public static String strip(String text, String character) {
		if (text == null) {
			return null;
		}

		int y = text.lastIndexOf(character);
		return text.substring(y + 1, text.length());

	}

	/***************************************************************************
	 * @author Bui Anh Dung
	 **************************************************************************/

	public static String replaceString(String text, final String[] pattern,
			final String[] replace) {
		int startIndex;
		int foundIndex;
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < pattern.length; i++) {
			startIndex = 0;
			result.setLength(0);
			while ((foundIndex = text.indexOf(pattern[i], startIndex)) >= 0) {
				result.append(text.substring(startIndex, foundIndex));
				result.append(replace[i]);
				startIndex = foundIndex + pattern[i].length();
			}
			result.append(text.substring(startIndex));
			text = result.toString();
		}
		return text;
	}

	/**
	 * This method uses to format content of FCKeditor before update to database
	 */

	public static String format(String str) {
		final String[] extended_ansi_html = { "&trade;", "&#8209;", "&nbsp;",
				"&iexcl;", "&cent;", "&pound;", "&curren;", "&yen;",
				"&brvbar;", "&sect;", "&uml;", "&copy;", "&ordf;", "&laquo;",
				"&not;", "&shy;", "&reg;", "&macr;", "&deg;", "&plusmn;",
				"&sup2;", "&sup3;", "&acute;", "&micro;", "&para;", "&middot;",
				"&cedil;", "&sup1;", "&ordm;", "&raquo;", "&frac14;",
				"&frac12;", "&frac34;", "&iquest;", "&Agrave;", "&Aacute;",
				"&Acirc;", "&Atilde;", "&Auml;", "&Aring;", "&AElig;",
				"&Ccedil;", "&Egrave;", "&Eacute;", "&Ecirc;", "&Euml;",
				"&Igrave;", "&Iacute;", "&Icirc;", "&Iuml;", "&ETH;",
				"&Ntilde;", "&Ograve;", "&Oacute;", "&Ocirc;", "&Otilde;",
				"&Ouml;", "&times;", "&Oslash;", "&Ugrave;", "&Uacute;",
				"&Ucirc;", "&Uuml;", "&Yacute;", "&THORN;", "&szlig;",
				"&agrave;", "&aacute;", "&acirc;", "&atilde;", "&auml;",
				"&aring;", "&aelig;", "&ccedil;", "&egrave;", "&eacute;",
				"&ecirc;", "&euml;", "&igrave;", "&iacute;", "&icirc;",
				"&iuml;", "&eth;", "&ntilde;", "&ograve;", "&oacute;",
				"&ocirc;", "&otilde;", "&ouml;", "&divide;", "&oslash;",
				"&ugrave;", "&uacute;", "&ucirc;", "&uuml;", "&yacute;",
				"&thorn;", "&yuml;" };
		final String[] extended_ansi = { "\u0099", "\u2011", "\u00A0",
				"\u00A1", "\u00A2", "\u00A3", "\u00A4", "\u00A5", "\u00A6",
				"\u00A7", "\u00A8", "\u00A9", "\u00AA", "\u00AB", "\u00AC",
				"\u00AD", "\u00AE", "\u00AF", "\u00B0", "\u00B1", "\u00B2",
				"\u00B3", "\u00B4", "\u00B5", "\u00B6", "\u00B7", "\u00B8",
				"\u00B9", "\u00BA", "\u00BB", "\u00BC", "\u00BD", "\u00BE",
				"\u00BF", "\u00C0", "\u00C1", "\u00C2", "\u00C3", "\u00C4",
				"\u00C5", "\u00C6", "\u00C7", "\u00C8", "\u00C9", "\u00CA",
				"\u00CB", "\u00CC", "\u00CD", "\u00CE", "\u00CF", "\u00D0",
				"\u00D1", "\u00D2", "\u00D3", "\u00D4", "\u00D5", "\u00D6",
				"\u00D7", "\u00D8", "\u00D9", "\u00DA", "\u00DB", "\u00DC",
				"\u00DD", "\u00DE", "\u00DF", "\u00E0", "\u00E1", "\u00E2",
				"\u00E3", "\u00E4", "\u00E5", "\u00E6", "\u00E7", "\u00E8",
				"\u00E9", "\u00EA", "\u00EB", "\u00EC", "\u00ED", "\u00EE",
				"\u00EF", "\u00F0", "\u00F1", "\u00F2", "\u00F3", "\u00F4",
				"\u00F5", "\u00F6", "\u00F7", "\u00F8", "\u00F9", "\u00FA",
				"\u00FB", "\u00FC", "\u00FD", "\u00FE", "\u00FF" };

		return replaceString(str, extended_ansi_html, extended_ansi);
	}

	public static String encode(String s, String enc) {
		StringBuffer out = new StringBuffer(s.length());
		String unreserved = new String(
				"-_.!~*'()ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz 0123456789");
		int caseUpper = ('a' - 'A');
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		OutputStreamWriter writer;
		try {
			writer = new OutputStreamWriter(buf, enc);
		} catch (UnsupportedEncodingException e) {
			// use JVM's default encoding
			writer = new OutputStreamWriter(buf);
		}

		for (int i = 0; i < s.length(); i++) {
			int c = (int) s.charAt(i);
			if (unreserved.indexOf(c) != -1) {
				if (c == ' ') {
					c = '+';
				}
				out.append((char) c);
			} else {
				// convert a character to the specified encoding
				// before hex conversion
				try {
					writer.write(c);
					writer.flush();
				} catch (IOException e) {
					buf.reset();
					continue;
				}
				byte[] ba = buf.toByteArray();
				for (int j = 0; j < ba.length; j++) {
					out.append('%');
					char ch = Character.forDigit((ba[j] >> 4) & 0xF, 16);
					if (Character.isLetter(ch)) {
						ch -= 'a' - 'A';
					}
					out.append(ch);
					ch = Character.forDigit(ba[j] & 0xF, 16);
					if (Character.isLetter(ch)) {
						ch -= caseUpper;
					}
					out.append(ch);
				}
				buf.reset();
			}
		}

		return out.toString();
	}

	public static Integer[] arrayStringToIntegers(String[] items) {
		int i = 0;
		Integer[] ids = new Integer[items.length];
		for (String item : items) {
			ids[i] = Integer.parseInt(item);
			i++;
		}
		return ids;
	}

	public static Long[] arrayStringToLongs(String[] items) {
		int i = 0;
		Long[] ids = new Long[items.length];
		for (String item : items) {
			ids[i] = Long.parseLong(item);
			i++;
		}
		return ids;
	}
	
	public static String getFileType(String url){
		int periodIndex = url.lastIndexOf('.');
		return url.substring(periodIndex+1, url.length());
	}
	
	public static boolean isValidEmailAddress(String email) {
	    String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
	    return email.matches(EMAIL_REGEX);
	  }
	
	public enum FileType {
		doc,rtf, pdf, xls,txt,zip,rar,ppt;
		String getTypeImageFile(String file){		
		switch(this) {
			case doc: return "doc.png";
			case rtf: return "doc.png";
			default: return "attach.gif";			
			}
		}
	}
	
	public static String getTypeImageFile(String file)
	{
		
		String type=getFileType(file); 
		String fileType = "attach.gif";				
		switch (type.hashCode()) {
		case 99640:
			fileType = "doc.png";
			break;
		case 3088960:
			fileType = "doc.png";
			break;
		case 113252:
			fileType = "doc.png";
			break;	
		case 111220:
			fileType = "ppt.png";	
			break;	
		case 118783:
			fileType = "xls.png";
			break;
		case 3682393:
			fileType = "xls.png";
			break;
		case 110834:
			fileType = "pdf.png";
			break;
		case 115312:
			fileType = "txt.png";		
			break;
		case 120609:
			fileType = "zip.png";	
			break;
		case 112675:
			fileType = "zip.png";
			break;
		default:
			fileType = "attach.gif";
			break;
		}
		return fileType;
	}
	
	public static String stripLucene(String value)
	{
		if(!StringTool.isEmptyOrNul(value)){
			Pattern pattern = Pattern.compile("\\?|\\*|\\(|\\)|\\{|\\}|\\+|-|^|!|:|~|/");
			Matcher matcher = pattern.matcher(value);
			value = matcher.replaceAll(" ");
		}
		return value.trim();
	}
	
	public static void main(String args[]) {

		/*
		 * double myNumber = 56551234.00; NumberFormat form=
		 * NumberFormat.getInstance(); // form.setGroupingUsed(true);
		 * //form.setParseIntegerOnly(true); System.out.print(" -> " +
		 * NumberFormat.getInstance().format(myNumber));
		 */
		
		//if(StringTool.isDPEND("fdd,"));
		System.out.println("doc=" + "doc".hashCode());
		System.out.println("doc=" + StringTool.stripLucene("ngay nha giao viet nam 20/11"));

	}
}
