/*
 * This file is part of one of the Data-Cockpit libraries.
 * 
 * Copyright (C) 2024 ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE (EPFL)
 * 
 * Author - Sébastien Rumley (sebastien.rumley@hefr.ch)
 * 
 * This open source release is made with the authorization of the EPFL,
 * the institution where the author was originally employed.
 * The author is currently affiliated with the HEIA-FR, which is the actual publisher.
 * 
 * The Data-Cockpit program is free software, you can redistribute it and/or modify
 * it under the terms of GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * The Data-Cockpit program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 * 
 * Contributor list -
 */
package ch.heiafr.isc.datacockpit.general_libraries.utils;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeParser {	

	private static Pattern threeDecimalCatchingPattern = Pattern.compile(
	"[^0-9]*([0-9]+)[^0-9]+[^0-9]*([0-9]+)[^0-9]+[^0-9]*([0-9]+)[^0-9]*");

	private static Pattern hexaFormatCatchingPattern = Pattern.compile(
			"[^#]*" + // Any char but not "#" (to consume spaces e.g)
			"#"     + // the # char (starts the sequence)
			"([0-9a-fA-F]{2})" +  // the first couple
			"([0-9a-fA-F]{2})" +  // the second couple
			"([0-9a-fA-F]{2})" +
	"[^0-9a-fA-F]*");  // the third couple

	public static java.awt.Color parseColor(String s) {
		try {
			return new Color(Integer.parseInt(s));
		}
		catch(Exception e) {
		}

		Matcher m = hexaFormatCatchingPattern.matcher(s);
		int red = -1;
		int green = -1;
		int blue = -1;
		if (m.matches()) {
			red = Math.max(0,Math.min(255,Integer.parseInt(m.group(1),16)));
			green = Math.max(0,Math.min(255,Integer.parseInt(m.group(2),16)));
			blue = Math.max(0,Math.min(255,Integer.parseInt(m.group(3),16)));

			return new java.awt.Color(red,green,blue);
		} else {
			Matcher m2 = threeDecimalCatchingPattern.matcher(s);
			if (m2.matches()) {
				red = Math.max(0,Math.min(255,Integer.parseInt(m2.group(1))));
				green = Math.max(0,Math.min(255,Integer.parseInt(m2.group(2))));
				blue = Math.max(0,Math.min(255,Integer.parseInt(m2.group(3))));
				return new java.awt.Color(red,green,blue);
			}
		}
		return null;
	}

	public static Class getRawType(String def) {
		if (def.equals("int")) {
			return Integer.TYPE;
		} else if (def.equals("double")) {
			return Double.TYPE;
		} else if (def.equals("boolean")) {
			return Boolean.TYPE;
		} else if (def.equals("char")) {
			return Character.TYPE;
		} else if (def.equals("long")) {
			return Long.TYPE;
		} else if (def.equals("byte")) {
			return Byte.TYPE;
		} else if (def.equals("short")) {
			return Short.TYPE;
		} else if (def.equals("float")) {
			return Float.TYPE;
		} else {
			return null;
		}		
	}
	
	public static Object parseRawType(String def, String s) {
		if (def.equals("int")) {
			return Integer.parseInt(s);
		} else if (def.equals("double")) {
			return Double.parseDouble(s);
		} else if (def.equals("boolean")) {
			return Boolean.parseBoolean(s);
		} else if (def.equals("char")) {
			return s.charAt(0);
		} else if (def.equals("long")) {
			return Long.parseLong(s);
		} else if (def.equals("byte")) {
			return Byte.parseByte(s);
		} else if (def.equals("short")) {
			return Short.parseShort(s);
		} else if (def.equals("float")) {
			return Float.parseFloat(s);
		} else {
			throw new IllegalStateException();
		}		
	}
	
	public static  int[] parseInt(String s) throws NumberFormatException {
		String parse = s.replace(" ", "");
		List<Integer> ret = new ArrayList<Integer>();
		String[] parts = parse.split(",");
		for (String part : parts){
			ret.addAll(parseIntInterval(part));
		}
		int[] obj = new int[ret.size()];
		int i = 0;
		for (int o : ret) {
			obj[i] = o;
			++i;
		}
		return obj;
	}
	
	public static  long[] parseLong(String s) throws NumberFormatException {
		String parse = s.replace(" ", "");
		List<Long> ret = new ArrayList<Long>();
		String[] parts = parse.split(",");
		for (String part : parts){
			ret.addAll(parseLongInterval(part));
		}
		long[] obj = new long[ret.size()];
		int i = 0;
		for (long o : ret) {
			obj[i] = o;
			++i;
		}
		return obj;
	}
	
	public static short[] parseShort(String s) throws NumberFormatException {
		String parse = s.replace(" ", "");
		List<Short> ret = new ArrayList<Short>();
		String[] parts = parse.split(",");
		for (String part : parts){
			List<Integer> p = parseIntInterval(part);
			for (int i = 0 ; i < p.size() ; i++) {
				ret.add((short)(int)p.get(i));
			}
		}
		short[] obj = new short[ret.size()];
		int i = 0;
		for (short o : ret) {
			obj[i] = o;
			++i;
		}
		return obj;
	}		
	
	public static  float[] parseFloat(String s) throws NumberFormatException {
		String parse = s.replace(" ", "");
		List<Float> ret = new ArrayList<Float>();
		String[] parts = parse.split(",");
		for (String part : parts){
			ret.addAll(parseFloatInterval(part));
		}
		float[] obj = new float[ret.size()];
		int i = 0;
		for (float o : ret) {
			obj[i] = o;
			++i;
		}
		return obj;
	}
	
	public static  double[] parseDouble(String s) throws NumberFormatException {
		String parse = s.replace(" ", "");
		List<Double> ret = new ArrayList<Double>();
		String[] parts = parse.split(",");
		for (String part : parts){
			ret.addAll(parseDoubleInterval(part));
		}
		double[] obj = new double[ret.size()];
		int i = 0;
		for (double o : ret) {
			obj[i] = o;
			++i;
		}
		return obj;
	}
	
	public static  List<Integer> parseIntInterval(String s) throws NumberFormatException {
		if (s.contains("log")) {
			return parseIntegerLogInterval(s);
		}
		if (s.contains("lin")) {
			return parseIntegerLinInterval(s);
		}				
		if (s.contains("pow")) {
			return parseIntegerPowerInterval(s);
		}
		String[] parts = s.split(":");
		List<Integer> ret = new ArrayList<Integer>();
		int first;
		int step;
		int last;
		switch (parts.length) {
		case 1:
			ret.add(Integer.parseInt(parts[0]));
			break;
		case 2:
			first = Integer.parseInt(parts[0]);
			last = Integer.parseInt(parts[1]);
			while (first <= last) {
				ret.add(first);
				++first;
			}
			break;
		case 3:
			first = Integer.parseInt(parts[0]);
			step = Integer.parseInt(parts[1]);
			last = Integer.parseInt(parts[2]);
			while (first <= last) {
				ret.add(first);
				first += step;
			}
			break;
		default:
			throw new NumberFormatException();
		}
		return ret;
	}

	public static  List<Long> parseLongInterval(String s) throws NumberFormatException {
		if (s.contains("log")) {
			return parseLongLogInterval(s);
		}
		if (s.contains("lin")) {
			return parseLongLinInterval(s);
		}		
		String[] parts = s.split(":");
		List<Long> ret = new ArrayList<Long>();
		long first;
		long step;
		long last;
		switch (parts.length) {
		case 1:
			ret.add(Long.parseLong(parts[0]));
			break;
		case 2:
			first = Long.parseLong(parts[0]);
			last = Long.parseLong(parts[1]);
			while (first <= last) {
				ret.add(first);
				++first;
			}
			break;
		case 3:
			first = Long.parseLong(parts[0]);
			step = Long.parseLong(parts[1]);
			last = Long.parseLong(parts[2]);
			while (first <= last) {
				ret.add(first);
				first += step;
			}
			break;
		default:
			throw new NumberFormatException();
		}
		return ret;
	}



	public static  List<Float> parseFloatInterval(String s) throws NumberFormatException {
		if (s.contains("log")) {
			return parseFloatLogInterval(s);
		}	
		if (s.contains("lin")) {
			return parseFloatLinInterval(s);
		}			
		
		String[] parts = s.split(":");
		List<Float> ret = new ArrayList<Float>();
		BigDecimal first;
		BigDecimal step;
		BigDecimal last;
		switch (parts.length) {
		case 1:
			ret.add(Float.parseFloat(parts[0]));
			break;
		case 2:
			first = new BigDecimal(Float.parseFloat(parts[0]));
			last = new BigDecimal(Float.parseFloat(parts[1]));
			while (first.floatValue() <= last.floatValue()) {
				ret.add(first.floatValue());
				first = first.add(BigDecimal.ONE);
			}
			break;
		case 3:
			first = new BigDecimal(Float.parseFloat(parts[0]));
			step = new BigDecimal(Float.parseFloat(parts[1]));
			last = new BigDecimal(Float.parseFloat(parts[2]));
			while (first.floatValue() <= last.floatValue()) {
				ret.add(first.floatValue());
				first = first.add(step);
			}
			break;
		default:
			throw new NumberFormatException();
		}
		return ret;
	}



	public static List<Double> parseDoubleInterval(String s) throws NumberFormatException {
		if (s.contains("log")) {
			return parseDoubleLogInterval(s);
		}
		if (s.contains("lin")) {
			return parseDoubleLinInterval(s);
		}
		String[] parts = s.split(":");
		List<Double> ret = new ArrayList<Double>();
		BigDecimal first;
		BigDecimal step;
		BigDecimal last;
		switch (parts.length) {
		case 1:
			ret.add(Double.parseDouble(parts[0]));
			break;
		case 2:
			first = new BigDecimal(Double.parseDouble(parts[0]));
			last = new BigDecimal(Double.parseDouble(parts[1]));
			while (first.doubleValue() <= last.doubleValue()) {
				ret.add(first.doubleValue());
				first = first.add(BigDecimal.ONE);
			}
			break;
		case 3:
			first = new BigDecimal(Double.parseDouble(parts[0]));
			step = new BigDecimal(Double.parseDouble(parts[1]));
			last = new BigDecimal(Double.parseDouble(parts[2]));
			while (first.doubleValue() <= last.doubleValue()) {
				ret.add(first.doubleValue());
				first = first.add(step);
			}
			break;
		default:
			throw new NumberFormatException();
		}
		return ret;
	}

	private static List<Double> parseDoubleLinInterval(String s) {
		String[] parts = s.split("lin");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Double.parseDouble(interval[0]);
		double end = Double.parseDouble(interval[1]);
		double inc = (end - start)/elements;
		List<Double> ret = new ArrayList<Double>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add(start);
			start += inc;
		}
		return ret;
	}

	private static List<Double> parseDoubleLogInterval(String s) {
		String[] parts = s.split("log");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Math.log(Double.parseDouble(interval[0]));
		double end = Math.log(Double.parseDouble(interval[1]));
		double inc = (end - start)/elements;
		List<Double> ret = new ArrayList<Double>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add(Math.exp(start));
			start += inc;
		}
		return ret;
	}
	
	private static List<Float> parseFloatLogInterval(String s) {
		String[] parts = s.split("log");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Math.log(Double.parseDouble(interval[0]));
		double end = Math.log(Double.parseDouble(interval[1]));
		double inc = (end - start)/elements;
		List<Float> ret = new ArrayList<Float>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add((float)Math.exp(start));
			start += inc;
		}
		return ret;
	}
	
	private static List<Float> parseFloatLinInterval(String s) {
		String[] parts = s.split("lin");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Double.parseDouble(interval[0]);
		double end = Double.parseDouble(interval[1]);
		double inc = (end - start)/elements;
		List<Float> ret = new ArrayList<Float>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add((float)start);
			start += inc;
		}
		return ret;
	}	
	
	private static List<Integer> parseIntegerLogInterval(String s) {
		String[] parts = s.split("log");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Math.log(Double.parseDouble(interval[0]));
		double end = Math.log(Double.parseDouble(interval[1]));
		double inc = (end - start)/elements;
		HashSet<Integer> ret = new HashSet<Integer>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add((int)Math.round(Math.exp(start)));
			start += inc;
		}
		ArrayList<Integer> list = new ArrayList<Integer>(ret);
		Collections.sort(list);
		return list;
	}
	
	private static List<Integer> parseIntegerLinInterval(String s) {
		String[] parts = s.split("lin");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Double.parseDouble(interval[0]);
		double end = Double.parseDouble(interval[1]);
		double inc = (end - start)/elements;
		HashSet<Integer> ret = new HashSet<Integer>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add((int)Math.round(start));
			start += inc;
		}
		ArrayList<Integer> list = new ArrayList<Integer>(ret);
		Collections.sort(list);
		return list;
	}	
	
	private static List<Integer> parseIntegerPowerInterval(String s) {
		String[] parts = s.split("pow");
		double power = Integer.parseInt(parts[1]);
		List<Integer> list = parseIntInterval(parts[0]);
		
		List<Integer> ret = new ArrayList<Integer>();
		for (Integer i : list) {
			ret.add((int)Math.pow(i, power));
		}
		return ret;
		
	/*	String[] interval = parts[0].split(":");
		int start = Integer.parseInt(interval[0]);
		int end = Integer.parseInt(interval[1]);
		HashSet<Integer> ret = new HashSet<Integer>();
		for (int i = start ; i <= end ; i++) {
			ret.add((int)Math.pow(i, power));
		}
		ArrayList<Integer> list = new ArrayList<Integer>(ret);
		return list;	*/	
	}	
	
	private static List<Long> parseLongLogInterval(String s) {
		String[] parts = s.split("log");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Math.log(Double.parseDouble(interval[0]));
		double end = Math.log(Double.parseDouble(interval[1]));
		double inc = (end - start)/elements;
		HashSet<Long> ret = new HashSet<Long>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add((long)Math.exp(start));
			start += inc;
		}
		return new ArrayList<Long>(ret);
	}
	
	private static List<Long> parseLongLinInterval(String s) {
		String[] parts = s.split("lin");
		double elements = Integer.parseInt(parts[1])-1;
		String[] interval = parts[0].split(":");
		double start = Double.parseDouble(interval[0]);
		double end = Double.parseDouble(interval[1]);
		double inc = (end - start)/elements;
		HashSet<Long> ret = new HashSet<Long>();
		for (int i = 0 ; i < elements+1 ; i++) {
			ret.add((long)start);
			start += inc;
		}
		return new ArrayList<Long>(ret);
	}	


	
	
}