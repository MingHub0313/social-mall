package com.zmm.common.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * @author 900045
 * @description:
 * @name StringUtil
 * @date By 2021-02-24 15:03:45
 */
public class StringUtil {

	private static final Logger LOGGER           = LoggerFactory.getLogger(StringUtil.class);
	
	/** code长度*/
	public static int               len              = 6;

	private StringUtil() {

	}

	/**
	 * 字串是否为空
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		} else if (str.length() == 0) {
			return true;
		} else if ("NULL".equals(str.toUpperCase())) {
			return true;
		}
		return false;
	}

	/**
	 * 字串是否为 null "   " ""
	 *
	 * @param str
	 * @return
	 */
	public static boolean isBlank(String str) {
		if (str == null) {
			return true;
		} else if (str.trim().length() == 0) {
			return true;
		}
		return false;
	}

	public static boolean isNotBlank(String str) {
		return !isBlank(str);
	}

	/**
	 * 字串是否为空
	 *
	 * @param str
	 * @return
	 */
	public static String checkEmpty(String str) {
		if (str == null) {
			return "";
		} else if (str.length() == 0) {
			return "";
		}
		return str;
	}

	/**
	 * 全替换
	 *
	 * @param src
	 *            替换字串
	 * @param tar
	 *            替换目标
	 * @param str
	 *            主字串
	 * @return
	 */
	public static String replaceAll(String src, String tar, String str) {
		StringBuilder sb = new StringBuilder();
		byte bytesSrc[] = src.getBytes();

		byte bytes[] = str.getBytes();
		int point = 0;
		for (int i = 0; i < bytes.length; i++) {

			if (isStartWith(bytes, i, bytesSrc, 0)) {

				sb.append(new String(bytes, point, i));
				sb.append(tar);
				i += bytesSrc.length;
				point = i;
			}

		}
		sb.append(new String(bytes, point, bytes.length));
		return sb.toString();
	}

	/**
	 *
	 * @param bytesSrc
	 * @param bytesTar
	 * @return
	 */
	private static boolean isStartWith(byte bytesSrc[], int startSrc, byte bytesTar[],
									   int startTar) {

		for (int j = startTar; j < bytesTar.length; j++) {
			if (bytesSrc[startSrc + j] != bytesTar[j]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 取中文拼音首字符
	 *
	 * @param str
	 * @return
	 */
    /*   public static char getFirstLetterFromChinessWord(String str) {
        char result = '*';
        String temp = str.toUpperCase();
        try {
            byte[] bytes = temp.getBytes("utf-8");
            if (bytes[0] < 128 && bytes[0] > 0) {
                return (char) bytes[0];
            }
    
            int gbkIndex = 0;
    
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] -= 160;
            }
            gbkIndex = bytes[0] * 100 + bytes[1];
            for (int i = 0; i < allEnglishLetter.length; i++) {
                if (i == 22) {
                }
                if (gbkIndex >= allChineseScope[i] && gbkIndex < allChineseScope[i + 1]) {
                    result = allEnglishLetter[i];
                    break;
                }
            }
    
        } catch (Exception e) {
    
        }
        return result;
    }*/

	/** UTF8 */
	public static String toUtf8String(String src) {
		byte[] b = src.getBytes();
		char[] c = new char[b.length];
		for (int i = 0; i < b.length; i++) {
			c[i] = (char) (b[i] & 0x00FF);
		}
		return new String(c);
	}

	/**
	 *
	 * 方法名: misspellDelsql 方法描述: 拼装删除语句sql的方法，供存储过程调用 修改时间：May 22, 2012 6:21:30
	 * PM 参数 @param cbv 参数 @return 参数说明 返回类型 String 返回类型 
	 */

	/**
	 *
	 * 方法名: misspellDelsql 方法描述: 拼装删除语句sql的方法，供存储过程调用 修改时间：May 22, 2012 6:21:30
	 * PM 参数 @param cbv 参数 @return 参数说明 返回类型 String 返回类型 
	 */

	/**
	 *
	 * 方法名: misspellDelsql 方法描述: 拼装批量插入字段的值的处理，供存储过程调用 修改时间：May 22, 2012 6:21:30
	 * PM 参数 @param cbv 参数 @return 参数说明 返回类型 String 返回类型 
	 */

	/**
	 *
	 * 方法名: misspellDelsql 方法描述: 拼装批量插入字段的值的处理，取数组的指定字段 修改时间：May 22, 2012
	 * 6:21:30 PM 参数 @param cbv 参数 @return 参数说明 返回类型 String 返回类型 
	 */

	public static String misspellString(Collection<String> cbv, String split) {
		if (cbv != null && cbv.size() > 0) {
			StringBuilder delWhereSql = new StringBuilder("");
			for (String key : cbv) {
				delWhereSql.append(key);
				delWhereSql.append(split);
			}
			return (delWhereSql.substring(0, delWhereSql.length() - split.length())).toString();
		} else {
			return "";
		}
	}

	/**
	 *
	 * 方法名: misspellString 方法描述: lable标签分隔符替换 修改时间：2015-11-17 下午5:39:21
	 * 参数 @param str 参数 @param regex 参数 @param replacement 参数 @return 参数说明 返回类型
	 * String 返回类型 
	 */
	public static String misspellString(String str, String regex, String replacement) {
		if (isBlank(str)) {
			return "";
		} else if (isBlank(regex) || isBlank(replacement)) {
			return str;
		} else {
			str = str.replaceAll(regex, replacement);
			if ("\\|\\|".equals(replacement)) {
				str = "|" + str + "|";
			} else
				str = str.replaceAll("\\|", "");
			return str;
		}

	}

	/**
	 *
	 * 方法名: trim 方法描述: 去左右空格 修改时间：Jun 27, 2012 2:34:04 PM 参数 @param str
	 * 参数 @return 参数说明 返回类型 String 返回类型 
	 */
	public static String trim(String str) {
		if (str == null || str.equals("")) {
			return str;
		} else {
			return str.replaceAll("^[　 ]+|[　 ]+$", "");
		}
	}

	/**
	 *
	 * 方法名: getRandom 方法描述: 获取6位随机数 修改时间：2012-7-16 上午11:47:00 参数 @return 参数说明
	 * 返回类型 String 返回类型 
	 */
	public static String getRandom(int count) {
		String str = "";
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			str += random.nextInt(10);
		}
		return str;
	}

	public static String replaceBlank(String str) {
		Pattern p = Pattern.compile("//s*|/t|/r|/n");
		Matcher m = p.matcher(str);
		// 去除字符串中的空格,回车,换行符,制表符;
		String after = m.replaceAll("").replaceAll("\\s*|\\t|\\r|\\n", "");
		return after;
	}

	public static String replaceHtml(String str) {
		// 剔出了<html>的标签
		str = str.replaceAll("<([^>]*)>", "").replaceAll("/s", "&nbsp;").replaceAll(">", "&gt;")
				.replaceAll("<", "&lt;").replaceAll("\'", "&#39;").replaceAll("\"", "&quot;")
				.replaceAll("…", "&hellip;").replaceAll("“", "&ldquo;").replaceAll("”", "&rdquo;");
		// 去除字符串中的空格,回车,换行符,制表符
		str = str.replaceAll("\\s+|\\t+|\\r+|\\n+", " ");
		return str;

	}

	/**
	 * 将字符串类型转换为整型
	 *
	 * @param s
	 * @return
	 */
	public static Integer parseInt(String s) {
		if (s != null && s.trim().matches("^(-)?[0-9]+$"))
			return Integer.parseInt(s.trim());
		return 0;
	}

	/**
	 *
	 * 方法名: getDistinct 方法描述: 数组剔重 修改时间：2013-6-14 下午04:48:34 参数 @param num
	 * 参数 @return 参数说明 返回类型 String[] 返回类型 
	 */
	public static String[] getDistinct(String num[]) {
		List<String> list = new java.util.ArrayList<String>();
		for (int i = 0; i < num.length; i++) {
			// 如果list数组不包括num[i]中的值的话，就返回true。
			if (!list.contains(num[i])) {
				// 在list数组中加入num[i]的值。已经过滤过。
				list.add(num[i]); 
			}
		}
		// toArray（数组）方法返回数组。并要指定Integer类型。new
		return list.toArray(new String[0]);
		// integer[o]的空间大小不用考虑。因为如果list中的长度大于0（你integer的长度），toArray方法会分配一个具有指定数组的运行时类型和此列表大小的新数组。
	}

	/**
	 *
	 * 方法名: decode 方法描述: 将字符串解码为UTF-8 修改人：zhengshan 修改时间：Apr 18, 2014 4:19:41 PM
	 * 参数 @param str 参数 @return 参数说明 返回类型 String 返回类型 
	 */
	public static String decodeToUTF8(String str) {
		if (null == str) {
			return str;
		}

		try {
			return URLDecoder.decode(str, "utf-8");
		} catch (UnsupportedEncodingException e) {
			LOGGER.error("", e);
		}

		return str;
	}

	/**
	 * 2014/05/09 去除特殊字符 started
	 * @param str
	 * @return
	 * @throws PatternSyntaxException
	 */
	public static String StringFilter(String str) throws PatternSyntaxException {
		// 只允许字母和数字
		// String regEx = "[^a-zA-Z0-9]";
		// 清除掉所有特殊字符
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	/**
	 * 方法名: checkAlias
	 * 方法描述: 判断昵称中是否含有特殊字符
	 * 修改时间：2017年10月16日 上午10:51:08 
	 * 参数 @param alias
	 * 参数 @return 参数说明
	 * 返回类型 boolean 返回类型
	 */
	@SuppressWarnings("static-access")
	public static boolean checkAlias(String alias) {
		String regEx = "[`~!@#$%^&*()+=|{}':;',//[//].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
		Pattern p = Pattern.compile(regEx);
		return p.matches(regEx, alias);
	}

	/**
	 * 只保留中文数字及常用中文句符， 空白也会保留
	 *
	 * @param str
	 * @return
	 *  PatternSyntaxException
	 */
	public static String StringHandler(String str) {
		if (StringUtils.isBlank(str)) {
			return str;
		}
		Pattern p = Pattern.compile("([\u4e00-\u9fa5_a-zA-Z0-9|\\s|!|\\?|,|，|\\.|。|？|\\/]*)");
		Matcher m = p.matcher(str);
		StringBuffer sb = new StringBuffer();
		String find = "";
		while (m.find()) {
			find = m.group(1);
			sb.append(find);
		}
		return sb.toString();
	}

	// luning 2014/05/09 去除特殊字符 end

	/**
	 * 将字符串类型转换为整型
	 *
	 * @param s
	 * @return
	 */
	public static Long parseLong(String s) {
		if (s != null && s.trim().matches("^(-)?[0-9]+$"))
			return Long.parseLong(s.trim());
		return 0L;
	}

	/**
	 * 将字符串类型转换为short
	 *
	 * @param s
	 * @return
	 */
	public static short parseShort(String s) {
		if (s != null && s.trim().matches("^(-)?[0-9]+$"))
			return Short.parseShort(s.trim());
		return 0;
	}

	/**
	 *
	 * 方法名: randomNumber 方法描述: 在max和min范围取随机数 修改时间：Aug 25, 2014 4:20:07 PM
	 * 参数 @param max 参数 @param min 参数 @return 参数说明 返回类型 int 返回类型 
	 */
	public static int randomNumber(int max, int min) {
		if (max == min) {
			return 0;
		} else {
			int randomNumber = (int) Math.round(Math.random() * (max - min) + min);
			return randomNumber;
		}
	}

	/**
	 *
	 * 方法名: isExistFile 方法描述: 判断文件是否存在 修改时间：Sep 10, 2014 1:43:08 PM 参数 @param
	 * path 参数 @param fileName 参数 @return 参数说明 返回类型 boolean 返回类型 
	 */
	public static boolean isExistFile(String path, String fileName) {
		File file = new File(path + "/" + fileName);
		return file.exists();

	}

	/**
	 *
	 * 方法名: hasString 方法描述: 判断字符串数组是否包含某个字符串 创建人：yangwei 修改时间：Sep 24, 2014
	 * 9:26:05 PM @param 字符串，字符串数组 @return boolean 
	 */
	public static boolean hasString(String str, String[] strArr) {
		if (strArr != null && str != null) {
			for (String tempStr : strArr) {
				if (str.equals(tempStr)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * 方法名: IgnoreCaseReplace 方法描述: 字符串不区分大小写替换 创建人：yangwei 修改时间：Sep 28, 2014
	 * 4:02:42 PM @param source 数据源，oldstring 需要替换的字符串，newstring 替换成的字符串 @return
	 * 返回替换之后的字符串 
	 */
	public static String IgnoreCaseReplace(String source, String oldstring, String newstring) {
		String ret = oldstring;
		try {
			Pattern p = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(source);
			ret = m.replaceAll(newstring);
		} catch (RuntimeException e) {
			LOGGER.error(" IgnoreCaseReplace E :{},{},{} ", source, oldstring, newstring, e);
		}
		return ret;
	}

	/**
	 *
	 * 方法名: html 方法描述: 转译html代码 创建人：yangwei 修改时间：Oct 10, 2014 5:16:47 PM @param
	 * 原字符串 @return 转译之后的字符串 
	 */
	public static String html(String content) {
		if (content == null)
			return "";
		String html = content;

		html = html.replaceAll("&", "&amp;").replaceAll("\"", "&quot;")
				.replaceAll("\t", "&nbsp;&nbsp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;");

		return html;
	}

	/**
	 *
	 * 方法名: getExtention 方法描述: 获取文件类型 修改时间：2015-7-17 上午10:40:14 参数 @param
	 * fileName 参数 @return 参数说明 返回类型 String 返回类型 
	 */
	public static String getExtention(String fileName) {
		int pos = fileName.lastIndexOf(".");
		if (pos == -1)
			return "";
		return fileName.substring(pos);
	}

	/**
	 *
	 * 方法名: parseDouble 方法描述: 字符串转小数 修改时间：2015-7-21 下午8:30:35 参数 @param str
	 * 参数 @return 参数说明 返回类型 double 返回类型 
	 */
	public static double parseDouble(String str) {
		if (isNotBlank(str)) {
			try {
				return Double.parseDouble(str.trim());
			} catch (NumberFormatException e) {
				LOGGER.error(" parseDouble E :{} ", str, e);
			}
		}
		return 0;
	}

	/**
	 * 把价格转换为分
	 *
	 * @param str
	 * @return
	 */
	public static String getMinPrice(String str) {

		if (StringUtil.isBlank(str)) {
			return "0";
		}
		return new StringBuilder().append(parseDouble(str) * 100).toString();
	}

	/**
	 *
	 * 方法名: floor 方法描述: 取下线 修改时间：2015-7-22 下午4:11:52 参数 @param str 参数 @return
	 * 参数说明 返回类型 int 返回类型 
	 */
	public static int floor(String str) {
		if (str != null) {
			return (int) Math.floor(parseDouble(str));
		}
		return 0;

	}

	public static String string2Json(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\"':
					sb.append("\\\"");
					break;
				case '\\':
					sb.append("\\\\");
					break;
				case '/':
					sb.append("\\/");
					break;
				case '\b':
					sb.append("\\b");
					break;
				case '\f':
					sb.append("\\f");
					break;
				case '\n':
					sb.append("\\n");
					break;
				case '\r':
					sb.append("\\r");
					break;
				case '\t':
					sb.append("\\t");
					break;
				default:
					sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * 方法名: getPrice 方法描述: 分传元保留2位小数 修改时间：2016年6月24日 上午10:54:26 参数 @param price
	 * 参数 @return 参数说明 返回类型 String 返回类型
	 *
	 */
	public static String getPrice(String price) {
		if (StringUtil.isBlank(price)) {
			return "0.00";
		}
		// 这样为保持2位
		DecimalFormat df2 = new DecimalFormat("##0.00");
		return df2.format(StringUtil.parseDouble(price) / 100);
	}

	/**
	 *    把object数组 加分隔符组合成字符串   分隔符默认为,
	 *
	 * @param objs
	 * @return
	 */
	public static String join(Object[] objs) {
		if (objs == null || objs.length <= 0) {
			return "";
		}
		String separator = ",";
		StringBuffer sb = new StringBuffer();
		addString(objs, separator, sb);
		return sb.toString();
	}

	private static void addString(Object[] objs, String separator, StringBuffer sb) {
		for (int i = 0; i < objs.length; i++) {
			if (i != 0) {
				sb.append(separator);
			}
			sb.append(objs[i].toString());
		}
	}

	/**
	 *    把objcet数组 加分隔符组合成字符串
	 *
	 * @param objs
	 * @param separator   分隔符  为null时解为空
	 * @return
	 */
	public static String join(Object[] objs, String separator) {
		if (objs == null || objs.length <= 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer();
		separator = separator == null ? "" : separator;
		addString(objs, separator, sb);
		return sb.toString();
	}

	/**
	 *  隐藏手机号码中间四位
	 *
	 * @param phone
	 */
	public static String hidePhone(String phone) {
       return StringUtils.isBlank(phone) || phone.length() != 11 ? phone
           : phone.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
	}
	/**
	 *  隐藏手机号码中间四位
	 *
	 * @param mobile
	 */
	public static String hideMobile(String mobile) {
		return StringUtils.isBlank(mobile) || mobile.length() != 11 ? mobile
				: mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2");
	}

	/**
	 *  隐藏身份证号码中间四位
	 *
	 * @param idNum
	 */
	public static String hideIdNum(String idNum) {
		if (StringUtils.isBlank(idNum)) {
			return idNum;
		} else {
			Pattern p = Pattern.compile("(\\w{4})(\\w+)(\\w{4})");
			Matcher m = p.matcher(idNum);
			return m.replaceAll("$1**********$3");
		}
	}


	/**
	 * 昵称中包含 手机号码进行中间隐藏
	 * @param nickName
	 * @return
	 */
	public static String hideNickName(String nickName){
		Pattern p = Pattern.compile("(\\d{3})(\\d+)(\\d{4})");
		Matcher m = p.matcher(nickName);
		return m.replaceAll("$1****$3");
	}

	/**
	 * 方法名: hideEmailName
	 * 方法描述: 隐藏邮箱，@前不足三位全显，大于三位，前示前两位，后一位，中间隐藏
	 * 修改时间：2017年11月7日 上午9:27:10 
	 * 参数 @param email
	 * 参数 @return 参数说明
	 * 返回类型 String 返回类型
	 */
	public static String hideEmailName(String email) {
		if (StringUtil.isBlank(email)) {
			return "";
		}
		//邮箱验证不通过直接返回原串
		if (!checkEmail(email)) {
			return email;
		}
		int index = email.indexOf("@");
		String subStr = email.substring(0, index);
		String endStr = email.substring(index, email.length());
		StringBuffer buffer = null;
		if (subStr.length() <= 3) {
			buffer = new StringBuffer();
			String str = subStr.replaceAll("(\\w{1})(\\w+)(\\w{1})", "$1*$3");
			buffer.append(str).append(endStr);
			return buffer.toString();
		} else {
			String firstStr = subStr.substring(0, 2);
			String middleStr = subStr.substring(2, subStr.length() - 1);
			String hideStr = middleStr.replaceAll("([\\u4e00-\\u9fa5|\\w])", "*");

			String lastStr = subStr.substring(firstStr.length() + middleStr.length());
			buffer = new StringBuffer();
			buffer.append(firstStr).append(hideStr).append(lastStr).append(endStr);
			return buffer.toString();
		}

	}

	/**
	 * 方法名: hideName
	 * 方法描述: 名字只显示第一位
	 * 修改时间：2017年9月19日 上午9:46:58 
	 * 参数 @param name
	 * 参数 @return 参数说明
	 * 返回类型 String 返回类型
	 */
	public static String hideName(String name) {
		if (StringUtils.isBlank(name)) {
			return name;
		} else {
			return new StringBuffer().append(name.charAt(0))
					.append(name.substring(1).replaceAll("([\\u4e00-\\u9fa5|\\w])", "*")).toString();
		}
	}

	/**
	 * 方法名: checkIdNum
	 * 方法描述: 要么是15位，要么是18位，最后一位可以为字母
	 * 修改时间：2017年3月21日 上午9:33:46 
	 * 参数 @param idNum
	 * 参数 @return 参数说明
	 * 返回类型 boolean 返回类型
	 */
	public static boolean checkIdNum(String idNum) {
		Pattern idNumPattern = Pattern.compile("(\\d{14}[0-9a-zA-Z])|(\\d{17}[0-9a-zA-Z])");
		Matcher m = idNumPattern.matcher(idNum);
		return m.find();
	}

	/**
	 * 匹配中国邮政编码  
	 * @param postCode 邮政编码
	 * @return 验证成功返回true，验证失败返回false  
	 */
	public static boolean isPostCode(String postCode) {
		String reg = "\\d{6}";
		return Pattern.matches(reg, postCode);
	}

	/**
	 * 方法名: checkEmail
	 * 方法描述: 检查邮箱格式
	 * 修改时间：2017年10月16日 上午10:11:53 
	 * 参数 @param email
	 * 参数 @return 参数说明
	 * 返回类型 boolean 返回类型
	 */
	public static boolean checkEmail(String email) {
		String reg = "^[a-zA-Z0-9]+[\\.a-zA-Z0-9_-]*@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
		return Pattern.matches(reg, email);
	}

	/**
	 * 方法名: getParamByUrl
	 * 方法描述: 获取url请求参数
	 * 修改时间：2017年4月22日 上午10:10:25 
	 * 参数 @param url
	 * 参数 @return 参数说明
	 * 返回类型String 返回类型
	 */
	public static String getParamByUrl(String url) {
		if (isBlank(url)) {
			return "";
		}
		int beginIndex = url.indexOf("?");
		if (beginIndex == -1) {
			return "";
		}
		String queryString = url.substring(beginIndex + 1);
		return queryString;
	}

	/**
	 * 方法名: getParamMapByUrl
	 * 方法描述: 获取url请求参数
	 * 修改时间：2017年4月22日 上午10:10:25 
	 * 参数 @param url
	 * 参数 @return 参数说明
	 * 返回类型 Map<String,String> 返回类型
	 */
	public static Map<String, String> getParamMapByUrl(String url) {
		if (isBlank(url)) {
			return null;
		}
		int beginIndex = url.indexOf("?");
		if (beginIndex == -1) {
			return null;
		}
		String queryString = url.substring(beginIndex + 1);
		String[] queryStringSplit = queryString.split("&");
		Map<String, String> queryStringMap = new HashMap<String, String>(queryStringSplit.length);
		String[] queryStringParam;
		for (String qs : queryStringSplit) {
			queryStringParam = qs.split("=");
			queryStringMap.put(queryStringParam[0], queryStringParam[1]);
		}
		return queryStringMap;
	}

	/**
	 * 寄件人姓名格式校验  2-20位英文
	 *
	 * @param name
	 * @return
	 */
	public static boolean checkJContactName(String name) {
		String reg = "^[A-Za-z]{2,20}$";
		return Pattern.matches(reg, name);
	}


	/**
	 * 判断卡密格式
	 *
	 * @param secretKey
	 * @return
	 */
	public static boolean checkKeyFormat(String secretKey) {
		String pattern = "^(?![0-9]+$)[0-9A-Za-z]{2,16}$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(secretKey);
		return m.matches();
	}

	/**
	 * 判断口令格式
	 *
	 * @param secretKey
	 * @return
	 */
	public static boolean checkCommandFormat(String secretKey) {
		String pattern = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]{1,15}+$";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(secretKey);
		return m.matches();
	}
}
